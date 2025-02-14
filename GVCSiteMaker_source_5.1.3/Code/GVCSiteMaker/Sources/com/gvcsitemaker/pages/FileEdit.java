// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page provides functions for changing the properties (access groups,
 * description, MIME type) for a SiteFile. It us shown on request from the
 * FileMgmt page.
 */
public class FileEdit extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    protected Website website; // For WebsiteContainer
    protected SiteFile currentFile; // SiteFile being edited

    /** @TypeInfo java.lang.String */
    protected NSArray mimeTypes; // List of all MIME types for WOPopup
    public String aMimeType; // Selected MIME type from WOPopup
    protected WOComponent previousPage;
    public SiteFileFolder aFolder;
    protected NSTimestampFormatter dateAndTimeFormatter;
    protected NSNumberFormatter standardNumberFormatter;

    // Not sure why this is required, but it is getting set by the Access Group tab
    public Group editingAccessGroup;


    /**
     * Designated constructor.  
     */
    public FileEdit(WOContext aContext)
    {
        super(aContext);
        previousPage = context().page();
        // Do not use WebsiteContainerBase.getWebsiteFromPageCreating(this).  On this page, website is obtained from currentFile().
        setMimeTypes(MIMEUtils.allMimeTypes());
        dateAndTimeFormatter = (NSTimestampFormatter) SMApplication.appProperties().propertyForKey("DateAndTimeFormatter");
        standardNumberFormatter = (NSNumberFormatter) SMApplication.appProperties().propertyForKey("StandardNumberFormatter");
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Cancel the upload of a new file, or the replacement of an existing file.  Whatever was uploaded is deleted.
     *
     * @return FileMgmt page
     */
    public WOComponent cancelUpload()
    {
        DebugOut.println(30, "Site's files before :" + website().files().valueForKey("uploadedFilename"));

        // Get rid of all name, quota, etc. changes
        editingContext().revert();
        DebugOut.println(30, "Site's files after :" + website().files().valueForKey("uploadedFilename"));

        return previousPage();
    }



    /**
     * Finish saving the new or updated file.
     *
     */
    public WOComponent saveChanges()
    {
        DebugOut.println(10, "Entering...");
        editingContext().saveChanges();
        return previousPage();
    }



    /**
     * Action method to e-mail a link to a SiteFile.
     *
     * @return an instance of the EmailPage page
     */
    public WOComponent sendEmail()
    {
        EmailPage emailPage = (EmailPage) pageWithName("EmailPage");
        emailPage.setFile(currentFile());
        emailPage.setReturnPage(this);

        return emailPage;
    }



    /**
     * Action method to replace a SiteFile.
     *
     * @return an instance of the FileUpload page
     */
    public WOComponent replaceFile()
    {
        FileReplace nextPage = (FileReplace) pageWithName("FileReplace");
        nextPage.setCurrentFile(currentFile());

        return nextPage;
    }



    /**
     * Returns <code>true</code> if the logged in user can configure this website.
     *
     * @return  <code>true</code> if the logged in user can configure this website.
     */
    public boolean canUserConfigureWebsite()
    {
        return website().canBeConfiguredByUser(((Session) session()).currentUser());
    }



    /**
     * Returns the editing context of the file being edited.  This is a peer editing context used by this and the FileUpload page to allow cancelling of file operations.
     *
     * @return the editing context of the file being edited.
     */
    public EOEditingContext editingContext()
    {
        /** require
        [current_file_not_null] currentFile() != null;
        [current_file_editing_context_not_null] currentFile().editingContext() != null; **/

        return currentFile().editingContext();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the Website which owns the SiteFile being edited.  This method implements WebsiteContainer and supports binding synchornization.
     *
     * @return the Website which owns the SiteFile being edited.
     */
    public Website website()
    {
        /** require [current_file_not_null] currentFile() != null; **/

        return currentFile().website();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Sets the Website which owns the SiteFile being edited.  This method
     * implements WebsiteContainer and supports binding synchornization.
     * The set value is ignored and we use the website from the currentFile
     * instead.
     *
     * @param value the Website which owns the SiteFile being edited.
     */
    public void setWebsite(Website value)
    {
        /** require [value_not_null] value != null; **/

        website = value;
    }



    /**
     * Returns the page title which varies whether the user can or cannot modify the file.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Revise File Properties";
    }



    /**
     * Returns true if user cannot configure websites. false otherwise.
     */
    public boolean canNotConfigureWebsite()
    {
        return !canUserConfigureWebsite();
    }



    /**
     * @return the selected folder() for currentFile()
     */
    public SiteFileFolder filesFolder()
    {
        return currentFile().folder();
    }



    /**
     * Sets the folder() for currentFile().  This cover method is needed so that the file is correctly 
     * decoupled from the original folder in memory.
     * 
     * @param newFolder the new SiteFileFolder to associate with this file.
     */
    public void setFilesFolder(SiteFileFolder newFolder)
    {
        if (!filesFolder().equals(newFolder))
        {
            currentFile().removeObjectFromBothSidesOfRelationshipWithKey(filesFolder(), "folder");
            currentFile().addObjectToBothSidesOfRelationshipWithKey(newFolder, "folder");
        }
    }



    /**
     * Returns the file size, in either KB or MB as appropriate, nicely formatted for display.
     * 
     * @return the file size, in either KB or MB as appropriate, nicely formatted for display
     */
    public String formattedFileSize()
    {
        double fileSize;
        String legend;

        if (currentFile().fileSizeUsage().longValue() > SMFileUtils.BYTES_PER_MEGABYTE)
        {
            fileSize = currentFile().fileSizeInMegabytes();
            legend = "MB";
        }
        else
        {
            fileSize = currentFile().fileSizeInKilobytes();
            legend = "KB";
        }

        return standardNumberFormatter.stringForObjectValue(new Float(fileSize)) + " " + legend;
    }


    /* Generic setters and getters ***************************************/

    public SiteFile currentFile()
    {
        return currentFile;
    }


    public void setCurrentFile(SiteFile value)
    {
        currentFile = value;
        website = null;  // will be lazy-init'ed from the current file
    }


    /** @TypeInfo java.lang.String */
    public NSArray mimeTypes()
    {
        return mimeTypes;
    }


    public void setMimeTypes(NSArray newMimeTypes)
    {
        mimeTypes = newMimeTypes;
    }


    public WOComponent previousPage()
    {
        previousPage.ensureAwakeInContext(context());
        return previousPage;
    }
}
