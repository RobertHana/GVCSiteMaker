// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.appserver;

import com.webobjects.appserver.WORequest;


/**
 * <p>This is an alias for DirectAction which is used in rewrite rules to work with the 
 * Location directive in Apache and external Web Single Sign On services so that
 * we can get encrypted (HTTPS) requests that are not authenticated.</p>
 * 
 * <p>See the RedirectPath and RequireHTTPSAccessForPublicSections config options, 
 * SMActionURLFactory, and Section for further details.</p>
 */
public class Encrypt extends DirectAction 
{

    public Encrypt(WORequest request) {
        super(request);
    }

}
