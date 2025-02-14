/**
 * Implementation of SiteFileFolder common to all installations. // Copyright
 * (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109
 * USA All rights reserved. // This software is published under the terms of the
 * Educational Community License (ECL) version 1.0, // a copy of which has been
 * included with this distribution in the LICENSE.TXT file.
 * 
 * @version $REVISION$
 */
package com.gvcsitemaker.core;


import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;



/*
 * * A SiteFileFolder is used to organize SiteFiles which have been uploaded to
 * a Website in GVC.SiteMaker. A SiteFile can only be associated with a single
 * SiteFileFolder. SiteFileFolders have parents and children so that they can be
 * organised into a hierarchy.
 */
public class SiteFileFolder extends _SiteFileFolder
{

    /**
     * Instance of NameComparator to be used when sorting SiteFileFolder.
     */
    static final public NSComparator NameComparator = new NameComparator();



    /**
     * Factory method to create new instances of SiteFileFolder.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SiteFileFolder or a subclass.
     */
    public static SiteFileFolder newSiteFileFolder()
    {
        return (SiteFileFolder) SMEOUtils.newInstanceOf("SiteFileFolder");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Creates a new SiteFileFolder named folderName.  If parentFolder is not null the new folder is 
     * made a child of this folder.  SiteFileFolders are not allowed to contain two folders with the same name.
     *
     * @param parentFolder - null if this is to be a root level folder, or the direct parent folder 
     * if this is a child folder
     * @param folderName - the name of the folder to be created
     * @return the newly created SiteFileFolder 
     */
    public static SiteFileFolder createFolder(EOEditingContext ec, SiteFileFolder parentFolder, String folderName)
    {
        /** require [valid_editingContext] ec != null;  [folder_name_not_null] folderName != null;  **/

        JassAdditions.pre("SiteFileFolder", "createFolder()", (parentFolder == null) || (parentFolder.editingContext().equals(ec)));
        JassAdditions.pre("SiteFileFolder", "createFolder()", (parentFolder == null) || (!parentFolder.containsFolderNamed(folderName)));

        SiteFileFolder newFolder = newSiteFileFolder();
        ec.insertObject(newFolder);
        newFolder.setName(folderName);

        if (parentFolder != null)
        {
            parentFolder.addObjectToBothSidesOfRelationshipWithKey(newFolder, "childFolders");
        }

        JassAdditions.post("SiteFileFolder", "createFolder()", newFolder.name().equals(folderName));
        JassAdditions.post("SiteFileFolder", "createFolder()", newFolder.editingContext().equals(ec));
        JassAdditions.post("SiteFileFolder", "createFolder()", newFolder.parentFolder() == parentFolder);

        return newFolder;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Creates a new child folder named folderName.  SiteFileFolders are not allowed to contain two folders with the same name.
     *
     * @param folderName - the name of the folder to be created.
     * @return the newly created SiteFileFolder 
     */
    public SiteFileFolder createFolder(String folderName)
    {
        /** require [folder_name_null_or_not_used] (folderName == null) || ! containsFolderNamed(folderName);  **/

        SiteFileFolder newFolder = newSiteFileFolder();
        editingContext().insertObject(newFolder);
        newFolder.setName(folderName);

        addObjectToBothSidesOfRelationshipWithKey(newFolder, "childFolders");

        return newFolder;

        /** ensure [result_not_null] Result != null; 
                   [name_set] (folderName == null) || Result.name().equals(folderName);
                   [parent_set] Result.parentFolder() == this;         **/
    }



    /**
     * Searches the child folders, their child folders, etc. for a SiteFileFolder named aName.  Returns
     * the folder or null if there is no matching folder.  Folder names are not case sensitive.
     *
     * @param aName - the name of the folder to search for
     * @return the child SiteFileFolder named aName or null if there is no matching folder.
     */
    public SiteFileFolder folderNamed(String aName)
    {
        /** require [a_name_not_null] aName != null; **/

        // Handle finding ourself
        if (aName.equalsIgnoreCase(name()))
        {
            return this;
        }

        SiteFileFolder folder = null;

        Enumeration folderEnumerator = childFolders().objectEnumerator();
        while (folderEnumerator.hasMoreElements() && (folder == null))
        {
            SiteFileFolder aFolder = (SiteFileFolder) folderEnumerator.nextElement();
            if (aName.equalsIgnoreCase(aFolder.name()))
            {
                folder = aFolder;
            }
            else
            {
                // Check the child's children too
                folder = aFolder.folderNamed(aName);
            }
        }

        return folder;
    }



    /**
     * Searches the child folders, their child folders, etc. for a SiteFileFolder named aName.  Returns
     * true if one is found, or false if there is no matching folder.  Folder names are not case sensitive.
     * 
     * @param aName - the name of the folder to search for
     * @return true if this folder directly contains a folder named aName.
     */
    public boolean containsFolderNamed(String aName)
    {
        /** require [a_name_not_null] aName != null; **/

        return folderNamed(aName) != null;
    }



    /**
     * Returns a file name that has not been used before.
     *
     * @return a file name that has not been used before
     */
    public String newFolderName()
    {
        for (int i = 0; ; i++)
        {
            String aFolderName = "Untitled Folder " + i;
            if ( ! containsFolderNamed(aFolderName))
            {
                return aFolderName;
            }
        }

        /** ensure [valid_new_group] (Result != null); **/
    }



    /**
     * Returns the SiteFile with this name, or null if it can not be found.  Both files uploaded
     * directly to this folder and files uploaded to any folder below it are looked at.
     * 
     * @param aFilename the normalized filename to look for
     * @return the corresponding file or null if it is not found
     */
    public SiteFile fileForFilename(String aFilename)
    {
        /** require [valid_filename] aFilename != null;
                    [normalized_filename] aFilename.equals(URLUtils.toLowerAndNormalize(aFilename));   **/


        SiteFile theFile = null;
        Enumeration fileEnum = files().objectEnumerator();
        while ((theFile == null) && fileEnum.hasMoreElements())
        {
            SiteFile aFile = (SiteFile) fileEnum.nextElement();
            if (aFile.uploadedFilename().equalsIgnoreCase(aFilename))
            {
                theFile = aFile;
            }
        }

        Enumeration folderEnum = childFolders().objectEnumerator();
        while ((theFile == null) && folderEnum.hasMoreElements())
        {
            SiteFileFolder aFolder = (SiteFileFolder) folderEnum.nextElement();
            theFile = aFolder.fileForFilename(aFilename);
        }

        return theFile;
    }



    /**
     * Returns <code>true</code> if the SiteFile is directly in this folder or any folder below it
     * 
     * @param aFile the SiteFile to find
     * @return <code>true</code> if the SiteFile is directly in this folder or any folder below it
     */
    public boolean containsFile(SiteFile aFile)
    {
        /** require [valid_file] aFile != null;
                    [same_ec] aFile.editingContext().equals(editingContext());   **/

        boolean containsFile = files().containsObject(aFile);

        Enumeration folderEnum = childFolders().objectEnumerator();
        while ((!containsFile) && folderEnum.hasMoreElements())
        {
            SiteFileFolder aFolder = (SiteFileFolder) folderEnum.nextElement();
            containsFile = aFolder.containsFile(aFile);
        }

        return containsFile;
    }


    /**
     * Returns the amount of file system space, in bytes, used by SiteFiles directly in this folder.
     *
     * @return the amount of file system space, in bytes, used by SiteFiles directly in this folder.
     */
    public Long fileSizeUsage()
    {
        Enumeration fileEnumerator = files().objectEnumerator();
        long totalSize = 0;

        while (fileEnumerator.hasMoreElements())
        {
            SiteFile aFile = (SiteFile) fileEnumerator.nextElement();
            totalSize += aFile.fileSizeUsage().longValue();
        }

        return new Long(totalSize);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the approximate amount of file system space, in megabytes, used by SiteFiles in this folder.  For example, a return value 5.5 indicates 5.5 MB of the quota has been used.
     *
     * @return the approximate amount of file system space, in megabytes, used by SiteFiles in this folder.
     */
    public float fileSizeUsageInMegabytes()
    {
        return SMFileUtils.bytesToMegaBytes(fileSizeUsage());

        /** ensure [result_greater_then_zero] Result >= 0.0; **/
    }



    /**
     * Returns the amount of file system space, in bytes, used by SiteFiles in this folder and in all folders below it in the hierarchy.
     *
     * @return the amount of file system space, in bytes, used by SiteFiles in this folder and in all folders below it in the hierarchy.
     */
    public Long totalFileSizeUsage()
    {
        Enumeration fileEnumerator = allFiles().objectEnumerator();
        long totalSize = 0;

        while (fileEnumerator.hasMoreElements())
        {
            SiteFile aFile = (SiteFile) fileEnumerator.nextElement();
            totalSize += aFile.fileSizeUsage().longValue();
        }

        return new Long(totalSize);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the approximate amount of file system space, in megabytes, used by SiteFiles in this folder and in all folders below it in the hierarchy.  For example, a return value 5.5 indicates 5.5 MB of the quota has been used.
     *
     * @return the approximate amount of file system space, in megabytes, used by SiteFiles in this folder and in all folders below it in the hierarchy.
     */
    public float totalFileSizeUsageInMegabytes()
    {
        return SMFileUtils.bytesToMegaBytes(totalFileSizeUsage());

        /** ensure [result_greater_then_zero] Result >= 0.0; **/
    }



    /**
     * Returns childFolders() sorted by the folder name.
     */
    public NSArray orderedFolders()
    {
        // Might want this to return the children of children too.  How to sort?
        return NSArrayAdditions.sortedArrayUsingComparator(childFolders(), SiteFileFolder.NameComparator);

        /** ensure
        [result_not_null] Result != null;
        [correct_number_of_folders] Result.count() == childFolders().count(); **/
    }



    /**
     * Returns the number of SiteFiles in this folder.
     *
     * @return the number of SiteFiles in this folder.
     */
    public NSArray orderedFiles()
    {
        return NSArrayAdditions.sortedArrayUsingComparator(files(), SiteFile.NameComparator);
    }



    /**
     * Returns the number of SiteFiles in this folder and in all folders below it in the hierarchy.
     *
     * @return the number of SiteFiles in this folder and in all folders below it in the hierarchy
     */
    public int numberOfFiles()
    {
        return allFiles().count();

        /** ensure [number_of_files_is_positive_integer] files().count() >= 0; **/
    }



    /**
     * Returns all SiteFiles in this folder and in all folders below it in the hierarchy.
     *
     * @return all SiteFiles in this folder and in all folders below it in the hierarchy.
     */
    public NSArray allFiles()
    {
        NSMutableArray allFiles = new NSMutableArray(files());
        Enumeration folderEnumerator = childFolders().objectEnumerator();
        while (folderEnumerator.hasMoreElements())
        {
            SiteFileFolder aFolder = (SiteFileFolder) folderEnumerator.nextElement();
            allFiles.addObjectsFromArray(aFolder.allFiles());
        }

        return allFiles;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the number of SiteFiles in this folder and in all folders below it in the hierarchy.
     *
     * @return the number of SiteFiles in this folder and in all folders below it in the hierarchy.
     */
    public int totalNumberOfFiles()
    {
        Enumeration folderEnumerator = childFolders().objectEnumerator();
        int totalNumberOfFiles = numberOfFiles();

        while (folderEnumerator.hasMoreElements())
        {
            SiteFileFolder aFolder = (SiteFileFolder) folderEnumerator.nextElement();
            totalNumberOfFiles += aFolder.totalNumberOfFiles();
        }

        return totalNumberOfFiles;

        /** ensure [result_greater_than_or_equal_to_zero] Result >= 0; **/
    }



    /**
    * Returns true if this folder is a root folder.  A root folder has no parent folder and is the root of a folder hierarchy.
     *
     * @return true if this folder is a root folder.
     */
    public boolean isRootFolder()
    {
        return parentFolder() == null;
    }



    /**
     * Returns true if this folder can be deleted.  For now, only empty, non-root folders can be deleted.
      *
      * @return true if this folder can be deleted
      */
    public boolean canDelete()
    {
        return !isRootFolder() && (childFolders().count() == 0) && (files().count() == 0);
    }



    /**
     * Returns true if this folder can be renamed.  For now, only non-root folders can be renamed.
      *
      * @return true if this folder can be renamed
      */
    public boolean canRename()
    {
        return !isRootFolder();
    }



    /**
     * Validation method to prevent duplication of folder names within a website.
     * 
     * @param newName
     * @return
     * @throws NSValidation.ValidationException
     */
    public Object validateName(Object newName) throws NSValidation.ValidationException
    {
        if (newName == null)
        {
            throw new NSValidation.ValidationException("This folder name is required.");
        }

        if (parentFolder() != null)
        {
            SiteFileFolder existingFolder = parentFolder().folderNamed((String) newName);
            if ((existingFolder != null) && (existingFolder != this))
            {
                // Not quite the right explanation, but as only one folder in a site is allowed to have
                // children for the time being, it will serve its purpose.
                throw new NSValidation.ValidationException("This website already has a folder named " + newName + ".");
            }
        }

        return newName;
    }



    /**
     * Simple Comparator to sort SiteFileFolders by name, ignoring the hierarchy.
     */
    static protected class NameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return ((SiteFileFolder) object1).name().compareToIgnoreCase(((SiteFileFolder) object2).name());
        }
    }
}
