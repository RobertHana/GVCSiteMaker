// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class OrgUnit extends _OrgUnit implements Deleteable, net.global_village.eofvalidation.EditingContextNotification
{
    public static final int ROOT_DEPTH = 0;
    public static final boolean EXCLUDE_NON_SYSTEM_WIDE_UNITS = true;
    public static final boolean INCLUDE_NON_SYSTEM_WIDE_UNITS = false;
    
    public static final String OrgUnitPathDelimiter = "::";

    public static final String ERROR_MESSAGE_KEY = "errorMessage";
    public static final String NULL_UNIT_NAME_ERROR_KEY = "nullUnitNameErrorKey";
    public static final String UNIT_NAME_EXISTS_ERROR_KEY = "unitNameExistsErrorKey";
    public static final String NULL_ENCOMPASSING_UNIT_NAME_ERROR_KEY = "nullEncompassingUnitNameErrorKey";
    public static final String NULL_ENCOMPASSING_UNIT_ERROR_KEY = "nullEncompassingUnitErrorKey";
    public static final String NULL_FILESPACE_QUOTA_ERROR_KEY = "nullFilespaceQuotaErrorKey";
    public static final String INVALID_FILESPACE_QUOTA_ERROR_KEY = "invalidFilespaceQuotaErrorKey";
    public static final String NEW_PARENT_IS_CHILD_ERROR_KEY = "newParentIsChildErrorKey";

    public static final NSArray BATCH_FILE_KEY_ARRAY = new NSArray( new String[] { UNITNAME,
                                                                                   PARENTORGUNIT,
                                                                                   FILESIZEQUOTA,
                                                                                   INPUBLICLIST} );

    public static final NSComparator HierarchicalComparator = new HierarchicalComparator();
    


    /**
     * Returns the list of all system wide Org Units in the hierarchy.
     *  
     * @return the list of all system wide Org Units in the hierarchy
     */
    public static NSArray allSystemWideUnits(EOEditingContext ec)
    {
        /** require [valid_ec] ec != null;  **/
        return allUnitsAtOrBelow(new NSArray(rootUnit(ec)), EXCLUDE_NON_SYSTEM_WIDE_UNITS);
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Returns the list of all Org Units at or below <code>rootUnits</code>.  This can be several 
     * disconnected branches of the OrgUnit hierarchy.
     *  
     * @return the list of all Org Units at or below <code>rootUnits</code>
     */
    public static NSArray allUnitsAtOrBelow(NSArray rootUnits, boolean includeOnlySystemWideUnits)
    {
        /** require [valid_param] rootUnits != null;  **/
        NSMutableSet adminableUnits = new NSMutableSet(rootUnits.count() * 2);
        Enumeration unitEnumeration = rootUnits.objectEnumerator();
        while (unitEnumeration.hasMoreElements())
        {
            OrgUnit orgUnit = (OrgUnit) unitEnumeration.nextElement();
            adminableUnits.addObjectsFromArray(orgUnit.orgUnitsInHierarchy(includeOnlySystemWideUnits));
            
        }
        
        return adminableUnits.allObjects();
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    public void awakeFromInsertion( EOEditingContext ec )
    {
        super.awakeFromInsertion( ec );

        if (fileSizeUsage() == null)
        {
            setFileSizeUsage( new Long(0) );
        }

        if (fileSizeQuota() == null)
        {
            setFileSizeQuota( new Integer(0) );
        }

        if (inPublicList() == null)
        {
            setInPublicList("Y");
        }
    }



    /**
     * OrgUnits are copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this OrgUnit
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }



    /* ************ STATIC METHODS ***************** */

    /**
     * Factory method to create new instances of OrgUnit.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of OrgUnit or a subclass.
     */
    public static OrgUnit newOrgUnit()
    {
        return (OrgUnit) SMEOUtils.newInstanceOf("OrgUnit");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the OrgUnit at the root of the OrgUnit hierarchy.
     *
     * @return the OrgUnit at the root of the OrgUnit hierarchy.
     */
    public static OrgUnit rootUnit(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        return (OrgUnit) EOUtilities.objectMatchingKeyAndValue(ec, "OrgUnit", "parentOrgUnit", NSKeyValueCoding.NullValue);

        /** ensure [result_not_null] Result != null; **/
    }

    

    public static NSDictionary createOrgUnit( OrgUnit newOrgUnit,
                                              OrgUnit parentOrgUnit,
                                              EOEditingContext ec ) {

        // Is this zero or one rooted? Does it matter
        newOrgUnit.setDepth( new Integer(parentOrgUnit.depth().intValue() + 1) );
        NSDictionary errors = newOrgUnit.validateOrgUnit( parentOrgUnit, ec );

        if( errors == null ) {
            ec.insertObject(newOrgUnit);

            newOrgUnit.addObjectToBothSidesOfRelationshipWithKey(parentOrgUnit, "parentOrgUnit");

            ec.saveChanges();

            return null;
        }
        else {
            return errors;
        }
    }



    public static boolean validateArrayOfOrgUnitDictionaries( NSArray dictArray,
                                                              EOEditingContext ec ) {
        boolean valid = true;
        Enumeration e = dictArray.objectEnumerator();

        while( e.hasMoreElements() ) {
            // grab each dictionary in the array
            NSMutableDictionary newOrgUnitDict = (NSMutableDictionary)e.nextElement();

            // get the parent org unit name and make it into an object
            String parentOrgUnitName = (String)newOrgUnitDict.objectForKey(PARENTORGUNIT);
            OrgUnit parentOrgUnit = OrgUnit.unitForUnitName( parentOrgUnitName, ec );

            NSDictionary errors = orgUnitFromDictionary(newOrgUnitDict).validateOrgUnit(parentOrgUnit, ec);

            // this is a bit of a hack here, but it allows us to differentiate
            // between a blank org unit name in the batch file and an org unit
            // name that could not be found in the db
            if( parentOrgUnitName == null || parentOrgUnitName.length() < 1 ) {
                NSMutableDictionary tmpErrorDict = new NSMutableDictionary(errors);
                tmpErrorDict.removeObjectForKey(NULL_ENCOMPASSING_UNIT_ERROR_KEY);
                tmpErrorDict.setObjectForKey("Encompassing unit is a required field!",
                                             NULL_ENCOMPASSING_UNIT_NAME_ERROR_KEY);
                errors = tmpErrorDict;
            }

            if( errors != null ) {
                newOrgUnitDict.setObjectForKey(errors, ERROR_MESSAGE_KEY);
                valid = false;
            }
        }

        return valid;
    }


    
    public static void createOrgUnitsFromArrayOfOrgUnitDictionaries( NSArray dictArray,
                                                                     EOEditingContext ec ) {
        Enumeration e = dictArray.objectEnumerator();

        while( e.hasMoreElements() ) {
            // grab each dictionary in the array
            NSMutableDictionary newOrgUnitDict = (NSMutableDictionary)e.nextElement();

            // get the parent org unit name and make it into an object
            String parentOrgUnitName = (String)newOrgUnitDict.objectForKey(PARENTORGUNIT);
            OrgUnit parentOrgUnit = OrgUnit.unitForUnitName( parentOrgUnitName, ec );

            OrgUnit.createOrgUnit(orgUnitFromDictionary(newOrgUnitDict),
                                  parentOrgUnit,
                                  ec);
        }
    }


    
    protected static OrgUnit orgUnitFromDictionary( NSDictionary dict ) {
        String displayOrgUnitInList = (String)dict.objectForKey(INPUBLICLIST);
        String fileSizeQuota = (String)dict.objectForKey(FILESIZEQUOTA);

        OrgUnit newOrgUnit = newOrgUnit();
        if( displayOrgUnitInList.equals("1") )
            newOrgUnit.setDisplayOrgUnitInPublicListOfUnits(true);
        else
            newOrgUnit.setDisplayOrgUnitInPublicListOfUnits(false);

        try {
            newOrgUnit.setFileSizeQuota( new Integer(fileSizeQuota) );
        }
        catch (NumberFormatException e) {
            // set it to a bogus # so it will get flagged by the validation
            newOrgUnit.setFileSizeQuota( new Integer(-1) );
        }

        newOrgUnit.setUnitName((String)dict.objectForKey(UNITNAME));

        return newOrgUnit;
    }


    
    public static OrgUnit unitForUnitName( String unitName, EOEditingContext ec )
    {
        // Shouldn't this be an error?  -ch
        if( unitName == null )
            return null;

        EOQualifier qualifier = new EOKeyValueQualifier("unitName", EOQualifier.QualifierOperatorCaseInsensitiveLike, unitName);
        EOFetchSpecification fetchSpec = new EOFetchSpecification("OrgUnit", qualifier, null);
        NSArray results = ec.objectsWithFetchSpecification(fetchSpec);

        DebugOut.println( 30, " = = = = = Fetched OrgUnit with ID: " + unitName );
        DebugOut.println( 30, results.toString() );

        // Shouldn't this be an error?  -ch
        if( results.count() > 0 )
            return (OrgUnit)results.objectAtIndex(0);
        else
            return null;
    }


    
    /* *************** DELETEABLE INTERFACE ********** */
    public boolean canBeDeleted() {
        return (errorMessages() == null);
    }

    public NSArray errorMessages() {
        NSMutableArray errorArray = new NSMutableArray();

        // Check any conditions that need to be met before an org unit is
        // deleted and add an error message to an NSArray for any that have
        // not been satisfied.

        // Do not delete this unit if it is the root unit. 
        if ( isRootUnit() )
            errorArray.addObject("Cannot delete root unit.");
        
        // Do not delete this unit if it is a parent unit. 
        if ( childOrgUnits().count() > 0 ) {    
            errorArray.addObject("Unit contains other Org Units.");
        }

        // Do not delete this unit if it contains web sites.
        if ( websites().count() > 0 ) {    
            errorArray.addObject("Unit has web sites associated with it.");
        }

        // Do not delete this unit if it owns any Section Styles.  The method stylesInOrgUnit also picks up styles from sub-units but that is OK as the presence of sub-units also prevent deletion.
        if ( SectionStyle.stylesInOrgUnit(this).count() > 0 ) {
            errorArray.addObject("Unit has Styles associated with it.");
        }

        if( errorArray.count() < 1 )
            errorArray = null;

        return errorArray;
    }


    
    /* *************** INSTANCE METHODS ************** */
    protected NSDictionary validateOrgUnit( OrgUnit parentOrgUnit,
                                          EOEditingContext ec ) {
        NSMutableDictionary errorMessages = new NSMutableDictionary();
        boolean isValid = true;

        if( unitName() == null || unitName().length() < 1 ) {
            errorMessages.setObjectForKey("Unit name is a required field!",
                                          NULL_UNIT_NAME_ERROR_KEY);
            isValid = false;
        }
        else {
            if( ec.registeredObjects().containsObject(this) ) {
                // we are validating an object already in the ec
                OrgUnit tmpOrgUnit = OrgUnit.unitForUnitName( unitName(), ec );
                if( tmpOrgUnit != null && !tmpOrgUnit.equals(this) ) {
                    errorMessages.setObjectForKey("Requested new Unit name already exists!",
                                                  UNIT_NAME_EXISTS_ERROR_KEY);
                    isValid = false;
                }
            }
            else {
                // we are validating a new object (it's not in the ec)
                if( OrgUnit.unitForUnitName( unitName(), ec ) != null ) {
                    errorMessages.setObjectForKey("Requested new Unit name already exists!",
                                                  UNIT_NAME_EXISTS_ERROR_KEY);
                    isValid = false;
                }
            }
        }

        if( parentOrgUnit == null && !this.isRootUnit() ) {
            errorMessages.setObjectForKey("Requested encompassing unit could not be found!",
                                          NULL_ENCOMPASSING_UNIT_ERROR_KEY);
            isValid = false;
        }

        if( fileSizeQuota() == null ) {
            errorMessages.setObjectForKey("Filespace quota is a required field!",
                                          NULL_FILESPACE_QUOTA_ERROR_KEY);
            isValid = false;
        }
        else if( fileSizeQuota().intValue() <= 0 ) {
            errorMessages.setObjectForKey("Requested quota is not a positive integer!",
                                          INVALID_FILESPACE_QUOTA_ERROR_KEY);
            isValid = false;
        }
        else if( (fileSizeUsage() != null) &&
                 (SMFileUtils.megaBytesToBytes(fileSizeQuota()) < fileSizeUsage().floatValue()) ) {
            errorMessages.setObjectForKey("Requested quota is less than actual current usage!",
                                          INVALID_FILESPACE_QUOTA_ERROR_KEY);
            isValid = false;
        }
        else if((parentOrgUnit != null) && (fileSizeQuota().intValue() > parentOrgUnit.fileSizeQuota().intValue()) ) {
            errorMessages.setObjectForKey("Requested quota is more than quota in parent unit.  Enter " + parentOrgUnit.fileSizeQuota() + " MB or less",
                                          INVALID_FILESPACE_QUOTA_ERROR_KEY);
            isValid = false;
        }
        
        if( isValid )
            return null;
        else
            return errorMessages;
    }


    
    public NSDictionary updateProperties( OrgUnit newParentUnit, EOEditingContext ec ) {
        NSDictionary errors = this.validateOrgUnit( newParentUnit, ec );

        if( errors == null ) {
            if( ( parentOrgUnit() != null ) && !parentOrgUnit().equals(newParentUnit)
               && (fileSizeUsage().longValue() > newParentUnit.availableSpaceInBytes())) {
                NSMutableDictionary errorDict = new NSMutableDictionary();
                errorDict.setObjectForKey("Cannot move unit, will violate quota in parent unit!",
                                          INVALID_FILESPACE_QUOTA_ERROR_KEY);
                return errorDict;
            }
            // Bug #35 -- check for root unit which has a null parent...
            if(  ( parentOrgUnit() != null ) && !parentOrgUnit().equals( newParentUnit ) ) {
                if( isParentOfOrgUnit( newParentUnit ) || this.equals(newParentUnit) ) {
                    NSMutableDictionary errorDict = new NSMutableDictionary();
                    errorDict.setObjectForKey("A unit may not be encompassed by itself or by any unit currently below it!",
                                              NEW_PARENT_IS_CHILD_ERROR_KEY);
                    return errorDict;
                }
                DebugOut.println(1, "parent org unit has changed...updating...");
                OrgUnit oldParent = parentOrgUnit();

                this.removeObjectFromBothSidesOfRelationshipWithKey(parentOrgUnit(), "parentOrgUnit");
                this.addObjectToBothSidesOfRelationshipWithKey(newParentUnit, "parentOrgUnit");
                this.setDepth( new Integer(newParentUnit.depth().intValue() + 1) );
                this.updateChildrenDepthValues();

                updateFileSizeUsage();
                oldParent.updateFileSizeUsage();
            }

            ec.saveChanges();

            return null;
        }
        else {
            ec.revert();
            return errors;
        }
    }


    
    public void updateChildrenDepthValues() {
        if( childOrgUnits() != null ) {
            Enumeration e = childOrgUnits().objectEnumerator();

            while( e.hasMoreElements() ) {
                OrgUnit child = (OrgUnit)e.nextElement();

                child.setDepth( new Integer( this.depth().intValue() + 1 ) );
                child.updateChildrenDepthValues();
            }
        }
    }


    
    public boolean isParentOfOrgUnit( OrgUnit orgUnit ) {
        if( childOrgUnits() != null ) {
            Enumeration e = childOrgUnits().objectEnumerator();

            while( e.hasMoreElements() ) {
                OrgUnit child = (OrgUnit)e.nextElement();
                if( child.equals(orgUnit) || child.isParentOfOrgUnit(orgUnit) )
                    return true;
            }
        }
        return false;
    }


    
    public boolean isRootUnit()
    {
        return parentOrgUnit() == null;
    }



    /**
     * Returns this OrgUnit and all units below it in the hierarchy.
     *
     * @return this OrgUnit and all units below it in the hierarchy.
     */
    public NSArray orderedOrgUnitsInHierarchy()
    {
        return orderedOrgUnitsInHierarchy(INCLUDE_NON_SYSTEM_WIDE_UNITS);
    }



    /**
     * Returns this OrgUnit and all units below it in the hierarchy which are included in the System Wide List of OrgUnits.
     *
     * @return this OrgUnit and all units below it in the hierarchy which are included in the System Wide List of OrgUnits.
     */
    public NSArray orderedSystemWideOrgUnitsInHierarchy()
    {
        return orderedOrgUnitsInHierarchy(EXCLUDE_NON_SYSTEM_WIDE_UNITS);
    }



    /**
     * Returns a hierarchial arrangement of this OrgUnit and all units below it in the hierarchy, 
     * optionally excluding OrgUnits which are not included in the System Wide List of OrgUnits.
     *
     * @param includeOnlySystemWideUnits <code>true</code> if only this unit and units below it in 
     * the System Wide List of OrgUnits should be returned.
     *
     * @return a hierarchial arrangement of this OrgUnit and all units below it in the hierarchy
     */
    public NSArray orderedOrgUnitsInHierarchy(boolean includeOnlySystemWideUnits)
    {
        return NSArrayAdditions.sortedArrayUsingComparator(orgUnitsInHierarchy(includeOnlySystemWideUnits),
                                                           HierarchicalComparator);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns this OrgUnit and all units below it in the hierarchy, optionally excluding OrgUnits 
     * which are not included in the System Wide List of OrgUnits.
     *
     * @param includeOnlySystemWideUnits <code>true</code> if only this unit and units below it in 
     * the System Wide List of OrgUnits should be returned.
     *
     * @return this OrgUnit and all units below it in the hierarchy, optionally excluding OrgUnits 
     * which are not included in the System Wide List of OrgUnits.
     */
    public NSArray orgUnitsInHierarchy(boolean includeOnlySystemWideUnits)
    {
        NSMutableArray results = new NSMutableArray();

        // Do not include this unit if ! this.displayOrgUnitInPublicListOfUnits()
        if (( ! includeOnlySystemWideUnits) || displayOrgUnitInPublicListOfUnits())
        {
            results.addObject(this);
        }
         
        Enumeration e = childOrgUnits().objectEnumerator();
        while( e.hasMoreElements() )
        {
            OrgUnit child = (OrgUnit)e.nextElement();
            results.addObjectsFromArray(child.orderedOrgUnitsInHierarchy(includeOnlySystemWideUnits));
        }
        
        return results;
        /** ensure [valid_result] Result != null;    **/
    }


    
    /**
     * Returns the path (branch) of OrgUnits from the Root Unit down to this unit, excluding any OrgUnit not in the System Wide List of OrgUnits.
     *
     * @return the branch of OrgUnits from the Root Unit down to this unit, excluding any OrgUnit not in the System Wide List of OrgUnits.
     */
    public NSArray orgUnitPath()
    {
        NSMutableArray orgUnitPath = new NSMutableArray();

        OrgUnit currentUnit = this;
        while (currentUnit != null)
        {
            if (currentUnit.displayOrgUnitInPublicListOfUnits())
            {
                orgUnitPath.insertObjectAtIndex(currentUnit, 0);
            }

            currentUnit = currentUnit.parentOrgUnit();
        }

        return orgUnitPath;
    }



    /**
     * Returns the elements of orgUnitPath surrounded by OrgUnitPathDelimiter, e.g. ::Root Unit::Child Unit::Grandchild Unit::
     *
     * @return the elements of orgUnitPath surrounded by OrgUnitPathDelimiter, e.g. ::Root Unit::Child Unit::Grandchild Unit::
     */
    public String delimitedOrgUnitPath()
    {
        return ( OrgUnitPathDelimiter +
                 ((NSArray)orgUnitPath().valueForKey("unitName")).componentsJoinedByString(OrgUnitPathDelimiter) +
                 OrgUnitPathDelimiter);
    }



    /**
     * Adds the user with a userID of adminID as a Unit Administrator of this unit.  If no user 
     * exists with a userID of adminID, one is created.
     *
     * @param adminID the userID of the user to add as a Unit Administrator of this unit
     */
    public NSDictionary addAdminForAdminID(String adminID)
    {
        /** require [in_ec] editingContext() != null;    **/
        
        NSMutableDictionary errorDict = new NSMutableDictionary();
        User adminUser = User.userForUserID( adminID, editingContext() );

        // Create User on demand
        if ( adminUser == null )
        {
            NSArray userIDErrors = User.validateUserID(adminID);
            if (userIDErrors.count() == 0)
            {
                adminUser = User.createUser(editingContext(), adminID);
            }
            else
            {
                // Hack until validation messages here are refactored as in User
                errorDict.setObjectForKey(userIDErrors.objectAtIndex(0), User.ADMIN_ID_NOT_FOUND_ERROR_KEY);
            }
        }

        if (errorDict.count() == 0)
        {
            addObjectToBothSidesOfRelationshipWithKey( adminUser, "admins");
        }

        return errorDict;

        /** ensure [valid_result] Result != null;  **/
    }



    public void removeAdmin( User adminUser,
                             EOEditingContext ec ) {

        if( adminUser != null ) {
            removeObjectFromBothSidesOfRelationshipWithKey(adminUser, "admins");
            ec.saveChanges();
        }
    }


    /**
     * Returns  the list of administrators from the closest unit that has designated administrators.
     * If this unit has designated administrators then only they are returned.  If this unit does not
     * have designated administrators then a search is made moving up in the Org Unit hierarchy for
     * the first parent unit with designated administrators.  As the root unit is required to have 
     * at least one administrator, this method will return the system administrators if no unit 
     * admins are located before the root unit is reached.
     * 
     * @return the list of administrators from the closest unit that has designated administrators
     */
    public NSArray nearestAdmins()
    {
        return (admins().count() != 0) ? admins() : parentOrgUnit().nearestAdmins();
        /** ensure [valid_result] Result != null;  [has_admins] Result.count() > 0;  **/
    }



    /**
     * Returns the explicitly declared admins in this unit and all units below it.  If an admin is
     * declared in more than one unit, they are returned only once (each returned admin is unique).
     * An empty array is returned if there are no admins explicitly declared admins in this unit or
     * any units below it.
     * 
     * @return the explicitly declared admins in this unit and all units below it
     */
    public NSArray adminsInAndBelow()
    {
        NSArray thisUnitAndBelow = allUnitsAtOrBelow(new NSArray(this), INCLUDE_NON_SYSTEM_WIDE_UNITS);
        Enumeration unitEnumerator = thisUnitAndBelow.objectEnumerator();
        NSMutableSet allAdmins = new NSMutableSet();
        while (unitEnumerator.hasMoreElements())
        {
            OrgUnit aUnit = (OrgUnit) unitEnumerator.nextElement();
            allAdmins.addObjectsFromArray(aUnit.admins());
        }        
        
        return allAdmins.allObjects();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the template to use for the message to announce a new website created in this unit.
     * If this Org Unit has no message of its own, it uses the message from the closest parent unit
     * that has a message.  The root unit always has message template.
     * 
     * @return the template to use for the message to announce a new website created in this unit
     */
    public String websiteCreationMessageTemplate()
    {
        /** require [has_template] ( ! StringAdditions.isEmpty(websiteCreationMessage()) ) || 
                                   ( ! isRootUnit());                                **/
        
        String websiteCreationMessageTemplate = websiteCreationMessage();

        if (StringAdditions.isEmpty(websiteCreationMessageTemplate)) 
        {
            websiteCreationMessageTemplate = parentOrgUnit().websiteCreationMessageTemplate();
        }

        return websiteCreationMessageTemplate;
        /** ensure [valid_result] ! StringAdditions.isEmpty(Result);  **/
    }



    /* ************ QUOTA METHODS ***************** */

    /**
     * Returns the exact amount of available space remaining in bytes.  This can be less than the remaining, unused, quota if an Org Unit higher in the hierarchy has less reamining quota space.  Effectively this is the minimum remaninging quota of any Org Unit from this one up to the root unit.
     *
     * @return the exact amount of quota remaining in bytes.
     */
    public long availableSpaceInBytes()
    {
        long availableBytes = SMFileUtils.megaBytesToBytes(fileSizeQuota()) - fileSizeUsage().longValue();
        DebugOut.println( 30, "fileSizeQuota() = " + fileSizeQuota());
        DebugOut.println( 30, "megaBytesToBytes(fileSizeQuota()) = " + SMFileUtils.megaBytesToBytes(fileSizeQuota()));
        DebugOut.println( 30, "fileSizeUsage() = " + fileSizeUsage());

        DebugOut.println(1,"orgUnitAvailableQuotaInBytes = " + availableBytes);

        // The check and see if any OrgUnits above us in the hierarchy have less remaining space.
        if (parentOrgUnit() != null)
        {
            long parentOrgUnitAvailableQuotaInBytes = parentOrgUnit().availableSpaceInBytes();
            DebugOut.println(1,"parentOrgUnitAvailableQuotaInBytes = " + parentOrgUnitAvailableQuotaInBytes);

            if ( parentOrgUnitAvailableQuotaInBytes < availableBytes )
            {
                availableBytes = parentOrgUnitAvailableQuotaInBytes;
            }
        }

        // Handle over quota problems caused by bugs, calculation errors etc.
        // Is this needed?  -ch
        if( availableBytes < 0 )
        {
            availableBytes = 0;
        }

        return availableBytes;
    }


    
    /**
     * Updates fileSizeUsage() to reflect the files uploaded to all Websites in this OrgUnit plus all the files uploaded to all Websites in OrgUnits under this OrgUnit.  Once this is determined, the updateFileSizeUsage() is propogated to our parentOrgUnit,
     */
    public void updateFileSizeUsage()
    {
        long totalSize = 0; 

        // Calculate the quota used by Websites in this unit
        Enumeration websiteEnumerator = websites().objectEnumerator();
        while (websiteEnumerator.hasMoreElements())
        {
            Website aSite = (Website)websiteEnumerator.nextElement();
            totalSize += aSite.fileSizeUsage().longValue();
        }

        // Calculate the quota used by Websites in units under this unit
        Enumeration orgUnitEnumerator = childOrgUnits().objectEnumerator();
        while (orgUnitEnumerator.hasMoreElements())
        {
            OrgUnit aUnit = (OrgUnit)orgUnitEnumerator.nextElement();
            totalSize += aUnit.fileSizeUsage().longValue();
        }

        setFileSizeUsage(new Long(totalSize));

        // Let our parent OrgUnit know that it should update too.
        if( parentOrgUnit() != null )
        {
            parentOrgUnit().updateFileSizeUsage();
        }
    }



    /**
     * Returns the approximate amount of quota used as a percentage.  For example, a return value 15.5 indicates 15.5% of the quota has been used.
     *
     * @return the approximate amount of quota used as a percentage.
     */
    public double percentageOfQuotaUsed()
    {
        return fileSizeUsage().floatValue() / SMFileUtils.megaBytesToBytes(fileSizeQuota()) * 100.0;
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
     * Returns the approximate amount of available space for SiteFiles in megabytes.  For example, a return value 5.5 indicates 5.5 MB of file system space was available.  This will return a lower number than the unused Quota if the parent OrgUnit, or OrgUnits above it, have less available space.
     *
     * @return the approximate amount of vailable space for SiteFiles in megabytes.
     */
    public Float availableSpaceInMegaBytes()
    {
        return new Float(SMFileUtils.bytesToMegaBytes(availableSpaceInBytes()));
    }



    public boolean userIsAdmin( User theUser ) {
        // is an admin for this unit?
        //
        if( admins() != null && admins().containsObject( theUser ) ) {
            return true;
        }
        else if( parentOrgUnit() != null ) {
            //  is an admin for any parent org unit?
            //
            return parentOrgUnit().userIsAdmin( theUser ); 
        }
        else {
            return false;  // guess not.
        }
    }


    
    /**
     * Returns all websites in this OrgUnit, sorted by siteID.
     *
     * @return all websites in this OrgUnit, sorted by siteID.
     */
    public NSArray orderedWebsitesInUnit()
    {
        EOSortOrdering defaultOrdering = new EOSortOrdering("siteID", EOSortOrdering.CompareCaseInsensitiveAscending);
        
        return EOSortOrdering.sortedArrayUsingKeyOrderArray(websites(), new NSArray(defaultOrdering));

        /** ensure [result_not_null] Result != null; **/
    }


    
    public boolean displayOrgUnitInPublicListOfUnits()
    {
        return inPublicList().equals("Y");
    }


    
    public void setDisplayOrgUnitInPublicListOfUnits( boolean flag )
    {
        setInPublicList( flag ? "Y" : "N");
    }




    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (isRootUnit() && StringAdditions.isEmpty(websiteCreationMessage()) )
        {
            exceptions.addObject(new NSValidation.ValidationException(
                "The new website creation message template is required on the root unit"));
        }
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /* Implmentation of EditingContextNotification */

    /**
     * Called after insertObject is invoked on super (EOEditingContext).
     */
    public void didInsert() {}
    
    
    /**
      * Called after a new Website is created to send an e-mail to the owner.  
      */
    public void hasInserted() {}
    
    
    /**
     * Called before deleteObject is invoked on super (EOEditingContext).  Deletes all the 
     * WebsiteRequests and QueuedTasks as there is no relationship to delete them automatically.
     */
    public void willDelete() 
    {
        EOEditingContext ec = editingContext();
        DeletePrefetcher deletePrefetcher = new DeletePrefetcher();
        NSArray requests = EOUtilities.objectsMatchingKeyAndValue(ec, 
                                                                  "WebsiteRequest", 
                                                                  "orgUnit", 
                                                                  this);
        Enumeration enumerator = requests.objectEnumerator();
        while (enumerator.hasMoreElements())
        {
            EOEnterpriseObject eo = (EOEnterpriseObject) enumerator.nextElement();
            ec.deleteObject(eo);
            deletePrefetcher.prefetchDeletionPathsFrom(eo);
        }
        
        
        NSArray tasks = EOUtilities.objectsMatchingKeyAndValue(ec, 
                                                                  "CopySiteTask", 
                                                                  "relatedOrgUnit", 
                                                                  this);
        enumerator = tasks.objectEnumerator();
        while (enumerator.hasMoreElements())
        {
            EOEnterpriseObject eo = (EOEnterpriseObject) enumerator.nextElement();
            ec.deleteObject(eo);
            deletePrefetcher.prefetchDeletionPathsFrom(eo);
        }
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
    public void willUpdate() {}


    /**
      * Called after a new Website is created to send an e-mail to the owner.  
      */
    public void hasUpdated() {} 


    /**
     * Called after revert is invoked on our EOEditingContext.
     */
    public void hasReverted() {}



    /**
     * Compares OrgUnits to produce a Hierarchical sorting.  This is where all the magic of the
     * hierarchical ordering happens.  And the logic is almost convoluted enough to qualify as  magic!
     */ 
     static class HierarchicalComparator extends NSComparator
     {

        /**
         * Comparison rules:<br/>
         * <ol>
         * <li>The root unit always sorts first</li>
         * <li>Parent units sort before their direct children</li>
         * <li>Units in the same parent sort alphabetically</li>
         * <li>Units at the same level of depth sort as their parents sort</li>
         * <li>Units at different levels of depth cannot be sorted, move up the hierarchy from the
         * lower unit until a sorting can be determined</li>
         * </ol> 
         * 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            if ((firstObject == null) || (secondObject == null))
            {
                throw new NSComparator.ComparisonException("Can't compare null objects");
            }
            
            int result = NSComparator.OrderedSame;
            
            OrgUnit firstUnit = (OrgUnit)firstObject;
            OrgUnit secondUnit = (OrgUnit)secondObject;
            
            
            // Special equality case
            if (firstUnit == secondUnit)
            {
                result = NSComparator.OrderedSame;
            }
            
            // Special handling for Root Unit
            else if (firstUnit.parentOrgUnit() == null)
            {
                result = NSComparator.OrderedAscending;
            }
            else  if (secondUnit.parentOrgUnit() == null)
            {
                result = NSComparator.OrderedDescending;
            }
            
            // Special handling for parent/child units
            else if (firstUnit.parentOrgUnit().equals(secondUnit))
            {
                result = NSComparator.OrderedDescending;
            }
            else if (secondUnit.parentOrgUnit().equals(firstUnit))
            {
                result = NSComparator.OrderedAscending;
            }
            
            // Siblings: sort by name
            else if (firstUnit.parentOrgUnit().equals(secondUnit.parentOrgUnit()))
            {
                result = NSComparator.AscendingStringComparator.compare(firstUnit.unitName(), secondUnit.unitName());
            }
            
            // Same depth in hierarchy, but unrelated
            else if (firstUnit.depth().equals(secondUnit.depth()))
            {
                result = compare(firstUnit.parentOrgUnit(), secondUnit.parentOrgUnit());
            }
            
            
            else  if (firstUnit.depth().intValue() < secondUnit.depth().intValue())
            {
                result = compare(firstUnit, secondUnit.parentOrgUnit());
            }
            else  if (firstUnit.depth().intValue() > secondUnit.depth().intValue())
            {
                result = compare(firstUnit.parentOrgUnit(), secondUnit);
            }
            
            // WTF?!!?
            else
            {
                throw new NSComparator.ComparisonException("Can't compare OrgUnits " + firstUnit + " and " + secondUnit);
            }
                        
            return result;
        }
         
         
     }

}
