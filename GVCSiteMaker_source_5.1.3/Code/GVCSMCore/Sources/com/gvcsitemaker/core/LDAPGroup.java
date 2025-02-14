// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Group sub-class using LDAP to restrict or grant access to specific items in a single Website.  
 * The actual LDAP groups are created and managed externally to GVC.SiteMaker.  This object is a local
 * description of an external LDAP group.
 */
public class LDAPGroup extends _LDAPGroup
{
    
    public static final String GROUP_TYPE = "LDAP";
    
     
    // Optimization
    protected NSMutableArray previouslyAuthorizedUsers = new NSMutableArray();
    protected NSMutableArray previouslyRejectedUsers = new NSMutableArray();
    

    /**
     * Factory method to create new instances of LDAPGroup.  Use this rather than calling the constructor 
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static Group newGroup()
    {
        return (Group) SMEOUtils.newInstanceOf("LDAPGroup");

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
     * Returns <code>true</code> if <code>aUser</code> is a member of this LDAP Group.
     *  
     * @param aUser the <code>User</code> to test for membership in this Group.
     * @return <code>true</code> if <code>aUser</code> is a member of this Group
     */
    public boolean isMember(User aUser)
    {
        /** require [same_ec] editingContext() == aUser.editingContext();  **/

        boolean isMember;
        
        // Optimization to avoid LDAP lookups
        if (aUser.isPublicUser() || previouslyRejectedUsers.containsObject(aUser))  
        {
            isMember = false;
        }
        // Optimization to avoid repreated LDAP lookups
        else if (previouslyAuthorizedUsers.containsObject(aUser))  
        {
            isMember = true;
        }
        else
        {
            // This acutally does the LDAP lookup
            isMember = ldapBranch().isMember(this, aUser);

            if (isMember)
            {
                previouslyAuthorizedUsers.addObject(aUser);
            }
            else
            {
                previouslyRejectedUsers.addObject(aUser);
            }
        }

        return isMember;
    }



    /**
     * Returns <code>true</code> if group <code>ldapGroupName()</code> exists in
     * <code>ldapBranch()</code>.
     *  
     * @return <code>true</code> if group <code>ldapGroupName()</code> exists in
     * <code>ldapBranch()</code>
     */
    public boolean exists()
    {
        /** require [same_ec] ldapGroupName() != null;  **/
        
        return ldapBranch().groupExists(ldapGroupName());
    }



    /**
     * Convenience method to display the displayName as the user presentable version of Group type.
     *
     * @return description of the Group type
     */
    public String typeDescription()
    {
        return ldapBranch().displayName();
    }
    
    
    
    /**
     * Overridden to trim LDAP group name before setting it.  Empty strings are 
     * made null. 
     */
    public void setLdapGroupName(String aValue) 
    {
        super.setLdapGroupName(SMStringUtils.trimmedString(aValue));
    }



    /**
     * Overridden to trim LDAP group name before validating it.  Empty strings 
     * are made null.  Validates that the name is not null and that it exists
     * as a group.
     */
    public Object validateLdapGroupName(Object aName) 
        throws NSValidation.ValidationException
    {
        aName = SMStringUtils.trimmedString((String)aName);
        
        if (aName == null)
        {
            throw new NSValidation.ValidationException("The External Group Name must be entered.");
        }
        else if ( ! ldapBranch().groupExists((String)aName))
        {
            throw new NSValidation.ValidationException("The External Group Name entered is not correct.");
        }
        
        return aName;
    }

}

