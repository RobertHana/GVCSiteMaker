package net.global_village.virtualtables.tests;

import java.math.*;

import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.gvcjunit.*;
import net.global_village.virtualtables.*;


/**
 * Tests for calculated fields.
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class CalculatedColumnTest extends GVCJUnitEOTestCase
{

    VirtualTable testTable;
    VirtualTable lookupTable;
    VirtualRow row, row2, row3, row4;
    VirtualCalculatedColumn expression1, expression2, expression3;


    public CalculatedColumnTest(String name)
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

        // Create a table containing a value that can be looked up in an expression
        lookupTable = VirtualTable.createVirtualTable(editingContext(), "Lookup_Table");
        VirtualColumn lookupSourceColumn = (VirtualColumn)lookupTable.newColumn("lookupSourceColumn", ColumnType.NumberColumnType);
        VirtualColumn lookupRelationalColumn = (VirtualColumn)lookupTable.newColumn("nameForLookupValue", ColumnType.StringColumnType);
        VirtualColumn lookupExpressionColumn = (VirtualColumn)lookupTable.newColumn("expression", ColumnType.CalculatedColumnType);
        ((VirtualCalculatedColumn)lookupExpressionColumn).setExpression("lookupSourceColumn * 10");
        editingContext().saveChanges();

        // Create a value to be looked up
        VirtualRow rowWithLookupValue = lookupTable.newRow();
        rowWithLookupValue.takeValueForKey(new BigDecimal(7), lookupSourceColumn.name());
        rowWithLookupValue.takeValueForKey("Big Number Seven!", lookupRelationalColumn.name());
        VirtualField lookupField = rowWithLookupValue.fieldNamed(lookupSourceColumn.name());
        editingContext().saveChanges();


        // Create the table that will have calculated columns
        testTable = VirtualTable.createVirtualTable(editingContext(), "Test Table");
        testTable.newColumn("Some Number", ColumnType.NumberColumnType);
        testTable.newColumn("Other Number", ColumnType.NumberColumnType);
        testTable.newColumn("string Column", ColumnType.StringColumnType);
        testTable.newColumn("boolean Column", ColumnType.BooleanColumnType);
        testTable.newColumn("date", ColumnType.TimestampColumnType);

        // Here are the calculated columns that will be tested
        expression1 = (VirtualCalculatedColumn)testTable.newColumn("expression", ColumnType.CalculatedColumnType);
        expression1.setExpression("SomeNumber");
        expression2 = (VirtualCalculatedColumn)testTable.newColumn("second calculation", ColumnType.CalculatedColumnType);
        expression2.setExpression("expression + 27");
        expression3 = (VirtualCalculatedColumn)testTable.newColumn("final calculation", ColumnType.CalculatedColumnType);
        expression3.setExpression("lookupColumn + 17");

        // Create a lookup column into lookupTable
        VirtualLookupColumn lookupColumn = (VirtualLookupColumn)testTable.newColumn("lookupColumn", ColumnType.LookupColumnType);
        lookupColumn.setLookupColumn(lookupSourceColumn);

        // Add rows of data that will be used to text the expressions
        row = testTable.newRow();
        row.takeValueForKey("string value", "string Column");
        row.takeValueForKey(Boolean.TRUE, "boolean Column");
        row.takeValueForKey(new BigDecimal("100"), "Some Number");

        row2 = testTable.newRow();
        row2.takeValueForKey("string value2", "stringColumn");
        row2.takeValueForKey(Boolean.FALSE, "booleanColumn");
        row2.takeValueForKey(new BigDecimal("101"), "Some Number");

        // Create a row using looking a value up in the lookup table
        row3 = testTable.newRow();
        row3.takeValueForKey("string value2", "stringColumn");
        row3.takeValueForKey(Boolean.FALSE, "booleanColumn");
        row3.takeValueForKey(new BigDecimal("101"), "Some Number");
        row3.takeValueForKey(new NSTimestamp(), "date");
        row3.takeValueForKey(lookupField, lookupColumn.name());

        editingContext().saveChanges();
    }


    /**
     * Test basic validation.
     */
    public void testBasicValidation()
    {
        assertEquals(expression1.expression(), expression1.validateExpression(expression1.expression()));
        assertEquals(expression2.expression(), expression2.validateExpression(expression2.expression()));
        assertEquals(expression3.expression(), expression3.validateExpression(expression3.expression()));
    }



    /**
     * Test basic calculations.
     */
    public void testBasicCalculations()
    {
        // Simple expression
        assertEquals(new BigDecimal("100"), row.valueForFieldNamed("expression"));

        // Expression referring to another expression
        assertEquals(new BigDecimal("128"), row2.valueForFieldNamed("secondcalculation"));

        // Expression referring to a lookup column expression
        assertEquals(new BigDecimal("24"), row3.valueForFieldNamed("final calculation"));
    }



    /**
     * Test calculations involving constants.
     */
    public void testConstants()
    {
        try
        {
            expression1.setExpression("1 + 1");
            assertEquals(new Integer("2"), row.valueForFieldNamed("expression"));
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test calculations involving extensions to NSTimestamp.
     */
    public void testNSTimestampExtensions()
    {
        try
        {
            expression1.setExpression("date.addYMDHMS(1, 0, 2, 3, 0, 1)");
            assertEquals(new NSTimestamp().timestampByAddingGregorianUnits(1, 0 , 2, 3, 0, 1),
                         row3.valueForFieldNamed("expression"));
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test calculations involving extensions to String.
     */
    public void testStringExtensions()
    {
        try
        {
            expression1.setExpression("stringColumn.max(8)");
            assertEquals("string v",
                         row3.valueForFieldNamed("expression"));

            expression1.setExpression("stringColumn.elipsize(8)");
            assertEquals("string \u2026",
                         row3.valueForFieldNamed("expression"));
        }
        finally
        {
            editingContext().revert();
        }
    }




    /**
     * Test calculations involving extensions to String.
     */
    public void testOtherStringStuff()
    {
        try
        {
            // There was a bug in which replaceAll failed if java.lang.String was not in the white list.
            expression1.setExpression("stringColumn.replaceAll('Foo', 'Candy')");
            row3.takeValueForKey("Foo Bar", "string Column");

            assertEquals("Candy Bar", row3.valueForFieldNamed("expression"));
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test that relational expressions are validated and calculated correctly.
     */
    public void testRelationalExtensions()
    {
        try
        {
            expression1.setExpression("lookupColumn" + VirtualRowPropertyAccessor.RelationalNavigationMarker + "nameForLookupValue");
            expression1.validateExpression(expression1.expression());
            assertEquals(row3.valueForFieldNamed("expression"), "Big Number Seven!");
        }
        catch (Exception e)
        {
            NSLog.out.appendln(e);
            fail("failed to allow relational expression");
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test that relational expressions are validated correctly.
     */
    public void testRelationalExtensionsAndCyclicExpression()
    {
        // There was a bug where a cycle was detected when the name of the calculated field appeared
        // in a relational expression in its formula.  This happens when a field of that same name
        // (expression in this case below) was in another (lookup) table and referred to in the expression
        // This was fixed by using Column objects, not names, to track cycles.
        try
        {
            expression1.setExpression("lookupColumn" + VirtualRowPropertyAccessor.RelationalNavigationMarker + "expression");
            expression1.validateExpression(expression1.expression());
            assertEquals(new BigDecimal(70), row3.valueForFieldNamed("expression"));
        }
        catch (EOFValidationException e)
        {
            NSLog.out.appendln(e);
            fail("failed to allow relational expression (" + e.getLocalizedMessage() + ")");
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test expressionType().
     */
    public void testExpressionType()
    {

        try
        {
            expression1.setExpression("1 + 1");
            assertEquals(ColumnType.NumberColumnType, expression1.expressionType());

            assertEquals(ColumnType.NumberColumnType, expression2.expressionType());

            expression1.setExpression("1 > 1");
            assertEquals(ColumnType.BooleanColumnType, expression1.expressionType());

            expression1.setExpression("date");
            assertEquals(ColumnType.TimestampColumnType, expression1.expressionType());

            expression1.setExpression("\"one\" + \"two\"");
            assertEquals(ColumnType.StringColumnType, expression1.expressionType());

        }
        finally
        {
            editingContext().revert();
        }


    }



    /**
     * Test that calculations can't call methods on objects.
     */
    public void testMethodsNotAccessible()
    {
        try
        {
            expression1.setExpression("#this.duplicate()");
            expression1.validateExpression(expression1.expression());
            fail("allowed method accesss on table");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("There is an error in the expression, it can't be understood.", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("allowed method accesss on row");
            }
            catch (RuntimeException e2)
            {
                assertEquals("There is an error in the expression, it can't be understood.", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test that calculations can't call methods on objects.
     */
    public void testStaticMethodsNotAccessible()
    {
        try
        {
            expression1.setExpression("@java.lang.System@exit(1)");
            expression1.validateExpression(expression1.expression());
            fail("allowed method accesss on table");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("There is an error in the expression, it can't be understood.", e.getLocalizedMessage());

            try
            {
                expression1.setExpression("@java.lang.System@exit(1)");
                row2.valueForFieldNamed("expression");
                fail("allowed method accesss on row");
            }
            catch (RuntimeException e2)
            {
                assertEquals("There is an error in the expression, it can't be understood.", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test reference to a column that does not exist.
     */
    public void testEmptyExpression()
    {

        expression1.setExpression("1 + 1");
        assertEquals(new Integer("2"), row.valueForFieldNamed("expression"));
        expression1.setExpression("");

        try
        {
            expression1.validateExpression(expression1.expression());
            fail("validated blank expression");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("The expression is required.", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("allowed missing expression");
            }
            catch (RuntimeException e2)
            {
                assertEquals("The expression is required.", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test reference to a column that does not exist.
     */
    public void testInvalidFieldReference()
    {
        expression1.setExpression("SomeNumber2 + 27");

        try
        {
            expression1.validateExpression(expression1.expression());
            fail("validated invalid field reference");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("The value can't be calculated because it refers to a non-existent field named SomeNumber2", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("failed to detect invalid field reference");
            }
            catch (RuntimeException e2)
            {
                assertEquals("The value can't be calculated because it refers to a non-existent field named SomeNumber2", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test circular field reference.
     */
    public void testCircularFieldReference()
    {
        expression1.setExpression("expression");

        try
        {
            expression1.validateExpression(expression1.expression());
            fail("validated invalid field reference");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("The value can't be calculated because it contains a circular reference to field expression", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("failed to detect circular field reference");
            }
            catch (RuntimeException e2)
            {
                assertEquals("The value can't be calculated because it contains a circular reference to field expression", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }

        expression1.setExpression("secondcalculation * 2");
        expression2.setExpression("finalcalculation / 30");
        expression3.setExpression("secondcalculation * 2");
        try
        {
            expression1.validateExpression(expression1.expression());
            fail("failed to detect circular field reference");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("The value can't be calculated because it contains a circular reference to field secondcalculation", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("failed to detect circular field reference");
            }
            catch (RuntimeException e2)
            {
                assertEquals("The value can't be calculated because it contains a circular reference to field secondcalculation", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test syntactically incorrect expression.
     */
    public void testIncorrectSyntax()
    {
        expression1.setExpression("SomeNumber * / 27");

        try
        {
            expression1.validateExpression(expression1.expression());
            fail("validated invalid syntax error");
        }
        catch (NSValidation.ValidationException e)
        {
            assertEquals("There is an error in the expression (Encountered \"/\" at line 1, column 14).", e.getLocalizedMessage());

            try
            {
                row2.valueForFieldNamed("expression");
                fail("failedt to detect syntax error");
            }
            catch (RuntimeException e2)
            {
                assertEquals("There is an error in the expression (Encountered \"/\" at line 1, column 14).", e2.getLocalizedMessage());
            }
        }
        finally
        {
            editingContext().revert();
        }
    }




    /**
     * Test null value in expression.
     */
    public void testNullValue()
    {

        try
        {
            row2.takeValueForKey(null, "Some Number");
            assertEquals(null, row2.valueForFieldNamed("expression"));

            expression1.setExpression("SomeNumber * SomeNumber");
            assertEquals(new BigInteger("0"), row2.valueForFieldNamed("expression"));

            expression1.setExpression("SomeNumber * 21");
            assertEquals(new Double("0.0"), row2.valueForFieldNamed("expression"));
        }
        catch (RuntimeException e2)
        {
            fail(e2.getLocalizedMessage());
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test null value in expression.
     */
    public void testDivideByZero()
    {
        try
        {
            row2.takeValueForKey(new BigDecimal(0), "Some Number");
            row2.takeValueForKey(new BigDecimal(1), "Other Number");
            expression1.setExpression("OtherNumber / SomeNumber");

            try
            {
                row2.valueForFieldNamed("expression");
                fail("failed to detect aritmetic error");
            }
            catch (RuntimeException e2)
            {
                assertEquals("There is an arithmetic error in the expression (divide by zero?)", e2.getLocalizedMessage());
            }

            try
            {
                expression1.setExpression("SomeNumber / OtherNumber");
                row2.valueForFieldNamed("expression");
            }
            catch (RuntimeException e2)
            {
                fail("detected 0 / 1 as aritmetic error");
            }
        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Test additions to BigDecimal from java.lang.math.
     */
    public void testBigDecimalAdditions()
    {

        try
        {
            row2.takeValueForKey(new BigDecimal(17), "Some Number");
            expression1.setExpression("SomeNumber.log10()");
            assertEquals(new BigDecimal("1.2304489213782739"), row2.valueForFieldNamed("expression"));

            row2.takeValueForKey(new BigDecimal(4), "Some Number");
            expression1.setExpression("SomeNumber.pow(2)");
            NSLog.out.appendln("Class " + row2.valueForFieldNamed("expression").getClass().getCanonicalName());
            assertEquals(new BigDecimal("16"), row2.valueForFieldNamed("expression"));

        }
        finally
        {
            editingContext().revert();
        }
    }



    /**
     * Cleans up the fixtures.
     * @exception Exception an exception that the setup may throw
     */
    public void tearDown() throws java.lang.Exception
    {
        editingContext().deleteObject(testTable);
        editingContext().saveChanges();
        editingContext().deleteObject(lookupTable);
        editingContext().saveChanges();

        super.tearDown();
    }



}
