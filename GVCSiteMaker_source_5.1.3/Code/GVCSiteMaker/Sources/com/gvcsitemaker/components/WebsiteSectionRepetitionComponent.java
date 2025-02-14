// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.components;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.editors.*;
import com.gvcsitemaker.pages.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


public class WebsiteSectionRepetitionComponent extends SMAuthComponent implements SMSecurePage
{
    public NSArray orderedSections;

    public Section aSection;
    public int sectionIndex;

    public Group aGroup;

    public NSMutableSet selectedSections;
    public NSArray selectedSectionsActions = new NSArray(new Object[] { "Select All", "Unselect All", "Show/Hide Selected", "Set Access on Selected", "Delete Selected" });
    public String anActionString;
    public String selectedSectionsAction = null;
    public NSMutableDictionary selectedSectionAction = new NSMutableDictionary();

    protected NSMutableSet sectionsShowingChildren;
    protected NSMutableSet sectionsShowingGroups;

    public boolean showDeleteModalDialog = false;
    public Section sectionToDelete; // has to be public due to being used in a precondition to a public method


    public WebsiteSectionRepetitionComponent(WOContext context)
    {
        super(context);
        selectedSections = new NSMutableSet();
    }



}
