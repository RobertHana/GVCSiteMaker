// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.components.DummyAccessible;
import com.gvcsitemaker.core.SiteFileFolder;
import com.gvcsitemaker.core.Website;
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
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSNumberFormatter;


/**
 * This page is where new SiteFiles get uploaded.  The upload is done via streaming with the WOFileUpload widget.
 */
public class FileUpload extends SMAuthComponent implements WebsiteContainer, SMSecurePage 
{
        
    protected Website website; 					// Website this SiteFile is being uploaded to.  Implements WebsiteContainer
    protected EOEditingContext editingContext;	// Peer EOEditingContext to suppport easy cancellation
    protected boolean showConfirmationPage;
    
    /** @TypeInfo com.gvcsitemaker.pages.FileUpload.WOFileUploadBindings */
    protected NSArray fileUploadBindings;
    public WOFileUploadBindings aFileUploadBinding;
    public DummyAccessible selectedGroups;
    protected static final int NUMBER_OF_UPLOADS = SMApplication.appProperties().intPropertyForKey("NumberOfFileUploadWidgets");
    protected boolean isConfirmMode = false;
    public int uploadIndex;
    public SiteFileFolder aFolder;

    /** @TypeInfo SiteFileFolder */
    protected SiteFileFolder selectedFolder;
    protected WOComponent previousPage;
    protected boolean inPlaceEditingMode;
    

    /**
     * Designated constructor.
     */
    public FileUpload(WOContext aContext)
    {
        super(aContext);
        previousPage = context().page();
        editingContext = ((SMSession)session()).newEditingContext();
        WebsiteContainerBase.getWebsiteFromPageCreating(this);  // No EO to get this from.
        
        // Helper class used as we have no EO object to get/set the access groups.
        selectedGroups = new DummyAccessible(editingContext());
        
        setShowConfirmationPage(true);
        
        // Create an object to hold the results of each File upload.
        NSMutableArray tempFileBindings = new NSMutableArray();
        for (int i = 0; i < NUMBER_OF_UPLOADS; i++)
        {
            tempFileBindings.addObject(new WOFileUploadBindings());
        }
        setFileUploadBindings(tempFileBindings);
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Saves the uploads.  
     *
     */
    public WOComponent saveUploads()
    {
        editingContext().revert();
        DebugOut.println(10, "Will show confirmation page? " + showConfirmationPage());

        for (int i = 0; i < NUMBER_OF_UPLOADS; i++)
        {
            WOFileUploadBindings bindings = (WOFileUploadBindings)fileUploadBindings.objectAtIndex(i);
            DebugOut.println(10, "Upload " + (i+1) + ": " + bindings);
            
            if ( ! bindings.wasFileUploaded())
            {
                bindings.setStatusMessage("No file selected.");
            }
            else if (website().fileForFilename(bindings.uploadedFileName()) != null)
            {
                bindings.setStatusMessage("Upload ignored - file with this name already exists, use replace function.");
            }
            else if (website().availableSpaceInBytes() < bindings.uploadedFile().length())
            {
                bindings.setStatusMessage("Upload ignored - file is larger than available quota.");
            }
            else
            {
                bindings.setGroups(selectedGroups.groups());
                bindings.setFolder(selectedFolder());
                website().createNewSiteFileFromUpload(bindings);
                try
                {
                    editingContext().saveChanges();
                    bindings.setStatusMessage("Upload successful.");
                }
                catch (Exception e)
                {
                    bindings.setStatusMessage("Upload save failed - " + e);

                }
            }
        }
        
        if (showConfirmationPage())
        {
            setIsConfirmMode(true);
            return context().page();
        }
        
        // Only do this if not confirming
        cleanupTempFiles();
        return previousPage();
    }
    
    
    
    /**
     * Cancels the upload process.  
     *
     */
    public WOComponent cancelUpload()
    {
        cleanupTempFiles();
        editingContext().revert();
        return previousPage();
    }


    
    /**
     * Cleans up temp files created by WOFileUploadBindings/
     */
    protected void cleanupTempFiles() 
    {
        for (int i = 0; i < NUMBER_OF_UPLOADS; i++)
        {
            WOFileUploadBindings bindings = (WOFileUploadBindings)fileUploadBindings.objectAtIndex(i);
            bindings.cleanupFiles();
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
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Upload New Files";
    }

    

    /**
     * Returns <code>true</code> if the logged in user can upload file to this website.
     *
     * @return  <code>true</code> if the logged in user can upload file to this website.
     */
    public boolean canUserUploadFiles()
    {
        return (website().canBeConfiguredByUser(((Session) session()).currentUser()) || website().isSectionEditor(((Session) session()).currentUser()) 
            || website().isSectionContributor(((Session) session()).currentUser())); 
    }

      

    /**
     * Returns true if user cannot upload file to this site.
     */
    public boolean canNotUserUploadFiles()
    {
        return ! canUserUploadFiles();
    }
    

    
    /**
     * @return the upload size, in KB, for the confirmation page
     */
    public String formattedFileSize() {
        if (aFileUploadBinding.wasFileUploaded())
        {
            NSNumberFormatter standardNumberFormatter = (NSNumberFormatter)SMApplication.appProperties().propertyForKey("StandardNumberFormatter");
            return standardNumberFormatter.stringForObjectValue(new Float(SMFileUtils.bytesToKiloBytes(aFileUploadBinding.uploadedFileSize()))) + "KB";
        }
        
        return "";
    }
    
    
    /**
     * @return 1..N index of uploads for confirmation page.
     */
    public String formattedUploadIndex() {
        return Integer.toString(uploadIndex + 1);
    }
    
    
    /* Generic setters and getters ***************************************/

    public Website website() {
        return website;
    }
    public void setWebsite(Website value) {
        website = (Website)EOUtilities.localInstanceOfObject(editingContext(), value);
    }

    public int numberOfUploads() {
            return NUMBER_OF_UPLOADS;
    }
    
    public boolean showConfirmationPage() {
        return showConfirmationPage;
    }
    public void setShowConfirmationPage(boolean newShowConfirmationPage) {
        showConfirmationPage = newShowConfirmationPage;
    }

    /** @TypeInfo com.gvcsitemaker.pages.FileUpload.WOFileUploadBindings */
    public NSArray fileUploadBindings() {
        return fileUploadBindings;
    }
    public void setFileUploadBindings(NSArray newFileUploadBindings) {
        fileUploadBindings = newFileUploadBindings;
    }


    public boolean isConfirmMode() {
        return isConfirmMode;
    }
    public void setIsConfirmMode(boolean newIsConfirmMode) {
        isConfirmMode = newIsConfirmMode;
    }

    /** @TypeInfo SiteFileFolder */
    public SiteFileFolder selectedFolder() {
        return selectedFolder;
    }
    public void setSelectedFolder(SiteFileFolder newSelectedFolder) {
        selectedFolder = newSelectedFolder;
    }

    
    public WOComponent previousPage() {
        previousPage.ensureAwakeInContext(context());
        return previousPage;
    }
    
    public boolean inPlaceEditingMode() {
        return inPlaceEditingMode;
    }
    public void setInPlaceEditingMode( boolean newMode ) {
            inPlaceEditingMode = newMode;
    }    
}


