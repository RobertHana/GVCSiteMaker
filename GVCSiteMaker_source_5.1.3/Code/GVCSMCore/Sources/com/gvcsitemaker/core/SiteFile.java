// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core;

import java.io.*;
import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;
import net.global_village.foundation.Date;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


public class SiteFile extends _SiteFile implements SMAccessible, EditingContextNotification, RTEListSelectable
{

    // Return this in place of the URL for a file which has been deleted
    public static final String MISSING_FILE_URL = "/Linked_File_Has_Been_Deleted";


    /**
     * Instance of NameComparator to be used when sorting SiteFiles.
     */
    static final public NSComparator NameComparator = new NameComparator();


    /**
     * Instance of ListDisplayNameComparator to be used when sorting SiteFiles by listDisplayName.
     */
    static final public NSComparator ListDisplayNameComparator = new ListDisplayNameComparator();

    /**
    * Transient data for uploading, copying and duplicating
    */
    protected String newDataPath = null;
    protected WOFileUploadBindings newUploadBindings;
    protected NSData newData;



    /**
     * Factory method to create new instances of SiteFile.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SiteFile or a subclass.
     */
    public static SiteFile newSiteFile()
    {
        return (SiteFile) SMEOUtils.newInstanceOf("SiteFile");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Sets default values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        this.setUploadDate(Date.now());
        setMimeType(MIMEUtils.DEFAULT_MIMETYPE);
        if (fileSizeUsage() == null)
        {
            setFileSizeUsage(new Long(0));
        }

        // Generate the PK so we can create file names.
        generateFileName();

        allowAccessForGroup(PublicGroup.group(ec));

        /** ensure
        [is_public] isPublic();
        [upload_date_not_null] uploadDate() != null;
        [mime_type_not_null] mimeType() != null;
        [file_size_usage_not_null] fileSizeUsage() != null;
        [file_name_not_null] filename() != null; **/
    }



    /**
     * Overridden to clear uploaded data when refaulted.
     */
    public void awakeFromFetch(EOEditingContext ec)
    {
        super.awakeFromFetch(ec);
        // BUG ??? Is this really what we should do in this case?  The edit in a foreign EC has not
        // invalidated *our* upload.  If this is what we want to do then do we also need to check for
        // the object being a fault in hasNewData()?
        clearNewData();
    }



    /**
     * Overridden to clear uploaded data when editing context reverted.  Although not documented, this method is what gets called.
     */
    public void updateFromSnapshot(NSDictionary snapShot)
    {
        super.updateFromSnapshot(snapShot);
        clearNewData();
    }



