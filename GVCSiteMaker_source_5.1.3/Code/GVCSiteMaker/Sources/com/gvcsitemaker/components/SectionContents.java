// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.

package com.gvcsitemaker.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.webobjects.appserver.*;


/*
 * * SectionContents displays the actual contents of a Website's Section within
 * the framework of a PageScaffold. It also displays a notice about the origin
 * of the Section's contents if they come from another Website.
 */
public class SectionContents extends WOComponent
{

    protected Section currentSection; // The Section whose contents are being displayed.
    protected SectionVersion currentVersion; // The Section whose contents are being displayed.


    /**
     * Designated constructor
     */
    public SectionContents(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Returns the website that our parent (PageScaffold) is displaying.  This section is being displayed as part of this website, though it may not actually belong to it (in the case of a foreign section).
     *
     * @return	the website that our parent (PageScaffold) is displaying.
     */
    public Website website()
    {
        return ((PageScaffold) parent()).website();
        /** ensure [result_valid] Result != null;  **/
    }



    /**
     * Returns true if the SectionSourceNotification should be displayed.  This is done for "foreign sections" where currentSection() is not directly contained by website().  Regardless of where currentSection() comes from, this returns false in the special case that currentSection() is of is EmbeddedSiteSectionType or EmbeddedSectionSectionType so that this message can be displayed for the correct (embedded) site by EmbeddedSite / EmbeddedSection.
     *
     * @return true if the SectionSourceNotification should be displayed.
     */
    public boolean showSectionSourceNotification()
    {
        return !(website().sections().containsObject(currentSection()) || (currentSection().type() instanceof EmbeddedSiteSectionType) || (currentSection()
                .type() instanceof EmbeddedSectionSectionType));
    }


    public boolean shouldShowVersion()
    {
        return currentVersion() != null;
    }



    public PageComponent currentVersionComponent()
    {
        if (currentSection().component() instanceof MixedMediaPaneArrangement)
        {
            return currentSection.component();
        }
        return currentVersion.component();
    }



    /* Generic setters and getters ***************************************/

    public Section currentSection()
    {
        return currentSection;
    }


    public void setCurrentSection(Section newCurrentSection)
    {
        currentSection = newCurrentSection;
    }


    public SectionVersion currentVersion()
    {
        return currentVersion;
    }


    public void setCurrentVersion(SectionVersion newCurrentSectionVersion)
    {
        currentVersion = newCurrentSectionVersion;
    }

}
