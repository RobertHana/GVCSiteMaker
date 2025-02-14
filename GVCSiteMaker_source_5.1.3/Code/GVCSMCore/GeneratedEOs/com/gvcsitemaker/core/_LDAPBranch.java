
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to LDAPBranch.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _LDAPBranch extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String LDAPBRANCH_ENTITY_NAME = "LDAPBranch";

    public static final String DISPLAYNAME = "displayName";
    public static final String DISPLAYORDER = "displayOrder";
    public static final String GROUPSUBTREE = "groupSubTree";
    public static final String INSTRUCTIONS = "instructions";
    public static final String LDAPENTITYNAME = "ldapEntityName";
    public static final String USERIDATTRIBUTE = "userIDAttribute";
    public static final String USERSUBTREE = "userSubTree";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "LDAPBranch");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public LDAPBranch localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (LDAPBranch)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String displayName() {
        return (String) storedValueForKey("displayName");
    }



    public void setDisplayName(String value) 
    {
        takeStoredValueForKey(value, "displayName");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number displayOrder() {
        return (Number) storedValueForKey("displayOrder");
    }



    public void setDisplayOrder(Number value) 
    {
        takeStoredValueForKey(value, "displayOrder");
    }
    
    

    /**
     *
     * @return String     
     */
    public String groupSubTree() {
        return (String) storedValueForKey("groupSubTree");
    }



    public void setGroupSubTree(String value) 
    {
        takeStoredValueForKey(value, "groupSubTree");
    }
    
    

    /**
     *
     * @return String     
     */
    public String instructions() {
        return (String) storedValueForKey("instructions");
    }



    public void setInstructions(String value) 
    {
        takeStoredValueForKey(value, "instructions");
    }
    
    

    /**
     *
     * @return String     
     */
    public String ldapEntityName() {
        return (String) storedValueForKey("ldapEntityName");
    }



    public void setLdapEntityName(String value) 
    {
        takeStoredValueForKey(value, "ldapEntityName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String userIDAttribute() {
        return (String) storedValueForKey("userIDAttribute");
    }



    public void setUserIDAttribute(String value) 
    {
        takeStoredValueForKey(value, "userIDAttribute");
    }
    
    

    /**
     *
     * @return String     
     */
    public String userSubTree() {
        return (String) storedValueForKey("userSubTree");
    }



    public void setUserSubTree(String value) 
    {
        takeStoredValueForKey(value, "userSubTree");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of LDAPBranchs in editingContext
	 */
    public static NSArray fetchAllLDAPBranchs(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _LDAPBranch.fetchAllLDAPBranchs(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of LDAPBranchs, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllLDAPBranchs(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _LDAPBranch.fetchLDAPBranchs(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of LDAPBranchs matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchLDAPBranchs(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(LDAPBRANCH_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of LDAPBranchs where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static LDAPBranch fetchLDAPBranch(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _LDAPBranch.fetchLDAPBranch(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of LDAPBranchs matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static LDAPBranch fetchLDAPBranch(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _LDAPBranch.fetchLDAPBranchs(editingContext, qualifier, null);
        LDAPBranch eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (LDAPBranch)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one LDAPBranch that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of LDAPBranchs where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static LDAPBranch fetchRequiredLDAPBranch(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _LDAPBranch.fetchRequiredLDAPBranch(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of LDAPBranchs matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static LDAPBranch fetchRequiredLDAPBranch(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        LDAPBranch eoObject = _LDAPBranch.fetchLDAPBranch(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no LDAPBranch that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
