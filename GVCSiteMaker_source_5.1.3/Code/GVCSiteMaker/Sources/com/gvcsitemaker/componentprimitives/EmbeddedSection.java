// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.webobjects.appserver.WOContext;


/**
 * Displays a directly embedded Section.  These Sections are handled specially so that error messages can be displayed when the embedded site is deleted or not published.  It also displays a notice about the origin of the Section's contents.  This is exactly the same as EmbeddedSite.  There are two components as SectionType does not support two PageComponents having the same ComponentPrimitive.  The template is duplicated here from EmbeddedSite as, in WO 5.1.3, there is no easy way to access its template for inheritance.  To only way to get it is to use the deprecated WOResourceManager method pathForResourceNamed to get the path to EmbeddedSite.wo and then build the paths to EmbeddedSite.wod and EmbeddedSite.html, then read them in as files.  As this is highly unlikely to function in the next version, the template was copied instead.

 */
public class EmbeddedSection extends EmbeddedSite
{


    /**
     * Designated constructor
     */
    public EmbeddedSection(WOContext context)
    {
        super(context);
    }



}
