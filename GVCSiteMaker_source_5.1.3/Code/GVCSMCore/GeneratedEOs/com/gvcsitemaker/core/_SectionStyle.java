
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SectionStyle.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SectionStyle extends com.gvcsitemaker.core.Style
{


    public static final String SECTIONSTYLE_ENTITY_NAME = "SectionStyle";

    public static final String BANNERCOMPONENT = "bannerComponent";
    public static final String DEFAULTSTYLE = "defaultStyle";
    public static final String FOOTERCOMPONENT = "footerComponent";
    public static final String NAVBARCOMPONENT = "navBarComponent";
    public static final String PAGESCAFFOLDCOMPONENT = "pageScaffoldComponent";
    public static final String RELATEDCSSURL = "relatedCSSUrl";
    public static final String RESOURCEDIRECTORY = "resourceDirectory";
    public static final String SECTIONCONTENTSCOMPONENT = "sectionContentsComponent";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SectionStyle");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SectionStyle localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SectionStyle)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String bannerComponent() {
        return (String) storedValueForKey("bannerComponent");
    }



    public void setBannerComponent(String value) 
    {
        takeStoredValueForKey(value, "bannerComponent");
    }
    
    

    /**
     *
     * @return String     
     */
    public String defaultStyle() {
        return (String) storedValueForKey("defaultStyle");
    }



    public void setDefaultStyle(String value) 
    {
        takeStoredValueForKey(value, "defaultStyle");
    }
    
    

    /**
     *
     * @return String     
     */
    public String footerComponent() {
        return (String) storedValueForKey("footerComponent");
    }



    public void setFooterComponent(String value) 
    {
        takeStoredValueForKey(value, "footerComponent");
    }
    
    

    /**
     *
     * @return String     
     */
    public String navBarComponent() {
        return (String) storedValueForKey("navBarComponent");
    }



    public void setNavBarComponent(String value) 
    {
        takeStoredValueForKey(value, "navBarComponent");
    }
    
    

    /**
     *
     * @return String     
     */
    public String pageScaffoldComponent() {
        return (String) storedValueForKey("pageScaffoldComponent");
    }



    public void setPageScaffoldComponent(String value) 
    {
        takeStoredValueForKey(value, "pageScaffoldComponent");
    }
    
    

    /**
     *
     * @return String     
     */
    public String relatedCSSUrl() {
        return (String) storedValueForKey("relatedCSSUrl");
    }



    public void setRelatedCSSUrl(String value) 
    {
        takeStoredValueForKey(value, "relatedCSSUrl");
    }
    
    

    /**
     *
     * @return String     
     */
    public String resourceDirectory() {
        return (String) storedValueForKey("resourceDirectory");
    }



    public void setResourceDirectory(String value) 
    {
        takeStoredValueForKey(value, "resourceDirectory");
    }
    
    

    /**
     *
     * @return String     
     */
    public String sectionContentsComponent() {
        return (String) storedValueForKey("sectionContentsComponent");
    }



    public void setSectionContentsComponent(String value) 
    {
        takeStoredValueForKey(value, "sectionContentsComponent");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of SectionStyles in editingContext
	 */
    public static NSArray fetchAllSectionStyles(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionStyle.fetchAllSectionStyles(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionStyles, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSectionStyles(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SectionStyle.fetchSectionStyles(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SectionStyles matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSectionStyles(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SECTIONSTYLE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SectionStyles where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SectionStyle fetchSectionStyle(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionStyle.fetchSectionStyle(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionStyles matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SectionStyle fetchSectionStyle(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SectionStyle.fetchSectionStyles(editingContext, qualifier, null);
        SectionStyle eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SectionStyle)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SectionStyle that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SectionStyles where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SectionStyle fetchRequiredSectionStyle(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SectionStyle.fetchRequiredSectionStyle(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SectionStyles matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SectionStyle fetchRequiredSectionStyle(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SectionStyle eoObject = _SectionStyle.fetchSectionStyle(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SectionStyle that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
