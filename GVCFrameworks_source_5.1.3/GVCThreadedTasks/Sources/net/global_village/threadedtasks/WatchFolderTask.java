package net.global_village.threadedtasks;



import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Watches for files to appear in a folder and calls a method to process each one.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public abstract class WatchFolderTask implements ITask, FileFilter
{

    private File folder;
    private Logger logger = LoggerFactory.makeLogger();


    /**
     * Checks for the existence of files in this folder.  Each file returned by <code>filesInFolder()</code>
     * that passes the <code>isStable(File)</code> check is passed to <code>process(EOEditingContext, File)</code>.
     * All the files and the processed files are passed to statusForProcessedFiles(NSArray, NSArray) to return the
     * status message.
     *
     * @see net.global_village.threadedtasks.ITask#process(com.webobjects.eocontrol.EOEditingContext)
     * @see #filesInFolder()
     * @see #isStable(File)
     * @see #processFile(EOEditingContext, File)
     * @see #statusForProcessedFiles(NSArray, NSArray)
     *
     * @param editingContext to use when processing files</code>
     * @return a string giving the status of processing
     */
    public String process(EOEditingContext editingContext)
    {
        /** require [folder_set] folder() != null;
                    [valid_editingContext] editingContext != null;  **/
        logger.trace("Processing");
        NSArray files = filesInFolder();
        if (files.count() > 0) logger.info("Will process " + files);
        NSMutableArray processedFiles = new NSMutableArray();
        NSMutableArray problemFiles = new NSMutableArray();

        for (int i = 0; i < files.count() && ! Thread.currentThread().isInterrupted(); i++)
        {
            File aFile = (File) files.objectAtIndex(i);
            if (isStable(editingContext, aFile))
            {
                logger.debug("Processing file " + aFile);
                if (processFile(editingContext, aFile))
                {
                    processedFiles.addObject(aFile);
                }
                else
                {
                    problemFiles.addObject(aFile);
                }
            }
            else
            {
                logger.debug("Skipping unstable file " + aFile);
            }
        }

        String status = statusForProcessedFiles(files, processedFiles, problemFiles);
        logger.debug(status);
        return status;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the files in <code>folder()</code> to consider for processing.  This implementation does not
     * look in sub-folders.  It filters the potential matches with <code>fileFilter()</code> and sorts the results
     * by the full path name.  Override this to return a different set of files to consider or to change the order
     * of the files returned.
     *
     * @see #process(EOEditingContext)
     * @see #fileFilter()
     *
     * @return the files in <code>folder()</code> to consider for processing
     */
    protected NSArray filesInFolder()
    {
        File[] files = folder().listFiles(fileFilter());
        Arrays.sort(files);
        return new NSArray(files);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Override this to select which files in the directory are processed by this task.  This
     * default implementation returns <code>this</code> to indicate that this class will filter
     * the files.
     *
     * @see #accept(File)
     * @see FileFilter
     * @see #filesInFolder()
     *
     * @return this
     */
    protected FileFilter fileFilter()
    {
        return this;
    }



    /**
     * Excludes directories.
     *
     * @see java.io.FileFilter#accept(java.io.File)
     *
     * @param file the file to check for inclusion as a delta file
     * @return <code>true</code> if file is not a directory
     */
    public boolean accept(File file)
    {
        return ! file.isDirectory();
    }



    /**
     * Determines if the file is stable enough to be processed, or if it is being modified.  This implementation
     * looks at the time of last modification and file size, waits stabilityCheckInterval() seconds, and checks for any change.
     * If you don't want a stability check, override this method to return <code>true</code>.  If you want to
     * wait for more or less time override <code>stabilityCheckInterval()</code>.
     *
     * @see #stabilityCheckInterval()
     *
     * @param editingContext used to verify stability</code>
     * @param file the File to check for stability
     * @return <code>true</code> if the lastModified date and file size remain the same for N seconds
     */
    protected boolean isStable(EOEditingContext editingContext, File file)
    {
        /** require [valid_editingContext] editingContext != null;
                    [valid_file] file != null;  **/
        long originalTime = file.lastModified();
        long originalSize = file.length();
        try
        {
            Thread.sleep(stabilityCheckInterval());
        }
        catch (InterruptedException e)
        {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
        }
        File newFile = new File(file.getAbsolutePath());
        return originalTime == newFile.lastModified() && originalSize == newFile.length();
    }



    /**
     * Returns the length of time in milliseconds to wait to see if a file is changing.
     * This implementation returns 500 (0.5 seconds).
     *
     * @see #isStable(File)
     *
     * @return the length of time in milliseconds to wait to see if a file is changing
     */
    protected long stabilityCheckInterval()
    {
        return 500;
    }



    /**
     * Override this method to process one file.  The file should be moved or removed after processing if
     * you don't want it processed again.
     *
     * @see #process(EOEditingContext)
     *
     * @param editingContext EOEditingContext to process this file in
     * @param file the File to process
     *
     * @return <code>true</code> if the file was processed without error, <code>false</code> if there was a problem processing it
     */
    protected abstract boolean processFile(EOEditingContext editingContext, File file);



    /**
     * Returns a status message for the processing of files.  This default implementation returns
     * a count of the files processed and a list of their names as determined by <code>statusNameForFile(File)</code>.
     *
     * @see #statusNameForFile(File)
     * @param allFiles all of the files found in the folder
     * @param processedFiles the files that were actually processed
     * @param problemFiles the files that were processed, but failed due to an error or problem
     *
     * @return status result message.
     */
    protected String statusForProcessedFiles(NSArray allFiles, NSArray processedFiles, NSArray problemFiles)
    {
        /** require [valid_allFiles] allFiles != null;
                    [valid_processedFiles] processedFiles != null;  **/

        StringBuffer status = new StringBuffer();
        status.append(processedFiles.count());
        status.append(" files processed");

        if (processedFiles.count() > 0)
        {
            status.append(": ");
            for (int i = 0; i < processedFiles.count(); i++)
            {
                if (i > 0)
                {
                    status.append(", ");
                }
                status.append(statusNameForFile((File)processedFiles.objectAtIndex(i)));
            }
        }

        if (problemFiles.count() > 0)
        {
            status.append(", ");
            status.append(problemFiles.count());
            status.append(" files encountered problems: ");
            for (int i = 0; i < problemFiles.count(); i++)
            {
                if (i > 0)
                {
                    status.append(", ");
                }
                status.append(statusNameForFile((File)problemFiles.objectAtIndex(i)));
            }
        }

        return status.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the name for the passed file to be included in the result status message.  This
     * implementation returns the file's name without any path information.
     *
     * @param aFile the to return a status name for
     * @return the file's name without any path information
     */
    protected String statusNameForFile(File aFile)
    {
        /** require [valid_file] aFile != null;  **/
        return aFile.getName();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * There is no cleanup.
     *
     * @see net.global_village.threadedtasks.ITask#cleanup(com.webobjects.eocontrol.EOEditingContext)
     *
     * @param editingContext not used
     */
    public void cleanup(EOEditingContext editingContext)
    {
    }



    /**
     * No end processing
     *
     * @see net.global_village.threadedtasks.ITask#end()
     *
     * @param editingContext not used
     */
    public void end(EOEditingContext ec)
    {
    }



    /**
     * Returns a user present-able name for this task.
     *
     * @see net.global_village.threadedtasks.ITask#name()
     *
     * @return net.global_village.threadedtasks.WatchFolderTask
     */
    public abstract String name();



    /**
     * Sets the folder that this task should watch;
     *
     * @param directory the folder that this task should watch
     */
    public void setFolder(File directory)
    {
        /** require [non_null_directory] directory != null;
                    [is_directory] directory.isDirectory();
                    [can_read_directory] directory.canRead();
         **/
        folder = directory;
    }



    /**
     * Sets the folder that this task should watch.  Path should be an absolute path.
     *
     * @param path path to the folder that this task should watch
     */
    public void setFolder(String path)
    {
        /** require [non_null_path] path != null;  **/
        if (folder() == null || ! folder().getAbsolutePath().equals(path))
        {
            File newFolder = new File(path);
            if ( ! newFolder.isDirectory())
            {
                throw new IllegalArgumentException(path + " is not a directory");
            }

            if (!  newFolder.canRead())
            {
                throw new IllegalArgumentException(path + " is not readable");
            }

            setFolder(newFolder);
        }
        /** ensure [folder_set] folder() != null;
                   [folder_matches_path] folder().getAbsolutePath().equals(path);
         **/
    }



    /**
     * Returns the folder that this task is watching.
     *
     * @return the folder that this task is watching
     */
    public File folder()
    {
        return folder;
    }


}
