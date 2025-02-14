
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Banner.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Banner extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String BANNER_ENTITY_NAME = "Banner";

    public static final String BANNERTEXT = "bannerText";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Banner");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Banner localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Banner)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String bannerText() {
        return (String) storedValueForKey("bannerText");
    }



    public void setBannerText(String value) 
    {
        takeStoredValueForKey(value, "bannerText");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of Banners in editingContext
	 */
    public static NSArray fetchAllBanners(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Banner.fetchAllBanners(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Banners, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllBanners(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Banner.fetchBanners(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Banners matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchBanners(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(BANNER_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Banners where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Banner fetchBanner(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Banner.fetchBanner(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Banners matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Banner fetchBanner(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Banner.fetchBanners(editingContext, qualifier, null);
        Banner eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Banner)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Banner that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Banners where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Banner fetchRequiredBanner(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Banner.fetchRequiredBanner(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Banners matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Banner fetchRequiredBanner(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Banner eoObject = _Banner.fetchBanner(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Banner that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
