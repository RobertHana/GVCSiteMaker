// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.components.WOSMConfirmAction;
import com.gvcsitemaker.core.components.WebsiteContainerBase;
import com.gvcsitemaker.core.interfaces.SMSecurePage;
import com.gvcsitemaker.core.interfaces.WebsiteContainer;

import com.webobjects.appserver.WOContext;

/**
 * The page that confirms an action by displaying a page wich a question that user can respond as yes or no.
 */  
public class ConfirmAction extends WOSMConfirmAction 
    implements WebsiteContainer, SMSecurePage 
{

    protected String descriptor;
    protected String actionName;
    protected String customMessage;    
    protected Website website;


    /**
     * Designated constructor
     */  
    public ConfirmAction(WOContext aContext)
    {
        super(aContext);
        WebsiteContainerBase.getWebsiteFromPageCreating(this);
        // A few reasonable defaults
        setActionName("Delete");
        customMessage = null;
    }

    
    
    public String actionName() {
        return actionName;
    }
    
    
    
    public void setActionName(String newActionName) {
        actionName = newActionName;
        setConfirmString("Yes, " + actionName + " it");
        setDenyString("No, don't " + actionName + " it");
    }
    
    
    /**
     * Retuns true if default message should be displayed, false otherwise.
     * 
     * @return true if default message should be displayed, false otherwise.
     */  
    public boolean shouldDisplayDefaultMessage()
    {
    		return customMessage() == null;
    }

    /* Generic setters and getters ***************************************/

    public String descriptor() {
        return descriptor;
    }
    public void setDescriptor(String newDescriptor) {
        descriptor = newDescriptor;
    }
    
    public String customMessage() {
        return customMessage;
    }
    public void setCustomMessage(String newCustomMessage) {
    		customMessage = newCustomMessage;
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
