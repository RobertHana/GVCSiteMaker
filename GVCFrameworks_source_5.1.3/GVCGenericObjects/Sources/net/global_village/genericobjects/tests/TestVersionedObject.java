package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;

import net.global_village.eofextensions.EOEditingContextAdditions;
import net.global_village.genericobjects.VersionableArchivableObject;



/**
 * TestVersionedObject -  an example subclass of VersionableArchivableObject which uses versioning
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */  
public class TestVersionedObject extends VersionableArchivableObject
{

    public String name()
    {
        willRead();
        return (String)storedValueForKey("name");
    }

    
    public void setName(String value)
    {
        willChange();
        takeStoredValueForKey(value, "name");
    }

    

    // customization methods follow


    
    public boolean shouldVersion()
    {
        return true;
    }



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
        
        return EOEditingContextAdditions.objectsWithQualifier(editingContext, "TestVersionedObject", aQualifier);

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
        
        return VersionableArchivableObject.activeObjectsWithQualifier( editingContext, "TestVersionedObject", aQualifier );

        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     * Fetchs all active objects, qualified by aQualifier, sorted by ActiveComparator
     *
     * @param aQualifier the qualifier to use for the fetch
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray all active objects from this entity, qualified by aQualifier, sorted by ActiveComparator
     */
    static public NSArray orderedActiveObjectsWithQualifier(EOQualifier aQualifier, EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedActiveObjectsWithQualifier(editingContext, "TestVersionedObject", aQualifier, new NameComparator() );

        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     *  Fetches all active objects sorted by ActiveComparator 
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray orderedActiveObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedActiveObjects(editingContext, "TestVersionedObject", new NameComparator());

        /** ensure [Result_not_null] Result != null; **/
    }


    
    /**
     * Fetchs all objects sorted by ActiveComparator
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of the desired objects
     */
    static public NSArray orderedObjects(EOEditingContext editingContext)
    {
        /** require [editingContext_not_null] editingContext != null; **/
        
        return VersionableArchivableObject.orderedObjects(editingContext, "TestVersionedObject", VersionableArchivableObject.ActiveComparator );

        /** ensure [Result_not_null] Result != null; **/
    }


    /**
     * Comparator to sort objects by name, case insensitively.
     */
    static protected class NameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return (((TestVersionedObject)object1)).name().compareToIgnoreCase(((TestVersionedObject)object2).name());
        }
    }


    
}
