package net.global_village.virtualtables;

import java.math.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.genericobjects.*;


/**
 * A lookup table containing the type names for all column types.  See the constants defined here for valid types.  The types are used to create the proper field classes for each field in a virtual table.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 9$
 */
public class ColumnType extends _ColumnType
{
    public static final String StringColumnType = "String";
    public static final String MemoColumnType = "Memo";
    public static final String NumberColumnType = "Number";
    public static final String TimestampColumnType = "Timestamp";
    public static final String LookupColumnType = "Lookup";
    public static final String BooleanColumnType = "Boolean";
    public static final String CalculatedColumnType = "Calculated";
    public static final String RecordIDColumnType = "RecordID";

    protected static NSMutableSet validExportTypes =
        new NSMutableSet(new String[] {StringColumnType, MemoColumnType, NumberColumnType, TimestampColumnType, LookupColumnType, BooleanColumnType, CalculatedColumnType});
    protected static NSMutableSet validImportTypes =
        new NSMutableSet(new String[] {StringColumnType, MemoColumnType, NumberColumnType, TimestampColumnType, LookupColumnType, BooleanColumnType});

    private static NSMutableDictionary representativeTypes = null;


    /**
     * Returns dictionary of values representative of each column type (e.g. 1 for a number, the current date for a Date).  These
     * values are used to validate calculated columns without needing to refer to data in the table.
     *
     * @return dictionary of values representative of each column type
     */
    public static NSDictionary representativeTypes()
    {
        if (representativeTypes == null)
        {
            representativeTypes= new NSMutableDictionary(new Object[]{"a string",
                                                                      "a string",
                                                                      new BigDecimal("1"),
                                                                      new NSTimestamp(),
                                                                      "not used, see VirtualLookupColumn",
                                                                      Boolean.TRUE,
                                                                      "1 + 1",
                                                                      "0123456789abcdef0123456789abcdeg0123456789abcdef"},
                                                         new String[]{StringColumnType,
                                                                      MemoColumnType,
                                                                      NumberColumnType,
                                                                      TimestampColumnType,
                                                                      LookupColumnType,
                                                                      BooleanColumnType,
                                                                      CalculatedColumnType,
                                                                      RecordIDColumnType});
        }

        return representativeTypes;
    }



    /**
     * Adds a column type and representative value to the dictionary of values representative of each column type.
     *
     * @param columnType the name of the Column Type to add
     * @param representativeType a representative type for this column
     *
     * @see #representativeTypes()
     */
    public static void addRepresentativeType(String columnType, Object representativeType)
    {
        /** require [valid_columnType] columnType != null; [valid_representativeType] representativeType != null; **/
        representativeTypes();
        representativeTypes.setObjectForKey(representativeType, columnType);
        /** ensure [type_recorded] representativeType.equals(representativeTypes().objectForKey(columnType));  **/
    }



    /**
     * Returns the type with the given name.
     *
     * @param ec the editing context into which to fetch the type
     * @param name the type's name
     * @return the column type with the given name
     */
    public static ColumnType columnTypeWithName(EOEditingContext ec, String name)
    {
        /** require
        [valid_ec_param] ec != null;
        [valid_name_param] name != null; **/

        return (ColumnType)Lookup.objectForName(ec, "ColumnType", name);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the types that are valid for exporting.
     *
     * @return the types that are valid for exporting
     */
    public static NSSet validExportTypes()
    {
        return validExportTypes;
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Adds the given name to the list of types that are valid for export.
     *
     * @param typeName the type name to add
     */
    public static void addToValidExportTypes(String typeName)
    {
        /** require [valid_param] typeName != null; **/
        validExportTypes.addObject(typeName);
    }


    /**
     * Returns the types that are valid for importing.
     *
     * @return the types that are valid for importing
     */
    public static NSSet validImportTypes()
    {
        return validImportTypes;
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Adds the given name to the list of types that are valid for export.
     *
     * @param typeName the type name to add
     */
    public static void addToValidImportTypes(String typeName)
    {
        /** require [valid_param] typeName != null; **/
        validImportTypes.addObject(typeName);
    }



    /**
     * Returns a default value representative of this column type (e.g. 1 for a number, the current date for a Date).  These
     * values are used to validate calculated columns without needing to refer to data in the table.
     *
     * @return a value representative of this column type
     */
    public Object representativeValue()
    {
        /** require [known_type] name() != null && representativeTypes().objectForKey(name()) != null; **/
        return representativeTypes().objectForKey(name());
        /** ensure [valid_result] Result != null; **/
    }


}
