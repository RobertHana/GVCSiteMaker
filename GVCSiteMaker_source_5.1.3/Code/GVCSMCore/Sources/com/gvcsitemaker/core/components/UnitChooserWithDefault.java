// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.appserver.*;


/**
 * This is exactly the same as UnitChooser, except that it also supports the noSelectionString.  It was not possible to do this in UnitChooser as defining a binding for noSelectionString, even if it returns null, still creates an empty default value at the top of the list.
 */
public class UnitChooserWithDefault extends UnitChooser
{
    protected String noSelectionString;  


    public UnitChooserWithDefault(WOContext aContext)
    {
        super(aContext);
    }



    public String noSelectionString()
    {
        return noSelectionString;
    }


    public void setNoSelectionString(String newNoSelectionString)
    {
        noSelectionString = newNoSelectionString;
    }


}
