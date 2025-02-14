package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


/**
 * Stores search sort order criteria for searching Virtual Tables.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessColumnSearchSortOrder extends _DataAccessColumnSearchSortOrder
{
    // Binding keys
    public static final String SORT_DIRECTION_BINDINGKEY = "sortDirection";

    public static final String ORDER_DIRECTION_ASCENDING = "Ascending";
    public static final String ORDER_DIRECTION_DESCENDING = "Descending";


    /**
     * Factory method to create new instances of DataAccessColumnSearchSortOrder.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessColumnSearchSortOrder or a subclass.
     */
    public static DataAccessColumnSearchSortOrder newDataAccessColumnSearchSortOrder()
    {
        return (DataAccessColumnSearchSortOrder)SMEOUtils.newInstanceOf("DataAccessColumnSearchSortOrder");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessColumnSearchSortOrder");

        /** ensure [defaults_set] true; **/
    }



    /**
     * Returns <code>true</code> if the given string is a valid order direction, <code>false</code> otherwise.
     *
     * @param orderDirection the order direction string to test
     * @return <code>true</code> if the given string is a valid order direction, <code>false</code> otherwise
     */
    public boolean isValidOrderDirection(String orderDirection)
    {
        /** require [valid_param] orderDirection != null; **/
        return orderDirection.equals(ORDER_DIRECTION_ASCENDING) || orderDirection.equals(ORDER_DIRECTION_DESCENDING);
    }



    //************** Binding Cover Methods **************\\


    /**
     * Returns the sort direction.
     *
     * @return the sort direction
     */
    public String sortDirection()
    {
        return (String)valueForBindingKey(SORT_DIRECTION_BINDINGKEY);
        /** ensure
        [result_is_asc_or_dec] Result != null ? isValidOrderDirection(Result) : true; **/
    }


    /**
     * Sets the sort direction.
     *
     * @param newSortDirection the new sort direction
     */
    public void setSortDirection(String newSortDirection)
    {
        /** require
        [direction_is_asc_or_dec] newSortDirection != null ? isValidOrderDirection(newSortDirection) : true; **/
        setBindingForKey(newSortDirection, SORT_DIRECTION_BINDINGKEY);
    }



}

