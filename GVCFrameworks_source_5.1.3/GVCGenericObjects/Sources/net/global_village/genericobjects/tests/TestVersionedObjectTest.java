package net.global_village.genericobjects.tests;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.GVCBoolean;
import net.global_village.gvcjunit.GVCJUnitEOTestCase;
// import net.global_village.genericobjects.VersionableArchivableObject;

/**
 * Tests TestVersionedObject - which is an example subclass of VersionableArchivableObject which uses versioning
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 *
 */  
public class TestVersionedObjectTest extends GVCJUnitEOTestCase
{
    private TestVersionedObject instanceOne; 
    private TestVersionedObject instanceTwo;
    

    
    /**
     * Designated constructor.
     *
     * @param name The name of the test to be initialized
     *
     */
    public TestVersionedObjectTest (String name)
    {
        super(name);
    }


    
    public void testVersioning()
    {
        TestVersionedObject newObject;
    
        instanceTwo = new TestVersionedObject();

        editingContext.insertObject( instanceTwo );
         
        instanceTwo.setName( "One" );

        editingContext.saveChanges();

        instanceOne = (TestVersionedObject) instanceTwo.newVersion();
        saveChangesShouldntThrow( "object created using newVersion()" );
        
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
        newObject =  (TestVersionedObject) instanceOne.newVersion();
        newObject.setName( "new name" );
        saveChangesShouldntThrow( "creating a new version" );
        
        // Can retire the current version
        newObject.setIsActive( GVCBoolean.falseBoolean() );  
        saveChangesShouldntThrow( "retiring the current version" );
        
        // Can save again without affecting old versions
        saveChangesShouldntThrow( "saving again without affecting old versions" );
    }


    
    public void testFetching()
    {
        TestVersionedObject instanceOneA;
        TestVersionedObject instanceTwoA;
        TestVersionedObject instanceThreeA;
        TestVersionedObject instanceThree;

        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat( "name != %@", new NSArray( "Beta" ) );
        NSArray fetchResults;

        instanceOne = new TestVersionedObject(); 
        editingContext.insertObject( instanceOne );
        instanceOne.setName( "Alpha" );

        instanceTwo = new TestVersionedObject();
        editingContext.insertObject( instanceTwo );
        instanceTwo.setName( "Beta" );

        instanceThree = new TestVersionedObject();
        editingContext.insertObject( instanceThree );
        instanceThree.setName( "Gamma" );

        editingContext.saveChanges();

        instanceOneA = (TestVersionedObject) instanceOne.newVersion();
        instanceTwoA = (TestVersionedObject) instanceTwo.newVersion();
        instanceThreeA = (TestVersionedObject) instanceThree.newVersion();
        editingContext.saveChanges();

        fetchResults = TestVersionedObject.activeObjectsWithQualifier( qualifier, editingContext );

        assertTrue( fetchResults.containsObject( instanceOneA ) );
        assertTrue( ! fetchResults.containsObject( instanceTwoA ) );
        assertTrue( fetchResults.containsObject( instanceThreeA ) );

        assertTrue( ! fetchResults.containsObject( instanceOne ) );
        assertTrue( ! fetchResults.containsObject( instanceTwo ) );
        assertTrue( ! fetchResults.containsObject( instanceThree ) );

        fetchResults = TestVersionedObject.orderedActiveObjectsWithQualifier( qualifier, editingContext );

        assertTrue( fetchResults.containsObject( instanceOneA ) );
        assertTrue( ! fetchResults.containsObject( instanceTwoA ) );
        assertTrue( fetchResults.containsObject( instanceThreeA ) );

        assertTrue( ! fetchResults.containsObject( instanceOne ) );
        assertTrue( ! fetchResults.containsObject( instanceTwo ) );
        assertTrue( ! fetchResults.containsObject( instanceThree ) );

        assertTrue( fetchResults.indexOfObject( instanceOneA ) != NSArray.NotFound );   // paranoia
        assertTrue( fetchResults.indexOfObject( instanceThreeA ) != NSArray.NotFound ); // paranoia

        assertTrue( fetchResults.indexOfObject( instanceOneA ) < fetchResults.indexOfObject( instanceThreeA ) );
    }


}
