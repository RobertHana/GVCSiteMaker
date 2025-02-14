
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to LDAPGroup.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _LDAPGroup extends com.gvcsitemaker.core.WebsiteGroup
{


    public static final String LDAPGROUP_ENTITY_NAME = "LDAPGroup";

    public static final String LDAPGROUPNAME = "ldapGroupName";

    public static final String LDAPBRANCH = "ldapBranch";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "LDAPGroup");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public LDAPGroup localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (LDAPGroup)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String ldapGroupName() {
        return (String) storedValueForKey("ldapGroupName");
    }



    public void setLdapGroupName(String value) 
    {
        takeStoredValueForKey(value, "ldapGroupName");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.LDAPBranch
     
     */
    public com.gvcsitemaker.core.LDAPBranch ldapBranch() 
    {
        return (com.gvcsitemaker.core.LDAPBranch)storedValueForKey("ldapBranch");
    }



    public void setLdapBranch(com.gvcsitemaker.core.LDAPBranch value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "ldapBranch");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of LDAPGroups in editingContext
	 */
    public static NSArray fetchAllLDAPGroups(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _LDAPGroup.fetchAllLDAPGroups(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of LDAPGroups, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllLDAPGroups(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _LDAPGroup.fetchLDAPGroups(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of LDAPGroups matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchLDAPGroups(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(LDAPGROUP_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of LDAPGroups where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static LDAPGroup fetchLDAPGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _LDAPGroup.fetchLDAPGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of LDAPGroups matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static LDAPGroup fetchLDAPGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _LDAPGroup.fetchLDAPGroups(editingContext, qualifier, null);
        LDAPGroup eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (LDAPGroup)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one LDAPGroup that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of LDAPGroups where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static LDAPGroup fetchRequiredLDAPGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _LDAPGroup.fetchRequiredLDAPGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of LDAPGroups matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static LDAPGroup fetchRequiredLDAPGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        LDAPGroup eoObject = _LDAPGroup.fetchLDAPGroup(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no LDAPGroup that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
