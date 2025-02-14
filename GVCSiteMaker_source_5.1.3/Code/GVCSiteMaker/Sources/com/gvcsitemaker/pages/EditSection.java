// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.pages;
import com.gvcsitemaker.core.interfaces.*;
import com.webobjects.appserver.*;

import er.ajax.*;


/**
 * <p>Extends EditSectionPublicly to enforce HTTPS access when editing of section types that don't support versioning
 * or editing from site viewing mode.</p>
 */
public class EditSection extends EditSectionPublicly implements SMSecurePage
{
    
    
    /**
     * Designated constructor
     */
    public EditSection(WOContext aContext)
    {
        super(aContext);
    }



}
