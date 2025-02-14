
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to OrgUnit.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _OrgUnit extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String ORGUNIT_ENTITY_NAME = "OrgUnit";

    public static final String DEPTH = "depth";
    public static final String FILESIZEQUOTA = "fileSizeQuota";
    public static final String FILESIZEUSAGE = "fileSizeUsage";
    public static final String INPUBLICLIST = "inPublicList";
    public static final String UNITNAME = "unitName";
    public static final String WEBSITECREATIONMESSAGE = "websiteCreationMessage";

    public static final String ADMINS = "admins";
    public static final String CHILDORGUNITS = "childOrgUnits";
    public static final String ORGUNITUSERS = "orgUnitUsers";
    public static final String PARENTORGUNIT = "parentOrgUnit";
    public static final String WEBSITES = "websites";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "OrgUnit");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public OrgUnit localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (OrgUnit)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return Number     
     */
    public Number depth() {
        return (Number) storedValueForKey("depth");
    }



    public void setDepth(Number value) 
    {
        takeStoredValueForKey(value, "depth");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number fileSizeQuota() {
        return (Number) storedValueForKey("fileSizeQuota");
    }



    public void setFileSizeQuota(Number value) 
    {
        takeStoredValueForKey(value, "fileSizeQuota");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number fileSizeUsage() {
        return (Number) storedValueForKey("fileSizeUsage");
    }



    public void setFileSizeUsage(Number value) 
    {
        takeStoredValueForKey(value, "fileSizeUsage");
    }
    
    

    /**
     *
     * @return String     
     */
    public String inPublicList() {
        return (String) storedValueForKey("inPublicList");
    }



    public void setInPublicList(String value) 
    {
        takeStoredValueForKey(value, "inPublicList");
    }
    
    

    /**
     *
     * @return String     
     */
    public String unitName() {
        return (String) storedValueForKey("unitName");
    }



    public void setUnitName(String value) 
    {
        takeStoredValueForKey(value, "unitName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String websiteCreationMessage() {
        return (String) storedValueForKey("websiteCreationMessage");
    }



    public void setWebsiteCreationMessage(String value) 
    {
        takeStoredValueForKey(value, "websiteCreationMessage");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit parentOrgUnit() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("parentOrgUnit");
    }



    public void setParentOrgUnit(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "parentOrgUnit");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     */
    public NSArray admins() 
    {
        return (NSArray)storedValueForKey("admins");
    }



    public void setAdmins(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.User)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "admins");
    }



    public void addToAdmins(com.gvcsitemaker.core.User object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_admins] ! admins().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)admins();

        willChange();
        array.addObject(object);
        /** ensure [added_to_admins] admins().containsObject(object);        **/
    }



    public void removeFromAdmins(com.gvcsitemaker.core.User object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_admins] admins().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)admins();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_admins] ! admins().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray admins(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = admins();
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
    public NSArray childOrgUnits() 
    {
        return (NSArray)storedValueForKey("childOrgUnits");
    }



    public void setChildOrgUnits(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.OrgUnit)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "childOrgUnits");
    }



    public void addToChildOrgUnits(com.gvcsitemaker.core.OrgUnit object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_childOrgUnits] ! childOrgUnits().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childOrgUnits();

        willChange();
        array.addObject(object);
        /** ensure [added_to_childOrgUnits] childOrgUnits().containsObject(object);        **/
    }



    public void removeFromChildOrgUnits(com.gvcsitemaker.core.OrgUnit object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_childOrgUnits] childOrgUnits().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childOrgUnits();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_childOrgUnits] ! childOrgUnits().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray childOrgUnits(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.OrgUnit.PARENTORGUNIT, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.OrgUnit.fetchOrgUnits(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = childOrgUnits();
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
     *
     * @return com.webobjects.eocontrol.EOGenericRecord
     */
    public NSArray orgUnitUsers() 
    {
        return (NSArray)storedValueForKey("orgUnitUsers");
    }



    public void setOrgUnitUsers(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.webobjects.eocontrol.EOGenericRecord)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "orgUnitUsers");
    }



    public void addToOrgUnitUsers(com.webobjects.eocontrol.EOGenericRecord object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_orgUnitUsers] ! orgUnitUsers().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)orgUnitUsers();

        willChange();
        array.addObject(object);
        /** ensure [added_to_orgUnitUsers] orgUnitUsers().containsObject(object);        **/
    }



    public void removeFromOrgUnitUsers(com.webobjects.eocontrol.EOGenericRecord object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_orgUnitUsers] orgUnitUsers().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)orgUnitUsers();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_orgUnitUsers] ! orgUnitUsers().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray orgUnitUsers(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier("orgUnit", EOQualifier.QualifierOperatorEqual, this);
    	
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

          EOFetchSpecification fetchSpec = new EOFetchSpecification("OrgUnitUser", qualifier, sortOrderings);
          fetchSpec.setIsDeep(true);
          results = editingContext().objectsWithFetchSpecification(fetchSpec);
      }
      else 
      {
          results = orgUnitUsers();
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
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.Website.PARENTORGUNIT, EOQualifier.QualifierOperatorEqual, this);
    	
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
	 * @return all instances of OrgUnits in editingContext
	 */
    public static NSArray fetchAllOrgUnits(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _OrgUnit.fetchAllOrgUnits(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of OrgUnits, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllOrgUnits(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _OrgUnit.fetchOrgUnits(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of OrgUnits matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchOrgUnits(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(ORGUNIT_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of OrgUnits where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static OrgUnit fetchOrgUnit(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _OrgUnit.fetchOrgUnit(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of OrgUnits matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static OrgUnit fetchOrgUnit(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _OrgUnit.fetchOrgUnits(editingContext, qualifier, null);
        OrgUnit eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (OrgUnit)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one OrgUnit that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of OrgUnits where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static OrgUnit fetchRequiredOrgUnit(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _OrgUnit.fetchRequiredOrgUnit(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of OrgUnits matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static OrgUnit fetchRequiredOrgUnit(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        OrgUnit eoObject = _OrgUnit.fetchOrgUnit(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no OrgUnit that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
