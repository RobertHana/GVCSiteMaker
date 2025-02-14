
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to User.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _User extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String USER_ENTITY_NAME = "User";

    public static final String PASSWORD = "password";
    public static final String USERID = "userID";

    public static final String GROUPS = "groups";
    public static final String ORGUNITSTOADMIN = "orgUnitsToAdmin";
    public static final String ORGUNITTOADMINSTYLESFOR = "orgUnitToAdminStylesFor";
    public static final String WEBSITES = "websites";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "User");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public User localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (User)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
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
     *
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit orgUnitToAdminStylesFor() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("orgUnitToAdminStylesFor");
    }



    public void setOrgUnitToAdminStylesFor(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "orgUnitToAdminStylesFor");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Group
     */
    public NSArray groups() 
    {
        return (NSArray)storedValueForKey("groups");
    }



    public void setGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Group)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "groups");
    }



    public void addToGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_groups] ! groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_groups] groups().containsObject(object);        **/
    }



    public void removeFromGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_groups] groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_groups] ! groups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray groups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = groups();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.OrgUnit
     */
    public NSArray orgUnitsToAdmin() 
    {
        return (NSArray)storedValueForKey("orgUnitsToAdmin");
    }



    public void setOrgUnitsToAdmin(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.OrgUnit)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "orgUnitsToAdmin");
    }



    public void addToOrgUnitsToAdmin(com.gvcsitemaker.core.OrgUnit object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_orgUnitsToAdmin] ! orgUnitsToAdmin().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)orgUnitsToAdmin();

        willChange();
        array.addObject(object);
        /** ensure [added_to_orgUnitsToAdmin] orgUnitsToAdmin().containsObject(object);        **/
    }



    public void removeFromOrgUnitsToAdmin(com.gvcsitemaker.core.OrgUnit object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_orgUnitsToAdmin] orgUnitsToAdmin().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)orgUnitsToAdmin();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_orgUnitsToAdmin] ! orgUnitsToAdmin().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray orgUnitsToAdmin(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = orgUnitsToAdmin();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.Website
     */
    public NSArray websites() 
    {
        return (NSArray)storedValueForKey("websites");
    }



    public void setWebsites(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Website)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "websites");
    }



    public void addToWebsites(com.gvcsitemaker.core.Website object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_websites] ! websites().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)websites();

        willChange();
        array.addObject(object);
        /** ensure [added_to_websites] websites().containsObject(object);        **/
    }



    public void removeFromWebsites(com.gvcsitemaker.core.Website object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_websites] websites().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)websites();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_websites] ! websites().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray websites(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.Website.OWNER, EOQualifier.QualifierOperatorEqual, this);
    	
          if (qualifier == null) 
          {
              fullQualifier = inverseQualifier;
          }
          else 
          {
              NSMutableArray qualifiers = new NSMutableArray();
              qualifiers.addObject(qualifier);
              qualifiers.addObject(inverseQualifier);
              fullQualifier = new EOAndQualifier(qualifiers);
          }

          results = com.gvcsitemaker.core.Website.fetchWebsites(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = websites();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  



	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of Users in editingContext
	 */
    public static NSArray fetchAllUsers(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _User.fetchAllUsers(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Users, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllUsers(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _User.fetchUsers(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Users matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchUsers(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(USER_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Users where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static User fetchUser(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _User.fetchUser(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Users matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static User fetchUser(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _User.fetchUsers(editingContext, qualifier, null);
        User eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (User)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one User that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Users where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static User fetchRequiredUser(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _User.fetchRequiredUser(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Users matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static User fetchRequiredUser(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        User eoObject = _User.fetchUser(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no User that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
