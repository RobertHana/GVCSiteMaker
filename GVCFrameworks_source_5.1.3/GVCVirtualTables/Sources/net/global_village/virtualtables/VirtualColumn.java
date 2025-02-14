package net.global_village.virtualtables;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.EOEditingContext;

import net.global_village.eofvalidation.*;
import net.global_village.foundation.*;


/**
 * Contains meta-data for the columns of a virtual table.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class VirtualColumn extends _VirtualColumn
{
    protected static final String VirtualColumnRestrictingValue = "VirtualColumn";


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(VirtualColumnRestrictingValue);
        setDateCreated(Date.now());
    }



    /**
     * Creates a virtual field based on this column.  Point of single choice for virtual field types.
     *
     * @param row the row to create the field in
     */
    public VirtualField newField(VirtualRow row)
    {
        /** require
        [valid_param] row != null;
        [same_ec] editingContext().equals(row.editingContext()); **/

        String virtualFieldEntityName = "Virtual" + type().name() + "Field";
        VirtualField field = (VirtualField)EOUtilities.createAndInsertInstance(editingContext(), virtualFieldEntityName);

        field.setRow(row);
        row.addToFields(field);
        field.setColumn(this);
        addToFields(field);

        return field;
    }



    /**
     * Returns a detailed, human-readable description of this column.  The description inlcudes which table it is from, its name, and its type.  The description is formatted as 'Table Name - Column Name (Column Type)', e.g. Pictures - Caption (Text)
     *
     * @return a detailed, human-readable description of the type of this column.
     */
    public String longDescription()
    {
        return table().name() + " - " + name() + "(" + type().textDescription() + ")";
    }



    /**
     * Checks for names that are invalid due to conflict with existing method/field names.  We do this so that we can use KVC with virtual rows.
     *
     * @param value the value to validate
     * @return the coerced or changed value
     * @exception EOFValidationException if the value was not valid
     */
    public String validateName(String value) throws EOFValidationException
    {
        /** require [valid_param] value != null; **/

        super.validateName(value);

        if ( ! table().isValidColumnName(value))
        {
            EOFValidationException exception = new EOFValidationException(this,  "name.badName");
            exception.setFailedValue(value);
            throw exception;
        }

        return value;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the value for this column in the passed row.
     *
     * @see VirtualField#value()
     *
     * @param row the VirtualRow to take the value from
     * @return the value for this column in the passed row.
     */
    public Object valueInRow(VirtualRow row)
    {
        /** require [valid_row] row != null; **/
        return row.hasFieldNamed(normalizedName()) ? row.fieldNamed(normalizedName()).value() : null;
    }



    /**
     * Returns the value to export for this column in the passed row.
     *
     * @see VirtualField#valueForExport(Object)
     *
     * @param row the VirtualRow to take the value from
     * @param sender an object that this method can use to further distinguish what should be done with the value
     *
     * @return the value to export for this column in the passed row.
     */
    public Object exportValueInRow(VirtualRow row, Object sender)
    {
        /** require [valid_row] row != null; **/
        return row.hasFieldNamed(normalizedName()) ? row.fieldNamed(normalizedName()).valueForExport(sender) : null;
    }


}
