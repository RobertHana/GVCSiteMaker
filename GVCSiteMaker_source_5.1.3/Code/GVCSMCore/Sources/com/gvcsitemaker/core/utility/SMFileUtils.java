// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.io.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.foundation.*;


/** 
 * A collection of file related utility methods.
 */
public class SMFileUtils extends Object
{

    private static final String FILE_NAME_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789-_.";
    private static final String PATH_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789-_./";
    public static final int BYTES_PER_KILOBYTE = 1024;
    public static final int BYTES_PER_MEGABYTE = BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE;

    /** Gives you back an object if you give it a java File
     * object. The object it returns is <i>usually</i> an
     * NSDictionary, but can also be an NSArray or a String (Tho I
     * don't know why you'd store just a string in a plist file). Now
     * that the XML stuff is in WO4.5, that might replace this at some
     * future point since plists can't hold stuff like Integers
     * etc. */
    public static Object objectFromPListFile(File file) {
        Object obj = null;
        try {
            obj = NSPropertyListSerialization.propertyListFromString(stringFromFile(file));
        }
        catch(IOException ioexception)
        {
            throw new ExceptionConverter(ioexception);
        }
        return obj;
    }


    
    /** Pretty self-explanatory. A useful thing to have around. 
      * @deprecated use net.global-village.FileUtilities.
      */
    public static String stringFromFile(File file) throws IOException {
        if (file == null) {
            throw new IOException("File is null. Unable to obtain string");
        }
        int size = (int) file.length();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[size];
        int bytesRead = 0;
        while (bytesRead < size) {
            bytesRead += fileInputStream.read(data, bytesRead, size-bytesRead);
        }
        fileInputStream.close();
        return new String(data);
    }



    /**
     * Returns the contents of this File in an NSData object.  If the file does not exist an empty NSData is returned and the error is recorded in the ErrorLog.  If the file cannot be read, a RuntimeException is raised.
     *
     * @throw FileNotFoundException - the file can not be found on the disk
     * @throw IOException - if there is an error reading the file
     * @return the contents of this SiteFile in an NSData object.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static NSData dataFromFile(File file) throws IOException
    {
        /** require  [valid_param] file != null;  **/

        // Doing this with large files can result in a OutOfMemoryException which kills the instance.  The code below tries to mitigate this as much as possible, but there will be a finite limit to the size of file that can be handled for a given JVM heap size.
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

        // The default buffer size is that of the entire file.
        int bufferSize = bufferedInputStream.available();

        // If the source file is larger than the designated buffer size, use the the designated buffer size.
        if (bufferSize > SMFileUtils.fileBufferSize())
        {
            bufferSize = SMFileUtils.fileBufferSize();
        }
        else  if (bufferSize < 1)
        {
            bufferSize = 1; // Handle zero length files.
        }

        NSData contentsOfFile = new NSData(bufferedInputStream, bufferSize);

        // Big files use a lot of memory, help the gc.  Should we force GC here if it was a large file?  -ch
        bufferedInputStream.close();
        fileInputStream.close();

        return contentsOfFile;

