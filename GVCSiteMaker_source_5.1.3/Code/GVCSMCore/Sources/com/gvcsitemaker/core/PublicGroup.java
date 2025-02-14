// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;


/**
 * Group sub-class which every user is implicitly member of.  Items with this access group allow 
 * access by anyone.  Also referred to as the Everyone group.  This is a system group as there is 
 * only ever one.
 */
public class PublicGroup extends _PublicGroup
{

     protected static EOGlobalID publicGroupGID = null;
     public static final String GROUP_TYPE = "Public";
    
    /**
     * Convenience method to return the Public access Group.
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Public Group in the indicated editing context.
     */
    public static Group group(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        if (publicGroupGID == null) 
        {
            PublicGroup group = (PublicGroup) EOUtilities.objectMatchingKeyAndValue(ec, "PublicGroup", TYPE, GROUP_TYPE);
            publicGroupGID = group.globalID();
        }
 
        return (Group) ec.faultForGlobalID(publicGroupGID, ec);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of Group.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static Group newGroup()
    {
        return (Group) SMEOUtils.newInstanceOf("PublicGroup");

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
     * Returns <code>true</code> as PublicGroup is one of the special system 
     * controlled groups.
     *
     * @return <code>true</code> as PublicGroup is one of the special system 
     * controlled groups
     */
    public boolean isSystemGroup()
    {
        return true;
    }



    /**
     * Returns <code>true</code> as everyone is a member of this Group.
     *  
     * @param aUser the <code>User</code> to test for membership in this Group.
     * @return <code>true</code> as everyone is a member of this Group
     */
    public boolean isMember(User aUser)
    {
        /** require [same_ec] editingContext() == aUser.editingContext();  **/
        
        return true;
    }


}

