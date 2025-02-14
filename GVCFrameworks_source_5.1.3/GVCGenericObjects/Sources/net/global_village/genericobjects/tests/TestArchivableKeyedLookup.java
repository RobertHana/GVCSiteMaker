package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;

import net.global_village.eofextensions.EOEditingContextAdditions;
import net.global_village.genericobjects.VersionableArchivableKeyedLookup;
import net.global_village.genericobjects.VersionableArchivableObject;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TestArchivableKeyedLookup extends VersionableArchivableKeyedLookup
{
    // wrappers for direct superclass (VersionableArchivableKeyedLookup)
    
    
    /**
     * Returns the object of entity where key == aKey and isActive == YES.  Throws an exception if there are no matching objects, or more than one matching object. 
     *
     * @param aKey key of specific object to look for.
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return object associated with <code>aKey</code>
     */
    static public TestArchivableKeyedLookup objectForKey(String aKey, EOEditingContext editingContext)
    {
        /**  require
        [aKey_not_null] aKey != null;
        [editingContext_not_null] editingContext != null; 
        **/

        return (TestArchivableKeyedLookup) VersionableArchivableKeyedLookup.objectForKey(editingContext, "TestArchivableKeyedLookup", aKey);

        /**  ensure [Result_not_null] Result != null;  **/ 
    }



    // wrappers for superclass (VersionableArchivableObject)

   //  default shouldVersion inherited which returns false


    
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
        
        return EOEditingContextAdditions.objectsWithQualifier(editingContext, "TestArchivableKeyedLookup", aQualifier);

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
        
        return VersionableArchivableObject.activeObjectsWithQualifier( editingContext, "TestArchivableKeyedLookup", aQualifier );

        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     * Fetchs all active objects, qualified by aQualifier, sorted by compare().
     *
     * @param aQualifier the qualifier to use for the fetch
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray all active objects from this entity, qualified by aQualifier, sorted by compare()
     */
    static public NSArray orderedActiveObjectsWithQualifier(EOQualifier aQualifier,
                                                            EOEditingContext editingContext,
                                                            NSComparator comparator)
    {
        /** require [editingContext_not_null] editingContext != null; **/ 
        
        return VersionableArchivableObject.orderedActiveObjectsWithQualifier(editingContext, "TestArchivableKeyedLookup", aQualifier, comparator);

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
        
        return VersionableArchivableObject.orderedActiveObjects(editingContext, "TestArchivableKeyedLookup", VersionableArchivableObject.ActiveComparator);

        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     * Fetchs all objects sorted by NameComparator.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray orderedObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/ 
        
        return VersionableArchivableObject.orderedObjects(editingContext, "TestArchivableKeyedLookup", NameComparator );

        /** ensure [Result_not_null] Result != null; **/
    }
}
