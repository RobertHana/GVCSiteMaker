// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.webobjects.appserver.WOContext;


/**
 * This is used to display the components in a TextImageLayoutComponent sub-class of PageComponent.  Unlike the other sub-classes it does not have an edit mode as editing of a TextImageLayoutComponent (i.e. re-ordering the components) is handled by TextImageEditor.
 */
public class TextImageLayout extends Layout 
{

    /**
     * Designated constructor.
     */
    public TextImageLayout(WOContext aContext)
    {
        super(aContext);
    }


}
