// Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.


package com.gvcsitemaker.utility.pages;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;


/**
 * Loads LDAPServer information from bootstrap files.  Used when upgrading an 
 * existing installation.
 */
public class LoadLDAPServers extends WOComponent 
{

    public LoadLDAPServers(WOContext context) 
    {
        super(context);
    }



    public WOComponent loadLDAPServers() 
    {
        EOEditingContext ec = ((SMSession)session()).newEditingContext();
        
        try
        {
            if (LDAPBranch.orderedObjects(ec).count() == 0)
            {
                InitDatabase.loadLDAPServers(ec);
                ec.saveChanges();
            }
        }
        finally
        {
            ((SMSession)session()).unregisterEditingContext(ec);
        }
        
        return pageWithName("Main");
    }

}
