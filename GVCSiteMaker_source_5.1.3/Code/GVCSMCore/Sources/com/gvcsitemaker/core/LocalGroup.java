// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import java.util.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Group sub-class used to restrict or grant access to specific items in a single Website.  These 
 * groups are created and managed entirely by Website owners and co-owners and are local to 
 * GVCSiteMaker.
 */
public class LocalGroup extends _LocalGroup
{
    public static final String GROUP_TYPE = "Local";
    

    /**
     * Factory method to create new instances of Group.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static Group newGroup()
    {
        return (Group) SMEOUtils.newInstanceOf("LocalGroup");

        /** ensure [result_not_null] Result != null; **/
    }
 
    
    
    /**
     * Overridden to set type().
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        setType(GROUP_TYPE);

        /** ensure  [type_not_null] type() != null; **/
    }



    /**
     * Convenience method to display "Local" as the user presentable version of Group type.
     *
     * @return description of the Group type
     */
    public String typeDescription()
    {
        return "Local";
    }



    /**
     * Returns <code>true</code> if <code>aUser</code> is a member of this Group.
     *  
     * @param aUser the <code>User</code> to test for membership in this Group.
     * @return <code>true</code> if <code>aUser</code> is a member of this Group
     */
    public boolean isMember(User aUser)
    {
        /** require [same_ec] editingContext() == aUser.editingContext();  **/
        
        return users().containsObject(aUser);
    }
    
    
    
    /**
     * Returns <code>true</code> if the passed User can be removed from this Group.  
     * The owner of a Website cannot be removed from a Group in that Website.
     *
     * @return <code>true</code> if the passed User can be removed from this Group
     */
    public boolean canRemoveUser(User aUser)
    {
        return ! parentWebsite().owner().equals(aUser);
    }



    /**
     * Ensures that all Users in listOfUsers are members of this Group.  If a User 
     * in listOfUsers is already in this Group no action is taken.  If they are 
     * not in the Group then they are added as a group member.
     *
     * @param listOfUser the list of user to ensure are members of this Group
     */
    public void addUsers(NSArray listOfUsers)
    {
        /** require [valid_list]  listOfUsers != null;  **/

        Enumeration userEnumerator = new NSArray(listOfUsers).objectEnumerator();
        while (userEnumerator.hasMoreElements())
        {
            User aUser = (User)userEnumerator.nextElement();
            if ( ! users().containsObject(aUser))
            {
                addObjectToBothSidesOfRelationshipWithKey(aUser, "users");
            }
        }

        /** ensure [users_in_group] (forall i : {0 .. listOfUsers.count() - 1} # 
            users().containsObject(listOfUsers.objectAtIndex(i)));  **/
    }



    /**
     * Ensures that no Users in listOfUsers are members of this Group, except 
     * special users which can't be removed from the group.  If a User in 
     * listOfUsers is not in this Group no action is taken.  If they are in the 
     * Group but cannot be removed no action is taken.  If they are in Group and 
     * can be removed then they are removed from being a group member.
     *
     * @param listOfUser the list of user to ensure are members of this Group
     */
    public void removeUsers(NSArray listOfUsers)
    {
        /** require [valid_list]  listOfUsers != null;  **/

        Enumeration userEnumerator = new NSArray(listOfUsers).objectEnumerator();
        while (userEnumerator.hasMoreElements())
        {
            User aUser = (User)userEnumerator.nextElement();
            if (users().containsObject(aUser) && canRemoveUser(aUser))
            {
                removeObjectFromBothSidesOfRelationshipWithKey(aUser, "users");
            }
        }

        /** ensure
            [users_in_group] (forall i : {0 .. listOfUsers.count() - 1} #
                              ( ! canRemoveUser((User)listOfUsers.objectAtIndex(i))) ||
                              ! users().containsObject(listOfUsers.objectAtIndex(i)));  **/
    }



    /**
     * Returns the e-mail addresses for members of this group.
     *  
     * @return the e-mail addresses for members of this group
     */
    public NSArray membersEmailAddresses()
    {
        return (NSArray) users().valueForKey("emailAddress");
    }

}

