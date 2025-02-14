// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


/**
 * This page provides functions for managing the SiteFiles uploaded to a
 * WebSite. The available functions are:<br> - upload a new file <br> - delete a
 * previously uploaded file <br> - replace a previously uploaded file <br> -
 * change the properties (access groups, description, MIME type) for a
 * previously uploaded file <br> - view a previously uploaded file <br> - send a
 * link to a previously uploaded file via e-mail <br>
 */
public class FileMgmt extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    public SiteFileFolder aFolder;
    public int aFolderIndex;
    public SiteFile aFile;
    public Group aGroup;

    protected NSArray orderedFolders;
    public NSMutableSet selectedFiles;
    public NSArray selectedFilesActions = new NSArray(new Object[] { "Select All Visible", "Unselect All Visible", "Set Access on Selected", "Move Selected To Folder", "Delete Selected" });
    public String anActionString;
    public String selectedFilesAction = null;
    public NSMutableDictionary selectedFileAction = new NSMutableDictionary();

    public static final String NAME_SORT = "uploadedFilename";
    public static final String SIZE_SORT = "fileSizeUsage";
    protected String sortOn = NAME_SORT;
    protected NSSelector sortOrder = EOSortOrdering.CompareAscending;

    protected NSMutableSet foldersShowingFiles;
    protected NSMutableSet filesShowingGroups;

    protected EOEditingContext editingContext; // Peer ec
    protected Website website; // For WebsiteContainer implementation, the Website we are managing files for

    protected SiteFile fileToDelete; // The file to be deleted
    protected SiteFileFolder folderToDelete; // The folder to be deleted
    public SiteFileFolder folderToMoveTo;

    protected NSNumberFormatter standardNumberFormatter;
    protected NSTimestampFormatter dateAndTimeFormatter;

    public String errorMessage;

    // Not sure why this is required, but it is getting set by the Access Group tab
    public Group editingAccessGroup;


    /**
     * Designated constructor.  Sets Website for WebsiteContainer.
     */
    public FileMgmt(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
        standardNumberFormatter = (NSNumberFormatter) SMApplication.appProperties().propertyForKey("StandardNumberFormatter");
        dateAndTimeFormatter = (NSTimestampFormatter) SMApplication.appProperties().propertyForKey("DateAndTimeFormatter");

        selectedFiles = new NSMutableSet();
    }



    /**
     * Returns the ordered list of folders.
     *
     * @return a list of folders, sorted by sortOn
     */
    public NSArray orderedFolders()
    {
        if (orderedFolders == null)
        {
            NSMutableArray unorderedFolders = website().orderedUserFolders().mutableClone();
            String sortKey = sortOn().equals(NAME_SORT) ? "name" : "fileSizeUsage";
            EOSortOrdering sortOrder = new EOSortOrdering(sortKey, sortOrder());
            EOSortOrdering.sortArrayUsingKeyOrderArray(unorderedFolders, new NSArray(sortOrder));
            unorderedFolders.removeObject(website().uploadedFilesFolder());
            unorderedFolders.insertObjectAtIndex(website().uploadedFilesFolder(), 0);
            orderedFolders = unorderedFolders;
        }
        return orderedFolders;
    }


    /**
     * Returns the ordered list of folders.
     *
     * @return a list of folders, sorted by sortOn
     */
    public NSArray orderedFiles()
    {
        NSArray unorderedFiles = aFolder.orderedFiles();
        EOSortOrdering sortOrder = new EOSortOrdering(sortOn(), sortOrder());
        return EOSortOrdering.sortedArrayUsingKeyOrderArray(unorderedFiles, new NSArray(sortOrder));
    }



    /**
     * Used by the AJAX control on this page to revert changes.
     *
     * @return null, this value is ignored
     */
    public WOComponent revertChanges()
    {
        website().editingContext().revert();
        return null; // return value is ignored
    }


    /**
     * Used by the AJAX control on this page to save changes.
     *
     * @return null, this value is ignored
     */
    public WOComponent saveChanges()
    {
        try
        {
            website().editingContext().saveChanges();
        }
        catch (Exception e)
        {
            // shouldn't be any exceptions
            website().editingContext().revert();
        }
        return null; // return value is ignored
    }



    /**
     * Action method to create a new folder
     */
    public void createFolder()
    {
        website().uploadedFilesFolder().createFolder(website().uploadedFilesFolder().newFolderName());
        orderedFolders = null;
        saveChanges();
    }



    /**
     * Adds or removes the current file from the list of selected files.
     *
     * @param selected is the current file selected?
     */
    public void setFileSelected(boolean selected)
    {
        if (selected)
        {
            selectedFiles.addObject(aFile);
        }
        else
        {
            selectedFiles.removeObject(aFile);
        }
    }


    /**
     * Returns true iff the current file is in the list of selected files.
     *
     * @return true iff the current file is in the list of selected files
     */
    public boolean isFileSelected()
    {
        return selectedFiles.containsObject(aFile);
    }


    /**
     * Returns the CSS class to use for the current file in the list.
     *
     * @return the CSS class to use for the current file in the list
     */
    public String fileListCSSClass()
    {
        return (selectedFiles.contains(aFile)) ? "selectedFile" : "";
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

        if (aFile.fileSizeUsage().longValue() > SMFileUtils.BYTES_PER_MEGABYTE)
        {
            fileSize = aFile.fileSizeInMegabytes();
            legend = "MB";
        }
        else
        {
            fileSize = aFile.fileSizeInKilobytes();
            legend = "KB";
        }

        return standardNumberFormatter.stringForObjectValue(new Float(fileSize)) + "&nbsp;" + legend;
    }



    /**
     * Returns <code>true</code> if the logged in user can configure this website.  This is needed as the DirectAction configSiteFileMgmtAction() does not check this so it must be handled on this page.
     *
     * @return  <code>true</code> if the logged in user can configure this website.  
     */
    public boolean canUserConfigureWebsite()
    {
        return website().canBeConfiguredByUser(((Session) session()).currentUser());
    }


    /**
     * Returns true if user cannot configure websites. false otherwise.
     */
    public boolean canNotConfigureWebsite()
    {
        return !canUserConfigureWebsite();
    }



    /**
     * Sorts the files in all folders based on sortColumn.  If sorting is already on this column, the
     * sort order (ascending or descending) is reversed.
     * 
     * @param sortColumn the column to sort on
     */
    public void sortOn(String sortColumn)
    {
        /** require [valid_sort_colum] (sortColumn != null);  **/

        if (sortOn().equals(sortColumn))
        {
            setSortOrder(isAscendingSort() ? EOSortOrdering.CompareDescending : EOSortOrdering.CompareAscending);
        }
        else
        {
            setSortOn(sortColumn);
            setSortOrder(EOSortOrdering.CompareAscending);
        }

        // reset cached order
        orderedFolders = null;

        /** ensure [sort_set] isSortingOn(sortColumn);   **/
    }



    /**
     * Returns <code>true</code> if sorting is currently on the passed column. 
     * 
     * @param sortColumn the column name to compare to the column currently sorted on
     * @return <code>true</code> if sorting is currently on the passed column
     */
    public boolean isSortingOn(String sortColumn)
    {
        /** require [valid_sort_colum] (sortColumn != null);  **/
        return sortOn().equals(sortColumn);
    }



    /**
     * @return <code>true</code> if sorting is currently in ascending order
     */
    public boolean isAscendingSort()
    {
        return sortOrder.equals(EOSortOrdering.CompareAscending);
    }



    /**
     * Sorts the list of files by name.  If the files are already sorted on name, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnFilename()
    {
        sortOn(NAME_SORT);
        return context().page();
    }



    /**
     * Sorts the list of files by size.  If the files are already sorted on size, this will reverse
     * the sort order.
     * 
     * @return current page
     */
    public WOComponent sortOnSize()
    {
        sortOn(SIZE_SORT);
        return context().page();
    }



    /**
     * @return the appropriate up/down arrow image for the selected sort column
     */
    public String sortDirectionImage()
    {
        return isAscendingSort() ? "/GVCSiteMaker/Images/sort-ascending.gif" : "/GVCSiteMaker/Images/sort-descending.gif";
    }



    /**
     * @return <code>true</code> if sorting is on file name
     */
    public boolean isSortingOnName()
    {
        return isSortingOn(NAME_SORT);
    }



    /**
     * @return <code>true</code> if sorting is on file size
     */
    public boolean isSortingOnSize()
    {
        return isSortingOn(SIZE_SORT);
    }



    /**
     * @return the appropriate open/closed folder image for the current folder.
     */
    public String folderIcon()
    {
        return folderShowsFiles() ? "/GVCSiteMaker/Images/folder_open.gif" : "/GVCSiteMaker/Images/folder_closed.gif";
    }



    /**
     * @return the appropriate expand/collapse arrow for the current folder.
     */
    public String folderExpanderIcon()
    {
        return folderShowsFiles() ? "/GVCSiteMaker/Images/collapse.gif" : "/GVCSiteMaker/Images/expand.gif";
    }


    /**
     * @return file description and last modified for use as title/tooltip text
     */
    public String filenameToolTipText()
    {
        StringBuffer sb = new StringBuffer(512);
        sb.append("Uploaded: ");
        sb.append(dateAndTimeFormatter.format(aFile.uploadDate()));

        if (aFile.fileDescription() != null)
        {
            sb.append("\n");
            sb.append(aFile.fileDescription());
        }

        return sb.toString();
    }



    /**
     * Move the current file to the selected folder.
     */
    public void moveFiles()
    {
        if (folderToMoveTo != null)
        {
            Enumeration enumeration = selectedFiles.objectEnumerator();
            while (enumeration.hasMoreElements())
            {
                SiteFile file = (SiteFile) enumeration.nextElement();
                file.removeObjectFromBothSidesOfRelationshipWithKey(file.folder(), "folder");
                file.addObjectToBothSidesOfRelationshipWithKey(folderToMoveTo, "folder");
            }

            folderToMoveTo = null;
            saveChanges();

            selectedFiles = new NSMutableSet();
        }
    }


    /**
     * Delete the current file.
     */
    public void confirmDeleteFolder()
    {
        if (aFolder.files().count() > 0)
        {
            errorMessage = "Cannot delete a folder that contains files.";
            AjaxModalDialog.open(context(), "ErrorMessageModalDialog");
        }
        else
        {
            folderToDelete = aFolder;
            AjaxModalDialog.open(context(), "ConfirmDeleteFolderModalDialog");
        }
    }


    /**
     * Delete the current file.
     */
    public void deleteFolder()
    {
        /** require [folderToDelete_not_null] folderToDelete != null; **/

        folderToDelete = (SiteFileFolder)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), folderToDelete);

        // clear cached values
        if (foldersShowingFiles.containsObject(folderToDelete))
        {
            foldersShowingFiles.removeObject(folderToDelete);
        }
        orderedFolders = null;

        EOEditingContext ec = folderToDelete.editingContext();
        ec.deleteObject(folderToDelete);
        ec.saveChanges();

        folderToDelete = null;
    }


    /**
     * Delete the current file.
     */
    public void deleteFile()
    {
        /** require [fileToDelete_not_null] fileToDelete != null; **/

        EOEditingContext deleteContext = ((SMSession) session()).newEditingContext();
        Website localSite = (Website) EOUtilities.localInstanceOfObject(deleteContext, website());
        SiteFile localFile = (SiteFile) EOUtilities.localInstanceOfObject(deleteContext, fileToDelete);

        localSite.removeFromFiles(localFile);
        localFile.folder().removeFromFiles(localFile);
        deleteContext.deleteObject(localFile);
        DebugOut.println(3, "Deleting " + fileToDelete.filename());

        fileToDelete = null;
        if (filesShowingGroups().containsObject(fileToDelete))
        {
            filesShowingGroups().removeObject(fileToDelete);
        }

        deleteContext.saveChanges();
        ((SMSession)session()).unregisterEditingContext(deleteContext);
    }


    /**
     * Delete the selected files.
     */
    public void deleteSelectedFiles()
    {
        NSMutableSet filesToDelete = selectedFiles.mutableClone();

        EOEditingContext deleteContext = ((SMSession) session()).newEditingContext();
        Website localSite = (Website) EOUtilities.localInstanceOfObject(deleteContext, website());

        Enumeration filesToDeleteEnumerator = filesToDelete.objectEnumerator();
        while (filesToDeleteEnumerator.hasMoreElements())
        {
            fileToDelete = (SiteFile)filesToDeleteEnumerator.nextElement();
            SiteFile localFile = (SiteFile) EOUtilities.localInstanceOfObject(deleteContext, fileToDelete);

            localSite.removeFromFiles(localFile);
            localFile.folder().removeFromFiles(localFile);
            deleteContext.deleteObject(localFile);
            if (filesShowingGroups().containsObject(localFile))
            {
                filesShowingGroups().removeObject(localFile);
            }
            DebugOut.println(3, "Deleting " + fileToDelete.filename());
        }

        fileToDelete = null;
        deleteContext.saveChanges();
        selectedFiles = new NSMutableSet();
        ((SMSession)session()).unregisterEditingContext(deleteContext);
    }



    /**
     * Returns true iff the given file is visible.
     *
     * @return true iff the given file is visible
     */
    protected boolean fileIsVisible(SiteFile theFile)
    {
        /** require [theFile_not_null] theFile != null; **/

        return visibleFiles().contains(theFile);
    }


    /**
     * Returns an ordered array of visible files.
     *
     * @return an ordered array of visible files
     */
    protected NSArray visibleFiles()
    {
        NSMutableArray visibleFiles = new NSMutableArray();
        Enumeration folderEnumerator = foldersShowingFiles().objectEnumerator();
        while (folderEnumerator.hasMoreElements())
        {
            SiteFileFolder folder = (SiteFileFolder)EOUtilities.localInstanceOfObject(
                    website().editingContext(), (SiteFileFolder)folderEnumerator.nextElement());
            Enumeration fileEnumerator = folder.orderedFiles().objectEnumerator();
            while (fileEnumerator.hasMoreElements())
            {
                SiteFile file = (SiteFile) fileEnumerator.nextElement();
                visibleFiles.addObject(file);
            }
        }
        return visibleFiles;
    }


    /**
     * Returns "none" if the file should not be displayed, returns "" otherwise.
     *
     * @return "none" if the file should not be displayed, returns "" otherwise
     */
    public String fileDisplay()
    {
        if (fileIsVisible(aFile))
        {
            return "";
        }
        else
        {
            return "display:none;";
        }
    }



    /**
     * Returns ", " or "" as a seperator between group names in the section area.
     *
     * @return ", " or "" as a seperator between group names in the section area
     */
    public String groupSeperator()
    {
        /** require [knows_section] aSection != null;  [knows_group] aGroup != null;  **/
        return ! aGroup.equals(aFile.groups().lastObject()) ? "<br/>" : "";
    }


    /**
     * Returns the ConfigAccessGroup page, configured for aGroup.
     *
     * @return the ConfigAccessGroup page, configured for aGroup
     */
    public WOComponent manageGroup()
    {
        /** require [knows_group] aGroup != null;  [is_WebsiteGroup] aGroup instanceof WebsiteGroup; **/
        ((ConfigTabSet)parent()).setTabSelected("Tab3");
        return null;
    }


    /**
     * Returns true iff the current section has at least 2 groups.
     *
     * @return true iff the current section has at least 2 groups
     */
    public boolean hasAtLeastTwoGroups()
    {
        return aFile.groups().count() > 1;
    }


    /**
     * Returns true iff there is at least one group showing children.
     *
     * @return true iff there is at least one group showing children
     */
    public boolean hasAtLeastOneGroupExpanded()
    {
        return filesShowingGroups().count() > 0;
    }


    /**
     * Sets the view to expand all if no groups are expanded, otherwise sets the view to collapse all.
     */
    public void toggleExpandCollapseAllGroups()
    {
        if (hasAtLeastOneGroupExpanded())
        {
            filesShowingGroups().removeAllObjects();
        }
        else
        {
            filesShowingGroups().removeAllObjects();
            NSArray filesByFolder = (NSArray)website().folders().valueForKey("files");
            NSArray allFiles = net.global_village.foundation.Collection.arrayByCollapsingSubCollections(filesByFolder, false);
            filesShowingGroups().addObjectsFromArray(
                    EOUtilities.localInstancesOfObjects(session().defaultEditingContext(), allFiles));
        }
    }


    /**
     * Returns true iff this file shows its groups.
     *
     * @return true iff this file shows its groups
     */
    public boolean fileShowsGroups()
    {
        SiteFile theFile = (SiteFile)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aFile);
        return filesShowingGroups().contains(theFile);
    }


    /**
     * Toggles showing groups.
     */
    public void toggleShowGroups()
    {
        SiteFile theFile = (SiteFile)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aFile);
        if (filesShowingGroups().contains(theFile))
        {
            filesShowingGroups().removeObject(theFile);
        }
        else
        {
            filesShowingGroups().addObject(theFile);
        }
    }



    /**
     * Returns true iff this folder is the main folder.
     *
     * @return true iff this folder is the main folder
     */
    public boolean isMainFolder()
    {
        return aFolder.equals(website().uploadedFilesFolder());
    }


    /**
     * Returns true iff this folder shows its files.
     *
     * @return true iff this folder shows its files
     */
    public boolean folderShowsFiles()
    {
        SiteFileFolder theFolder = (SiteFileFolder)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aFolder);
        return foldersShowingFiles().contains(theFolder);
    }


    /**
     * Toggles showing groups.
     */
    public void toggleShowFolder()
    {
        SiteFileFolder theFolder = (SiteFileFolder)EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aFolder);
        if (foldersShowingFiles().contains(theFolder))
        {
            foldersShowingFiles().removeObject(theFolder);
        }
        else
        {
            foldersShowingFiles().addObject(theFolder);
        }
    }


    /**
     * Returns true iff there is at least one section showing children.
     *
     * @return true iff there is at least one section showing children
     */
    public boolean hasAtLeastOneFolderExpanded()
    {
        return foldersShowingFiles().count() > 0;
    }


    /**
     * Sets the view to expand all if no folders are expanded, otherwise sets the view to collapse all.
     */
    public void toggleExpandCollapseAll()
    {
        if (hasAtLeastOneFolderExpanded())
        {
            foldersShowingFiles().removeAllObjects();
        }
        else
        {
            foldersShowingFiles().removeAllObjects();
            foldersShowingFiles().addObjectsFromArray(
                    EOUtilities.localInstancesOfObjects(session().defaultEditingContext(), website().folders()));
        }
    }


    /**
     * Returns the sections that are showing children. When initially null, gets
     * the view data from the session.
     *
     * @return the sections that are showing children
     */
    public NSMutableSet foldersShowingFiles()
    {
        // if it is null, get the value from the session
        if (foldersShowingFiles == null)
        {
            NSDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("FileMgmt", website().siteID());
            foldersShowingFiles = (NSMutableSet)viewData.objectForKey("foldersShowingFiles");
        }

        // if it is still null, set it to the default open set (just the Main folder open)
        if (foldersShowingFiles == null)
        {
            NSMutableDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("FileMgmt", website().siteID());
            foldersShowingFiles = new NSMutableSet(EOUtilities.localInstanceOfObject(session().defaultEditingContext(), website().uploadedFilesFolder()));
            viewData.setObjectForKey(foldersShowingFiles, "foldersShowingFiles");
        }

        return foldersShowingFiles;

        /** ensure Result != null; **/
    }



    /**
     * Returns the sections that are showing groups. When initially null, gets
     * the view data from the session.
     *
     * @return the sections that are showing groups
     */
    public NSMutableSet filesShowingGroups()
    {
        // if it is null, get the value from the session
        if (filesShowingGroups == null)
        {
            NSDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("FileMgmt", website().siteID());
            filesShowingGroups = (NSMutableSet)viewData.objectForKey("filesShowingGroups");
        }

        // if it is still null, set it to the empty set
        if (filesShowingGroups == null)
        {
            NSMutableDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("FileMgmt", website().siteID());
            filesShowingGroups = new NSMutableSet();
            viewData.setObjectForKey(filesShowingGroups, "filesShowingGroups");
        }

        return filesShowingGroups;

        /** ensure Result != null; **/
    }



    /**
     * Worker method to return the FileMultipleEdit configured to process the marked files in the
     * indicated mode, or the current page if no files are marked.
     * 
     * @return FileMultipleEdit or current page
     */
    public WOComponent changeAccessGroups()
    {
        if (selectedFiles.count() == 0)
        {
            return context().page();
        }

        FileMultipleEdit multiEdit = (FileMultipleEdit)pageWithName("FileMultipleEdit");
        multiEdit.setFilesAndMode(selectedFiles.allObjects(), FileMultipleEdit.GROUPS_MODE);
        selectedFiles = new NSMutableSet();
        return multiEdit;
    }


    /**
     * Main dispatch for actions applying to all files (as checked by the user).
     */
    public void selectedFilesObserverAction()
    {
        if ("Select All Visible".equals(selectedFilesAction))
        {
            selectedFiles.unionSet(new NSSet(visibleFiles()));
            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteFilesPane", context());
        }
        else if ("Unselect All Visible".equals(selectedFilesAction))
        {
            selectedFiles.subtractSet(new NSSet(visibleFiles()));
            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteFilesPane", context());
        }
        else if ("Set Access on Selected".equals(selectedFilesAction))
        {
            if (selectedFiles.count() > 0)
            {
                AjaxUtils.redirectTo(changeAccessGroups());
            }
        }
        else if ("Move Selected To Folder".equals(selectedFilesAction))
        {
            if (selectedFiles.count() > 0)
            {
                AjaxModalDialog.open(context(), "MoveFilesToFolderModalDialog");
            }
        }
        else if ("Delete Selected".equals(selectedFilesAction))
        {
            if (selectedFiles.count() > 0)
            {
                AjaxModalDialog.open(context(), "ConfirmDeleteFilesModalDialog");
            }
        }

        selectedFilesAction = null;
    }



    /**
     * Action method to modify a SiteFile's properties.
     *
     * @return an instance of the FileEdit page
     */
    public WOComponent modifyFile(SiteFile theFile)
    {
        /** require theFile != null; **/
        FileEdit nextPage = (FileEdit) pageWithName("FileEdit");
        nextPage.setCurrentFile(theFile);
        return nextPage;
    }


    /**
     * Action method to replace a SiteFile.
     *
     * @return an instance of the FileUpload page
     */
    public WOComponent replaceFile(SiteFile theFile)
    {
        /** require theFile != null; **/
        FileReplace nextPage = (FileReplace) pageWithName("FileReplace");
        nextPage.setCurrentFile(theFile);
        return nextPage;
    }


    /**
     * Action method to e-mail a link to a SiteFile.
     *
     * @return an instance of the EmailPage page
     */
    public WOComponent sendEmail(SiteFile theFile)
    {
        /** require theFile != null; **/
        EmailPage emailPage = (EmailPage)pageWithName("EmailPage");
        emailPage.setFile(theFile);
        emailPage.setReturnPage(context().page());
        return emailPage;
    }
    
    
    /**
     * Returns the selected choice in the Actions column for the current file.
     *
     * @return the selected choice in the Actions column for the current file
     */
    public String selectedFileAction()
    {
        return (String)selectedFileAction.objectForKey(aFile);
    }


    /**
     * Sets the selected action in the Actions column for the current file.
     *
     * @param newAction the action to set
     */
    public void setSelectedFileAction(String newAction)
    {
        if (newAction != null)
        {
            selectedFileAction.setObjectForKey(newAction, aFile);
        }
        else
        {
            selectedFileAction.removeObjectForKey(aFile);
        }
    }


    /**
     * Returns the list of actions appropriate for the selected file.
     *
     * @return a list of actions as strings
     */
    public NSArray actionsList()
    {
        NSMutableArray actionsList = new NSMutableArray();
        actionsList.addObject("Edit Properties");  // configFile
        actionsList.addObject("Replace File");
        actionsList.addObject("Email Link To File");
        actionsList.addObject("Delete");  // deleteFile

        return actionsList;
    }


    /**
     * Main dispatch for actions applying to a single file.
     */
    public WOActionResults fileRepetitionObserverAction()
    {
        if ((selectedFileAction != null) && (selectedFileAction.count() > 0))
        {
            // should only be a single key and value in the dictionary
            SiteFile theFile = (SiteFile) selectedFileAction.allKeys().lastObject();
            String actionForCurrentSection = (String) selectedFileAction.objectForKey(theFile);
            selectedFileAction = new NSMutableDictionary();

            if ("Edit Properties".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(modifyFile(theFile));
                return null;
            }
            else if ("Replace File".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(replaceFile(theFile));
                return null;
            }
            else if ("Email Link To File".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(sendEmail(theFile));
                return null;
            }
            else if ("Delete".equals(actionForCurrentSection))
            {
                fileToDelete = theFile;
                AjaxModalDialog.open(context(), "ConfirmDeleteFileModalDialog");
            }

            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteSectionsPane", context());
        }

        return context().page();
    }



    public String editInPlaceID()
    {
        return "editFolderName" + aFolderIndex;
    }


    public String editInPlaceEdit()
    {
        return "editFolderName" + aFolderIndex + "Edit()";
    }


    public String editInPlaceSave()
    {
        return "editFolderName" + aFolderIndex + "Save()";
    }


    public String editInPlaceCancel()
    {
        return "editFolderName" + aFolderIndex + "Cancel()";
    }


    public String editInPlaceTextFieldID()
    {
        return "editFolderNameTextField" + aFolderIndex;
    }



    /* Generic setters and getters ***************************************/


    public EOEditingContext editingContext()
    {
        // Create this on demand and our super's constructor causes setWebsite to be called and it needs this.
        if (editingContext == null)
        {
            editingContext = ((Session) session()).newEditingContext();
        }

        return editingContext;
    }



    public SiteFile fileToDelete()
    {
        return fileToDelete;
    }


    public void setFileToDelete(SiteFile value)
    {
        fileToDelete = value;
    }



    public Website website()
    {
        return website;
    }


    public void setWebsite(Website value)
    {
        website = (Website) EOUtilities.localInstanceOfObject(editingContext(), value);
    }



    public String sortOn()
    {
        return sortOn;
    }


    public void setSortOn(String sort)
    {
        sortOn = sort;
    }


    public NSSelector sortOrder()
    {
        return sortOrder;
    }


    public void setSortOrder(NSSelector order)
    {
        sortOrder = order;
    }



    public String twistFolderHyperlinkOnLoadingScript()
    {
        return "function () { var busyRow = document.getElementById('" + busyRowID() + "'); busyRow.style.display=''; }";
    }


    public String twistFolderHyperlinkOnCompleteScript()
    {
        return "function () { var busyRow = document.getElementById('" + busyRowID() + "'); busyRow.style.display='none'; }";
    }


    public String busyRowID()
    {
        return "busyRow" + (aFolderIndex + 1);
    }



}
