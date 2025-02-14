// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;
import net.global_village.jmail.*;
import net.global_village.kerberos.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


  
/**
 * TODO move code from SMSession.setCurrentAuthenticatedUser here
 */
public class User extends _User implements Deleteable, net.global_village.eofvalidation.EditingContextNotification
{

    protected static Integer maxUserIDLength = null;
    
    public static final String ERROR_MESSAGE_KEY = "errorMessage";
    public static final String USER_ID_KEY = "userID";

    public static final String NULL_USER_ID_ERROR = "User ID is a required field!";
    public static final String INVALID_CHARS_ERROR = "User ID must contain URL-friendly characters only!";
    public static final String USER_ID_EXISTS_ERROR =  "Requested UserID already exists";
    public static final String USER_ID_NOT_EMAIL = "User ID is not a valid e-mail address.";
    
    public static final String ADMIN_ID_NOT_FOUND_ERROR_KEY = "adminIDNotFoundErrorKey";
    public static final String ADMIN_ID_IN_USE_ERROR_KEY = "adminIDInUseErrorKey";

    public static final NSArray BATCH_FILE_KEY_ARRAY = new NSArray( new String[] { USER_ID_KEY } );

    

    /**
     * Factory method to create new instances of User.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of User or a subclass.
     */
    public static User newUser()
    {
        return (User) SMEOUtils.newInstanceOf("User");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of User given the userID, inserted into an editing context.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of User or a subclass.
     */
    public static User newUser( String aUserID, EOEditingContext ec )
    {
        /** require
        [a_user_id_not_null] aUserID != null;
        [ec_not_null] ec != null;
        [valid_userID] isValidUserID(aUserID);  **/
        
        User newUser = newUser();
        ec.insertObject( newUser );
        newUser.setUserID( canonicalUserID(aUserID) ); 

        JassAdditions.post("User", "newUser()", newUser.userID().equals(canonicalUserID(aUserID)));
        JassAdditions.post("User", "newUser()", ec.insertedObjects().containsObject(newUser));
        
        return newUser;

        /** ensure [result_not_null] Result != null; **/
     }



    /**
     * Returns the user ID suffix that indicates an internal user if present.  The intention is that this is the domain at the end of an e-mail address.  It should be stripped off of the userID before saving and added back before sending e-mail.
     *
     * @return the user ID suffix that indicates an internal user if present
     */
    private static String internalUserSuffix()
    {
        return SMApplication.appProperties().stringPropertyForKey("InternalUserSuffix").toLowerCase();

        /** ensure [result_not_null] Result != null; **/
    }



    
    /**
     * Returns <code>true</code> if the installation has internal users (is using the Internal Users functionality).
     *
     * @return <code>true</code> if the installation has internal users (is using the Internal Users functionality)
     */
    public static boolean applicationHasInternalUsers()
    {
        return SMApplication.appProperties().booleanPropertyForKey("HasInternalUsers");
    }



    /**
     * Returns <code>true</code> if  the passed string ends in the InternalUserSuffix (see GVCSiteMaker.plist) <b>and</b> the installation has internal users.
     *
     * @return <code>true</code> if the passed string ends in the InternalUserSuffix (see GVCSiteMaker.plist)
     */
    public static boolean hasInternalUserSuffix(String aUserID)
    {
        /** require [a_user_id_not_null] aUserID != null; **/
        
        return (applicationHasInternalUsers() &&
                aUserID.trim().toLowerCase().endsWith(internalUserSuffix()) );
     }



    /**
     * Returns <code>true</code> if  the passed string matches the requirements for an InternalUser ID (no @domain.name or ends in the InternalUserSuffix)  <b>and</b> the installation has internal users.
     *
     * @return <code>true</code> if the passed string matches the requirements for an InternalUser ID
     */
    public static boolean isInternalUserID(String aUserID)
    {
        /** require [a_user_id_not_null] aUserID != null; **/
        
        return (applicationHasInternalUsers() &&
                (canonicalUserID(aUserID).indexOf("@") == -1) &&
                ! PublicUser.isPublicUserID(aUserID) );
     }



    /**
     * Returns aUserID with InternalUserSuffix stripped off (if it is there and the application is handling Internal Users).  This is the user ID that should be used for creating or fetching users.
     *
     * @return aUserID with InternalUserSuffix stripped off
     */
    public static String canonicalUserID(String aUserID)
    {
        /** require [a_user_id_not_null] aUserID != null; **/
        
        String canonicalUserID = aUserID.toLowerCase();

        if (hasInternalUserSuffix(aUserID))
        {
            int index = aUserID.toLowerCase().indexOf(internalUserSuffix());
            canonicalUserID = aUserID.substring(0, index);
        }

        return canonicalUserID.trim();
     }



    /**
     * Returns aUserID converted into an e-mail address.  For Internal User IDs this is with the InternalUserSuffix appended.
     *
     * @return aUserID converted into an e-mail address
     */
    public static String emailAddressFromUserID(String aUserID)
    {
        /** require [a_user_id_not_null] aUserID != null; **/
        
        String emailAddressFromUserID = aUserID;
        if (User.isInternalUserID(aUserID))
        {
            emailAddressFromUserID += User.internalUserSuffix();
        }

        return emailAddressFromUserID;
        /** ensure [valid_result] Result != null;  **/
     }



    /**
     * Returns <code>true</code> if this userID and password can be authenticated with the Kerberos server.
     *
     * @param userID the user ID / principal name to authenticate
     * @param password the password string to authenticate this user with
     *
     * @return <code>true</code> if this user can be authenticated with the password, <code>false</code> if the user cannot be authenticated
     */
    public static boolean canAuthenticateInternalUser(String userID, String password)
    {
        /** require
        [has_internal_users] applicationHasInternalUsers();
        [user_id_not_null] userID != null;
        [password_not_null] password != null; **/
        
        boolean canAuthenticateInternalUser = false;

        DebugOut.println(1, "Authenticating user against Kerberos: " + userID);
        try
        {
            canAuthenticateInternalUser = KerberosAuthenticator.getInstance().canAuthenticate(userID, password);
        }
        catch (Exception e)
        {
            // We failed to authenticate for some reason...
        }
        DebugOut.println(1, "Kerberos authentication " +
                         (canAuthenticateInternalUser ? "succeeded" : "failed") +
                         " for user: " + userID);


        return canAuthenticateInternalUser;
    }



    /**
     * Returns the maximum length of the userID in characters.
     *
     * @return the maximum length of the userID in characters
     */
    public static Integer maxUserIDLength()
    {
        if (maxUserIDLength == null)
        {
            maxUserIDLength = new Integer(EOModelGroup.defaultGroup()
                                          .entityNamed("User").attributeNamed("userID").width());
        }
        
        return maxUserIDLength;

        /** ensure [valid_result] Result != null;  **/
    }


    
    public void awakeFromInsertion(EOEditingContext anEditingContext)
    {
         super.awakeFromInsertion(anEditingContext); 
    }



    /**
     * Users are copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this User
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }



    /* ************ STATIC METHODS ***************** */

    
    /**
     * Creates a new User with a userID of newUserID.  The User is inserted into the editing context, 
     * but not saved.  An e-mail containing the password is sent to new External Users.
     *
     * @param ec - the EOEditingContext to create this new user in.
     * @param newUserID - the userID for the new User.
     *
     * @return the newly created user
     */
    public static User createUser(EOEditingContext ec, String newUserID)
    {
        /** require
        [valid_ec] ec != null;
        [userID_valid] isValidUserID(newUserID);
        [user_doesnt_exist] User.userForUserID(newUserID, ec) == null;
        **/

        User newUser = User.newUser(newUserID, ec);

        return newUser;
        
        /** ensure [valid_result] Result != null;  **/
    }


    
    public static boolean validateArrayOfUserDictionaries( NSArray dictArray,
                                                           EOEditingContext ec ) {
        boolean valid = true;
        Enumeration e = dictArray.objectEnumerator();

        while( e.hasMoreElements() ) {
            // grab each dictionary in the array
            NSMutableDictionary newUserDict = (NSMutableDictionary)e.nextElement();

            NSArray errors = createUserFromDictionary(ec, newUserDict).validateUser();

            if( errors.count() > 0 ) {
                // TODO Fix this now that errors is an array not dictionary
                newUserDict.setObjectForKey(errors, ERROR_MESSAGE_KEY);
                valid = false;
            }
        }

        return valid;
    }


    
    public static void createUsersFromArrayOfUserDictionaries( NSArray dictArray,
                                                               EOEditingContext ec ) {
        Enumeration e = dictArray.objectEnumerator();

        while( e.hasMoreElements() )
        {
            // grab each dictionary in the array
            NSMutableDictionary newUserDict = (NSMutableDictionary)e.nextElement();
            createUserFromDictionary(ec, newUserDict);
        }
    }


    
    protected static User createUserFromDictionary(EOEditingContext ec, NSDictionary dict )
    {
        return createUser(ec, (String)dict.objectForKey(User.USER_ID_KEY));
    }



    /**
     * Returns the user matching userID, or null if no user matches.
     *
     * @param userID the userID to match when locating a user.  It is case insensitive.
     * @param ec the EOEditingContext to fetch this user into
     *
     * @return the user matching userID, or null if no user matches.
     */
    public static User userForUserID( String userID, EOEditingContext ec )
    {
        /** require [ec_not_null] ec != null; **/
        
        User matchedUser = null;

        if( (userID != null) && (userID.length() > 0))
        {
            NSArray results = EOUtilities.objectsWithQualifierFormat(ec, "User", "userID = %@",
                                                                     new NSArray(User.canonicalUserID(userID)));

            if( results.count() == 1 )
            {
                matchedUser = (User)results.objectAtIndex(0);
            }
            else if( results.count() > 0 )
            {
                throw new RuntimeException("Found multiple users with a userID of " + userID);
            }
            // else no user was found so we will return null
        }

        JassAdditions.post("User", "userForUserID()", (matchedUser == null) || matchedUser.userID().equalsIgnoreCase(User.canonicalUserID(userID)));

        return matchedUser;
    }



    /**
     * Returns the list of unique users who have configure permission (are members of a ConfigureGroup) on one or more Websites.
     *
     * @param ec - the editing context to return the list of users who have configure permission in
     * @return the list of unique users who have configure permission (are members of a ConfigureGroup) on one or more Websites.
     */
    public static NSArray usersWithConfigPermission(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        Enumeration configureGroupsEnumerator = ConfigureGroup.groups(ec).objectEnumerator();
        NSMutableSet usersWithConfigPermission = new NSMutableSet();

        while (configureGroupsEnumerator.hasMoreElements())
        {
            ConfigureGroup thisGroup = (ConfigureGroup) configureGroupsEnumerator.nextElement();
            usersWithConfigPermission.addObjectsFromArray(thisGroup.users());
        }

        return usersWithConfigPermission.allObjects();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of Users who are system administrators (unit admins for root unit).
     * 
     * @param ec the editing context to return the list of system administrators in
     * @return the list of Users who are system administrators
     */
    public static NSArray systemAdministrators(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/

        return OrgUnit.rootUnit(ec).admins();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of Users who are style administrators.
     * 
     * @param ec the editing context to return the list of style administrators in
     * @return the list of Users who are style administrators
     */
    public static NSArray styleAdministrators(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        EOFetchSpecification fs = EOFetchSpecification.fetchSpecificationNamed("styleAdministrators", "User");
        return ec.objectsWithFetchSpecification(fs);
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the list of Users who are unit administrators.
     * 
     * @param ec the editing context to return the list of unit administrators in
     * @return the list of Users who are unit administrators
     */
    public static NSArray unitAdministrators(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/

        EOFetchSpecification fs = EOFetchSpecification.fetchSpecificationNamed("orgUnitsForAdministrators", "OrgUnit");
        NSArray orgUnits = ec.objectsWithFetchSpecification(fs);
        NSArray admins = (NSArray)orgUnits.valueForKey("admins");  // Gives an array of admin arrays
        admins = Collection.arrayByCollapsingSubCollections(admins, true);
        return admins;

        /** ensure [result_not_null] Result != null; **/
    }



    /* *************** DELETEABLE INTERFACE ********** */
    public boolean canBeDeleted()
    {
        return (errorMessages() == null);
    }


    
    public NSArray errorMessages()
    {
        NSMutableArray errorArray = new NSMutableArray();

        // TODO - check any conditions that need to be met before a user is deleted and add an error message to an NSArray for any that have not been satisfied.
        if( websites() != null && websites().count() > 0 )
            errorArray.addObject("A user cannot be deleted if they own any websites.");

        if( errorArray.count() < 1 )
            errorArray = null;

        return errorArray;
    }

    

    /* *************** INSTANCE METHODS ************** */


    /**
     * Non-EOF validation for User EO.  Returns an array of validation failure messages.  The Array is empty if the User is valid.  Checks only validity of userID according to validateUserID(String) and also check that the userID is not in user by another User.
     *
     * @return  array of validation failure messages
     */
    public NSMutableArray validateUser()
    {
        /** require [user_inserted] editingContext() != null;  **/

        NSMutableArray errorMessages = new NSMutableArray();
        errorMessages.addObjectsFromArray(validateUserID(userID()));
        
        if ( errorMessages.count() == 0 )
        {
            if( editingContext().globalIDForObject(this).isTemporary() )
            {
                // we are validating a new object (it's not in the ec)
                if( User.userForUserID( userID(), editingContext() ) != null )
                {
                    errorMessages.addObject(USER_ID_EXISTS_ERROR);
                }
            }
            else
            {
                // we are validating an object already in the ec
                User tmpUser = User.userForUserID( userID(), editingContext() );
                if( tmpUser != null && ! tmpUser.equals(this) )
                {
                    errorMessages.addObject(USER_ID_EXISTS_ERROR);
                }
            }
        }

        return errorMessages;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Checks the passed user ID to see if it meets the criteria for a valid userID.
     *
     * @param aUserID the userID to validate
     * @return a dictionary of any validation failures
     */
    public static NSArray validateUserID(String aUserID)
    {
        NSMutableArray errorMessages = new NSMutableArray();

        if( aUserID == null || aUserID.length() < 2 )
        {
            errorMessages.addObject(NULL_USER_ID_ERROR);
        }
        else if ( ! URLUtils.stringContainsOnlyValidURLChars(aUserID) )
        {
            errorMessages.addObject(INVALID_CHARS_ERROR);
        }
        else if ((! applicationHasInternalUsers()) && (canonicalUserID(aUserID).indexOf("@") == -1))  // Don't use isInternalUserID()!
        {
            errorMessages.addObject(USER_ID_NOT_EMAIL);
        }
        else if (! EmailAddress.isValidAddressFormat(emailAddressFromUserID(canonicalUserID(aUserID))) )
        {
            errorMessages.addObject(USER_ID_NOT_EMAIL);
        }
        else if (aUserID.length() > maxUserIDLength().intValue())
        {
            errorMessages.addObject("UserID is too long.  The maximim number of characters is " + maxUserIDLength());
        }

        return errorMessages;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the passed user ID meets the criteria for a valid userID.
     *
     * @param aUserID - the userID to validate
     *
     * @return <code>true</code> if the passed user ID meets the criteria for a valid userID.
     */
    public static boolean isValidUserID(String aUserID)
    {
        return validateUserID(aUserID).count() == 0;
    }


    
    /**
     * Returns <code>true</code> if this user can be authenticated with the password, <code>false</code> if the user cannot be authenticated, or if the password is null.  This could be extended to add additional authentications based on Kerberos etc.  Authentication is done in an instance, rather than a static, method so that it can be overridden in UserCustom.java in GVCSMCustom.
     *
     * @param password the password string to authenticate this user with
     *
     * @return <code>true</code> if this user can be authenticated with the password, <code>false</code> if the user cannot be authenticated, or if the password is null.
     */
    public boolean canAuthenticateWithPassword(String aPassword)
    {
        boolean canAuthenticateWithPassword = false;

        if (aPassword != null)
        {
            if (isInternalUser())
            {
                canAuthenticateWithPassword = canAuthenticateInternalUser(userID(), aPassword);
            }
            else
            {
                canAuthenticateWithPassword = password().equals(aPassword);
            }
        }

        return canAuthenticateWithPassword;
    }



    /**
     * Returns <code>true</code> if this User can administer <code>website</code>.  System administrators
     * can administer all sites.  Unit administrators can administer all sites in the units they
     * are in charge of and all units below those units in the hierarchy. 
     *  
     * @param aWebsite the Website to check administration rights on.
     * @return <code>true</code> if this User can administer <code>website</code>
     */
    public boolean canManageWebsite(Website aWebsite) 
    {
        /** require [valid_website] aWebsite != null;  
                    [same_ec] aWebsite.editingContext().equals(editingContext());  **/
        OrgUnit siteParentOrgUnit = aWebsite.parentOrgUnit();
        boolean canManageWebsite = false;
        
        // The first three tests are optimizations
        if (isSystemAdmin())
        {
            canManageWebsite = true;
        }
        else if ( ! isUnitAdmin())
        {
            canManageWebsite = false;
        }
        else if( orgUnitsToAdmin().containsObject(siteParentOrgUnit))
        {
            canManageWebsite = true;
        }
        else if (adminableUnits().containsObject(siteParentOrgUnit) ) 
        {
            canManageWebsite = true;
        }

        return canManageWebsite;
    }



    /**
     * @return <code>true</code> if this user is a sys admin or is a designated Style Admin
     */
    public boolean canAdminStyles()
    {
        return isSystemAdmin() || (orgUnitToAdminStylesFor() != null);
    }


    
    /**
     * @param aUnit the OrgUnit to check style admin privledges in
     * @return <code>true</code> if this user can admin styles in aUnit
     */
    public boolean canAdminStylesInUnit(OrgUnit aUnit)
    {
        /** require [valid_unit] aUnit != null; 
                    [same_ec] aUnit.editingContext().equals(editingContext());
         **/
        return canAdminStyles() && (orgUnitToAdminStylesFor().equals(aUnit) || 
                                    orgUnitToAdminStylesFor().isParentOfOrgUnit(aUnit));
    }
    
    
    
    // Note: This method places <code>this</code> at the front of the array!
    // TODO Can this be moved to Website or Group?
    public NSArray sortUsers( NSArray unsortedUsers )
    {
        DebugOut.println( 16, "Entering..." );
        NSMutableArray sortOrdering = new NSMutableArray();
        NSMutableArray sortedUsers = new NSMutableArray( unsortedUsers );
        EOSortOrdering userOrdering = EOSortOrdering.sortOrderingWithKey( "userID", EOSortOrdering.CompareCaseInsensitiveAscending );

        sortOrdering.addObject( userOrdering );

        EOSortOrdering.sortArrayUsingKeyOrderArray( sortedUsers, sortOrdering );

        sortedUsers.removeObject( this );
        sortedUsers.insertObjectAtIndex(this, 0 );

        DebugOut.println( 10, "Leaving..." );
        return sortedUsers;
    }



    /**
     * Returns <code>true</code> if this user is an internal user.
     *
     * @return <code>true</code> if this user is an internal user.
     */
    public boolean isInternalUser() 
    {
        return isInternalUserID(userID());
    }



    /**
     * Returns <code>true</code> if this user is the Public user.
     *
     * @return <code>true</code> if this user is the Public user.
     */
    public boolean isPublicUser() 
    {
        return false;
    }

    
    
    /*
     * Perfroms basic edits on user ID before storeing it.  Ensures that all white space is removed first so that we don't get two users with the same e-mail address.  For example, 'bill@home.com' and 'bill@home.com '.  If the User ID ends in the internal user suffix, this suffix is removed before storing the ID.
     */
    public void setUserID(String newUserID)
    {
        if ((userID() == null) || ! userID().equals(newUserID))
        {
            if (newUserID != null)
            {
                newUserID = canonicalUserID(newUserID);
            }

            super.setUserID(newUserID);

            // If an internal user is changed to external or vice versa we need to either clear or create a password
            if (isInternalUserID(newUserID))
            {
                setPassword(null);
            }
            else if (password() == null)
            {
                setPassword(SMStringUtils.passwordWithLength(8));
            }
        }
    }


    
    public String emailAddress()
    {
        if( this.isInternalUser() )
            return this.userID() + internalUserSuffix();
        else
            return this.userID();
    }


    
    public boolean canConfigureMultipleSites()
    {
        // This implementation is too slow for users who can configure many sites.
        // return configurableWebsites().count() > 1;

        DebugOut.println( 25, "Entering");
        DebugOut.println( 25, "user's groups are: " + groups() );

        int configGroupCount = 0;

        Enumeration groupEnum = groups().objectEnumerator();
        while( groupEnum.hasMoreElements() )
        {
            if ( groupEnum.nextElement() instanceof ConfigureGroup)
            {
                configGroupCount++;

                if (configGroupCount > 1)
                {
                    break;
                }
            }
        }

        return configGroupCount > 1;
    }


    
    // This method returns a list of all the webites for which this user is a member of the website's configure group.  It is NOT a list of all the websites that a user can configure as unit and system admins have configuration privileges on websites within their area regardless of their membership in the configure group.  The true test of ability to configure a website is in the method SMSession.userCanConfigureCurrentWebsite() which ought to be in this class or in the Website class.  This method is used to display a list of the websites that a user has direct configure permission on without cluttering it for unit and sys admins.
    public NSArray configurableWebsites()
    {
        DebugOut.println( 25, "Entering");

        Enumeration groupEnum = groups().objectEnumerator();
        NSMutableArray configurableWebsites = new NSMutableArray();

        DebugOut.println( 25, "user's groups are: " + groups() );

        while( groupEnum.hasMoreElements() )
        {
            Group someGroup = (Group) groupEnum.nextElement();

            if(someGroup instanceof ConfigureGroup)
            {
                ConfigureGroup configureGroup = (ConfigureGroup)someGroup;
                configurableWebsites.addObject( configureGroup.parentWebsite() );
            }
        }

        DebugOut.println( 25, "Returning array of websites: " + configurableWebsites.valueForKey("siteID") );

        return configurableWebsites;
    }



    /**
     * Returns <code>true</code> if the user is an administrator for an OrgUnit.
     *
     * @return <code>true</code> if the user is an administrator for an OrgUnit.
     */
    public boolean isUnitAdmin()
    {
        return orgUnitsToAdmin().count() > 0;
    }

    

    /**
     * Returns <code>true</code> if the user is a system administrator.  Administrators of the root
     * unit are system administrators.
     *
     * @return <code>true</code> if the user is a system administrator.
     */
    public boolean isSystemAdmin()
    {
        boolean isSystemAdmin = false;
        Enumeration unitEnum = orgUnitsToAdmin().objectEnumerator();
        while (unitEnum.hasMoreElements() && ! isSystemAdmin)
        {
            isSystemAdmin = ((OrgUnit)unitEnum.nextElement()).isRootUnit();
        }
        
        return isSystemAdmin;
    }


  
    /**
     * Sets whether this User is a system administrator or not.  If the User is becoming a system 
     * administrator they are also made an administrator for the Root Org Unit.  If the User is being 
     * removed as a system administrator they are also removed as an administrator for the Root Org Unit.
     *
     */
    public void setIsSystemAdmin(boolean newIsSystemAdmin)
    {
        OrgUnit rootUnit = OrgUnit.rootUnit(editingContext());
        
        // If the isSystemAdmin state is being changed we need to take special action.
        if (newIsSystemAdmin != isSystemAdmin())
        {
            if (newIsSystemAdmin)
            {
                addObjectToBothSidesOfRelationshipWithKey(rootUnit, ORGUNITSTOADMIN);
            }
            else
            {
                removeObjectFromBothSidesOfRelationshipWithKey(rootUnit, ORGUNITSTOADMIN);
            }
        }

        /** ensure [set_is_system_admin_correctly] ((newIsSystemAdmin && isUnitAdmin() && isSystemAdmin()) || 
                                                    ( ! newIsSystemAdmin && (! isUnitAdmin() || ! isSystemAdmin()))); **/
    }



    /**
     * Returns <code>true</code> if this User is the one and only system administrator.  They must not be removed as system administrator if this is the case.
     *
     * @return <code>true</code> if the User is the one and only system administrator.
     */
    public boolean isLastSystemAdmin()
    {
        /** require [editing_context_not_null] editingContext() != null; **/
        
        NSArray systemAdmins = systemAdministrators(editingContext());

        return (systemAdmins.count() == 1) && (systemAdmins.containsObject(this));
    }
    
    
    
    /**
     * Returns a qualifer to match all of the OrgUnits that that user can administer.
     *  
     * @param pathToOrgUnit keyPath from object being qualifier to an OrgUnit, used as the key in a 
     * key-value qualifier
     * @return a qualifer to match all of the OrgUnits that that user can administer
     */
    public EOQualifier adminableUnitsQualifier(String pathToOrgUnit)
    {
        /** require [valid_path] pathToOrgUnit != null;  **/
        EOQualifier adminableUnitsQualifier = null;
        
        if (isSystemAdmin())
        {
            // Sys admins can admin everything
            adminableUnitsQualifier = new EOKeyComparisonQualifier(pathToOrgUnit, 
                                                                   EOQualifier.QualifierOperatorEqual, 
                                                                   pathToOrgUnit);
        }
        else if (isUnitAdmin())
        {
            // Unit admins can admin only a select list
            NSArray adminableUnits = adminableUnits();
            NSMutableArray orgUnitQualifiers = new NSMutableArray(adminableUnits.count());
            Enumeration unitEnumeration = adminableUnits.objectEnumerator();
            while (unitEnumeration.hasMoreElements())
            {
                OrgUnit orgUnit = (OrgUnit) unitEnumeration.nextElement();
                orgUnitQualifiers.addObject(new EOKeyValueQualifier(pathToOrgUnit, 
                                                                    EOQualifier.QualifierOperatorEqual, 
                                                                    orgUnit));
            }
            adminableUnitsQualifier = new EOOrQualifier(orgUnitQualifiers);
        }
        else
        {
            // Regular users can't admin anything
            adminableUnitsQualifier = new EOKeyComparisonQualifier(pathToOrgUnit, 
                                                                   EOQualifier.QualifierOperatorNotEqual, 
                                                                   pathToOrgUnit);
        }
        
        return adminableUnitsQualifier;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the list of all Org Units that this User can administer, including non-system wide
     * units.
     *  
     * @return the list of all Org Units that this User can administer
     */
    public NSArray adminableUnits()
    {
        return OrgUnit.allUnitsAtOrBelow(orgUnitsToAdmin(), OrgUnit.INCLUDE_NON_SYSTEM_WIDE_UNITS);
        /** ensure [valid_result] Result != null;
                   [all_units_included] Result.count() >= orgUnitsToAdmin().count();  **/
    }
    


    /**
     * Returns the list of all Org Units that this User can administer, including non-system wide
     * units sorted into the unit hierarchy.
     *   
     * @return the list of all Org Units that this User can administer
     */
    public NSArray orderedAdminableUnits()
    {
        return NSArrayAdditions.sortedArrayUsingComparator(adminableUnits(), OrgUnit.HierarchicalComparator);
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

        // Add manual validations performed elsewhere
        NSArray validationMessages = validateUser();
        if (validationMessages.count() == 0)
        {
            Enumeration messageEnum = validationMessages.objectEnumerator();
            while (messageEnum.hasMoreElements())
            {
                String message = (String) messageEnum.nextElement();
                exceptions.addObject(new NSValidation.ValidationException(message));
            }
        }
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }
    
    
    /* Implmentation of EditingContextNotification */

    /**
     * Called after a newly created User has been saved so that the password can be sent to 
     * non-internal users.
     */
    public void hasInserted()
    {
        /** require [has_userID] userID() != null;
                    [valid_state] isPublicUser() || isInternalUser() || (password() != null);   **/
        if ( ! (isInternalUser() || isPublicUser()))
        {
            SendEmail.sendUserCreationMessage(this);
        }
    }

    /**
     * Called before deleteObject is invoked on super (EOEditingContext).
     */
    public void willDelete() {}

    /**
     * Called after a deleted object has been removed from the persistent object store.  This would handle the need to clean up related, non-EO, resources when the EO was deleted.
     */
    public void hasDeleted() {}

    /**
     * Called before saveChanges is invoked on super (EOEditingContext).  This would allow things such as setting the dateModified before saving an EO.  Note that changes in this method may result in more updated (or inserted or deleted!) objects, so it should iterate until there are no more changes.
     */
    public void willUpdate() {}

    /**
     * Called after saveChanges is invoked on super (EOEditingContext).
     */
    public void hasUpdated() {}

    /**
     * Called after revert is invoked on our EOEditingContext.
     */
    public void hasReverted() {}

}
