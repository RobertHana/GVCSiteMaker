
package com.gvcsitemaker.core.pagecomponent;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to PageComponent.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _PageComponent extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String PAGECOMPONENT_ENTITY_NAME = "PageComponent";

    public static final String BINDINGS = "bindings";
    public static final String COMPONENTID = "componentId";
    public static final String COMPONENTTYPE = "componentType";
    public static final String DATEMODIFIED = "dateModified";
    public static final String RELATEDFILEID = "relatedFileId";
    public static final String SUBTYPE = "subType";

    public static final String SECTIONS = "sections";
    public static final String TOCHILDREN = "toChildren";
    public static final String TOPARENT = "toParent";
    public static final String TORELATEDFILE = "toRelatedFile";
    public static final String VERSIONS = "versions";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "PageComponent");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public PageComponent localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (PageComponent)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String bindings() {
        return (String) storedValueForKey("bindings");
    }



    public void setBindings(String value) 
    {
        takeStoredValueForKey(value, "bindings");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number componentId() {
        return (Number) storedValueForKey("componentId");
    }



    public void setComponentId(Number value) 
    {
        takeStoredValueForKey(value, "componentId");
    }
    
    

    /**
     *
     * @return String     
     */
    public String componentType() {
        return (String) storedValueForKey("componentType");
    }



    public void setComponentType(String value) 
    {
        takeStoredValueForKey(value, "componentType");
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
     * @return Number     
     */
    public Number relatedFileId() {
        return (Number) storedValueForKey("relatedFileId");
    }



    public void setRelatedFileId(Number value) 
    {
        takeStoredValueForKey(value, "relatedFileId");
    }
    
    

    /**
     *
     * @return String     
     */
    public String subType() {
        return (String) storedValueForKey("subType");
    }



    public void setSubType(String value) 
    {
        takeStoredValueForKey(value, "subType");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.PageComponent
     
     */
    public com.gvcsitemaker.core.pagecomponent.PageComponent toParent() 
    {
        return (com.gvcsitemaker.core.pagecomponent.PageComponent)storedValueForKey("toParent");
    }



    public void setToParent(com.gvcsitemaker.core.pagecomponent.PageComponent value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "toParent");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.SiteFile
     
     */
    public com.gvcsitemaker.core.SiteFile toRelatedFile() 
    {
        return (com.gvcsitemaker.core.SiteFile)storedValueForKey("toRelatedFile");
    }



    public void setToRelatedFile(com.gvcsitemaker.core.SiteFile value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "toRelatedFile");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Section
     */
    public NSArray sections() 
    {
        return (NSArray)storedValueForKey("sections");
    }



    public void setSections(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Section)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "sections");
    }



    public void addToSections(com.gvcsitemaker.core.Section object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_sections] ! sections().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)sections();

        willChange();
        array.addObject(object);
        /** ensure [added_to_sections] sections().containsObject(object);        **/
    }



    public void removeFromSections(com.gvcsitemaker.core.Section object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_sections] sections().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)sections();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_sections] ! sections().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray sections(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.Section.COMPONENT, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.Section.fetchSections(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = sections();
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
     *
     * @return com.gvcsitemaker.core.pagecomponent.PageComponent
     */
    public NSArray toChildren() 
    {
        return (NSArray)storedValueForKey("toChildren");
    }



    public void setToChildren(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.PageComponent)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "toChildren");
    }



    public void addToToChildren(com.gvcsitemaker.core.pagecomponent.PageComponent object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_toChildren] ! toChildren().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)toChildren();

        willChange();
        array.addObject(object);
        /** ensure [added_to_toChildren] toChildren().containsObject(object);        **/
    }



    public void removeFromToChildren(com.gvcsitemaker.core.pagecomponent.PageComponent object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_toChildren] toChildren().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)toChildren();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_toChildren] ! toChildren().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray toChildren(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.pagecomponent.PageComponent.TOPARENT, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.pagecomponent.PageComponent.fetchPageComponents(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = toChildren();
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
     *
     * @return com.gvcsitemaker.core.SectionVersion
     */
    public NSArray versions() 
    {
        return (NSArray)storedValueForKey("versions");
    }



    public void setVersions(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.SectionVersion)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "versions");
    }



    public void addToVersions(com.gvcsitemaker.core.SectionVersion object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_versions] ! versions().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)versions();

        willChange();
        array.addObject(object);
        /** ensure [added_to_versions] versions().containsObject(object);        **/
    }



    public void removeFromVersions(com.gvcsitemaker.core.SectionVersion object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_versions] versions().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)versions();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_versions] ! versions().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray versions(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SectionVersion.COMPONENT, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.SectionVersion.fetchSectionVersions(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = versions();
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
	 * @return all instances of PageComponents in editingContext
	 */
    public static NSArray fetchAllPageComponents(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _PageComponent.fetchAllPageComponents(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of PageComponents, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllPageComponents(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _PageComponent.fetchPageComponents(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of PageComponents matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchPageComponents(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(PAGECOMPONENT_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of PageComponents where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static PageComponent fetchPageComponent(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _PageComponent.fetchPageComponent(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of PageComponents matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static PageComponent fetchPageComponent(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _PageComponent.fetchPageComponents(editingContext, qualifier, null);
        PageComponent eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (PageComponent)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one PageComponent that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of PageComponents where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static PageComponent fetchRequiredPageComponent(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _PageComponent.fetchRequiredPageComponent(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of PageComponents matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static PageComponent fetchRequiredPageComponent(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        PageComponent eoObject = _PageComponent.fetchPageComponent(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no PageComponent that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
