
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to EmbeddedSite.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _EmbeddedSite extends com.gvcsitemaker.core.pagecomponent.InternalLink
{


    public static final String EMBEDDEDSITE_ENTITY_NAME = "EmbeddedSite";


    public static final String LINKEDWEBSITE = "linkedWebsite";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "EmbeddedSite");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public EmbeddedSite localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (EmbeddedSite)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.Website
     
     */
    public com.gvcsitemaker.core.Website linkedWebsite() 
    {
        return (com.gvcsitemaker.core.Website)storedValueForKey("linkedWebsite");
    }



    public void setLinkedWebsite(com.gvcsitemaker.core.Website value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "linkedWebsite");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of EmbeddedSites in editingContext
	 */
    public static NSArray fetchAllEmbeddedSites(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSite.fetchAllEmbeddedSites(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSites, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllEmbeddedSites(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _EmbeddedSite.fetchEmbeddedSites(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of EmbeddedSites matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchEmbeddedSites(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(EMBEDDEDSITE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of EmbeddedSites where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static EmbeddedSite fetchEmbeddedSite(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSite.fetchEmbeddedSite(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSites matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static EmbeddedSite fetchEmbeddedSite(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _EmbeddedSite.fetchEmbeddedSites(editingContext, qualifier, null);
        EmbeddedSite eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (EmbeddedSite)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one EmbeddedSite that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of EmbeddedSites where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static EmbeddedSite fetchRequiredEmbeddedSite(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _EmbeddedSite.fetchRequiredEmbeddedSite(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of EmbeddedSites matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static EmbeddedSite fetchRequiredEmbeddedSite(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        EmbeddedSite eoObject = _EmbeddedSite.fetchEmbeddedSite(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no EmbeddedSite that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
