
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SectionType.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SectionType extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String SECTIONTYPE_ENTITY_NAME = "SectionType";

    public static final String EDITORNAME = "editorName";
    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String OID = "oid";
    public static final String ORDER = "order";
    public static final String PAGECOMPONENTENTITY = "pageComponentEntity";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SectionType");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SectionType localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SectionType)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String editorName() {
        return (String) storedValueForKey("editorName");
    }



    public void setEditorName(String value) 
    {
        takeStoredValueForKey(value, "editorName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String key() {
        return (String) storedValueForKey("key");
    }



    public void setKey(String value) 
    {
        takeStoredValueForKey(value, "key");
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
     * @return Number     
     */
    public Number order() {
        return (Number) storedValueForKey("order");
    }



    public void setOrder(Number value) 
    {
        takeStoredValueForKey(value, "order");
    }
    
    

    /**
     *
     * @return String     
     */
    public String pageComponentEntity() {
        return (String) storedValueForKey("pageComponentEntity");
    }



    public void setPageComponentEntity(String value) 
    {
        takeStoredValueForKey(value, "pageComponentEntity");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of SectionTypes in editingContext
	 */
    public static NSArray fetchAllSectionTypes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionType.fetchAllSectionTypes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionTypes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSectionTypes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionType.fetchSectionTypes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionTypes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSectionTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SECTIONTYPE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SectionType fetchSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionType.fetchSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionTypes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SectionType fetchSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SectionType.fetchSectionTypes(editingContext, qualifier, null);
        SectionType eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SectionType)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SectionType that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SectionType fetchRequiredSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionType.fetchRequiredSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionTypes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SectionType fetchRequiredSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SectionType eoObject = _SectionType.fetchSectionType(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SectionType that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
