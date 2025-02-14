
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SiteFile.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SiteFile extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String SITEFILE_ENTITY_NAME = "SiteFile";

    public static final String FILEDESCRIPTION = "fileDescription";
    public static final String FILENAME = "filename";
    public static final String FILEPKEY = "filePKey";
    public static final String FILESIZEUSAGE = "fileSizeUsage";
    public static final String MIMETYPE = "mimeType";
    public static final String SHORTNAME = "shortName";
    public static final String UPLOADDATE = "uploadDate";
    public static final String UPLOADEDFILENAME = "uploadedFilename";

    public static final String FOLDER = "folder";
    public static final String GROUPS = "groups";
    public static final String PASSWORDS = "passwords";
    public static final String TOCOMPONENTS = "toComponents";
    public static final String WEBSITE = "website";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SiteFile");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SiteFile localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SiteFile)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String fileDescription() {
        return (String) storedValueForKey("fileDescription");
    }



    public void setFileDescription(String value) 
    {
        takeStoredValueForKey(value, "fileDescription");
    }
    
    

    /**
     *
     * @return String     
     */
    public String filename() {
        return (String) storedValueForKey("filename");
    }



    public void setFilename(String value) 
    {
        takeStoredValueForKey(value, "filename");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number filePKey() {
        return (Number) storedValueForKey("filePKey");
    }



    public void setFilePKey(Number value) 
    {
        takeStoredValueForKey(value, "filePKey");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number fileSizeUsage() {
        return (Number) storedValueForKey("fileSizeUsage");
    }



    public void setFileSizeUsage(Number value) 
    {
        takeStoredValueForKey(value, "fileSizeUsage");
    }
    
    

    /**
     *
     * @return String     
     */
    public String mimeType() {
        return (String) storedValueForKey("mimeType");
    }



    public void setMimeType(String value) 
    {
        takeStoredValueForKey(value, "mimeType");
    }
    
    

    /**
     *
     * @return String     
     */
    public String shortName() {
        return (String) storedValueForKey("shortName");
    }



    public void setShortName(String value) 
    {
        takeStoredValueForKey(value, "shortName");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp uploadDate() {
        return (NSTimestamp) storedValueForKey("uploadDate");
    }



    public void setUploadDate(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "uploadDate");
    }
    
    

    /**
     *
     * @return String     
     */
    public String uploadedFilename() {
        return (String) storedValueForKey("uploadedFilename");
    }



    public void setUploadedFilename(String value) 
    {
        takeStoredValueForKey(value, "uploadedFilename");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.SiteFileFolder
     
     */
    public com.gvcsitemaker.core.SiteFileFolder folder() 
    {
        return (com.gvcsitemaker.core.SiteFileFolder)storedValueForKey("folder");
    }



    public void setFolder(com.gvcsitemaker.core.SiteFileFolder value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "folder");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Website
     
     */
    public com.gvcsitemaker.core.Website website() 
    {
        return (com.gvcsitemaker.core.Website)storedValueForKey("website");
    }



    public void setWebsite(com.gvcsitemaker.core.Website value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "website");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Group
     */
    public NSArray groups() 
    {
        return (NSArray)storedValueForKey("groups");
    }



    public void setGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Group)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "groups");
    }



    public void addToGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_groups] ! groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_groups] groups().containsObject(object);        **/
    }



    public void removeFromGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_groups] groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_groups] ! groups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray groups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = groups();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.FilePassword
     */
    public NSArray passwords() 
    {
        return (NSArray)storedValueForKey("passwords");
    }



    public void setPasswords(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.FilePassword)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "passwords");
    }



    public void addToPasswords(com.gvcsitemaker.core.FilePassword object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_passwords] ! passwords().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)passwords();

        willChange();
        array.addObject(object);
        /** ensure [added_to_passwords] passwords().containsObject(object);        **/
    }



    public void removeFromPasswords(com.gvcsitemaker.core.FilePassword object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_passwords] passwords().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)passwords();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_passwords] ! passwords().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray passwords(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.FilePassword.SITEFILE, EOQualifier.QualifierOperatorEqual, this);
    	
          if (qualifier == null) 
          {
              fullQualifier = inverseQualifier;
          }
          else 
          {
              NSMutableArray qualifiers = new NSMutableArray();
              qualifiers.addObject(qualifier);
              qualifiers.addObject(inverseQualifier);
              fullQualifier = new EOAndQualifier(qualifiers);
          }

          results = com.gvcsitemaker.core.FilePassword.fetchFilePasswords(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = passwords();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.PageComponent
     */
    public NSArray toComponents() 
    {
        return (NSArray)storedValueForKey("toComponents");
    }



    public void setToComponents(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.PageComponent)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "toComponents");
    }



    public void addToToComponents(com.gvcsitemaker.core.pagecomponent.PageComponent object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_toComponents] ! toComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)toComponents();

        willChange();
        array.addObject(object);
        /** ensure [added_to_toComponents] toComponents().containsObject(object);        **/
    }



    public void removeFromToComponents(com.gvcsitemaker.core.pagecomponent.PageComponent object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_toComponents] toComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)toComponents();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_toComponents] ! toComponents().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray toComponents(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.pagecomponent.PageComponent.TORELATEDFILE, EOQualifier.QualifierOperatorEqual, this);
    	
          if (qualifier == null) 
          {
              fullQualifier = inverseQualifier;
          }
          else 
          {
              NSMutableArray qualifiers = new NSMutableArray();
              qualifiers.addObject(qualifier);
              qualifiers.addObject(inverseQualifier);
              fullQualifier = new EOAndQualifier(qualifiers);
          }

          results = com.gvcsitemaker.core.pagecomponent.PageComponent.fetchPageComponents(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = toComponents();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  



	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of SiteFiles in editingContext
	 */
    public static NSArray fetchAllSiteFiles(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SiteFile.fetchAllSiteFiles(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SiteFiles, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSiteFiles(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SiteFile.fetchSiteFiles(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SiteFiles matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSiteFiles(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SITEFILE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SiteFiles where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SiteFile fetchSiteFile(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SiteFile.fetchSiteFile(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SiteFiles matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SiteFile fetchSiteFile(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SiteFile.fetchSiteFiles(editingContext, qualifier, null);
        SiteFile eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SiteFile)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SiteFile that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SiteFiles where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SiteFile fetchRequiredSiteFile(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SiteFile.fetchRequiredSiteFile(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SiteFiles matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SiteFile fetchRequiredSiteFile(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SiteFile eoObject = _SiteFile.fetchSiteFile(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SiteFile that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
