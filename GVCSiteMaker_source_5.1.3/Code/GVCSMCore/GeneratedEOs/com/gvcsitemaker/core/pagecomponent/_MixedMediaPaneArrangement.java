
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to MixedMediaPaneArrangement.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _MixedMediaPaneArrangement extends com.gvcsitemaker.core.pagecomponent.Layout
{


    public static final String MIXEDMEDIAPANEARRANGEMENT_ENTITY_NAME = "MixedMediaPaneArrangement";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "MixedMediaPaneArrangement");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public MixedMediaPaneArrangement localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (MixedMediaPaneArrangement)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of MixedMediaPaneArrangements in editingContext
	 */
    public static NSArray fetchAllMixedMediaPaneArrangements(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaPaneArrangement.fetchAllMixedMediaPaneArrangements(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaPaneArrangements, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllMixedMediaPaneArrangements(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaPaneArrangement.fetchMixedMediaPaneArrangements(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaPaneArrangements matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchMixedMediaPaneArrangements(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(MIXEDMEDIAPANEARRANGEMENT_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of MixedMediaPaneArrangements where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static MixedMediaPaneArrangement fetchMixedMediaPaneArrangement(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaPaneArrangement.fetchMixedMediaPaneArrangement(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaPaneArrangements matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static MixedMediaPaneArrangement fetchMixedMediaPaneArrangement(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _MixedMediaPaneArrangement.fetchMixedMediaPaneArrangements(editingContext, qualifier, null);
        MixedMediaPaneArrangement eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (MixedMediaPaneArrangement)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one MixedMediaPaneArrangement that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of MixedMediaPaneArrangements where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static MixedMediaPaneArrangement fetchRequiredMixedMediaPaneArrangement(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaPaneArrangement.fetchRequiredMixedMediaPaneArrangement(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaPaneArrangements matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static MixedMediaPaneArrangement fetchRequiredMixedMediaPaneArrangement(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        MixedMediaPaneArrangement eoObject = _MixedMediaPaneArrangement.fetchMixedMediaPaneArrangement(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no MixedMediaPaneArrangement that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
