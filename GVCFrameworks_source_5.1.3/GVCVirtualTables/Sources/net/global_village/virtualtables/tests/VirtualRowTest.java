package net.global_village.virtualtables.tests;

import java.math.BigDecimal;
import java.util.Enumeration;

import com.webobjects.eocontrol.EOQualifier;

import net.global_village.gvcjunit.GVCJUnitEOTestCase;
import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.ColumnType;
import net.global_village.virtualtables.VirtualColumn;
import net.global_village.virtualtables.VirtualField;
import net.global_village.virtualtables.VirtualLookupColumn;
import net.global_village.virtualtables.VirtualRow;
import net.global_village.virtualtables.VirtualTable;


/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class VirtualRowTest extends GVCJUnitEOTestCase
{
    protected static final String numberColumnName = "number column with weird chars !@#$%^&*(),.<>/?;':\"[]{}-=_+";

    VirtualTable testTable;
    VirtualRow row, row2;
    VirtualColumn stringColumn;
    VirtualColumn booleanColumn;
    VirtualLookupColumn lookupColumn;

    public VirtualRowTest(String name)
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
        testTable.newColumn(numberColumnName, ColumnType.NumberColumnType);
        stringColumn = (VirtualColumn)testTable.newColumn("stringColumn", ColumnType.StringColumnType);
        booleanColumn = (VirtualColumn)testTable.newColumn("booleanColumn", ColumnType.BooleanColumnType);
        lookupColumn = (VirtualLookupColumn)testTable.newColumn("lookupColumn", ColumnType.LookupColumnType);
        lookupColumn.setLookupColumn(stringColumn);

        row = testTable.newRow();
        row.takeValueForKey("string value", "stringColumn");
        row.takeValueForKey(Boolean.TRUE, "booleanColumn");
        row.takeValueForKey(new BigDecimal("100"), numberColumnName);

        row2 = testTable.newRow();
        row2.takeValueForKey("string value2", "stringColumn");
        row2.takeValueForKey(Boolean.FALSE, "booleanColumn");
        row2.takeValueForKey(new BigDecimal("101"), numberColumnName);

        editingContext().saveChanges();
    }



    /**
     * Test creating a row.
     */
    public void testNewRow()
    {
        assertEquals(2, testTable.rows().count());
    }



    /**
     * Test duplicating a row.
     */
    public void testDuplicateRow()
    {
        VirtualRow duplicateRow = row.duplicate();

        Enumeration fieldEnumerator = row.fields().objectEnumerator();
        while (fieldEnumerator.hasMoreElements())
        {
            VirtualField field = (VirtualField) fieldEnumerator.nextElement();
            Object originalValue = field.value();
            Object duplicateValue = duplicateRow.valueForFieldNamed(field.name());

            if ( ! field.column().isSystemColumn().booleanValue())
            {
                // This only works as no VirtualField subclass in this framework creates a new, unequal value
                assertEquals(originalValue, duplicateValue);
            }
            else
            {
                assertTrue("orginalValue != duplicateValue", ! (originalValue == duplicateValue));
            }
        }

        assertEquals(3, testTable.rows().count());
    }



    /**
     * Test for existance of a column.
     */
    public void testHasColumnNamed()
    {
        assertTrue(row.hasColumnNamed("stringColumn"));
        assertTrue(row.hasColumnNamed(numberColumnName));

        testTable.newColumn("CapitalColumn", ColumnType.StringColumnType);
        assertTrue(row.hasColumnNamed("CapitalColumn"));
    }



    /**
     * Test adding data to a field of a row.
     */
    public void testSetDataInColumn()
    {
        assertEquals("string value", row.valueForKey("stringColumn"));
        assertEquals(new BigDecimal("100"), row.valueForKey(numberColumnName));

        try
        {
            row2.takeValueForKey(row.fieldNamed("stringColumn"), "lookupColumn");
            editingContext().saveChanges();
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException e)
        {
            fail("Set a lookup value to the same column that was specified - shouldn't raise a validation exception.");
        }

        try
        {
            row2.takeValueForKey(row.fieldNamed(numberColumnName), "lookupColumn");
            editingContext().saveChanges();
            fail("Set a lookup value to a different column than was specified.");
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException e) { }
    }



    /**
     * Test fetching objects.
     */
    public void testObjectsWithQualifier()
    {
        String normalizedNumberColumnName = Column.normalizeStringForColumnNames(numberColumnName);
        EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat(normalizedNumberColumnName + " > 100", null);
        assertEquals(1, testTable.objectsWithQualifier(qualifier).count());

        qualifier = EOQualifier.qualifierWithQualifierFormat(normalizedNumberColumnName + " < 100", null);
        assertEquals(0, testTable.objectsWithQualifier(qualifier).count());

        qualifier = EOQualifier.qualifierWithQualifierFormat("stringColumn like \"string value*\"", null);
        assertEquals(2, testTable.objectsWithQualifier(qualifier).count());
    }



    /**
     * Test removing a column from the table.  All associated fields should be deleted as well.
     */
    public void testRemoveColumn()
    {
        editingContext().deleteObject(lookupColumn);
        editingContext().saveChanges();

        // There are 3 more columns than we create manually: date create and date modified
        assertEquals(5, testTable.columns().count());
        assertEquals(5, row.fields().count());
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(testTable);
        editingContext().saveChanges();

        super.tearDown();
    }



}
