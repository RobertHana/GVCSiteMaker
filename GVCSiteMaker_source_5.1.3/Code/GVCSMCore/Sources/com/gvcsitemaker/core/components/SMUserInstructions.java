// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;


/**
 * This serves only to hold the instructions which appear on the ConfigAccessGroups page when adding a user to an access group.  It is here in a component so that it can be replaced on an installation by installation basis if the generic version is not suitable.
 */
public class SMUserInstructions extends WOComponent
{

    public Group internalUsersGroup;

    /**
     * Designated constructor
     */
    public SMUserInstructions(WOContext context)
    {
        super(context);
        internalUsersGroup = InternalUsersGroup.group(context.session().defaultEditingContext());
    }


}
