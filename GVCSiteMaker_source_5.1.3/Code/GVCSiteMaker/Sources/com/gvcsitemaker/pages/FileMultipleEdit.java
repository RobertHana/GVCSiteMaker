// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import java.util.Enumeration;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.components.DummyAccessible;
import com.gvcsitemaker.core.Group;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.core.SiteFileFolder;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.components.SMAuthComponent;
import com.gvcsitemaker.core.components.WebsiteContainerBase;
import com.gvcsitemaker.core.interfaces.SMSecurePage;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
  

/**
 * This page provides functions for changing the folder, access groups, or deleting multiple files.
 */
public class FileMultipleEdit extends SMAuthComponent implements WebsiteContainer, SMSecurePage 
{
    public static final String MOVE_MODE = "move";
    public static final String DELETE_MODE = "delete";
    public static final String GROUPS_MODE = "groups";
    public static final NSArray VALID_MODES = new NSArray(new Object[] {MOVE_MODE, DELETE_MODE, GROUPS_MODE});

    
    protected Website website;                  // For WebsiteContainer
    protected EOEditingContext editingContext;  // Peer EOEditingContext to suppport easy cancellation

    /** @TypeInfo com.gvcsitemaker.core.SiteFile */
    protected NSArray files;                    // SiteFiles being edited
    protected String currentMode;

    protected WOComponent previousPage;
    public SiteFileFolder aFolder;
    protected SiteFileFolder selectedFolder;
    public SiteFile aFile;
    public DummyAccessible selectedGroups;

    // Not sure why this is required, but it is getting set by the Access Group tab
    public Group editingAccessGroup;

    
    
    /**
     * Designated constructor.  
     */
    public FileMultipleEdit(WOContext aContext)
    {
        super(aContext);
        previousPage = context().page();
        editingContext = ((SMSession)session()).newEditingContext();
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
        currentMode = null;
        // Helper class used as we have no EO object to get/set the access groups.
        selectedGroups = new DummyAccessible(editingContext());
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
     * Use this method to set the list of files to be updated and the update mode.
     * 
     * @param filesToChange list of the files to change
     * @param changeMode the change mode, one of DELETE_MODE, MOVE_MODE, or GROUPS_MODE
     */
    public void setFilesAndMode(NSArray filesToChange, String changeMode) 
    {
        /** require [valid_files] (filesToChange != null) && filesToChange.count() > 0;
                    [valid_mode] (changeMode != null) && VALID_MODES.containsObject(changeMode);  **/ 

        files = EOUtilities.localInstancesOfObjects(editingContext(), filesToChange);
        currentMode = changeMode;
    }

    
    
    /**
     * Cancel changes.  
     *
     * @return FileMgmt page
     */
    public WOComponent cancelUpload()
    {
        DebugOut.println(30,"Site's files before :" + website().files().valueForKey("uploadedFilename"));

        // Get rid of all name, quota, etc. changes
        editingContext().revert();
        DebugOut.println(30,"Site's files after :" + website().files().valueForKey("uploadedFilename"));

        return previousPage();
    }



    /**
     * Finish updating the files.
     *
     */
    public WOComponent saveChanges()
    {
        DebugOut.println(10,"Entering...");
        if (isDeleting())
        {
            deleteFiles();
        }
        else if (isMovingFiles())
        {
            moveFiles();
        }
        else if (isChangingGroups())
        {
            changeAccessGroups();
        }
           
        editingContext().saveChanges();
        return previousPage();
    }

    
    
    /**
     * Handles deletion of the files.
     */
    protected void deleteFiles()
    {
        Enumeration enumeration = files().objectEnumerator();
        while (enumeration.hasMoreElements()) {
            SiteFile file = (SiteFile) enumeration.nextElement();
            website().removeSiteFile(file);
        }
    }
    
    

    /**
     * Handles moving the files.
     */
    protected void moveFiles()
    {
        Enumeration enumeration = files().objectEnumerator();
        while (enumeration.hasMoreElements()) {
            SiteFile file = (SiteFile) enumeration.nextElement();
            file.removeObjectFromBothSidesOfRelationshipWithKey(file.folder(), "folder");
            file.addObjectToBothSidesOfRelationshipWithKey(selectedFolder(), "folder");
        }
    }
    

    
    /**
     * Handles changing the access protection of the files.
     */
    protected void changeAccessGroups()
    {
        Enumeration enumeration = files().objectEnumerator();
        while (enumeration.hasMoreElements()) {
            SiteFile file = (SiteFile) enumeration.nextElement();
            
            // Remove the existing groups
            Enumeration groupEnum = new NSArray(file.groups()).objectEnumerator();
            while (groupEnum.hasMoreElements())
            {
            	file.removeObjectFromBothSidesOfRelationshipWithKey((Group)groupEnum.nextElement(), "groups");
            }

            // Add the new groups
            groupEnum = selectedGroups.groups().objectEnumerator();
            while (groupEnum.hasMoreElements())
            {
            	file.addObjectToBothSidesOfRelationshipWithKey((Group)groupEnum.nextElement(), "groups");
            }
        }
    }
    

    
    /**
     * @return <code>true</code> if the current mode is to delete files
     */
    public boolean isDeleting() {
        return currentMode().equals(DELETE_MODE);
    }
    
    
    
    /**
     * @return <code>true</code> if the current mode is to move files
     */
    public boolean isMovingFiles() {
        return currentMode().equals(MOVE_MODE);
    }

    
    
    /**
     * @return <code>true</code> if the current mode is to change access groups
     */
    public boolean isChangingGroups() {
        return currentMode().equals(GROUPS_MODE);
    }

    
    
    /**
     * Returns the page title which varies whether the user can or cannot modify the file.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Revise File";
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

    public EOEditingContext editingContext() {
        return editingContext;
    }

    public Website website() {
        return website;
    }
    public void setWebsite(Website value) {
        website = (Website)EOUtilities.localInstanceOfObject(editingContext(), value);
    }
    
    /** @TypeInfo com.gvcsitemaker.core.SiteFile */
    public NSArray files() {
        return files;
    }

    protected String currentMode() {
        return currentMode;
        /** ensure [valid_result] Result != null;  [valid_mode] VALID_MODES.containsObject(Result);  **/
    }

    public WOComponent previousPage() {
        previousPage.ensureAwakeInContext(context());
        return previousPage;
    }
    public SiteFileFolder selectedFolder() {
        return selectedFolder;
    }
    public void setSelectedFolder(SiteFileFolder newSelectedFolder) {
        selectedFolder = newSelectedFolder;
    }

}


