// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * LDAPGroupRecord is intended to be used as the implementing class for all entities representing 
 * LDAP group records fetched by LDAPBranch.  It provides functionality making it easier to determine
 * User membership in these groups.  
 */
public class LDAPGroupRecord extends com.webobjects.eocontrol.EOGenericRecord
{

    /**
     * Returns <code>true</code> if <code>ldapUserID</code> is found in the <code>members</code> or
     * <code>externalMembers</code> attributes.  It does not check for membership in sub-groups.
     *  
     * @param ldapUserID <code>String</code> user ID to check for membership.  This is assumed to be 
     * in the correct format for String.equals() comparison.
     * @return <code>true</code> if <code>ldapUserID</code> is found in the <code>members</code> or
     * <code>externalMembers</code> attributes
     */
    protected boolean isDirectMember(String ldapUserID)
    {
        /** require [valid_ldapUserID] ldapUserID != null;  **/

        boolean isDirectMember = NSArrayAdditions.caseInsensitiveContains(members(),ldapUserID);
        
        if ( ! isDirectMember)
        {
            isDirectMember = NSArrayAdditions.caseInsensitiveContains(externalMembers(), ldapUserID);
        }

        return isDirectMember;
    }
    
    
    
    /**
     * Returns the name that a give User is known by in the LDAP data.  The default is to prepend
     * <code>userIDAttribute()=</code> and append <code>,userSubTree()</code> (e.g. 
     * uid=somebody,ou=People,dc=foo,dc=com) for internal users and leave an external user ID alone.  
     * This could be replaced with some other scheme in a custom sub-class.
     * 
     * @param aUser the User to return the LDAP name for
     * @return the name that a given User is known by in the LDAP data
     */
    public String ldapName(LDAPBranch ldapBranch, User aUser)
    {
        /** require [valid_user] aUser != null;
                    [valid_userID] aUser.userID() != null;  **/        
        String ldapUserName = aUser.userID();
        if (aUser.isInternalUser())
        {
            ldapUserName = ldapBranch.userIDAttribute() + "=" + aUser.userID() + "," + ldapBranch.userSubTree();
        }

        return ldapUserName;
        /** ensure [valid_result] Result != null;   **/
    }
    
    

    /**
     * Checks each of the elements in <code>members()</code> with <code>isGroup()</code> and creates
     * a qualifier with <code>qualifierForGroup()</code> for any that are determined to be a group.
     * The list of these qualifiers is returned and can be used to fetch the sub-groups of this group.
     *  
     * @param groupSubTree suffix (e.g. ou=Groups,dc=foo,dc=com) identifying members of this group
     * as a Group (sub-group), rather than a person.
     * @return list of EOQualifiers that can be used to fetch the sub-groups.
     */
    public NSArray qualifiersForSubGroups(String groupSubTree)
    {
        /** require [valid_groupSubTree] groupSubTree != null;   **/
        
        NSMutableArray subGroupQualifiers = new NSMutableArray();
        Enumeration memberEnum = members().objectEnumerator();

        // Accumulate qualifiers for each sub-group
        while (memberEnum.hasMoreElements())
        {
            String member = (String) memberEnum.nextElement();
            if (isGroup(member, groupSubTree))
            {
                subGroupQualifiers.addObject(qualifierForGroup(member)); 
            }
        }

        return subGroupQualifiers;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns <code>true</code> if <code>member</code> is a group member rather than an individual
     * member.  The default test is to see if <code>member</code> ends with <code>groupSubTree</code>
     * but custom sub-classes are free to implement their own test.
     * 
     * @param member Distinguished Name describing this group member
     * @param groupSubTree sub-tree identifying the branch in the LDAP hierarchy that groups are located
     * in
     * @return <code>true</code> if <code>member</code> is a group member rather than an individual
     * member
     */
    public boolean isGroup(String member, String groupSubTree)
    {
        /** require [valid_member] member != null;   
                    [valid_groupSubTree] groupSubTree != null;   **/

        return member.endsWith(groupSubTree);
    }



    /**
     * Returns an EOQualifier that can be used to fetch the LDAP record identified by <code>member</code>
     * as a LDAPGroupRecord.  The default is <code>name=SMLDAPUtilities.firstLdapAttributeValue(member)</code>
     * Custom sub-classes are free to implement their own qualifier.
     * 
     * @param member Distinguished Name describing this group member
     * @return an EOQualifier that can be used to fetch the LDAP record identified by <code>member</code>
     * as a LDAPGroupRecord
     */
    public EOQualifier qualifierForGroup(String member)
    {
        /** require [valid_member] member != null;    **/   
        return new EOKeyValueQualifier("name", 
                                       EOQualifier.QualifierOperatorEqual, 
                                       SMLDAPUtilities.firstLdapAttributeValue(member));
        /** ensure [valid_result] Result != null;   **/
    }

    
    
    /**
     * Returns the Distinguished Names of the members of this group as an 
     * SMLDAPUtilities.ldapMultiValue().
     * 
     * @return the Distinguished Names of the members of this group as an
     * SMLDAPUtilities.ldapMultiValue()
     */
    public NSArray members()
    {
        return SMLDAPUtilities.ldapMultiValue(storedValueForKey("members"));
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the external members of this group as an SMLDAPUtilities.ldapMultiValue().
     * 
     * @return the external members of this group as an SMLDAPUtilities.ldapMultiValue()
     */
    public NSArray externalMembers()
    {
        return SMLDAPUtilities.ldapMultiValue(storedValueForKey("externalMembers"));
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the name(s) of this group as an SMLDAPUtilities.ldapMultiValue().
     * 
     * @return the name(s) of this group as an SMLDAPUtilities.ldapMultiValue()
     */
    public NSArray name()
    {
        return SMLDAPUtilities.ldapMultiValue(storedValueForKey("name"));
    }

}
