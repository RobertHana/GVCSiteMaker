package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.GVCBoolean;
import net.global_village.genericobjects.VersionableArchivableLookup;
import net.global_village.gvcjunit.GVCJUnitEOTestCase;

/**
 * Unit tests for TestArchivableLookup - which is an example subclass of VersionableArchivableLookup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 *
 */  
public class TestArchivableLookupTest extends GVCJUnitEOTestCase
{
    private VersionableArchivableLookup instanceOne; // not used by subclass, so I made them private
    private VersionableArchivableLookup instanceTwo; // these could actually be TestArchivableLookup
    private VersionableArchivableLookup instanceThree; // but it doesn't matter much

    
    
    /**
     * Designated constuctor.
     *
     * @param name The name of the test to be initialized
     */
    public TestArchivableLookupTest(String name)
    {
        super(name);
    }


    
    public void tearDown() throws java.lang.Exception
    {
        this.destroyObjects();
        super.tearDown();
    }


    
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
        this.createObjects();
    }
    

    
    public void createObjects()
    {
        instanceOne   = new TestArchivableLookup();
        instanceTwo   = new TestArchivableLookup();
        instanceThree = new TestArchivableLookup();

        editingContext.insertObject( instanceOne );
        editingContext.insertObject( instanceTwo );
        editingContext.insertObject( instanceThree );
        
   		instanceOne.setName( this.globallyUniqueString() );
        instanceTwo.setName( this.globallyUniqueString() );
        instanceThree.setName( this.globallyUniqueString() );

        saveChangesShouldntThrow("after creating Objects");
        instanceTwo.setIsActive( GVCBoolean.falseBoolean() );
        saveChangesShouldntThrow("after inactivating instanceTwo");        
    }

    
    public void destroyObjects()
    {
        editingContext.deleteObject( instanceOne );
        editingContext.deleteObject( instanceTwo );
        editingContext.deleteObject( instanceThree );

        saveChangesShouldntThrow("destroying Objects");
    }

    

    // methods which need to be overriden by subclasses


    /**
     * Array of all objects of the Entity being tested  
     */
    protected NSArray allObjects()
    {
        return TestArchivableLookup.objectsWithQualifier(null, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }
    

    /**
     * Array of ordered objects for the Entity being tested  
     */
    protected NSArray orderedObjects()
    {
        return TestArchivableLookup.orderedObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }


    /**
     * Array of ordered active objects for the Entity being tested  
     */
    protected NSArray orderedActiveObjects()
    {
        return TestArchivableLookup.orderedActiveObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }

    
    /**
     * Array of inactive objects for the Entity being tested  
     */
    protected NSArray inactiveObjects()
    {
        NSArray args = new NSArray( GVCBoolean.falseBoolean() );
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat( "isActive = %@", args );
        return TestArchivableLookup.objectsWithQualifier(qualifier, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }


    
    public void testFetchCount()
    {
        NSArray resultArray = this.allObjects();
        assertTrue( resultArray.count() >= 3 );      
    }


    
    public void testObjectOrder()
    {
        NSArray resultArray = this.orderedObjects();

        for (int i = resultArray.count() - 1; i > 0; i-- )
        {
            VersionableArchivableLookup firstObject = (VersionableArchivableLookup) resultArray.objectAtIndex( i - 1 );
            VersionableArchivableLookup secondObject = (VersionableArchivableLookup) resultArray.objectAtIndex( i );
            String firstName = firstObject.name();
            String secondName = secondObject.name();

            assertTrue( firstName.compareTo(secondName) <= 0 );   // not in descending order 
        }       
    }


    
    public void testActiveStatus() // need to re-implement in subclass since it refers to private instances!!!
    {
        NSArray resultArray = this.orderedActiveObjects();

        assertTrue( resultArray.containsObject( instanceOne ) );
        assertTrue( ! resultArray.containsObject( instanceTwo ) );
        assertTrue( resultArray.containsObject( instanceThree ) );
        
        resultArray = this.inactiveObjects();

        assertTrue( ! resultArray.containsObject( instanceOne ) );
        assertTrue( resultArray.containsObject( instanceTwo ) );
        assertTrue( ! resultArray.containsObject( instanceThree ) );
    }

}
