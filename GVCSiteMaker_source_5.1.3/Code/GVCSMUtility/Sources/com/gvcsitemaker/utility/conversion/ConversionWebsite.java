// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.utility.conversion;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.foundation.NSComparator.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;
import net.global_village.virtualtables.*;


public class ConversionWebsite extends Website implements Deleteable, net.global_village.eofvalidation.EditingContextNotification
{
    // Transient values for post-save notifications and processing
    protected boolean didRemoveFiles = false;
    protected String messageFrom = null;
    protected String messageSubject = null; 
    protected String messageTemplate = null;
    public String clonedSiteID = null;         // Public to allow KVC access
    
    protected static NSMutableArray restrictedWebsiteIDs = null;

    public static final String ERROR_MESSAGE_KEY = "errorMessage";
    public static final String SITE_ID_KEY = "siteID";
    public static final String OWNER_ID_KEY = "ownerID";
    public static final String ASSOC_UNIT_NAME = "associatedUnitName";
    public static final String QUOTA_KEY = "megQuota";
    public static final String REDIRECT_URL_KEY = "redirectURL";
    public static final String EXAMPLE_SITE_ID = "exampleSiteID";

    public static final String NULL_SITE_ID_ERROR_KEY = "nullSiteIDErrorKey";
    public static final String INVALID_SITE_ID_ERROR_KEY = "invalidSiteIDErrorKey";
    public static final String SITE_ID_EXISTS_ERROR_KEY = "siteIDExistsErrorKey";
    public static final String NULL_OWNER_ID_ERROR_KEY = "nullOwnerIDErrorKey";
    public static final String NULL_ASSOCIATED_UNIT_ERROR_KEY = "nullAssociatedUnitErrorKey";
    public static final String INVALID_FILESPACE_QUOTA_ERROR_KEY = "invalidFilespaceQuotaErrorKey";
    public static final String INSUFFICIENT_FILESPACE_QUOTA_ERROR_KEY = "insufficientFilespaceQuotaErrorKey";
    public static final String SITE_CAN_NOT_BE_REPLICATED_KEY = "siteCanNotBeReplicatedKey";
    public static final String EXAMPLE_SITE_ID_NOT_FOUND_ERROR_KEY = "exampleSiteIDNotFoundErrorKey";
    public static final String WEBSITE_OUT_OF_ADMIN_SCOPE_ERROR_KEY = "websiteOutOfAdminScopeErrorKey";
    public static final String INVALID_REDIRECT_URL_ERROR_KEY = "invalidRedirectUrlKey";
    public static final String INVALID_CHARS_ERROR_KEY = "invalidUserID";
    
    // For the SiteFileFolders created with a Website.
    public static final String DEFAULT_UPLOADED_FILES_FOLDER_NAME = "Uploaded Files";
    public static final String DEFAULT_DATABASE_TABLES_FOLDER_NAME = "Database Tables";

    public static final NSArray BATCH_FILE_KEY_ARRAY = new NSArray(new String[] { SITE_ID_KEY,
                                                                                   OWNER_ID_KEY,
                                                                                   ASSOC_UNIT_NAME,
                                                                                   QUOTA_KEY,
                                                                                   REDIRECT_URL_KEY,
                                                                                   EXAMPLE_SITE_ID });
                                                                                   
    public static final NSComparator OwnerComparator = new OwnerComparator();
    public static final NSComparator SiteIDComparator = new SiteIDComparator();

    
    /**
     * Factory method to create new instances of Website.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Website or a subclass.
     */
    public static Website newWebsite()
    {
        return (Website) SMEOUtils.newInstanceOf("Website");

        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
     * Factory method to create new instances of Website.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @param associatedUnit OrgUnit that Website will be created under.
     * @param siteID ID for new Website
     * @param bannerText text for banner
     * @param owner owner of Website
     * @param ec editing context to create Website in
     *
     * @return a new instance of Website or a subclass.
     */
    public static Website newWebsite(OrgUnit associatedUnit,
                                     String siteID,
                                     String bannerText,
                                     User owner,
                                     EOEditingContext ec)
    {
        /** require
        [associatedUnit_not_null] associatedUnit != null;
        [siteID_not_null] siteID != null;
        [bannerText_not_null] bannerText != null;
        [owner_not_null] owner != null;
        [ec_not_null] ec != null;
        **/

        DebugOut.println(20,"Creating new website named " + siteID);
        DebugOut.println(20,"Owner is " + owner.userID());

        Website newWebsite = newWebsite();
        ec.insertObject(newWebsite);
        newWebsite.setSiteID(siteID);
        newWebsite.banner().setBannerText(bannerText);
        newWebsite.setMegQuota(new Integer( SMApplication.appProperties().intPropertyForKey( "DefaultQuotaInMegabytes" )));
        DebugOut.println(30,"Editing Context inserted objects" + ec.insertedObjects());

        Group configGroup = ConfigureGroup.newGroup();
        ec.insertObject(configGroup);
        
        newWebsite.addObjectToBothSidesOfRelationshipWithKey(owner, "owner");
        newWebsite.addObjectToBothSidesOfRelationshipWithKey(configGroup, "configureGroup");
        newWebsite.addObjectToBothSidesOfRelationshipWithKey(configGroup, "childGroups");
        configGroup.addObjectToBothSidesOfRelationshipWithKey(owner, "users");
        newWebsite.addObjectToBothSidesOfRelationshipWithKey(associatedUnit, "parentOrgUnit");

        //newWebsite.createFolder(null, DEFAULT_UPLOADED_FILES_FOLDER_NAME);
        //newWebsite.createFolder(null, DEFAULT_DATABASE_TABLES_FOLDER_NAME);

        newWebsite.createDefaultHomeSection();

        JassAdditions.post("Website", "newWebsite [parentOrgUnit_set]", newWebsite.parentOrgUnit().equals(associatedUnit));
        JassAdditions.post("Website", "newWebsite [siteID_set]", newWebsite.siteID().equals(siteID));
        JassAdditions.post("Website", "newWebsite [banner_text_set]", newWebsite.banner().bannerText().equals(bannerText));
        JassAdditions.post("Website", "newWebsite [owner_set]", newWebsite.owner().equals(owner));
        JassAdditions.post("Website", "newWebsite [quota_set]", newWebsite.megQuota() != null);
        JassAdditions.post("Website", "newWebsite [has_groups]", newWebsite.childGroups().count() > 0);
        JassAdditions.post("Website", "newWebsite [configureGroup_set]", newWebsite.configureGroup() != null);
        JassAdditions.post("Website", "newWebsite [configureGroup_in_childGroups]", newWebsite.childGroups().containsObject(newWebsite.configureGroup()));
        JassAdditions.post("Website", "newWebsite [owner_in_configureGroup]", newWebsite.configureGroup().users().containsObject(owner));
        JassAdditions.post("Website", "newWebsite [has_uploadedFilesFolder]", newWebsite.uploadedFilesFolder() != null);
        JassAdditions.post("Website", "newWebsite [has_databaseTablesFolder]", newWebsite.databaseTablesFolder() != null);
       
        return newWebsite;

        /** ensure [result_not_null] Result != null; **/
    }


    
    /**
     * Returns a copy of a Website.  The copy is made manually because the embeddedSiteComponents relationship must not be copied as it only applies to the Website being copied.  If this relationship is copied, then Websites embedding this Website will also get dragged into the copy.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this Section
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(this);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(this);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(this, copy);

        // These need to reflect the date of copying, not the original site.
        copy.takeValueForKey(new NSTimestamp(), "dateCreated");
        copy.takeValueForKey(new NSTimestamp(), "dateLastModified");

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("banner"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("childGroups"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("configureGroup"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("databaseTables"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("files"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("folders"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("footer"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("sections"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("homeSection"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("owner"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("parentOrgUnit"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("previousHomeSection"));
        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("sectionStyle"));
        
        return copy;

        /** ensure
        [copy_made] Result != null;
        [embeddedSectionComponents_not_copied] ((Website)Result).embeddedSiteComponents().count() == 0;
        **/
    }



    /* ************ STATIC METHODS ***************** */

 
    



