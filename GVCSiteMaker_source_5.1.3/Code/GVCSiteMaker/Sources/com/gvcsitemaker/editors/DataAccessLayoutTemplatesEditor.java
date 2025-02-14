// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.pagecomponent.DataAccessMode;

import com.webobjects.appserver.WOContext;


/**
 * This class oversees editing the custom template for the DataAccessModes.  It uses DataAccessModeTemplateEditor to edit each mode.
 */
public class DataAccessLayoutTemplatesEditor extends DataAccessBaseEditor
{
    public DataAccessMode addMode;
    public DataAccessMode singleMode;
    public DataAccessMode listMode;
    public DataAccessMode searchMode;
    public DataAccessMode importMode;
    
    /**
     * Designated constructor.
     */
    public DataAccessLayoutTemplatesEditor(WOContext context) 
    {
        super(context);
    }



    /**
     * Overridden to set the modes when the Section is set.
     */
    public void setSection( Section newSection )
    {
        super.setSection(newSection);
        singleMode = dataAccessComponent().componentForMode(DataAccessMode.SINGLE_MODE);
        listMode = dataAccessComponent().componentForMode(DataAccessMode.LIST_MODE);
        addMode = dataAccessComponent().componentForMode(DataAccessMode.ADD_MODE);
        searchMode = dataAccessComponent().componentForMode(DataAccessMode.SEARCH_MODE);
        importMode = dataAccessComponent().componentForMode(DataAccessMode.IMPORT_MODE);
    }

}
