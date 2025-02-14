package net.global_village.virtualtables.tests;

import java.io.*;
import java.math.*;
import java.text.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.virtualtables.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class VirtualTableTest extends GVCJUnitEOTestCase
{
    protected static final String stringColumnName = "string column with weird chars !@#$%^&*(),.<>/?;':\"[]{}-=_+";

    VirtualTable testTable;

    public VirtualTableTest(String name)
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

        testTable = VirtualTable.createVirtualTable(editingContext(), "Test Table");
        testTable.newColumn("number column", ColumnType.NumberColumnType);
        testTable.newColumn("date column", ColumnType.TimestampColumnType);
        testTable.newColumn(stringColumnName, ColumnType.StringColumnType);
        testTable.newColumn("boolean column", ColumnType.BooleanColumnType);
        VirtualCalculatedColumn expression = (VirtualCalculatedColumn)testTable.newColumn("expression", ColumnType.CalculatedColumnType);
        expression.setExpression("numbercolumn + 13");
        editingContext().saveChanges();
    }



    /**
     * Test finding a table.
     */
    public void testFindTable()
    {
        Table foundTable = Table.tableWithTableID(editingContext(), testTable.tableID());
        assertTrue(foundTable != null);
    }



    /**
     * Test deleting a table.
     */
    public void testDeleteTable()
    {
        EOEditingContext ec = new NotifyingEditingContext();
        Table tableToDelete = (Table) EOUtilities.localInstanceOfObject(ec, testTable);

        ec.deleteObject(tableToDelete);

//        DebugOut.println(1, "Deleted  Objects " + ec.deletedObjects().valueForKey("entityName"));
//        DebugOut.println(1, "Updated  Objects " + ec.updatedObjects().valueForKey("entityName"));
//        DebugOut.println(1, "Inserted Objects " + ec.insertedObjects().valueForKey("entityName"));
//        DebugOut.println(1, "Processing recent changes...");
        //ec.processRecentChanges();
//        DebugOut.println(1, "Deleted  Objects " + ec.deletedObjects().valueForKey("entityName"));
//        DebugOut.println(1, "Updated  Objects " + ec.updatedObjects().valueForKey("entityName"));
//        DebugOut.println(1, "Inserted Objects " + ec.insertedObjects().valueForKey("entityName"));
        ec.saveChanges();

    }



    /**
     * Test creating a column.
     */
    public void testNewColumn()
    {
        // 7 because of the date created and modified columns created by default on all virtual tables
        assertEquals(7, testTable.columns().count());

        try
        {
            testTable.newColumn("number column", ColumnType.StringColumnType);
            editingContext().saveChanges();
            fail("Succeeded in creating a column with a duplicate name.");
        }
        catch (EOFValidationException e) { }

        try
        {
            testTable.newColumn("columnWithName", ColumnType.StringColumnType);
            editingContext().saveChanges();
            fail("Succeeded in creating a column with an invalid name (name exists as a method or field name on VirtualRow).");
        }
        catch (EOFValidationException e) { }

        editingContext().revert();
    }



    /**
     * Test deleting a column containing data.
     */
    public void testDeleteColumn()
    {
        // 7 because of the date created and modified columns created by default on all virtual tables
        assertEquals(7, testTable.columns().count());

        try
        {
            testTable.newColumn("junk column", ColumnType.StringColumnType);
            editingContext().saveChanges();

            VirtualRow row = testTable.newRow();
            row.takeValueForKey("string value", stringColumnName);
            row.takeValueForKey(Boolean.TRUE, "boolean column");
            row.takeValueForKey(new BigDecimal("100"), "number column");

            VirtualRow row2 = testTable.newRow();
            row2.takeValueForKey("string value2", stringColumnName);
            row2.takeValueForKey(new BigDecimal("101"), "number column");
            editingContext().saveChanges();

            Column columnToDelete = testTable.columnNamed("number column");
            testTable.removeObjectFromBothSidesOfRelationshipWithKey(columnToDelete, "columns");
            editingContext().deleteObject(columnToDelete);
            editingContext().saveChanges();
        }
        catch (Throwable e)
        {
            fail("Raised deleting column: " + e);
        }

    }



    /**
     * Test for existance of a column.
     */
    public void testHasColumnNamed()
    {
        assertTrue(testTable.hasColumnNamed("dateCreated"));
        assertTrue(testTable.hasColumnNamed("dateModified"));
        assertTrue(testTable.hasColumnNamed(stringColumnName));
        assertTrue(testTable.hasColumnNamed("numbercolumn"));
        assertTrue(testTable.hasColumnNamed("number column"));
    }



    /**
     * Test filters for user and system columns.
     */
    public void testColumnFilters()
    {
        assertTrue(testTable.userColumns().count() == 5);
        assertTrue(testTable.systemColumns().count() == 2);

        assertTrue(testTable.userColumns().containsObject(testTable.columnNamed(stringColumnName)));
        assertTrue(testTable.userColumns().containsObject(testTable.columnNamed("numbercolumn")));
        assertTrue(testTable.userColumns().containsObject(testTable.columnNamed("datecolumn")));
        assertTrue(testTable.systemColumns().containsObject(testTable.columnNamed("dateCreated")));
        assertTrue(testTable.systemColumns().containsObject(testTable.columnNamed("dateModified")));
    }



    /**
     * Test isValidColumnName.
     */
    public void testIsValidColumnName()
    {
        assertTrue(testTable.isValidColumnName("hello"));
        assertTrue( ! testTable.isValidColumnName("fieldNamed"));
        assertTrue( ! testTable.isValidColumnName(""));
        assertTrue( ! testTable.isValidColumnName("1st Choice"));
    }



    /**
     * Test setRows.
     */
    public void testSetRows()
    {
        VirtualRow row = testTable.newRow();
        row.takeValueForKey("string value", stringColumnName);
        row.takeValueForKey(new BigDecimal("100"), "number column");

        VirtualRow row2 = testTable.newRow();
        row2.takeValueForKey("string value2", stringColumnName);
        row2.takeValueForKey(new BigDecimal("101"), "number column");

        editingContext().saveChanges();

        assertEquals(2, testTable.rows().count());

        testTable.setRows(new NSMutableArray());
        assertEquals(0, testTable.rows().count());

        editingContext().revert();
        assertEquals(2, testTable.rows().count());
    }



    /**
     * Test importing and exporting data.
     */
    public void testImportExportTable() throws IOException, ParseException
    {
        VirtualTable lookupTable = null;
        try
        {
            // A lookup table
            lookupTable = VirtualTable.createVirtualTable(editingContext(), "Lookup Table");
            Column columnToLookup = lookupTable.newColumn("lookupString", ColumnType.StringColumnType);
            VirtualRow row = lookupTable.newRow();
            row.takeValueForKey("lookup value 1", "lookupString");
            row = lookupTable.newRow();
            row.takeValueForKey("lookup value 2", "lookupString");

            // Add a lookup column to the test table
            VirtualLookupColumn lookupColumn = (VirtualLookupColumn)testTable.newColumn("lookup column", ColumnType.LookupColumnType);
            lookupColumn.setLookupColumn(columnToLookup);

            // Have to save so that the new lookup column gets a permanent global ID
            editingContext().saveChanges();

            NSBundle bundle = NSBundle.bundleForClass(VirtualTableTest.class);
            String tablePath = bundle.resourcePathForLocalizedResourceNamed("TestImportData.txt", null);
            byte[] tableBytes = bundle.bytesForResourcePath(tablePath);

            NSArray columnsToImport = new NSArray(new Column[] {testTable.columnNamed(stringColumnName),
                                                                testTable.columnNamed("numbercolumn"),
                                                                testTable.columnNamed("date column"),
                                                                testTable.columnNamed("lookup column"),
                                                                testTable.columnNamed("boolean column"),
                                                                testTable.columnNamed("expression")});
            NSDictionary formatters = new NSDictionary(new NSTimestampFormatter("%Y-%m-%d"), "date column");

            InputStream stream = new ByteArrayInputStream(tableBytes);
            testTable.importTable(stream, null, columnsToImport, formatters, "\t", true, true);
            editingContext().saveChanges();
            assertEquals(3, testTable.rows().count());

            stream = new ByteArrayInputStream(tableBytes);
            testTable.importTable(stream, null, columnsToImport, formatters, "\t", true, true);
            editingContext().saveChanges();
            assertEquals(3, testTable.rows().count());

            stream = new ByteArrayInputStream(tableBytes);
            testTable.importTable(stream, null, columnsToImport, formatters, "\t", true, false);
            editingContext().saveChanges();
            assertEquals(6, testTable.rows().count());


            // Test for invalid type
            /*columnsToImport = new NSArray(new Column[] {testTable.columnNamed("numbercolumn"), testTable.columnNamed(stringColumnName)});
            stream = new ByteArrayInputStream(tableBytes);
            try
            {
                testTable.importTable(stream, null, columnsToImport, new NSDictionary(), "\t", true, true);
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            } */


            // Now export some of that data... first, munge some data so that it has cr/lf to test the munging facility
            row = (VirtualRow)testTable.rows().objectAtIndex(0);
            row.takeValueForKey("test\nof\rEOL\r\ncharacters", stringColumnName);
            NSArray columnsToExport = new NSArray(new Column[] {testTable.columnNamed(stringColumnName),
                                                                testTable.columnNamed("numbercolumn"),
                                                                testTable.columnNamed("date column"),
                                                                testTable.columnNamed("lookup column"),
                                                                testTable.columnNamed("boolean column"),
                                                                testTable.columnNamed("expression")});
            // Test without header
            File exportTableData = File.createTempFile("ExportedTableData", ".txt");
            FileOutputStream fos = new FileOutputStream(exportTableData);
            testTable.exportTable(null, null, fos, null, columnsToExport, "\t", false);
            fos.close();
            exportTableData.deleteOnExit();

            FileInputStream fileInputStream = new FileInputStream(exportTableData);
            NSData contentsOfFile = new NSData(fileInputStream, fileInputStream.available());
            fileInputStream.close();
            String exportTableString = new String(contentsOfFile.bytes());

            String cannonicalTestTablePath = bundle.resourcePathForLocalizedResourceNamed("ExportTestDataWithoutHeader.txt", null);
            byte[] cannonicalTableBytes = bundle.bytesForResourcePath(cannonicalTestTablePath);
            String cannonicalTestString = new String(cannonicalTableBytes);

            assertEquals(cannonicalTestString, exportTableString);

            // Test with header
            ByteArrayOutputStream exportedTableDataWithHeader = new ByteArrayOutputStream();
            testTable.exportTable(null, null, exportedTableDataWithHeader, null, columnsToExport, ",", true);
            exportedTableDataWithHeader.close();

            cannonicalTestTablePath = bundle.resourcePathForLocalizedResourceNamed("ExportTestDataWithHeader.txt", null);
            cannonicalTableBytes = bundle.bytesForResourcePath(cannonicalTestTablePath);
            cannonicalTestString = new String(cannonicalTableBytes);

            assertEquals(cannonicalTestString, exportedTableDataWithHeader.toString());
        }
        finally
        {
            // Cleanup after this test
            editingContext().deleteObject(lookupTable);
        }
    }



    /**
     * Test importing quoted data.
     */
    public void testImportQuotedTable() throws IOException, ParseException
    {
        VirtualTable importTable = null;
        try
        {
            NSMutableArray columnsToImport = new NSMutableArray();
            importTable = VirtualTable.createVirtualTable(editingContext(), "Import Table");
            columnsToImport.addObject(importTable.newColumn("Case Name", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Date of Decision", ColumnType.TimestampColumnType));
            columnsToImport.addObject(importTable.newColumn("Court", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Country", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Reported Citation", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Electronic Citation", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Web Citation", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Other Locator", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Holdings re subjective element", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Ratio or Obiter Dictum", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Relevant Quotations", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Comments or Observations", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("References for Follow Up", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Ideas", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Author or Authors", ColumnType.StringColumnType));
            columnsToImport.addObject(importTable.newColumn("Published", ColumnType.BooleanColumnType));
            assertEquals(18, importTable.columns().count());

            editingContext().saveChanges();

            NSBundle bundle = NSBundle.bundleForClass(VirtualTableTest.class);
            String tablePath = bundle.resourcePathForLocalizedResourceNamed("TestImportQuotingData.txt", null);
            byte[] tableBytes = bundle.bytesForResourcePath(tablePath);

            NSDictionary formatters = new NSDictionary(new NSTimestampFormatter("%Y-%m-%d %H:%M:%S"), "Date of Decision");

            InputStream stream = new ByteArrayInputStream(tableBytes);
            importTable.importTable(stream, null, columnsToImport, formatters, ",", true, true);
            editingContext().saveChanges();
            assertEquals(2, importTable.rows().count());
            assertEquals(18, ((VirtualRow)importTable.rows().objectAtIndex(0)).fields().count());
            assertEquals(18, ((VirtualRow)importTable.rows().objectAtIndex(1)).fields().count());
        }
        finally
        {
            // Cleanup after this test
            editingContext().deleteObject(importTable);
        }
    }



    /**
     * Test objectWithPrimaryKey
     */
    public void testObjectWithPrimaryKey()
    {
        VirtualRow row = testTable.newRow();
        row.takeValueForKey("string value", stringColumnName);
        row.takeValueForKey(new BigDecimal("100"), "number column");

        VirtualRow row2 = testTable.newRow();
        row2.takeValueForKey("string value2", stringColumnName);
        row2.takeValueForKey(new BigDecimal("101"), "number column");

        editingContext().saveChanges();

        try
        {
            testTable.objectWithPrimaryKey(row.virtualRowID());
        }
        catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
        {
            fail("Failed to return object: " + e.getMessage());
        }

        try
        {
            testTable.objectWithPrimaryKey(new Integer(-1));
            fail("Returned dummy object for PK -1");
        }
        catch (com.webobjects.eoaccess.EOObjectNotAvailableException e)
        {
        }
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        if (editingContext().registeredObjects().containsObject(testTable))
        {
            editingContext().deleteObject(testTable);
        }

        editingContext().saveChanges();

        super.tearDown();
    }



}
