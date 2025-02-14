// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;


import java.util.*;

import net.global_village.virtualtables.*;
import net.global_village.woextensions.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/** 
 * The only reason for the existence of this class is for the purpose of initializing the database.
 * We need to do the following:
 *
 * - Create an admin website
 * - Create the Internal Users group
 * - Create the Public group
 * - Create the admin user
 * - Create the public user
 * - Create relationships between them all 
 * - Load style templates
 * - Load section types
 * - Load database column types
 * - Load LDAP server / group information
 * - Load queued task statuses
 **/ 
public class InitDatabase extends Object
{

    
    public static void initDatabase(EOEditingContext ec)
    {
        // Create Root OrgUnit
        DebugOut.println(1,"Creating Root Org Unit.");
        OrgUnit rootOrgUnit = OrgUnit.newOrgUnit();
        ec.insertObject(rootOrgUnit);

        rootOrgUnit.setUnitName(SMApplication.appProperties().stringPropertyForKey("RootUnitName"));
        rootOrgUnit.setDepth(new Integer(OrgUnit.ROOT_DEPTH));
        rootOrgUnit.setFileSizeQuota(new Integer(50));
        rootOrgUnit.setDisplayOrgUnitInPublicListOfUnits(true);  // Do this or nothing shows up!
        String defaultMessage = ResourceManagerAdditions.stringFromResource(
            "WebsiteCreationMessageDefaultTemplate.html", "GVCSMCore");
        if (defaultMessage == null)
        {
            throw new RuntimeException("Failed to load WebsiteCreationMessageDefaultTemplate.html");
        }
        rootOrgUnit.setWebsiteCreationMessage(defaultMessage);
        ec.saveChanges();
        DebugOut.println(1,"Created Root Org Unit " + rootOrgUnit.unitName());

        // Create Admin User
        DebugOut.println(1,"Creating Admin User.");
        User adminUser = User.newUser(SMApplication.appProperties().stringPropertyForKey("AdminID"), ec);
        adminUser.setPassword(SMApplication.appProperties().stringPropertyForKey("AdminPassword"));
        adminUser.setIsSystemAdmin(true);
        ec.saveChanges();
        DebugOut.println(1,"Created admin user  " + adminUser.userID());


        // Create a user for the developer for convenience sake, but not if they are the administrator!
        User develUser = null;
        if ( (SMApplication.appProperties().hasPropertyForKey("DeveloperID") ) &&
             (User.userForUserID(SMApplication.appProperties().stringPropertyForKey("DeveloperID"), ec) == null) )
        {
            DebugOut.println(1,"Creating development user...");
            develUser = User.newUser(SMApplication.appProperties().stringPropertyForKey("DeveloperID"), ec);
            develUser.setPassword(SMApplication.appProperties().stringPropertyForKey("DeveloperPassword"));
            DebugOut.println(1,"Created development user " + develUser.userID());
            ec.saveChanges();
        }

        // Create user to represent users who have not authenticated.
        DebugOut.println(1,"Creating public user...");

        // Hack to allow public user to be created when Internal Users are not allowed (public user ID is not e-mail address)
        boolean hasInternalUsers = SMApplication.appProperties().booleanPropertyForKey("HasInternalUsers");
        if ( ! hasInternalUsers)
        {
            SMApplication.appProperties().addPropertyForKey("true", "HasInternalUsers");
            SMApplication.appProperties().addPropertyForKey("@dummy.com", "InternalUserSuffix");
        }

        User publicUser = PublicUser.newPublicUser(ec);
        ec.saveChanges();

        // Hack to allow public user to be created when Internal Users are not allowed (public user ID is not e-mail address)
        if ( ! hasInternalUsers)
        {
            SMApplication.appProperties().addPropertyForKey("false", "HasInternalUsers");
        }

        DebugOut.println(1,"Created public user " + publicUser.userID());

        // Greate Groups
        DebugOut.println(1, "Creating Internal Users group...");
        Group internalUsersGroup = InternalUsersGroup.newGroup();
        ec.insertObject(internalUsersGroup);
        internalUsersGroup.setName(SMApplication.appProperties().stringPropertyForKey("InternalUsersGroupName"));
        DebugOut.println(1, "Created Internal Users group with name " + internalUsersGroup.name());

        DebugOut.println(1, "Creating Public group");
        Group publicGroup = PublicGroup.newGroup();
        ec.insertObject(publicGroup);
        publicGroup.setName(SMApplication.appProperties().stringPropertyForKey("PublicGroupName"));
        ec.saveChanges();
        DebugOut.println(1, "Created Public group with name " + publicGroup.name());

        // Load the style templates for Websites and sections.
        loadStyles(rootOrgUnit);

        // Load the Section Types
        loadSectionTypes(ec);
        
        // Create admin website
        DebugOut.println(1, "Creating Admin website...");
        Website site = Website.newWebsite(rootOrgUnit, "admin",
                                          SMApplication.appProperties().stringPropertyForKey("AdminSiteName"),
                                          adminUser, ec);

        DebugOut.println(1, "Created Admin website with siteID of '" + site.siteID() + "' and banner of " + site.banner().bannerText());

        DebugOut.println(1,"Saving Changes...");
        ec.saveChanges();

        // Load the Database Table Column Types
        loadColumnTypes(ec);

        // Load the LDAP server / LDAPBranch information
        loadLDAPServers(ec);
        
        // Load Task Statuses to be used by Queued Tasks
        loadTaskStatuses(ec);        
        
        // That *should* be all.
    }