        /** ensure [result_not_null] Result != null; **/
    }


    
    public static NSArray arrayOfObjectsFromBatchFileData(NSData batchFileData, NSArray keyArray) {
        NSMutableArray newObjects = new NSMutableArray();

        BufferedReader input;
        input = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( batchFileData.bytes(0, batchFileData.length()) )));
        String currentLine = null;

        try {
            // skip the row of column labels
            currentLine = input.readLine();
            if( currentLine != null )
                currentLine = input.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
            DebugOut.println(1, e.getMessage());
            return null;
        }

        while ( currentLine != null ) {
            NSMutableDictionary object = new NSMutableDictionary();
            DebugOut.println(1, "currentLine = " + currentLine);
            NSArray attributes = SMStringUtils.arrayFromStringWithSeparator(currentLine,
                                                                           "\t");

            DebugOut.println(1, "attributes = " + attributes);
            DebugOut.println(1, "keyArray = " + keyArray);
            if( attributes.count() != keyArray.count() ) {
                // improper number of attributes
                DebugOut.println(1, "key count = " + keyArray.count());
                DebugOut.println(1, "attribute count = " + attributes.count());
                return null;
            }

            for( int i = 0; i < attributes.count(); i++ ) {
                object.setObjectForKey(attributes.objectAtIndex(i), keyArray.objectAtIndex(i));
            }

            newObjects.addObject(object);

            try {
                currentLine = input.readLine();
            }
            catch ( EOFException e ) {
                DebugOut.println(20, "##### End of file exception ######");
                currentLine = null;
            }
            catch (IOException e) {
                e.printStackTrace();
                DebugOut.println(1, e.getMessage());
                return null;
            }
        }

        try {
            input.close();
        }
        catch (IOException e) {
            input = null;
        }
        return newObjects;
    }



    /**
    * Copies sourceFileName to destinationFileName buffered IO so that large files can be copied.  This is a convenience method wrapping copyFile(InputStream, String).  See that method for more details.
     *
     * @param sourceFileName  the path to the source file including the file name
     * @param destinationFileName    path and file name of the destination
     * @deprecated use net.global-village.FileUtilities.
     */
    public static void copyFile(String sourceFileName, String destinationFileName)
    {
        /** require
        [valid_stream] sourceFileName != null;
        [valid_filename] destinationFileName != null;
        [source_exists] true; /# ((new File(sourceFileName))).exists(); #/
        **/

        DebugOut.println(20, "Copying " + sourceFileName + " to " + destinationFileName + " ...");
        try
        {
            copyFile(new FileInputStream(sourceFileName), destinationFileName);
        }
        catch (FileNotFoundException e)
        {
            DebugOut.println(20, "*** Can not find source file: " + e.getMessage());
            throw new ExceptionConverter(e);
        }

        /** ensure [file_created] true; /# (new File(destinationFileName)).exists();  #/  **/
    }



    /**
     * Copies a stream using buffered IO so that large files can be copied.  Performance of this method should be approximately the same as a native OS copy command.  If the destinationFileName specifies a directory that does not exist it will be created.  If there are problems with source or destination (file not found, file locked, destination not writable etc.) a Runtime exception is raised.
     * <br><br>
     * This method is based on demonstration code posted to the Omni WebObjects Dev list by Stefan Apelt (stefan@tetlabors.de).  The original code and interesting comments and test results can be found at either of these URLs: <br>
     * http://www.omnigroup.com/mailman/archive/webobjects-dev/2001-June/010517.html <br>
     * http://wodeveloper.com/omniLists/webobjects-dev/2001/June/msg00325.html
     *
     * @param sourceStream - the stream to copy to the file
     * @param destinationFileName    path and file name of the destination
     * @deprecated use net.global-village.FileUtilities.
     */
    public static void copyFile(InputStream sourceStream, String destinationFileName)
    {
        /** require [valid_stream] sourceStream != null; [valid_filename] destinationFileName != null;  **/
        DebugOut.println(20, "Copying stream to " + destinationFileName + " ...");

        try
        {
            // Create the destination directory if it does not exist.
            File destinationDirectory = new File(new File(destinationFileName).getParent());
            if ( ! destinationDirectory.isDirectory())
            {
                DebugOut.println(20, " -> destination directory does not exist, creating " + destinationDirectory);
                destinationDirectory.mkdirs();
            }

            // create and init input and output streams
            BufferedInputStream in = new BufferedInputStream(sourceStream);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFileName));

            // This is the maximum size of the memory buffer for copying the file.  bufferSize should be between 100K and 10MB.  Larger values will result in faster copying at the cost of a higher memory load.  At a bufferSize > 10MB speed will likely degrade as read/write cycles take too much time (plus you may get OutOfMemoryExceptions with standard heap settings).  For my system (2x Intel P3-600, 1gig RAM) bufferSize = 8M is a good setting
            int bufferSize = fileBufferSize();

            // If the source file is smaller than the targeted buffer size, reduce the buffer size to reduce memory load: there is little sense in allocating 8meg for a file of 300 bytes.
            if (in.available() < bufferSize)
            {
                bufferSize = in.available();
            }

            // Create the buffer we use for copying
            byte[] buf = new byte[bufferSize];

            int bytesRead = 0; // Contains the number of bytes actually read.

            // in.available() gives the number of bytes yet to copy.  When it drops to 0, we are done.  The next four lines of code are everything that matters.
            while (in.available() != 0)
            {
                // Read from the source file to the memory buffer.
                bytesRead = in.read(buf, 0, bufferSize);

                // In all loops except the last one, bytesRead == bufferSize.  The last part of the source file will not fill the buffer completely, so we can only write 'bytesRead' bytes out
                out.write(buf, 0, bytesRead);
            }

            // Write out anything that might be left in the output stream
            out.flush();
            out.close();
            in.close();

            DebugOut.println(20, " -> copying finished.");
        }
        catch (Exception e)
        {
            // if there was some error during the process
            DebugOut.println(20, "*** error occured while copying: " + e.getMessage());
            throw new ExceptionConverter(e);
        }
        /** ensure [file_created] true; /# (new File(destinationFileName)).exists(); #/  **/
    }



    /**
     * Returns the size of buffer to use when copying files.
     *
     * @return the size of buffer to use when copying files.
     */
    public static int fileBufferSize()
    {
        return BYTES_PER_MEGABYTE * SMApplication.appProperties().intPropertyForKey("FileBufferMegaBytes");
    }



    /**
     * Returns the parameter converted from bytes into megabytes.
     *
     * @param bytes - number of bytes to convert into megabytes
     * @return the parameter converted from bytes into megabytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static float bytesToMegaBytes( long bytes )
    {
        return (float)bytes / (float)BYTES_PER_MEGABYTE;
    }

  

    /**
     * Returns the parameter converted from bytes into megabytes.
     *
     * @param bytes - number of bytes to convert into megabytes
     * @return the parameter converted from bytes into megabytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static float bytesToMegaBytes(Number bytes)
    {
        /** require [bytes_not_null] bytes != null; **/
        
        return bytesToMegaBytes(bytes.longValue());
    }



    /**
     * Returns the parameter converted from bytes into kilobytes.
     *
     * @param bytes - number of bytes to convert into kilobytes
     * @return the parameter converted from bytes into kilobytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static float bytesToKiloBytes( long bytes )
    {
        return bytes / BYTES_PER_KILOBYTE;
    }



    /**
     * Returns the parameter converted from bytes into kilobytes.
     *
     * @param bytes - number of bytes to convert into kilobytes
     * @return the parameter converted from bytes into kilobytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static float bytesToKiloBytes( Number bytes )
    {
        /** require [bytes_not_null] bytes != null; **/
        
        return bytesToKiloBytes(bytes.longValue());
    }



    /**
     * Returns the parameter converted from megabytes to bytes
     *
     * @param megs - number of megabytes to convert into bytes
     * @return the parameter megabytes from bytes into bytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static long megaBytesToBytes( long megs )
    {
        return megs * BYTES_PER_MEGABYTE;
    }



    /**
     * Returns the parameter converted from megabytes to bytes
     *
     * @param megs - number of megabytes to convert into bytes
     * @return the parameter megabytes from bytes into bytes.
     * @deprecated use net.global-village.FileUtilities.
     */
    public static long megaBytesToBytes( Number megs )
    {
        /** require [megs_not_null] megs != null; **/
        
        return megaBytesToBytes(megs.longValue());
    }



    /**
     * Returns <code>true</code> if the string contains only valid characters for file names.
     *
     * @param aString - the string ot check for validity.
     * @return <code>true</code> if the string contains only valid characters for file names.
     */
    public static boolean stringContainsOnlyValidFileNameChars(String aString)
    {
        /** require [a_string_not_null] aString != null; **/
        
        return SMStringUtils.stringContainsOnlyCharsInString(aString, FILE_NAME_CHARS);
    }



    /**
     * Returns <code>true</code> if the string contains only valid characters for paths.  These are the same characters as for file names with the addition of /.  Note that the Windows \ is purposely disallowed as the primary intended use for this method is for URL paths which only permit the forward slash.
     *
     * @param aString - the string ot check for validity.
     * @return <code>true</code> if the string contains only valid characters for paths.
     */
    public static boolean stringContainsOnlyValidPathChars(String aString)
    {
        /** require [a_string_not_null] aString != null; **/
        
        return SMStringUtils.stringContainsOnlyCharsInString(aString, PATH_CHARS);
    }


}
