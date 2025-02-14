// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.support.SectionDisplay;

import com.webobjects.appserver.WOContext;


/**
 * Displays a directly embedded Section, or the home Section of an embedded Website.  These Sections are handled specially so that error messages can be displayed when the embedded site is deleted or not published.  These only need to be detected for directly embedded Sections and the home Section of an embedded Website. and displaying a notice about the origin of the Section's contents.  It also displays a notice about the origin of the Section's contents.

 */
public class EmbeddedSite extends ComponentPrimitive
{


    /**
     * Designated constructor
     */
    public EmbeddedSite(WOContext context)
    {
        super(context);
    }



    /**
     * Returns the EmbeddedSite PageComponent for this Section properly cast.
     *
     * @return the EmbeddedSite PageComponent for this Section properly cast.
     */
    public com.gvcsitemaker.core.pagecomponent.InternalLink internalLinkComponent()
    {
        /** require [has_componentObject] componentObject() != null; **/
        return (com.gvcsitemaker.core.pagecomponent.InternalLink) componentObject();
        /** ensure [result_valid] Result != null; **/
    }


    
    /**
     * Returns true if the contents of this Section can be displayed.  The contents cannot be displayed if the Website containing this Section has been deleted or is not published.
     *
     * @return true if the contents of this Section can be displayed.
     */
    public boolean canDisplaySection()
    {
        return ! ( hasTargetBeenDeleted() || isUnpublishedSite());
    }

    

    /**
     * Returns true if the Website containing this Section has been deleted.
     *
     * @return true if the Website containing this Section has been deleted.
     */
    public boolean hasTargetBeenDeleted()
    {
        return  internalLinkComponent().hasTargetBeenDeleted();
    }



    /**
     * Returns true if the Website containing this Section is not published unless overidden by the "config" form value.  This overridding form value is put in by the configuration pages to allow sections of undisplayable sites to be previewed before publication.  It returns false if the Website has been deleted as that condition takes priority.
     *
     * @return true if the Website containing this Section is not published.
     */
    public boolean isUnpublishedSite()
    {
        return ! (hasTargetBeenDeleted() ||
                  SectionDisplay.canSiteBeViewedForRequest(internalLinkComponent().linkedWebsite(), context().request()));
    }


}
