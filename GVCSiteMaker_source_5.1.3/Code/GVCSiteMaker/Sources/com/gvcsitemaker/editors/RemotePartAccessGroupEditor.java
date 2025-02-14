// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.RemoteParticipantGroup;
import com.gvcsitemaker.core.SectionStyle;
import com.gvcsitemaker.pages.ConfigAccessGroup;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;



/** 
 * Editor used by the ConfigureAccessGroup page for RemoteParticipantGroups. 
 */
public class RemotePartAccessGroupEditor extends WOComponent 
{

    protected RemoteParticipantGroup currentGroup;
    protected SectionStyle aStyle;
    protected NSArray styles;
    
    
    public RemotePartAccessGroupEditor(WOContext context) 
    {
        super(context);
    }

    
    
    /**
     * Lazy creation of available styles list.
     *
     * @see com.webobjects.appserver.WOComponent#awake()
     */
    public void awake() 
    {
        super.awake();

        if ( (parentEditor().website() != null) && (styles == null) )
        {
            styles = SectionStyle.stylesAdvailableForWebsite(parentEditor().localWebsite());
        }
    }
    

    
    /**
     * Returns the direct action URL for a Tools Interoperability Launch Request for this parentEditor().website().
     * 
     * @return the direct action URL for a Tools Interoperability Launch Request for this parentEditor().website()
     */
    public String siteAccessURL()
    {
        return parentEditor().website().tiLaunchRequestURL(context().request());
    }
    
    

    /* Generic setters and getters ***************************************/
    
    public RemoteParticipantGroup currentGroup() {
        return currentGroup;
    }
    public void setCurrentGroup(RemoteParticipantGroup aGroup) {
        currentGroup = aGroup;
    }

    public ConfigAccessGroup parentEditor() {
        return (ConfigAccessGroup) parent();
    }
    
    public SectionStyle aStyle() {
        return aStyle;
    }
    public void setAStyle(SectionStyle newAStyle) {
        aStyle = newAStyle;
    }

    public NSArray styles() {
        return styles;
    }
}