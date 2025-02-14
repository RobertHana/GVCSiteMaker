// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.SiteFileFolder;
import com.gvcsitemaker.core.Website;

import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;


/**
 * This is a controller for the type of view implemented by the FileMgmt page.
 * It manages the state of a set of folders and, with SiteFileFolderProxy, the
 * state of the files in those folders.
 */
public class FolderController
{
    public static final String NAME_SORT = "uploadedFilename";
    public static final String SIZE_SORT = "fileSizeUsage";
    public static final NSArray VALID_SORT_COLUMNS = new NSArray(new Object[] {NAME_SORT, SIZE_SORT});
    public static final NSArray VALID_SORT_ORDERS = new NSArray(new Object[] {EOSortOrdering.CompareAscending, EOSortOrdering.CompareDescending});

    protected NSMutableArray orderedFolders;
    protected String sortOn = NAME_SORT;
    protected NSSelector sortOrder = EOSortOrdering.CompareAscending;
    protected Website website;
    protected SiteFileFolderProxy currentFolder;


    /**
     * Designated constructor.
     * 
     * @param aWebsite the Website whose folder we act as a controller for
     */
    public FolderController(Website aWebsite)
    {
        super();
        /** require [valid_website] aWebsite != null;   **/
        website = aWebsite;

        // Initialize the list of folders and open the first one by default
        orderedFolders = new NSMutableArray();
        synchFolders();
        ((SiteFileFolderProxy) orderedFolders().objectAtIndex(0)).open();
    }



    /**
     * Sets the selection mark for all files in all open folders.
     */
    public void selectAllVisibleFiles()
    {
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy aFolder = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            if (aFolder.isOpen())
            {
                aFolder.selectAllFiles();
            }
        }
    }



    /**
     * Clears the selection mark for all files in all open folders.
     */
    public void unselectAllVisibleFiles()
    {
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy aFolder = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            if (aFolder.isOpen())
            {
                aFolder.unselectAllFiles();
            }
        }
    }



    /**
     * Return the marked files in all folders.  As closed folders are not permitted to have marked files
     * this effectively means all marked files in open folders.
     * 
     * @return the marked files in all folders
     */
    public NSArray selectedFiles()
    {
        NSMutableArray selectedFiles = new NSMutableArray();
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy aFolder = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            if (aFolder.isOpen())
            {
                selectedFiles.addObjectsFromArray(aFolder.selectedFiles());
            }
        }
        return selectedFiles;
    }



    /**
     * Sorts the files in all folders based on sortColumn.  If sorting is already on this column, the
     * sort order (ascending or descending) is reversed.
     * 
     * @param sortColumn the column to sort on, must be in VALID_SORT_COLUMNS
     */
    public void sortOn(String sortColumn)
    {
        /** require [valid_sort_colum] (sortColumn != null) && VALID_SORT_COLUMNS.containsObject(sortColumn);  **/

        if (sortOn().equals(sortColumn))
        {
            setSortOrder(isAscendingSort() ? EOSortOrdering.CompareDescending : EOSortOrdering.CompareAscending);
        }
        else
        {
            setSortOn(sortColumn);
            setSortOrder(EOSortOrdering.CompareAscending);
        }

        // Inform the folders we manage that their sort order has changed
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy folderProxy = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            folderProxy.sortOn(sortOn(), sortOrder());
        }

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
        /** require [valid_sort_colum] (sortColumn != null) && VALID_SORT_COLUMNS.containsObject(sortColumn);  **/
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
     * Synchronizes the list of folders that this controller is maintaining with the list in the
     * Website it is associated with.  This handles folders being added, removed, and renamed.  The
     * controller does not attempt to determine changes by watching for notifications.  This method
     * needs to be called externally when the above changes take place.
     */
    protected void synchFolders()
    {
        NSArray websiteFolders = website.orderedUserFolders();

        // The folders are in synch if the controller has the same number as the website and each 
        // folder in the same position has the same name.
        boolean foldersInSynch = orderedFolders().count() == websiteFolders.count();
        if (foldersInSynch)
        {
            for (int i = 0; foldersInSynch && (i < websiteFolders.count()); i++)
            {
                SiteFileFolder aFolder = (SiteFileFolder) websiteFolders.objectAtIndex(i);
                foldersInSynch = ((SiteFileFolderProxy) orderedFolders().objectAtIndex(i)).folder().equals(aFolder);
            }
        }

        if (!foldersInSynch)
        {
            // To save time we don't want to recreate a SiteFileFolderProxy that already exists.  If
            // a proxy already exists, it is used.  New proxies are only created if a new folder has 
            // been created.

            // foldersByGlobalID() will not include a proxy for a folder that has been deleted.  
            NSDictionary foldersByGlobalID = foldersByGlobalID();

            orderedFolders = new NSMutableArray();
            for (int i = 0; i < websiteFolders.count(); i++)
            {
                SiteFileFolder aFolder = (SiteFileFolder) websiteFolders.objectAtIndex(i);
                SiteFileFolderProxy aFolderProxy = (SiteFileFolderProxy) foldersByGlobalID.objectForKey(aFolder.globalID());
                if (aFolderProxy != null)
                {
                    orderedFolders.addObject(aFolderProxy);
                }
                else
                {
                    orderedFolders.addObject(new SiteFileFolderProxy(aFolder, sortOn, sortOrder));
                }
            }
        }
    }



    /**
     * Informs the SiteFileFolderProxies that that this controller is maintaining that they may need
     * to synchronize their lists of files.  This handles files being added, removed, moved between
     * folders, and updated.  The controller does not attempt to determine changes by watching for 
     * notifications.  This method needs to be called externally when the above changes take place.
     */
    public void synchFiles()
    {
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy aFolder = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            aFolder.synchFiles();
        }
    }



    /**
     * Returns a dictionary of the SiteFileFolderProxy objects that we are managing, keyed on the globalID
     * of the SiteFileFolder it is a proxy for.  If a proxy's folder has no globalID (it has been deleted)
     * then the proxy is not included in this dictionary. 
     * 
     * @return a dictionary of the SiteFileFolderProxy objects that we are managing, keyed on the globalID
     * of the SiteFileFolder it is a proxy for
     */
    protected NSDictionary foldersByGlobalID()
    {
        NSMutableDictionary foldersByGlobalID = new NSMutableDictionary(orderedFolders().count());
        for (int i = 0; i < orderedFolders().count(); i++)
        {
            SiteFileFolderProxy aFolder = (SiteFileFolderProxy) orderedFolders().objectAtIndex(i);
            if (aFolder.folder().editingContext() != null)
            {
                foldersByGlobalID.setObjectForKey(aFolder, aFolder.folder().globalID());
            }
        }
        return foldersByGlobalID;
    }



    /* Generic setters and getters ***************************************/


    /**
     * This is intended to used with the item binding in a WORepetition
     */
    public SiteFileFolderProxy currentFolder()
    {
        return currentFolder;
    }


    /**
     * This is intended to used with the item binding in a WORepetition
     */
    public void setCurrentFolder(SiteFileFolderProxy aFolder)
    {
        this.currentFolder = aFolder;
    }


    public String sortOn()
    {
        return sortOn;
    }


    public void setSortOn(String sort)
    {
        this.sortOn = sort;
    }


    public NSSelector sortOrder()
    {
        return sortOrder;
    }


    public void setSortOrder(NSSelector order)
    {
        this.sortOrder = order;
    }


    /**
     * This is intended to used with the list binding in a WORepetition
     */
    public NSArray orderedFolders()
    {
        return orderedFolders;
    }


    protected Website website()
    {
        return website;
    }

}
