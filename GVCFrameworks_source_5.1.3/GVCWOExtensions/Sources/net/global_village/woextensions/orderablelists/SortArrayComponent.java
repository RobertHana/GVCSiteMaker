package net.global_village.woextensions.orderablelists;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * An anchor that the user can click to sort the passed in array.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 8$
 */
public class SortArrayComponent extends net.global_village.woextensions.ValidatingComponent
{
    protected java.lang.Boolean sortAscending = null;


    /**
     * Designated constructor.
     *
     * @param context context of a transaction
     */
    public SortArrayComponent(WOContext context)
    {
        super(context);
        /** require [valid_param] context != null; **/
    }



    /**
     * Sorts the selected column.
     *
     * @return this page
     */
    public com.webobjects.appserver.WOComponent sortColumn()
    {
        if (comparator() != null)
        {
            setValueForBinding(NSArrayAdditions.sortedArrayUsingComparator(array(), comparator()), "array");
        }
        else
        {
            if (toggleAscendingDescending())
            {
                setValueForBinding( Boolean.valueOf(! sortAscending()), "sortAscending");
                sortAscending = new java.lang.Boolean( ! sortAscending.booleanValue());
            }
            NSSelector selector = sortAscending() ? EOSortOrdering.CompareCaseInsensitiveAscending : EOSortOrdering.CompareCaseInsensitiveDescending;
            EOSortOrdering sortOrdering = new EOSortOrdering(sortKey(), selector);
            setValueForBinding(EOSortOrdering.sortedArrayUsingKeyOrderArray(array(), new NSArray(sortOrdering)), "array");

            if (hasBinding("selectedSortOrder"))
            {
                setValueForBinding(sortOrdering, "selectedSortOrder");
            }
        }

        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Overriden so that only one SortArrayComponent will assign to the selectedSortOrder binding.
     *
     * @return false
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    public NSArray array()
    {
        return (NSArray)valueForBinding("array");
    }


    public NSComparator comparator()
    {
        return (NSComparator)valueForBinding("comparator");
    }


    public String sortKey()
    {
        return (String)valueForBinding("sortKey");
    }


    public boolean toggleAscendingDescending()
    {
        return booleanValueForBinding("toggleAscendingDescending", true);
    }


    public boolean sortAscending()
    {
        if (sortAscending == null)
        {
            sortAscending = Boolean.valueOf(booleanValueForBinding("sortAscending", false));
        }
        return sortAscending.booleanValue();
    }



}