    public static boolean validateArrayOfWebsiteDictionaries(User currentUser,
                                                              NSArray dictArray,
                                                              EOEditingContext ec) {
        boolean valid = true;
        Enumeration e = dictArray.objectEnumerator();

        while(e.hasMoreElements())
        {
            // grab each dictionary in the array
            NSMutableDictionary newWebsiteDict = (NSMutableDictionary)e.nextElement();

            // get the associated org unit name and make it into an object
            String assocOrgUnitName = (String)newWebsiteDict.objectForKey(Website.ASSOC_UNIT_NAME);
            OrgUnit assocOrgUnit = OrgUnit.unitForUnitName(assocOrgUnitName, ec);

            String ownerID = (String)newWebsiteDict.objectForKey(Website.OWNER_ID_KEY);
            Website website = websiteFromDictionary(newWebsiteDict);
            NSMutableDictionary errors = validateWebsite(website.siteID(),
                                                         website.redirectURL(),
                                                         assocOrgUnit,
                                                         ownerID,
                                                         website.megQuota(),
                                                         website.fileSizeUsage(),
                                                         ec);

            if ( ! assocOrgUnit.userIsAdmin(currentUser))
            {
                errors.setObjectForKey("Cannot create unit: you are not a admin for the requested parent unit.",
                                        WEBSITE_OUT_OF_ADMIN_SCOPE_ERROR_KEY);
            }

            if (errors.count() > 0)
            {
                newWebsiteDict.setObjectForKey(errors, ERROR_MESSAGE_KEY);
                valid = false;
            }
        }

        return valid;
    }


    
    public static void createWebsitesFromArrayOfWebsiteDictionaries(NSArray dictArray,
                                                                     EOEditingContext ec) {

        /* Commented out until batch creation is fixed
        Enumeration e = dictArray.objectEnumerator();
         
         while(e.hasMoreElements()) {
            // grab each dictionary in the array
            NSMutableDictionary newWebsiteDict = (NSMutableDictionary)e.nextElement();

            // get the associated org unit name and make it into an object
            String assocOrgUnitName = (String)newWebsiteDict.objectForKey(Website.ASSOC_UNIT_NAME);
            OrgUnit assocOrgUnit = OrgUnit.unitForUnitName(assocOrgUnitName, ec);

            String ownerID = (String)newWebsiteDict.objectForKey(Website.OWNER_ID_KEY);

            //TODO need to check for example site ID
            Website.createWebsite(websiteFromDictionary(newWebsiteDict),
                                  assocOrgUnit,
                                  ownerID,
                                  ec);


        }
                */

    }


    
    protected static Website websiteFromDictionary(NSDictionary dict) {
        String fileSizeQuota = (String)dict.objectForKey(Website.QUOTA_KEY);

        Website newWebsite = newWebsite();

        newWebsite.setSiteID((String)dict.objectForKey(Website.SITE_ID_KEY));
        try {
            newWebsite.setMegQuota(new Integer(fileSizeQuota));
        }
        catch (NumberFormatException e) {
            newWebsite.setMegQuota(new Integer(SMApplication.appProperties().intPropertyForKey("DefaultQuotaInMegabytes")));
        }

        // If the redirectURL is missing, the empty string is in the dictionary.  Don't set this into the website as it does not validate correctly.
        if ((dict.objectForKey(Website.REDIRECT_URL_KEY) != null) &&
             (((String)dict.objectForKey(Website.REDIRECT_URL_KEY)).length() > 0))
        {
            newWebsite.setRedirectURL((String)dict.objectForKey(Website.REDIRECT_URL_KEY));
        }

        return newWebsite;
    }


    
    /**
     * Searches for and returns the Website with a siteID of aSiteID.  Returns null if such a Website can not be found.
     *
     * @param aSiteID- Website siteID to search for
     * @param ec editing context to fetch Website in
     *
     * @return the corresponding Website, or null if not found.
     */
    public static Website websiteForSiteID(String aSiteID, EOEditingContext ec)
    {
        /** require [valid_siteID] aSiteID != null;  [valid_ec] ec != null;  **/

        DebugOut.println(15, " = = = = = Fetching Website with ID: " + aSiteID);

        EOQualifier qualifier = new EOKeyValueQualifier("siteID", EOQualifier.QualifierOperatorCaseInsensitiveLike, aSiteID);
        EOFetchSpecification fetchSpec = new EOFetchSpecification("Website", qualifier, null);
        NSArray siteResults = ec.objectsWithFetchSpecification(fetchSpec);

        if (siteResults.count() > 1)
        {
            throw new RuntimeException("Found multiple Websites with a siteID of " + aSiteID);
        }

        return (siteResults.count() == 0) ? null : (Website) siteResults.objectAtIndex(0);
    }



    /**
     * Returns a list of all published Websites, orderd by their banner text.
     *
     * @return a list of all published Websites, orderd by their banner text.
     */
    public static NSArray publishedWebsites(EOEditingContext ec)
    {
        NSArray sortOrdering = new NSArray(new EOSortOrdering ("banner.bannerText", EOSortOrdering.CompareCaseInsensitiveAscending));

        EOFetchSpecification fetchSpec = new EOFetchSpecification("Website", canBeDisplayedQualifier(), sortOrdering);
        NSArray publishedWebsites = ec.objectsWithFetchSpecification(fetchSpec);

        return publishedWebsites;
    }



    /**
     * Returns an <code>EOQualifier</code> that determines if a Website can be displayed.  This is the case if it is both marked as published by the Website's configuration group and has permission to be published from Website's administrator.  This should when fetching Websites for public display (as opposed to configuration or administration).  See also canBeDisplayed().
     *
     * @return an <code>EOQualifier</code> that determines if a Website can be displayed
     */
    static public EOQualifier canBeDisplayedQualifier()
    {
        return EOQualifier.qualifierWithQualifierFormat("published = 'Y' AND publicationPermitted = 'Y'", new NSArray());
    }

   
    
    /* *************** DELETEABLE INTERFACE ********** */
    public boolean canBeDeleted() {
        return (errorMessages() == null);
    }


    
    public NSArray errorMessages()
    {
        NSMutableArray errorArray = new NSMutableArray();

        // Don't allow a Website to be deleted if it contains one of the special groups (public or internal users).  At present this is only true for the Admin website.  If the design is ever changed so that these groups are removed from the Admin Website then this restriction can also be removed.
        Enumeration groupEnumerator = childGroups().objectEnumerator();
        while (groupEnumerator.hasMoreElements())
        {
            Group aGroup = (Group)groupEnumerator.nextElement();
            if (aGroup.isSystemGroup())
            {
                errorArray.addObject("Website contains special Access Group " + aGroup.name());
            }
        }

        // TODO check any conditions that need to be met before a website is deleted and add an error message to an NSArray for any that have not been satisfied.

        if (errorArray.count() < 1)
            errorArray = null;

        return errorArray;
    }


    /* *************** INSTANCE METHODS ************** */


    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        if (megQuota() == null)
        {
            setMegQuota(new Integer(SMApplication.appProperties().intPropertyForKey("DefaultQuotaInMegabytes")));
        }

        if (showBanner() == null)
        {
            setIsBannerVisible(true);
        }

        if (showNavbar() == null)
        {
            setIsNavbarVisible(true);
        }

        if (showFooter() == null)
        {
            setIsFooterVisible(true);
        }

        if (published() == null)
        {
            setIsPublished(true);
        }

        if (publicationPermitted() == null)
        {
            setHasPermissionToPublish(true);
        }

        if (canReplicate() == null)
        {
            setIsReplicable(false);
        }

        if (fileSizeUsage() == null)
        {
            setFileSizeUsage(new Integer(0));
        }

        if (sectionStyle() == null)
        {
            setSectionStyle(SectionStyle.defaultSectionStyle(ec));
        }

        if (dateLastModified() == null)
        {
            setDateLastModified(new NSTimestamp());
        }

