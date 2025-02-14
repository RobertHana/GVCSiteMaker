
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to FilePassword.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _FilePassword extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String FILEPASSWORD_ENTITY_NAME = "FilePassword";

    public static final String EXPIRATIONDATE = "expirationDate";
    public static final String PASSWORD = "password";

    public static final String SITEFILE = "siteFile";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "FilePassword");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public FilePassword localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (FilePassword)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp expirationDate() {
        return (NSTimestamp) storedValueForKey("expirationDate");
    }



    public void setExpirationDate(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "expirationDate");
    }
    
    

    /**
     *
     * @return String     
     */
    public String password() {
        return (String) storedValueForKey("password");
    }



    public void setPassword(String value) 
    {
        takeStoredValueForKey(value, "password");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.SiteFile
     
     */
    public com.gvcsitemaker.core.SiteFile siteFile() 
    {
        return (com.gvcsitemaker.core.SiteFile)storedValueForKey("siteFile");
    }



    public void setSiteFile(com.gvcsitemaker.core.SiteFile value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "siteFile");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of FilePasswords in editingContext
	 */
    public static NSArray fetchAllFilePasswords(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _FilePassword.fetchAllFilePasswords(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of FilePasswords, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllFilePasswords(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _FilePassword.fetchFilePasswords(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of FilePasswords matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchFilePasswords(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(FILEPASSWORD_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of FilePasswords where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static FilePassword fetchFilePassword(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _FilePassword.fetchFilePassword(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of FilePasswords matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static FilePassword fetchFilePassword(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _FilePassword.fetchFilePasswords(editingContext, qualifier, null);
        FilePassword eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (FilePassword)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one FilePassword that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of FilePasswords where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static FilePassword fetchRequiredFilePassword(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _FilePassword.fetchRequiredFilePassword(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of FilePasswords matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static FilePassword fetchRequiredFilePassword(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        FilePassword eoObject = _FilePassword.fetchFilePassword(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no FilePassword that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
