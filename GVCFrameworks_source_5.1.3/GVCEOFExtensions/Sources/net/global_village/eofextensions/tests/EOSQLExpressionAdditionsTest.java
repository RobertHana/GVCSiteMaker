package net.global_village.eofextensions.tests;

import java.lang.reflect.*;
import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.FrontbasePlugIn.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;


/**
 * Test the EOObjectTest functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class EOSQLExpressionAdditionsTest extends GVCJUnitEOTestCase
{


    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
    public EOSQLExpressionAdditionsTest(String name)
    {
        super(name);
    }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();
    }



    /**
     * Test sqlStringForQualifier.  Tests specifically for generating SQL for a qualifier that references a different entity.
     * This test is certainly DB specific, but we make sure the database is correct, so you don't need to worry.
     */
    public void testSqlStringForQualifier()
    {
        EOSQLExpression expression = null;

        EOEntity anEntity = EOModelGroup.defaultGroup().entityNamed("RootObject");
        EODatabaseContext context = EODatabaseContext.registeredDatabaseContextForModel(anEntity.model(), editingContext);
        EOAdaptorChannel channel = context.availableChannel().adaptorChannel();
        EOAdaptor adaptor = channel.adaptorContext().adaptor();

        try
        {
            Constructor eoConstructor = adaptor.expressionClass().getConstructor(new Class[] {EOEntity.class});
            expression = (EOSQLExpression)eoConstructor.newInstance(new Object[] {anEntity});
            expression.setUseAliases(true);
        }
        catch(Exception e)
        {
            // This should never happen.
            throw new Error(e.getMessage());
        }

        // Make sure the test has a chance of succeeding
        if ( ! (expression instanceof FrontbaseExpression))
        {
            throw new Error("This test is not appropriate for a non-Frontbase database.");
        }

        RootObject aRootObject;
        OwnedObject anOwnedObject;

        aRootObject = new RootObject();
        editingContext().insertObject(aRootObject);

        anOwnedObject = new OwnedObject();
        editingContext().insertObject(anOwnedObject);

        aRootObject.addToOwnedObjects(anOwnedObject);
        aRootObject.setName("Some Name");
        anOwnedObject.setRootObject(aRootObject);
        anOwnedObject.setName("Another Name");

        editingContext().saveChanges();

        try
        {
            EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("ownedObjects = %@", new NSArray(anOwnedObject));
            String whereClause = EOSQLExpressionAdditions.sqlStringForQualifier(expression, qual);

            assertTrue(whereClause.startsWith("t0.\"OID\" = "));
        }
        finally
        {
            editingContext().deleteObject(aRootObject);
            editingContext().saveChanges();
        }
    }



    /**
     * Test timestampAsSQLString
     */
    public void testTimestampAsSQLString()
    {
        TimeZone timeZone = TimeZone.getDefault();
        NSTimestamp today = new NSTimestamp(2002, 4, 5, 18, 35, 22, timeZone);
        String todayAsSQLString = EOSQLExpressionAdditions.timestampAsFrontBaseSQLString(today);

        assertEquals("todayAsSQLString length is correct", 31, todayAsSQLString.length());
        assertEquals("todayAsSQLString is correctly converted", "TIMESTAMP '2002-04-05 18:35:22'", todayAsSQLString);

        // test date under DST
        NSTimestamp dayWithDST = new NSTimestamp(2002, 4, 8, 0, 0, 0, timeZone);
        String dayWithDSTAsSQLString = EOSQLExpressionAdditions.timestampAsFrontBaseSQLString(dayWithDST);

        assertEquals("dayWithDSTAsSQLString length is correct", 31, todayAsSQLString.length());
        assertEquals("dayWithDSTAsSQLString is correctly converted", "TIMESTAMP '2002-04-08 00:00:00'", dayWithDSTAsSQLString);
    }



    /**
     * Delete the inserted objects in the setup.
     */
    public void tearDown() throws java.lang.Exception
    {
        super.tearDown();
    }



}