    /**
    * Create a SectionType EO for each entry in the configuration item SectionTypes and populates it with the valeus from SectionTypes.
     *
     * @param ec - the EOEditingContext to create the SectionType EOs in.
     */
    public static void loadSectionTypes(EOEditingContext ec)
    {
        /** require [has_c] ec != null;  **/	

        Enumeration sectionTypeEnumerator = SMApplication.appProperties().arrayPropertyForKey("SectionTypes").objectEnumerator();
        while (sectionTypeEnumerator.hasMoreElements())
        {
            NSMutableDictionary sectionTypeValues = new NSMutableDictionary((NSDictionary)sectionTypeEnumerator.nextElement());

            // Translate String to Integer.  
            sectionTypeValues.setObjectForKey(new Integer((String) sectionTypeValues.objectForKey("order")),
                                              "order");

            SectionType newSectionType = SectionType.newSectionType();
            ec.insertObject(newSectionType);
            newSectionType.takeValuesFromDictionary(sectionTypeValues);
            ec.saveChanges();
            DebugOut.println(1, "Loaded Section Type: " + newSectionType.name());

            // We have created a sub-class of SectionType as an instance of the super-class.  We must get EOF to forget this object so that it will be refeteched as the correct type.  Invalidating and refault do not accomplish that, the EOEntity associated with the PK is stilll cached in the object store.
            ec.forgetObject(newSectionType);
        }

    }




    /**
     * Loads all the styles in the property SiteStyles and creates them under unitCreatedFor.  This is intended only for use at bootstrap time.
     *
     * @param unitCreatedFor - the OrgUnit to associate the created styles with.
     */
    public static void loadStyles(OrgUnit unitCreatedFor)
    {
        /** require [unitCreatedFor_not_null] unitCreatedFor != null; **/
        DebugOut.println(1,"Loading styles...");
        NSDictionary styleDictionary = ((NSDictionary)SMApplication.appProperties().propertyForKey("SiteStyles"));
        Enumeration styleEnumeration = styleDictionary.keyEnumerator();
        while (styleEnumeration.hasMoreElements())
        {
            String styleName = (String)styleEnumeration.nextElement();
            String styleDescription = (String)styleDictionary.objectForKey(styleName);
            loadStyle(unitCreatedFor, styleName, styleDescription);
        }

        DebugOut.println(1,"Saving styles...");
        unitCreatedFor.editingContext().saveChanges();
        DebugOut.println(1,"Styles saved...");

        DebugOut.println(1,"Marking default style...");
        SectionStyle defaultStyle = SectionStyle.sectionStyleWithStyleID(unitCreatedFor.editingContext(),
                                                                                       SMApplication.appProperties().stringPropertyForKey("DefaultStyle"));
        defaultStyle.setIsDefaultStyle(true);
        unitCreatedFor.editingContext().saveChanges();
        DebugOut.println(1,"Default Style marked...");
    }



