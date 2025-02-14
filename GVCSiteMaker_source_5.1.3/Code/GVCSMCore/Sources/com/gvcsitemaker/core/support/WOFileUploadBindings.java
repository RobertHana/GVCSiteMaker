// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.support;

import java.io.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.foundation.*;


/**
 * This is a utility class to support and supplement WOFileUpload when streaming is used.  It combines
 * support and reporting methods commonly useful with WOFileUpload.  Several methods are intended to be
 * bound directly to the WOFileUpload component:<br />
 * <ol>
 *   <li>uploadedFileName()</li>
 *   <li>streamToFilePath()</li>
 *   <li>mimeType()</li>
 *   <li>finalFilePath()</li>
 * </ol>
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class WOFileUploadBindings
{
    // For direct bindings
    protected String finalFilePath;
    protected String uploadedFileName;
    protected String mimeType;

    // Indirect bindings
    protected File streamToFile;
    protected File uploadedFile = null;

    // For use in UI
    protected String statusMessage;
    protected NSArray groups;
    protected long uploadedFileSize;
    protected SiteFileFolder folder;
    
    
    public WOFileUploadBindings()
    {
        super();
        reset();
    }

    
    
    /**
     * @return <code>true</code> if a file was uploaded, <code>false</code> if the form was submitted
     * with no file selected in the WOFileUpload.
     */
    public boolean wasFileUploaded() 
    {
        return (uploadedFileName != null) && (finalFilePath != null);  
    }
    

    
    /**
     * @return the file name only portion of the file uploaded, or null if there is no upload
     */
    public String uploadedFileName() 
    {
        return wasFileUploaded() ? StringAdditions.lastPathComponentXPlatform(uploadedFileName) : null;
    }
    
    
    
    /**
     * @return File pointing to result of upload, or null if no file was uploaded.
     */
    public File uploadedFile() 
    {
        if (wasFileUploaded() && uploadedFile == null)
        {
            uploadedFile = new File(finalFilePath());
        }
        
        return uploadedFile;
        /** ensure [valid_result] Result != null;    **/
    }
    
    
    /**
     * @return the actual size of uploadedFile(), in bytes, or null if there is no upload
     */
    public long uploadedFileSize() 
    {
        long fileSize = 0;
        
        if (uploadedFile() != null)
        {
            fileSize = uploadedFile().length();
        }

        return fileSize;
    }

    

    /**
     * @return the path where WOFileUpload should attempt to stream the file to.
     */
    public String streamToFilePath()
    {
        return (streamToFile !=  null) ? streamToFile.getAbsolutePath() : null;
    }
    
    
    
    /**
     * Return the MIME type, determined from either the file contents (for images) or the extension on
     * the file name.  The MIME type set by WOFileUpload is ignored.
     * 
     * @return the MIME type of the uploaded file or null of there was no upload
     */
    public String mimeType() {
        return wasFileUploaded() ? MIMEUtils.mimeTypeFromFileOrPath(finalFilePath(), uploadedFileName) : null;
    }

    
    
    /**
     * Resets the internal state of this object so that it can be used with a new upload.  Any temp
     * files created are deleted.
     */
    public void reset() {
        try {
            cleanupFiles();
            streamToFile = File.createTempFile("GVCSM", ".upload");
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
        
        finalFilePath = null;
        uploadedFile = null;
        uploadedFileSize = 0;
        uploadedFileName = null;
        mimeType = null;
        statusMessage = null;
        setGroups(new NSMutableArray());
    }
    

    
    /**
     * Overidden to delete any temp files created.
     */
    public void finalize() throws Throwable
    {
        cleanupFiles();
        super.finalize();
    }
    

    
    /**
     * Delete any temp files created.  This can be called after processing an upload to quickly clean
     * up the working files rather than waiting for finalize.  This object should be reused after
     * calling this.  If reuse is what you want, call reset(). 
     */
    public  void cleanupFiles()
    {
        // Delete temp file created at streamToFilePath()
        if ((streamToFile != null) && streamToFile.exists())
        {
            streamToFile.delete();
        }
        
        // Delete uploaded file if any, and if not at streamToFilePath()
        if (finalFilePath() != null)
        {
            File aFile = new File(finalFilePath());
            if (aFile.exists())
            {
                aFile.delete();
            }
        }        
        /** ensure [streamToFile_gone] true;  /# (streamToFile == null) || (! streamToFile.exists());  parse error on exists #/
                    [final_file_gone] true;                             **/ 
    }
    

    
    /**
     * @return the information that WOFileUpload supplied.
     */
    public String toString() 
    {
        StringBuffer sb = new StringBuffer();
        
        if (wasFileUploaded())
        {
            sb.append("uploadedFileName = ");
            sb.append(uploadedFileName());
            sb.append(", mimeType = ");
            sb.append(mimeType);
            sb.append(", streamToFilePath = ");
            sb.append(streamToFilePath());
            sb.append(", finalFilePath = ");
            sb.append(finalFilePath);
            if (folder() != null)
            {
                sb.append(", folder = " + folder().name());
            }
            if (groups() != null)
            {
                sb.append(", groups = " + groups().valueForKey("name"));
            }
        }
        else
        {
           sb.append("No file uploaded");
           sb.append(", streamToFilePath = ");
           sb.append(streamToFilePath());
        }
        return sb.toString();
        
    }
    
    //****************** Generic accessor / mutator methods ******************//
    
    /**
     * For client UI use.
     * 
     * @return the status message set by setStatusMessage
     */
    public String statusMessage() {
        return statusMessage;
    }
    public void setStatusMessage(String message) {
        statusMessage = message;
    }

    
    /**
     * For client UI use.
     * 
     * @return the Groups access protecting this Upload.
     */
    public NSArray groups() {
        return groups;
    }
    public void setGroups(NSArray newGroups) {
        groups = newGroups;
    }

    /**
     * For client UI use.
     * 
     * @return the Folder these files will be uploaded to
     */
    public SiteFileFolder folder() {
        return folder;
    }
    public void setFolder(SiteFileFolder newFolder) {
        folder = newFolder;
    }

    /**
     * For WOFileUpload use.
     * 
     * @param newMimeType from WOFileUpload
     */
    public void setMimeType(String newMimeType) {
        mimeType = newMimeType;
    }
    public void setUploadedFileName(String newName) {
        uploadedFileName = newName;
    }

    
    /**
     * For WOFileUpload use.
     * 
     * @return the finalFilePath that WOFileUpload actually streamed this file to
     */
    public String finalFilePath() {
        return finalFilePath;
    }
    public void setFinalFilePath(String newPath) {
        finalFilePath = newPath;
    }
    

}



