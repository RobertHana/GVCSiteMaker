/**
 * Implementation of InternalLink common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.*;


/**
 * InternalLink is the superclass for Page Components which represent links to resources in another GVC.SiteMaker Website.  The linked resources can be a Section, entire Website etc.  
 */ 
public class InternalLink extends _InternalLink
{


    /**
     * Returns <code>true</code> if the target (whatever is linked to in another site) has been deleted.
     *
     * @return <code>true</code> if the target (whatever is linked to in another site) has been deleted.
     */
    public boolean hasTargetBeenDeleted()
    {
        return true;
    }



    /**
     * Returns the Website which contains target resource being linked by this PageComponent.
     *
     * @return the Website which contains target resource being linked by this PageComponent.
     */
    public Website linkedWebsite()
    {
        return null;


        /** ensure
        [target_deleted_or_returns_website] ((hasTargetBeenDeleted() && (Result == null)) ||
                                             (Result != null) );
        **/
    }



    //************** Binding Cover Methods **************\\


}