    /**
     * Loads all the inidcated style, assigned it a name, and associates it with unitCreatedFor.
     *
     * @param unitCreatedFor - the OrgUnit to associate the created styles with.
     * @param anID - the unique ID for this style, must match the name of a .html resource in GVCSMCustom.
     * @param aName - the UI presentable name for this style.
     */
    public static void loadStyle(OrgUnit unitCreatedFor, String anID, String aName)
    {
        /** require
        [has_unitCreatedFor] unitCreatedFor != null;
        [has_anID] anID != null;
        [has_aName] aName != null;
         **/
        
        DebugOut.println(1, "Creating SectionStyle for " + anID + " as " + aName + " from " + "StyleTemplates/" + anID + ".html");

        EOEditingContext ec = unitCreatedFor.editingContext();
        SectionStyle sectionStyle = SectionStyle.newSectionStyle();
        ec.insertObject(sectionStyle);

        sectionStyle.setStyleID(anID);
        sectionStyle.setName(aName);
        sectionStyle.setResourceDirectory(SMApplication.appProperties().stringPropertyForKey("SectionStyleResourcePath") + anID);
        sectionStyle.setTemplate(ResourceManagerAdditions.stringFromResource("StyleTemplates/" + anID + ".html", "GVCSMCustom"));
        sectionStyle.setNotes("Created during system bootstrap");
        sectionStyle.setIsPublished(true);
        sectionStyle.setCreatedBy((User)unitCreatedFor.admins().objectAtIndex(0));
        sectionStyle.setOwningOrgUnit(unitCreatedFor);
    }



    /**
     * Create a VirtualTables ColumnType EO for each entry in the configuration item ColumnTypes.
     *
     * @param ec - the EOEditingContext to create the ColumnType EOs in.
     */
    public static void loadColumnTypes(EOEditingContext ec)
    {
        /** require [has_ec] ec != null;  **/

        Enumeration columnTypeEnumerator = SMApplication.appProperties().arrayPropertyForKey("ColumnTypes").objectEnumerator();
        while (columnTypeEnumerator.hasMoreElements())
        {
            NSMutableDictionary columnTypeValues = new NSMutableDictionary((NSDictionary)columnTypeEnumerator.nextElement());

            /*
             * For some reason doing this:
             * 
             * ColumnType newColumnType = new ColumnType();
             * ec.insertObject(newColumnType);
             * 
             * resulted in newColumnType being assigned the EOEntityClassDescription from GVCLookup
             * instead of its own.  This resulted in this exception:
             * 
             * Application constructor threw <net.global_village.virtualtables.ColumnType 0x1ecfe07> takeValueForKey(): attempt to assign value to unknown key: 'entityNameRestrictor'.
             * This class does not have an instance variable of the name entityNameRestrictor or _entityNameRestrictor, nor a method of the name setEntityNameRestrictor or _setEntityNameRestrictor
             * com.webobjects.foundation.NSKeyValueCoding$UnknownKeyException: <net.global_village.virtualtables.ColumnType 0x1ecfe07> takeValueForKey(): attempt to assign value to unknown key: 'entityNameRestrictor'.
             * This class does not have an instance variable of the name entityNameRestrictor or _entityNameRestrictor, nor a method of the name setEntityNameRestrictor or _setEntityNameRestrictor
             * at com.webobjects.foundation.NSKeyValueCoding$DefaultImplementation.handleTakeValueForUnboundKey(NSKeyValueCoding.java:1337)
             * at com.webobjects.eocontrol.EOCustomObject.handleTakeValueForUnboundKey(EOCustomObject.java:1641)
             * at com.webobjects.foundation.NSKeyValueCoding$Utility.handleTakeValueForUnboundKey(NSKeyValueCodin  g.java:568)
             * at com.webobjects.foundation.NSKeyValueCoding$_KeyBinding.setValueInObject(NSKeyValueCoding.java:925)
             * at com.webobjects.eocontrol.EOCustomObject.takeStoredValueForKey(EOCustomObject.java:1778)
             * at net.global_village.virtualtables._ColumnType.setEntityNameRestrictor(_ColumnType.java: 61)
             * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
             * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
             * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
             * at java.lang.reflect.Method.invoke(Method.java:324)
             * at com.webobjects.foundation.NSKeyValueCoding$1.setMethodValue(NSKeyValueCoding.java:688)
             * at com.webobjects.foundation.NSKeyValueCoding$_MethodBinding.setValueInObject(NSKeyValueCoding.java:1175)
             * at com.webobjects.eocontrol.EOCustomObject.takeValueForKey(EOCustomObject.java:1601)
             * at com.webobjects.eocontrol.EOKeyValueCodingAdditions$DefaultImplementation.takeValuesFromDictionary(EOKeyValueCodingAdditions.java:385)
             * at com.webobjects.eocontrol.EOCustomObject.takeValuesFromDictionary(EOCustomObject.java:1822)
             * at com.gvcsitemaker.core.support.InitDatabase.loadColumnTypes(InitDatabase.java:277)
             * 
             * Hence the use of createAndInsertInstance.  This problme was sporadic and often affected
             * only one of GVC.SiteMaker or GVC.SMAdmin.  It is probably related to model loading /
             * initialization order WRT entities referenced.
             */

            ColumnType newColumnType = (ColumnType)EOUtilities.createAndInsertInstance(ec, "ColumnType");
            newColumnType.takeValuesFromDictionary(columnTypeValues);
            ec.saveChanges();
            DebugOut.println(1, "Loaded Column Type: " + newColumnType.name());
        }
    }



