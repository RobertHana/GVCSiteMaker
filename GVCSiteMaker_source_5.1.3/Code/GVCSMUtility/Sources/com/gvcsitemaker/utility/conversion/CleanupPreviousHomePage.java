// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.conversion;
import com.gvcsitemaker.core.*;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.EOEditingContext;
import net.global_village.foundation.GVCBoolean;


public class CleanupPreviousHomePage extends WOComponent {

    public String cleanupMessage;
    
    public CleanupPreviousHomePage(WOContext context) {
        super(context);
    }

    
    public WOComponent cleanup() {
        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        Integer lastPosition = new Integer(999);
        try
        {
            EOQualifier hasPreviousHomeSection = EOQualifier.qualifierWithQualifierFormat("previousHomeSection != nil", NSArray.EmptyArray);
            EOFetchSpecification fetchSpec = new EOFetchSpecification("ConversionWebsite", hasPreviousHomeSection, null);
            NSArray affectedWebsites = ec.objectsWithFetchSpecification(fetchSpec); 
            
            for (int i = 0; i < affectedWebsites.count(); i++)
            {
                Section previousHomeSection = (Section)((ConversionWebsite)affectedWebsites.objectAtIndex(i)).valueForKey("previousHomeSection"); 
                if ((previousHomeSection != null) && ( ! EOObject.isDummyFaultEO(previousHomeSection)))
                {
                    previousHomeSection.setIsNavigable(GVCBoolean.falseBoolean());
                    previousHomeSection.setName("_old_home_hidden_from_navigation_");
                    previousHomeSection.setSectionOrder(lastPosition);
                    ec.saveChanges();
                }
            }
            
            ec.saveChanges();
                
            cleanupMessage = "Cleaned up " + affectedWebsites.count() + " websites";
            
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
        
        
        return context().page();
    }
    
    
}
