
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to VirtualNumberField.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _VirtualNumberField extends net.global_village.virtualtables.VirtualField
{


    public static final String VIRTUALNUMBERFIELD_ENTITY_NAME = "VirtualNumberField";

    public static final String NUMBERVALUE = "numberValue";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "VirtualNumberField");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public VirtualNumberField localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (VirtualNumberField)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return java.math.BigDecimal     
     */
    public java.math.BigDecimal numberValue() {
        return (java.math.BigDecimal) storedValueForKey("numberValue");
    }



    public void setNumberValue(java.math.BigDecimal value) 
    {
        takeStoredValueForKey(value, "numberValue");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of VirtualNumberFields in editingContext
	 */
    public static NSArray fetchAllVirtualNumberFields(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualNumberField.fetchAllVirtualNumberFields(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualNumberFields, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllVirtualNumberFields(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualNumberField.fetchVirtualNumberFields(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualNumberFields matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchVirtualNumberFields(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(VIRTUALNUMBERFIELD_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of VirtualNumberFields where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static VirtualNumberField fetchVirtualNumberField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualNumberField.fetchVirtualNumberField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualNumberFields matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static VirtualNumberField fetchVirtualNumberField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _VirtualNumberField.fetchVirtualNumberFields(editingContext, qualifier, null);
        VirtualNumberField eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (VirtualNumberField)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one VirtualNumberField that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of VirtualNumberFields where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static VirtualNumberField fetchRequiredVirtualNumberField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualNumberField.fetchRequiredVirtualNumberField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualNumberFields matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static VirtualNumberField fetchRequiredVirtualNumberField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        VirtualNumberField eoObject = _VirtualNumberField.fetchVirtualNumberField(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no VirtualNumberField that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
