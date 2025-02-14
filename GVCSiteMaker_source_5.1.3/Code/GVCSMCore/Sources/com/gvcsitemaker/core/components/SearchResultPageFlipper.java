// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;

public class SearchResultPageFlipper extends WOComponent {

    protected SearchResultManager searchResultManager;
    public Integer aGroupNumber;


    /**
     * Designated constructor.
     */
     public SearchResultPageFlipper(WOContext aContext)
    {
           super(aContext);
    }
  

    
    /**
     * Selects aGroupNumber as the currently displayed group.
     */
    public WOComponent selectGroup()
    {
        /** require
        [valid_number_of_groups] searchResultManager().numberOfGroups() > 0;
        [a_group_number_not_null] aGroupNumber != null;
        [a_group_number_positive] aGroupNumber.intValue() > 0;
        [valid_group_number] aGroupNumber.intValue() <= searchResultManager().numberOfGroups(); **/
        
        searchResultManager().setCurrentGroupNumber(aGroupNumber);
        
        return context().page();
    }

    

    /**
     * Returns <code>true</code> if aGroupNumber is the currentGroup().  Used to disable the hyperlink.
     *
     * @return <code>true</code> if aGroupNumber is the currentGroup().
     */
    public boolean isCurrentGroup()
    {
        return searchResultManager().currentGroupNumber().equals(aGroupNumber);
    }

    
    
    /* ********** Generic setters and getters ************** */
    public SearchResultManager searchResultManager() {
        return searchResultManager;
    }
    public void setSearchResultManager(SearchResultManager newSearchResultManager) {
        searchResultManager = newSearchResultManager;
    }

}
