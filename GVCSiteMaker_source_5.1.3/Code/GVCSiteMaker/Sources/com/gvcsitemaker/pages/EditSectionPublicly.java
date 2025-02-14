// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.SectionVersion;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;

/**
 * <p>This class is used to handle the editing from site viewing mode of section types that can be versioned.
 * It shows the correct editor component on this page. The type of component editor to show is determined by the section type.
 * The editors can be found in the <code>Editors.subproj</code>. A call to <code>setSection</code> should be made before the page is displayed.</p>
 *
 * <p>When editing from site viewing mode, setReturnUrl should be called to
 * set the URL to return (redirect to) when the editing process is completed.</p>
 */
public class EditSectionPublicly extends com.gvcsitemaker.core.components.WebsiteContainerBase
{


    protected Section section;  // The Section being edited.
    protected SectionVersion version;  // The version being edited.
    protected String returnUrl;
    protected WOElement editSectionTemplate;


    /**
     * Designated constructor
     */
    public EditSectionPublicly(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * The template for this page is the same as for the EditSection page.  As that page is used more often, the
     * template was left there and manually constructed here.
     */
    public WOElement template()
    {
        if (editSectionTemplate == null)
        {
            editSectionTemplate = templateWithHTMLString(new String(application().resourceManager().bytesForResourceNamed("EditSection.wo/EditSection.html", null, session().languages())),
                                                         new String(application().resourceManager().bytesForResourceNamed("EditSection.wo/EditSection.wod", null,  session().languages())),
                                                         session().languages());
        }

        return editSectionTemplate;
    }



    /**
     * If the editorName is "EmbeddedSiteEditor" or "EmbeddedSectionEditor" returns false, true otherwise.
     * Those editors don't need the preview JavaScript.
     *
     * @return true if the editorName is "EmbeddedSiteEditor" false otherwise
     */
    public boolean showPreviewURLJavaScript()
    {
        return ! (section().type().editorName().equals("EmbeddedSiteEditor") ||
                  section().type().editorName().equals("EmbeddedSectionEditor"));
    }


    /* Generic setters and getters ***************************************/
    public Section section() {
        return section;
    }
    public void setSection(Section newSection ) {
        section = newSection;
    }

    /**
     * @return the URL to return to if reached from site viewing mode.
     */
    public String returnUrl() {
        return returnUrl;
    }
    public void setReturnUrl(String newUrl ) {
        returnUrl = newUrl;
    }
    public boolean inPlaceEditingMode() {
        return returnUrl() != null;
    }

    public SectionVersion version() {
        return version;
    }
    public void setVersion(SectionVersion newVersion) {
        version = newVersion;
    }



}
