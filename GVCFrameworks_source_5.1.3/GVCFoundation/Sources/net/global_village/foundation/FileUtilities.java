package net.global_village.foundation;


import java.io.*;

import org.apache.log4j.*;

import com.webobjects.foundation.*;

/**
 * A collection of file related utility methods.
 */
public class FileUtilities extends Object
{


    public static final int BYTES_PER_KILOBYTE = 1024;
    public static final int BYTES_PER_MEGABYTE = BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE;
    public static final int FILE_BUFFER_SIZE = 8 * BYTES_PER_MEGABYTE;

    private static Logger logger = LoggerFactory.makeLogger();



    /**
     * Reads the entire contents of a file into memory as a String.
     *
     * @param file
     *            the file to read into the String
     * @return String containing contents of file
     * @throws IOException
     *             if the file can't be read
     */
    public static String stringFromFile(File file) throws IOException
    {
        /** require [valid_file] file != null;
                    [file_can_be_read] file.canRead();    **/

        int size = (int) file.length();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[size];
        int bytesRead = 0;
        while (bytesRead < size)
        {
            bytesRead += fileInputStream
                            .read(data, bytesRead, size - bytesRead);
        }
        fileInputStream.close();
        return new String(data);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the contents of this File in an NSData object.
     *
     * @param file the contents of this file are returned in an NSData
     * @throws IOException - if there is an error reading the file
     * @return the contents of this SiteFile in an NSData object.
     */
    public static NSData dataFromFile(File file) throws IOException
    {
        /** require [valid_param] file != null && file.canRead(); **/

        // Doing this with large files can result in a OutOfMemoryException which kills the instance. The code below
        // tries to mitigate this as much as possible, but there will be a finite limit to the size of file that can be
        // handled for a given JVM heap size.
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

        // The default buffer size is that of the entire file.
        int bufferSize = bufferedInputStream.available();

        // If the source file is larger than the designated buffer size, use the the designated buffer size.
        if (bufferSize > FILE_BUFFER_SIZE)
        {
            bufferSize = FILE_BUFFER_SIZE;
        }
        else if (bufferSize < 1)
        {
            bufferSize = 1; // Handle zero length files.
        }

        NSData contentsOfFile = new NSData(bufferedInputStream, bufferSize);
        bufferedInputStream.close();
        fileInputStream.close();

        return contentsOfFile;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Parses a delimited file (comma delimited, tab delimited etc) and returns the contents as an array of dictionaries.
     * If columnNames is null the first line in the file is assumed to be delimited column names.
     *
     * @param delimitedFile the File to parse
     * @param delimiters string containing the allowed delimiters, ' and " can be used to quote delimiters
     * @param columnNames name for each column in the file or null if the first line contains the column names
     *
     * @return array of dictionaries containing the data from the file
     *
     * @throws IOException if the file can't be read or closed
     * @throws IllegalArgumentException if the number of elements in any row does not match the number of columns
     */
    public static NSArray dataFromDelimitedFile(InputStream delimitedFile, String delimiters, NSArray columnNames) throws IOException, IllegalArgumentException
    {
        /** require [valid_file] delimitedFile != null;
                    [valid_delimiters] delimiters != null && delimiters.length() > 0;
         **/
        BufferedReader input = new BufferedReader( new InputStreamReader( delimitedFile ));
        NSMutableArray data = new NSMutableArray();

        try
        {
            String currentLine = input.readLine();
            if( currentLine != null )
            {
                // Use first row a column labels if they are not provided, otherwise skip over first line
                if (columnNames == null)
                {
                    DelimitedStringTokenizer tokenizer = new DelimitedStringTokenizer(currentLine, delimiters, "\"'", null);
                    columnNames = tokenizer.allRemainingTokens();
                }
                currentLine = input.readLine();
                logger.debug("Columns in file: " + columnNames);
            }

            while ( currentLine != null )
            {
                logger.trace("currentLine = " + currentLine);
                DelimitedStringTokenizer tokenizer = new DelimitedStringTokenizer(currentLine, delimiters, "\"'", columnNames);
                NSDictionary rowData = tokenizer.allRemainingTokensWithNames();

                if( columnNames.count() != rowData.count() ) {
                    logger.error("Data " + rowData + " does not match expected " + columnNames);
                    throw new IllegalArgumentException("Data " + rowData + " does not match expected " + columnNames);
                }

                data.addObject(rowData);
                currentLine = input.readLine();
            }
        }
        finally
        {
            input.close();
        }

        return data;
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Copies sourceFileName to destinationFileName buffered IO so that large files can be copied. This is a convenience
     * method wrapping copyFile(InputStream, String). See that method for more details.
     *
     * @param sourceFileName
     *            the path to the source file including the file name
     * @param destinationFileName
     *            path and file name of the destination
     */
    public static void copyFile(String sourceFileName, String destinationFileName)
    {
        /**
         * require [valid_stream] sourceFileName != null; [valid_filename] destinationFileName != null; [source_exists]
         * true; /# ((new File(sourceFileName))).exists(); #/
         */

        logger.debug("Copying " + sourceFileName + " to " + destinationFileName + " ...");
        try
        {
            copyFile(new FileInputStream(sourceFileName), destinationFileName);
        }
        catch (FileNotFoundException e)
        {
            logger.debug("*** Can not find source file: " + e.getMessage());
            throw new ExceptionConverter(e);
        }

        /** ensure [file_created] true; /# (new File(destinationFileName)).exists(); #/ **/
    }



    /**
     * Copies a stream using buffered IO so that large files can be copied. Performance of this method should be
     * approximately the same as a native OS copy command. If the destinationFileName specifies a directory that does
     * not exist it will be created. If there are problems with source or destination (file not found, file locked,
     * destination not writable etc.) a Runtime exception is raised. <br>
     * <br>
     * This method is based on demonstration code posted to the Omni WebObjects Dev list by Stefan Apelt
     * (stefan@tetlabors.de). The original code and interesting comments and test results can be found at either of
     * these URLs: <br>
     * http://www.omnigroup.com/mailman/archive/webobjects-dev/2001-June/010517.html <br>
     * http://wodeveloper.com/omniLists/webobjects-dev/2001/June/msg00325.html
     *
     * @param sourceStream -
     *            the stream to copy to the file
     * @param destinationFileName
     *            path and file name of the destination
     */
    public static void copyFile(InputStream sourceStream,
                                String destinationFileName)
    {
        /** require [valid_stream] sourceStream != null; [valid_filename] destinationFileName != null; **/
        logger.debug("Copying stream to " + destinationFileName + " ...");

        try
        {
            // Create the destination directory if it does not exist.
            File destinationDirectory = new File(new File(destinationFileName)
                            .getParent());
            if (!destinationDirectory.isDirectory())
            {
                logger.debug(" -> destination directory does not exist, creating " + destinationDirectory);
                destinationDirectory.mkdirs();
            }

            // create and init input and output streams
            BufferedInputStream in = new BufferedInputStream(sourceStream);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFileName));

            // This is the maximum size of the memory buffer for copying the file. bufferSize should be between 100K and
            // 10MB. Larger values will result in faster copying at the cost of a higher memory load. At a bufferSize >
            // 10MB speed will likely degrade as read/write cycles take too much time (plus you may get
            // OutOfMemoryExceptions with standard heap settings). For my system (2x Intel P3-600, 1gig RAM) bufferSize
            // = 8M is a good setting
            int bufferSize = FILE_BUFFER_SIZE;

            // If the source file is smaller than the targeted buffer size, reduce the buffer size to reduce memory
            // load: there is little sense in allocating 8meg for a file of 300 bytes.
            if (in.available() < bufferSize)
            {
                bufferSize = in.available();
            }

            // Create the buffer we use for copying
            byte[] buf = new byte[bufferSize];

            int bytesRead = 0; // Contains the number of bytes actually read.

            // in.available() gives the number of bytes yet to copy. When it drops to 0, we are done. The next four
            // lines of code are everything that matters.
            while (in.available() != 0)
            {
                // Read from the source file to the memory buffer.
                bytesRead = in.read(buf, 0, bufferSize);

                // In all loops except the last one, bytesRead == bufferSize. The last part of the source file will not
                // fill the buffer completely, so we can only write 'bytesRead' bytes out
                out.write(buf, 0, bytesRead);
            }

            // Write out anything that might be left in the output stream
            out.flush();
            out.close();
            in.close();

            logger.debug(" -> copying finished.");
        }
        catch (Exception e)
        {
            // if there was some error during the process
            logger.debug("*** error occured while copying: " + e.getMessage());
            throw new ExceptionConverter(e);
        }
        /** ensure [file_created] true; /# (new File(destinationFileName)).exists(); #/ **/
    }




    /**
     * Returns the parameter converted from bytes into megabytes.
     *
     * @param bytes -
     *            number of bytes to convert into megabytes
     * @return the parameter converted from bytes into megabytes.
     */
    public static float bytesToMegaBytes(long bytes)
    {
        return (float) bytes / (float) BYTES_PER_MEGABYTE;
    }



    /**
     * Returns the parameter converted from bytes into megabytes.
     *
     * @param bytes -
     *            number of bytes to convert into megabytes
     * @return the parameter converted from bytes into megabytes.
     */
    public static float bytesToMegaBytes(Number bytes)
    {
        /** require [bytes_not_null] bytes != null; **/

        return bytesToMegaBytes(bytes.longValue());
    }



    /**
     * Returns the parameter converted from bytes into kilobytes.
     *
     * @param bytes -
     *            number of bytes to convert into kilobytes
     * @return the parameter converted from bytes into kilobytes.
     */
    public static float bytesToKiloBytes(long bytes)
    {
        return bytes / BYTES_PER_KILOBYTE;
    }



    /**
     * Returns the parameter converted from bytes into kilobytes.
     *
     * @param bytes -
     *            number of bytes to convert into kilobytes
     * @return the parameter converted from bytes into kilobytes.
     */
    public static float bytesToKiloBytes(Number bytes)
    {
        /** require [bytes_not_null] bytes != null; **/

        return bytesToKiloBytes(bytes.longValue());
    }



    /**
     * Returns the parameter converted from megabytes to bytes
     *
     * @param megs -
     *            number of megabytes to convert into bytes
     * @return the parameter megabytes from bytes into bytes.
     */
    public static long megaBytesToBytes(long megs)
    {
        return megs * BYTES_PER_MEGABYTE;
    }



    /**
     * Returns the parameter converted from megabytes to bytes
     *
     * @param megs -
     *            number of megabytes to convert into bytes
     * @return the parameter megabytes from bytes into bytes.
     */
    public static long megaBytesToBytes(Number megs)
    {
        /** require [megs_not_null] megs != null; **/

        return megaBytesToBytes(megs.longValue());
    }


}
