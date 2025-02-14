/**
 * Implementation of SectionType common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core;

import java.util.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.virtualtables.*;



/**
 * Section Type defines a particular type of Section in terms of it's UI name, how it is created, and how it is edited.
 */
public class SectionType extends _SectionType
{


    /**
     * Factory method to create new instances of SectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SectionType or a subclass.
     */
    public static SectionType newSectionType()
    {
        return (SectionType) SMEOUtils.newInstanceOf("SectionType");

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns all of the SectionTypes ordered by order.
     *
     * @return all of the SectionTypes ordered by order.
     */
    public static NSArray orderedSectionTypes(EOEditingContext ec)
    {
        /** require [ec_is_valid] ec != null;  **/

        NSArray orderByOrder = new NSArray(EOSortOrdering.sortOrderingWithKey("order", EOSortOrdering.CompareAscending));
        EOFetchSpecification fetchSpec = new EOFetchSpecification("SectionType", null, orderByOrder);
        NSArray orderedSectionTypes = ec.objectsWithFetchSpecification(fetchSpec);

        return orderedSectionTypes;

        /** ensure  [return_is_valid] Result != null;     **/
    }



    /**
     * Returns the singleton instance of the SectionType sub-class for the EOEntity named anEntityName in the passed EOEditingContext.
     *
     * @param ec - the editing context to return the SectionType instance in
     * @param anEntityName - the EOEntity name of the desired SectionType sub-class
     * @return the singleton instance of the SectionType sub-class
     */
    protected static SectionType getInstance(EOEditingContext ec, String anEntityName)
    {
        /** require
        [ec_not_null] ec != null;
        [anEntityName_not_null] anEntityName != null;
        **/
        NSArray matchingTypes = EOUtilities.objectsForEntityNamed(ec, anEntityName);

        if (matchingTypes.count() != 1)
        {
            throw new RuntimeException("Database corrupt, " + anEntityName + " has no or many instances.");
        }

        DebugOut.println(15, "Returning " + matchingTypes.lastObject().getClass());
        return (SectionType) matchingTypes.lastObject();
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * SectionTypes is essentially a lookup value and is copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this SectionType
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Sets the Section's type and creates and attaches to the section the PageComponent sub-class
     * and children which implement Sections of this type.
     *
     * @param aSection the Section to attach the page components to
     */
    public void createComponents(Section aSection)
    {
        /** require
        [aSection_is_valid] aSection != null;
        [aSection_is_in_ec]  aSection.editingContext() != null;
        **/

        DebugOut.println(30, "Creating section type: " + name() + " with PageComponent " + pageComponentEntity());

        PageComponent newComponent = (PageComponent) SMEOUtils.newInstanceOf(pageComponentEntity());
        aSection.editingContext().insertObject(newComponent);

        aSection.setType(this);
        aSection.addObjectToBothSidesOfRelationshipWithKey(newComponent, "component");

        /** ensure [type_set] aSection.type().equals(this);
                   [component_set] aSection.component() != null;      **/
    }



    /**
     * Returns the URL for the passed Section customized for the request.  This method should NOT be used by client code.  Use one of the sectionURL() methods on Section instead.
     *
     * @param aSection - the Section to return the URL for.
     * @param aRequest - the request to take the domain name from
     *
     * @return the URL for the passed Section customized for the request.
     */
    public String urlForSection(Section aSection, WORequest aRequest)
    {
        /** require [aSection_is_valid] aSection != null; **/
        return aSection.standardURL(aRequest);
        /** ensure [return_is_valid] Result != null; **/
    }



    /**
    * Returns the sections immediately below the passed Section in the hierarchy of sections, if any.  These sections can come from linked sites, child sections etc.  Returns an empty array if there are no sub-sections.
     *
     * @param aSection - the Section to return subsections for.
     * @returns the sections immediately below the passed Section in the hierarchy of sections, if any.
     */
    public NSArray subSections(Section aSection)
    {
        /** require [section_is_valid] aSection != null; **/

        // At present this is only implemented for EmbeddedSites, but can be extended to cover child sections as well.
        return NSArray.EmptyArray;

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

        return false;
    }



    /**
     * Returns the Website that the passed Section is linked to.
     *
     * @param aSection - the Section to return the linked site for.
     * @returns the Website that this Section is linked to.
     */
    public Website linkedSite(Section aSection)
    {
        /** require
        [section_is_valid] aSection != null;
        [has_linked_site] hasLinkedSite(aSection);
        **/

        return null;

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns true if Sections of this type can be emebedded in another Section.
     *
     * @returns true if Sections of this type can be emebedded in another Section.
     */
    public boolean canBeEmbedded()
    {
        return true;
    }



    /**
     * Returns true if Sections of this type can be displayed in a MixedMedia Section.
     *
     * @returns true if Sections of this type can be displayed in a MixedMedia Section.
     */
    public boolean canBeDisplayedInMixedMediaSection()
    {
        return true;
    }



    /**
     * Returns true if Sections of this type can be a Website's home section.
     *
     * @returns true if Sections of this type can be a Website's home section
     */
    public boolean canBeHomeSection(Section aSection)
    {
        /** require [section_is_valid] aSection != null;   **/
        return true;
    }



    /**
     * Returns <code>true</code> if Sections of this type can be modified in the Configuration pages.
     *
     * @return <code>true</code> if Sections of this type can be modified in the Configuration pages.
     */
    public boolean canBeModified()
    {
        return true;
    }



    /**
     * Returns <code>true</code> if Sections of this type can be deleted in the Configuration pages.
     *
     * @return <code>true</code> if Sections of this type can be deleted in the Configuration pages.
     */
    public boolean canBeDeleted()
    {
        return  true;
    }



    /**
     * Returns <code>true</code> if Sections of this type is required to have a component.
     *
     * @return <code>true</code> if Sections of this type is required to have a component.
     */
    public boolean isComponentRequired()
    {
        return  true;
    }



    /**
     * Returns <code>true</code> if Sections of this type can be versioned. Defaults to false.
     *
     * @return <code>true</code> if Sections of this type can be versioned.
     */
    public boolean isVersionable()
    {
        return false;
    }



    /**
     * Returns <code>true</code> if Sections of this type supports editors.
     *
     * @return <code>true</code> if Sections of this type supports editors.
     */
    public boolean supportsEditors()
    {
        return false;
    }



    /**
     * Returns <code>true</code> if Sections of this type supports contributors.
     *
     * @return <code>true</code> if Sections of this type supports contributors.
     */
    public boolean supportsContributors()
    {
        return false;
    }



    /**
     * Returns <code>true</code> if this Section uses passed Database Table.
     *
     * @param aSection - the Section to check Database Table usage for
     * @param aTable - the database table to check
     * @return <code>false</code> to indicate that this Section does not use a Database Table.
     */
    public boolean usesDatabaseTable(Section aSection, Table aTable)
    {
        /** require [valid_section] aSection != null;   [valid_table] aTable != null; **/
        return false;
    }



    /**
     * Returns <code>true</code> if this aUser has permission to view aSection.
     *
     * @param aSection the Section to check view permission for
     * @param aUser the User to check against
     *
     * @return <code>false</code> if this aUser has permission to view aSection
     */
    public boolean isViewableByUser(Section aSection, User aUser)
    {
        /** require
        [valid_section] aSection != null;
        [valid_user] aUser != null;
        [same_ec] aSection.editingContext().equals(aUser.editingContext()); **/

        boolean isViewableByUser = false;

        Enumeration groupEnum = aSection.groups().objectEnumerator();
        while(( ! isViewableByUser) && groupEnum.hasMoreElements())
        {
            isViewableByUser = ((Group)groupEnum.nextElement()).isMember(aUser);
        }

        return isViewableByUser;
    }



    /**
     * Returns <code>true</code> if access protection can be configured for this Section.
     *
     * @param aSection - the Section to check type of access protection for
     * @return <code>true</code> if access protection can be configured for this Section
     */
    public boolean usesAccessProtection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/
        return true;
    }



    /**
     * Returns <code>true</code> if aSection requires access to be via SSL.
     *
     * @return <code>true</code> if aSection requires access to be via SSL
     */
    public boolean requiresSSLConnection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/

        return ! aSection.isPublic();
    }



    /**
     * Returns <code>true</code> if aSection requires public access to be via SSL.
     *
     * @return <code>true</code> if aSection requires public access to be via SSL
     */
    public boolean requiresPublicHttpsAccess(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/

    	    return aSection.requireHttpsAccessForPublicSections().booleanValue();
    }



    /**
     * Returns <code>true</code> if this aSection references something outside of GVC.SiteMaker.
     *
     * @return <code>true</code> if this aSection references something outside of GVC.SiteMaker
     */
    public boolean isExternalReference(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/

        return false;
    }



    /**
     * Returns true if Sections of this type are valid Sections and not placeholders or temporary Sections (for queued tasks)
     *
     * @returns true if Sections of this type are valid Sections and not placeholders or temporary Sections (for queued tasks)
     */
    public boolean isPlaceHolder()
    {
        return false;
    }



    /**
     * Allows SectionType to determine the actual mime type.  This implementation just returns section.mimeType().
     *
     * @see com.gvcsitemaker.core.Section#mimeType()
     * @see EmbeddedSectionSectionType
     *
     * @param section the Section to return the MIME type for
     * @return the MIME type to use when displaying this section
     */
    public String mimeType(Section section)
    {
        return section.mimeType();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Ensure that two SectionTypes with the same type do not get created.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        // This code has a potential race condition, but as it is only used at bootstrap and conversion this should not be a serious issue.
        NSArray matchingTypes = EOUtilities.objectsWithQualifierFormat(editingContext(),
                                                                       "SectionType",
                                                                       "key caseInsensitiveLike %@",
                                                                       new NSArray(key()));
        if ((matchingTypes.count() > 0) && (matchingTypes.lastObject() != this))
        {
            exceptions.addObject(new NSValidation.ValidationException("SectionType already exists with key=" + key()));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }
}

