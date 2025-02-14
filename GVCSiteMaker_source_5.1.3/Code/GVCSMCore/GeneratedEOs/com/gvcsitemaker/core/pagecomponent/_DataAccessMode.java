
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to DataAccessMode.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _DataAccessMode extends com.gvcsitemaker.core.pagecomponent.Layout
{


    public static final String DATAACCESSMODE_ENTITY_NAME = "DataAccessMode";


    public static final String CUSTOMTEMPLATE = "customTemplate";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "DataAccessMode");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public DataAccessMode localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (DataAccessMode)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.DataAccessModeTemplate
     
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessModeTemplate customTemplate() 
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessModeTemplate)storedValueForKey("customTemplate");
    }



    public void setCustomTemplate(com.gvcsitemaker.core.pagecomponent.DataAccessModeTemplate value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "customTemplate");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of DataAccessModes in editingContext
	 */
    public static NSArray fetchAllDataAccessModes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessMode.fetchAllDataAccessModes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessModes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllDataAccessModes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessMode.fetchDataAccessModes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessModes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchDataAccessModes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(DATAACCESSMODE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of DataAccessModes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static DataAccessMode fetchDataAccessMode(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessMode.fetchDataAccessMode(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessModes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static DataAccessMode fetchDataAccessMode(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _DataAccessMode.fetchDataAccessModes(editingContext, qualifier, null);
        DataAccessMode eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (DataAccessMode)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one DataAccessMode that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of DataAccessModes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static DataAccessMode fetchRequiredDataAccessMode(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessMode.fetchRequiredDataAccessMode(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessModes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static DataAccessMode fetchRequiredDataAccessMode(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        DataAccessMode eoObject = _DataAccessMode.fetchDataAccessMode(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no DataAccessMode that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
