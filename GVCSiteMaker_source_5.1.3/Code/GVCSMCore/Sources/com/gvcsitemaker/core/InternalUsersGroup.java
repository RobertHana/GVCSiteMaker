// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;


/**
 * Group sub-class in which every Internal User is implicitly a member.  This allows for easy granting 
 * of permission to anyone who is an Internal User.  This is a system group as there is only ever one.
 */
public class InternalUsersGroup extends _InternalUsersGroup
{
     protected static EOGlobalID internalUsersGID = null;
     public static final String GROUP_TYPE = "InternalUsers";
     

    /**
     * Convenience method to return the Internal Users access Group.
     *
     * @param ec - the editing context to return the Internal Users Group in
     * @returns the Internal Users Group in the indicated editing context.
     */
    public static Group group(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        
        if (internalUsersGID == null) 
        {
            InternalUsersGroup group = (InternalUsersGroup) EOUtilities.objectMatchingKeyAndValue(ec, "InternalUsersGroup", TYPE, GROUP_TYPE);
            internalUsersGID = group.globalID();           
        }

        return (Group) ec.faultForGlobalID(internalUsersGID, ec);
        
        /** ensure [result_not_null] Result != null; **/
    }
    


    /**
     * Factory method to create new instances of Group.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static Group newGroup()
    {
        return (Group) SMEOUtils.newInstanceOf("InternalUsersGroup");

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
     * Returns <code>true</code> as InternalUsersGroup is one of the special system 
     * controlled groups.
     *
     * @return <code>true</code> as InternalUsersGroup is one of the special system 
     * controlled groups
     */
    public boolean isSystemGroup()
    {
        return true;
    }
    
    
    
    /**
     * Returns <code>true</code> if <code>aUser</code> is an Internal User.
     *  
     * @param aUser the <code>User</code> to test for membership in this Group.
     * @return <code>true</code> if <code>aUser</code> is an Internal User
     */
    public boolean isMember(User aUser)
    {
        /** require [same_ec] editingContext() == aUser.editingContext();  **/
        
        return aUser.isInternalUser();
    }


}

