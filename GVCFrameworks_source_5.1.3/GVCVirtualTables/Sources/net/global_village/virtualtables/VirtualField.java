package net.global_village.virtualtables;



/**
 * Holds the data for a virtual table.  Each field in a table will be associated with a specific column, which defines the type of data that the field must contain.  Each different data type is a separate subclass of this.<p>
 *
 * Creating new VirtualField subclasses<br>
 * Creating a new VirtualFIeld subclass is usually simple (just do it!), there are a few cases where you may need to do other things:
 * <ul>
 *   <li>If the "value" of the field is <em>not</em> an attribute of the EO (as is the case with VirtualLookupColumns (q.v.)), you'll also need to create a VirtualColumn subclass and override importAttributeName() and valueForImportedValue()
 * </ul>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class VirtualField extends _VirtualField
{


    /**
     * Returns the field's column name.
     *
     * @return this field's column name
     */
    public String name()
    {
        return column().name();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * The generic accessor for fields.  Subclasses should override this to return their data.
     *
     * @return this field's value
     */
    public Object value()
    {
        throw new Error("Subclass responsibility");
    }



    /**
     * The generic setter for fields.  Subclasses should override this to set their data.
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        throw new Error("Subclass responsibility");
    }



    /**
     * Returns a copy of this field's value.  This is for use when duplicating and copying rows.
     *
     * @return a copy of this field's value
     */
    public Object valueCopy()
    {
        // In most cases the value is immutable so we can just return it.
        return value();
    }



    /**
     * Returns <code>true</code> if any fields in another table refers to this field.  This is the case if a lookup field in a row of another table uses this field as the source of its lookup data.  If this returns <code>true</code>, then this field cannot be deleted (prevented by Deny deletion rule in VirutalField).
     *
     * @return <code>true</code> if any fields in another table refers to this field.
     */
    public boolean hasReferringFields()
    {
        return referringFields().count() > 0;
    }



    /**
     * Used by the table export process to perform additional processing on an exported field value.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @return the value to use during table export
     */
    public Object valueForExport(Object sender)
    {
        return value();
    }


}
