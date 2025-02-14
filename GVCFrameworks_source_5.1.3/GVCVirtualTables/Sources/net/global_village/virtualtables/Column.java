package net.global_village.virtualtables;

import java.util.*;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * A <code>Column</code> holds the meta-data for a column in a <code>Table</code>.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 14$
 */
public class Column extends _Column
{
    protected String normalizedName;

    /**
     * Instance of DefaultComparator to be used when sorting Columns.
     */
    static final public NSComparator DefaultComparator = new DefaultComparator();

    static final protected NSMutableDictionary cachedNormalizedNames = new NSMutableDictionary();


    /**
     * Returns given string normalized to include only alphanumeric characters and the underscore.
     *
     * @param string the string to normalize
     * @return the normalized string
     */
    public static String normalizeStringForColumnNames(String string)
    {
        /** require [valid_param] string != null; **/

        // Memory & Performance optimization
        String normalizeStringForColumnName = (String) cachedNormalizedNames.objectForKey(string);

        // Normalize and cache if not already cached
        if (normalizeStringForColumnName == null)
        {
            StringBuffer resultingString = new StringBuffer();
            for (int i = 0; i < string.length(); i++)
            {
                char character = string.charAt(i);
                if (Character.isDigit(character) || Character.isLetter(character) || character == '_')
                {
                    resultingString.append(character);
                }
            }
            normalizeStringForColumnName = resultingString.toString();
            cachedNormalizedNames.setObjectForKey(normalizeStringForColumnName, string);
        }

        return normalizeStringForColumnName;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setIsSystemColumn(net.global_village.foundation.GVCBoolean.no());
    }



    /**
     * Called when we are fetched from an external data store, or when another editing context changes this object (for example, by changing the name of the column).
     *
     * @param ec the editing context in which this is being fetched
     */
    public void awakeFromFetch(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromFetch(ec);
        table().clearColumnNameMapping();
    }



    /**
     * Returns <code>true</code> if this Column contains an editable value.  System columns are not editable.
     *
     * @return <code>true</code> if this Column contains an editable value
     */
    public net.global_village.foundation.GVCBoolean isEditableColumn()
    {
        return isSystemColumn().negate();
    }



    /**
     * Overridden to clear the table's column name mapping and the normalized name.
     *
     * @param value the name's new value
     */
    public void setName(String value)
    {
        table().clearColumnNameMapping();
        normalizedName = null;
        super.setName(value);
    }



    /**
     * Returns this column's name normalized to include only alphanumeric characters and the underscore.
     *
     * @return the normalized name
     */
    public String normalizedName()
    {
        if ((normalizedName == null) && (name() != null))
        {
            normalizedName = normalizeStringForColumnNames(name());
        }
        return normalizedName;

        /** ensure [definition] ((name() == null) && (Result == null)) || Result.equals(normalizeStringForColumnNames(name())); **/
    }



    /**
     * Is this column referred to by a lookup column?
     *
     * @return <code>true</code> if the column is referred to by a lookup column, <code>false</code> otherwise
     */
    public boolean hasReferringColumns()
    {
        return referringColumns().count() > 0;
    }



