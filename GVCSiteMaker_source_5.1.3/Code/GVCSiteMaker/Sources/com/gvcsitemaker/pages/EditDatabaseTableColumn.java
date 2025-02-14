// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page allows a new column (a.k.a. field in GVC.SiteMaker) to be added
 * to a VirtualTable and also provides a list of the user created columns in
 * that table so they can be revised or deleted.
 */
public class EditDatabaseTableColumn extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage
{
    protected Table databaseTable;

    protected NSArray lookupSourceColumns;
    public VirtualColumn aColumn;
    public VirtualColumn selectedLookupSourceColumn;

    protected EOEditingContext editingContext;
    protected String fieldTypeError;

    protected ColumnType selectedFieldType;
    protected String selectedFieldTypeName;

    protected ColumnType textColumnType;
    protected ColumnType booleanColumnType;
    protected ColumnType dateColumnType;
    protected ColumnType numberColumnType;
    protected ColumnType fileColumnType;
    protected ColumnType lookupColumnType;
    protected ColumnType calculatedColumnType;


    /**
     * Designated constructor.
     */
    public EditDatabaseTableColumn(WOContext context)
    {
        super(context);
        setEditingContext(website().editingContext());
        editingContext().revert(); // Just in case...

        // One ivar per column type shown on this page.
        setTextColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.StringColumnType));
        setBooleanColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.BooleanColumnType));
        setDateColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.TimestampColumnType));
        setNumberColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.NumberColumnType));
        setLookupColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.LookupColumnType));
        setCalculatedColumnType(ColumnType.columnTypeWithName(editingContext(), ColumnType.CalculatedColumnType));
        setFileColumnType(ColumnType.columnTypeWithName(editingContext(), VirtualSiteFileField.ColumnTypeName));
        resetFields();
    }



    /**
     * Overriden to reset lookupSourceColumns.
     */
    public void awake()
    {
        super.awake();
        lookupSourceColumns = null;
    }



    /**
     * Returns <code>true></code> if there are any validation errors which would prevent the creation of the column.
     *
     * @return <code>true></code> if there are any validation errors which would prevent the creation of the column.
     */
    public boolean hasErrors()
    {
        return (fieldTypeError() != null);
    }


    /**
     * Clears any existing error messages.
     */
    public void clearErrors()
    {
        setFieldTypeError(null);
    }


    /**
     * Checks the data input fields to see if they are valid and sets error message and hasErrors() if they are not.
     */
    public void validateFields()
    {
        clearErrors();
        validateFieldType();
    }



    /**
     * Checks fieldType to see if it is valid and set fieldTypeError if it is not.
     */
    public void validateFieldType()
    {
        /** require [field_type_selected] selectedFieldType() != null;   **/
        if (selectedFieldType().name().equals(ColumnType.LookupColumnType) && selectedLookupSourceColumn == null)
        {
            setFieldTypeError("Please select a field from another Database Table to use as the source of this Value List, or choose a different data type.");
        }
    }



    /**
     * Returns the fields in Database Tables in this Website which can be used as the source of a Lookup column.  Fields in the table being edited are not returned.
     *
     * @return the fields in Database Tables in this Website which can be used as the source of a Lookup column
     */
    /** @TypeInfo VirtualColumn */
    public NSArray lookupSourceColumns()
    {
        if (lookupSourceColumns == null)
        {
            lookupSourceColumns = new NSMutableArray();
            Enumeration tableEnumerator = website().orderedDatabaseTables().objectEnumerator();
            while (tableEnumerator.hasMoreElements())
            {
                Table table = (Table) tableEnumerator.nextElement();
                if (table instanceof SMVirtualTable && !table.equals(databaseTable()))
                {
                    ((NSMutableArray) lookupSourceColumns).addObjectsFromArray(((SMVirtualTable) table).lookupSourceColumns());
                }
            }
        }

        return lookupSourceColumns;
    }



    /**
     * Sets the data input fields in preparation for adding a new field.
     */
    public void resetFields()
    {
        // Set default selected type
        setSelectedFieldType(textColumnType());
        setSelectedFieldTypeName(null);
        lookupSourceColumns = null;
        selectedLookupSourceColumn = null;
    }



    //****************************  Generic Accessors Below Here  ****************************\\

    public Table databaseTable()
    {
        return databaseTable;
    }


    public void setDatabaseTable(Table newTable)
    {
        databaseTable = (Table)EOUtilities.localInstanceOfObject(editingContext(), newTable);
    }


    public EOEditingContext editingContext()
    {
        return editingContext;
    }


    public void setEditingContext(EOEditingContext newEditingContext)
    {
        editingContext = newEditingContext;
    }


    public String fieldTypeError()
    {
        return fieldTypeError;
    }


    public void setFieldTypeError(String newFieldTypeError)
    {
        fieldTypeError = newFieldTypeError;
    }


    public ColumnType selectedFieldType()
    {
        if ((selectedFieldTypeName != null) && ((selectedFieldType == null) || ( ! selectedFieldType.name().equals(selectedFieldTypeName()))))
        {
            selectedFieldType = ColumnType.columnTypeWithName(editingContext(), selectedFieldTypeName());
        }
        return selectedFieldType;
    }


    public void setSelectedFieldType(ColumnType newSelectedFieldType)
    {
        selectedFieldType = newSelectedFieldType;
    }


    public String selectedFieldTypeName()
    {
        if ((selectedFieldTypeName == null) && (selectedFieldType != null))
        {
            selectedFieldTypeName = selectedFieldType.name();
        }
        return selectedFieldTypeName;
    }


    public void setSelectedFieldTypeName(String newSelectedFieldTypeName)
    {
        selectedFieldTypeName = newSelectedFieldTypeName;
    }


    public ColumnType textColumnType()
    {
        return textColumnType;
    }


    public void setTextColumnType(ColumnType newTextColumnType)
    {
        textColumnType = newTextColumnType;
    }


    public ColumnType booleanColumnType()
    {
        return booleanColumnType;
    }


    public void setBooleanColumnType(ColumnType newBooleanColumnType)
    {
        booleanColumnType = newBooleanColumnType;
    }


    public ColumnType dateColumnType()
    {
        return dateColumnType;
    }


    public void setDateColumnType(ColumnType newDateColumnType)
    {
        dateColumnType = newDateColumnType;
    }


    public ColumnType numberColumnType()
    {
        return numberColumnType;
    }


    public void setNumberColumnType(ColumnType newNumberColumnType)
    {
        numberColumnType = newNumberColumnType;
    }


    public ColumnType fileColumnType()
    {
        return fileColumnType;
    }


    public void setFileColumnType(ColumnType newFileColumnType)
    {
        fileColumnType = newFileColumnType;
    }


    public ColumnType calculatedColumnType()
    {
        return calculatedColumnType;
    }


    public void setCalculatedColumnType(ColumnType newCalculatedColumnType)
    {
        calculatedColumnType = newCalculatedColumnType;
    }


    public ColumnType lookupColumnType()
    {
        return lookupColumnType;
    }


    public void setLookupColumnType(ColumnType newLookupColumnType)
    {
        lookupColumnType = newLookupColumnType;
    }


}
