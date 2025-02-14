// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.utility.pages;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;

import com.webobjects.appserver.*;


/* Stolen from GVCSiteMaker to modify UI */
public class ConfirmAction extends WOSMConfirmAction implements WebsiteContainer {

	public String descriptor;
	public String actionName;
	public Website website;


    public ConfirmAction(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
        // A few reasonable defaults
        setActionName("delete");
    }

    public String actionName() {
        return actionName;
    }
    public void setActionName(String newActionName) {
        actionName = newActionName;
        setConfirmString("Yes, " + actionName + " it");
        setDenyString("No, don't " + actionName + " it");
    }

    /* Generic setters and getters ***************************************/

    public String descriptor() {
        return descriptor;
    }
    public void setDescriptor(String newDescriptor) {
        descriptor = newDescriptor;
    }

    public Website website()
    {
        return website;
    }
    public void setWebsite(Website newWebsite)
    {
        website = newWebsite;
    }

}
