/**
 * Implementation of DataAccessSectionType common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;



public class DataAccessSectionType extends _DataAccessSectionType
{


    /**
     * Returns the singleton instance of DataAccessSectionType in the passed EOEditingContext
     */
    public static DataAccessSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (DataAccessSectionType) getInstance(ec, "DataAccessSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of DataAccessSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessSectionType or a subclass.
     */
    public static DataAccessSectionType newDataAccessSectionType()
    {
        return (DataAccessSectionType) SMEOUtils.newInstanceOf("DataAccessSectionType");

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this Section uses passed Database Table.
     *
     * @param aSection - the Section to check Database Table usage for
     * @param aTable - the database table to check
     * @return <code>true</code> if this Section uses passed Database Table.
     */
    public boolean usesDatabaseTable(Section aSection, Table aTable)
    {
        /** require [valid_section] aSection != null;   [valid_table] aTable != null; **/

        DataAccess dataAccessComponent = (DataAccess)aSection.component();
        // Warning, dataAccessComponent.databaseTable() can be null if the section does not yet have a table assigned.  Do not reverse this comparison.
        return (dataAccessComponent != null) && aTable.equals(dataAccessComponent.databaseTable());
    }


}

