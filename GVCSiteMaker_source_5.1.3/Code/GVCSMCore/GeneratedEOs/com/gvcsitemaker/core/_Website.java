
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to Website.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _Website extends com.gvcsitemaker.core.support.SMGenericRecord
{


    public static final String WEBSITE_ENTITY_NAME = "Website";

    public static final String CANREPLICATE = "canReplicate";
    public static final String CONTACTEMAIL = "contactEmail";
    public static final String CONTACTFAX = "contactFax";
    public static final String CONTACTNAME = "contactName";
    public static final String CONTACTPHONE = "contactPhone";
    public static final String CUSTOMHEADERCONTENT = "customHeaderContent";
    public static final String DATECREATED = "dateCreated";
    public static final String DATELASTMODIFIED = "dateLastModified";
    public static final String FILESIZEUSAGE = "fileSizeUsage";
    public static final String MEGQUOTA = "megQuota";
    public static final String PUBLICATIONPERMITTED = "publicationPermitted";
    public static final String PUBLISHED = "published";
    public static final String REDIRECTURL = "redirectURL";
    public static final String SHOWBANNER = "showBanner";
    public static final String SHOWFOOTER = "showFooter";
    public static final String SHOWLOGINLOGOUTLINKS = "showLoginLogoutLinks";
    public static final String SHOWNAVBAR = "showNavbar";
    public static final String SITEID = "siteID";

    public static final String BANNER = "banner";
    public static final String CHILDGROUPS = "childGroups";
    public static final String CONFIGUREGROUP = "configureGroup";
    public static final String DATABASETABLES = "databaseTables";
    public static final String EMBEDDEDSITECOMPONENTS = "embeddedSiteComponents";
    public static final String FILES = "files";
    public static final String FOLDERS = "folders";
    public static final String FOOTER = "footer";
    public static final String OWNER = "owner";
    public static final String PARENTORGUNIT = "parentOrgUnit";
    public static final String SECTIONS = "sections";
    public static final String SECTIONSTYLE = "sectionStyle";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "Website");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public Website localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (Website)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return String     
     */
    public String canReplicate() {
        return (String) storedValueForKey("canReplicate");
    }



    public void setCanReplicate(String value) 
    {
        takeStoredValueForKey(value, "canReplicate");
    }
    
    

    /**
     *
     * @return String     
     */
    public String contactEmail() {
        return (String) storedValueForKey("contactEmail");
    }



    public void setContactEmail(String value) 
    {
        takeStoredValueForKey(value, "contactEmail");
    }
    
    

    /**
     *
     * @return String     
     */
    public String contactFax() {
        return (String) storedValueForKey("contactFax");
    }



    public void setContactFax(String value) 
    {
        takeStoredValueForKey(value, "contactFax");
    }
    
    

    /**
     *
     * @return String     
     */
    public String contactName() {
        return (String) storedValueForKey("contactName");
    }



    public void setContactName(String value) 
    {
        takeStoredValueForKey(value, "contactName");
    }
    
    

    /**
     *
     * @return String     
     */
    public String contactPhone() {
        return (String) storedValueForKey("contactPhone");
    }



    public void setContactPhone(String value) 
    {
        takeStoredValueForKey(value, "contactPhone");
    }
    
    

    /**
     *
     * @return String     
     */
    public String customHeaderContent() {
        return (String) storedValueForKey("customHeaderContent");
    }



    public void setCustomHeaderContent(String value) 
    {
        takeStoredValueForKey(value, "customHeaderContent");
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
    public NSTimestamp dateLastModified() {
        return (NSTimestamp) storedValueForKey("dateLastModified");
    }



    public void setDateLastModified(NSTimestamp value) 
    {
        takeStoredValueForKey(value, "dateLastModified");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number fileSizeUsage() {
        return (Number) storedValueForKey("fileSizeUsage");
    }



    public void setFileSizeUsage(Number value) 
    {
        takeStoredValueForKey(value, "fileSizeUsage");
    }
    
    

    /**
     *
     * @return Number     
     */
    public Number megQuota() {
        return (Number) storedValueForKey("megQuota");
    }



    public void setMegQuota(Number value) 
    {
        takeStoredValueForKey(value, "megQuota");
    }
    
    

    /**
     *
     * @return String     
     */
    public String publicationPermitted() {
        return (String) storedValueForKey("publicationPermitted");
    }



    public void setPublicationPermitted(String value) 
    {
        takeStoredValueForKey(value, "publicationPermitted");
    }
    
    

    /**
     *
     * @return String     
     */
    public String published() {
        return (String) storedValueForKey("published");
    }



    public void setPublished(String value) 
    {
        takeStoredValueForKey(value, "published");
    }
    
    

    /**
     *
     * @return String     
     */
    public String redirectURL() {
        return (String) storedValueForKey("redirectURL");
    }



    public void setRedirectURL(String value) 
    {
        takeStoredValueForKey(value, "redirectURL");
    }
    
    

    /**
     *
     * @return String     
     */
    public String showBanner() {
        return (String) storedValueForKey("showBanner");
    }



    public void setShowBanner(String value) 
    {
        takeStoredValueForKey(value, "showBanner");
    }
    
    

    /**
     *
     * @return String     
     */
    public String showFooter() {
        return (String) storedValueForKey("showFooter");
    }



    public void setShowFooter(String value) 
    {
        takeStoredValueForKey(value, "showFooter");
    }
    
    

    /**
     *
     * @return String     
     */
    public String showLoginLogoutLinks() {
        return (String) storedValueForKey("showLoginLogoutLinks");
    }



    public void setShowLoginLogoutLinks(String value) 
    {
        takeStoredValueForKey(value, "showLoginLogoutLinks");
    }
    
    

    /**
     *
     * @return String     
     */
    public String showNavbar() {
        return (String) storedValueForKey("showNavbar");
    }



    public void setShowNavbar(String value) 
    {
        takeStoredValueForKey(value, "showNavbar");
    }
    
    

    /**
     *
     * @return String     
     */
    public String siteID() {
        return (String) storedValueForKey("siteID");
    }



    public void setSiteID(String value) 
    {
        takeStoredValueForKey(value, "siteID");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.Banner
     
     */
    public com.gvcsitemaker.core.Banner banner() 
    {
        return (com.gvcsitemaker.core.Banner)storedValueForKey("banner");
    }



    public void setBanner(com.gvcsitemaker.core.Banner value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "banner");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.ConfigureGroup
     
     */
    public com.gvcsitemaker.core.ConfigureGroup configureGroup() 
    {
        return (com.gvcsitemaker.core.ConfigureGroup)storedValueForKey("configureGroup");
    }



    public void setConfigureGroup(com.gvcsitemaker.core.ConfigureGroup value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "configureGroup");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.Footer
     
     */
    public com.gvcsitemaker.core.Footer footer() 
    {
        return (com.gvcsitemaker.core.Footer)storedValueForKey("footer");
    }



    public void setFooter(com.gvcsitemaker.core.Footer value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "footer");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.User
     
     */
    public com.gvcsitemaker.core.User owner() 
    {
        return (com.gvcsitemaker.core.User)storedValueForKey("owner");
    }



    public void setOwner(com.gvcsitemaker.core.User value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "owner");
    }
  
  
  
    /**
     *
     * @return com.gvcsitemaker.core.OrgUnit
     
     */
    public com.gvcsitemaker.core.OrgUnit parentOrgUnit() 
    {
        return (com.gvcsitemaker.core.OrgUnit)storedValueForKey("parentOrgUnit");
    }



    public void setParentOrgUnit(com.gvcsitemaker.core.OrgUnit value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "parentOrgUnit");
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
     * @return com.gvcsitemaker.core.WebsiteGroup
     */
    public NSArray childGroups() 
    {
        return (NSArray)storedValueForKey("childGroups");
    }



    public void setChildGroups(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.WebsiteGroup)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "childGroups");
    }



    public void addToChildGroups(com.gvcsitemaker.core.WebsiteGroup object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_childGroups] ! childGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childGroups();

        willChange();
        array.addObject(object);
        /** ensure [added_to_childGroups] childGroups().containsObject(object);        **/
    }



    public void removeFromChildGroups(com.gvcsitemaker.core.WebsiteGroup object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_childGroups] childGroups().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)childGroups();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_childGroups] ! childGroups().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray childGroups(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.WebsiteGroup.PARENTWEBSITE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.WebsiteGroup.fetchWebsiteGroups(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = childGroups();
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
     * @return net.global_village.virtualtables.Table
     */
    public NSArray databaseTables() 
    {
        return (NSArray)storedValueForKey("databaseTables");
    }



    public void setDatabaseTables(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((net.global_village.virtualtables.Table)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "databaseTables");
    }



    public void addToDatabaseTables(net.global_village.virtualtables.Table object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_databaseTables] ! databaseTables().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)databaseTables();

        willChange();
        array.addObject(object);
        /** ensure [added_to_databaseTables] databaseTables().containsObject(object);        **/
    }



    public void removeFromDatabaseTables(net.global_village.virtualtables.Table object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_databaseTables] databaseTables().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)databaseTables();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_databaseTables] ! databaseTables().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by

	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray databaseTables(EOQualifier qualifier, NSArray sortOrderings) 
    {
        NSArray results;
          results = databaseTables();
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
     * @return com.gvcsitemaker.core.pagecomponent.EmbeddedSite
     */
    public NSArray embeddedSiteComponents() 
    {
        return (NSArray)storedValueForKey("embeddedSiteComponents");
    }



    public void setEmbeddedSiteComponents(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.pagecomponent.EmbeddedSite)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "embeddedSiteComponents");
    }



    public void addToEmbeddedSiteComponents(com.gvcsitemaker.core.pagecomponent.EmbeddedSite object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_embeddedSiteComponents] ! embeddedSiteComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)embeddedSiteComponents();

        willChange();
        array.addObject(object);
        /** ensure [added_to_embeddedSiteComponents] embeddedSiteComponents().containsObject(object);        **/
    }



    public void removeFromEmbeddedSiteComponents(com.gvcsitemaker.core.pagecomponent.EmbeddedSite object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_embeddedSiteComponents] embeddedSiteComponents().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)embeddedSiteComponents();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_embeddedSiteComponents] ! embeddedSiteComponents().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray embeddedSiteComponents(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.pagecomponent.EmbeddedSite.LINKEDWEBSITE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.pagecomponent.EmbeddedSite.fetchEmbeddedSites(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = embeddedSiteComponents();
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
     * @return com.gvcsitemaker.core.SiteFile
     */
    public NSArray files() 
    {
        return (NSArray)storedValueForKey("files");
    }



    public void setFiles(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.SiteFile)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "files");
    }



    public void addToFiles(com.gvcsitemaker.core.SiteFile object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_files] ! files().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)files();

        willChange();
        array.addObject(object);
        /** ensure [added_to_files] files().containsObject(object);        **/
    }



    public void removeFromFiles(com.gvcsitemaker.core.SiteFile object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_files] files().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)files();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_files] ! files().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray files(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SiteFile.WEBSITE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.SiteFile.fetchSiteFiles(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = files();
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
     * @return com.gvcsitemaker.core.SiteFileFolder
     */
    public NSArray folders() 
    {
        return (NSArray)storedValueForKey("folders");
    }



    public void setFolders(NSMutableArray aValue) 
    {
        /** require [valid_value] aValue != null;
                    [same_ec] (forall i : {0 .. aValue.count()-1} # editingContext() == ((com.gvcsitemaker.core.SiteFileFolder)aValue.objectAtIndex(i)).editingContext());  
         **/
        takeStoredValueForKey(aValue, "folders");
    }



    public void addToFolders(com.gvcsitemaker.core.SiteFileFolder object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [not_in_folders] ! folders().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)folders();

        willChange();
        array.addObject(object);
        /** ensure [added_to_folders] folders().containsObject(object);        **/
    }



    public void removeFromFolders(com.gvcsitemaker.core.SiteFileFolder object) 
    {
        /** require [same_ec] editingContext() == object.editingContext();  
                    [valid_object] object != null;
                    [in_folders] folders().containsObject(object);     
         **/
        NSMutableArray array = (NSMutableArray)folders();

        willChange();
        array.removeObject(object);
        /** ensure [removed_from_folders] ! folders().containsObject(object);        **/
    }



	/**
	 * @param qualifier optional EOQualifier restricting the ${relationship.pluralName} to return
	 * @param sortOrderings optional list of EOSortOrdering to order the returned ${relationship.pluralName} by
	 * @param fetch true if fresh objects should be fetched from the database
	 * @return ${relationship.pluralName} matching qualifier, ordered by sortOrderings
     */
    public NSArray folders(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) 
    {
        NSArray results;
        if (fetch) 
        {
            EOQualifier fullQualifier;
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.SiteFileFolder.WEBSITE, EOQualifier.QualifierOperatorEqual, this);
    	
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

          results = com.gvcsitemaker.core.SiteFileFolder.fetchSiteFileFolders(editingContext(), fullQualifier, sortOrderings);
      }
      else 
      {
          results = folders();
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
            EOQualifier inverseQualifier = new EOKeyValueQualifier(com.gvcsitemaker.core.Section.WEBSITE, EOQualifier.QualifierOperatorEqual, this);
    	
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
	 * @param editingContext EC to return objects in
	 * @return all instances of Websites in editingContext
	 */
    public static NSArray fetchAllWebsites(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Website.fetchAllWebsites(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Websites, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllWebsites(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _Website.fetchWebsites(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of Websites matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchWebsites(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(WEBSITE_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of Websites where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static Website fetchWebsite(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Website.fetchWebsite(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Websites matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static Website fetchWebsite(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _Website.fetchWebsites(editingContext, qualifier, null);
        Website eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (Website)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one Website that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of Websites where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static Website fetchRequiredWebsite(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _Website.fetchRequiredWebsite(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of Websites matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static Website fetchRequiredWebsite(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        Website eoObject = _Website.fetchWebsite(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no Website that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
