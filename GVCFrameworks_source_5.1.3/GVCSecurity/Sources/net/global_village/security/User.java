package net.global_village.security;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * <code>User</code> represents the users of a system.  It is used to record access status and related information. A User can be a member of a Group. Usernames must be unique and this is enforced in <code>validateForSave</code>.
 *
 * @author Copyright (c) 2001-6  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 11$
 */
public class User extends _User
{
    private static final long serialVersionUID = -5125361224552315140L;


    /**
     * Default comparator to be used when sorting Users.
     */
    static final public NSComparator DefaultComparator = new NameComparator();

    protected NSMutableSet allPrivilegesAndIncludedPrivileges;
    protected NSArray allPrivilegesAndIncludedPrivilegesNames;


    /**
     * Comparator to sort Users by userName, case insensitively.
     */
    protected static class NameComparator extends NSComparator
    {

        public int compare(Object user1, Object user2)
        {
            /* Jass doesn't seem to like this... require
            [valid_user1_param] user1 != null;
            [valid_user2_param] user2 != null; **/

            return (((User)user1)).userName().compareToIgnoreCase(((User)user2).userName());
        }

    }



    /**
     * Protected method that returns the <code>User</code> with this user name.
     *
     * @param aUserName the user name of the user to check
     * @param ec the editingContext to be used for evaluating
     * @param entityName the entity name of the user entity to search
     * @return the <code>User</code> with this user name, <code>null</code> otherwise
     */
    protected static User _user(String aUserName, EOEditingContext ec, String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null; **/
        JassAdditions.pre("User", "_user", aUserName.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        User matchingUser = null;

        try
        {
            matchingUser = (User)EOUtilities.objectMatchingKeyAndValue(ec,
                    entityName,
                    "userName",
                    aUserName);
        }
        catch (EOObjectNotAvailableException e) {
            // This is expected, not an error.
        }
        catch (Exception error)
        {
            throw new ExceptionConverter(error);
        }

        return matchingUser;
    }



    /**
     * Protected method that returns the <code>User</code> with this user name.
     *
     * @param aUserName the user name of the user to check
     * @param ec the editingContext to be used for evaluating
     * @return the <code>User</code> with this user name, <code>null</code> otherwise
     */
    protected static User _user(String aUserName, EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null; **/
        JassAdditions.pre("User", "_user", aUserName.length() > 0);

        return _user(aUserName, ec, "GVCUser");
    }



    /**
     * Protected method that returns the <code>User</code> with this user name and password.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the editingContext to be used for evaluating
     * @param entityName the entity name of the user entity to search
     * @return the <code>User</code> with this user name and password, <code>null</code> otherwise
     */
    protected static User _user(String aUserName,
                                String aPassword,
                                EOEditingContext ec,
                                String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null; **/
        JassAdditions.pre("User", "_user", aUserName.length() > 0);
        JassAdditions.pre("User", "_user", aPassword.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        User matchingUser = null;

        try
        {
            matchingUser = (User)EOUtilities.objectWithQualifierFormat(ec,
                    entityName,
                    "userName = %@ and password = %@",
                    new NSArray(new Object[] {aUserName, aPassword}));
        }
        catch (java.lang.Exception e) {}

        return matchingUser;
    }



    /**
     * Protected method that returns the <code>User</code> with this user name and password.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the editingContext to be used for evaluating
     * @return the <code>User</code> with this user name and password, <code>null</code> otherwise
     */
    protected static User _user(String aUserName,
                                String aPassword,
                                EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null; **/
        JassAdditions.pre("User", "_user", aUserName.length() > 0);
        JassAdditions.pre("User", "_user", aPassword.length() > 0);

        return _user(aUserName, aPassword, ec, "GVCUser");
    }



    /**
     * Returns <code>true</code> if a <code>User</code> with this user name exists.
     *
     * @param aUserName the user name of the user to check
     * @param ec the <code>editingContext</code> to be used for evaluation
     * @param entityName the entity name of the user entity to search
     * @return <code>true</code> if a user with this user name exists, <code>false</code> otherwise
     */
    public static boolean userExists(String aUserName, EOEditingContext ec, String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null; **/
        JassAdditions.pre("User", "userExists", aUserName.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        User matchingUser = _user(aUserName, ec, entityName);
        return (matchingUser != null);
    }


    /**
     * Returns <code>true</code> if a <code>User</code> with this user name exists.
     *
     * @param aUserName the user name of the user to check
     * @param ec the <code>editingContext</code> to be used for evaluation
     * @return <code>true</code> if a user with this user name exists, <code>false</code> otherwise
     */
    public static boolean userExists(String aUserName, EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null; **/
        JassAdditions.pre("User", "userExists", aUserName.length() > 0);

        User matchingUser = _user(aUserName, ec);
        return (matchingUser != null);
    }



    /**
     * Returns <code>true</code> if a <code>User</code> with this user name and password exists.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the <code>editingContext</code> to be used for evaluation
     * @param entityName the entity name of the user entity to search
     * @return <code>true</code> if a user with this user name exists, <code>false</code> otherwise
     */
    public static boolean userExists(String aUserName,
                                     String aPassword,
                                     EOEditingContext ec,
                                     String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null; **/
        JassAdditions.pre("User", "userExists", aUserName.length() > 0);
        JassAdditions.pre("User", "userExists", aPassword.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        User matchingUser = _user(aUserName, aPassword, ec, entityName);
        return (matchingUser != null);
    }


    /**
     * Returns <code>true</code> if a <code>User</code> with this user name and password exists.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the <code>editingContext</code> to be used for evaluation
     * @return <code>true</code> if a user with this user name exists, <code>false</code> otherwise
     */
    public static boolean userExists(String aUserName,
                                     String aPassword,
                                     EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null; **/
        JassAdditions.pre("User", "userExists", aUserName.length() > 0);
        JassAdditions.pre("User", "userExists", aPassword.length() > 0);

        User matchingUser = _user(aUserName, aPassword, ec);
        return (matchingUser != null);
    }



    /**
     * Returns all the users in the array of <code>UserGroups</code> passed in with no duplicates.  (Union)
     *
     * @param groups array of <code>UserGroups</code>
     * @return array of <code>Users</code>
     */
    public static NSArray usersInGroups(NSArray groups)
    {
        /** require [valid_param] groups != null; **/

        NSMutableSet users = new NSMutableSet();
        Enumeration groupsEnumerator = groups.objectEnumerator();

        while(groupsEnumerator.hasMoreElements())
        {
            UserGroup aGroup = (UserGroup) groupsEnumerator.nextElement();
            users.addObjectsFromArray(aGroup.users());
        }

        return users.allObjects();

        /** ensure [valid_result] Result != null; **/
    }




    /**
     * Returns the <code>User</code> with this user name.
     *
     * @param aUserName the user name of the user to check
     * @param ec the editingContext to be used for evaluating
     * @param entityName the entity name of the user entity to search
     * @return the <code>User</code> with this user name.
     */
    public static User user(String aUserName, EOEditingContext ec, String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null;
        [user_exists] userExists(aUserName, ec, entityName); **/
        JassAdditions.pre("User", "user", aUserName.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        // This raises an exception if there are no, or more than one, objects
        // returned.  The preconditions should protect this sufficiently.  There could
        // be a problem if the user was deleted between checking their existance and
        // "fetching" the object.  This is only a slim chance of this happening.
        User matchingUser = _user(aUserName, ec, entityName);

        return matchingUser;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the <code>User</code> with this user name.
     *
     * @param aUserName the user name of the user to check
     * @param ec the editingContext to be used for evaluating
     * @return the <code>User</code> with this user name.
     */
    public static User user(String aUserName, EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_ec_param] ec != null;
        [user_exists] userExists(aUserName, ec); **/
        JassAdditions.pre("User", "user", aUserName.length() > 0);

        // This raises an exception if there are no, or more than one, objects
        // returned.  The preconditions should protect this sufficiently.  There could
        // be a problem if the user was deleted between checking their existance and
        // "fetching" the object.  This is only a slim chance of this happening.
        User matchingUser = _user(aUserName, ec);

        return matchingUser;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the <code>User</code> with this user name and password.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the editingContext to be used for evaluating
     * @param entityName the entity name of the user entity to search
     * @return the <code>User</code> with this user name and password.
     */
    public static User user(String aUserName,
                            String aPassword,
                            EOEditingContext ec,
                            String entityName)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null;
        [valid_entityName_param] entityName != null;
        [user_exists] userExists(aUserName, aPassword, ec, entityName); **/
        JassAdditions.pre("User", "user", aUserName.length() > 0);
        JassAdditions.pre("User", "user", aPassword.length() > 0);
        JassAdditions.pre("User", "_user", entityName.length() > 0);

        // This raises an exception if there are no, or more than one, objects
        // returned.  The preconditions should protect this sufficiently.  There could
        // be a problem if the user was deleted between checking their existance and
        // "fetching" the object.  This is only a slim chance of this happening.
        User matchingUser = _user(aUserName, aPassword, ec, entityName);
        return matchingUser;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns the <code>User</code> with this user name and password.
     *
     * @param aUserName the user name of the user to check
     * @param aPassword the password of the user to check
     * @param ec the editingContext to be used for evaluating
     * @return the <code>User</code> with this user name and password.
     */
    public static User user(String aUserName,
                            String aPassword,
                            EOEditingContext ec)
    {
        /** require
        [valid_aUserName_param] aUserName != null;
        [valid_aPassword_param] aPassword != null;
        [valid_ec_param] ec != null;
        [user_exists] userExists(aUserName, aPassword, ec); **/
        JassAdditions.pre("User", "user", aUserName.length() > 0);
        JassAdditions.pre("User", "user", aPassword.length() > 0);

        // This raises an exception if there are no, or more than one, objects
        // returned.  The preconditions should protect this sufficiently.  There could
        // be a problem if the user was deleted between checking their existance and
        // "fetching" the object.  This is only a slim chance of this happening.
        User matchingUser = _user(aUserName, aPassword, ec);
        return matchingUser;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * From the list of users past in returns all those who are active.  No sorting is performed.
     *
     * @param incomingUsers of users to cull active users from
     * @return all active users in array
     */
    public static NSArray activeUsers(NSArray incomingUsers)
    {
        /** require [valid_param] incomingUsers != null; **/

        return EOQualifier.filteredArrayWithQualifier(incomingUsers, User.isAccountDisabledQualifier());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an EOQualifier to select Users whose account is disabled.
     *
     * @return an EOQualifier to select Users whose account is disabled.
     */
    protected static EOQualifier isAccountDisabledQualifier()
    {
        NSArray args = new NSArray(new net.global_village.foundation.GVCBoolean(false));
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("isAccountDisabled = %@", args);
        return qualifier;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to initialize some values.
     *
     * @param ec the editing context this object is being inserted into
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);

        setMustChangePassword(new GVCBoolean(false));
        setCanChangePassword(new GVCBoolean(true));
        setIsAccountDisabled(new GVCBoolean(false));

        /** ensure
        [must_change_password] mustChangePassword().isFalse();
        [can_change_password] canChangePassword().isTrue();
        [is_account_disabled] isAccountDisabled().isFalse(); **/
    }



    /**
     * Returns <code>true</code> if this <code>User</code> is granted the indicated privilege.
     *
     * @param thePrivilege the <code>Privilege</code> to check
     * @return <code>true</code> if this <code>User</code> is granted the <code>thePrivilege</code> argument, <code>false</code> otherwise
     */
    public synchronized boolean hasPrivilege(Privilege thePrivilege)
    {
        /** require [valid_param] thePrivilege != null; **/

        determinePrivilegesIfNeeded();
        return allPrivilegesAndIncludedPrivileges.containsObject(thePrivilege);
    }



    /**
     * Returns <code>true</code> if this <code>User</code> is granted the privilege with the passed name.
     *
     * @param thePrivilegeName the privilege name to be evaluated
     * @return <code>true</code> if this <code>User</code> is granted the privilege with the passed name, <code>false</code> otherwise
     */
    public boolean hasPrivilegeNamed(String thePrivilegeName)
    {
        /** require
        [valid_editingContext] editingContext() != null;
        [valid_thePrivilegeName_param] thePrivilegeName != null;
        [privilege_exists] Privilege.privilegeExists(thePrivilegeName, editingContext()); **/

        determinePrivilegesIfNeeded();
        return allPrivilegesAndIncludedPrivilegesNames.containsObject(thePrivilegeName);
    }



    /**
     * Examines the groups the <code>User</code> is a member of to determine which privileges they have.  Caches the privileges to speed subsequent queries.
     */
    public synchronized void determineCurrentPrivileges()
    {
        allPrivilegesAndIncludedPrivileges = new NSMutableSet();

        //Get all the privileges of this user's groups
        Enumeration eachGroup = groups().objectEnumerator();
        while(eachGroup.hasMoreElements())
        {
            UserGroup thisGroup = (UserGroup) eachGroup.nextElement();
            allPrivilegesAndIncludedPrivileges.addObjectsFromArray(thisGroup.privileges());

            //Get all the privileges if this users' groups's privileges
            Enumeration eachPrivilege = thisGroup.privileges().objectEnumerator();
            while(eachPrivilege.hasMoreElements())
            {
                Privilege thisPrivilege = (Privilege) eachPrivilege.nextElement();
                allPrivilegesAndIncludedPrivileges.addObjectsFromArray(thisPrivilege.includedPrivileges());
            }
        }

        allPrivilegesAndIncludedPrivilegesNames = (NSArray)allPrivilegesAndIncludedPrivileges.allObjects().valueForKey("name");
        /** ensure
        [privilege_cache_created] allPrivilegesAndIncludedPrivileges != null;
        [privilege_name_cache_created] allPrivilegesAndIncludedPrivilegesNames != null; **/
    }



    /**
     * Protected method.  Supports lazy creation of privilege cache.
     */
    protected synchronized void determinePrivilegesIfNeeded()
    {
        if (allPrivilegesAndIncludedPrivileges == null)
        {
            determineCurrentPrivileges();
        }
        /** ensure
        [privilege_cache_created] allPrivilegesAndIncludedPrivileges != null;
        [privilege_name_cache_created] allPrivilegesAndIncludedPrivilegesNames != null; **/
    }



    /**
     * Convenience method to obtain a test description of the <code>User</code>.
     *
     * @return a text description (its name) of the <code>User</code>.
     */
    public String toString()
    {
        return userName() == null ? "" : userName();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Validate the user.
     */
    public void validateForSave() throws com.webobjects.foundation.NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( com.webobjects.foundation.NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(ValidationExceptionAdditions.unaggregateExceptions(e));
        }

        // Check for userName uniqueness, this validation should be done here instead in validateForKey so that it will only be called everytime a user tries to save and not during takeValuesFromRequest (in the case where userName is directly bound to a WOComponent)
        if (userName() != null)
        {
            NSDictionary snapshot = editingContext().committedSnapshotForObject(this);
            Object committedUserName = snapshot.objectForKey("userName");

            // Do not validate if it did not change
            if ( ! userName().equals(committedUserName))
            {
                if (userExists(userName(), editingContext()))
                {
                    // Get the corresponding user
                    User namedUser = User.user(userName(), editingContext());

                    // If we find a user and they are not the same, we have a problem
                    if (namedUser != this)
                    {
                        exceptions.addObject(new com.webobjects.foundation.NSValidation.ValidationException("The user name must be unique."));
                    }
                }
            }
        }

        // Conflict in changing password options
        if (mustChangePassword().isTrue() && canChangePassword().isFalse())
        {
            exceptions.addObject(new com.webobjects.foundation.NSValidation.ValidationException("Must change password and can change password options are conflicting."));
        }


        if (exceptions.count() > 0)
        {
            throw EOFValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * This method changes the users password and removes the flag to ask the user to change their password
     *
     * @param newPassword the password to replace the existing one.
     */
    public void changePasswordTo(String newPassword)
    {
        /** require
        [valid_param] newPassword != null;
        [new_password_is_different] ! newPassword.equals(password()); **/

        setPassword(newPassword);
        setMustChangePassword(new GVCBoolean(false));

        /** ensure
        [password_is_changed] newPassword.equals(password());
        [user_doesnt_need_to_change_password] ! mustChangePassword().booleanValue(); **/
    }



}
