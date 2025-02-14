// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.core.SiteFileFolder;

import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;



/**
 * This is a proxy/controller for a SiteFileFolder on the type of view implemented by the FileMgmt page.  
 * It works in co-ordination with FolderController and manages the state of the files in a folder.
 */
public class SiteFileFolderProxy {
    
    protected boolean isOpen;
    protected SiteFileFolder folder;
    protected SiteFile currentFile;
    protected NSMutableArray orderedFiles;
    protected NSMutableSet selectedFiles;
    protected EOSortOrdering sortOrdering;
    protected boolean needToSort;

        
    
    /**
     * Designated constructor.  Creates an closed folder proxy.
     * 
     * @param aFolder the SiteFileFolder to act as a proxy for
     * @param sortOnColumn the column to sort on, from FolderController.VALID_SORT_COLUMNS.
     * @param sortOrder the direction to sort in, from FolderController.VALID_SORT_ORDERS
     */
    public SiteFileFolderProxy(SiteFileFolder aFolder, String sortOnColumn, NSSelector sortOrder) {
        super();
        /** require [valid_folder] aFolder != null;
                    [valid_sort_colum] (sortOnColumn != null) && FolderController.VALID_SORT_COLUMNS.containsObject(sortOnColumn);  
                    [valid_sort_order] (sortOrder != null) && FolderController.VALID_SORT_ORDERS.containsObject(sortOrder);  **/
        folder = aFolder;
        orderedFiles = null;
        selectedFiles = new NSMutableSet();
        isOpen = false;  
        sortOn(sortOnColumn, sortOrder);
        
        /** ensure [folder_set] folder() != null;
                   [no_files_selected] ! hasSelectedFiles();
                   [sort_column_set] sortOrdering().key().equals(sortOnColumn);
                   [sort_order_set] sortOrdering().selector().equals(sortOrder);                **/
    }
    
    
    
    /**
     * Sets the selection mark for all files in this folder.
     */
    public void selectAllFiles()
    {
        /** require [is_open] isOpen();    **/
        selectedFilesSet().addObjectsFromArray(orderedFiles());
        /** ensure [all_files_selected] selectedFiles().count() == orderedFiles().count();  **/
    }
    
    

    /**
     * Clears the selection mark for all files in this folder.
     */
    public void unselectAllFiles()
    {
        /** require [is_open] isOpen();    **/
        selectedFilesSet().removeAllObjects();
        /** ensure [no_files_selected] ! hasSelectedFiles();  **/
    }

    
    
    /**
     * @return the marked files in this folder
     */
    public NSArray selectedFiles()
    {
        return selectedFilesSet().allObjects();
    }
    
    
    
    /**
     * @return <code>true</code> if there are any selected files in this folder
     */
    public boolean hasSelectedFiles()
    {
        return selectedFilesSet().count() > 0;
    }
    
    
    
    /**
     * @return <code>true</code> if currentFile() is in selectedFiles()
     */
    public boolean isFileSelected() 
    {
        /** require [has_current_file] currentFile() != null;  **/
        return selectedFilesSet().containsObject(currentFile());
    }
    
    
    
    /**
     * Sets or clears the selection mark for currentFile().
     * 
     * @param newIsFileSelected <code>true</code> if currentFile should be selected
     */
    public void setIsFileSelected(boolean newIsFileSelected) 
    {
        /** require [has_current_file] currentFile() != null;   **/
        if (newIsFileSelected)
        {
            selectedFilesSet().addObject(currentFile());
        }
        else
        {
            selectedFilesSet().removeObject(currentFile());
        }
        /** ensure [selection_set] isFileSelected() == newIsFileSelected; **/
    }
    
    
    
