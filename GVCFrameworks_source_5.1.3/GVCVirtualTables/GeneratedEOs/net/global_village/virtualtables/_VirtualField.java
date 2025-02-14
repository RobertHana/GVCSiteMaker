
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to VirtualField.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _VirtualField extends net.global_village.eofextensions.GenericRecord
{


    public static final String VIRTUALFIELD_ENTITY_NAME = "VirtualField";

    public static final String RESTRICTINGVALUE = "restrictingValue";
    public static final String VIRTUALFIELDID = "virtualFieldID";

    public static final String COLUMN = "column";
    public static final String REFERRINGFIELDS = "referringFields";
    public static final String ROW = "row";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "VirtualField");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public VirtualField localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (VirtualField)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
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
     * @return Number     
     */
    public Number virtualFieldID() {
        return (Number) storedValueForKey("virtualFieldID");
    }



    public void setVirtualFieldID(Number value) 
    {
        takeStoredValueForKey(value, "virtualFieldID");
    }
    
    

    /**
     *
     * @return net.global_village.virtualtables.VirtualColumn
     
     */
    public net.global_village.virtualtables.VirtualColumn column() 
    {
        return (net.global_village.virtualtables.VirtualColumn)storedValueForKey("column");
    }



    public void setColumn(net.global_village.virtualtables.VirtualColumn value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "column");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.VirtualRow
     
     */
    public net.global_village.virtualtables.VirtualRow row() 
    {
        return (net.global_village.virtualtables.VirtualRow)storedValueForKey("row");
    }



    public void setRow(net.global_village.virtualtables.VirtualRow value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "row");
    }
  
  
  
    /**
     *
     * @return net.global_village.virtualtables.VirtualLookupField
     */
    public NSArray referringFields() 
    {
        return (NSArray)storedValueForKey("referringFields");
    }



    public void setReferringFields(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.VirtualLookupField)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "referringFields");
    }



    public void addToReferringFields(net.global_village.virtualtables.VirtualLookupField object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_referringFields] ! referringFields().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)referringFields();

        willChange();
        array.addObject(object);
        /** ensure [added_to_referringFields] referringFields().containsObject(object);        **/
    }



    public void removeFromReferringFields(net.global_village.virtualtables.VirtualLookupField object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_referringFields] referringFields().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)referringFields();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_referringFields] ! referringFields().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray referringFields(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(net.global_village.virtualtables.VirtualLookupField.LOOKUPVALUE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = net.global_village.virtualtables.VirtualLookupField.fetchVirtualLookupFields(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = referringFields();
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
	 * @return all instances of VirtualFields in editingContext
	 */
    public static NSArray fetchAllVirtualFields(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualField.fetchAllVirtualFields(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualFields, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllVirtualFields(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualField.fetchVirtualFields(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualFields matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchVirtualFields(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(VIRTUALFIELD_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of VirtualFields where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static VirtualField fetchVirtualField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualField.fetchVirtualField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualFields matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static VirtualField fetchVirtualField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _VirtualField.fetchVirtualFields(editingContext, qualifier, null);
        VirtualField eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (VirtualField)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one VirtualField that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of VirtualFields where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static VirtualField fetchRequiredVirtualField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualField.fetchRequiredVirtualField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualFields matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static VirtualField fetchRequiredVirtualField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        VirtualField eoObject = _VirtualField.fetchVirtualField(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no VirtualField that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
