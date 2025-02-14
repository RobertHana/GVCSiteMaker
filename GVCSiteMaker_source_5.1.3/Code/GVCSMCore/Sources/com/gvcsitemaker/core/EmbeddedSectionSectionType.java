/**
 * Implementation of EmbeddedSectionSectionType common to all installations.
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


public class EmbeddedSectionSectionType extends _EmbeddedSectionSectionType
{

    /**
     * Returns the singleton instance of EmbeddedSectionSectionType in the passed EOEditingContext
     */
    public static EmbeddedSectionSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (EmbeddedSectionSectionType) getInstance(ec, "EmbeddedSectionSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of EmbeddedSectionSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of EmbeddedSectionSectionType or a subclass.
     */
    public static EmbeddedSectionSectionType newEmbeddedSectionSectionType()
    {
        return (EmbeddedSectionSectionType) SMEOUtils.newInstanceOf("EmbeddedSectionSectionType");

        /** ensure [result_not_null] Result != null; **/
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
        return ! ((EmbeddedSection) aSection.component()).hasTargetBeenDeleted();
    }



    /**
     * Returns the Website that the passed Section is linked to.
     *
     * @param aSection - the Section to return the linked site for.
     * @returns the Website that this Section is linked to.
     */
    public Website linkedSite(Section aSection)
    {
        return ((EmbeddedSection) aSection.component()).linkedWebsite();
    }



    /**
     * Returns the Section that the passed Section is linked to.
     *
     * @param aSection - the Section to return the linked section for
     * @returns the Section that this Section is linked to
     */
    public Section linkedSection(Section aSection)
    {
        return ((EmbeddedSection)aSection.component()).linkedSection();
    }



    /**
     * Returns false as Embedded Sections cannot be emebedded in another Section.
     *
     * @returns false as Embedded Sections cannot be emebedded in another Section.
     */
    public boolean canBeEmbedded()
    {
        return false;
    }



    /**
     * Returns false as Embedded Sections can not be home Sections
     *
     * @returns false as Embedded Sections can not be home Sections
     */
    public boolean canBeHomeSection(Section aSection)
    {
        /** require [section_is_valid] aSection != null;   **/
        return false;
    }



    /**
     * Returns <code>true</code> if this aUser has permission to view the Section which is embedded.
     *
     * @param aSection the Section to check view permission for
     * @param aUser the User to check against
     *
     * @return <code>false</code> if this aUser has permission to view the Section which is embedded
     */
    public boolean isViewableByUser(Section aSection, User aUser)
    {
        /** require
        [valid_section] aSection != null;
        [valid_user] aUser != null;
        [same_ec] aSection.editingContext().equals(aUser.editingContext()); **/

        // If the embedded Section has been deleted, grant permission to view so that the user sees the "has been deleted" message.  Otherwise, defer to the Embedded Section.
        boolean canViewEmbeddedSection = ( ! hasLinkedSite(aSection)) ||
                                         linkedSection(aSection).isViewableByUser(aUser);

        return canViewEmbeddedSection;
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
     * Returns <code>true</code> if aSection requires access to be via SSL.
     *
     * @return <code>true</code> if aSection requires access to be via SSL
     */
    public boolean requiresSSLConnection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/

        return ! (aSection.isPublic() && hasLinkedSite(aSection) && linkedSection(aSection).isPublic());
    }



    /**
     * Returns <code>true</code> if aSection requires access to be via SSL.
     *
     * @return <code>true</code> if aSection requires access to be via SSL
     */
    public boolean requiresPublicHttpsAccess(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/

        return (hasLinkedSite(aSection) && linkedSection(aSection).requiresPublicHttpsAccess());
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
        return hasLinkedSite(section) ? linkedSection(section).mimeType() : section.mimeType();
        /** ensure [valid_result] Result != null;  **/
    }




}

