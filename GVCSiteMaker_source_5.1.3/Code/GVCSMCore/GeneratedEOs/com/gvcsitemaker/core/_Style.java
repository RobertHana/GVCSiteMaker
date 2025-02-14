
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Style.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Style extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String STYLE_ENTITY_NAME = "Style";

    public static final String DATECREATED = "dateCreated";
    public static final String DATEMODIFIED = "dateModified";
    public static final String NAME = "name";
    public static final String NOTES = "notes";
    public static final String PUBLISHED = "published";
    public static final String STYLEID = "styleID";
    public static final String TEMPLATE = "template";

    public static final String CREATEDBY = "createdBy";
    public static final String MODIFIEDBY = "modifiedBy";
    public static final String OWNINGORGUNIT = "owningOrgUnit";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Style");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Style localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Style)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateCreated() {
        return (NSTimestamp) storedValueForKey("dateCreated");
    }



    public void setDateCreated(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateCreated");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateModified() {
        return (NSTimestamp) storedValueForKey("dateModified");
    }



    public void setDateModified(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateModified");
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
     * @return String     
     */
    public String notes() {
        return (String) storedValueForKey("notes");
    }



    public void setNotes(String value) 
    {
        takeStoredValueForKey(value, "notes");
    }
    
    

    /**
     *
     * @return String     
     */
    public String published() {
        return (String) storedValueForKey("published");
    }



    public void setPublished(String value) 
    {
        takeStoredValueForKey(value, "published");
    }
    
    

    /**
     *
     * @return String     
     */
    public String styleID() {
        return (String) storedValueForKey("styleID");
    }



    public void setStyleID(String value) 
    {
        takeStoredValueForKey(value, "styleID");
    }
    
    

    /**
     *
     * @return String     
     */
    public String template() {
        return (String) storedValueForKey("template");
    }



    public void setTemplate(String value) 
    {
        takeStoredValueForKey(value, "template");
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
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit owningOrgUnit() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("owningOrgUnit");
    }



    public void setOwningOrgUnit(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "owningOrgUnit");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of Styles in editingContext
	 */
    public static NSArray fetchAllStyles(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Style.fetchAllStyles(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Styles, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllStyles(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Style.fetchStyles(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Styles matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchStyles(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(STYLE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Styles where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Style fetchStyle(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Style.fetchStyle(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Styles matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Style fetchStyle(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Style.fetchStyles(editingContext, qualifier, null);
        Style eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Style)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Style that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Styles where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Style fetchRequiredStyle(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Style.fetchRequiredStyle(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Styles matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Style fetchRequiredStyle(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Style eoObject = _Style.fetchStyle(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Style that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
