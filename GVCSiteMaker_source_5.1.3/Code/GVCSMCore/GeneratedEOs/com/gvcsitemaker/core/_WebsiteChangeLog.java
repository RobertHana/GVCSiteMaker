
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to WebsiteChangeLog.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _WebsiteChangeLog extends com.gvcsitemaker.core.ChangeLog
{


    public static final String WEBSITECHANGELOG_ENTITY_NAME = "WebsiteChangeLog";

    public static final String WEBSITEFKEY = "websiteFKey";

    public static final String WEBSITE = "website";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "WebsiteChangeLog");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public WebsiteChangeLog localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (WebsiteChangeLog)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return Number     
     */
    public Number websiteFKey() {
        return (Number) storedValueForKey("websiteFKey");
    }



    public void setWebsiteFKey(Number value) 
    {
        takeStoredValueForKey(value, "websiteFKey");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.Website
     
     */
    public com.gvcsitemaker.core.Website website() 
    {
        return (com.gvcsitemaker.core.Website)storedValueForKey("website");
    }



    public void setWebsite(com.gvcsitemaker.core.Website value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "website");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of WebsiteChangeLogs in editingContext
	 */
    public static NSArray fetchAllWebsiteChangeLogs(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteChangeLog.fetchAllWebsiteChangeLogs(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteChangeLogs, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllWebsiteChangeLogs(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteChangeLog.fetchWebsiteChangeLogs(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteChangeLogs matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchWebsiteChangeLogs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(WEBSITECHANGELOG_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of WebsiteChangeLogs where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static WebsiteChangeLog fetchWebsiteChangeLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteChangeLog.fetchWebsiteChangeLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteChangeLogs matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static WebsiteChangeLog fetchWebsiteChangeLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _WebsiteChangeLog.fetchWebsiteChangeLogs(editingContext, qualifier, null);
        WebsiteChangeLog eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (WebsiteChangeLog)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one WebsiteChangeLog that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of WebsiteChangeLogs where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static WebsiteChangeLog fetchRequiredWebsiteChangeLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteChangeLog.fetchRequiredWebsiteChangeLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteChangeLogs matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static WebsiteChangeLog fetchRequiredWebsiteChangeLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        WebsiteChangeLog eoObject = _WebsiteChangeLog.fetchWebsiteChangeLog(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no WebsiteChangeLog that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
