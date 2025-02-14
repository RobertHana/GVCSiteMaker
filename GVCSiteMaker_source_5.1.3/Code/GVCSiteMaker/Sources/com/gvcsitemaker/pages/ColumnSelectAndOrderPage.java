// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;


import java.util.Enumeration;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import net.global_village.virtualtables.Column;
import net.global_village.virtualtables.VirtualColumn;
import net.global_village.virtualtables.VirtualTable;
import net.global_village.woextensions.orderablelists.OrderableList;


/**
 * Super class for pages which need to order and select the columns (fields) in a database table.
 */
public class ColumnSelectAndOrderPage extends com.gvcsitemaker.core.components.WebsiteContainerBase
{

    protected VirtualTable table;
    protected NSMutableArray selectedColumns;
    protected OrderableList orderedColumns;
    protected VirtualColumn currentColumn;


    /**
     * Designated constructor.
     */
    public ColumnSelectAndOrderPage(WOContext context)
    {
        super(context);
    }



    /**
     * Action method to set all columns to the "selected" state.
     */
    public WOComponent includeAllFields()
    {
        setSelectedColumns(new NSMutableArray(orderedColumns().ordered()));
        return context().page();
        /** ensure [all_columns_selected] selectedColumns().count() == orderedColumns().ordered().count();  **/
    }



    /**
     * Action method to set all columns to the "unselected" state.
     */
    public WOComponent excludeAllFields()
    {
        setSelectedColumns(new NSMutableArray());
        return context().page();
        /** ensure [no_columns_selected] selectedColumns().count() == 0;  **/
    }



    /**
     * Returns <code>true</code> if the currentColumn() is in the list of selected columns.
     *
     * @return <code>true</code> if the currentColumn() is in the list of selected columns.
     */
    public boolean isColumnIncluded()
    {
        return selectedColumns().containsObject(currentColumn());
    }



    /**
     * Includes or excludes the currentColumn() from the list of selected columns.
     *
     * @param shouldIncludeColumn - <code>true</code> if the currentColumn() should be included in the list of selected columns, <code>false</code> if the currentColumn() should be excluded from the list of selected columns.
     */
    public void setIsColumnIncluded(boolean shouldIncludeColumn)
    {
        if (shouldIncludeColumn && ! isColumnIncluded())
        {
            selectedColumns().addObject(currentColumn());
        }
        else if (isColumnIncluded() && ! shouldIncludeColumn)
        {
            selectedColumns().removeObject(currentColumn());
        }
        /** ensure [column_included_correctly] (shouldIncludeColumn && isColumnIncluded()) || ( ! shouldIncludeColumn && ! isColumnIncluded()); **/
    }



    /**
     * Returns the selected columns ordered by the user's ordering.
     *
     * @return the selected columns ordered by the user's ordering
     */
    public NSArray orderedSelectedColumns()
    {
        NSMutableArray orderedSelectedColumns = new NSMutableArray();
        Enumeration orderedColumnEnumerator = orderedColumns().ordered().objectEnumerator();
        while (orderedColumnEnumerator.hasMoreElements())
        {
            Column column = (Column)orderedColumnEnumerator.nextElement();
            if (selectedColumns().containsObject(column))
            {
                orderedSelectedColumns.addObject(column);
            }
        }
        return orderedSelectedColumns;
        /** ensure [valid_result] Result != null; [all_columns_included] Result.count() == selectedColumns().count(); **/
    }



    /**
     * Initializes the state of this page and the columns shown based on the columns contained in newDatabaseTable.  By default system created columns are not available.
     *
     * @param newDatabaseTable the database table to initialize this page with
     */
    public void initializeWithDatabaseTable(VirtualTable newDatabaseTable)
    {
        /** require [valid_table] newDatabaseTable != null; **/

        if (newDatabaseTable != table)
        {
            table = newDatabaseTable;

            setOrderedColumns(new OrderableList(table.orderedUserColumns()));
            setSelectedColumns(new NSMutableArray(orderedColumns()));
        }

        /** ensure [table_set] databaseTable() != null; **/
    }




    //****************************  Generic Accessors Below Here  ****************************\\

    /** @TypeInfo VirtualTable */
    public VirtualTable table() {
        return table;
    }

    public void setTable(VirtualTable newTable) {
        initializeWithDatabaseTable(newTable);
    }

    public NSMutableArray selectedColumns() {
        return selectedColumns;
    }
    public void setSelectedColumns(NSMutableArray newSelectedColumns) {
        selectedColumns = newSelectedColumns;
    }

    public OrderableList orderedColumns() {
        return orderedColumns;
    }
    public void setOrderedColumns(OrderableList newOrderedColumns) {
        orderedColumns = newOrderedColumns;
    }

    public VirtualColumn currentColumn() {
        return currentColumn;
    }
    public void setCurrentColumn(VirtualColumn newCurrentColumn) {
        currentColumn = newCurrentColumn;
    }


}
