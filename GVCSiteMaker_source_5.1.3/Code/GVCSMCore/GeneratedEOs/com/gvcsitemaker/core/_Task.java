
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Task.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Task extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String TASK_ENTITY_NAME = "Task";

    public static final String BINDINGS = "bindings";
    public static final String CREATEDDATE = "createdDate";
    public static final String DETAILS = "details";
    public static final String FAILUREREASON = "failureReason";
    public static final String TASKTYPE = "taskType";

    public static final String OWNER = "owner";
    public static final String STATUS = "status";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Task");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Task localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Task)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String bindings() {
        return (String) storedValueForKey("bindings");
    }



    public void setBindings(String value) 
    {
        takeStoredValueForKey(value, "bindings");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp createdDate() {
        return (NSTimestamp) storedValueForKey("createdDate");
    }



    public void setCreatedDate(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "createdDate");
    }
    
    

    /**
     *
     * @return String     
     */
    public String details() {
        return (String) storedValueForKey("details");
    }



    public void setDetails(String value) 
    {
        takeStoredValueForKey(value, "details");
    }
    
    

    /**
     *
     * @return String     
     */
    public String failureReason() {
        return (String) storedValueForKey("failureReason");
    }



    public void setFailureReason(String value) 
    {
        takeStoredValueForKey(value, "failureReason");
    }
    
    

    /**
     *
     * @return String     
     */
    public String taskType() {
        return (String) storedValueForKey("taskType");
    }



    public void setTaskType(String value) 
    {
        takeStoredValueForKey(value, "taskType");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User owner() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("owner");
    }



    public void setOwner(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "owner");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.TaskStatus
     
     */
    public com.gvcsitemaker.core.TaskStatus status() 
    {
        return (com.gvcsitemaker.core.TaskStatus)storedValueForKey("status");
    }



    public void setStatus(com.gvcsitemaker.core.TaskStatus value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "status");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of Tasks in editingContext
	 */
    public static NSArray fetchAllTasks(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Task.fetchAllTasks(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Tasks, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllTasks(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Task.fetchTasks(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Tasks matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchTasks(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(TASK_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Tasks where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Task fetchTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Task.fetchTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Tasks matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Task fetchTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Task.fetchTasks(editingContext, qualifier, null);
        Task eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Task)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Task that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Tasks where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Task fetchRequiredTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Task.fetchRequiredTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Tasks matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Task fetchRequiredTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Task eoObject = _Task.fetchTask(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Task that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
