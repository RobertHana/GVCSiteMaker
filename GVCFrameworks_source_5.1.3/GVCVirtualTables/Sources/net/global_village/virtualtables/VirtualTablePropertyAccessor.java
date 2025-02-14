package net.global_village.virtualtables;


import java.util.*;

import ognl.*;

import com.webobjects.foundation.*;

import net.global_village.eofvalidation.*;

/**
 * OGNL property accessor for VirtualTables.  This is only used to validate a calculated
 * expression.  It returns representative values (e.g. dummy values) based on the type of
 * the column. Only getProperty is implemented.
 *
 * @author Copyright (c) 2001-2008  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 10$
 */
public class VirtualTablePropertyAccessor implements PropertyAccessor
{


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

        // If the name indicates relational navigation, set the flag, and grab the first field name
        boolean isRelationonalNavigation = ((String)name).indexOf(VirtualRowPropertyAccessor.RelationalNavigationMarker) > 0;
        String nameAfterMarker = null;
        if (isRelationonalNavigation)
        {
            String key = (String)name;
            int relationalMarkerPosition = key.indexOf(VirtualRowPropertyAccessor.RelationalNavigationMarker);
            nameAfterMarker = key.substring(relationalMarkerPosition +
                                            VirtualRowPropertyAccessor.RelationalNavigationMarker.length());

            if (nameAfterMarker.length() == 0)
            {
                throw new EOFValidationException((VirtualTable)target, "expression", "invalidRelationalExpression", name);
            }
                        key = key.substring(0, relationalMarkerPosition);
            name = key;
        }

        VirtualTable table = ((VirtualTable) target);
        if ( ! table.hasColumnNamed(name.toString()))
        {
            throw new EOFValidationException(table, "expression", "columnDoesNotExist", name);
        }

        Column column = table.columnNamed(name.toString());
        if (! VirtualCalculatedColumn.canUseColumnInCalculation(column))
        {
            throw new EOFValidationException(table, "expression", "cantBeUsedInCalculation", name);
        }

        NSMutableArray calculatingColumns = (NSMutableArray) map.get(VirtualRowPropertyAccessor.CalculatingColumns);

        if (calculatingColumns.containsObject(column))
        {
            throw new EOFValidationException(table, "expression", "circularReference", name);
        }

        if (column.type().name().equals(ColumnType.CalculatedColumnType))
        {
            calculatingColumns.addObject(column);
        }


        Object property;
        if (isRelationonalNavigation && ! (column instanceof VirtualLookupColumn))
        {
            throw new EOFValidationException(table, "expression", "invalidRelationalNavigation", name + VirtualRowPropertyAccessor.RelationalNavigationMarker);
        }
        // Handle relational navigation by evaluating the rest of the name on the destination virtual table
        else if (isRelationonalNavigation)
        {
            Table targetTable = ((VirtualLookupColumn) column).lookupColumn().table();
            targetTable.setCalculationContext(table.calculationContext());
            try
            {
                property = getProperty(map, targetTable, nameAfterMarker);
            }
            finally
            {
                targetTable.setCalculationContext(null);
            }
        }
        else
        {
            property = column.representativeValue();
        }

        calculatingColumns.removeObject(column);

        return property;
    }



    /**
     * Not implemented, throws.
     *
     * @see ognl.PropertyAccessor#setProperty(java.util.Map, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void setProperty(Map map, Object target, Object name, Object value) throws OgnlException
    {
        // Setting values by a calculated field is not supported
        throw new EOFValidationException(((Table)target), "expression", "cantAssign", name);
    }


}
