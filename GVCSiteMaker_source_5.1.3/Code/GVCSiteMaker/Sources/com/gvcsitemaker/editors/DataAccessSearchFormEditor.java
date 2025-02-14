// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;


import java.util.Enumeration;

import com.gvcsitemaker.core.pagecomponent.DataAccessColumnSearchSortOrder;
import com.gvcsitemaker.core.pagecomponent.DataAccessColumnSearchValue;
import com.gvcsitemaker.core.pagecomponent.DataAccessSiteFileColumn;
import com.gvcsitemaker.core.pagecomponent.PageComponent;
import com.gvcsitemaker.core.support.OrderedComponentList;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;



/**
 * Sub-editor for a DataAccessConfigurationEditor to handle display elements specific to the Search for Records form.
 */
public class DataAccessSearchFormEditor extends DataAccessSubEditor
{


    /**
     * Designated constructor.
     */
    public DataAccessSearchFormEditor(WOContext context) 
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
        return com.gvcsitemaker.core.pagecomponent.DataAccessMode.SEARCH_MODE;
    }
    
    
    
    /**
     * Convenince method.
     * 
     * @return dataAccessMode() cast to com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchMode()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode) dataAccessMode();
    }
    
    
    
    /**
     * @return constant type for query builder search form
     */
    public String queryBuilderFormType()
    {
        return com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode.QUERY_BUILDER_FORM;
    }
    


    /**
     * @return constant type for search all search form
     */
    public String searchAllFormType()
    {
        return com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode.SEARCH_ALL_FORM;
    }

    
    
    /**
     * @return constant type for simple search form
     */
    public String simpleSearchFormType()
    {
        return com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode.SIMPLE_SEARCH_FORM;
    }

    
    
    /**
     * @return the columns to display
     */    
    public OrderedComponentList orderedComponents()
    {
        if (orderedComponents == null)
        {
            orderedComponents = new OrderedComponentList(orderedDataAccessColumns());
        }
        
        return orderedComponents;
        
        /** ensure [valid_result] Result != null;  **/        
    }    

    
    
    /**
     * Overriden to ensure that non-searchable columns are at the end of the orderedComponents so that the ReorderPopup will work.
     * 
     * @return the normal DataAccessColumn children, in order
     */
    public NSArray orderedDataAccessColumns()
    {
        //For ReorderPopup to work, items not included in this list should be at the bottom of the list
        
        Enumeration componentEnumerator = searchMode().orderedComponents().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent child = (PageComponent)componentEnumerator.nextElement();

            if ((child instanceof DataAccessColumnSearchValue) || 
                    (child instanceof DataAccessColumnSearchSortOrder) ||
                    (child instanceof DataAccessSiteFileColumn) )
            {
                searchMode().moveLast(child);
            }
        }

        return searchMode().orderedDataAccessColumns();
    }    
}
