// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * <p>Groups are used to restrict or grant access to specific items (Sections, SiteFiles) or to specific 
 * functionality (e.g. the configuring a Website).  Items are associated with a Group to restrict 
 * access to them to members of that Group.  Items can be asscociated with more than one Group to grant 
 * access to anyone who is a memeber of any of those groups.  A User is conferred access rights by 
 * being made a member of a Group.  Group is abstract with sub-classes implementing specific methods
 * of determining membership.</p>
 */
public abstract class Group extends _Group
{


    /**
     * Overridden to set creationDate().
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        if (creationDate() == null)
        {
            setCreationDate(Date.now());
        }

        /** ensure [creation_date_not_null] creationDate() != null;  **/
    }



    /**
     * Returns a copy of this Group.  System groups are copied by reference, the 
     * other groups are copied deeply.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the 
     * object the copy was made from.
     * @return a copy of this Group
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        Group copy = this;  // Reference copy system groups  
        if ( ! isSystemGroup())
        {
            copy = (Group) super.duplicate(copiedObjects);
        }

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Returns <code>true</code> if this Group is one of the special system 
     * controlled groups.  These groups need special handling, such as being 
     * protected from deletion or duplication.
     *
     * @return <code>true</code> if this Group is one of the special system 
     * controlled groups
     */
    public boolean isSystemGroup()
    {
        return false;
    }



    /**
     * Returns <code>true</code> if this Group can be deleted, <code>false</code> if it must be 
     * protected from deletion.  In the general case system groups and groups in use should not be 
     * deleted.
     *
     * @return <code>true</code> if this Group can be deleted 
     */
    public boolean canBeDeleted()
    {
        return ! (isSystemGroup() || isInUse());
    }



    /**
     * Returns <code>true</code> if this Group is in active use (is protecting a resource or the
     * target of data access notifcations). 
     *
     * @return <code>true</code> if this Group is in active use
     */
    public boolean isInUse()
    {
        // In the general case system groups are considered to always be in use
        return ! isSystemGroup();
    }
    

    
    /**
     * Convenience method for debugging, returns the Group name.
     *
     * @return the Group name.
     */
    public String toString()
    {
        return name();
    }



    /**
     * Convenience method to display user presentable version of Group type.
     *
     * @return description of the Group type
     */
    public String typeDescription()
    {
        return type();
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

        // Generation gap pattern prevents this method from being declared abstract
        throw new RuntimeException("Forgot to implement isMember(User)");
    }

    

    /**
     * Overridden to trim name before setting it.  Empty strings are made null. 
     */
    public void setName(String aValue) 
    {
        super.setName(SMStringUtils.trimmedString(aValue));
    }



    /**
     * Overridden to trim name before validating it.  Empty strings are made null. 
     * Validates that the name is not null and not already in use.
     */
    public Object validateName(Object aValue) 
        throws NSValidation.ValidationException
    {
        aValue = SMStringUtils.trimmedString((String)aValue);
        
        if (aValue == null)
        {
            throw new NSValidation.ValidationException("You have to have a name for this Access Group.");
        }
 
        return aValue;
    }



}
