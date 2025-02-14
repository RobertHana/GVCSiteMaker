
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to WebsiteGroup.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _WebsiteGroup extends com.gvcsitemaker.core.Group
{


    public static final String WEBSITEGROUP_ENTITY_NAME = "WebsiteGroup";


    public static final String DATAACCESSNOTIFICATIONS = "dataAccessNotifications";
    public static final String FILES = "files";
    public static final String PARENTWEBSITE = "parentWebsite";
    public static final String SECTIONS = "sections";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "WebsiteGroup");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public WebsiteGroup localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (WebsiteGroup)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.Website
     
     */
    public com.gvcsitemaker.core.Website parentWebsite() 
    {
        return (com.gvcsitemaker.core.Website)storedValueForKey("parentWebsite");
    }



    public void setParentWebsite(com.gvcsitemaker.core.Website value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "parentWebsite");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.DataAccess
     */
    public NSArray dataAccessNotifications() 
    {
        return (NSArray)storedValueForKey("dataAccessNotifications");
    }



    public void setDataAccessNotifications(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.DataAccess)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "dataAccessNotifications");
    }



    public void addToDataAccessNotifications(com.gvcsitemaker.core.pagecomponent.DataAccess object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_dataAccessNotifications] ! dataAccessNotifications().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)dataAccessNotifications();

        willChange();
        array.addObject(object);
        /** ensure [added_to_dataAccessNotifications] dataAccessNotifications().containsObject(object);        **/
    }



    public void removeFromDataAccessNotifications(com.gvcsitemaker.core.pagecomponent.DataAccess object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_dataAccessNotifications] dataAccessNotifications().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)dataAccessNotifications();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_dataAccessNotifications] ! dataAccessNotifications().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray dataAccessNotifications(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = dataAccessNotifications();
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

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray files(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = files();
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
     * @return com.gvcsitemaker.core.Section
     */
    public NSArray sections() 
    {
        return (NSArray)storedValueForKey("sections");
    }



    public void setSections(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Section)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "sections");
    }



    public void addToSections(com.gvcsitemaker.core.Section object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_sections] ! sections().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)sections();

        willChange();
        array.addObject(object);
        /** ensure [added_to_sections] sections().containsObject(object);        **/
    }



    public void removeFromSections(com.gvcsitemaker.core.Section object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_sections] sections().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)sections();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_sections] ! sections().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray sections(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = sections();
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
	 * @param editingContext EC to return objects in
	 * @return all instances of WebsiteGroups in editingContext
	 */
    public static NSArray fetchAllWebsiteGroups(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteGroup.fetchAllWebsiteGroups(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteGroups, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllWebsiteGroups(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteGroup.fetchWebsiteGroups(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteGroups matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchWebsiteGroups(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(WEBSITEGROUP_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of WebsiteGroups where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static WebsiteGroup fetchWebsiteGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteGroup.fetchWebsiteGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteGroups matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static WebsiteGroup fetchWebsiteGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _WebsiteGroup.fetchWebsiteGroups(editingContext, qualifier, null);
        WebsiteGroup eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (WebsiteGroup)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one WebsiteGroup that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of WebsiteGroups where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static WebsiteGroup fetchRequiredWebsiteGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteGroup.fetchRequiredWebsiteGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteGroups matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static WebsiteGroup fetchRequiredWebsiteGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        WebsiteGroup eoObject = _WebsiteGroup.fetchWebsiteGroup(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no WebsiteGroup that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
