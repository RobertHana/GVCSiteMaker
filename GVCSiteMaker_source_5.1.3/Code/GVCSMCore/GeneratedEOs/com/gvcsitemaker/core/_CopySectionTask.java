
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to CopySectionTask.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _CopySectionTask extends com.gvcsitemaker.core.Task
{


    public static final String COPYSECTIONTASK_ENTITY_NAME = "CopySectionTask";

    public static final String ISNAVIGABLE = "isNavigable";

    public static final String RELATEDSECTION = "relatedSection";
    public static final String SOURCESECTION = "sourceSection";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "CopySectionTask");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public CopySectionTask localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (CopySectionTask)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isNavigable() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isNavigable");
    }



    public void setIsNavigable(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isNavigable");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.Section
     
     */
    public com.gvcsitemaker.core.Section relatedSection() 
    {
        return (com.gvcsitemaker.core.Section)storedValueForKey("relatedSection");
    }



    public void setRelatedSection(com.gvcsitemaker.core.Section value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedSection");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Section
     
     */
    public com.gvcsitemaker.core.Section sourceSection() 
    {
        return (com.gvcsitemaker.core.Section)storedValueForKey("sourceSection");
    }



    public void setSourceSection(com.gvcsitemaker.core.Section value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "sourceSection");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of CopySectionTasks in editingContext
	 */
    public static NSArray fetchAllCopySectionTasks(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CopySectionTask.fetchAllCopySectionTasks(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CopySectionTasks, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllCopySectionTasks(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _CopySectionTask.fetchCopySectionTasks(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of CopySectionTasks matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchCopySectionTasks(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(COPYSECTIONTASK_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of CopySectionTasks where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static CopySectionTask fetchCopySectionTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CopySectionTask.fetchCopySectionTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CopySectionTasks matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static CopySectionTask fetchCopySectionTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _CopySectionTask.fetchCopySectionTasks(editingContext, qualifier, null);
        CopySectionTask eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (CopySectionTask)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one CopySectionTask that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of CopySectionTasks where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static CopySectionTask fetchRequiredCopySectionTask(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _CopySectionTask.fetchRequiredCopySectionTask(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of CopySectionTasks matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static CopySectionTask fetchRequiredCopySectionTask(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        CopySectionTask eoObject = _CopySectionTask.fetchCopySectionTask(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no CopySectionTask that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
