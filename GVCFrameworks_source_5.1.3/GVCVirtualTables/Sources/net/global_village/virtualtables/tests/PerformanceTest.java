package net.global_village.virtualtables.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.jdbcadaptor.JDBCContext;

import net.global_village.gvcjunit.GVCJUnitEOTestCase;
import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.ColumnType;
import net.global_village.virtualtables.VirtualLookupColumn;
import net.global_village.virtualtables.VirtualTable;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class PerformanceTest extends GVCJUnitEOTestCase
{
    public VirtualTable testTable;


    public PerformanceTest(String name)
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

        testTable = new VirtualTable();
        editingContext().insertObject(testTable);
        testTable.setName("testTable");

        testTable.newColumn("numberColumn1", ColumnType.NumberColumnType);
        testTable.newColumn("numberColumn2", ColumnType.NumberColumnType);
        testTable.newColumn("numberColumn3", ColumnType.NumberColumnType);
        testTable.newColumn("numberColumn4", ColumnType.NumberColumnType);
        testTable.newColumn("stringColumn1", ColumnType.StringColumnType);
        testTable.newColumn("stringColumn2", ColumnType.StringColumnType);
        testTable.newColumn("stringColumn3", ColumnType.StringColumnType);
        testTable.newColumn("stringColumn4", ColumnType.StringColumnType);
        Column stringColumn5 = testTable.newColumn("stringColumn5", ColumnType.StringColumnType);
        VirtualLookupColumn column = (VirtualLookupColumn)testTable.newColumn("lookupColumn", ColumnType.LookupColumnType);
        column.setLookupColumn(stringColumn5);

        editingContext().saveChanges();
    }



    /**
     * Test batch insert performance.
     */
    public void testBatchInsertPerformance() throws SQLException
    {
        System.out.println("\nStart batch insert performance test: " + new NSTimestamp());

        EOModel vtModel = EOModelGroup.defaultGroup().modelNamed("VirtualTables");
        EODatabaseContext dbContext = EODatabaseContext.registeredDatabaseContextForModel(vtModel, editingContext());
        JDBCContext adContext = (JDBCContext)dbContext.adaptorContext();
        java.sql.Connection con = adContext.connection();

        for (int i = 0; i < 400; i++)
        {
            // My testing indicates that it is much faster (by a factor of more than 5) to do rows one at a time
            Statement statement = con.createStatement();

            Object rowID = testTable.batchNewRow(null, statement);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("stringColumn1"), "stringColumn1 " + i);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("stringColumn2"), "stringColumn2 " + i);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("stringColumn3"), "stringColumn3 " + i);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("stringColumn4"), "stringColumn4 " + i);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("stringColumn5"), "stringColumn5 " + i);
            testTable.batchNewField(statement, rowID, testTable.columnNamed("numberColumn1"), new BigDecimal(1 + i));
            testTable.batchNewField(statement, rowID, testTable.columnNamed("numberColumn2"), new BigDecimal(2 + i));
            testTable.batchNewField(statement, rowID, testTable.columnNamed("numberColumn3"), new BigDecimal(3 + i));
            testTable.batchNewField(statement, rowID, testTable.columnNamed("numberColumn4"), new BigDecimal(4 + i));

            statement.executeBatch();
        }
        con.commit();

        System.out.println("End batch insert insert performance test: " + new NSTimestamp());

        System.out.println("\nStart save performance test: " + new NSTimestamp());
        editingContext().saveChanges();
        System.out.println("End save performance test: " + new NSTimestamp());

        // The rows are in the database, but EOF doesn't know about them. Make EOF refault the table so that its relationships get loaded
        editingContext().invalidateObjectsWithGlobalIDs(new NSArray(editingContext().globalIDForObject(testTable)));
    }



    /**
     * Test performance.
     */
    public void testPerformance() throws IOException, ParseException
    {
        System.out.println("\nStart import performance test: " + new NSTimestamp());
        NSBundle bundle = NSBundle.bundleForClass(PerformanceTest.class);
        String tablePath = bundle.resourcePathForLocalizedResourceNamed("TestImportDataPerformance.txt", null);
        byte[] tableBytes = bundle.bytesForResourcePath(tablePath);
        NSArray columnsToImport = new NSArray(new Column[] {
            testTable.columnNamed("numberColumn1"),
            testTable.columnNamed("numberColumn2"),
            testTable.columnNamed("numberColumn3"),
            testTable.columnNamed("numberColumn4"),
            testTable.columnNamed("stringColumn1"),
            testTable.columnNamed("stringColumn2"),
            testTable.columnNamed("stringColumn3"),
            testTable.columnNamed("stringColumn4"),
            testTable.columnNamed("stringColumn5")});
        InputStream stream = new ByteArrayInputStream(tableBytes);
        testTable.importTable(stream, null, columnsToImport, new NSDictionary(), "\t", true, true);
        System.out.println("End import performance test: " + new NSTimestamp());

        System.out.println("\nStart save performance test: " + new NSTimestamp());
        editingContext().saveChanges();
        System.out.println("End save performance test: " + new NSTimestamp());

        System.out.println("\nStart qualify number performance test: " + new NSTimestamp());
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("numberColumn1 < 100", null);
        assertEquals(99, testTable.objectsWithQualifier(qualifier).count());
        System.out.println("End qualify number performance test: " + new NSTimestamp());

        System.out.println("\nStart like qualify string performance test: " + new NSTimestamp());
        qualifier = EOQualifier.qualifierWithQualifierFormat("stringColumn1 like \"string*\"", null);
        assertEquals(100, testTable.objectsWithQualifier(qualifier).count());
        System.out.println("End like qualify string performance test: " + new NSTimestamp());

        System.out.println("\nStart CI like qualify string performance test: " + new NSTimestamp());
        qualifier = EOQualifier.qualifierWithQualifierFormat("stringColumn3 caseInsensitiveLike \"STRING*\"", null);
        assertEquals(100, testTable.objectsWithQualifier(qualifier).count());
        System.out.println("End CI like qualify string performance test: " + new NSTimestamp());

        System.out.println("\nStart equals qualify string performance test: " + new NSTimestamp());
        qualifier = EOQualifier.qualifierWithQualifierFormat("stringColumn4 = \"string4.0\"", null);
        assertEquals(1, testTable.objectsWithQualifier(qualifier).count());
        System.out.println("End equals qualify string performance test: " + new NSTimestamp());


        System.out.println("\nClearing the cache");
        editingContext().invalidateAllObjects();

        System.out.println("\nStart equals qualify string performance test: " + new NSTimestamp());
        qualifier = EOQualifier.qualifierWithQualifierFormat("stringColumn4 = \"string4\"", null);
        assertEquals(99, testTable.objectsWithQualifier(qualifier).count());
        System.out.println("End equals qualify string performance test: " + new NSTimestamp());
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        System.out.println("\nTearing down performance test: " + new NSTimestamp());

        testTable.deleteRows();
        editingContext().deleteObject(testTable);
        editingContext().saveChanges();

        super.tearDown();

        System.out.println("Done tearing down performance test: " + new NSTimestamp());
    }



}
