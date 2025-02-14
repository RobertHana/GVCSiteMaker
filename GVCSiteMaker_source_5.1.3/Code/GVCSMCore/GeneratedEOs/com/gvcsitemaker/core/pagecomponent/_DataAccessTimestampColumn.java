
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to DataAccessTimestampColumn.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _DataAccessTimestampColumn extends com.gvcsitemaker.core.pagecomponent.DataAccessColumn
{


    public static final String DATAACCESSTIMESTAMPCOLUMN_ENTITY_NAME = "DataAccessTimestampColumn";




     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "DataAccessTimestampColumn");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public DataAccessTimestampColumn localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (DataAccessTimestampColumn)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }




	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of DataAccessTimestampColumns in editingContext
	 */
    public static NSArray fetchAllDataAccessTimestampColumns(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessTimestampColumn.fetchAllDataAccessTimestampColumns(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessTimestampColumns, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllDataAccessTimestampColumns(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessTimestampColumn.fetchDataAccessTimestampColumns(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessTimestampColumns matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchDataAccessTimestampColumns(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(DATAACCESSTIMESTAMPCOLUMN_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of DataAccessTimestampColumns where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static DataAccessTimestampColumn fetchDataAccessTimestampColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessTimestampColumn.fetchDataAccessTimestampColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessTimestampColumns matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static DataAccessTimestampColumn fetchDataAccessTimestampColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _DataAccessTimestampColumn.fetchDataAccessTimestampColumns(editingContext, qualifier, null);
        DataAccessTimestampColumn eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (DataAccessTimestampColumn)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one DataAccessTimestampColumn that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of DataAccessTimestampColumns where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static DataAccessTimestampColumn fetchRequiredDataAccessTimestampColumn(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessTimestampColumn.fetchRequiredDataAccessTimestampColumn(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessTimestampColumns matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static DataAccessTimestampColumn fetchRequiredDataAccessTimestampColumn(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        DataAccessTimestampColumn eoObject = _DataAccessTimestampColumn.fetchDataAccessTimestampColumn(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no DataAccessTimestampColumn that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
