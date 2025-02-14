// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.interfaces;

import com.gvcsitemaker.core.*;


/**
 * Interface for pages and components that contain a Website for display or editing.  This just defines the interface for getting and setting the contained Website.  This was created as an alternative to storing the Website in the session.
 */
public interface WebsiteContainer 
{
    /**
     * Returns the Website being displayed / edited.  It may be possible for this to be null.
     *
     * @return the Website being displayed / edited.
     */
    public Website website();



    /**
     * Sets the Website to be displayed / edited.
     *
     * @param the Website to be displayed / edited.
     */
    public void setWebsite(Website newWebsite);

}
