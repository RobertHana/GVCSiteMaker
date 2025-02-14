
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to InstallDataTablePackageTask.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _InstallDataTablePackageTask extends com.gvcsitemaker.core.Task
{


    public static final String INSTALLDATATABLEPACKAGETASK_ENTITY_NAME = "InstallDataTablePackageTask";


    public static final String RELATEDSITEFILE = "relatedSiteFile";
    public static final String RELATEDTABLE = "relatedTable";
    public static final String SECTIONS = "sections";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "InstallDataTablePackageTask");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public InstallDataTablePackageTask localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (InstallDataTablePackageTask)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.SiteFile
     
     */
    public com.gvcsitemaker.core.SiteFile relatedSiteFile() 
    {
        return (com.gvcsitemaker.core.SiteFile)storedValueForKey("relatedSiteFile");
    }



    public void setRelatedSiteFile(com.gvcsitemaker.core.SiteFile value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedSiteFile");
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
	 * @param editingContext EC to return objects in
	 * @return all instances of InstallDataTablePackageTasks in editingContext
	 */
    public static NSArray fetchAllInstallDataTablePackageTasks(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _InstallDataTablePackageTask.fetchAllInstallDataTablePackageTasks(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of InstallDataTablePackageTasks, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllInstallDataTablePackageTasks(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _InstallDataTablePackageTask.fetchInstallDataTablePackageTasks(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of InstallDataTablePackageTasks matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchInstallDataTablePackageTasks(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(INSTALLDATATABLEPACKAGETASK_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of InstallDataTablePackageTasks where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static InstallDataTablePackageTask fetchInstallDataTablePackageTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _InstallDataTablePackageTask.fetchInstallDataTablePackageTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of InstallDataTablePackageTasks matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static InstallDataTablePackageTask fetchInstallDataTablePackageTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _InstallDataTablePackageTask.fetchInstallDataTablePackageTasks(editingContext, qualifier, null);
        InstallDataTablePackageTask eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (InstallDataTablePackageTask)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one InstallDataTablePackageTask that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of InstallDataTablePackageTasks where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static InstallDataTablePackageTask fetchRequiredInstallDataTablePackageTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _InstallDataTablePackageTask.fetchRequiredInstallDataTablePackageTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of InstallDataTablePackageTasks matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static InstallDataTablePackageTask fetchRequiredInstallDataTablePackageTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        InstallDataTablePackageTask eoObject = _InstallDataTablePackageTask.fetchInstallDataTablePackageTask(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no InstallDataTablePackageTask that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
