
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to DataAccess.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _DataAccess extends com.gvcsitemaker.core.pagecomponent.PageComponent
{


    public static final String DATAACCESS_ENTITY_NAME = "DataAccess";


    public static final String DATABASETABLE = "databaseTable";
    public static final String NOTIFICATIONCOLUMNS = "notificationColumns";
    public static final String NOTIFICATIONGROUPS = "notificationGroups";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "DataAccess");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public DataAccess localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (DataAccess)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return net.global_village.virtualtables.Table
     
     */
    public net.global_village.virtualtables.Table databaseTable() 
    {
        return (net.global_village.virtualtables.Table)storedValueForKey("databaseTable");
    }



    public void setDatabaseTable(net.global_village.virtualtables.Table value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "databaseTable");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.Column
     */
    public NSArray notificationColumns() 
    {
        return (NSArray)storedValueForKey("notificationColumns");
    }



    public void setNotificationColumns(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.Column)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "notificationColumns");
    }



    public void addToNotificationColumns(net.global_village.virtualtables.Column object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_notificationColumns] ! notificationColumns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)notificationColumns();

        willChange();
        array.addObject(object);
        /** ensure [added_to_notificationColumns] notificationColumns().containsObject(object);        **/
    }



    public void removeFromNotificationColumns(net.global_village.virtualtables.Column object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_notificationColumns] notificationColumns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)notificationColumns();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_notificationColumns] ! notificationColumns().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray notificationColumns(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = notificationColumns();
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
     * @return com.gvcsitemaker.core.WebsiteGroup
     */
    public NSArray notificationGroups() 
    {
        return (NSArray)storedValueForKey("notificationGroups");
    }



    public void setNotificationGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.WebsiteGroup)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "notificationGroups");
    }



    public void addToNotificationGroups(com.gvcsitemaker.core.WebsiteGroup object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_notificationGroups] ! notificationGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)notificationGroups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_notificationGroups] notificationGroups().containsObject(object);        **/
    }



    public void removeFromNotificationGroups(com.gvcsitemaker.core.WebsiteGroup object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_notificationGroups] notificationGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)notificationGroups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_notificationGroups] ! notificationGroups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray notificationGroups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = notificationGroups();
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
	 * @return all instances of DataAccesses in editingContext
	 */
    public static NSArray fetchAllDataAccesses(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccess.fetchAllDataAccesses(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccesses, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllDataAccesses(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccess.fetchDataAccesses(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccesses matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchDataAccesses(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(DATAACCESS_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of DataAccesses where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static DataAccess fetchDataAccess(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccess.fetchDataAccess(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccesses matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static DataAccess fetchDataAccess(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _DataAccess.fetchDataAccesses(editingContext, qualifier, null);
        DataAccess eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (DataAccess)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one DataAccess that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of DataAccesses where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static DataAccess fetchRequiredDataAccess(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccess.fetchRequiredDataAccess(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccesses matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static DataAccess fetchRequiredDataAccess(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        DataAccess eoObject = _DataAccess.fetchDataAccess(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no DataAccess that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
