
package com.gvcsitemaker.core.databasetables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to SMVirtualTable.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _SMVirtualTable extends net.global_village.virtualtables.VirtualTable
{


    public static final String SMVIRTUALTABLE_ENTITY_NAME = "SMVirtualTable";


    public static final String CREATEDBY = "createdBy";
    public static final String MODIFIEDBY = "modifiedBy";
    public static final String WEBSITES = "websites";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "SMVirtualTable");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public SMVirtualTable localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (SMVirtualTable)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User createdBy() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("createdBy");
    }



    public void setCreatedBy(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "createdBy");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User modifiedBy() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("modifiedBy");
    }



    public void setModifiedBy(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "modifiedBy");
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

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray websites(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = websites();
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
	 * @param editingContext EC to return objects in
	 * @return all instances of SMVirtualTables in editingContext
	 */
    public static NSArray fetchAllSMVirtualTables(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SMVirtualTable.fetchAllSMVirtualTables(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SMVirtualTables, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSMVirtualTables(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _SMVirtualTable.fetchSMVirtualTables(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of SMVirtualTables matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSMVirtualTables(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SMVIRTUALTABLE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of SMVirtualTables where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static SMVirtualTable fetchSMVirtualTable(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SMVirtualTable.fetchSMVirtualTable(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SMVirtualTables matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static SMVirtualTable fetchSMVirtualTable(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _SMVirtualTable.fetchSMVirtualTables(editingContext, qualifier, null);
        SMVirtualTable eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (SMVirtualTable)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one SMVirtualTable that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of SMVirtualTables where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static SMVirtualTable fetchRequiredSMVirtualTable(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _SMVirtualTable.fetchRequiredSMVirtualTable(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of SMVirtualTables matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static SMVirtualTable fetchRequiredSMVirtualTable(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        SMVirtualTable eoObject = _SMVirtualTable.fetchSMVirtualTable(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no SMVirtualTable that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
