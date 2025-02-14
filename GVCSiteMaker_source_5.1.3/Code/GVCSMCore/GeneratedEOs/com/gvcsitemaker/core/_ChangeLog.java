
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to ChangeLog.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _ChangeLog extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String CHANGELOG_ENTITY_NAME = "ChangeLog";

    public static final String CHANGES = "changes";
    public static final String DATERECORDED = "dateRecorded";
    public static final String NOTES = "notes";

    public static final String USER = "user";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "ChangeLog");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public ChangeLog localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (ChangeLog)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String changes() {
        return (String) storedValueForKey("changes");
    }



    public void setChanges(String value) 
    {
        takeStoredValueForKey(value, "changes");
    }
    
    

    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateRecorded() {
        return (NSTimestamp) storedValueForKey("dateRecorded");
    }



    public void setDateRecorded(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateRecorded");
    }
    
    

    /**
     *
     * @return String     
     */
    public String notes() {
        return (String) storedValueForKey("notes");
    }



    public void setNotes(String value) 
    {
        takeStoredValueForKey(value, "notes");
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
	 * @return all instances of ChangeLogs in editingContext
	 */
    public static NSArray fetchAllChangeLogs(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ChangeLog.fetchAllChangeLogs(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ChangeLogs, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllChangeLogs(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _ChangeLog.fetchChangeLogs(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of ChangeLogs matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchChangeLogs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(CHANGELOG_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of ChangeLogs where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static ChangeLog fetchChangeLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ChangeLog.fetchChangeLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ChangeLogs matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static ChangeLog fetchChangeLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _ChangeLog.fetchChangeLogs(editingContext, qualifier, null);
        ChangeLog eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (ChangeLog)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one ChangeLog that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of ChangeLogs where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static ChangeLog fetchRequiredChangeLog(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _ChangeLog.fetchRequiredChangeLog(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of ChangeLogs matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static ChangeLog fetchRequiredChangeLog(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        ChangeLog eoObject = _ChangeLog.fetchChangeLog(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no ChangeLog that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
