package net.global_village.virtualtables.tests;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.virtualtables.*;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ColumnTest extends GVCJUnitEOTestCase
{
    protected static final String lookupSourceColumnName = "string column with weird chars !@#$%^&*(),.<>/?;':\"[]{}-=_+";

    VirtualTable testTable;
    Column numberColumn;


    public ColumnTest(String name)
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
        numberColumn = testTable.newColumn("number column", ColumnType.NumberColumnType);
        testTable.newColumn(lookupSourceColumnName, ColumnType.StringColumnType);

        editingContext().saveChanges();
    }



    /**
     * Test the column name normalization feature.
     */
    public void testNormalizeStringForColumnNames()
    {
        String testString = "hello this is a test !@#$%^&*()_+-={}[]:\";'<>,./?";
        String normalizedString = Column.normalizeStringForColumnNames(testString);
        assertEquals("hellothisisatest_", normalizedString);

        assertEquals("number column", numberColumn.name());
        assertEquals("numbercolumn", numberColumn.normalizedName());

        // Test that a normalized string is the same when re-normalized...
        assertEquals(normalizedString, Column.normalizeStringForColumnNames(normalizedString));
    }



    /**
     * Test changing a column's name.
     */
    public void testSetName()
    {
        assertTrue(testTable.hasColumnNamed(lookupSourceColumnName));
        Column lookupSourceColumn = testTable.columnNamed(lookupSourceColumnName);
        lookupSourceColumn.setName("this is a test!");
        assertTrue(testTable.hasColumnNamed("this is a test!"));
    }


    /**
     * Test validating a column's name.
     */
    public void testValidateName()
    {
        Column duplicateNumberColumn = testTable.newColumn("number column", ColumnType.NumberColumnType);
        try
        {
            duplicateNumberColumn.validateName("number column");
            fail("Allowed duplicate column name");
        }
        catch (EOFValidationException e) {}

        try
        {
            duplicateNumberColumn.validateName("1st choice");
            fail("Allowed column name that did not start with alphabetic.");
        }
        catch (EOFValidationException e) {}

        try
        {
            duplicateNumberColumn.validateName("");
            fail("Allowed empty string as column name");
        }
        catch (EOFValidationException e) {}
    }


    /**
     * Test the lookup column functionality.
     */
    public void testLookupColumns()
    {
        // Create the table containing the values which will be looked up
        VirtualTable lookupTable = VirtualTable.createVirtualTable(editingContext(), "Lookup_Table");
        VirtualColumn lookupSourceColumn = (VirtualColumn)lookupTable.newColumn("lookupSourceColumn", ColumnType.StringColumnType);
        editingContext().saveChanges();

        // Create a column using this lookup table.
        VirtualLookupColumn lookupColumn = (VirtualLookupColumn)testTable.newColumn("lookupColumn", ColumnType.LookupColumnType);
        lookupColumn.setLookupColumn(lookupSourceColumn);
        //lookupSourceColumn.addToReferringColumns(lookupColumn);// This should be handled by the class, no?
        editingContext().saveChanges();

        // Create a value to be looked up
        VirtualRow rowWithLookupValue = lookupTable.newRow();
        rowWithLookupValue.takeValueForKey("a value", lookupSourceColumn.name());
        VirtualField lookupField = rowWithLookupValue.fieldNamed(lookupSourceColumn.name());
        editingContext().saveChanges();

        // Create a row using looking a value up in the lookup table
        VirtualRow rowLookingUpValue = testTable.newRow();
        rowLookingUpValue.takeValueForKey(lookupField, lookupColumn.name());
        editingContext().saveChanges();

        // Tests Start Here
        assertTrue("lookupTable.hasReferringTables()", lookupTable.hasReferringTables());
        assertTrue("rowWithLookupValue.hasReferringRows()", rowWithLookupValue.hasReferringRows());

        editingContext().deleteObject(rowWithLookupValue);
        saveChangesShouldThrow("Deleted a row containing a field used in a lookup.");
        editingContext().revert();

        testTable.removeFromRows(rowLookingUpValue);
        saveChangesShouldntThrow("Failed to delete row using lookup value.");

        assertTrue("lookupTable.hasReferringTables()", lookupTable.hasReferringTables());
        assertTrue("! rowWithLookupValue.hasReferringRows()", ! rowWithLookupValue.hasReferringRows());

        lookupTable.removeFromRows(rowWithLookupValue);
        saveChangesShouldntThrow("Failed to delete field no longer used in a lookup.");

        assertTrue("lookupTable.hasReferringTables()", lookupTable.hasReferringTables());

        editingContext().deleteObject(lookupTable);
        saveChangesShouldThrow("Deleted a table with a column used in a lookup.");
        editingContext().revert();

        editingContext().deleteObject(lookupSourceColumn);
        saveChangesShouldThrow("Deleted a column used in a lookup.");
        editingContext().revert();

        // Cleanup after this test
        editingContext().deleteObject(lookupColumn);
        saveChangesShouldntThrow("Failed to delete lookupColumn.");
        assertTrue("! lookupTable.hasReferringTables()", ! lookupTable.hasReferringTables());

        editingContext().deleteObject(lookupTable);
        editingContext().saveChanges();
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the tear down may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(testTable);
        editingContext().saveChanges();

        super.tearDown();
    }



}