    /**
     * Returns a copy of this object.  Each EOEnterpriseObject can override this this to produce the actual copy by an appropriate mechanism (reference, shallow, deep, or custom).  The default is a deep copy.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        SiteFile copy = (SiteFile) super.duplicate(copiedObjects);

        // Clear the copied Primary Key based filename and setup to copy the file data when this new SiteFile is saved (depends on use with NotifyingEditingContext);
        copy.setFilename(null);
        copy.generateFileName();
        copy.setData(filePath());

        return copy;
        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Returns a duplicate of this SiteFile.  This is used when copying a VirtualSiteFileField.
     *
     * @return a copy of this SiteFile.
     */
    public SiteFile duplicate()
    {

        DebugOut.println(1, "Duplicating...");

        SiteFile newFile = (SiteFile) EOCopying.Utility.shallowCopy(this);

        // Clear the copied Primary Key based filename and setup to copy the file data when this new SiteFile is saved (depends on use with NotifyingEditingContext);
        newFile.setFilename(null);
        newFile.generateFileName();
        newFile.setData(filePath());

        return newFile;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Sets the values for a SiteFile from WOFileUploadBindings.
     * 
     * @param uploadBindings the values to initialize this SiteFile with
     */
    public void initializeFromUpload(WOFileUploadBindings uploadBindings)
    {
        /** require
        [uploadBindings_not_null] uploadBindings != null;
        [valid_groups] (uploadBindings.groups() != null);
        [valid_folder] (uploadBindings.folder() != null);
        [file_was_uploaded] uploadBindings.wasFileUploaded(); 
        **/

        // Hold a reference to the bindings as they delete the temp files when finalized. If the garbage 
        // collector runs between now and when the save completes, the file data that we are to save 
        // will be deleted before we can copy it.  To allow cancel, we don't want to copy until then.
        newUploadBindings = uploadBindings;

        // This contract does not hold for files uploaded to DA tables:
        // (uploadBindings.groups().count() > 0);
        setUploadDate(Date.now());
        setMimeType(uploadBindings.mimeType());
        setUploadedFilename(uploadBindings.uploadedFileName());
        setData(uploadBindings.finalFilePath());
        setFileSizeUsage(new Long(uploadBindings.uploadedFile().length()));

        if (uploadBindings.groups().count() > 0)
        {

            // Update groups, disconnect existing
            NSArray groups = new NSArray(groups());
            for (int i = 0; i < groups.count(); i++)
            {
                Group aGroup = (Group) groups.objectAtIndex(i);
                removeObjectFromBothSidesOfRelationshipWithKey(aGroup, "groups");
            }

            // Update groups, connect new
            for (int i = 0; i < uploadBindings.groups().count(); i++)
            {
                Group aGroup = (Group) uploadBindings.groups().objectAtIndex(i);
                addObjectToBothSidesOfRelationshipWithKey(aGroup, "groups");
            }

        }

        // Update folder
        if (!uploadBindings.folder().equals(folder()))
        {
            if (folder() != null)
            {
                removeObjectFromBothSidesOfRelationshipWithKey(folder(), "folder");
            }

            addObjectToBothSidesOfRelationshipWithKey(uploadBindings.folder(), "folder");
        }
    }



    /**
     * Overridden to change value to the default MIME type if it is null.
     *
     * @param value - the MIME type of this SiteFile.
     */
    public void setMimeType(String value)
    {
        super.setMimeType((value == null) ? MIMEUtils.DEFAULT_MIMETYPE : value);

        /** ensure [mime_type_not_null] mimeType() != null; **/
    }



    /**
     * Returns <code>true</code> if this SiteFile is a recognized image type.
     *
     * @return <code>true</code> if this SiteFile is a recognized image type.
     */
    public boolean isImageFile()
    {
        /** require [valid_MIME_type] mimeType() != null;  **/
        return MIMEUtils.isImageMimeType(mimeType());
    }



    /**
     * Returns the contents of this SiteFile in an NSData object.  If there are unsaved changes to the file contents, these are returned.  If the file does not exist an empty NSData is returned and the error is recorded in the ErrorLog.  If the file cannot be read, an exception is raised.
     *
     * @return the contents of this SiteFile in an NSData object.
     */
    public NSData data()
    {
        /** require [file_name_not_null] filename() != null; **/

        NSData contentsOfFile = null;

        try
        {
            if (hasNewData())
            {
                DebugOut.println(1, "=== reading file: " + newDataPath);
                if (newDataPath != null)
                {
                    contentsOfFile = SMFileUtils.dataFromFile(new File(newDataPath));
                }
                else
                {
                    contentsOfFile = newData;
                }
            }
            else
            {
                DebugOut.println(1, "=== reading file: " + filePath());
                contentsOfFile = SMFileUtils.dataFromFile(new File(filePath()));
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            // Should this just be logged, or should it raise the exception?
            DebugOut.println(1, "=== FAILED! file does not exist at : " + filePath());
            contentsOfFile = NSData.EmptyData;
            RuntimeException fileNotFound = new RuntimeException("Can not find file, siteID = " + website().siteID() + ", name = " + uploadedFilename()
                    + ", PK = " + filename() + ", stored at " + filePath());
            ErrorLogger.logErrorLocation(editingContext(), null, fileNotFound, "Missing File");
        }
        catch (java.io.IOException e)
        {
            throw new ExceptionConverter(e);
        }

        return contentsOfFile;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Records sourceFilePath as the source for new data for this SiteFile, but does not copy it to disk.  This unsaved data <b>will</b> be returned from data().  The data will be written to disk after this SiteFile has been saved to disk.  This was added to supplement FileUpload.pl and to support Data Access Section uploads.
     *
     * @param sourceFilePath - the path to the file this SiteFile should hold after being saved.
     */
    public void setData(String sourceFilePath)
    {
        /** require [valid_param] sourceFilePath != null;  **/

        newDataPath = sourceFilePath;

        /** ensure [file_contents_set]  hasNewData();  **/
    }



    public void setData(NSData fileData)
    {
        /** require [valid_param] fileData != null;  **/

        newData = fileData;

        /** ensure [file_contents_set]  hasNewData();  **/
    }



    /**
     * Returns <code>true</code> if this SiteFile has new data (file contents) which have not yet been written to disk.
     *
     * @return <code>true</code> if this SiteFile has new data (file contents) which have not yet been written to disk.
     */
    public boolean hasNewData()
    {
        return (newDataPath != null) || (newData != null);
    }



    /**
     * Clears any new data for this SiteFile which has not been saved to disk.
     */
    protected void clearNewData()
    {
        newDataPath = null;
        newData = null;
        newUploadBindings = null;
        /** ensure [file_contents_cleared]  ! hasNewData();  **/
    }



    /**
     * Saves data() as the data for this SiteFile.
     */
    protected void writeFile()
    {
        /** require [valid_param] hasNewData();  **/

        if (filePath() == null)
        {
            generateFileName();
        }

        DebugOut.println(20, "Writing " + uploadedFilename() + " to " + filePath());


        if (newDataPath == null)
        {
            SMFileUtils.copyFile(data().stream(), filePath());
        }
        else
        {
            SMFileUtils.copyFile(newDataPath, filePath());
        }

        clearNewData();

        /** ensure [valid_param] ! hasNewData();  **/
    }



    /**
     * Tries to delete the physical file from the disk.  If this fails an error is recorded in the ErrorLog.
     */
    public void deleteFileFromDisk()
    {
        try
        {
            DebugOut.println(20, "Deleting file at path " + filePath());
            File file = new File(filePath());

            if (!file.delete())
            {
                throw new SecurityException("Failed to delete file");
            }
        }
        catch (SecurityException e)
        {
            DebugOut.println(1, "Unable to delete file from disk " + filePath());
            RuntimeException cantDeleteFile = new RuntimeException("Failed to delete file (" + e.getMessage() + "), name = " + uploadedFilename() + ", PK = "
                    + filename() + ", stored at " + filePath());
            EOEditingContext ec = new EOEditingContext();
            ec.lock();
            try
            {
                ErrorLogger.logErrorLocation(ec, null, cantDeleteFile, "Can't Delete File");
            }
            finally
            {
                ec.unlock();
                ec.dispose();
            }
        }
    }



    /**
     * Generates filename(), the unique name to store this SiteFile as in the file system.  It does nothing if this has already been generated.  At present filename() is just the String version of the primary key.  This has the side effect of generating the primary key for this SiteFile before it has been saved.
     */
    public void generateFileName()
    {
        if (filename() == null)
        {
            NSDictionary primaryKeyDict = SMEOUtils.primaryKeyDictionaryForNewObject(this);
            SMEOUtils.setPrimaryKeyForNewObject(primaryKeyDict, this);

            String filePK;
            if (SMApplication.smApplication().isUsingIntegerPKs())
            {
                filePK = primaryKeyDict.objectForKey("filePKey").toString();
            }
            else
            {
                filePK = ERXStringUtilities.byteArrayToHexString(((NSData)primaryKeyDict.objectForKey("filePKey")).bytes());
                filePK = filePK.replaceFirst("0+$", "");
            }
            setFilename(filePK);
        }

        /** ensure [file_name_not_null] filename() != null;  **/
    }



    /**
     * Returns the sub-directory under UploadDirectory (see GVCSiteMakerCustom.plist) and the filename within that sub-directory where this SiteFile is stored.  This file path fragement needs to be appended to UploadDirectory to form the full path.  At present, the format of filePathFragment is a two digit directory name, a forward slash, and a number of at least a single digit for the file name, e.g. 08/08 or 73/4273.
     *
     * @return the sub-directory under UploadDirectory (see GVCSiteMakerCustom.plist) and the filename within that sub-directory where this SiteFile is stored.
     */
    public String filePathFragment()
    {
        /** require
        [file_name_not_null] filename() != null;
        [file_name_contains_characters] filename().length() > 0; **/

        // public so that GVCSMUtility can access it
        String filePathFragment;

        // This will be handled by DBC when it is integrate with SiteMaker, but as this is such a serious problem this exception was placed here in the meantime.  -ch
        if ((filename() == null) || (filename().length() == 0))
        {
            throw new RuntimeException("filePathFragment() called with no filename() set");
        }

        // Use the rightmost two characters of the filename (aka the primary key) as the directory to store this file in (left zero paded if < 10 to maintain two digits).  This divides all files over 100 directories (00-99).  I have no idea why they did it like this, but changing it now will be a nightmare for upgrading existing sites.  - ch
        String subdir = "0" + filename();
        subdir = subdir.substring(subdir.length() - 2);

        // e.g. 08/08 or 73/4273
        filePathFragment = (subdir + "/" + filename());

        return filePathFragment;

        /** ensure
        [result_not_null] Result != null;
        [result_length_greater_than_three] Result.length() > 3;
        **/
    }



    /**
     * Returns the absolute path where this SiteFile is stored.
     *
     * @return the absolute path where this SiteFile is stored.
     */
    public String filePath()
    {
        /** require
        [file_name_not_null] filename() != null;
        [file_name_contains_characters] filename().length() > 0; **/

        return SMApplication.appProperties().stringPropertyForKey("UploadDirectory") + "/" + filePathFragment();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overidden to ensure that value is normalized and converted to lowercase.  This is done so that it can be used in a URL.
     */
    public void setUploadedFilename(String value)
    {
        super.setUploadedFilename(URLUtils.toLowerAndNormalize(value));
    }



    /**
     * Creates and returns a new FilePassword for this file.  The FilePassword is inserted into an
     * editing context, but not saved.
     *  
     * @return a new FilePassword for this file
     */
    public FilePassword newPassword()
    {
        FilePassword newFilePassword = FilePassword.newFilePassword();
        editingContext().insertObject(newFilePassword);
        newFilePassword.addObjectToBothSidesOfRelationshipWithKey(this, "siteFile");

        return newFilePassword;
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this SiteFile has a FilePassword which has not expired and which has the passed password.
     *
     * @param password - the FilePassword password string to match
     * @return <code>true</code> if this SiteFile has a FilePassword which has not expired and which has the passed password.
     */
    public boolean hasPassword(String password)
    {
        boolean hasPassword = false;

        Enumeration passwordEnum = passwords().objectEnumerator();

        while (passwordEnum.hasMoreElements())
        {
            FilePassword aFilePassword = (FilePassword) passwordEnum.nextElement();

            DebugOut.println(18, "=== Comparing password " + aFilePassword.password() + " with " + password);

            if (aFilePassword.password().equals(password))
            {
                DebugOut.println(18, "Passwords match, FilePassword expires: " + aFilePassword.expirationDate());

                // Make sure it is not expired
                if (!aFilePassword.expirationDate().before(new NSTimestamp()))
                {
                    hasPassword = true;
                    break;
                }
            }
        }

        DebugOut.println(18, "hasPassword? " + hasPassword);

        return hasPassword;
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if aUser is a member of an access Group protecting this SiteFile.
     *
     * @return <code>true</code> if aUser is a member of an access Group protecting this SiteFile
     */
    public boolean isViewableByUser(User aUser)
    {
        /** require
        [a_user_not_null] aUser != null;
        [editing_context_matches] aUser.editingContext().equals(editingContext()); **/

        boolean isViewableByUser = false;

        Enumeration groupEnum = groups().objectEnumerator();
        while ((!isViewableByUser) && groupEnum.hasMoreElements())
        {
            isViewableByUser = ((Group) groupEnum.nextElement()).isMember(aUser);
        }

        return isViewableByUser;
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if this SiteFile allows public access.
     *
     * @return <code>true</code> if this SiteFile allows public access.
     */
    public boolean isPublic()
    {
        boolean isFilePublic = false;

        Enumeration groupEnum = groups().objectEnumerator();
        while (groupEnum.hasMoreElements())
        {
            Group someGroup = (Group) groupEnum.nextElement();
            if (someGroup instanceof PublicGroup)
            {
                isFilePublic = true;
                break;
            }
        }

        return isFilePublic;
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if this SiteFile requires access to be via SSL.
     *
     * @return <code>true</code> if this SiteFile requires access to be via SSL
     */
    public boolean requiresSSLConnection()
    {
        return !isPublic();
    }



    /**
     * SMAccessible method.  Permits access to this resource to members of the group.
     * 
     * @param aGroup the Group to allow access to
     */
    public void allowAccessForGroup(Group newGroup)
    {
        /** require [valid_group] newGroup != null; 
                    [same_ec] editingContext() == newGroup.editingContext();
                    [not_in_group] ! groups().containsObject(newGroup);       
         **/

        Enumeration anEnum = new NSArray(groups()).objectEnumerator();
        addToGroups(newGroup);

        while (anEnum.hasMoreElements())
        {
            Group currentGroup = (Group) anEnum.nextElement();
            if ((newGroup instanceof PublicGroup) || (currentGroup instanceof PublicGroup))
            {
                revokeAccessForGroup(currentGroup);
            }
        }

        if (newGroup instanceof WebsiteGroup)
        {
            ((WebsiteGroup) newGroup).addToFiles(this);
        }

        /** ensure [access_allowed] groups().containsObject(newGroup);
                   [file_recorded] ( ! (newGroup instanceof WebsiteGroup)) || 
                                      ((WebsiteGroup)newGroup).files().containsObject(this);
         **/
    }



    /**
     * SMAccessible method.  Removes permission access to this resource by members of the group.
     * 
     * @param aGroup the Group to revoke access to
     */
    public void revokeAccessForGroup(Group aGroup)
    {
        /** require [valid_group] aGroup != null; 
                    [same_ec] editingContext() == aGroup.editingContext();      
                    [in_group] groups().containsObject(aGroup);            
                    [has_multiple_groups] groups().count() > 1;            
         **/

        removeFromGroups(aGroup);
        if (aGroup instanceof WebsiteGroup)
        {
            ((WebsiteGroup) aGroup).removeFromFiles(this);
        }

        /** ensure [access_not_allowed] ! groups().containsObject(aGroup);
                   [has_a_group] groups().count() > 0;
                   [file_disconnected] ( ! (aGroup instanceof WebsiteGroup)) || 
                                      ! ((WebsiteGroup)aGroup).files().containsObject(this);
         **/
    }



    /**
     * Returns the URL to access this SiteFile using the domain defined in 
     * GVCSiteMaker.plist as DomainName.  http:// or https:// is used as 
     * indicated by requiresSSLConnection().
     *
     * @return the URL to access this SiteFile using the domain defined in 
     * GVCSiteMaker.plist as DomainName  
     */
    public String url()
    {
        return SMActionURLFactory.fileURL(this.website().siteID(), uploadedFilename(), requiresSSLConnection());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to access this SiteFile using the domain specified in the 
     * request.  This is for GVC.SiteMaker Virtual Host support.  http:// or 
     * https:// is used as indicated by requiresSSLConnection().
     *
     * @return the URL to access this SiteFile using the domain specified in the 
     * request
     */
    public String url(WORequest aRequest)
    {
        return SMActionURLFactory.fileURL(this.website().siteID(), uploadedFilename(), requiresSSLConnection(), aRequest);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to access this SiteFile using the FilePassword specified.  This is returned
     * as an HTTP URL regardless of the access protection on this SiteFile.  Passwords trump access
     * protection.
     *
     * @return the URL to access this SiteFile using the FilePassword specified
     */
    public String url(FilePassword aFilePassword)
    {
        /** require [valid_password] (aFilePassword != null) && (aFilePassword.siteFile() == this);  **/

        return SMActionURLFactory.fileURL(this.website().siteID(), uploadedFilename(), false) + "/" + aFilePassword.password();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Sets the file size used by this SiteFile to value and tells the Website containing this SiteFile to updateFileSizeUsage().
     */
    public void setFileSizeUsage(Number value)
    {
        super.setFileSizeUsage(value);

        if (website() != null)
        {
            website().updateFileSizeUsage();
        }
    }



    /**
     * Returns the approximate file size of this file in kilobytes (KB)
     *
     * @return the approximate file size of this file in kilobytes (KB)
     */
    public double fileSizeInKilobytes()
    {
        /** require [file_size_usage_not_null] fileSizeUsage() != null; **/

        return SMFileUtils.bytesToKiloBytes(fileSizeUsage());
    }



    /**
     * Returns the approximate file size of this file in megabytes (MB)
     *
     * @return the approximate file size of this file in megabytes (MB)
     */
    public double fileSizeInMegabytes()
    {
        /** require [file_size_usage_not_null] fileSizeUsage() != null; **/

        return SMFileUtils.bytesToMegaBytes(fileSizeUsage());
    }



    /**
     * Simple Comparator to sort SiteFileFolders by name, ignoring the hierarchy.
     */
    static protected class NameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return ((SiteFile) object1).displayName().compareToIgnoreCase(((SiteFile) object2).displayName());
        }
    }



    /**
     * Comparator to sort SiteFiles first by folder alphabetically, then by filename.  The SiteFiles in the root folder are on top of the list
     */
    static protected class ListDisplayNameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result = NSComparator.OrderedSame;

            SiteFile firstFile = (SiteFile) object1;
            SiteFile secondFile = (SiteFile) object2;

            if (firstFile.folder().isRootFolder() && (!secondFile.folder().isRootFolder()))
            {
                result = NSComparator.OrderedAscending;
            }
            else if ((!firstFile.folder().isRootFolder()) && (secondFile.folder().isRootFolder()))
            {
                result = NSComparator.OrderedDescending;
            }
            else if (!(firstFile.folder().name().equals(secondFile.folder().name())))
            {
                result = firstFile.folder().name().compareToIgnoreCase(secondFile.folder().name());
            }
            else
            {
                result = firstFile.displayName().compareToIgnoreCase(secondFile.displayName());
            }

            return result;
        }
    }



    // EditingContextNotification methods

    /**
     * Writes uploaded file to disk after being saved to the persistent object store (a.k.a. database).
     */
    public void hasInserted()
    {
        DebugOut.println(20, "hasInserted " + uploadedFilename());
        if (hasNewData())
        {
            writeFile();
        }
    }



    /**
     * Called before deleteObject is invoked on super (EOEditingContext).  Notifies website on deletion so that website can update it quota usage statistics. Deletes all the QueuedTasks as there is no relationship to delete them automatically.
     */
    public void willDelete()
    {
        if (website() != null)
        {
            website().notifyFileDeleted(this);
        }
        else
        {
            // Ignore this, this happens when a site is deleted.
            // new RuntimeException("No Website on file deletion!").printStackTrace(DebugOut.out());
        }
    }



    /**
     * Deletes uploaded file from disk after being removed from the persistent object store (a.k.a. database).
     */
    public void hasDeleted()
    {
        DebugOut.println(20, "deleting file from disk");
        deleteFileFromDisk();
        clearNewData();
    }



    /**
     * Writes uploaded file to disk after being saved to the persistent object store (a.k.a. database).
     */
    public void hasUpdated()
    {
        if (hasNewData())
        {
            DebugOut.println(20, "hasUpdated writing " + uploadedFilename() + " to disk");
            writeFile();
        }
    }



    // Notifications we do not care about.
    public void didInsert()
    {
    }


    public void didDelete()
    {
    }


    public void willUpdate()
    {
    }


    public void hasReverted()
    {
    }


    /**
     * Returns name as "folder name: displayName".  It returns just the displayName if it belongs to the root folder.  This is an implementation for RTEListSelectable.
     *  
     * @return name as "folder name: displayName".  It returns just the displayName if it belongs to the root folder
     */
    public String listDisplayName()
    {
        String listDisplayName = displayName();

        if (!folder().isRootFolder())
        {
            listDisplayName = folder().name() + ": " + listDisplayName;
        }

        return listDisplayName;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the shortName of SiteFile, if null or empty, returns the uploadedFilename.  This is used for SiteFile picklists and alt tag for images.
     *  
     * @return the shortName of SiteFile, if null or empty, returns the uploadedFilename.
     */
    public String displayName()
    {
        return StringAdditions.isEmpty(shortName()) ? uploadedFilename() : shortName();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the relativeFileURL for this SiteFile.  http:// or https:// are not included to avoid security warnings about mixed secure and nonsecure items on the same page. This is an implementation for RTEListSelectable.
     *  
     * @return the relativeFileURL for this SiteFile
     */
    public String listURL()
    {
        return SMActionURLFactory.relativeFileURL(this.website().siteID(), uploadedFilename());

        /** ensure [result_not_null] Result != null; **/
    }
}
