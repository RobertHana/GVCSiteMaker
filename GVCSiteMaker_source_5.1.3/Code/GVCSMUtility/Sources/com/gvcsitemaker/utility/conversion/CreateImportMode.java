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


public class CreateImportMode extends WOComponent {
    public String importModeMessage;

    
    public CreateImportMode(WOContext context) 
    {
        super(context);
    }

    
    public WOComponent createImportMode() {
        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        try
        {
            // Fetch like this to avoid fetching as much bad data as possible.
            NSArray validDataAccessSections = EOUtilities.objectsWithQualifierFormat(ec, "Section",
                    "website <> null and component <> null and type.key = 'DataAccess'" , NSArray.EmptyArray);
            DebugOut.println(1, "Found " + validDataAccessSections.count() + " sections to add import to.");
            
            NSMutableArray dataAccessComponents = new NSMutableArray();
            for (int i = 0; i < validDataAccessSections.count(); i++) {
                Section aSection = (Section) validDataAccessSections.objectAtIndex(i);
                try
                {
                    DataAccess dataAccess = (DataAccess)aSection.component();
                    if ((dataAccess.databaseTable() != null) && ( ! EOObject.isDummyFaultEO(dataAccess.databaseTable())))
                    {
                        dataAccessComponents.addObject(dataAccess);
                    }
                }
                catch (java.lang.IllegalStateException e)
                {
                    // Bad data - ignore.  Probably component missing from DB.
                }
            }
            DebugOut.println(1, "Found " + dataAccessComponents.count() + " data access Components to add import to.");

            int affectedSections = 0;
            
            for (int i = 0; i < dataAccessComponents.count(); i++)
            {
                DataAccess dataAccess = (DataAccess)dataAccessComponents.objectAtIndex(i);

                // Set default for access
                if (dataAccess.valueForBindingKey(DataAccess.CAN_IMPORT_RECORDS_BINDINGKEY) == null)
                {
                    dataAccess.setCanImportRecords(false);
                }
                
                // Create mode component
                if (dataAccess.componentForMode(DataAccessMode.IMPORT_MODE) == null)
                {
                    dataAccess.addChild(DataAccessImportMode.newDataAccessImportMode());
                    affectedSections++;
                }
                ec.saveChanges();
            }
                
            importModeMessage = "Added import mode to " + affectedSections + " sections";
            DebugOut.println(1, "SUCCESS: Added import mode to " + affectedSections + " data access Components.");
            
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
        
        
        return context().page();
    }

}

