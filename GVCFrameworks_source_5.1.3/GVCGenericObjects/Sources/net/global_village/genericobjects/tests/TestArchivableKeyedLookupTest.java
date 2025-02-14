package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.GVCBoolean;
import net.global_village.genericobjects.VersionableArchivableKeyedLookup;



/**
 *  Unit tests for TestArchivableKeyedLookup - which is an example subclass of VersionableArchivableKeyedLookup
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 *
 */  
public class TestArchivableKeyedLookupTest extends TestArchivableLookupTest
{
    private VersionableArchivableKeyedLookup instanceOne; // could actually be TestArchivableLookup
    private VersionableArchivableKeyedLookup instanceTwo; // but it doesn't matter much
    private VersionableArchivableKeyedLookup instanceThree;

    

    /**
     * Designated constuctor.
     *
     * @param name The name of the test to be initialized
     */
    public TestArchivableKeyedLookupTest(String name)
    {
        super(name);
    }



    public void createObjects()
    {
        instanceOne   = new TestArchivableKeyedLookup();
        instanceTwo   = new TestArchivableKeyedLookup();
        instanceThree = new TestArchivableKeyedLookup();

        editingContext.insertObject( instanceOne );
        editingContext.insertObject( instanceTwo );
        editingContext.insertObject( instanceThree );
        
   		  instanceOne.setName( this.globallyUniqueString() );
        instanceOne.setKey("One");
        
        instanceTwo.setName( this.globallyUniqueString() );
        instanceTwo.setKey("One"); // this one will be inactivated
        
        instanceThree.setName( this.globallyUniqueString() );
        instanceThree.setKey("Three");

        saveChangesShouldntThrow("after creating Objects"); // keys are not forced to be unique even if multiple are active
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


    // overridden methods


    /**
     * Array of all objects of the Entity being tested  
     */
    protected NSArray allObjects()
    {
        return TestArchivableKeyedLookup.objectsWithQualifier(null, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }
    

    /**
     * Array of ordered objects for the Entity being tested  
     */
    protected NSArray orderedObjects()
    {
        return TestArchivableKeyedLookup.orderedObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }


    /**
     * Array of ordered active objects for the Entity being tested  
     */
    protected NSArray orderedActiveObjects()
    {
        return TestArchivableKeyedLookup.orderedActiveObjects( editingContext );

        /** ensure [Result_not_null] Result != null; **/
    }

    
    /**
     * Array of inactive objects for the Entity being tested  
     */
    protected NSArray inactiveObjects()
    {
        NSArray args = new NSArray( GVCBoolean.falseBoolean() );
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat( "isActive = %@", args );
        return TestArchivableKeyedLookup.objectsWithQualifier(qualifier, editingContext);

        /** ensure [Result_not_null] Result != null; **/
    }


    
    public void testFetchCount()
    {
        super.testFetchCount();
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
        assertTrue( resultArray.containsObject( instanceThree ) );
        
        resultArray = this.inactiveObjects();

        assertTrue( ! resultArray.containsObject( instanceOne ) );
        assertTrue( resultArray.containsObject( instanceTwo ) );
        assertTrue( ! resultArray.containsObject( instanceThree ) );
    }


    
    public void testKeyedRetrieval()
    {
        assertTrue( TestArchivableKeyedLookup.objectForKey( instanceOne.key(), editingContext ) == instanceOne );
        assertTrue( TestArchivableKeyedLookup.objectForKey( instanceThree.key(), editingContext ) == instanceThree );
    }

}
