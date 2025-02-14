// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.interfaces;

import com.webobjects.foundation.*;

public interface Deleteable {

    public static final String DELETE_NOT_PERMITTED_ERROR_KEY = "deleteNotPermittedErrorKey";
    public boolean canBeDeleted();
    public NSArray errorMessages();

}