    /**
     * Toggles the selection mark on this Folder between open and closed.  If the folder is closed,
     * selection marks from all files are cleared.
     */
    public void toggleState()
    {
        if (isOpen())
        {
            close();
        }
        else
        {
            open();
        }
        
        /** ensure [no_selected_files_in_closed_folders] isOpen() || ! hasSelectedFiles();    **/
    }


    
    /**
     * @return <code>true</code> if this folder is marked as open
     */
    public boolean isOpen()
    {
        return isOpen;
    }
    

    
    /**
     * Marks this folder as open.
     */
    public void open()
    {
        isOpen = true;
        // Creation of the file list and sorting is deferred until the folder is opened for performance reasons
        sortIfNeeded();
        /** ensure [folder_open] isOpen(); **/
    }
    


    /**
     * Marks this folder as closed.  Clears selection marks from all files.
     */
    public void close()
    {
        unselectAllFiles();
        isOpen = false;
        /** ensure [is_not_open] ! isOpen();
                   [no_selected_files_in_closed_folders] ! hasSelectedFiles();    **/

    }

    
    
    /**
     * Sets the column to sort on and the sort direction.
     * 
     * @param sortOnColumn the column to sort on
     * @param sortOrder the sort direction
     */
    public void sortOn(String sortOnColumn, NSSelector sortOrder)
    {
        /** require [valid_sort_colum] (sortOnColumn != null) && FolderController.VALID_SORT_COLUMNS.containsObject(sortOnColumn);  
                    [valid_sort_order] (sortOrder != null) && FolderController.VALID_SORT_ORDERS.containsObject(sortOrder);  **/

        sortOrdering = new EOSortOrdering(sortOnColumn, sortOrder);
        setNeedToSort(true);
        // Creation of the file list and sorting is deferred until the folder is opened for performance reasons
        sortIfNeeded();        

        /** ensure [sort_column_set] sortOrdering().key().equals(sortOnColumn);
                   [sort_order_set] sortOrdering().selector().equals(sortOrder);                **/
    }

    
    
    /**
     * Creation of the file list and sorting is deferred until the folder is opened for performance 
     * reasons.  The file list is only sorted if this folder is open and if the needToSort() flag
     * is set.
     */
    protected void sortIfNeeded()
    {
        if (isOpen() && needToSort())
        {
            EOSortOrdering.sortArrayUsingKeyOrderArray(orderedFiles(), new NSArray(sortOrdering));
            setNeedToSort(false);
        }
    }
    
    
    
    /**
     * Synchronizes the list of files in this proxy with that in the folder it is a proxy for.
     * This handles files being added, removed, moved between folders, and updated.  This proxy 
     * does not attempt to determine changes by watching for notifications.  This method needs to be 
     * called externally when the above changes take place.
     */
    public void synchFiles()
    {
        // There is nothing to do if the list has not been created
        if (orderedFiles == null)
        {
            return;
        }
       
        
        if (orderedFiles().count() != folder().files().count())
        {
            orderedFiles = null;
            setNeedToSort(true);
            
            // Ensure that we don't reatin any selection marks for files that have been removed/moved from this folder
            if (isOpen())
            {
                selectedFilesSet().intersectSet(new NSSet(orderedFiles()));
            }
        }
        else
        {
            // At present, only the file size can change during replacement, so as an optimization
            // we only trigger a resort if file size is the selected sort
            setNeedToSort(sortOrdering().key().equals(FolderController.SIZE_SORT));
        }
        
        sortIfNeeded();
    }
    
    
    
    /* Generic setters and getters ***************************************/

    public NSMutableArray orderedFiles() {
        // Creation of this list is deferred for performance reasons
        if (orderedFiles == null)
        {
            orderedFiles = new NSMutableArray(folder().files());
        }
        return orderedFiles;
    }

    public SiteFile currentFile() {
        return currentFile;
    }
    public void setCurrentFile(SiteFile aFile) {
        this.currentFile = aFile;
    }

    public NSMutableSet selectedFilesSet()
    {
        return selectedFiles;
    }

    public SiteFileFolder folder() {
        return folder;
    }
    
    
    protected EOSortOrdering sortOrdering() {
        return sortOrdering;
    }
    
    protected boolean needToSort() {
        return needToSort;
    }
    public void setNeedToSort(boolean newValue) {
        needToSort = newValue;
    }
    
}
