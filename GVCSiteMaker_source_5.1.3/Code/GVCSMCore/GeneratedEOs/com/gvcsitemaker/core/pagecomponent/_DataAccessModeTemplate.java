
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to DataAccessModeTemplate.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _DataAccessModeTemplate extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String DATAACCESSMODETEMPLATE_ENTITY_NAME = "DataAccessModeTemplate";

    public static final String HTML = "html";

    public static final String DATAACCESSMODES = "dataAccessModes";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "DataAccessModeTemplate");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public DataAccessModeTemplate localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (DataAccessModeTemplate)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String html() {
        return (String) storedValueForKey("html");
    }



    public void setHtml(String value) 
    {
        takeStoredValueForKey(value, "html");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.DataAccessMode
     */
    public NSArray dataAccessModes() 
    {
        return (NSArray)storedValueForKey("dataAccessModes");
    }



    public void setDataAccessModes(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.DataAccessMode)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "dataAccessModes");
    }



    public void addToDataAccessModes(com.gvcsitemaker.core.pagecomponent.DataAccessMode object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_dataAccessModes] ! dataAccessModes().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)dataAccessModes();

        willChange();
        array.addObject(object);
        /** ensure [added_to_dataAccessModes] dataAccessModes().containsObject(object);        **/
    }



    public void removeFromDataAccessModes(com.gvcsitemaker.core.pagecomponent.DataAccessMode object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_dataAccessModes] dataAccessModes().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)dataAccessModes();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_dataAccessModes] ! dataAccessModes().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray dataAccessModes(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.pagecomponent.DataAccessMode.CUSTOMTEMPLATE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.pagecomponent.DataAccessMode.fetchDataAccessModes(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = dataAccessModes();
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
	 * @return all instances of DataAccessModeTemplates in editingContext
	 */
    public static NSArray fetchAllDataAccessModeTemplates(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessModeTemplate.fetchAllDataAccessModeTemplates(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessModeTemplates, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllDataAccessModeTemplates(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _DataAccessModeTemplate.fetchDataAccessModeTemplates(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of DataAccessModeTemplates matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchDataAccessModeTemplates(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(DATAACCESSMODETEMPLATE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of DataAccessModeTemplates where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static DataAccessModeTemplate fetchDataAccessModeTemplate(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessModeTemplate.fetchDataAccessModeTemplate(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessModeTemplates matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static DataAccessModeTemplate fetchDataAccessModeTemplate(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _DataAccessModeTemplate.fetchDataAccessModeTemplates(editingContext, qualifier, null);
        DataAccessModeTemplate eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (DataAccessModeTemplate)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one DataAccessModeTemplate that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of DataAccessModeTemplates where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static DataAccessModeTemplate fetchRequiredDataAccessModeTemplate(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _DataAccessModeTemplate.fetchRequiredDataAccessModeTemplate(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of DataAccessModeTemplates matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static DataAccessModeTemplate fetchRequiredDataAccessModeTemplate(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        DataAccessModeTemplate eoObject = _DataAccessModeTemplate.fetchDataAccessModeTemplate(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no DataAccessModeTemplate that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
