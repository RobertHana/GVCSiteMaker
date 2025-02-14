// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.conversion;
import com.gvcsitemaker.core.pagecomponent.*;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofvalidation.EOEditingContext;

public class ConvertExternalURL extends WOComponent {

    public String conversionMessage;
    
    
    public ConvertExternalURL(WOContext context) {
        super(context);
    }

    
    public WOComponent convertSections() {
        EOEditingContext ec = new EOEditingContext();
        ec.lock();
        try {
            EOFetchSpecification fetchSpec = new EOFetchSpecification("Hyperlink", null,
                    null);
            NSArray components = ec.objectsWithFetchSpecification(fetchSpec);
            int affectedSections = 0;

            for (int i = 0; i < components.count(); i++) {
                Hyperlink hyperlink = (Hyperlink) components.objectAtIndex(i);

                if (hyperlink.linkType() == null)
                {
                    hyperlink.setLinkType(PageComponent.URL_TYPE);
                    affectedSections++;
                }
                    
                ec.saveChanges();

            }

            conversionMessage = "Configured " + affectedSections + " sections";

        } finally {
            ec.unlock();
            ec.dispose();
        }

        return context().page();
    }

}
