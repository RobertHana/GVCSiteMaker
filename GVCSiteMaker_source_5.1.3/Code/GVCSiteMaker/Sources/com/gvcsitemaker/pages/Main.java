// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.support.SMActionURLFactory;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * Default Main page and core Main page functionality.  This page is intended to be subclassed or overridden in GVCSMCustom.
 */
public class Main extends WOComponent 
{
  

    public Main(WOContext aContext)
    {
        super(aContext);

        System.out.println("### Main Constructor!");
    }


    public String adminLoginUrl()
    {
        return SMActionURLFactory.adminMainPageURL();
    }
}  


