// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.components.SMAuthComponent;
import com.gvcsitemaker.core.components.WebsiteContainerBase;
import com.gvcsitemaker.core.interfaces.SMSecurePage;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;
import com.gvcsitemaker.core.support.WOFileUploadBindings;
import com.gvcsitemaker.core.utility.DebugOut;
import com.gvcsitemaker.core.utility.SMFileUtils;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSNumberFormatter;


/*
 * This page is where existing SiteFiles are replaced uploaded. The upload is
 * done via streaming with the WOFileUpload widget.
 */
public class FileReplace extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    public SiteFile currentFile; // SiteFile being uploaded for creation or replaced, public for JASS
    protected Website website; // Website this SiteFile is being uploaded to.  Implements WebsiteContainer
    protected EOEditingContext editingContext; // Peer EOEditingContext to suppport easy cancellation
    protected boolean isConfirmMode = false;
    protected WOFileUploadBindings fileUploadBindings;

    protected static final int NUMBER_OF_UPLOADS = 1;
    protected boolean showConfirmationPage;
    protected WOComponent previousPage;

    // Not sure why this is required, but it is getting set by the Access Group tab
    public Group editingAccessGroup;


    /**
     * Designated constructor.
     */
    public FileReplace(WOContext aContext)
    {
        super(aContext);
        previousPage = context().page();

        editingContext = ((SMSession) session()).newEditingContext();
        WebsiteContainerBase.getWebsiteFromPageCreating(this);

        setShowConfirmationPage(true);
        fileUploadBindings = new WOFileUploadBindings();
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
     * Saves the uploaded file.  
     *
     */
    public WOComponent saveUpload()
    {
        // This page does not change the folder
        fileUploadBindings.setFolder(currentFile.folder());

        DebugOut.println(10, "Will show confirmation page? " + showConfirmationPage());

        DebugOut.println(10, "Upload : " + fileUploadBindings);

        if (!fileUploadBindings.wasFileUploaded())
        {
            fileUploadBindings.setStatusMessage("Upload failed - No file selected.");
        }
        else if ((website().fileForFilename(fileUploadBindings.uploadedFileName()) != null)
                && (website().fileForFilename(fileUploadBindings.uploadedFileName()) != currentFile()))
        {
            fileUploadBindings.setStatusMessage("Upload failed - the name of the file uploaded matches the name of another file already uploaded.");
        }
        else if ((website().availableSpaceInBytes() + currentFile.fileSizeUsage().longValue()) < fileUploadBindings.uploadedFile().length())
        {
            fileUploadBindings.setStatusMessage("Upload failed - file is larger than available quota.");
        }
        else
        {
            currentFile.initializeFromUpload(fileUploadBindings);
            try
            {
                editingContext().saveChanges();
                fileUploadBindings.setStatusMessage("Upload successful.");
            }
            catch (Exception e)
            {
                fileUploadBindings.setStatusMessage("Upload save failed - " + e);

            }
        }

        if (showConfirmationPage())
        {
            setIsConfirmMode(true);
            return context().page();
        }

        // Only do this if not confirming.
        fileUploadBindings.cleanupFiles();
        return previousPage();
    }



    /**
     * Cancels the upload process.  
     *
     */
    public WOComponent cancelUpload()
    {
        fileUploadBindings.cleanupFiles();
        editingContext().revert();
        return previousPage();
    }



    /**
     * Returns the SiteFile being uploaded.  If no SiteFile has been set this is taken to indicate an upload of a new file and a new SiteFile instance is created to handle this.
     *
     * @return the SiteFile being uploaded.  
     */
    public SiteFile currentFile()
    {
        return currentFile;
    }



    /**
     * Sets the SiteFile to be uploaded.  This is moved into the peer editingContext() if needed.
     *
     * @return the SiteFile being uploaded.
     */
    public void setCurrentFile(SiteFile value)
    {
        /** require [current_file_is_null_or_value] (currentFile == null) || (value == currentFile); **/

        if (value != null)
        {
            // The SiteFile must be in our private ec for the save to work.
            currentFile = (SiteFile) EOUtilities.localInstanceOfObject(editingContext(), value);
        }
        else
        {
            currentFile = null;
            website = null;
        }
    }



    /**
     * Returns the peer editing context used by this and the FileEdit pages to allow cancelling of file upload operations.
     *
     * @return the peer editing context used by this and the FileEdit pages to allow cancelling of file upload operations.
     */
    public EOEditingContext editingContext()
    {
        return editingContext;
    }



    /**
     * Returns the page title which varies whether the user can or cannot modify the file.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Replace File";
    }



    /**
     * @return the upload size, in KB, for the confirmation page
     */
    public String formattedFileSize()
    {
        if (fileUploadBindings.wasFileUploaded())
        {
            NSNumberFormatter standardNumberFormatter = (NSNumberFormatter) SMApplication.appProperties().propertyForKey("StandardNumberFormatter");
            return standardNumberFormatter.stringForObjectValue(new Float(SMFileUtils.bytesToKiloBytes(fileUploadBindings.uploadedFileSize()))) + "KB";
        }

        return "";
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



    /* Generic setters and getters ***************************************/

    public Website website()
    {
        return website;
    }


    public void setWebsite(Website value)
    {
        website = (Website) EOUtilities.localInstanceOfObject(editingContext(), value);
    }


    public int numberOfUploads()
    {
        return NUMBER_OF_UPLOADS;
    }


    public boolean showConfirmationPage()
    {
        return showConfirmationPage;
    }


    public void setShowConfirmationPage(boolean newShowConfirmationPage)
    {
        showConfirmationPage = newShowConfirmationPage;
    }


    public boolean isConfirmMode()
    {
        return isConfirmMode;
    }


    public void setIsConfirmMode(boolean newIsConfirmMode)
    {
        isConfirmMode = newIsConfirmMode;
    }


    public WOFileUploadBindings fileUploadBindings()
    {
        return fileUploadBindings;
    }


    public WOComponent previousPage()
    {
        previousPage.ensureAwakeInContext(context());
        return previousPage;
    }
}
