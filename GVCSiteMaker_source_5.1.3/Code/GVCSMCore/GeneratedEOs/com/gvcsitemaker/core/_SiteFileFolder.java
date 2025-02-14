
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SiteFileFolder.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SiteFileFolder extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String SITEFILEFOLDER_ENTITY_NAME = "SiteFileFolder";

    public static final String NAME = "name";
    public static final String OID = "oid";

    public static final String CHILDFOLDERS = "childFolders";
    public static final String FILES = "files";
    public static final String PARENTFOLDER = "parentFolder";
    public static final String WEBSITE = "website";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SiteFileFolder");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SiteFileFolder localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SiteFileFolder)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String name() {
        return (String) storedValueForKey("name");
    }



    public void setName(String value) 
    {
        takeStoredValueForKey(value, "name");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number oid() {
        return (Number) storedValueForKey("oid");
    }



    public void setOid(Number value) 
    {
        takeStoredValueForKey(value, "oid");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.SiteFileFolder
     
     */
    public com.gvcsitemaker.core.SiteFileFolder parentFolder() 
    {
        return (com.gvcsitemaker.core.SiteFileFolder)storedValueForKey("parentFolder");
    }



    public void setParentFolder(com.gvcsitemaker.core.SiteFileFolder value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "parentFolder");
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
     * @return com.gvcsitemaker.core.SiteFileFolder
     */
    public NSArray childFolders() 
    {
        return (NSArray)storedValueForKey("childFolders");
    }



    public void setChildFolders(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.SiteFileFolder)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "childFolders");
    }



    public void addToChildFolders(com.gvcsitemaker.core.SiteFileFolder object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_childFolders] ! childFolders().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childFolders();

        willChange();
        array.addObject(object);
        /** ensure [added_to_childFolders] childFolders().containsObject(object);        **/
    }



    public void removeFromChildFolders(com.gvcsitemaker.core.SiteFileFolder object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_childFolders] childFolders().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childFolders();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_childFolders] ! childFolders().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray childFolders(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SiteFileFolder.PARENTFOLDER, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.SiteFileFolder.fetchSiteFileFolders(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = childFolders();
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
     * @return com.gvcsitemaker.core.SiteFile
     */
    public NSArray files() 
    {
        return (NSArray)storedValueForKey("files");
    }



    public void setFiles(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.SiteFile)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "files");
    }



    public void addToFiles(com.gvcsitemaker.core.SiteFile object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_files] ! files().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)files();

        willChange();
        array.addObject(object);
        /** ensure [added_to_files] files().containsObject(object);        **/
    }



    public void removeFromFiles(com.gvcsitemaker.core.SiteFile object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_files] files().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)files();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_files] ! files().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray files(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SiteFile.FOLDER, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.SiteFile.fetchSiteFiles(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = files();
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
	 * @return all instances of SiteFileFolders in editingContext
	 */
    public static NSArray fetchAllSiteFileFolders(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SiteFileFolder.fetchAllSiteFileFolders(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SiteFileFolders, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSiteFileFolders(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SiteFileFolder.fetchSiteFileFolders(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SiteFileFolders matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSiteFileFolders(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SITEFILEFOLDER_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SiteFileFolders where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SiteFileFolder fetchSiteFileFolder(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SiteFileFolder.fetchSiteFileFolder(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SiteFileFolders matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SiteFileFolder fetchSiteFileFolder(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SiteFileFolder.fetchSiteFileFolders(editingContext, qualifier, null);
        SiteFileFolder eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SiteFileFolder)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SiteFileFolder that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SiteFileFolders where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SiteFileFolder fetchRequiredSiteFileFolder(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SiteFileFolder.fetchRequiredSiteFileFolder(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SiteFileFolders matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SiteFileFolder fetchRequiredSiteFileFolder(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SiteFileFolder eoObject = _SiteFileFolder.fetchSiteFileFolder(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SiteFileFolder that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
