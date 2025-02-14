
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to MixedMediaConfigurableContentPane.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _MixedMediaConfigurableContentPane extends com.gvcsitemaker.core.pagecomponent.MixedMediaPane
{


    public static final String MIXEDMEDIACONFIGURABLECONTENTPANE_ENTITY_NAME = "MixedMediaConfigurableContentPane";


    public static final String RELATEDSECTION = "relatedSection";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "MixedMediaConfigurableContentPane");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public MixedMediaConfigurableContentPane localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (MixedMediaConfigurableContentPane)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.Section
     
     */
    public com.gvcsitemaker.core.Section relatedSection() 
    {
        return (com.gvcsitemaker.core.Section)storedValueForKey("relatedSection");
    }



    public void setRelatedSection(com.gvcsitemaker.core.Section value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "relatedSection");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of MixedMediaConfigurableContentPanes in editingContext
	 */
    public static NSArray fetchAllMixedMediaConfigurableContentPanes(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaConfigurableContentPane.fetchAllMixedMediaConfigurableContentPanes(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaConfigurableContentPanes, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllMixedMediaConfigurableContentPanes(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _MixedMediaConfigurableContentPane.fetchMixedMediaConfigurableContentPanes(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of MixedMediaConfigurableContentPanes matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchMixedMediaConfigurableContentPanes(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(MIXEDMEDIACONFIGURABLECONTENTPANE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of MixedMediaConfigurableContentPanes where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static MixedMediaConfigurableContentPane fetchMixedMediaConfigurableContentPane(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaConfigurableContentPane.fetchMixedMediaConfigurableContentPane(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaConfigurableContentPanes matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static MixedMediaConfigurableContentPane fetchMixedMediaConfigurableContentPane(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _MixedMediaConfigurableContentPane.fetchMixedMediaConfigurableContentPanes(editingContext, qualifier, null);
        MixedMediaConfigurableContentPane eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (MixedMediaConfigurableContentPane)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one MixedMediaConfigurableContentPane that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of MixedMediaConfigurableContentPanes where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static MixedMediaConfigurableContentPane fetchRequiredMixedMediaConfigurableContentPane(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _MixedMediaConfigurableContentPane.fetchRequiredMixedMediaConfigurableContentPane(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of MixedMediaConfigurableContentPanes matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static MixedMediaConfigurableContentPane fetchRequiredMixedMediaConfigurableContentPane(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        MixedMediaConfigurableContentPane eoObject = _MixedMediaConfigurableContentPane.fetchMixedMediaConfigurableContentPane(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no MixedMediaConfigurableContentPane that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
