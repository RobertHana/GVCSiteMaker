
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Section.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Section extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String SECTION_ENTITY_NAME = "Section";

    public static final String DETAILS = "details";
    public static final String INDENTATION = "indentation";
    public static final String ISAUTOVERSIONING = "isAutoVersioning";
    public static final String ISNAVIGABLE = "isNavigable";
    public static final String ISVERSIONING = "isVersioning";
    public static final String MIMETYPE = "mimeType";
    public static final String NAME = "name";
    public static final String NORMALNAME = "normalName";
    public static final String REQUIREHTTPSACCESSFORPUBLICSECTIONS = "requireHttpsAccessForPublicSections";
    public static final String SECTIONORDER = "sectionOrder";

    public static final String COMPONENT = "component";
    public static final String CONTRIBUTORGROUPS = "contributorGroups";
    public static final String COPYSECTIONSOURCE = "copySectionSource";
    public static final String EDITORGROUPS = "editorGroups";
    public static final String EMBEDDEDSECTIONCOMPONENTS = "embeddedSectionComponents";
    public static final String GROUPS = "groups";
    public static final String SECTIONSTYLE = "sectionStyle";
    public static final String TYPE = "type";
    public static final String VERSIONS = "versions";
    public static final String WEBSITE = "website";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Section");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Section localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Section)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String details() {
        return (String) storedValueForKey("details");
    }



    public void setDetails(String value) 
    {
        takeStoredValueForKey(value, "details");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number indentation() {
        return (Number) storedValueForKey("indentation");
    }



    public void setIndentation(Number value) 
    {
        takeStoredValueForKey(value, "indentation");
    }
    
    

    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isAutoVersioning() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isAutoVersioning");
    }



    public void setIsAutoVersioning(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isAutoVersioning");
    }
    
    

    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isNavigable() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isNavigable");
    }



    public void setIsNavigable(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isNavigable");
    }
    
    

    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean isVersioning() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("isVersioning");
    }



    public void setIsVersioning(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "isVersioning");
    }
    
    

    /**
     *
     * @return String     
     */
    public String mimeType() {
        return (String) storedValueForKey("mimeType");
    }



    public void setMimeType(String value) 
    {
        takeStoredValueForKey(value, "mimeType");
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
    public String normalName() {
        return (String) storedValueForKey("normalName");
    }



    public void setNormalName(String value) 
    {
        takeStoredValueForKey(value, "normalName");
    }
    
    

    /**
     *
     * @return net.global_village.foundation.GVCBoolean     
     */
    public net.global_village.foundation.GVCBoolean requireHttpsAccessForPublicSections() {
        return (net.global_village.foundation.GVCBoolean) storedValueForKey("requireHttpsAccessForPublicSections");
    }



    public void setRequireHttpsAccessForPublicSections(net.global_village.foundation.GVCBoolean value) 
    {
        takeStoredValueForKey(value, "requireHttpsAccessForPublicSections");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number sectionOrder() {
        return (Number) storedValueForKey("sectionOrder");
    }



    public void setSectionOrder(Number value) 
    {
        takeStoredValueForKey(value, "sectionOrder");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.PageComponent
     
     */
    public com.gvcsitemaker.core.pagecomponent.PageComponent component() 
    {
        return (com.gvcsitemaker.core.pagecomponent.PageComponent)storedValueForKey("component");
    }



    public void setComponent(com.gvcsitemaker.core.pagecomponent.PageComponent value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "component");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.SectionStyle
     
     */
    public com.gvcsitemaker.core.SectionStyle sectionStyle() 
    {
        return (com.gvcsitemaker.core.SectionStyle)storedValueForKey("sectionStyle");
    }



    public void setSectionStyle(com.gvcsitemaker.core.SectionStyle value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "sectionStyle");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.SectionType
     
     */
    public com.gvcsitemaker.core.SectionType type() 
    {
        return (com.gvcsitemaker.core.SectionType)storedValueForKey("type");
    }



    public void setType(com.gvcsitemaker.core.SectionType value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "type");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Website
     
     */
    public com.gvcsitemaker.core.Website website() 
    {
        return (com.gvcsitemaker.core.Website)storedValueForKey("website");
    }



    public void setWebsite(com.gvcsitemaker.core.Website value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "website");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Group
     */
    public NSArray contributorGroups() 
    {
        return (NSArray)storedValueForKey("contributorGroups");
    }



    public void setContributorGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Group)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "contributorGroups");
    }



    public void addToContributorGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_contributorGroups] ! contributorGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)contributorGroups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_contributorGroups] contributorGroups().containsObject(object);        **/
    }



    public void removeFromContributorGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_contributorGroups] contributorGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)contributorGroups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_contributorGroups] ! contributorGroups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray contributorGroups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = contributorGroups();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.CopySectionTask
     */
    public NSArray copySectionSource() 
    {
        return (NSArray)storedValueForKey("copySectionSource");
    }



    public void setCopySectionSource(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.CopySectionTask)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "copySectionSource");
    }



    public void addToCopySectionSource(com.gvcsitemaker.core.CopySectionTask object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_copySectionSource] ! copySectionSource().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)copySectionSource();

        willChange();
        array.addObject(object);
        /** ensure [added_to_copySectionSource] copySectionSource().containsObject(object);        **/
    }



    public void removeFromCopySectionSource(com.gvcsitemaker.core.CopySectionTask object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_copySectionSource] copySectionSource().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)copySectionSource();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_copySectionSource] ! copySectionSource().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray copySectionSource(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.CopySectionTask.SOURCESECTION, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.CopySectionTask.fetchCopySectionTasks(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = copySectionSource();
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
     * @return com.gvcsitemaker.core.Group
     */
    public NSArray editorGroups() 
    {
        return (NSArray)storedValueForKey("editorGroups");
    }



    public void setEditorGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Group)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "editorGroups");
    }



    public void addToEditorGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_editorGroups] ! editorGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)editorGroups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_editorGroups] editorGroups().containsObject(object);        **/
    }



    public void removeFromEditorGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_editorGroups] editorGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)editorGroups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_editorGroups] ! editorGroups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray editorGroups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = editorGroups();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
          }
       return results;
       /** ensure [valid_result] Result != null;  **/
    }
  


    /**
     *
     * @return com.gvcsitemaker.core.pagecomponent.EmbeddedSection
     */
    public NSArray embeddedSectionComponents() 
    {
        return (NSArray)storedValueForKey("embeddedSectionComponents");
    }



    public void setEmbeddedSectionComponents(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.EmbeddedSection)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "embeddedSectionComponents");
    }



    public void addToEmbeddedSectionComponents(com.gvcsitemaker.core.pagecomponent.EmbeddedSection object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_embeddedSectionComponents] ! embeddedSectionComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)embeddedSectionComponents();

        willChange();
        array.addObject(object);
        /** ensure [added_to_embeddedSectionComponents] embeddedSectionComponents().containsObject(object);        **/
    }



    public void removeFromEmbeddedSectionComponents(com.gvcsitemaker.core.pagecomponent.EmbeddedSection object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_embeddedSectionComponents] embeddedSectionComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)embeddedSectionComponents();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_embeddedSectionComponents] ! embeddedSectionComponents().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray embeddedSectionComponents(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.pagecomponent.EmbeddedSection.LINKEDSECTION, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.pagecomponent.EmbeddedSection.fetchEmbeddedSections(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = embeddedSectionComponents();
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
     * @return com.gvcsitemaker.core.Group
     */
    public NSArray groups() 
    {
        return (NSArray)storedValueForKey("groups");
    }



    public void setGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.Group)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "groups");
    }



    public void addToGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_groups] ! groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_groups] groups().containsObject(object);        **/
    }



    public void removeFromGroups(com.gvcsitemaker.core.Group object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_groups] groups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)groups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_groups] ! groups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray groups(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = groups();
          if (qualifier != null) 
          {
              results = EOQualifier.filteredArrayWithQualifier(results, qualifier);
          }
          if (sortOrderings != null) 
          {
               results = EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
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
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SectionVersion.SECTION, EOQualifier.QualifierOperatorEqual, this);
    	
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
	 * @return all instances of Sections in editingContext
	 */
    public static NSArray fetchAllSections(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Section.fetchAllSections(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Sections, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllSections(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Section.fetchSections(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Sections matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchSections(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(SECTION_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Sections where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Section fetchSection(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Section.fetchSection(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Sections matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Section fetchSection(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Section.fetchSections(editingContext, qualifier, null);
        Section eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Section)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Section that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Sections where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Section fetchRequiredSection(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Section.fetchRequiredSection(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Sections matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Section fetchRequiredSection(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Section eoObject = _Section.fetchSection(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Section that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
