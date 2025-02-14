// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;


/**
 * This page provides functions for managing data tables.
 */
public class ConfigureDataTablesPane extends WebsiteContainerBase implements WebsiteContainer, SMSecurePage
{
    public String tableViewComponentName = "AddEditDatabaseTable";
    public VirtualTable table;


    /**
     * Designated constructor.  Sets Website for WebsiteContainer.
     */
    public ConfigureDataTablesPane(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
    }



}
