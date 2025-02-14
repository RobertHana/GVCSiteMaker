package net.global_village.eofextensions.tests;

import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the EODatabaseContextAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EODatabaseContextAdditionsTests extends GVCJUnitEOTestCase
{

    protected SimpleRelationshipTestEntity relationshipTestEntity;
    protected ToManyEntity toManyEntity;
    protected ToOneEntity toOneEntity;


    /**
     * Designated constructor.
     *
     * @param name the method name of the test to be initialized
     */
    public EODatabaseContextAdditionsTests(String name)
    {
        super(name);
    }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        relationshipTestEntity = new SimpleRelationshipTestEntity();
        toManyEntity = new ToManyEntity();
        toOneEntity = new ToOneEntity();

        editingContext().insertObject(relationshipTestEntity);
        editingContext().insertObject(toManyEntity);
        editingContext().insertObject(toOneEntity);

        relationshipTestEntity.setName("relationshipTestEntity");
        toManyEntity.setName("toManyEntity");
        toOneEntity.setName("toOneEntity");

        relationshipTestEntity.addObjectToBothSidesOfRelationshipWithKey(toManyEntity, "mandatoryToMany");
        relationshipTestEntity.addObjectToBothSidesOfRelationshipWithKey(toOneEntity, "mandatoryToOne");

        editingContext().saveChanges();
    }



    /**
     * Tests preloadRelationship
     */
    public void testPreloadRelationship()
    {
        NSArray objects = new NSArray(relationshipTestEntity);
        try {

            EODatabaseContextAdditions.preloadRelationship(objects, "mandatoryToMany");
        }
        catch (RuntimeException e) {
            fail("Failed prefetching to many");
        }

        try {
            EODatabaseContextAdditions.preloadRelationship(objects, "mandatoryToOne");
        }
        catch (RuntimeException e) {
            fail("Failed prefetching to one");
        }

        try
        {
            EODatabaseContextAdditions.preloadRelationship(NSArray.EmptyArray, "mandatoryToMany");
        }
        catch (RuntimeException e) {
            fail("Failed prefetching empty array");
        }
    }



    /**
     * Delete the inserted objects in the setup.
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(relationshipTestEntity);
        editingContext().deleteObject(toManyEntity);
        editingContext().deleteObject(toOneEntity);

        super.tearDown();
    }
}
