// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.interfaces;

import com.gvcsitemaker.core.*;


/**
 * This is used to add GVC.SiteMaker specific additions to net.global_village.virtualtables.Table and subclasses.  This is in place of MI or AOP.
 */
public interface SMTable
{

    public Website website();
    public com.gvcsitemaker.core.User createdBy();
    public void setCreatedBy(com.gvcsitemaker.core.User aValue);
    public com.gvcsitemaker.core.User modifiedBy();
    public void setModifiedBy(com.gvcsitemaker.core.User aValue);

}
