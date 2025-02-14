// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.interfaces;

import com.gvcsitemaker.core.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
 
/**
 * Interface for all access controlled GVCSiteMaker resources.  The GroupPicker
 * also relies on this for setting access control. 
 */
public interface SMAccessible 
{

    /**
     * Returns <code>true</code> if this resource allows public access.
     *
     * @return <code>true</code> if this resource allows public access
     */
    public boolean isPublic();
    
    
    
    /**
     * Returns <code>true</code> if <code>aUser</code> has permission to view 
     * this resource.
     *
     * @param aUser the <code>User</code> to check against
     *
     * @return <code>true</code> if <code>aUser</code> has permission to view 
     * this resource
     */
    public boolean isViewableByUser(User aUser);
    
    
    
    /**
     * Returns <code>true</code> if this resource requires access to be via SSL.
     *
     * @return <code>true</code> if this resource requires access to be via SSL
     */
    public boolean requiresSSLConnection();
    
    
    
    /**
     * Permits access to this resource to members of the group.
     * 
     * @param aGroup the Group to allow access to
     */
    public void allowAccessForGroup(Group aGroup);
    
    
    
    /**
     * Removes permission access to this resource by members of the group.
     * 
     * @param aGroup the Group to revoke access to
     */
    public void revokeAccessForGroup(Group aGroup);
    
    
    
    /**
     * Returns the <code>Group</code>s which are permitted to access this resource. 
     * @return
     */
    public NSArray groups();


    public EOEditingContext editingContext();
    
}

