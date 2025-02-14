// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
// This component is being phased out due to Alann's redesign....

package com.gvcsitemaker.admin.commonwidgets;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;

public class BodyTitle extends WOComponent
{

    protected String bodyTitle;


    public BodyTitle(WOContext aContext)
    {
        super(aContext);
    }



    public boolean isStateless()
    {
        return true;
    }

    public String bodyTitle()
    {
        return SMApplication.productName() + " " + valueForBinding("bodyTitle");
    }
}
