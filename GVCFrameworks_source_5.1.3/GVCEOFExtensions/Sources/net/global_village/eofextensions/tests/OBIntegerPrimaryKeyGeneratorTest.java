package net.global_village.eofextensions.tests;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the OpenBase primary key generator.
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class OBIntegerPrimaryKeyGeneratorTest extends GVCJUnitEOTestCase
{
    public static final int PKCacheSize = 10;
    public static final int NumberOfObjectsToInsert = 100;  // Don't go below PKCacheSize * 2 or the tests won't work

    public EODatabaseContext dbContext;
    public Object oldDBContextDelegate;


    public OBIntegerPrimaryKeyGeneratorTest(String name)
    {
        super(name);
    }



    /**
     * Sets up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        // Set a new PK generator delegate...
        EOModel model = EOModelGroup.defaultGroup().modelNamed("GVCTestEOModelBase");
        OBIntegerPrimaryKeyGenerator pkGenerator = new OBIntegerPrimaryKeyGenerator(PKCacheSize);
        pkGenerator.cachePKsForEntity(EOUtilities.entityNamed(editingContext(), "EntityWithDecimalPK"));
        
        dbContext = EODatabaseContext.registeredDatabaseContextForModel(model, EOObjectStoreCoordinator.defaultCoordinator());
        dbContext.lock();
        try
        {
            oldDBContextDelegate = dbContext.delegate();
            dbContext.setDelegate(pkGenerator);
        }
        finally
        {
            dbContext.unlock();
        }
    }



    /**
     * Test the PK generation for sequential numbers.
     */
    public void testPKGeneratorGeneratesSequentialNumbers()
    {
        for (int i = 0; i < NumberOfObjectsToInsert; i++)
        {
            EntityWithDecimalPK testEO = new EntityWithDecimalPK();
            editingContext().insertObject(testEO);
            testEO.setDataAttribute(new NSData("test " + i, "ASCII"));
        }
        editingContext().saveChanges();

        EntityWithDecimalPK lastEO = null;
        NSArray sortedEOs = EOSortOrdering.sortedArrayUsingKeyOrderArray(EOUtilities.objectsForEntityNamed(editingContext(), "EntityWithDecimalPK"), new NSArray(new EOSortOrdering("theDecimalPK", EOSortOrdering.CompareAscending)));
        Enumeration sortedEOsEnumerator = sortedEOs.objectEnumerator();
        while (sortedEOsEnumerator.hasMoreElements())
        {
            EntityWithDecimalPK eo = (EntityWithDecimalPK)sortedEOsEnumerator.nextElement();
            if (lastEO != null)
            {
                assertEquals(lastEO.theDecimalPK().intValue() + 1, eo.theDecimalPK().intValue());
            }
            lastEO = eo;
        }
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().revert();
        NSArray eos = EOUtilities.objectsForEntityNamed(editingContext(), "EntityWithDecimalPK");
        Enumeration eoEnumerator = eos.objectEnumerator();
        while (eoEnumerator.hasMoreElements())
        {
            EntityWithDecimalPK eo = (EntityWithDecimalPK)eoEnumerator.nextElement();
            editingContext().deleteObject(eo);
        }
        editingContext().saveChanges();

        dbContext.lock();
        try
        {
            dbContext.setDelegate(oldDBContextDelegate);
        }
        finally
        {
            dbContext.unlock();
        }

        super.tearDown();
    }



}