    /**
     * Returns a human-readable description of the type of this column.  Note that this method is <em>not</em> guaranteed to return either the type's cannonical name, nor the restricting value for fields (it may, for example, be localized in the future).
     *
     * @return a human-readable description of the type of this column
     */
    public String typeDescription()
    {
        return type().textDescription();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the name of the field attribute that the import process should use to set the field data. This allows columns, such as <code>VirtualLookupColumn</code>, that specify a relationship to name the FK attribute instead of the regular "<type>Value" attribute (which is the default).
     *
     * @return the name of the field attribute that the import process should use
     */
    public String importAttributeName()
    {
        return StringAdditions.downcaseFirstLetter(type().name()) + "Value";
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Used by the table import process to perform additional processing on an imported field value. The value will be
     * set into the attribute specified by <code>importAttributeName()</code>. This is on Column instead of
     * VirtualField for two reasons: first, during import, we don't actually even create VirtualField EOs (this is an
     * optimization) and second, because not all tables will even have a VirtualField (a concrete table, for example).
     * <p>
     * Note that no coercion is done, so <code>value</code> must be the correct type.
     * </p>
     * <p>If this method returns null, no value will be imported.  This can be used to quietly skip importing a specific
     * column type.
     * </p>
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @param value the value we got during import, possibly formatted by a formatter
     * @return the value to use during table import
     * @exception EOFValidationException thrown if <code>value</code> is not a valid value for this column
     */
    public Object valueForImportedValue(Object sender, Object value) throws EOFValidationException
    {
        /** require [valid_value_param] value != null; **/
        return value;
    }



    /**
     * Returns a value representative of this column type (e.g. 1 for a number, the current date for a Date).  These
     * values are used to validate calculated columns without needing to refer to data in the table.
     *
     * @return a value representative of this column type
     */
    public Object representativeValue()
    {
        return type().representativeValue();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * This can be used when sorting if something other than value() is needed for ordering.  This
     * is mostly useful when value() is a relation to another object which contains the actual value
     * to be compared when sorting.  The default implementation just returns normalizedName().
     *
     * @return the keypath to use when comparing fields of this type when sorting rows
     */
    public String keyPathForSorting()
    {
        return normalizedName();
    }



    /**
     * Is this column the same type as the passed name?
     *
     * @param typeName - the name of the ColumnType to check this Column against.
     * @return <code>true</code> if this column the same type as the passed name, <code>false</code> otherwise
     */
    public boolean isType(String typeName)
    {
        /** require [valid_param] typeName != null;  [valid_type] /# No way to check this. #/ true;  **/
        return type().name().equals(typeName);
    }



    /**
     * Returns the internal ID for the column (the primary key value).
     *
     * @return the internal ID for the column
     */
    public Number columnID()
    {
        return (Number)primaryKeyValue();
    }



    /**
     * Checks that this column's name isn't a duplicate of another column's name, missing, or starts with
     * a non-alphabetic character.  We might be able to move this validation to VirtualColumn, since concrete
     * tables are guaranteed to not have duplicate column names, but it doesn't hurt anything to have it here.
     *
     * @param value the value to validate
     * @return the coerced or changed value
     * @exception EOFValidationException if the value was not valid
     */
    public String validateName(String value) throws EOFValidationException
    {
        /** require [valid_param] value != null; **/

        if (value.length() == 0)
        {
            EOFValidationException exception = new EOFValidationException(this, "name.missing");
            exception.setFailedValue(value);
            throw exception;
        }

        if (! Character.isLetter(value.charAt(0)))
        {
            EOFValidationException exception = new EOFValidationException(this, "name.invalid");
            exception.setFailedValue(value);
            throw exception;
        }

        // In WO 5.4.3, table() is null in ColumnTest.testLookupColumns() when saving
        // editingContext().deleteObject(lookupColumn);  A change in validation?
        if (table() != null)
        {
            // Can't just use table().hasColumnNamed() because this object will be found and it will always return true, so we have to do it this way:
            NSMutableSet setOfColumnNames = new NSMutableSet();
            Enumeration columnEnumerator = table().columns().objectEnumerator();
            while (columnEnumerator.hasMoreElements())
            {
                Column column = (Column)columnEnumerator.nextElement();
                if ( ! column.equals(this))
                {
                    setOfColumnNames.addObject(column.normalizedName());
                }
            }

            if (setOfColumnNames.containsObject(normalizedName()))
            {
                EOFValidationException exception = new EOFValidationException(this, "name.duplicate");
                exception.setFailedValue(value);
                throw exception;
            }
        }

        return value;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Makes sure that this column can be deleted.  A column cannot be deleted if it has referring columns.
     *
     * @exception EOFValidationException if the object was not valid for delete
     */
    public void validateForDelete() throws com.webobjects.foundation.NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForDelete();
        }
        catch (com.webobjects.foundation.NSValidation.ValidationException exception)
        {
            exceptions.addObject(exception);
        }

        // If this column is being looked up by other columns, we can't delete it
        if (hasReferringColumns())
        {
            EOFValidationException exception = new EOFValidationException(this, "referringColumns.inUse");
            exceptions.addObject(exception);
        }

        if (exceptions.count() > 0)
        {
            throw com.webobjects.foundation.NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Simple Comparator to sort Column by system column status, date created and name (ignoring case).  This results in system columns being sorted after non-system columns and columns created at the same time being sorted by name.
     */
    static protected class DefaultComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            Column column1 = (Column) object1;
            Column column2 = (Column) object2;

            int ordering = column1.isSystemColumn().compareTo(column2.isSystemColumn());

            if (ordering == NSComparator.OrderedSame)
            {
                ordering = column1.dateCreated().compareTo(column2.dateCreated());
            }

            if (ordering == NSComparator.OrderedSame)
            {
                ordering = column1.name().compareToIgnoreCase(column2.name());
            }

            return ordering;
        }
    }
}
