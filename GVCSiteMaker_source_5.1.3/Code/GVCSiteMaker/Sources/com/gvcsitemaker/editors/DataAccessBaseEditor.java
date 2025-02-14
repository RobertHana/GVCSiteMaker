// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;


import com.gvcsitemaker.core.pagecomponent.DataAccess;

import com.webobjects.appserver.WOContext;

import net.global_village.virtualtables.Table;


/**
 * Support methods common to all components editing some part of a Data Access Section.
 */
public class DataAccessBaseEditor extends BaseEditor
{


    /**
     * Designated constructor.
     */
    public DataAccessBaseEditor(WOContext context)
    {
        super(context);
    }


    
    /**
     * Returns component() downcast to com.gvcsitemaker.core.pagecomponent.DataAccess.
     *
     * @return component() downcast to com.gvcsitemaker.core.pagecomponent.DataAccess.
     */
    public DataAccess dataAccessComponent()
    {
        return (DataAccess) section().component();
    }



    /**
     * Returns the database table linked to dataAccessComponent().
     *
     * @return the database table linked to dataAccessComponent().
     */
    public Table databaseTable()
    {
        return dataAccessComponent().databaseTable();
    }
   


    /**
     * This method should be called by sub-classes and sub-components to notify the main Editor that something is invalid and can not be saved.  The component finding the invalid data is responsible for displaying a message identifying the problem.  The main Editor is responsible for aborting any save attempt and displaying a generic message that the save was not performed due to validation problems.  The default implementation of this method simply passes the notification to it's parent() if that parent is a DataAccessBaseEditor.
     */
    public void notifySectionInvalid()
    {
        if (parent() instanceof DataAccessBaseEditor)
        {
            ((DataAccessBaseEditor)parent()).notifySectionInvalid();
        }
    }
    
    
}
