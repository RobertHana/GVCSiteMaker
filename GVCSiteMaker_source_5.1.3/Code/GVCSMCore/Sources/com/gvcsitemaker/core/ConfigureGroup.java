// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
  
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Group sub-class representing configuration permission on a single Website.
 * This is also referred to as the Website Owner/Co-owners group in the UI.
 */
public class ConfigureGroup extends _ConfigureGroup
{
    public static final String GROUP_TYPE = "Configure";
    
    /**
     * Returns all Configure Website Groups (Groups to be used by Websites as the Configure Website group).
     *
     * @param ec - the editing context to return the Groups in
     * @returnall Configure Website Groups 
     */
    public static NSArray groups(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
        return EOUtilities.objectsWithQualifierFormat(ec, "Group", "type = %@", new NSArray(GROUP_TYPE));

        /** ensure [result_not_null] Result != null; **/
    }
    


    /**
     * Factory method to create new instances of Group.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static Group newGroup()
    {
        return (Group) SMEOUtils.newInstanceOf("ConfigureGroup");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overridden to set type() and name().
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        setType(GROUP_TYPE);
        setName("Site Owner/Co-owners");
        
        /** ensure [type_not_null] type() != null;  [name_not_null] name() != null; **/
    }



    /**
     * Returns <code>false</code> as Configure groups must not be deleted 
     *
     * @return <code>false</code> 
     */
    public boolean canBeDeleted()
    {
        return false;
    }



    /**
     * Returns <code>true</code> as the Configure group is always in use
     *
     * @return <code>true</code> as the Configure group is always in use
     */
    public boolean isInUse()
    {
        return true;
    }



}

