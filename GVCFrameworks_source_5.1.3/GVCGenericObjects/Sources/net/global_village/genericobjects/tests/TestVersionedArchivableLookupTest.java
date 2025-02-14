package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.GVCBoolean;
import net.global_village.genericobjects.VersionableArchivableLookup;

/**
 * Unit tests for TestVersionedArchivableLookup - which is an example subclass of VersionableArchivableLookup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 *
 */  
public class TestVersionedArchivableLookupTest extends TestArchivableLookupTest
{
    private VersionableArchivableLookup instanceOne; // could actually be TestVersionedArchivableLookup
    private VersionableArchivableLookup instanceTwo; // but it doesn't matter much
    private VersionableArchivableLookup newObject;


    
    /**
     * Designated constuctor.
     *
     * @param name The name of the test to be initialized
     */
    public TestVersionedArchivableLookupTest(String name)
    {
        super(name);
    }


    
    public void createObjects()
    {
        instanceTwo = new TestVersionedArchivableLookup();

        editingContext.insertObject(instanceTwo);
        
        instanceTwo.setName( this.globallyUniqueString() );

        editingContext.saveChanges();
        
        instanceOne = (VersionableArchivableLookup) instanceTwo.newVersion();
        
        editingContext.saveChanges(); 
    }


    public void destroyObjects()
    {
        // you cannot delete VersionableArchivableObjects
        
    }
    

    // overridden methods


    /**
     * Array of all objects of the Entity being tested  
     */
    protected NSArray allObjects()
    {
        return TestVersionedArchivableLookup.objectsWithQualifier(null, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }
    

    /**
     * Array of ordered objects for the Entity being tested  
     */
    protected NSArray orderedObjects()
    {
        return TestVersionedArchivableLookup.orderedObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }


    /**
     * Array of ordered active objects for the Entity being tested  
     */
    protected NSArray orderedActiveObjects()
    {
        return TestVersionedArchivableLookup.orderedActiveObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }

    
    /**
     * Array of inactive objects for the Entity being tested  
     */
    protected NSArray inactiveObjects()
    {
        NSArray args = new NSArray( GVCBoolean.falseBoolean() );
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat( "isActive = %@", args );
        return TestVersionedArchivableLookup.objectsWithQualifier(qualifier, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }


    
    public void testFetchCount()  
    {
        NSArray resultArray = this.allObjects();
        assertTrue( resultArray.count() >= 2 ); // instanceTwo and instanceOne and those from previous tests  
    }

    

    public void testObjectOrder()
    {
        super.testObjectOrder();
    }

    

    public void testActiveStatus() // needed to re-implement in subclass since it refers to private instances!!!
    {
        NSArray resultArray = this.orderedActiveObjects();

        assertTrue( resultArray.containsObject( instanceOne ) );
        assertTrue( ! resultArray.containsObject( instanceTwo ) );
        
        resultArray = this.inactiveObjects();

        assertTrue( ! resultArray.containsObject( instanceOne ) );
        assertTrue( resultArray.containsObject( instanceTwo ) );
    }


    
    public void testVersioning()
    {   
        // Can't alter an existing instance
        instanceOne.setName( "new name" );
        saveChangesShouldThrow( "altering existing instance" );
        editingContext.revert();

        // Can't make an old version the current version
        instanceTwo.setIsActive( GVCBoolean.trueBoolean() );
        saveChangesShouldThrow( "making old version the current version" );
        editingContext.revert();

        // Can't delete an instance
        editingContext.deleteObject( instanceOne );
        saveChangesShouldThrow( "deleting an instance" );
        editingContext.revert();

        // Can create a new version and save it
        newObject =  (VersionableArchivableLookup) instanceOne.newVersion();
        newObject.setName( "new name" );
        saveChangesShouldntThrow( "creating a new version" );
        
        // Can retire the current version
        newObject.setIsActive( GVCBoolean.falseBoolean() );  
        saveChangesShouldntThrow( "retiring the current version" );
    }

}