    /**
     * Create an LDAPBranch for each entry in the configuration item LDAPServers.
     *
     * @param ec - the EOEditingContext to create the LDAPBranch EOs in.
     */
    public static void loadLDAPServers(EOEditingContext ec)
    {
        /** require [has_ec] ec != null;  **/

        NSArray ldapServers = SMApplication.appProperties().arrayPropertyForKey("LDAPServers");
        if (ldapServers != null)
        {
            // Mapping from names used in bootstrap configuration and the attributes of LDAPBranch
            NSMutableDictionary configToLDAPBranchMapping = new NSMutableDictionary();
            configToLDAPBranchMapping.setObjectForKey("displayName", "DisplayName");
            configToLDAPBranchMapping.setObjectForKey("groupSubTree", "GroupSubTree");
            configToLDAPBranchMapping.setObjectForKey("userSubTree", "UserSubTree");
            configToLDAPBranchMapping.setObjectForKey("userIDAttribute", "UserIDAttribute");
            configToLDAPBranchMapping.setObjectForKey("instructions", "Instructions");
            configToLDAPBranchMapping.setObjectForKey("ldapEntityName", "LDAPEntityName");
            
            for (int i = 0;  i < ldapServers.count(); i++)
            {
                NSDictionary ldapServerValues = (NSDictionary)ldapServers.objectAtIndex(i);

                LDAPBranch newLDAPBranch = new LDAPBranch();
                ec.insertObject(newLDAPBranch);
                newLDAPBranch.takeValuesFromDictionaryWithMapping(ldapServerValues,
                                                                  configToLDAPBranchMapping);
                newLDAPBranch.setDisplayOrder(new Integer(i));                                          
                ec.saveChanges();
                DebugOut.println(1, "Loaded LDAP Banch: " + newLDAPBranch.displayName() + "(" +
                 newLDAPBranch.groupSubTree() + ")");
            }
        }
        else
        {
            DebugOut.println(1, "No LDAPServers bootstrap information found, skipping...");
        }
        
    }

    
    
    /**
     * Create a TaskStatus EO for each entry in the configuration item TaskStatuses.
     *
     * @param ec - the EOEditingContext to create the TaskStatus EOs in.
     */
    public static void loadTaskStatuses(EOEditingContext ec)
    {
        /** require [has_ec] ec != null;  **/

        Enumeration taskStatusEnumerator = SMApplication.appProperties().arrayPropertyForKey("TaskStatuses").objectEnumerator();
        while (taskStatusEnumerator.hasMoreElements())
        {
            NSMutableDictionary columnTypeValues = new NSMutableDictionary((NSDictionary)taskStatusEnumerator.nextElement());

            TaskStatus newTaskStatus = (TaskStatus)EOUtilities.createAndInsertInstance(ec, "TaskStatus");
            newTaskStatus.takeValuesFromDictionary(columnTypeValues);
            ec.saveChanges();
            DebugOut.println(1, "Loaded Task Status: " + newTaskStatus.name());
        }
    }    
}
