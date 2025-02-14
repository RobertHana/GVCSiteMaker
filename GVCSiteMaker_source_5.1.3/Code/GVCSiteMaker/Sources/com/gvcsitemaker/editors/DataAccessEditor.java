// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;

import com.webobjects.appserver.WOContext;


/**
 * This page chooses which sub-editor to use for this Data Access Section.  DataAccessTableEditor is used for the one time only second step of creating a DataAccess Section - choosing the table.  After a table has been assigned, the DataAccessConfigurationEditor is used to edit this type of Section.
 */
public class DataAccessEditor extends DataAccessBaseEditor 
{

    public String editorComponent = null;


    /**
     * Designated constructor.
     */
    public DataAccessEditor(WOContext context)
    {
        super(context);
    }



    /**
     * Sets the Section whose component will be edited in this editor and chooses which of DataAccessTableEditor or DataAccessConfigurationEditor will be used to edit it.
     *
     * @param newSection - the Section whose component will be edited in this editor.
     */
    public void setSection( Section newSection )
    {
        if (section() != newSection)
        {
            super.setSection(newSection);

            if (databaseTable() == null)
            {
                editorComponent = "DataAccessTableEditor";
            }
            else
            {
                editorComponent = "DataAccessConfigurationEditor";
            }

        }

        /** ensure [has_editorComponent] editorComponent != null;  **/
    }


}
