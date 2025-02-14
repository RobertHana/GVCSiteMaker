/**
 * Implementation of EmbeddedSiteSectionType common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class EmbeddedSiteSectionType extends _EmbeddedSiteSectionType
{


    /**
     * Returns the singleton instance of EmbeddedSiteSectionType in the passed EOEditingContext
     */
    public static EmbeddedSiteSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (EmbeddedSiteSectionType) getInstance(ec, "EmbeddedSiteSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of EmbeddedSiteSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of EmbeddedSiteSectionType or a subclass.
     */
    public static EmbeddedSiteSectionType newEmbeddedSiteSectionType()
    {
        return (EmbeddedSiteSectionType) SMEOUtils.newInstanceOf("EmbeddedSiteSectionType");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
    * Returns the sections immediately below the passed Section in the hierarchy of sections, if any.  These sections can come from linked sites, child sections etc.  Returns an empty array if there are no sub-sections.
     *
     * @param aSection - the Section to return subsections for.
     * @returns the sections immediately below the passed Section in the hierarchy of sections, if any.
     */
    public NSArray subSections(Section aSection)
    {
        return ((EmbeddedSite)aSection.component()).embeddedSections();

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns true if the passed Section is a link to another Website.
     *
     * @param aSection - the Section to check for a linked site.
     * @returns true if the passed Section is a link to another Website.
     */
    public boolean hasLinkedSite(Section aSection)
    {
        /** require [section_is_valid] aSection != null; **/

        return ! ((EmbeddedSite) aSection.component()).hasTargetBeenDeleted();
    }



    /**
     * Returns the Website that the passed Section is linked to.
     *
     * @param aSection - the Section to return the linked site for.
     * @returns the Website that this Section is linked to.
     */
    public Website linkedSite(Section aSection)
    {
        return ((EmbeddedSite) aSection.component()).linkedWebsite();
    }



    /**
     * Returns false as Embedded Sites cannot be emebedded in another Section.
     *
     * @returns false as Embedded Sites cannot be emebedded in another Section.
     */
    public boolean canBeEmbedded()
    {
        return false;
    }



    /**
     * Returns false as Embedded Sites can not be home Sections
     *
     * @returns false as Embedded Sites can not be home Sections
     */
    public boolean canBeHomeSection(Section aSection)
    {
        /** require [section_is_valid] aSection != null;   **/
        return false;
    }



    /**
     * Returns <code>false</code> as emebedded section types take access protection from what is embedded.
     *
     * @param aSection - the Section to check type of access protection for
     * @return <code>false</code> as emebedded section types take access protection from what is embedded
     */
    public boolean usesAccessProtection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/
        return false;
    }



    /**
     * Returns <code>true</code> if this aUser has permission to view the home section of the Website which is embedded.
     *
     * @param aSection the Section to check view permission for
     * @param aUser the User to check against
     *
     * @return <code>false</code> if this aUser has permission to view the home section of the Website which is embedded
     */
    public boolean isViewableByUser(Section aSection, User aUser)
    {
        /** require
        [valid_section] aSection != null;
        [valid_user] aUser != null;
        [same_ec] aSection.editingContext().equals(aUser.editingContext()); **/

        // If the embedded site has been deleted, grant permission to view so that the user sees the
        // "has been deleted" message.  Otherwise, defer to the home section of the embedded site.
        boolean canViewEmbeddedSection = ( ! hasLinkedSite(aSection)) ||
                                         linkedSite(aSection).homeSection().isViewableByUser(aUser);

        return canViewEmbeddedSection;
    }



    /**
     * Returns the MIME type of the linked section if it has not been deleted.  Returns section.mimeType() if it has
     * been deleted.
     *
     * @see com.gvcsitemaker.core.Section#mimeType()
     * @see SectionType#mimeType(Section)
     *
     * @param section the Section to return the MIME type for
     * @return the MIME type to use when displaying this section
     */
    public String mimeType(Section section)
    {
        return hasLinkedSite(section) ? linkedSite(section).homeSection().mimeType() : section.mimeType();
        /** ensure [valid_result] Result != null;  **/
    }

}

