// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import net.global_village.woextensions.components.*;

import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * GVCSiteMaker version of component which binds directly to application properties.
 */
public class SMCSSComponent extends CSSComponent
{


    public SMCSSComponent(WOContext context)
    {
        super(context);
    }
   

    /**
     * Returns the dictionary of style sheets.
     *
     * @return the dictionary of style sheets
     */
    protected NSDictionary styleDictionary()
    {
        return SMApplication.appProperties().dictionaryPropertyForKey("StyleSheets");

        /** ensure [valid_result] Result != null; **/
    }


}
