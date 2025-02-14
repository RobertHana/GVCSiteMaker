// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;


import com.gvcsitemaker.core.SiteFileFolder;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.interfaces.SMSecurePage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import net.global_village.foundation.ValidationExceptionAdditions;


/**
 * This page handles both creating a new folder (SiteFileFolder) and changing the properties of an 
 * existing folder.
 */
public class FolderAddEdit extends com.gvcsitemaker.core.components.WebsiteContainerBase
                           implements SMSecurePage
{
    protected String message;                   // Validation error message
    protected EOEditingContext editingContext;  // Peer ec

    protected SiteFileFolder folder;            // Folder being worked on
    protected WOComponent previousPage;         // WOComponent to return to
        
    
    /**
     * Designated constructor.
     */
    public FolderAddEdit(WOContext context) {
        super(context);
        previousPage = context().page();
    }


    
    /**
     * Call this externally to create a new folder in the uploadedFilesFolder() of website().  
     * All folders are created as children of the website().uploadedFilesFolder()
     */
    public void createFolder()
    {
        setFolder(website().uploadedFilesFolder().createFolder(null));

        /** ensure [has_folder] folder() != null;  **/
    }



    /**
     * Call this externally to edit the passed folder.
     *
     * @param aFolder the SiteFileFolder to edit
     */
    public void editFolder(SiteFileFolder aFolder)
    {
        /** require [correct_website] EOUtilities.localInstanceOfObject(editingContext(), ((SiteFileFolder)aFolder).website()) == website(); **/
        SiteFileFolder localFolder = (SiteFileFolder) EOUtilities.localInstanceOfObject(editingContext(), aFolder);
        setFolder(localFolder);

        /** ensure [has_folder] folder() != null;  **/    
    }



    /**
     * Attempts to save changes (creating folder or updating existing folder).  If the save fails, 
     * redisplays this page with error messages.  If the save succeeds, returns to the previous page.
     */
    public WOComponent saveChanges()
    {
        WOComponent nextPage = this;

        setMessage(null);
        try
        {
             editingContext().saveChanges();
            nextPage = previousPage();
            nextPage.ensureAwakeInContext(context());
        }
        catch ( NSValidation.ValidationException e)
        {
            NSArray exceptions = (NSArray) ValidationExceptionAdditions.unaggregateExceptions(e).valueForKey("message");
            setMessage(exceptions.componentsJoinedByString(" "));
        }

        return nextPage;

        /** ensure [valid_state] (message() != null) || ! editingContext().hasChanges();  **/
    }



    /**
     * Action method to cancel the addition of a new table.
     */
    public WOComponent cancelAddFolder()
    {
        /** require [is_adding] ! isModifyingSiteFileFolder();  **/
        editingContext().revert();
        createFolder();      // In case someone backtracks here.  As an alternative we could force them back to ConfigPage.
        previousPage().ensureAwakeInContext(context());

        return previousPage();
    }



    /**
     * Action method to cancel edits to an existing table.
     */
    public WOComponent cancelModifyFolder()
    {
        editingContext().revert();
        return previousPage();
    }

  

    /**
     * Sets the Website after translating it into our editing context.  This is part of WebsiteContainerBase.
     *
     * @param value - the Website to add or edit the Database SiteFileFolder in.
     */
    public void setWebsite(Website value)
    {
        /** require [value_not_null] value != null; **/
        
        super.setWebsite((Website) EOUtilities.localInstanceOfObject(editingContext(), value));

        /** ensure [website_in_correct_ec] editingContext().equals(website().editingContext());  **/
    }



    /**
     * Returns <code>true</code> if the table being edited has already been saved, <code>false</code> if it is a new table being added.
     *
     * @return  <code>true</code> if the table being edited has already been saved, <code>false</code> if it is a new table being added.
     */
    public boolean isModifyingSiteFileFolder()
    {
        return ! editingContext().globalIDForObject(folder()).isTemporary();
    }


    /**
     * Overridden to handle validation errors when setting folder name.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.equals("folder.name"))
        {
            // Force the invalid value into the name so that the user can see it
            takeValueForKeyPath(value, keyPath);
            setMessage(t.getMessage());
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }

    /**
     * Returns the appropriate page title for the mode of this page (add/edit).
     *
     * @return the appropriate page title for the mode of this page (add/edit).
     */
    public String pageTitle() 
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": " +
        (isModifyingSiteFileFolder() ? "Change Folder Properties" : "Create Folder");
    }


    
    //****************************  Generic Accessors Below Here  ****************************\\

    public void setMessage(String newMessage) {
        message = newMessage;
    }
    public String message() {
        return message;
    }

    public EOEditingContext editingContext()
    {
        // Create this on demand and our super's constructor causes setWebsite to be called and it needs this.
        if (editingContext == null)
        {
            editingContext = smSession().newEditingContext();
        }

        return editingContext;
    }

     public SiteFileFolder folder() {
        return folder;
    }
    public void setFolder(SiteFileFolder newFolder) {
        /** require [valid_param] newFolder != null;   **/
        folder = (SiteFileFolder) EOUtilities.localInstanceOfObject(editingContext(), newFolder);
    }
    
    public WOComponent previousPage() {
        return previousPage;
    }
    
}
