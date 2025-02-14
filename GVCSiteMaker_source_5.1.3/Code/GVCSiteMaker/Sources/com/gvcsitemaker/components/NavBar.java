// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.components;

import com.gvcsitemaker.core.ExternalURLSectionType;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.components.PageScaffold;
import com.gvcsitemaker.core.support.SMActionURLFactory;
import com.gvcsitemaker.core.support.SMSectionHierarchy;
import com.gvcsitemaker.core.support.SMSectionHierarchy.SectionDepth;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;


/**
 * Concrete base class for Navigation Bars.  This is used with PageScaffold to display navigation 
 * links for the sections of the Website being displayed.  This class displayes the links as a vertical, 
 * indented list of lists.
 */
public class NavBar extends WOComponent
{
    protected SectionDepth aSection;					// Current item in WORepetition iterating over sectionHierarchy

    protected static final String PADDING_CHARS = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    protected static final int PADDING_LENGTH = "&nbsp;&nbsp;".length();
    
    
    /**
     * Designated constructor
     */
    public NavBar(WOContext aContext)
    {
        super(aContext);
    }

    

    /**
     * Returns <code>true</code> if the current section name being rendered is the section being displayed 
     * on this page.  This is used to render the link in the NavBar as a string rather than a hyperlink.  
     *
     * @return <code>true</code> if the current section being rendered is the section being displayed on this page.
     *
     */
    public boolean isViewingSection()
    {
        return currentSection() == section();
    }


  
    /**
     * Returns the spacer string to display before the section name.  
     *
     * @return the HTML spacers to display before Section names of embedded Websites in a vertical list.
     */
    public String getNameSpacers()
    {
        /** require [a_section_not_null] aSection() != null; **/
        return aSection().isNested() ? PADDING_CHARS.substring(0, aSection().depth() * PADDING_LENGTH) : "";
        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
     * Returns URL of to show either a section of a site, or a section of a site embedded within another site.  See SMActionURLFactory.sectionURL(Website, Section, WORequest) for more details.
     *
     * @return URL of to show either a section of a site, or a section of a site embedded within another site.
     */
    public String urlForSection()
    {
        /** require
        [website_not_null] website() != null;
        [a_section_not_null] section() != null;
        [context_request_not_null] context().request() != null;  **/
        
        return SMActionURLFactory.sectionURL(website(), section(), context().request());

        /** ensure [result_not_null] Result != null; **/
    }


    
    /**
     * Returns true if section is an External URL section.
     *
     * TODO: Can this special handling be refactored away?  Part of problem is the title for External URL vs normal sections which itself needs to be fixed.  The other part of the problem is the _target.  Move this and urlForSection above to SectionType?
     */
    public boolean isExternalURL()
    {
        return section().type() instanceof ExternalURLSectionType;
    }



    /**
     * Returns true if section is an Embedded Site which is redirected.
     *
     * TODO: Can this special handling be refactored away?
     */
    public boolean isRedirectedEmbeddedSite()
    {
        return (section().hasLinkedSite() && section().linkedSite().isRedirected());
    }



    /**
     * @return the Section from the SectionDepth referenced in aSection.
     */
    public Section section() {
        return aSection.section();
    }
    

    /* Generic setters and getters ***************************************/

    public PageScaffold pageScaffold() {
        return (PageScaffold) parent();
    }
    
    public Website website() {
        return pageScaffold().website();
    }

    public SectionDepth aSection() {
        return aSection;
    }
    public void setASection(SectionDepth newASection) {
        aSection = newASection;
    }

    public Section currentSection() {
        return pageScaffold().sectionToDisplay();
    }

    public SMSectionHierarchy sectionHierarchy() {
        return pageScaffold().sectionHierarchy();
    }

}
