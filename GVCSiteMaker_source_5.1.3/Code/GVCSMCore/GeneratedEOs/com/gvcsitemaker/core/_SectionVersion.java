
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SectionVersion.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SectionVersion extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String SECTIONVERSION_ENTITY_NAME = "SectionVersion";

    public static final String COMPONENTORDER = "componentOrder";
    public static final String CREATEDDATE = "createdDate";
    public static final String DETAILS = "details";
    public static final String ISLOCKED = "isLocked";
    public static final String MODIFIEDDATE = "modifiedDate";
    public static final String NAME = "name";
    public static final String VERSIONNUMBER = "versionNumber";

    public static final String COMPONENT = "component";
    public static final String CREATEDBY = "createdBy";
    public static final String MODIFIEDBY = "modifiedBy";
    public static final String SECTION = "section";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SectionVersion");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SectionVersion localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SectionVersion)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return Number     
     */
    public Number componentOrder() {
        return (Number) storedValueForKey("componentOrder");
    }



    public void setComponentOrder(Number value) 
    {
        takeStoredValueForKey(value, "componentOrder");
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
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isLocked() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isLocked");
    }



    public void setIsLocked(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isLocked");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp modifiedDate() {
        return (NSTimestamp) storedValueForKey("modifiedDate");
    }



    public void setModifiedDate(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "modifiedDate");
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
    public Number versionNumber() {
        return (Number) storedValueForKey("versionNumber");
    }



    public void setVersionNumber(Number value) 
    {
        takeStoredValueForKey(value, "versionNumber");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.PageComponent
     
     */
    public com.gvcsitemaker.core.pagecomponent.PageComponent component() 
    {
        return (com.gvcsitemaker.core.pagecomponent.PageComponent)storedValueForKey("component");
    }



    public void setComponent(com.gvcsitemaker.core.pagecomponent.PageComponent value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "component");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User createdBy() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("createdBy");
    }



    public void setCreatedBy(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "createdBy");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User modifiedBy() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("modifiedBy");
    }



    public void setModifiedBy(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "modifiedBy");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Section
     
     */
    public com.gvcsitemaker.core.Section section() 
    {
        return (com.gvcsitemaker.core.Section)storedValueForKey("section");
    }



    public void setSection(com.gvcsitemaker.core.Section value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "section");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of SectionVersions in editingContext
	 */
    public static NSArray fetchAllSectionVersions(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionVersion.fetchAllSectionVersions(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionVersions, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSectionVersions(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionVersion.fetchSectionVersions(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionVersions matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSectionVersions(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SECTIONVERSION_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SectionVersions where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SectionVersion fetchSectionVersion(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionVersion.fetchSectionVersion(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionVersions matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SectionVersion fetchSectionVersion(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SectionVersion.fetchSectionVersions(editingContext, qualifier, null);
        SectionVersion eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SectionVersion)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SectionVersion that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SectionVersions where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SectionVersion fetchRequiredSectionVersion(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionVersion.fetchRequiredSectionVersion(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionVersions matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SectionVersion fetchRequiredSectionVersion(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SectionVersion eoObject = _SectionVersion.fetchSectionVersion(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SectionVersion that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
