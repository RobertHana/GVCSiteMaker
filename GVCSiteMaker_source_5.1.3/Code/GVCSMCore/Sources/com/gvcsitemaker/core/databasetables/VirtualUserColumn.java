/**
 * Implementation of VirtualUserColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core.databasetables;

import net.global_village.eofvalidation.*;

import com.gvcsitemaker.core.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;


public class VirtualUserColumn extends _VirtualUserColumn
{
    protected static final String VirtualUserColumnRestrictingValue = "VirtualUserColumn";


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(VirtualUserColumnRestrictingValue);
    }



    /**
     * Returns the name of the field attribute that the import process should use to set the field data.
     *
     * @return the name of the field attribute that the import process should use
     */
    public String importAttributeName()
    {
        return "userFkey";
        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Used by the table import process to perform additional processing on an imported field value.  This subclass looks for the value in it's lookup table and returns the lookup value's FK.<p>
     *
     * Note that no coercion is done, so <code>value</code> must be the correct type.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @param value the value we got during import, possibly formatted by a formatter
     * @return the value to use during table import
     * @exception EOFValidationException thrown if <code>value</code> is not a valid value for this column
     */
    public Object valueForImportedValue(Object sender, Object value) throws EOFValidationException
    {
        /** require [valid_value_param] value != null; [value_is_string] value instanceof String; **/
        User user = User.userForUserID((String)value, editingContext());
        if (user == null)
        {
            EOFValidationException exception = new EOFValidationException("VirtualUserColumn.value.noSuchUser", this);
            exception.setFailedValue(value);
            throw exception;
        }

        NSDictionary pk = EOUtilities.primaryKeyForObject(user.editingContext(), user);
        /** check [has_pk_value] pk.objectForKey("userPKey") != null; **/

        return pk.objectForKey("userPKey");
        /** ensure [valid_result] Result != null; **/
    }

    
    
    /**
     * VirtualUserColumns sort on user ID.
     *
     * @return the keypath to use when comparing fields of this type when sorting rows
     */
    public String keyPathForSorting()
    {
        return normalizedName() + ".userID";
    }
}
