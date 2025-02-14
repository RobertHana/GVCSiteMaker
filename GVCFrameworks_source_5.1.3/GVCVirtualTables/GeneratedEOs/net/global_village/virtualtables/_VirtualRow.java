
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to VirtualRow.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _VirtualRow extends net.global_village.eofextensions.GenericRecord
{


    public static final String VIRTUALROW_ENTITY_NAME = "VirtualRow";

    public static final String VIRTUALROWID = "virtualRowID";

    public static final String FIELDS = "fields";
    public static final String TABLE = "table";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "VirtualRow");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public VirtualRow localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (VirtualRow)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return Number     
     */
    public Number virtualRowID() {
        return (Number) storedValueForKey("virtualRowID");
    }



    public void setVirtualRowID(Number value) 
    {
        takeStoredValueForKey(value, "virtualRowID");
    }
    
    

    /**
     *
     * @return net.global_village.virtualtables.VirtualTable
     
     */
    public net.global_village.virtualtables.VirtualTable table() 
    {
        return (net.global_village.virtualtables.VirtualTable)storedValueForKey("table");
    }



    public void setTable(net.global_village.virtualtables.VirtualTable value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "table");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.VirtualField
     */
    public NSArray fields() 
    {
        return (NSArray)storedValueForKey("fields");
    }



    public void setFields(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.VirtualField)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "fields");
    }



    public void addToFields(net.global_village.virtualtables.VirtualField object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_fields] ! fields().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)fields();

        willChange();
        array.addObject(object);
        /** ensure [added_to_fields] fields().containsObject(object);        **/
    }



    public void removeFromFields(net.global_village.virtualtables.VirtualField object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_fields] fields().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)fields();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_fields] ! fields().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray fields(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(net.global_village.virtualtables.VirtualField.ROW, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = net.global_village.virtualtables.VirtualField.fetchVirtualFields(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = fields();
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
	 * @return all instances of VirtualRows in editingContext
	 */
    public static NSArray fetchAllVirtualRows(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualRow.fetchAllVirtualRows(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualRows, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllVirtualRows(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualRow.fetchVirtualRows(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualRows matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchVirtualRows(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(VIRTUALROW_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of VirtualRows where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static VirtualRow fetchVirtualRow(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualRow.fetchVirtualRow(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualRows matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static VirtualRow fetchVirtualRow(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _VirtualRow.fetchVirtualRows(editingContext, qualifier, null);
        VirtualRow eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (VirtualRow)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one VirtualRow that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of VirtualRows where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static VirtualRow fetchRequiredVirtualRow(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualRow.fetchRequiredVirtualRow(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualRows matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static VirtualRow fetchRequiredVirtualRow(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        VirtualRow eoObject = _VirtualRow.fetchVirtualRow(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no VirtualRow that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
