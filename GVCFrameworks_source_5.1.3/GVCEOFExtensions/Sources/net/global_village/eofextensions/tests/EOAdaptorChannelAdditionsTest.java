package net.global_village.eofextensions.tests;

import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.gvcjunit.*;
import net.global_village.testeomodelbase.*;


/**
 * Test the EOAdaptorChannelAdditions functionality.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class EOAdaptorChannelAdditionsTest extends GVCJUnitEOTestCase
{
    protected static final String entityOneName = "One";
    protected static final String entityTwoName = "Two";
    protected static final String entityThreeName = "Three";
    protected static final String entityFourName = "Four";

    protected SimpleTestEntity entityOne;
    protected SimpleTestEntity entityTwo;
    protected SimpleTestEntity entityThree;
    protected SimpleTestEntity entityFour;


    /**
     * Designated constructor.
     *
     * @param name The method name of the test to be initialized
     */
     public EOAdaptorChannelAdditionsTest(String name)
     {
         super(name);
     }



    /**
     * Creates and inserts SimpleTestEntity in the editingContext() with aName as name
     *
     * @param aName the name of the entity to be created
     * @return an SimpleTestEntity with aName as name
     */
    public SimpleTestEntity createSimpleTestEntityWithName(String aName)
    {
        SimpleTestEntity anEntity = new SimpleTestEntity();
        editingContext().insertObject(anEntity);
        anEntity.setName(aName);

        return anEntity;
    }



    /**
     * Common test code. Create and insert needed objects for the tests
     */
    public void setUp() throws java.lang.Exception
    {
        super.setUp();

        entityOne = createSimpleTestEntityWithName(entityOneName);
        entityTwo = createSimpleTestEntityWithName(entityTwoName);
        entityThree = createSimpleTestEntityWithName(entityThreeName);
        entityFour = createSimpleTestEntityWithName(entityFourName);

        editingContext().saveChanges();
    }

   

    /**
     * test resultOfEvaluatingSQLExpression()
     */
    public void testResultOfEvaluatingSQLExpression()
    {
        EOSQLExpressionFactory expressionFactory = new EOSQLExpressionFactory(EOAdaptor.adaptorWithModel(EOModelGroup.defaultGroup().modelNamed("TestModel1")));

        EOSQLExpression singleValueExpression = expressionFactory.expressionForString("select count(*) from simple_test_entity");

        EOSQLExpression singleRowExpression = expressionFactory.expressionForString("select * from simple_test_entity where the_id = (select max(the_id) from simple_test_entity)");

        EOSQLExpression multiRowExpression = expressionFactory.expressionForString("select * from simple_test_entity");

        EOSQLExpression emptyResultSetExpression = expressionFactory.expressionForString("select * from simple_test_entity where the_id <> the_id");

        EOSQLExpression noResultSetExpression = expressionFactory.expressionForString("update simple_test_entity set the_name = 'name'");

        EOEntity anEntity = EOModelGroup.defaultGroup().entityNamed("SimpleTestEntity");
        EODatabaseContext context = EODatabaseContext.registeredDatabaseContextForModel(anEntity.model(), editingContext());
        EOAdaptorChannel channel = (context.availableChannel()).adaptorChannel();

        assertTrue((EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, singleValueExpression)).getClass().getSuperclass().equals(Number.class));

        assertTrue((EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, singleRowExpression)).getClass().getSuperclass().equals(NSArray.class));

        assertTrue((EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, multiRowExpression)).getClass().getSuperclass().equals(NSArray.class));

        assertTrue(EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, emptyResultSetExpression) == null);

        assertTrue(EOAdaptorChannelAdditions.resultOfEvaluatingSQLExpression(channel, noResultSetExpression) == null);
    }



    /**
     * Delete the inserted objects in the setup.
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().refaultAllObjects();
        NSArray allObjects = EOUtilities.objectsForEntityNamed(editingContext(), "SimpleTestEntity");

        Enumeration objectEnumerator = allObjects.objectEnumerator();
        while(objectEnumerator.hasMoreElements())
        {
            SimpleTestEntity anObject = (SimpleTestEntity) objectEnumerator.nextElement();
            editingContext().deleteObject(anObject);
        }

        editingContext().saveChanges();

        super.tearDown();
    }



}
