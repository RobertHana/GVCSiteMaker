// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.conversion;
import com.gvcsitemaker.core.*;

import com.webobjects.appserver.*;
import com.webobjects.appserver.WOComponent;

import net.global_village.woextensions.*;


/**
 * Sets or overwrites the WebsiteCreationMessage for the root OrgUnit.  
 */
public class SetSiteCreationTemplate extends WOComponent 
{
    protected OrgUnit rootOrgUnit;

    public SetSiteCreationTemplate(WOContext context) 
    {
        super(context);
        setRootOrgUnit(OrgUnit.rootUnit(session().defaultEditingContext()));
    }

 
    public WOComponent setTemplate() 
    {
        rootOrgUnit().setWebsiteCreationMessage(ResourceManagerAdditions.stringFromResource(
            "WebsiteCreationMessageDefaultTemplate.html", "GVCSMCore"));
        session().defaultEditingContext().saveChanges();
                    
        return null;
    }
    

    public OrgUnit rootOrgUnit() {
         return rootOrgUnit;
     }
     public void setRootOrgUnit(OrgUnit newRootOrgUnit) {
         rootOrgUnit = newRootOrgUnit;
     }

}
