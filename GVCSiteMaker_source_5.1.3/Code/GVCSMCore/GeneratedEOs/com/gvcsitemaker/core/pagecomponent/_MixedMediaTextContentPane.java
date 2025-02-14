
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to MixedMediaTextContentPane.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _MixedMediaTextContentPane extends com.gvcsitemaker.core.pagecomponent.MixedMediaPane
{


    public static final String MIXEDMEDIATEXTCONTENTPANE_ENTITY_NAME = "MixedMediaTextContentPane";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "MixedMediaTextContentPane");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public MixedMediaTextContentPane localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (MixedMediaTextContentPane)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of MixedMediaTextContentPanes in editingContext
	 */
    public static NSArray fetchAllMixedMediaTextContentPanes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaTextContentPane.fetchAllMixedMediaTextContentPanes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaTextContentPanes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllMixedMediaTextContentPanes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaTextContentPane.fetchMixedMediaTextContentPanes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaTextContentPanes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchMixedMediaTextContentPanes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(MIXEDMEDIATEXTCONTENTPANE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of MixedMediaTextContentPanes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static MixedMediaTextContentPane fetchMixedMediaTextContentPane(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaTextContentPane.fetchMixedMediaTextContentPane(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaTextContentPanes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static MixedMediaTextContentPane fetchMixedMediaTextContentPane(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _MixedMediaTextContentPane.fetchMixedMediaTextContentPanes(editingContext, qualifier, null);
        MixedMediaTextContentPane eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (MixedMediaTextContentPane)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one MixedMediaTextContentPane that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of MixedMediaTextContentPanes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static MixedMediaTextContentPane fetchRequiredMixedMediaTextContentPane(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaTextContentPane.fetchRequiredMixedMediaTextContentPane(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaTextContentPanes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static MixedMediaTextContentPane fetchRequiredMixedMediaTextContentPane(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        MixedMediaTextContentPane eoObject = _MixedMediaTextContentPane.fetchMixedMediaTextContentPane(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no MixedMediaTextContentPane that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
