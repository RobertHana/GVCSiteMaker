package net.global_village.security;

import net.global_village.foundation.ExceptionConverter;
import net.global_village.foundation.JassAdditions;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.*;
import com.webobjects.foundation.NSArray;


/**
 * A group which has Users as members.  A group has Privileges granted to it and confers
 * these privileges on its members.
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class UserGroup extends _UserGroup
{
    private static final long serialVersionUID = 1569324627404507211L;


    /**
     * Protected Helper method to obtain the named <code>UserGroup</code> with the given <code>EOEditingContext</code>.
     *
     * @param aName name of the <code>UserGroup</code> to find
     * @param editingContext editingContext used for obtaining the UserGroup
     * @return the named usergroup or <code>null</code> if it was not found
     */
    protected static UserGroup _userGroup(String aName,
                                          EOEditingContext editingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_editingContext_param] editingContext != null; **/
        JassAdditions.pre("UserGroup", "_userGroup", aName.length() > 0);

        UserGroup theUserGroup;

        try
        {
            theUserGroup = (UserGroup) EOUtilities.objectMatchingKeyAndValue(editingContext,
                                                                             "GVCUserGroup",
                                                                             "name",
                                                                             aName);
        }
        catch (Exception e)
        {
            theUserGroup = null;
        }

        return theUserGroup;
    }



    /**
     * Determines whether the named <code>UserGroup</code> with the given <code>EOEditingContext</code> exists.
     *
     * @param aName name of the <code>UserGroup</code> to find
     * @param editingContext editingContext used for obtaining the UserGroup
     * @return <code>true</code> if the named <code>UserGroup</code> exists, <code>false</code> if it was not found
     */
    public static boolean userGroupExists(String aName,
                                          EOEditingContext editingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_editingContext_param] editingContext != null; **/
        JassAdditions.pre("UserGroup", "userGroupExists", aName.length() > 0);

        return (UserGroup._userGroup(aName, editingContext) != null);
    }



    /**
     * Method to obtain a named <code>UserGroup</code> with the given <code>EOEditingContext</code>.
     *
     * @param aName name of the <code>UserGroup</code> to find
     * @param editingContext editingContext used for obtaining the UserGroup
     * @return the named usergroup
     */
    public static UserGroup userGroup(String aName,
                                      EOEditingContext editingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_editingContext_param] editingContext != null;
        [usergroup_exists] userGroupExists(aName, editingContext); **/
        JassAdditions.pre("UserGroup", "userGroup", aName.length() > 0);

        return UserGroup._userGroup(aName, editingContext);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * List of all user groups sorted by name.
     *
     * @return list of all user groups sorted by name.
     */
    public static NSArray orderedListOfAllUserGroups(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        NSArray userGroups
           = net.global_village.eofextensions.EOEditingContextAdditions.orderedObjects(ec, "GVCUserGroup", "name", EOSortOrdering.CompareCaseInsensitiveAscending);

        return userGroups;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Compares two <code>UserGroups</code> by name case insensitively.
     *
     * @param otherUserGroup the otherUserGroup to compare itself against
     * @return returns the same result as the String <code>compareTo()</code> method
     */
    public int compareByName(UserGroup otherUserGroup)
    {
        /** require [valid_param] otherUserGroup != null; **/

        return name().compareToIgnoreCase(otherUserGroup.name());
    }



    /**
     * Convenience method to obtain a description of the <code>UserGroup</code>.
     *
     * @return a description (its name) of the <code>UserGroup</code>.
     */
    public String toString()
    {
        /** require
        [valid_name] name() != null;
        [name_is_at_least_one_char_long] name().length() > 0; **/

        return name();

        /** ensure
        [valid_result] Result != null;
        [result_is_at_least_one_char_long] Result.length() > 0; **/
    }



    /**
     * Method to determine if this <code>UserGroup</code> has the given user as a member
     *
     * @param aUser the user to check the membership of.
     * @return <code>true</code> if the indicated user is a member of this group.
     */
    public boolean hasAsMember(User aUser)
    {
        /** require [valid_param] aUser != null; **/

        return users().containsObject(aUser);
    }



    /**
     * Checks if the <code>group</code> has a given <code>privilege</code>.
     *
     * @param aPrivilege the <code>Privilege</code> being checked for membership in.
     * @return <code>true</code> if this group has been granted the indicated privilege
     */
    public boolean hasPrivilege(Privilege aPrivilege)
    {
        /** require [valid_param] aPrivilege != null; **/

        return privileges().containsObject(aPrivilege);
    }



    /**
     * Returns the name that describes the receiver. Useful for debugging.
     *
     * @return name()
     */
    public String eoShallowDescription()
    {
        return name();
    }



    /**
      * List of users who are active sorted by compare().  To be active users must not have isAccountDisabled.
      *
      * @return array of active users sorted by name.
      */
    public NSArray users()
    {
        EOQualifier qualifier = EOQualifier.qualifierToMatchAllValues(new NSDictionary(this, "groups"));
        EOFetchSpecification eofetchspecification = new EOFetchSpecification("GVCUser", qualifier, null, false, true, null);
        return editingContext().objectsWithFetchSpecification(eofetchspecification);
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * List of users who are active sorted by compare().  To be active users must not have isAccountDisabled.
     *
     * @return array of active users sorted by name.
     */
   public NSArray activeUsers()
   {
       /** require [users_array_setup] users() != null; **/

       NSArray activeUsers;

       NSArray interimArray = User.activeUsers(users());

       // We are not doing anything with this exception so just make it a runtime exception if it happens.
       try
       {
           activeUsers = interimArray.sortedArrayUsingComparator(User.DefaultComparator);
       }
       catch (com.webobjects.foundation.NSComparator.ComparisonException e)
       {
           throw new ExceptionConverter(e);
       }

       return activeUsers;

       /** ensure [valid_result] Result != null; **/
   }



}
