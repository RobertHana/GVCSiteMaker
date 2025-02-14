// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.conversion;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;

public class ConfigureSearchModeDefault extends WOComponent {
    public String searchModeMessage;

    public ConfigureSearchModeDefault(WOContext context) {
        super(context);
    }

    public WOComponent configureSearchMode() {
        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        try {
            // Fetch like this to avoid fetching as much bad data as possible.
            NSArray validDataAccessSections = EOUtilities.objectsWithQualifierFormat(ec, "Section",
                    "website <> null and component <> null and type.key = 'DataAccess'" , NSArray.EmptyArray);
            DebugOut.println(1, "Found " + validDataAccessSections.count() + " sections to configure search mode for.");
            
            NSMutableArray searchModeComponents = new NSMutableArray();
            for (int i = 0; i < validDataAccessSections.count(); i++) {
                Section aSection = (Section) validDataAccessSections.objectAtIndex(i);
                try
                {
                    DataAccess dataAccess = (DataAccess)aSection.component();
                    if ((dataAccess.databaseTable() != null) && ( ! EOObject.isDummyFaultEO(dataAccess.databaseTable())))
                    {
                        searchModeComponents.addObject(dataAccess.componentForMode(DataAccessMode.SEARCH_MODE));
                    }
                }
                catch (java.lang.IllegalStateException e)
                {
                    // Bad data - ignore.  Probably component missing from DB.
                }
            }
            DebugOut.println(1, "Found " + searchModeComponents.count() + " searchMode Components to configure search mode for.");

    
            int affectedSections = 0;

            for (int i = 0; i < searchModeComponents.count(); i++) {
                DataAccessSearchMode searchMode = (DataAccessSearchMode) searchModeComponents.objectAtIndex(i);

                // Set default for access
                if (searchMode.valueForBindingKey(DataAccessSearchMode.SEARCH_FORM_TYPE_BINDING) == null) {
                    searchMode.setBindingForKey(DataAccessSearchMode.QUERY_BUILDER_FORM, DataAccessSearchMode.SEARCH_FORM_TYPE_BINDING);
                    
                    // Hack for bad data!
                    if (searchMode.toChildren().count() != searchMode.orderedComponents().count())
                    {
                        searchMode.layoutComponents();
                    }
                    
                    // Create DataAccessColumns for search config
                    searchMode.synchronizeColumnsWithTable();
                    affectedSections++;
                }

                ec.saveChanges();
            }

            searchModeMessage = "Configured " + affectedSections + " sections";
            DebugOut.println(1, "SUCCESS: Configured " + affectedSections + " searchMode Components.");
            
        } finally {
            ec.unlock();
            ec.dispose();
        }

        return context().page();
    }

}
