
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to EmbeddedSectionSectionType.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _EmbeddedSectionSectionType extends com.gvcsitemaker.core.SectionType
{


    public static final String EMBEDDEDSECTIONSECTIONTYPE_ENTITY_NAME = "EmbeddedSectionSectionType";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "EmbeddedSectionSectionType");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public EmbeddedSectionSectionType localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (EmbeddedSectionSectionType)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of EmbeddedSectionSectionTypes in editingContext
	 */
    public static NSArray fetchAllEmbeddedSectionSectionTypes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSectionSectionType.fetchAllEmbeddedSectionSectionTypes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSectionSectionTypes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllEmbeddedSectionSectionTypes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSectionSectionType.fetchEmbeddedSectionSectionTypes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSectionSectionTypes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchEmbeddedSectionSectionTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(EMBEDDEDSECTIONSECTIONTYPE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of EmbeddedSectionSectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static EmbeddedSectionSectionType fetchEmbeddedSectionSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSectionSectionType.fetchEmbeddedSectionSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSectionSectionTypes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static EmbeddedSectionSectionType fetchEmbeddedSectionSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _EmbeddedSectionSectionType.fetchEmbeddedSectionSectionTypes(editingContext, qualifier, null);
        EmbeddedSectionSectionType eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (EmbeddedSectionSectionType)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one EmbeddedSectionSectionType that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of EmbeddedSectionSectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static EmbeddedSectionSectionType fetchRequiredEmbeddedSectionSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSectionSectionType.fetchRequiredEmbeddedSectionSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSectionSectionTypes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static EmbeddedSectionSectionType fetchRequiredEmbeddedSectionSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        EmbeddedSectionSectionType eoObject = _EmbeddedSectionSectionType.fetchEmbeddedSectionSectionType(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no EmbeddedSectionSectionType that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
