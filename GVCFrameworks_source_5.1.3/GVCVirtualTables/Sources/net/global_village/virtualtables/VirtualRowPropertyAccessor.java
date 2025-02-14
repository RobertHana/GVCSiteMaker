package net.global_village.virtualtables;


import java.util.*;

import ognl.*;

import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;

/**
 * OGNL property accessor for VirtualRows.  Only getProperty is implemented.
 *
 * @author Copyright (c) 2001-2008  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */
public class VirtualRowPropertyAccessor implements PropertyAccessor
{

    /** This is used in calculated column expressions with Lookup fields to indicate navigation to the VirtualRow
     * containing the lookup value.
     */
    public static String RelationalNavigationMarker = "$$";


    /**
     * Key into calculation context giving the names of columns which are being calculated and have not yet finished.
     * An attempt to calculate the value for a field already in this list indicates a circular definition in the
     * expression.
     */
    public static final String CalculatingColumns = "calculatingColumns";



    /**
     * Gets the value for one field in a VirtualRow by field name.
     *
     * @see PropertyAccessor#getProperty(Map, Object, Object)
     *
     * @param map used to prevent circular expressions
     * @param target the VirtualRow to extract a value from
     * @param name the name of the column/field to extract
     * @return the value for the named field in target
     * @throws OgnlException if the value can't be obtained
     */
    public Object getProperty(Map map, Object target, Object name) throws OgnlException
    {
        /** require [valid_map] map != null;
                    [valid_target] target != null;
                    [valid_name] name != null;   **/

        VirtualRow row = (VirtualRow) target;
        NSMutableArray calculatingColumns = (NSMutableArray) map.get(CalculatingColumns);

        // If the name indicates relational navigation, set the flag, and grab the first field name
        boolean isRelationonalNavigation = ((String)name).indexOf(VirtualRowPropertyAccessor.RelationalNavigationMarker) > 0;
        String nameAfterMarker = null;
        if (isRelationonalNavigation)
        {
            String key = (String)name;
            int relationalMarkerPosition = key.indexOf(RelationalNavigationMarker);
            nameAfterMarker = key.substring(relationalMarkerPosition+ RelationalNavigationMarker.length());

            if (nameAfterMarker.length() == 0)
            {
                throw new EOFValidationException(row.table(), "expression", "invalidRelationalExpression", name);
            }

            key = key.substring(0, relationalMarkerPosition);
            name = key;
        }

        if ( ! row.table().hasColumnNamed(name.toString()))
        {
            throw new EOFValidationException(row.table(), "expression", "columnDoesNotExist", name);
        }

        Column column = row.table().columnNamed(name.toString());
        if (calculatingColumns.containsObject(column))
        {
            throw new EOFValidationException(row.table(), "expression", "circularReference", name);
        }

        if (! VirtualCalculatedColumn.canUseColumnInCalculation(column))
        {
            throw new EOFValidationException(row.table(), "expression", "cantBeUsedInCalculation", name);
        }

        if (column.type().name().equals(ColumnType.CalculatedColumnType))
        {
            calculatingColumns.addObject(column);
        }

        Object property = row.valueForFieldNamed(name.toString());

        // Dereference look up columns
        if (property instanceof VirtualField)
        {
            if (isRelationonalNavigation)
            {
             // Handle relational navigation by evaluating the rest of the name on the destination virtual row
                VirtualRow targetRow = ((VirtualField)property).row();
                targetRow.setCalculationContext(row.calculationContext());
                try
                {
                    property = getProperty(map, targetRow, nameAfterMarker);
                }
                finally
                {
                    targetRow.setCalculationContext(null);
                }
            }
            else
            {
                property = ((VirtualField)property).value();
            }
        }

        calculatingColumns.removeObject(column);
        return property;
    }



    /**
     * Not implemented, throws OgnlException.
     *
     * @see ognl.PropertyAccessor#setProperty(java.util.Map, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void setProperty(Map map, Object target, Object name, Object value) throws OgnlException
    {
        // Setting values by a calculated field is not supported
        throw new EOFValidationException(((VirtualRow)target), "expression", "cantAssign", name);
    }


}
