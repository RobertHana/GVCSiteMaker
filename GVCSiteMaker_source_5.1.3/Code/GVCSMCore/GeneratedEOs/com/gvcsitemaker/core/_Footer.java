
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Footer.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Footer extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String FOOTER_ENTITY_NAME = "Footer";

    public static final String CUSTOMFOOTER = "customFooter";
    public static final String LINKTEXT1 = "linkText1";
    public static final String LINKTEXT2 = "linkText2";
    public static final String LINKTEXT3 = "linkText3";
    public static final String LINKTEXT4 = "linkText4";
    public static final String LINKURL1 = "linkURL1";
    public static final String LINKURL2 = "linkURL2";
    public static final String LINKURL3 = "linkURL3";
    public static final String LINKURL4 = "linkURL4";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Footer");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Footer localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Footer)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String customFooter() {
        return (String) storedValueForKey("customFooter");
    }



    public void setCustomFooter(String value) 
    {
        takeStoredValueForKey(value, "customFooter");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkText1() {
        return (String) storedValueForKey("linkText1");
    }



    public void setLinkText1(String value) 
    {
        takeStoredValueForKey(value, "linkText1");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkText2() {
        return (String) storedValueForKey("linkText2");
    }



    public void setLinkText2(String value) 
    {
        takeStoredValueForKey(value, "linkText2");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkText3() {
        return (String) storedValueForKey("linkText3");
    }



    public void setLinkText3(String value) 
    {
        takeStoredValueForKey(value, "linkText3");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkText4() {
        return (String) storedValueForKey("linkText4");
    }



    public void setLinkText4(String value) 
    {
        takeStoredValueForKey(value, "linkText4");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkURL1() {
        return (String) storedValueForKey("linkURL1");
    }



    public void setLinkURL1(String value) 
    {
        takeStoredValueForKey(value, "linkURL1");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkURL2() {
        return (String) storedValueForKey("linkURL2");
    }



    public void setLinkURL2(String value) 
    {
        takeStoredValueForKey(value, "linkURL2");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkURL3() {
        return (String) storedValueForKey("linkURL3");
    }



    public void setLinkURL3(String value) 
    {
        takeStoredValueForKey(value, "linkURL3");
    }
    
    

    /**
     *
     * @return String     
     */
    public String linkURL4() {
        return (String) storedValueForKey("linkURL4");
    }



    public void setLinkURL4(String value) 
    {
        takeStoredValueForKey(value, "linkURL4");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of Footers in editingContext
	 */
    public static NSArray fetchAllFooters(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Footer.fetchAllFooters(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Footers, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllFooters(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Footer.fetchFooters(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Footers matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchFooters(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(FOOTER_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Footers where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Footer fetchFooter(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Footer.fetchFooter(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Footers matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Footer fetchFooter(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Footer.fetchFooters(editingContext, qualifier, null);
        Footer eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Footer)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Footer that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Footers where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Footer fetchRequiredFooter(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Footer.fetchRequiredFooter(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Footers matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Footer fetchRequiredFooter(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Footer eoObject = _Footer.fetchFooter(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Footer that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
