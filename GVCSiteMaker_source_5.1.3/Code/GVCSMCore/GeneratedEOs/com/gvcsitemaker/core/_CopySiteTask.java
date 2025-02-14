
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to CopySiteTask.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _CopySiteTask extends com.gvcsitemaker.core.Task
{


    public static final String COPYSITETASK_ENTITY_NAME = "CopySiteTask";


    public static final String RELATEDORGUNIT = "relatedOrgUnit";
    public static final String RELATEDREQUEST = "relatedRequest";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "CopySiteTask");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public CopySiteTask localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (CopySiteTask)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit relatedOrgUnit() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("relatedOrgUnit");
    }



    public void setRelatedOrgUnit(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedOrgUnit");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.WebsiteRequest
     
     */
    public com.gvcsitemaker.core.WebsiteRequest relatedRequest() 
    {
        return (com.gvcsitemaker.core.WebsiteRequest)storedValueForKey("relatedRequest");
    }



    public void setRelatedRequest(com.gvcsitemaker.core.WebsiteRequest value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedRequest");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of CopySiteTasks in editingContext
	 */
    public static NSArray fetchAllCopySiteTasks(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CopySiteTask.fetchAllCopySiteTasks(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CopySiteTasks, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllCopySiteTasks(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CopySiteTask.fetchCopySiteTasks(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CopySiteTasks matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchCopySiteTasks(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(COPYSITETASK_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of CopySiteTasks where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static CopySiteTask fetchCopySiteTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CopySiteTask.fetchCopySiteTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CopySiteTasks matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static CopySiteTask fetchCopySiteTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _CopySiteTask.fetchCopySiteTasks(editingContext, qualifier, null);
        CopySiteTask eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (CopySiteTask)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one CopySiteTask that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of CopySiteTasks where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static CopySiteTask fetchRequiredCopySiteTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CopySiteTask.fetchRequiredCopySiteTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CopySiteTasks matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static CopySiteTask fetchRequiredCopySiteTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        CopySiteTask eoObject = _CopySiteTask.fetchCopySiteTask(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no CopySiteTask that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
