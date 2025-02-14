package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import net.global_village.eofextensions.EOEditingContextAdditions;
import net.global_village.genericobjects.VersionableArchivableLookup;
import net.global_village.genericobjects.VersionableArchivableObject;



/**
 * TestArchivableLookup.  Archivable Lookups can be edited and archived freely.
 * (Versioned Archivable Lookups can only be archived - see TestVersionedArchivableLookup)
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 *
 */ 
public class TestArchivableLookup extends VersionableArchivableLookup
{
    // default shouldVersion inherited which returns false
    
    // and now for the various wrapper methods ...
    
    /**
     * Fetchs all instances of this entity, qualified by aQualifier. 
     *
     * @param aQualifier the qualifier to use for the fetch
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray objectsWithQualifier(EOQualifier aQualifier, EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return EOEditingContextAdditions.objectsWithQualifier(editingContext, "TestArchivableLookup", aQualifier);

        /** ensure [Result_not_null] Result != null; **/
    }

    

    /**
     * Fetchs all active objects, qualified by aQualifier.  This method should be used for fetching to avoid returning inactive objects.
     *
     * @param aQualifier the qualifier to use for the fetch
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray all active objects from this Entity, qualified by aQualifier
     */
    static public NSArray activeObjectsWithQualifier(EOQualifier aQualifier, EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.activeObjectsWithQualifier( editingContext, "TestArchivableLookup", aQualifier);

        /** ensure [Result_not_null] Result != null; **/
    }

    

    /**
    * Fetchs all active objects, qualified by aQualifier, sorted by NameComparator.
     *
     * @param aQualifier the qualifier to use for the fetch
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray all active objects from this entity, qualified by aQualifier, sorted by compare()
     */
    static public NSArray orderedActiveObjectsWithQualifier(EOQualifier aQualifier, EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedActiveObjectsWithQualifier(editingContext, "TestArchivableLookup", aQualifier, VersionableArchivableLookup.NameComparator );

        /** ensure [Result_not_null] Result != null; **/
    }

    

    /**
     *  Fetches all active objects sorted by compare(). 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray orderedActiveObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedActiveObjects(editingContext, "TestArchivableLookup", VersionableArchivableLookup.NameComparator);

        /** ensure [Result_not_null] Result != null; **/
    }

    

    /**
     * Fetchs all objects sorted by compare().
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray orderedObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedObjects(editingContext, "TestArchivableLookup", VersionableArchivableLookup.NameComparator );

        /** ensure [Result_not_null] Result != null; **/
    }


    // compares the same way as superclass...

}
