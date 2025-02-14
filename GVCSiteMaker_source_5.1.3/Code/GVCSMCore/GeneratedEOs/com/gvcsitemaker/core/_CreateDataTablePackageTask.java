
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to CreateDataTablePackageTask.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _CreateDataTablePackageTask extends com.gvcsitemaker.core.Task
{


    public static final String CREATEDATATABLEPACKAGETASK_ENTITY_NAME = "CreateDataTablePackageTask";


    public static final String RELATEDSITEFILEFOLDER = "relatedSiteFileFolder";
    public static final String RELATEDTABLE = "relatedTable";
    public static final String SECTIONS = "sections";
    public static final String TABLES = "tables";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "CreateDataTablePackageTask");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public CreateDataTablePackageTask localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (CreateDataTablePackageTask)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.SiteFileFolder
     
     */
    public com.gvcsitemaker.core.SiteFileFolder relatedSiteFileFolder() 
    {
        return (com.gvcsitemaker.core.SiteFileFolder)storedValueForKey("relatedSiteFileFolder");
    }



    public void setRelatedSiteFileFolder(com.gvcsitemaker.core.SiteFileFolder value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedSiteFileFolder");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.Table
     
     */
    public net.global_village.virtualtables.Table relatedTable() 
    {
        return (net.global_village.virtualtables.Table)storedValueForKey("relatedTable");
    }



    public void setRelatedTable(net.global_village.virtualtables.Table value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedTable");
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
     *
     * @return com.gvcsitemaker.core.databasetables.SMVirtualTable
     */
    public NSArray tables() 
    {
        return (NSArray)storedValueForKey("tables");
    }



    public void setTables(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.databasetables.SMVirtualTable)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "tables");
    }



    public void addToTables(com.gvcsitemaker.core.databasetables.SMVirtualTable object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_tables] ! tables().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)tables();

        willChange();
        array.addObject(object);
        /** ensure [added_to_tables] tables().containsObject(object);        **/
    }



    public void removeFromTables(com.gvcsitemaker.core.databasetables.SMVirtualTable object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_tables] tables().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)tables();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_tables] ! tables().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray tables(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = tables();
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
	 * @return all instances of CreateDataTablePackageTasks in editingContext
	 */
    public static NSArray fetchAllCreateDataTablePackageTasks(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CreateDataTablePackageTask.fetchAllCreateDataTablePackageTasks(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CreateDataTablePackageTasks, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllCreateDataTablePackageTasks(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CreateDataTablePackageTask.fetchCreateDataTablePackageTasks(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CreateDataTablePackageTasks matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchCreateDataTablePackageTasks(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(CREATEDATATABLEPACKAGETASK_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of CreateDataTablePackageTasks where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static CreateDataTablePackageTask fetchCreateDataTablePackageTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CreateDataTablePackageTask.fetchCreateDataTablePackageTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CreateDataTablePackageTasks matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static CreateDataTablePackageTask fetchCreateDataTablePackageTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _CreateDataTablePackageTask.fetchCreateDataTablePackageTasks(editingContext, qualifier, null);
        CreateDataTablePackageTask eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (CreateDataTablePackageTask)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one CreateDataTablePackageTask that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of CreateDataTablePackageTasks where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static CreateDataTablePackageTask fetchRequiredCreateDataTablePackageTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CreateDataTablePackageTask.fetchRequiredCreateDataTablePackageTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CreateDataTablePackageTasks matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static CreateDataTablePackageTask fetchRequiredCreateDataTablePackageTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        CreateDataTablePackageTask eoObject = _CreateDataTablePackageTask.fetchCreateDataTablePackageTask(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no CreateDataTablePackageTask that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