        if (dateCreated() == null)
        {
            setDateCreated(new NSTimestamp());
        }
    }



    /**
     * Creates a new SiteFileFolder in this Website named folderName.  If parentFolder is not null the new folder is made a child of this folder.  Websites are not allowed to contain two folders with the same name at the root level or under a parent folder.
     *
     * @param parentFolder null if this is to be a root level folder, or the direct parent folder if this is a child folder.
     * @param folderName the name of the folder to be created.
     * @return the newly created SiteFileFolder
     */
    public SiteFileFolder createFolder(SiteFileFolder parentFolder, String folderName)
    {
        /** require
        [folder_name_not_null] folderName != null;
        [parent_folder_valid] (parentFolder == null) || (parentFolder.editingContext().equals(editingContext()));
         **/
        
        SiteFileFolder newFolder = SiteFileFolder.createFolder(editingContext(), parentFolder, folderName);
        addObjectToBothSidesOfRelationshipWithKey(newFolder, "folders");

        return newFolder;

        /** ensure
        [result_not_null] Result != null;
        [name_matches] Result.name().equals(folderName);
        [editing_context_matches] Result.editingContext().equals(editingContext());
        [parent_folder_matches] Result.parentFolder() == parentFolder; **/
    }



    /**
     * Returns the SiteFileFolder from this Website which is named aName.  If parentFolder is null, then only root level folders are checked for a matching name.  If parentFolder is not null, only that folder is checked for a matching name.  Folder names are not case sensitive.
     *
     * @param aName the name of the folder to search for
     * @return the named folder or null if it is not found.
     */
    public SiteFileFolder folderNamed(SiteFileFolder parentFolder, String aName)
    {
        return null;
    }



    /**
     * Returns true if this Website contains a folder named aName.  If parentFolder is null, then only root level folders are checked for a matching name.  If parentFolder is not null, only that folder is checked for a matching name.  Folder names are not case sensitive.
     *
     * @param aName the name of the folder to search for
     * @return true if this folder directly contains a folder named aName.
     */
    public boolean hasFolderNamed(SiteFileFolder parentFolder, String aName)
    {
        return false;
    }



    /**
     * Returns the root level folder for SiteFiles uploaded to this Website.
     *
     * @return the root level folder for SiteFiles uploaded to this Website.
     */
     public SiteFileFolder uploadedFilesFolder()
    {
         return null;

         /** ensure
         [result_not_null] Result != null;
         [resulting_name_correct] Result.name().equalsIgnoreCase(DEFAULT_UPLOADED_FILES_FOLDER_NAME); **/
    }



    /**
     * Returns the root level folder for SiteFiles uploaded to Database Tables in this Website.
     *
     * @return the root level folder for SiteFiles uploaded to Database Tables in this Website.
     */
    public SiteFileFolder databaseTablesFolder()
    {
        return null;

        /** ensure
        [result_not_null] Result != null;
        [resulting_name_correct] Result.name().equalsIgnoreCase(DEFAULT_DATABASE_TABLES_FOLDER_NAME); **/
    }



    /**
     * Returns folders() sorted by the folder name.
     */
    public NSArray foldersByName()
    {
        return NSArrayAdditions.sortedArrayUsingComparator(folders(), SiteFileFolder.NameComparator);

        /** ensure
        [result_not_null] Result != null;
        [correct_number_of_folders] Result.count() == folders().count(); **/
    }


    
    /**
     * Returns <code>true</code> if aSection is the home section of this website.
     * 
     * @param aSection the section to test to see if it is the home section.
     * @return <code>true</code> if aSection is the home section of this website
     */
    public boolean isHomeSection(Section aSection) {
        return orderedSections().objectAtIndex(0).equals(aSection);
    }
    
    
    
    /**
     * Creates a default home page for a new Website.  This allows the Website to be immediately viewable removing the need to have the owner configure it before viewing it.
     *
     * TODO: make this protected and call from awakeFromInsertion after refactoring to remove spurious saveChanges()
     */
    public void createDefaultHomeSection()
    {
        /** require [home_section_is_null] homeSection() == null; **/
        
        SectionType sectionType = TextImageSectionType.getInstance(editingContext());
        createHomeSectionOfType(sectionType);
        ((TextImageLayout)homeSection().component()).textComponent().setText("<div align=\"center\">Under Construction</div>");

        /** ensure
        [home_section_not_null] homeSection() != null;
        [home_section_type_correct] homeSection().type() instanceof TextImageSectionType;
        **/
    }



    /**
     * Creates a new home page for this Website of the specified type.
     */
    public void createHomeSectionOfType(SectionType sectionType)
    {
        /** require
        [section_type_not_null] sectionType != null;
        [section_type_is_valid] (sectionType instanceof TextImageSectionType) || (sectionType instanceof ListOfLinksSectionType);
          **/
        
        Section newSection = Section.newSection();
        editingContext().insertObject(newSection);
        newSection.setName("Home");
        newSection.setDetails("");  // Don't set this.  SCR 779, no UI to edit for Home sections.
        sectionType.createComponents(newSection);
        
        addObjectToBothSidesOfRelationshipWithKey(newSection, "sections");
        
        Group everyone = PublicGroup.group(editingContext());
        newSection.addObjectToBothSidesOfRelationshipWithKey(everyone, "groups");
        newSection.setSectionOrder(nextSectionOrderNumber());

        // Aaaragh!  We need to save so the next line works! (depends on PK componentId not being null!) -ch
        editingContext().saveChanges();
        
        // Aaaragh!  It saves! -ch
        ((Layout)newSection.component()).layoutComponents();

        /** ensure [home_section_not_null] homeSection() != null; **/
    }

    

    /**
     * Returns <code>true</code> if the siteID is valid (not null, only valid URL characters, and not in restrictedWebsiteIDs()).  This really belongs in validateSiteID(String), the validation is all messed up right now, so this is an interim step.
     *
     * @return <code>true</code> if the siteID is valid (not null, only valid URL characters, and not in restrictedWebsiteIDs()).
     */
    static public boolean isValidSiteID(String siteIDToCheck)
    {
        // siteID is to be treated as case insensitive
        boolean isValidSiteID = (siteIDToCheck != null) && URLUtils.stringContainsOnlyValidURLChars(siteIDToCheck.toLowerCase());

        Enumeration invalidIDEnumeration = restrictedWebsiteIDs().objectEnumerator();
        while (isValidSiteID && invalidIDEnumeration.hasMoreElements())
        {
            isValidSiteID = ! siteIDToCheck.equalsIgnoreCase((String)invalidIDEnumeration.nextElement());
        }

        return isValidSiteID;
    }


    /**
     * Returns the list of siteIDs which are not allowed as they conflict with rewrite rules.  Extra items can be added to this list by settin RestrictedSiteIDs in GVCSiteMaker.plist or GVCSiteMakerCustom.plist.
     *
     * @return the list of siteIDs which are not allowed as they conflict with rewrite rules.
     */
    public static NSArray restrictedWebsiteIDs()
    {
        if (restrictedWebsiteIDs == null)
        {
            restrictedWebsiteIDs = new NSMutableArray(10);
            restrictedWebsiteIDs.addObject("files");			// For displayFile DA
            restrictedWebsiteIDs.addObject("da.data");			// For displayDataAccessFile DA
            restrictedWebsiteIDs.addObject("GVCSiteMaker");		// For image access
            restrictedWebsiteIDs.addObject("WebObjects");		// For image access
            restrictedWebsiteIDs.addObject("cgi-bin");			// For cgi-bin
            restrictedWebsiteIDs.addObject("apps");				// For mod_webobjects
            restrictedWebsiteIDs.addObject("index.html");		// For front page access
            restrictedWebsiteIDs.addObject("siteindex.html");	// For index DA
            restrictedWebsiteIDs.addObject("scripts");			// Blocked by anti-Nimda rewrite rule
            restrictedWebsiteIDs.addObject("msdac");			// Blocked by anti-Nimda rewrite rule
            restrictedWebsiteIDs.addObject("c");				// Blocked by anti-Nimda rewrite rule
            restrictedWebsiteIDs.addObject("d");				// Blocked by anti-Nimda rewrite rule
            restrictedWebsiteIDs.addObject("favicon.ico"); 		// Blocked by IE Favorites Icon rewrite rule
            restrictedWebsiteIDs.addObject("robots.txt"); 		// Used by search engine spiders

            // Add any extra items from configuration
            restrictedWebsiteIDs.addObjectsFromArray(SMApplication.appProperties().arrayPropertyForKey("RestrictedSiteIDs"));
        }

        return restrictedWebsiteIDs;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Validates the information to be used to create a new Website as copy of an existing Website (e.g. a template based site creation).  This is used for both on-line and batch creation of template based sites.
     *
     * @param associatedUnit OrgUnit that Website will be created under.
     * @param siteID ID for new Website
     * @param ownerID user ID for owner of Website
     * @param quota filespace quota in MB for Website
     * @param redirectUrl URL to redirect Website requests to
     * @param ec editing context to create Website in
     * @param adminUser used to determine if this user can copy the example site if permission is not explicitly given
     * @param exampleSiteID the siteID of the Website to base this new Website on
     *
     * @return <code>null</code> if the all the information is valid, or a dictionary of error messages and keys if it is not.
     */
    public static NSDictionary validateWebsiteCreationBasedOnExampleSite(OrgUnit associatedUnit,
                                                                         String siteID,
                                                                         String ownerID,
                                                                         Number quota,
                                                                         String redirectUrl,
                                                                         EOEditingContext ec,
                                                                         User adminUser,
                                                                         String exampleSiteID )
    {
        /** require
        [valid_associatedUnit] associatedUnit != null;
        [valid_editingContext] ec != null;
        [valid_example_site_id]  exampleSiteID != null;
        [valid_adminUser] adminUser != null;
        **/

        DebugOut.println(1, "Validating Website with ID of " + siteID + ", based on website with ID " + exampleSiteID +", for owner " + ownerID);

        NSMutableDictionary errors = validateWebsite(siteID, redirectUrl, associatedUnit, ownerID, quota, new Integer(0), ec);

        if ((errors.valueForKey(NULL_SITE_ID_ERROR_KEY) == null) && 
            (Website.websiteForSiteID(siteID, ec) != null))
        {
            errors.setObjectForKey("Requested SiteID already exists",
                                          SITE_ID_EXISTS_ERROR_KEY);
        }

        Website exampleSite = Website.websiteForSiteID(exampleSiteID, ec);
        if (exampleSite == null)
        {
            errors.setObjectForKey("The example Site ID you entered could not be found!", EXAMPLE_SITE_ID_NOT_FOUND_ERROR_KEY);
        }
        else if (exampleSite.childGroups().containsObject(PublicGroup.group(ec)))
        {
            // Do not allow copying root admin site!  This will also copy the Public or Internal Users groups.  That would be bad.
            errors.setObjectForKey("Copying the Admin site is not allowed.", EXAMPLE_SITE_ID_NOT_FOUND_ERROR_KEY);
        }

        if (exampleSite != null)
        {
            if (exampleSite.fileSizeUsage().longValue() > associatedUnit.availableSpaceInBytes())
            {
                errors.setObjectForKey("Creating a new web site based on the example site will exceed the authorized unit quota.",
                                        INVALID_FILESPACE_QUOTA_ERROR_KEY);
            }

            if (exampleSite.fileSizeUsage().longValue() > SMFileUtils.megaBytesToBytes(quota))
            {
                errors.setObjectForKey("The example site has over " + 
                        (int) SMFileUtils.bytesToMegaBytes(exampleSite.fileSizeUsage()) +
                        " MB in uploaded files, which exceeds the quota you have specified for this new site.",
                        INSUFFICIENT_FILESPACE_QUOTA_ERROR_KEY);
            }
            
            if ( !  (exampleSite.isReplicable() ||
                    adminUser.canManageWebsite(exampleSite)))
            {
                errors.setObjectForKey("Replication of the example site '" + exampleSiteID + "' is not permitted.",
                                        SITE_CAN_NOT_BE_REPLICATED_KEY);
            }
        }

        // If the site owner does not exists as a user, validate that it can be created.
        if ((ownerID != null) && User.userForUserID(ownerID, ec) == null)
        {
            NSArray userIDErrors = User.validateUserID(ownerID);

                if (userIDErrors.count() > 0)
                {
                    // Hack until validation messages here are refactored as in User
                    errors.setObjectForKey(userIDErrors.objectAtIndex(0), INVALID_CHARS_ERROR_KEY);
                }
        }

        return (errors.count() > 0) ? errors : null;
    }


    
    protected static NSMutableDictionary validateWebsite(String siteID,
                                                         String redirectUrl,
                                                         OrgUnit parentOrgUnit,
                                                         String ownerID,
                                                         Number quota,
                                                         Number fileSizeUsage,
                                                         EOEditingContext ec)
    {
        NSMutableDictionary errorMessages = new NSMutableDictionary();

        if ( StringAdditions.isEmpty(siteID))
        {
            errorMessages.setObjectForKey("Site ID is a required field!", NULL_SITE_ID_ERROR_KEY);
        }
        else
        {
            if ( !  Website.isValidSiteID(siteID))
            {
                errorMessages.setObjectForKey("This SiteID contains characters which are not permitted or this is a restricted name.",
                                              INVALID_SITE_ID_ERROR_KEY);
            }

            if ((redirectUrl != null) && ! URLUtils.hasValidURIScheme(redirectUrl))
            {
                errorMessages.setObjectForKey(URLUtils.invalidURLErrorMessage(redirectUrl), INVALID_REDIRECT_URL_ERROR_KEY);
            }
        }

        if (ownerID == null) {
            errorMessages.setObjectForKey("Owner ID is a required field!", NULL_OWNER_ID_ERROR_KEY);
        }

        if (parentOrgUnit == null) {
            errorMessages.setObjectForKey("Associated Unit is a required field!", NULL_ASSOCIATED_UNIT_ERROR_KEY);
        }

        if ((quota == null) || (quota.intValue() <= 0))
        {
            errorMessages.setObjectForKey("Filespace Quota must be a positive integer!", INVALID_FILESPACE_QUOTA_ERROR_KEY);
        }
        else if ((fileSizeUsage != null) &&
                 (SMFileUtils.megaBytesToBytes(quota) < fileSizeUsage.longValue()))
        {
            errorMessages.setObjectForKey("Requested quota is less than actual current usage!", INVALID_FILESPACE_QUOTA_ERROR_KEY);
        }
        else if (quota.intValue() > parentOrgUnit.fileSizeQuota().intValue())
        {
            errorMessages.setObjectForKey("Requested quota is more than quota in parent unit.  Enter " + parentOrgUnit.fileSizeQuota() + " MB or less", INVALID_FILESPACE_QUOTA_ERROR_KEY);
        }
        return errorMessages;
        /** ensure [valid_result] Result != null;  **/
    }


    
    /**
     * Validates the changes to this Website's properties and updates them if all the changes are valid.
     *
     * @param newAssociatedUnit the OrgUnit to move this Website to
     * @param newOwnerID the ID of the User who will be the Website's owner
     *
     * @return NSDictionary of error messages (if any).
     */
    public NSDictionary updateProperties(OrgUnit newAssociatedUnit, String newOwnerID)
    {
        NSMutableDictionary errors = validateWebsite(siteID(),
                                                     redirectURL(),
                                                     newAssociatedUnit,
                                                     newOwnerID,
                                                     megQuota(),
                                                     fileSizeUsage(),
                                                     editingContext());

        if (errors.valueForKey(NULL_SITE_ID_ERROR_KEY) == null)
        {
            Website tmpWebsite = Website.websiteForSiteID(siteID(), editingContext());
            if (tmpWebsite != null && ! tmpWebsite.equals(this))
            {
                errors.setObjectForKey("Requested SiteID already exists",
                                       SITE_ID_EXISTS_ERROR_KEY);
            }
        }

        //  Quota violation error
        //
        if ((newAssociatedUnit != parentOrgUnit()) &&
           this.fileSizeUsage().longValue() > newAssociatedUnit.availableSpaceInBytes())
        {
            errors.setObjectForKey("Cannot move web site, will violate quota in new unit.", INVALID_FILESPACE_QUOTA_ERROR_KEY);
        }

        User newOwner = null;
        if (errors.count() == 0)
        {
            newOwner = User.userForUserID(newOwnerID, editingContext());
            if (newOwner == null)
            {
                NSArray userIDErrors = User.validateUserID(newOwnerID);
                if (userIDErrors.count() == 0)
                {
                    newOwner = User.createUser(editingContext(), newOwnerID);
                }
                else
                {
                    // Hack until validation messages here are refactored as in User
                    errors.setObjectForKey(userIDErrors.objectAtIndex(0), INVALID_CHARS_ERROR_KEY);
                }
            }
        }

        if ((errors == null) || (errors.count() == 0))
        {
            // For return value
            errors = null;

            if ( ! owner().equals(newOwner))
            {
                switchOwnerTo(newOwner);
            }

            if ( ! parentOrgUnit().equals(newAssociatedUnit))
            {
                OrgUnit oldOrgUnit = parentOrgUnit();
                this.removeObjectFromBothSidesOfRelationshipWithKey(parentOrgUnit(), "parentOrgUnit");
                this.addObjectToBothSidesOfRelationshipWithKey(newAssociatedUnit, "parentOrgUnit");
                // If we are changing units, we need to update filesizes.
                updateFileSizeUsage();
                oldOrgUnit.updateFileSizeUsage();
            }
        }

        return errors;
    }



    /**
     * Transfers ownership of this Website to newOwner.  The old owner is replaced with the new owner 
     * in all groups.  All other group members stay the same.
     *
     * @param newOwner the User to transfer ownership of this Website to
     */
    public void switchOwnerTo(User newOwner)
    {
        /** require
        [valid_new_owner] newOwner != null;
        [new_owner_different] ! owner().equals(newOwner);
        [same_ec] editingContext() == newOwner.editingContext();  **/

        User originalSiteOwner = owner();

        removeObjectFromBothSidesOfRelationshipWithKey(originalSiteOwner, "owner");
        addObjectToBothSidesOfRelationshipWithKey(newOwner, "owner");
        
        for (int i = 0; i < childGroups().count(); i++)
        {
            Group aGroup = (Group)childGroups().objectAtIndex(i);

            if (aGroup instanceof LocalGroup)
            {
                LocalGroup localGroup = (LocalGroup) aGroup;
                localGroup.removeObjectFromBothSidesOfRelationshipWithKey(originalSiteOwner, "users");

                if ( ! localGroup.users().containsObject(newOwner))
                {
                    localGroup.addObjectToBothSidesOfRelationshipWithKey(newOwner, "users");
                }
            }
        }

        /** ensure
        [new_owner_set] owner().equals(newOwner);
        [new_owner_in_groups] (forall i : {0 .. childGroups().count() - 1} # ( ! (childGroups().objectAtIndex(i) instanceof LocalGroup)) || 
                                                                             ((LocalGroup)childGroups().objectAtIndex(i)).users().containsObject(newOwner));  **/
    }


    
    /* ************ QUOTA METHODS ***************** */
    /**
     * Returns the exact amount of available space remaining in bytes.  This can be less than the remaining, unused, quota for this Website if our parentOrgUnit(), or any OrgUnit higher in the hierarchy has less reamining quota space.  Effectively this is the minimum remaninging quota of this Website and any Org Unit from our parentOrgUnit() up to the root unit.
     *
     * @return the exact amount of quota remaining in bytes.
     */
    public long availableSpaceInBytes()
    {
        long orgUnitAvailableQuotaInBytes = parentOrgUnit().availableSpaceInBytes();
        long availableBytes = SMFileUtils.megaBytesToBytes(megQuota()) - fileSizeUsage().longValue();
        DebugOut.println(30, "megQuota() = " + megQuota());
        DebugOut.println(30, "megaBytesToBytes(megQuota()) = " + SMFileUtils.megaBytesToBytes(megQuota()));
        DebugOut.println(30, "fileSizeUsage() = " + fileSizeUsage());
        DebugOut.println(30, "websiteAvailableQuotaInBytes = " + availableBytes);

        // The quota for the OrgUnit which owns this Website overrides the quota set on this Website if it is lower.
        if (orgUnitAvailableQuotaInBytes < availableBytes)
        {
            availableBytes = orgUnitAvailableQuotaInBytes;
        }

        // Handle over quota problems caused by bugs, calculation errors etc.
        // Is this needed?  -ch
        if (availableBytes < 0)
        {
            availableBytes = 0;
        }

        DebugOut.println(30, "orgUnitAvailableQuotaInBytes = " + orgUnitAvailableQuotaInBytes);
        DebugOut.println(30, "availableBytes = " + availableBytes);

        return availableBytes;
    }

    

    /**
     * Returns the approximate amount of quota used as a percentage.  For example, a return value 15.5 indicates 15.5% of the quota has been used.
     *
     * @return the approximate amount of quota used as a percentage.
     */
    public double percentageOfQuotaUsed()

    {
        return fileSizeUsage().floatValue() / SMFileUtils.megaBytesToBytes(megQuota()) * 100.0;
    }



    /**
     * Returns the approximate amount of quota used in megabytes.  For example, a return value 5.5 indicates 5.5 MB of the quota has been used.
     *
     * @return the approximate amount of quota used in megabytes.
     */
    public float fileSizeUsageInMegabytes()
    {
        return SMFileUtils.bytesToMegaBytes(fileSizeUsage());
    }

    

    /**
     * Returns the approximate amount of unused quota in megabytes.  For example, a return value 5.5 indicates 5.5 MB of the quota has not been used yet.
     *
     * @return the approximate amount of unused quota in megabytes.
     */
    public Float remainingQuotaInMegaBytes()
    {
        return new Float(SMFileUtils.bytesToMegaBytes(SMFileUtils.megaBytesToBytes(megQuota()) - fileSizeUsage().longValue()));
    }



    /**
     * Returns the approximate amount of available space for SiteFiles in megabytes.  For example, a return value 5.5 indicates 5.5 MB of file system space was available.  This will return a lower number than remainingQuotaInMegaBytes() if the OrgUnit which contains the Website, or OrgUnits above it, have less available space than remainingQuotaInMegaBytes().
     *
     * @return the approximate amount of vailable space for SiteFiles in megabytes.
     */
    public Float availableSpaceInMegaBytes()
    {
        return new Float(SMFileUtils.bytesToMegaBytes(availableSpaceInBytes()));
    }



    /**
     * Updates fileSizeUsage() to reflect the current list of files uploaded to this Website and also updates the Website's OrgUnit.
     */
    public void updateFileSizeUsage()
    {
        Enumeration fileEnumerator = files().objectEnumerator();
        long totalSize = 0;

        while (fileEnumerator.hasMoreElements())
        {
            SiteFile aFile = (SiteFile)fileEnumerator.nextElement();
            totalSize += aFile.fileSizeUsage().longValue();
        }

        setFileSizeUsage(new Long(totalSize));

        // Let our OrgUnit know that it should update too.
        if (parentOrgUnit() != null)
        {
            parentOrgUnit().updateFileSizeUsage();
        }
    }



    /**
     * Returns this website's parent org unit and all parent org units up to the root org unit.  Org
     * Units not displayed in the system wide list are excluded.
     *
     * @return this website's parent org unit and all parent org units up to the root org unit
     */
    public NSArray allParentOrgUnits()
    {
        return parentOrgUnit().orgUnitPath();

        /** ensure [valid_result] Result != null;  **/
     }


    /**
     * Returns the Sections in this Website (less the homeSection() and previousHomeSection()), sorted by the section order.
     *
     * @return the Sections in this Website (less the homeSection() and previousHomeSection()), sorted by the section order.
     * @deprecated Don't use this!
     */
    public NSMutableArray nonHomeSections()
    {
        NSMutableArray nonHomeSections = sortSections(new NSMutableArray(sections()));
        nonHomeSections.removeObject(homeSection());


        DebugOut.println(30,"====Returning non-home Sections: " + nonHomeSections.valueForKey("name"));

        return nonHomeSections;

        /** ensure
        [return_valid] Result != null;
        [home_not_included] ! Result.containsObject(homeSection());
        [all_non_home_sections_included] (forall i : {0 .. sections().count() - 1} #
                                          ((Section)sections().objectAtIndex(i)).isHomeSection() ||
                                          Result.containsObject(sections().objectAtIndex(i)) );    **/
    }





    /**
     * Returns the Sections in this Website, sorted by the section order.
     *
     * @return the Sections in this Website, sorted by the section order
     */
    public NSArray orderedSections()
    {
        return sortSections(new NSMutableArray(sections()));

        /** ensure
        [return_valid] Result != null;
        [all_non_home_sections_included] (forall i : {0 .. sections().count() - 1} #
                                          Result.containsObject(sections().objectAtIndex(i)) );    **/
    }



    /**
     * Returns the Sections in this Website (including the homeSection() but not previousHomeSection()), which are of a Section Type which can be embedded.  The returned Sections are sorted by the section order.
     *
     * @return the Sections in this Website, which are of a Section Type which can be embedded, sorted by the section order.
     */
    public NSArray embeddableSections()
    {
        NSMutableArray embeddableSections = new NSMutableArray();
        NSMutableArray sortedSections = nonHomeSections();
        sortedSections.insertObjectAtIndex(homeSection(), 0);
        
        Enumeration sectionEnum = sortedSections.objectEnumerator();
        while (sectionEnum.hasMoreElements())
        {
            Section aSection = (Section) sectionEnum.nextElement();
            if (aSection.canBeEmbedded())
            {
                embeddableSections.addObject(aSection);
            }
        }

        DebugOut.println(30,"====Returning embeddableSections Sections: " + embeddableSections.valueForKey("name"));

        return embeddableSections;

        /** ensure
        [return_valid] Result != null;
        [all_embeddable_sections_included] (forall i : {0 .. sections().count() - 1} #
                                             ( ! ((Section)sections().objectAtIndex(i)).canBeEmbedded()) ||
                                             Result.containsObject(sections().objectAtIndex(i)) );
        [non_embeddable_sections_excluded] (forall i : {0 .. Result.count() - 1} #
                                            ((Section)Result.objectAtIndex(i)).canBeEmbedded());       **/
    }

    
    
    public NSMutableArray sortSections(NSMutableArray sections) {
        DebugOut.println(30, siteID() + "Unsorted Sections: " + sections.valueForKey("name"));
        
        EOSortOrdering sectionOrdering =
        EOSortOrdering.sortOrderingWithKey("sectionOrder", EOSortOrdering.CompareAscending);

        NSArray sortOrdering = new NSArray(sectionOrdering);

        EOSortOrdering.sortArrayUsingKeyOrderArray(sections, sortOrdering);

        DebugOut.println(30, siteID() + "Sorted Sections: " + sections.valueForKey("name"));

        return sections;
    }



    /**
     * Returns true if otherWebsite is already linked below the current site.  This test is used to prevent creating an infinite recursive loop of sites linked within each other.
     *
     * @return true if otherWebsite is already linked below the current site.
     */
    public boolean containsSite(Website otherWebsite)
    {

        /** require
        [otherWebsite_valid] otherWebsite != null;
        [otherWebsite_not_this] otherWebsite != this;
        [otherWebsite_in_same_ec] editingContext().equals(otherWebsite.editingContext());
         **/

        DebugOut.println(16, " = = = = = Testing to see if " + siteID() + " contains: [" + otherWebsite.siteID() + "]");
        boolean contains = false;
        Enumeration sectionEnum = sections().objectEnumerator();

        while(( !  contains) && sectionEnum.hasMoreElements())
        {
            Section aSection = (Section) sectionEnum.nextElement();

            if (aSection.hasLinkedSite())
            {
                contains = (aSection.linkedSite().equals(otherWebsite) ||
                            aSection.linkedSite().containsSite(otherWebsite));
            }
        }

        DebugOut.println(16, " = = = = = " + siteID() + " contains: [" + otherWebsite.siteID() + "]: [" + contains + "]");

        return contains;
    }



    /**
     * Returns true if theSection is in this Website or can be reached by navigating the linkedSite() of sections.
     *
     * @return true if theSection is in this Website or can be reached by navigating the linkedSite() of sections.
     */
    public boolean containsSection(Section theSection)
    {
        /** require
        [theSection_valid] theSection != null;
        [theSection_in_same_ec] editingContext().equals(theSection.editingContext());
         **/

        boolean contains = false;
        Enumeration sectionEnum = sections().objectEnumerator();

        while( ( ! contains) && sectionEnum.hasMoreElements())
        {
            Section aSection = (Section) sectionEnum.nextElement();

            if (aSection.equals(theSection))
            {
                contains = true;
            }
            else if (aSection.hasLinkedSite())
            {
                contains = aSection.linkedSite().containsSection(theSection);
            }
        }

        return contains;
    }



    /**
     * Returns the section with the passed name or null if it cannot be found.  If the section name is Section.HomeSectionName returns the current home section.
     *
     * @param sectionName the normalized name (lower case) for the section to return
     * @return the section with the passed name or null if it cannot be found.
     */
    public Section sectionForNormalizedName(String sectionName)
    {
        /** require
        [section_name_not_null] sectionName != null;
        [section_name_is_lower_case] sectionName.toLowerCase().equals(sectionName); **/
        
        Section sectionForNormalizedName = null;

        // Handle special case where two sections can both be named home.
        if (sectionName.equalsIgnoreCase("Home"))
        {
            sectionForNormalizedName = homeSection();
        }
        else // Search through the list
        {
            Enumeration sectionEnum = sections().objectEnumerator();

            while(sectionEnum.hasMoreElements())
            {
                Section aSection = (Section)sectionEnum.nextElement();
                if (aSection.normalName().equals(sectionName))
                {
                    sectionForNormalizedName = aSection;
                    break;
                }
            }
        }

        return sectionForNormalizedName;
    }

    

    public boolean hasSectionWithNormalizedName(String sectionNormalName) {
        Enumeration sectionEnum = sections().objectEnumerator();

        while(sectionEnum.hasMoreElements()) {
            Section aSection = (Section)sectionEnum.nextElement();
            if (aSection.normalName().equals(sectionNormalName))
                return true;
        }
        return false;
    }



    /**
     * Removes aSection from this Website and updates any Websites embedding this Section (via delete rule).  The Home sections can not be removed.
     *
     * @param aSection the Section to remove
     */
    public void removeSection(Section aSection)
    {
        /** require
        [valid_section] aSection != null;
        [section_exists] sections().containsObject(aSection);
        [not_home_section] ! aSection.isHomeSection();     **/

        DebugOut.println(3,"Removing section " + aSection.name() + " from site " + siteID());
        removeObjectFromBothSidesOfRelationshipWithKey(aSection, "sections");

        DebugOut.println(3,"Deleting section from ec " + aSection.name());
        editingContext().deleteObject(aSection);

        editingContext().processRecentChanges();  // Needed for post condition

        /** ensure [section_has_been_removed] ! sections().containsObject(aSection); **/
    }



    /**
     * Returns the Local Groups for this Website sorted by name.
     *  
     * @return the Local Groups for this Website sorted by name
     */
    public NSArray orderedLocalGroups()
    {
        NSMutableArray localGroups = new NSMutableArray(childGroups().count());
        Enumeration groupEnumerator = childGroups().objectEnumerator();
        while (groupEnumerator.hasMoreElements())
        {
            Group aGroup = (Group) groupEnumerator.nextElement();
            if (aGroup instanceof LocalGroup)
            {
                localGroups.addObject(aGroup);
            }
        }
       
        NSArray orderedLocalGroups;
        
        try
        {
            orderedLocalGroups = localGroups.sortedArrayUsingComparator(
                new KeyValueComparator("name", NSComparator.OrderedAscending));
        }
        catch (ComparisonException e)
        {
            throw new ExceptionConverter(e);
        }
        
        return orderedLocalGroups;
    }



    /**
     * Returns the group named <code>name</code> from this Website, or null if a group with that name is not found.
     *
     * @return the group named <code>name</code> from this Website, or null if a group with that name is not found.
     *
     * @param name the name of the group to return
     */
    public Group groupNamed(String name)
    {
        /** require
        [name_not_null] name != null;
        [editing_context_not_null] editingContext() != null; **/
        
        Group fetchedGroup;

        NSMutableArray qualifierArgs = new NSMutableArray();
        qualifierArgs.addObject(name);
        qualifierArgs.addObject(this);

        // BUG SQL is 'parent_website is null' when Website unsaved.
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("name caseInsensitiveLike %@ AND parentWebsite = %@",qualifierArgs);
        EOFetchSpecification fetchSpec = new EOFetchSpecification("Group", qualifier, null);

        NSArray groupResult = editingContext().objectsWithFetchSpecification(fetchSpec);

        if (groupResult.count() == 1)
        {
            DebugOut.println(1,"Fetched group named " + name + " for site " + siteID());
            fetchedGroup =  (Group)groupResult.objectAtIndex(0);
        }
        else if (groupResult.count() == 0)
        {
            DebugOut.println(1,"Failed to fetch group named " + name + " for site " + siteID());
            fetchedGroup =  null;
        }
        else
        {
            // Panic!
            throw new RuntimeException("Got " + groupResult.count() + " groups for key " + name + " from fetch.");
        }

        return fetchedGroup;

        /** ensure [valid_returned_group] (Result == null) || (Result.name().equalsIgnoreCase(name));  **/
    }



    /**
     * Creates and returns a new LocalGroup, named aGroupName, attached to this Website with the Website owner as the Group's only member.
     *
     * @param aGroupName the name to give to the created Group.
     *
     * @return the newly created Group
     */
    public LocalGroup newLocalGroupNamed(String aGroupName)
    {
        /** require [valid_name] aGroupName != null;  [not_duplicate_name] groupNamed(aGroupName) == null;  **/
        
        LocalGroup newGroup = (LocalGroup)LocalGroup.newGroup();
        editingContext().insertObject(newGroup);
        newGroup.setName(aGroupName);

        addObjectToBothSidesOfRelationshipWithKey(newGroup, "childGroups");

        // The site owner is a mandatory member of all groups in a Website
        newGroup.addObjectToBothSidesOfRelationshipWithKey(owner(), "users");

        return newGroup;
        
        /** ensure
        [valid_new_group] (Result != null) && (Result.name().equals(aGroupName));
        [website_set] Result.parentWebsite() == this;
        [owner_added] Result.users().containsObject(owner());
        [group_added] childGroups().containsObject(Result);      **/
    }



 

    /**
     * Removes aGroup from this Website.  The configureGroup() cannot be removed.
     *
     * @param aGroup the Group to remove
     */
    public void removeGroup(Group aGroup)
    {
        /** require
        [valid_group] aGroup != null;
        [group_exists] childGroups().containsObject(aGroup);
        [not_configure_group] ! aGroup.equals(configureGroup());     **/
        
        removeObjectFromBothSidesOfRelationshipWithKey(aGroup, "childGroups");
        editingContext().deleteObject(aGroup);
        editingContext().processRecentChanges();  // Needed for post condition

        /** ensure [group_has_been_removed] ! childGroups().containsObject(aGroup); **/
    }


    
    public NSArray groupsSansSpecialGroups()
    {
        NSMutableArray groupsSansSpecialGroups = new NSMutableArray(childGroups().count());

        Enumeration groupEnum = childGroups().objectEnumerator();
        while (groupEnum.hasMoreElements())
        {
            Group aGroup = (Group)groupEnum.nextElement();
            if ( !  aGroup.isSystemGroup())
            {
                groupsSansSpecialGroups.addObject(aGroup);
            }
        }

        return groupsSansSpecialGroups;
    }

    

    public boolean containsFileWithFilename(String uploadedFilename) {
        Enumeration fileEnum = uploadedFilesFolder().files().objectEnumerator();

        while(fileEnum.hasMoreElements()) {
            SiteFile aFile = (SiteFile)fileEnum.nextElement();
            if (aFile.uploadedFilename().equals(uploadedFilename))
                return true;
        }
        return false;
    }


    
    public SiteFile fileForFilename(String uploadedFilename) {
        Enumeration fileEnum = uploadedFilesFolder().files().objectEnumerator();

        while(fileEnum.hasMoreElements()) {
            SiteFile aFile = (SiteFile)fileEnum.nextElement();
            if (aFile.uploadedFilename().equals(uploadedFilename))
                return aFile;
        }
        return null;
    }



    /**
     * Returns the URL for this Website using http:// and the domain defined in GVCSiteMaker.plist as DomainName.
     *
     * @return the URL for this Website using http:// and the domain defined in GVCSiteMaker.plist as DomainName.
     */
    public String siteURL()
    {
        return SMActionURLFactory.siteURL(siteID());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL for this Website using http:// and the domain specified in the request.
     *
     * @param aRequest the request to take the domain name from
     *
     * @return the URL for this Website using http:// and the domain specified in the request.
     */
    public String siteURL(WORequest aRequest)
    {
        
        return SMActionURLFactory.siteURL(siteID(), aRequest);

        /** ensure [result_not_null] Result != null; **/
    }



    
    public int nonImageFilesCount(){
        return (uploadedFilesFolder().files().count() - imageFiles().count());
    }


    
    public NSArray imageFiles() {
        NSArray mimeTypes = MIMEUtils.imageMimeTypes();
        NSMutableArray imageFiles = new NSMutableArray();
        Enumeration fileEnum = uploadedFilesFolder().orderedFiles().objectEnumerator();

        while(fileEnum.hasMoreElements()) {
            SiteFile aFile = (SiteFile)fileEnum.nextElement();
            if (mimeTypes.containsObject(aFile.mimeType())){
                imageFiles.addObject(aFile);
            }
        }
        return imageFiles;
    }



    public Integer nextSectionOrderNumber() {
        DebugOut.println(18,"Entering...");
        Integer maxVal;

        maxVal = (Integer)sections().valueForKey("@max.sectionOrder");
        DebugOut.println(18,"==== Got maxVal" + maxVal);
        if (maxVal == null) 
            return new Integer(1);
        return new Integer(maxVal.intValue() + 1);
    }



    /**
     * Returns the siteID followed by a dash and the banner text.  This can be used to unambiguously identify the site to a user.
     *
     * @return the siteID followed by a dash and the banner text.
     */
    public String bannerAndSiteID()
    {
        return siteID() + " - " + banner().bannerText();

        /** ensure [result_not_null] Result != null; **/
    }


    
    /**
     * Adds <code>aSiteFile</code> to the files attached to this site, adds it to the public access group, and inserts it into the editing context.  Do not use for Database Table files as it grants public access!
     */
    public void addSiteFile(SiteFile aSiteFile)
    {
        /** require
        [a_site_file_not_null] aSiteFile != null;
        [editing_context_not_null] editingContext() != null;
        [file_not_already_uploaded] ! uploadedFilesFolder().files().containsObject(aSiteFile); **/
        
        editingContext().insertObject(aSiteFile);
        aSiteFile.addObjectToBothSidesOfRelationshipWithKey(this, "website");

        aSiteFile.addObjectToBothSidesOfRelationshipWithKey(uploadedFilesFolder(), "folder");
        updateFileSizeUsage();

        // Is this really needed?  It seems like membership in this group could be implicit...  -ch
        Group everyone = PublicGroup.group(editingContext());
        aSiteFile.addObjectToBothSidesOfRelationshipWithKey(everyone, "groups");

        /** ensure [file_is_sucessfully_added] uploadedFilesFolder().files().containsObject(aSiteFile); **/
    }

    

    /**
     * Removes <code>aSiteFile</code> from the files attached to this site, and deletes it from the 
     * editing context.  It does <b>not</b> delete it from this filesystem.  This will be done after 
     * saving by the NotifyingEditingContext.
     */
    public void removeSiteFile(SiteFile aSiteFile)
    {
        /** require
        [a_site_file_not_null] aSiteFile != null;
        [editing_context_not_null] editingContext() != null;
        [file_is_currently_uploaded] uploadedFilesFolder().files().containsObject(aSiteFile); **/
        
        DebugOut.println(3,"Deleting file from ec " + aSiteFile.filename());
        // Need to do this before removeObjectFromBothSidesOfRelationshipWithKey.  SiteFile can't
        // call it from willDelete as it will no longer have a Website reference.  Can't just call
        // deleteObject() as the website will still have a reference to this file and quota won't
        // get recalculated correctly.
        notifyFileDeleted(aSiteFile);  
        removeObjectFromBothSidesOfRelationshipWithKey(aSiteFile, "files");
        editingContext().deleteObject(aSiteFile);

        /** ensure [file_has_been_removed] editingContext().deletedObjects().containsObject(aSiteFile); **/
    }



    /**
     * Call this to notify this Website that one of its SiteFiles has been (or will be) removed.
     * The website will recalculate its quota usage at save time (after deletion rules have been 
     * processed) if this has been called.  SiteFile calls this in its <code>willDelete()</code> 
     * method.
     * 
     * @param whichFile the SiteFile which will be deleted
     */
    public void notifyFileDeleted(SiteFile whichFile)
    {
        /** require [valid_sitefile] whichFile != null;  **/
        willChange();
        didRemoveFiles = true;
        /** ensure [file_deletion_flag_set] didRemoveFiles;    **/
    }
    
    

    public boolean hasFiles()
    {
        return files().count() > 0;
    }



    public boolean hasUploadedFiles()
    {
        return uploadedFilesFolder().numberOfFiles() > 0;
    }



    /**
     * Returns <code>true</code> if there is more than one Database Table with this name.
     *
     * @return <code>true</code> if there is more than one Database Table with this name.
     **/
    public boolean isDuplicateDatabaseTableName(String aName)
    {
        /** require [valid_param] (aName != null) && (aName.length() > 0);  **/

        aName = aName.trim();

        int databaseTableCount = 0;
        Enumeration tableEnumerator = databaseTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            Table aTable = (Table) tableEnumerator.nextElement();
            if (aName.equalsIgnoreCase(aTable.name()))
            {
                databaseTableCount++;
            }
        }

        return (databaseTableCount > 1);
    }



    /**
     * Adds the passed table to this Website.
     *
     * @param newTable the Table to add to this Website.
     */
    public void addDatabaseTable(Table newTable)
    {
        /** require
        [valid_param] newTable != null;
        [param_in_our_ec] newTable.editingContext().equals(editingContext());
        **/

        addObjectToBothSidesOfRelationshipWithKey(newTable, "databaseTables");

        /** ensure
        [website_has_table] databaseTables().containsObject(newTable);
        [table_has_website] ((com.gvcsitemaker.core.interfaces.SMTable)newTable).website().equals(this);
        **/
    }



    /**
     * Returns a sorted list of the Database Tables (SMVirtualTable etc.) in this Website.
     *
     * @return a sorted list of the Database Tables (SMVirtualTable etc.) in this Website.
     */
    public NSArray orderedDatabaseTables()
    {
        return NSArrayAdditions.sortedArrayUsingComparator(databaseTables(), Table.NameComparator);
        /** ensure [valid_result] Result != null; [all_tables_returned] Result.count() == databaseTables().count(); **/
    }



    /**
     * Returns <code>true</code> if there are any Database Tables in this Website.
     *
     * @return <code>true</code> if there are any Database Tables in this Website.
     */
    public boolean hasDatabaseTables()
    {
        return databaseTables().count() > 0;
    }



    /**
     * Returns <code>true</code> if the User can configure this Website.  This is the case if the user is in this Website's configure group, the user is an administrator of this or a containing OrgUnit, or if the User is a system administrator
     *
     * @return <code>true</code> if this Website can be displayed.
     */
    public boolean canBeConfiguredByUser(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/
        
        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);
        }

        return (configureGroup().users().containsObject(aUser) ||
                 parentOrgUnit().userIsAdmin(aUser) ||
                 aUser.isSystemAdmin());
    }



    /**
     * Returns <code>true</code> if this Website can be displayed.  This is the case if it is both marked as published by the Website's configuration group and has permission to be published from Website's administrator.  This should be checked before displaying any sections from this Website.    See also canBeDisplayedQualifier().
     *
     * @return <code>true</code> if this Website can be displayed.
     */
    public boolean canBeDisplayed() {
        DebugOut.println(10, " isPublished(): " + isPublished());
        DebugOut.println(10, "hasPermissionToPublish(): " +hasPermissionToPublish());
        return isPublished() && hasPermissionToPublish();
    }

    

    /**
     * Returns <code>true</code> if this Website is marked as published by the Website's configuration group.
     *
     * @return <code>true</code> if this Website is marked as published by the configuration group.
     */
    public boolean isPublished()
    {
        return published().equals("Y");
    }



    /**
     * Controls if this Website is marked as published by the Website's configuration group.
     *
     * @param value	<code>true</code> if this Website is marked as published by the Website's configuration group.
     */
    public void setIsPublished(boolean value)
    {
        setPublished(value ? "Y" : "N");
    }



    /**
     * Returns <code>true</code> if this Website has permission to be published from Website's administrator.
     *
     * @return <code>true</code> if this Website has permission to be published from Website's administrator
     */
    public boolean hasPermissionToPublish()
    {
        return publicationPermitted().equals("Y");
    }



    /**
     * Controls if this Website has permission to be published from Website's administrator
     *
     * @param value	<code>true</code> if this Website has permission to be published.
     */
    public void setHasPermissionToPublish(boolean value)
    {
        setPublicationPermitted(value ? "Y" : "N");
    }



    /**
     * Returns <code>true</code> if this Website can be used as a template when creating a new Website
     *
     * @return <code>true</code> if this Website can be used as a template when creating a new Website
     */
    public boolean isReplicable()
    {
        return canReplicate().equals("Y");
    }



    /**
     * Returns <code>true</code> if the Banner is displayed for Sections in this Website.
     *
     * @return <code>true</code> if the Banner is displayed for Sections in this Website.
     */
    public boolean isBannerVisible()
    {
        return showBanner().equals("Y");
    }



    /**
     * Controls if the Banner is displayed for Sections in this Website.
     *
     * @param value <code>true</code> if the Banner should be displayed for Sections in this Website.
     */
    public void setIsBannerVisible(boolean value)
    {
        setShowBanner(value ? "Y" : "N");
    }



    /**
     * Returns <code>true</code> if the NavBar is displayed for Sections in this Website.
     *
     * @return <code>true</code> if the Navbar is displayed for Sections in this Website.
     */
    public boolean isNavbarVisible()
    {
        return showNavbar().equals("Y");
    }



    /**
     * Controls if the NavBar is displayed for Sections in this Website.
     *
     * @param value <code>true</code> if the NavBar should be displayed for Sections in this Website.
     */
    public void setIsNavbarVisible(boolean value)
    {
        setShowNavbar(value ? "Y" : "N");        willChange();
    }



    /**
     * Returns <code>true</code> if the Footer is displayed for Sections in this Website.
     *
     * @return <code>true</code> if the Footer is displayed for Sections in this Website.
     */
    public boolean isFooterVisible()
    {
        return showFooter().equals("Y");
    }



    /**
     * Controls if the Footer is displayed for Sections in this Website.
     *
     * @param value <code>true</code> if the Footer should be displayed for Sections in this Website.
     */
    public void setIsFooterVisible(boolean value)
    {
        setShowFooter(value ? "Y" : "N");
    }


    /**
     * Controls if this Website can be used as a template when creating a new Website
     *
     * @param value <code>true</code> if this Website can be used as a template when creating a new Website
     */
    public void setIsReplicable(boolean value)
    {
        setCanReplicate(value ? "Y" : "N");
    }



    /**
     * Returns <code>true</code> if this Website is current redirected to a URL.
     *  
     * @return <code>true</code> if this Website is current redirected to a URL
     */
    public boolean isRedirected() 
    {
        return  ! StringAdditions.isEmpty(redirectURL());
    }
    
    
    
    /**
     * Sends a message to the Website Owner and co-owners and records the message in the change log.
     * 
     * @param fromUser User sending this message
     * @param subject subject line of the message
     * @param body body of the message
     */
    public void sendMessageToOwners(User fromUser, String subject, String body)
    {
        /** require [valid_fromUser] fromUser != null;
                    [fromUser_in_same_ec] editingContext().equals(fromUser.editingContext());
                    [valid_subject] subject != null;                                           **/
        
        SendEmail.composePlainTextEmail(fromUser.emailAddress(),
                                        configureGroup().membersEmailAddresses(),
                                        NSArray.EmptyArray,
                                        subject,
                                        (body != null ? body : ""));
                                        
        StringBuffer notes = new StringBuffer();
        notes.append("Message to Owners");
        notes.append("\nSubject: ");
        notes.append(subject);
        notes.append("\nMessage: ");
        if ( ! StringAdditions.isEmpty(body))
        {
            notes.append(body);
        }

        createChangeLog(fromUser, notes.toString(), null);
    }
    
    
    
    /**
     * Returns any WebsiteChangeLogs for this Website, ordered with the most recent first.
     * 
     * @return any WebsiteChangeLogs for this Website
     */
    public NSArray orderedChangeLogs()
    {
        /** require [is_in_ec] editingContext() != null;  **/
        EOQualifier qualifer = new EOKeyValueQualifier("website", EOQualifier.QualifierOperatorEqual, this);
        NSArray ordering = new NSArray(new EOSortOrdering(WebsiteChangeLog.DATERECORDED, 
                                                          EOSortOrdering.CompareDescending));

        EOFetchSpecification fetchSpec = new EOFetchSpecification("WebsiteChangeLog",
                                                                  qualifer,
                                                                  ordering);

        return editingContext().objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;           **/
    }
    
    
    
    /**
     * Creates and returns a WebsiteChangeLog for the creation of this site.  It would be better if 
     * this could be directly done in the creation methods.
     *  
     * @param creator the User who performed the creation
     * @param adminNotes optional notes entered by creator as admin notes
     * @param websiteRequest optional WebsiteRequest that prompted creation of this Website
     * @param siteCreatedFrom option siteID of the Website this one was cloned from
     * 
     * @return the newly created (inserted but not saved) WebsiteChangeLog
     */
    public WebsiteChangeLog logSiteCreation(User creator, 
                                            String adminNotes, 
                                            WebsiteRequest websiteRequest,
                                            String siteCreatedFrom)
    {
        /** require [valid_creator] creator != null;  **/
        StringBuffer notes = new StringBuffer();
        
        notes.append("<b>Site created</b>\nSiteID = ");
        notes.append(siteID());
        notes.append("\nOwnerID = ");
        notes.append(owner().userID());
        notes.append("\nUnit = ");
        notes.append(parentOrgUnit().unitName());
        notes.append("\nFilespace Quota = ");
        notes.append(megQuota());
        notes.append(" Mb");
        if (siteCreatedFrom != null)
        {
            notes.append("\nCloned from ");
            notes.append(siteCreatedFrom);
        }
        notes.append("\nRedirect to URL = ");
        notes.append(redirectURL());
        
        if ( ! StringAdditions.isEmpty(adminNotes))
        {
            notes.append("\n\n<b>Administrative note</b>: \n");
            notes.append(adminNotes);
        }
                
        if (websiteRequest != null)
        {
            notes.append("\n\n<b>Site request note</b>: \n");
            if (websiteRequest.administrativeNotes() != null)
            {
                notes.append(websiteRequest.administrativeNotes());
                notes.append("\n");
            }
            if (websiteRequest.requesterNote() != null)
            {
                notes.append(websiteRequest.requesterNote());
            }
        }
                
        return createChangeLog(creator, notes.toString(), null);
        /** ensure [valid_result] Result != null;
                   [linked_to_website] Result.website().equals(this);
                   [linked_to_creator] Result.user().equals(creator);
                   [has_notes] Result.notes() != null;                  **/
    }
    
    
    
    /**
     * Creates and returns a WebsiteChangeLog for notes from the admin and / or changes from the
     * admin.
     * 
     * @param admin User recording the note and/or making the changes
     * @param adminsNotes notes entered by the admin
     * @param previousSnapshot snapshot of this Website before any changes made by the admin
     * 
     * @return the newly created (inserted but not saved) WebsiteChangeLog
     */
    public WebsiteChangeLog logAdminChanges(User admin,
                                            String adminsNotes,
                                            NSDictionary previousSnapshot)
    { 
        /** require [valid_admin] admin != null;
                    [same_ec] editingContext().equals(admin.editingContext());
                    [has_notes_or_changes] ( ! StringAdditions.isEmpty(adminsNotes)) || 
                                           (changesFromSnapshot(previousSnapshot).count() > 0); **/

        NSDictionary changedValues = changesFromSnapshot(previousSnapshot);
        StringBuffer changes = new StringBuffer();
        if (changedValues.count() > 0)
        {
            if (changedValues.objectForKey(SITEID) != null)
            {
                changes.append("\nSiteID = ");
                changes.append(changedValues.objectForKey(SITEID));
            }
             
            if (changedValues.objectForKey(OWNER) != null)
            {
                changes.append("\nOwnerID = ");
                changes.append(((User)changedValues.objectForKey(OWNER)).userID());
            }

            if (changedValues.objectForKey(PARENTORGUNIT) != null)
            {
                changes.append("\nUnit = ");
                changes.append(((OrgUnit)changedValues.objectForKey(PARENTORGUNIT)).unitName());
            }
            
            if (changedValues.objectForKey(MEGQUOTA) != null)
            {
                changes.append("\nFilespace Quota = ");
                changes.append(changedValues.objectForKey(MEGQUOTA));
                changes.append(" Mb");
            }

            if (changedValues.objectForKey(REDIRECTURL) != null)
            {
                changes.append("\nRedirect to URL = ");
                changes.append(changedValues.objectForKey(REDIRECTURL));
            }
            
            if (changedValues.objectForKey(PUBLICATIONPERMITTED) != null)
            {
                changes.append("\nPublication is permitted = ");
                changes.append(changedValues.objectForKey(PUBLICATIONPERMITTED));
            }
            
            if (changedValues.objectForKey(CANREPLICATE) != null)
            {
                changes.append("\nReplicate outside unit = ");
                changes.append(changedValues.objectForKey(CANREPLICATE));
            }
            
            if (changes.length() > 0)
            {
                changes.insert(0, "Updated Properties:");
            }
        }

        return createChangeLog(admin, adminsNotes, changes.toString());
        /** ensure [valid_result] Result != null;
                   [linked_to_website] Result.website().equals(this);
                   [linked_to_admin] Result.user().equals(admin);
                   [has_notes_or_changes] (Result.changes() != null) || (Result.notes() != null);  **/
    }



    /**
     * Method to create a generic change log.
     *  
     * @param initiator User to caused this change log to be recorded
     * @param notes notes for change log, if any
     * @param changes record of changes made, if any
     *
     * @return the newly created (inserted but not saved) WebsiteChangeLog
     */
    public WebsiteChangeLog createChangeLog(User initiator,
                                            String notes,
                                            String changes)
    { 
        /** require [valid_initiator] initiator != null;
                    [same_ec] editingContext().equals(initiator.editingContext());
                    [has_notes_or_changes] ( ! StringAdditions.isEmpty(notes)) || 
                                           ( ! StringAdditions.isEmpty(changes)); **/

        WebsiteChangeLog log = WebsiteChangeLog.newWebsiteChangeLog();
        editingContext().insertObject(log);

        log.setWebsite(this);
        log.setUser(initiator);
        
        // Avoid saving empty strings
        if (StringAdditions.isEmpty(notes))
        {
            notes = null;
        }
        if (StringAdditions.isEmpty(changes))
        {
            changes = null;
        }

        
        log.setNotes(notes); 
        log.setChanges(changes);

        return log;
        /** ensure [valid_result] Result != null;
                   [linked_to_website] Result.website().equals(this);
                   [linked_to_admin] Result.user().equals(initiator);
                   [notes_set] ((notes == null) && (Result.notes() == null)) || (Result.notes().equals(notes));  
                   [notes_set] ((changes == null) && (Result.changes() == null)) || (Result.changes().equals(changes));  **/
    }    

    
    
    /* Implmentation of EditingContextNotification */

    /**
     * Called after insertObject is invoked on super (EOEditingContext).
     */
    public void didInsert() {}
    
    
    /**
      * Called after a new Website is created to send an e-mail to the owner.  
      */
    public void hasInserted() 
    {
        if (messageSubject != null)
        {
            SendEmail.sendIndividualMessages(messageFrom, 
                                             new NSArray(owner().emailAddress()), 
                                             messageSubject, 
                                             WebsiteCreationMessage.messageFor(this, messageTemplate));
        }
        
        messageTemplate = null;
        messageSubject = null;
        messageFrom = null; 
        clonedSiteID = null; 
    }
    
    
    /**
     * Called before deleteObject is invoked on super (EOEditingContext).  Deletes all the 
     * WebsiteChangeLogs and WebsiteRequests as there is no relationship to delete them automatically.
     * Zeros quota usage and updates OrgUnit as the OrgUnit is unreachable once the delete rules have
     * been propogated.
     */
    public void willDelete() 
    {
        EOEditingContext ec = editingContext();

        Enumeration changeLogEnum = orderedChangeLogs().objectEnumerator();
        while (changeLogEnum.hasMoreElements())
        {
            WebsiteChangeLog changeLog = (WebsiteChangeLog) changeLogEnum.nextElement();
            ec.deleteObject(changeLog);
        }
        
        NSArray requests = EOUtilities.objectsMatchingKeyAndValue(ec, 
                                                                  "WebsiteRequest", 
                                                                  "website", 
                                                                  this);
        
        Enumeration requestEnum = requests.objectEnumerator();
        while (requestEnum.hasMoreElements())
        {
            WebsiteRequest request = (WebsiteRequest) requestEnum.nextElement();
            ec.deleteObject(request);
        }
        
        setFileSizeUsage(new Long(0));
        parentOrgUnit().updateFileSizeUsage();
    }

    
    
    /**
     * Called after the deletion is processed by the EOEditingContext.  At this point the delete rules have been applied and changes propogated to related objects.
     */
    public void didDelete() {}
    
    
    /**
     * Called after a deleted object has been removed from the persistent object store.  This would handle the need to clean up related, non-EO, resources when the EO was deleted.
     */
    public void hasDeleted() {}
    
    
    /**
     * Recalculates file quota usage if any files have been removed.
     */
    public void willUpdate() 
    {
        if (didRemoveFiles)
        {
            updateFileSizeUsage();
            didRemoveFiles = false;
        }
    }
    
    
    
    /**
      * Called after a new Website is created to send an e-mail to the owner.  
      */
    public void hasUpdated() 
    {
        // REFACTOR Change the Website creation process to NOT save changes in the middle!
        /* The code to send the website created message should, properly, NOT be here.  However, the
           creation process when not copying a site saves mid-way through and the the website is not 
           ready to send the message as that point.
        */
        hasInserted();
    }
        
       
       
    /**
     * Compares Websites by the owner's User ID.
     */ 
     static class OwnerComparator extends NSComparator
     {

        /**
         * Compares Websites by the owner's User ID.
         * 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            if ((firstObject == null) || (secondObject == null))
            {
                throw new NSComparator.ComparisonException("Can't compare null objects");
            }
            
            Website firstSite = (Website)firstObject;
            Website secondSite = (Website)secondObject;
            
            return NSComparator.AscendingStringComparator.compare(firstSite.owner().userID(), 
                                                                  secondSite.owner().userID());
        }
     }



    /**
     * Compares Websites by the site ID.
     */ 
     static class SiteIDComparator extends NSComparator
     {

        /**
         * Compares Websites by the site ID.
         * 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            if ((firstObject == null) || (secondObject == null))
            {
                throw new NSComparator.ComparisonException("Can't compare null objects");
            }

            Website firstSite = (Website)firstObject;
            Website secondSite = (Website)secondObject;
            
            return NSComparator.AscendingStringComparator.compare(firstSite.siteID(), 
                                                                  secondSite.siteID());
        }
         
     }

}
