// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.webobjects.appserver.WOContext;


/**
 * Sub-editor for a DataAccessConfigurationEditor to handle display elements specific to Single Record.
 */
public class DataAccessListOfRecordsEditor extends DataAccessSubEditor
{


    /**
     * Designated constructor.
     */
    public DataAccessListOfRecordsEditor(WOContext context) 
    {
        super(context);
    }



    /**
     * Returns the String name of the mode from com.gvcsitemaker.core.pagecomponent.DataAccessMode
     *
     * @return the String name of the mode from com.gvcsitemaker.core.pagecomponent.DataAccessMode.
     */
    public String editorModeName()
    {
        return com.gvcsitemaker.core.pagecomponent.DataAccessMode.LIST_MODE;
    }
}
