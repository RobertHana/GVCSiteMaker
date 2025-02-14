
package net.global_village.virtualtables;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to VirtualCalculatedColumn.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _VirtualCalculatedColumn extends net.global_village.virtualtables.VirtualColumn
{


    public static final String VIRTUALCALCULATEDCOLUMN_ENTITY_NAME = "VirtualCalculatedColumn";

    public static final String EXPRESSION = "expression";



     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "VirtualCalculatedColumn");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public VirtualCalculatedColumn localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (VirtualCalculatedColumn)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String expression() {
        return (String) storedValueForKey("expression");
    }



    public void setExpression(String value) 
    {
        takeStoredValueForKey(value, "expression");
    }
    
    


	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of VirtualCalculatedColumns in editingContext
	 */
    public static NSArray fetchAllVirtualCalculatedColumns(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualCalculatedColumn.fetchAllVirtualCalculatedColumns(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualCalculatedColumns, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllVirtualCalculatedColumns(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _VirtualCalculatedColumn.fetchVirtualCalculatedColumns(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of VirtualCalculatedColumns matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchVirtualCalculatedColumns(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(VIRTUALCALCULATEDCOLUMN_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of VirtualCalculatedColumns where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static VirtualCalculatedColumn fetchVirtualCalculatedColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualCalculatedColumn.fetchVirtualCalculatedColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualCalculatedColumns matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static VirtualCalculatedColumn fetchVirtualCalculatedColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _VirtualCalculatedColumn.fetchVirtualCalculatedColumns(editingContext, qualifier, null);
        VirtualCalculatedColumn eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (VirtualCalculatedColumn)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one VirtualCalculatedColumn that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of VirtualCalculatedColumns where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static VirtualCalculatedColumn fetchRequiredVirtualCalculatedColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _VirtualCalculatedColumn.fetchRequiredVirtualCalculatedColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of VirtualCalculatedColumns matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static VirtualCalculatedColumn fetchRequiredVirtualCalculatedColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        VirtualCalculatedColumn eoObject = _VirtualCalculatedColumn.fetchVirtualCalculatedColumn(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no VirtualCalculatedColumn that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
