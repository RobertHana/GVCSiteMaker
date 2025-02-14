
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Table.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Table extends net.global_village.eofextensions.GenericRecord
{


    public static final String TABLE_ENTITY_NAME = "Table";

    public static final String DATECREATED = "dateCreated";
    public static final String DATEMODIFIED = "dateModified";
    public static final String NAME = "name";
    public static final String TABLEID = "tableID";
    public static final String TEXTDESCRIPTION = "textDescription";

    public static final String COLUMNS = "columns";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Table");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Table localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Table)EOUtilities.localInstanceOfObject(editingContext, this);
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
     * @return NSTimestamp     
     */
    public NSTimestamp dateModified() {
        return (NSTimestamp) storedValueForKey("dateModified");
    }



    public void setDateModified(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateModified");
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
     * @return Number     
     */
    public Number tableID() {
        return (Number) storedValueForKey("tableID");
    }



    public void setTableID(Number value) 
    {
        takeStoredValueForKey(value, "tableID");
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
     * @return net.global_village.virtualtables.Column
     */
    public NSArray columns() 
    {
        return (NSArray)storedValueForKey("columns");
    }



    public void setColumns(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.Column)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "columns");
    }



    public void addToColumns(net.global_village.virtualtables.Column object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_columns] ! columns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)columns();

        willChange();
        array.addObject(object);
        /** ensure [added_to_columns] columns().containsObject(object);        **/
    }



    public void removeFromColumns(net.global_village.virtualtables.Column object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_columns] columns().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)columns();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_columns] ! columns().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray columns(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(net.global_village.virtualtables.Column.TABLE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = net.global_village.virtualtables.Column.fetchColumns(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = columns();
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
	 * @return all instances of Tables in editingContext
	 */
    public static NSArray fetchAllTables(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Table.fetchAllTables(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Tables, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllTables(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Table.fetchTables(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Tables matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchTables(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(TABLE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Tables where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Table fetchTable(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Table.fetchTable(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Tables matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Table fetchTable(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Table.fetchTables(editingContext, qualifier, null);
        Table eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Table)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Table that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Tables where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Table fetchRequiredTable(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Table.fetchRequiredTable(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Tables matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Table fetchRequiredTable(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Table eoObject = _Table.fetchTable(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Table that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
