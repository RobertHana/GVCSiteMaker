// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.components;
import com.gvcsitemaker.appserver.Application;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * Displays a notice stating that "The contents of this page originate from a different site: <link to site>".  This was done for SCR 316 for UM.  Many other installations have complained about this and so it can be deactivated by setting the configuration item ShowSectionSourceNotice.  There are also several pending SCRs for making this message conditional on site owner ship and configuration settings.
 */
public class SectionSourceNotification extends WOComponent
{


    /**
     * Designated constructor.
     */
    public SectionSourceNotification(WOContext context)
    {
        super(context);
    }


    
    /**
     * Returns true, this component is stateless.
     *
     * @return true, this component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }

    

    /**
     * Returns false, this component uses the ^ notation.
     *
     * @return false, this component uses the ^ notation.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }


    
    /**
     * Returns true if the notice should be displayed, false if not.  This is a cover method for the configuration item ShowSectionSourceNotice.
     *
     * @return true if the notice should be displayed, false if not.
     */
    public boolean shouldShowNotification()
    {
        return ((Application)application()).properties().booleanPropertyForKey("ShowSectionSourceNotice");
    }

    
}
