
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to EmbeddedSiteSectionType.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _EmbeddedSiteSectionType extends com.gvcsitemaker.core.SectionType
{


    public static final String EMBEDDEDSITESECTIONTYPE_ENTITY_NAME = "EmbeddedSiteSectionType";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "EmbeddedSiteSectionType");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public EmbeddedSiteSectionType localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (EmbeddedSiteSectionType)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of EmbeddedSiteSectionTypes in editingContext
	 */
    public static NSArray fetchAllEmbeddedSiteSectionTypes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSiteSectionType.fetchAllEmbeddedSiteSectionTypes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSiteSectionTypes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllEmbeddedSiteSectionTypes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSiteSectionType.fetchEmbeddedSiteSectionTypes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSiteSectionTypes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchEmbeddedSiteSectionTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(EMBEDDEDSITESECTIONTYPE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of EmbeddedSiteSectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static EmbeddedSiteSectionType fetchEmbeddedSiteSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSiteSectionType.fetchEmbeddedSiteSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSiteSectionTypes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static EmbeddedSiteSectionType fetchEmbeddedSiteSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _EmbeddedSiteSectionType.fetchEmbeddedSiteSectionTypes(editingContext, qualifier, null);
        EmbeddedSiteSectionType eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (EmbeddedSiteSectionType)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one EmbeddedSiteSectionType that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of EmbeddedSiteSectionTypes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static EmbeddedSiteSectionType fetchRequiredEmbeddedSiteSectionType(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSiteSectionType.fetchRequiredEmbeddedSiteSectionType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSiteSectionTypes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static EmbeddedSiteSectionType fetchRequiredEmbeddedSiteSectionType(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        EmbeddedSiteSectionType eoObject = _EmbeddedSiteSectionType.fetchEmbeddedSiteSectionType(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no EmbeddedSiteSectionType that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
