// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.custom.appserver;
import com.gvcsitemaker.core.appserver.SMDirectAction;

import com.webobjects.appserver.WORequest;


/**
 * Add site specific direct action functionality here.
 */
public class SMCustomDirectAction extends SMDirectAction 
{
    public SMCustomDirectAction(WORequest aRequest)
    {
        super(aRequest);
    }

}
