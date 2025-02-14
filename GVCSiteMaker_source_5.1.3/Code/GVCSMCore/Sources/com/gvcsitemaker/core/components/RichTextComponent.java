// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * The component that provides a Rich Text Editor for editing text. New Rich
 * Text components should inherit from this component.
 */
public class RichTextComponent extends SMWOTextComponent
{
    /**
     * Designated constructor
     */
    public RichTextComponent(WOContext context)
    {
        super(context);
    }



    /**
     * The component name of the RichTextEditor to use for this installation
     */
    public String richTextComponentName()
    {
        return ((SMApplication) application()).properties().stringPropertyForKey("RichTextEditor");

        /** ensure [result_not_null] Result != null; **/
    }



}
