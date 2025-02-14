
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to VirtualTimestampField.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _VirtualTimestampField extends net.global_village.virtualtables.VirtualField
{


    public static final String VIRTUALTIMESTAMPFIELD_ENTITY_NAME = "VirtualTimestampField";

    public static final String TIMESTAMPVALUE = "timestampValue";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "VirtualTimestampField");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public VirtualTimestampField localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (VirtualTimestampField)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return NSTimestamp     
     */
    public NSTimestamp timestampValue() {
        return (NSTimestamp) storedValueForKey("timestampValue");
    }



    public void setTimestampValue(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "timestampValue");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of VirtualTimestampFields in editingContext
	 */
    public static NSArray fetchAllVirtualTimestampFields(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualTimestampField.fetchAllVirtualTimestampFields(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualTimestampFields, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllVirtualTimestampFields(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualTimestampField.fetchVirtualTimestampFields(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualTimestampFields matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchVirtualTimestampFields(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(VIRTUALTIMESTAMPFIELD_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of VirtualTimestampFields where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static VirtualTimestampField fetchVirtualTimestampField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualTimestampField.fetchVirtualTimestampField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualTimestampFields matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static VirtualTimestampField fetchVirtualTimestampField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _VirtualTimestampField.fetchVirtualTimestampFields(editingContext, qualifier, null);
        VirtualTimestampField eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (VirtualTimestampField)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one VirtualTimestampField that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of VirtualTimestampFields where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static VirtualTimestampField fetchRequiredVirtualTimestampField(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualTimestampField.fetchRequiredVirtualTimestampField(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualTimestampFields matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static VirtualTimestampField fetchRequiredVirtualTimestampField(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        VirtualTimestampField eoObject = _VirtualTimestampField.fetchVirtualTimestampField(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no VirtualTimestampField that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
