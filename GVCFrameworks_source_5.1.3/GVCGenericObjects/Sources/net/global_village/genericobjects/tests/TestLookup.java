package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import net.global_village.genericobjects.Lookup;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TestLookup extends Lookup
{
   
    /**
     * A convenience method which returns all objects of this Entity sorted by <code>NameComparator</code>.  
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of all objects of the entity specified by <code>entityName</code> and sorted by <code>NameComparator</code>
     */
    static public NSArray orderedObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return Lookup.orderedObjects( editingContext, "TestLookup", Lookup.NameComparator );

        /** ensure [Result_not_null] Result != null; **/
    }    
}
