
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to ErrorLog.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _ErrorLog extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String ERRORLOG_ENTITY_NAME = "ErrorLog";

    public static final String APPNAME = "appName";
    public static final String COMPONENTNAME = "componentName";
    public static final String EVENTDATE = "eventDate";
    public static final String EXCEPTIONMESSAGE = "exceptionMessage";
    public static final String HOSTNAME = "hostName";
    public static final String LOGTYPE = "logType";
    public static final String OID = "oid";
    public static final String PAGENAME = "pageName";
    public static final String REQUEST = "request";
    public static final String STACKTRACE = "stackTrace";
    public static final String USERID = "userID";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "ErrorLog");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public ErrorLog localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (ErrorLog)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String appName() {
        return (String) storedValueForKey("appName");
    }



    public void setAppName(String value) 
    {
        takeStoredValueForKey(value, "appName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String componentName() {
        return (String) storedValueForKey("componentName");
    }



    public void setComponentName(String value) 
    {
        takeStoredValueForKey(value, "componentName");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp eventDate() {
        return (NSTimestamp) storedValueForKey("eventDate");
    }



    public void setEventDate(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "eventDate");
    }
    
    

    /**
     *
     * @return String     
     */
    public String exceptionMessage() {
        return (String) storedValueForKey("exceptionMessage");
    }



    public void setExceptionMessage(String value) 
    {
        takeStoredValueForKey(value, "exceptionMessage");
    }
    
    

    /**
     *
     * @return String     
     */
    public String hostName() {
        return (String) storedValueForKey("hostName");
    }



    public void setHostName(String value) 
    {
        takeStoredValueForKey(value, "hostName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String logType() {
        return (String) storedValueForKey("logType");
    }



    public void setLogType(String value) 
    {
        takeStoredValueForKey(value, "logType");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number oid() {
        return (Number) storedValueForKey("oid");
    }



    public void setOid(Number value) 
    {
        takeStoredValueForKey(value, "oid");
    }
    
    

    /**
     *
     * @return String     
     */
    public String pageName() {
        return (String) storedValueForKey("pageName");
    }



    public void setPageName(String value) 
    {
        takeStoredValueForKey(value, "pageName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String request() {
        return (String) storedValueForKey("request");
    }



    public void setRequest(String value) 
    {
        takeStoredValueForKey(value, "request");
    }
    
    

    /**
     *
     * @return String     
     */
    public String stackTrace() {
        return (String) storedValueForKey("stackTrace");
    }



    public void setStackTrace(String value) 
    {
        takeStoredValueForKey(value, "stackTrace");
    }
    
    

    /**
     *
     * @return String     
     */
    public String userID() {
        return (String) storedValueForKey("userID");
    }



    public void setUserID(String value) 
    {
        takeStoredValueForKey(value, "userID");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of ErrorLogs in editingContext
	 */
    public static NSArray fetchAllErrorLogs(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ErrorLog.fetchAllErrorLogs(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ErrorLogs, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllErrorLogs(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ErrorLog.fetchErrorLogs(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ErrorLogs matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchErrorLogs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(ERRORLOG_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of ErrorLogs where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static ErrorLog fetchErrorLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ErrorLog.fetchErrorLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ErrorLogs matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static ErrorLog fetchErrorLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _ErrorLog.fetchErrorLogs(editingContext, qualifier, null);
        ErrorLog eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (ErrorLog)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one ErrorLog that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of ErrorLogs where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static ErrorLog fetchRequiredErrorLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ErrorLog.fetchRequiredErrorLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ErrorLogs matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static ErrorLog fetchRequiredErrorLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        ErrorLog eoObject = _ErrorLog.fetchErrorLog(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no ErrorLog that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
