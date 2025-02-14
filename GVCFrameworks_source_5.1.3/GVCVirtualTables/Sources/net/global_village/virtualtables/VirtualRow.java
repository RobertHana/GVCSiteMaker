package net.global_village.virtualtables;

import java.util.*;

import net.global_village.foundation.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * A virtual row contains the data for a given row of a virtual table. It holds the fields (which in turn hold the data).
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class VirtualRow extends _VirtualRow
{

    private Map calculationContext;



    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        // Really would like to create the values for the dateCreated and dateModified columns here... but, sadly, we don't have access to the table yet, so it is done in Table.newRow()
    }



    /**
     * Returns a duplicate of this row.  Only user fields are duplicated, system fields are not so that the duplicate row gets an accurate date created  etc.
     *
     * @return a duplicate of this row, except for system columns.
     */
    public VirtualRow duplicate()
    {
        /** require [has_table] table() != null;  **/

        VirtualRow newRow = table().newRow();
        Enumeration fieldEnumerator = fields().objectEnumerator();
        while (fieldEnumerator.hasMoreElements())
        {
            VirtualField field = (VirtualField) fieldEnumerator.nextElement();

            // Don't copy system fields, the new row should get its own.
            if ( ! field.column().isSystemColumn().booleanValue())
            {
                newRow.takeValueForKey(field.valueCopy(), field.name());
            }
        }

        return newRow;

        /** ensure
         [valid_result] Result != null;
        [user_fields_copied] (forall i : {0 .. fields().count() - 1} #
                              ((((VirtualField)fields().objectAtIndex(i)).column().isSystemColumn().booleanValue()) ||
                               (Result.valueForKey(((VirtualField)fields().objectAtIndex(i)).name()) != null)));
        **/
    }



    /**
     * Does this row's table contain a column with the given name?
     *
     * @param columnName the column name to look for
     * @return <code>true</code> if the row's table contains a column with the given name, <code>false</code> otherwise
     */
    public boolean hasColumnNamed(String columnName)
    {
        /** require [valid_param] columnName != null; **/
        return table().hasColumnNamed(columnName);
    }



    /**
     * Returns the column with the given name.
     *
     * @param columnName the column name to look for
     * @return the column with the given name
     */
    public Column columnNamed(String columnName)
    {
        /** require
        [valid_param] columnName != null;
        [has_column] hasColumnNamed(columnName); **/
        return table().columnNamed(columnName);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the field whose column is named the given name, or null if one doesn't exist.  Note that a row might not have a field for a given column (this indicates "null" for that field).
     *
     * @param fieldName the field name to look for
     * @return the given field
     */
    public VirtualField findFieldNamed(String fieldName)
    {
        /** require [valid_param] fieldName != null; **/

        VirtualField targetField = null;
        String targetName = Column.normalizeStringForColumnNames(fieldName);
        Enumeration fieldEnumeration = fields().objectEnumerator();
        while (fieldEnumeration.hasMoreElements() && (targetField == null))
        {
            VirtualField aField = (VirtualField) fieldEnumeration.nextElement();
            if (aField.column().normalizedName().equals(targetName))
            {
                targetField = aField;
            }
        }

        return targetField;
    }



    /**
     * Does this row contain a field with the given name?
     *
     * @param fieldName the field name to look for
     * @return <code>true</code> if the row contains a field with the given name, <code>false</code> otherwise
     */
    public boolean hasFieldNamed(String fieldName)
    {
        /** require
        [valid_param] fieldName != null;
        [has_column] hasColumnNamed(fieldName); **/
        return findFieldNamed(fieldName) != null;
    }



    /**
     * Returns the field for the given name.
     *
     * @param fieldName the field name to look for
     * @return the given field
     */
    public VirtualField fieldNamed(String fieldName)
    {
        /** require
        [valid_param] fieldName != null;
        [has_column] hasColumnNamed(fieldName);
        [has_field] hasFieldNamed(fieldName); **/
        return findFieldNamed(fieldName);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the data for the given field name.
     *
     * @param fieldName the field name to look for
     * @return the data in the given field, or null if the field doesn't exist or is null
     */
    public Object valueForFieldNamed(String fieldName)
    {
        /** require  [valid_param] fieldName != null;
                     [has_column] hasColumnNamed(fieldName); **/

        VirtualColumn column = (VirtualColumn)columnNamed(fieldName);
        boolean didCreateOGNLContext =  calculationContext() == null && column.type().name().equals(ColumnType.CalculatedColumnType);
        if (didCreateOGNLContext)
        {
            setCalculationContext(((VirtualCalculatedColumn)column).newCalculationContext());
        }

        try
        {
            // special handling for RecordID, since no associated column
            if ("RecordID".equals(fieldName))
            {
                if (Table.isUsingIntegerPKs())
                {
                    return virtualRowID().toString();
                }
                else
                {
                    Object downcaster = virtualRowID();
                    return ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
                }
            }

            return column.valueInRow(this);
        }
        finally
        {
            if (didCreateOGNLContext)
            {
                setCalculationContext(null);
            }
        }
    }



    /**
     * Sets the data for the given field name, overwriting the previous value.
     *
     * @param newValue the new value for this field
     * @param fieldName the field name to look for
     */
    public void takeValueForFieldNamed(Object newValue, String fieldName)
    {
        /** require
        [valid_param] fieldName != null;
        [has_column] hasColumnNamed(fieldName); **/

        VirtualField field = null;
        if (hasFieldNamed(fieldName))
        {
            field = fieldNamed(fieldName);
        }
        else if (newValue != null)
        {
            field = ((VirtualColumn)columnNamed(fieldName)).newField(this);
        }

        if (newValue != null)
        {
            String typedKeyPath = StringAdditions.downcaseFirstLetter(field.column().type().name()) + "Value";
            // Use validateTakeValueForKeyPath() because it coerces the value
            field.validateTakeValueForKeyPath(newValue, typedKeyPath);
        }
        else if (field != null)
        {
            removeFromFields(field);
        }
    }



    /**
     * Returns the data for the given field name.
     *
     * @param fieldName the field name to look for
     * @param sender an object that this method can use to further distinguish what should be done with the value
     *
     * @return the data in the given field, or null if the field doesn't exist or is null
     */
    public Object exportValueForFieldNamed(String fieldName, Object sender)
    {
        /** require  [valid_param] fieldName != null;
                     [has_column] hasColumnNamed(fieldName); **/

        VirtualColumn column = (VirtualColumn)columnNamed(fieldName);
        boolean didCreateOGNLContext =  calculationContext() == null && column.type().name().equals(ColumnType.CalculatedColumnType);
        if (didCreateOGNLContext)
        {
            setCalculationContext(((VirtualCalculatedColumn)column).newCalculationContext());
        }

        try
        {
            return column.exportValueInRow(this, sender);
        }
        finally
        {
            if (didCreateOGNLContext)
            {
                setCalculationContext(null);
            }
        }
    }



    /**
     * Returns the field value for the given key if there is such a field, otherwise returns the result of calling super (which will likely raise an exception).
     *
     * @param key the key to lookup
     */
    public Object handleQueryWithUnboundKey(String key)
    {
        /** require [valid_param] key != null; **/

        if (hasColumnNamed(key))
        {
            return valueForFieldNamed(key);
        }

        return super.handleQueryWithUnboundKey(key);
    }



    /**
     * Sets the field value for the given key if there is such a field, otherwise sets the key in super (which will likely raise an exception).
     *
     * @param value the value to set
     * @param key the key to set
     */
    public void handleTakeValueForUnboundKey(Object value, String key)
    {
        /** require [valid_key_param] key != null; **/

        if (hasColumnNamed(key))
        {
            takeValueForFieldNamed(value, key);
        }
        else
        {
            super.handleTakeValueForUnboundKey(value, key);
        }
    }



    /**
     * Returns <code>true</code> if any row in another table refers to data in this row.  This is the case if a lookup field in a row of another table uses a field in this row as  the source of its lookup data.  If this returns <code>true</code>, then this row cannot be deleted (prevented by Deny deletion rule in VirutalField).
     *
     * @return <code>true</code> if row in another table refers to data in this row.
     */
    public boolean hasReferringRows()
    {
        boolean hasReferringRows = false;

        Enumeration fieldEnumerator = fields().objectEnumerator();
        while (fieldEnumerator.hasMoreElements() && ! hasReferringRows)
        {
            hasReferringRows = ((VirtualField) fieldEnumerator.nextElement()).referringFields().count() > 0;
        }

        return hasReferringRows;
    }



    /**
     * Returns the Context used when evaluating calculated fields.  This will return null
     * if there is no evaluation in progress.  This context is passed down the chain when calculated fields
     * include other calculated fields in their expressions.
     *
     * @return the Context used when evaluating calculated fields
     */
    public Map calculationContext()
    {
        return calculationContext;
    }



    /**
     * Sets the Context used when evaluating calculated fields.  Called with null after
     * an evaluation is complete.
     *
     * @param context the Context to be used when evaluating calculated fields
     */
    public void setCalculationContext(Map context)
    {
        calculationContext = context;
    }

}
