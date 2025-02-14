
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to WebsiteRequest.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _WebsiteRequest extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String WEBSITEREQUEST_ENTITY_NAME = "WebsiteRequest";

    public static final String ADMINISTRATIVENOTES = "administrativeNotes";
    public static final String DATEREQUESTED = "dateRequested";
    public static final String DATERESOLVED = "dateResolved";
    public static final String REQUESTERNOTE = "requesterNote";
    public static final String SITEID = "siteID";

    public static final String ORGUNIT = "orgUnit";
    public static final String REQUESTER = "requester";
    public static final String WEBSITE = "website";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "WebsiteRequest");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public WebsiteRequest localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (WebsiteRequest)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String administrativeNotes() {
        return (String) storedValueForKey("administrativeNotes");
    }



    public void setAdministrativeNotes(String value) 
    {
        takeStoredValueForKey(value, "administrativeNotes");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateRequested() {
        return (NSTimestamp) storedValueForKey("dateRequested");
    }



    public void setDateRequested(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateRequested");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateResolved() {
        return (NSTimestamp) storedValueForKey("dateResolved");
    }



    public void setDateResolved(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateResolved");
    }
    
    

    /**
     *
     * @return String     
     */
    public String requesterNote() {
        return (String) storedValueForKey("requesterNote");
    }



    public void setRequesterNote(String value) 
    {
        takeStoredValueForKey(value, "requesterNote");
    }
    
    

    /**
     *
     * @return String     
     */
    public String siteID() {
        return (String) storedValueForKey("siteID");
    }



    public void setSiteID(String value) 
    {
        takeStoredValueForKey(value, "siteID");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit orgUnit() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("orgUnit");
    }



    public void setOrgUnit(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "orgUnit");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User requester() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("requester");
    }



    public void setRequester(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "requester");
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
	 * @return all instances of WebsiteRequests in editingContext
	 */
    public static NSArray fetchAllWebsiteRequests(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteRequest.fetchAllWebsiteRequests(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteRequests, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllWebsiteRequests(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _WebsiteRequest.fetchWebsiteRequests(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of WebsiteRequests matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchWebsiteRequests(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(WEBSITEREQUEST_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of WebsiteRequests where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static WebsiteRequest fetchWebsiteRequest(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteRequest.fetchWebsiteRequest(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteRequests matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static WebsiteRequest fetchWebsiteRequest(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _WebsiteRequest.fetchWebsiteRequests(editingContext, qualifier, null);
        WebsiteRequest eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (WebsiteRequest)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one WebsiteRequest that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of WebsiteRequests where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static WebsiteRequest fetchRequiredWebsiteRequest(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _WebsiteRequest.fetchRequiredWebsiteRequest(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of WebsiteRequests matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static WebsiteRequest fetchRequiredWebsiteRequest(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        WebsiteRequest eoObject = _WebsiteRequest.fetchWebsiteRequest(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no WebsiteRequest that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
