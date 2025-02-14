
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to UsageLog.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _UsageLog extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String USAGELOG_ENTITY_NAME = "UsageLog";

    public static final String ACCESSTIME = "accessTime";

    public static final String USER = "user";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "UsageLog");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public UsageLog localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (UsageLog)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp accessTime() {
        return (NSTimestamp) storedValueForKey("accessTime");
    }



    public void setAccessTime(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "accessTime");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User user() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("user");
    }



    public void setUser(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "user");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of UsageLogs in editingContext
	 */
    public static NSArray fetchAllUsageLogs(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _UsageLog.fetchAllUsageLogs(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of UsageLogs, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllUsageLogs(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _UsageLog.fetchUsageLogs(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of UsageLogs matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchUsageLogs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(USAGELOG_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of UsageLogs where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static UsageLog fetchUsageLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _UsageLog.fetchUsageLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of UsageLogs matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static UsageLog fetchUsageLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _UsageLog.fetchUsageLogs(editingContext, qualifier, null);
        UsageLog eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (UsageLog)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one UsageLog that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of UsageLogs where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static UsageLog fetchRequiredUsageLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _UsageLog.fetchRequiredUsageLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of UsageLogs matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static UsageLog fetchRequiredUsageLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        UsageLog eoObject = _UsageLog.fetchUsageLog(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no UsageLog that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
