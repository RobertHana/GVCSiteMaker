
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to ListOfLinksLayout.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _ListOfLinksLayout extends com.gvcsitemaker.core.pagecomponent.Layout
{


    public static final String LISTOFLINKSLAYOUT_ENTITY_NAME = "ListOfLinksLayout";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "ListOfLinksLayout");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public ListOfLinksLayout localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (ListOfLinksLayout)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of ListOfLinksLayouts in editingContext
	 */
    public static NSArray fetchAllListOfLinksLayouts(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ListOfLinksLayout.fetchAllListOfLinksLayouts(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ListOfLinksLayouts, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllListOfLinksLayouts(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ListOfLinksLayout.fetchListOfLinksLayouts(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ListOfLinksLayouts matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchListOfLinksLayouts(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(LISTOFLINKSLAYOUT_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of ListOfLinksLayouts where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static ListOfLinksLayout fetchListOfLinksLayout(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ListOfLinksLayout.fetchListOfLinksLayout(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ListOfLinksLayouts matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static ListOfLinksLayout fetchListOfLinksLayout(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _ListOfLinksLayout.fetchListOfLinksLayouts(editingContext, qualifier, null);
        ListOfLinksLayout eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (ListOfLinksLayout)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one ListOfLinksLayout that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of ListOfLinksLayouts where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static ListOfLinksLayout fetchRequiredListOfLinksLayout(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ListOfLinksLayout.fetchRequiredListOfLinksLayout(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ListOfLinksLayouts matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static ListOfLinksLayout fetchRequiredListOfLinksLayout(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        ListOfLinksLayout eoObject = _ListOfLinksLayout.fetchListOfLinksLayout(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no ListOfLinksLayout that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
