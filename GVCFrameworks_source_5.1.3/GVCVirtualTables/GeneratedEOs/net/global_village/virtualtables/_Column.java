
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Column.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Column extends net.global_village.eofextensions.GenericRecord
{


    public static final String COLUMN_ENTITY_NAME = "Column";

    public static final String DATECREATED = "dateCreated";
    public static final String ISSYSTEMCOLUMN = "isSystemColumn";
    public static final String NAME = "name";
    public static final String RESTRICTINGVALUE = "restrictingValue";
    public static final String TEXTDESCRIPTION = "textDescription";

    public static final String REFERRINGCOLUMNS = "referringColumns";
    public static final String TABLE = "table";
    public static final String TYPE = "type";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Column");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Column localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Column)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp dateCreated() {
        return (NSTimestamp) storedValueForKey("dateCreated");
    }



    public void setDateCreated(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateCreated");
    }
    
    

    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isSystemColumn() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isSystemColumn");
    }



    public void setIsSystemColumn(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isSystemColumn");
    }
    
    

    /**
     *
     * @return String     
     */
    public String name() {
        return (String) storedValueForKey("name");
    }



    public void setName(String value) 
    {
        takeStoredValueForKey(value, "name");
    }
    
    

    /**
     *
     * @return String     
     */
    public String restrictingValue() {
        return (String) storedValueForKey("restrictingValue");
    }



    public void setRestrictingValue(String value) 
    {
        takeStoredValueForKey(value, "restrictingValue");
    }
    
    

    /**
     *
     * @return String     
     */
    public String textDescription() {
        return (String) storedValueForKey("textDescription");
    }



    public void setTextDescription(String value) 
    {
        takeStoredValueForKey(value, "textDescription");
    }
    
    

    /**
     *
     * @return net.global_village.virtualtables.Table
     
     */
    public net.global_village.virtualtables.Table table() 
    {
        return (net.global_village.virtualtables.Table)storedValueForKey("table");
    }



    public void setTable(net.global_village.virtualtables.Table value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "table");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.ColumnType
     
     */
    public net.global_village.virtualtables.ColumnType type() 
    {
        return (net.global_village.virtualtables.ColumnType)storedValueForKey("type");
    }



    public void setType(net.global_village.virtualtables.ColumnType value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "type");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.VirtualLookupColumn
     */
    public NSArray referringColumns() 
    {
        return (NSArray)storedValueForKey("referringColumns");
    }



    public void setReferringColumns(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.VirtualLookupColumn)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "referringColumns");
    }



    public void addToReferringColumns(net.global_village.virtualtables.VirtualLookupColumn object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_referringColumns] ! referringColumns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)referringColumns();

        willChange();
        array.addObject(object);
        /** ensure [added_to_referringColumns] referringColumns().containsObject(object);        **/
    }



    public void removeFromReferringColumns(net.global_village.virtualtables.VirtualLookupColumn object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_referringColumns] referringColumns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)referringColumns();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_referringColumns] ! referringColumns().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray referringColumns(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(net.global_village.virtualtables.VirtualLookupColumn.LOOKUPCOLUMN, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = net.global_village.virtualtables.VirtualLookupColumn.fetchVirtualLookupColumns(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = referringColumns();
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
	 * @return all instances of Columns in editingContext
	 */
    public static NSArray fetchAllColumns(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Column.fetchAllColumns(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Columns, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllColumns(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Column.fetchColumns(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Columns matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchColumns(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(COLUMN_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Columns where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Column fetchColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Column.fetchColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Columns matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Column fetchColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Column.fetchColumns(editingContext, qualifier, null);
        Column eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Column)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Column that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Columns where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Column fetchRequiredColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Column.fetchRequiredColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Columns matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Column fetchRequiredColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Column eoObject = _Column.fetchColumn(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Column that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
