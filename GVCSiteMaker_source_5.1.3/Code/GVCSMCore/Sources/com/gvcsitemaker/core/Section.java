// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import java.util.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.eofvalidation.*;
import net.global_village.foundation.GVCBoolean;
import net.global_village.virtualtables.*;


public class Section extends _Section implements SMAccessible, EditingContextNotification, RTEListSelectable
{

    /** Indentation level indicating no indentation. */
    public static final int NOT_INDENTED = 0;


    /**
     * Context for EOCopying that indicates only the section (rather than the entire site) is being copied.
     * @see duplicate(NSMutableDictionary)
     */
    public static final String SECTION_ONLY_COPY_CONTEXT = "CopySectionOnly";



    /**
     * @param context copying context passed to duplicate
     * @return <code>true</code> if the passed context indicates that this is a Section only copy
     */
    public static boolean isSectionOnlyCopy(NSDictionary context)
    {
        /** require [context_not_null] context != null; **/
        return (context.objectForKey(EOCopying.COPY_CONTEXT) != null) && context.objectForKey(EOCopying.COPY_CONTEXT).equals(Section.SECTION_ONLY_COPY_CONTEXT);
    }


    /**
     * Factory method to create new instances of Section.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Section or a subclass
     */
    public static Section newSection()
    {
        return (Section) SMEOUtils.newInstanceOf("Section");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overidden to set defaults.
     *
     * @see com.webobjects.eocontrol.EOEnterpriseObject#awakeFromInsertion(com.webobjects.eocontrol.EOEditingContext)
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        if (isNavigable() == null)
        {
            setIsNavigable(net.global_village.foundation.GVCBoolean.trueBoolean());
        }

        if (isAutoVersioning() == null)
        {
           setIsAutoVersioning(new net.global_village.foundation.GVCBoolean(SMApplication.appProperties().booleanPropertyForKey("AutoVersioningEnabled")));
        }

        if (isVersioning() == null)
        {
           setIsVersioning(new net.global_village.foundation.GVCBoolean(SMApplication.appProperties().booleanPropertyForKey("VersioningEnabled")));
        }

        if (indentation() == null)
        {
            setIndentation(new Integer(0));
        }

        if (requireHttpsAccessForPublicSections() == null)
        {
            setRequireHttpsAccessForPublicSections(new net.global_village.foundation.GVCBoolean(SMApplication.appProperties().booleanPropertyForKey("RequireHTTPSAccessForPublicSections")));
        }

        if (mimeType() == null)
        {
            setMimeType(MIMEUtils.HTML_MIMETYPE);
        }

        allowAccessForGroup(PublicGroup.group(ec));

        /** ensure
             [is_public] isPublic();
             [navigable_set] isNavigable() != null;
             [versioning_set] isVersioning() != null;
             [auto_versioning_set] isAutoVersioning() != null;
             [indentation_set] indentation() != null;
             [mime_type_set] mimeType() != null;
             [requireHttpsAccessForPublicSections_set] requireHttpsAccessForPublicSections() != null;
        **/
    }



    /**
     * Returns a copy of a Section.  The copy is made manually because the embeddedSectionComponents relationship must not be copied as it only applies to the Section being copied.  If this relationship is copied, then Websites embedding this Section will also get dragged into the copy.
     *
     * @param copiedObjects the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this Section
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null; **/

        EOEnterpriseObject copy = EOCopying.Utility.newInstance(this);

        // Handle circular relationships by registering this object right away.
        EOGlobalID globalID = editingContext().globalIDForObject(this);
        copiedObjects.setObjectForKey(copy, globalID);

        EOCopying.Utility.copyAttributes(this, copy);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        if (Section.isSectionOnlyCopy(copiedObjects))
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("component"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("versions"));
            Section newSection = (Section) copy;
            newSection.setGroups(groups().mutableClone());
            newSection.setEditorGroups(editorGroups().mutableClone());
            newSection.setContributorGroups(contributorGroups().mutableClone());
            newSection.setSectionStyle(sectionStyle());
            newSection.setType(type());
            website().attachSection(newSection);
        }
        else
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("component"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("groups"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("editorGroups"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("contributorGroups"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("sectionStyle"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("type"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("website"));
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("versions"));
        }

        return copy;

        /** ensure
        [copy_made] Result != null;
        [embeddedSectionComponents_not_copied] ((Section)Result).embeddedSectionComponents().count() == 0; **/
    }



    /**
     * Returns the sections immediately below this Section in the hierarchy of sections, if any.  These sections can come from linked sites, child sections etc.  Returns an empty array if there are no sub-sections.
     *
     * @return the sections immediately below this Section in the hierarchy of sections, if any
     */
    public NSArray subSections()
    {
        return type().subSections(this);

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the Website that this Section is linked to.
     *
     * @return the Website that this Section is linked to
     */
    public Website linkedSite()
    {
        /** require [has_linked_site] hasLinkedSite(); **/

        return type().linkedSite(this);

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns true if this Section is a link to another Website.
     *
     * @return true if this Section is a link to another Website
     */
    public boolean hasLinkedSite()
    {
        return type().hasLinkedSite(this);
    }



    /**
     * Returns the URL for this Section in its own Website using http:// and the domain defined in GVCSiteMaker.plist as DomainName.  The URL for the home section is a special case, it is the same URL as for the Website.
     *
     * @return the URL for this Website Section using http:// and the domain defined in GVCSiteMaker.plist as DomainName
     */
    public String sectionURL()
    {
        return sectionURL(null);

        // TODO: this can be violated when an External URL section is created without a URL (yes, that can happen...) /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the URL for this Section in its own Website using http:// and the domain specified in the request.  The URL for the home section is a special case, it is the same URL as for the Website.
     *
     * @param aRequest the request to take the domain name from
     * @return the URL for this Website Section using http:// and the domain specified in the request
     */
    public String sectionURL(WORequest aRequest)
    {
        return type().urlForSection(this, aRequest);

        // TODO: this can be violated when an External URL section is created without a URL (yes, that can happen...) /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the standard URL for this Website Section.  This method should NOT be used by client code.
     *
     * @param aRequest the request to take the domain name from
     * @return the standard URL for this Website Section
     */
    public String standardURL(WORequest aRequest)
    {
        return SMActionURLFactory.sectionURL(website(), this, aRequest);

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the URL that returns this Section's contents.  Compared to sectionURL(), the contents returned by this URL does not include the other page borders like the headers, menu, navbar, etc, it just returns the contents.
	 *
     * @param aRequest the request to take the domain name from
     * @return the URL that returns this Section's contents
     */
    public String sectionContentsURL(WORequest aRequest)
    {
        return SMActionURLFactory.sectionContentsURL(website(), this, aRequest);

        /** ensure [return_not_null] Result != null; **/
    }



    public void setName(String value) {
        takeStoredValueForKey(value, "name");
        if (value != null)
            this.setNormalName(URLUtils.toLowerAndNormalize(value));
    }


    /**
     * Returns the Section's name followed by the Section Type's name in parenthesis.  e.g.  Home (Text / Image)
     *
     * @return the Section's name followed by the Section Type's name in parenthesis
     */
    public String nameAndType()
    {
        return name() + " (" + type().name() + ")";

        /** ensure [return_is_valid] Result != null;  **/
    }

    public void swapOrderWithSection(Section aSection) {
        Number tmpOrder = aSection.sectionOrder();
        aSection.setSectionOrder(this.sectionOrder());
        this.setSectionOrder(tmpOrder);
    }



    /**
     * Returns <code>true</code> if this Section references something outside of GVC.SiteMaker.
     *
     * @return <code>true</code> if this Section references something outside of GVC.SiteMaker
     */
    public boolean isExternalReference()
    {
        return type().isExternalReference(this);
    }


    /**
     * SMAccessible method.  Returns <code>true</code> if this Section allows public access.
     *
     * @return <code>true</code> if this Section allows public access.
     */
    public boolean isPublic()
    {
        return hasPublicGroup(groups());
    }



    /**
     * Returns <code>true</code> if this Section allows public to edit it.
     *
     * @return <code>true</code> if this Section allows public to edit it.
     */
    public boolean isEditingPublic()
    {
        return hasPublicGroup(editorGroups());
    }



    /**
     * Returns <code>true</code> if this Section allows public to contribute to it.
     *
     * @return <code>true</code> if this Section allows public to contribute to it.
     */
    public boolean isContributionPublic()
    {
        return hasPublicGroup(contributorGroups());
    }




    /**
     * Returns <code>true</code> if the passed groups has the PublicGroup.
     *
     * @return <code>true</code> if the passed groups has the PublicGroup.
     */
    public boolean hasPublicGroup(NSArray groups)
    {
        boolean hasPublicGroup = false;

        Enumeration groupEnum = groups.objectEnumerator();
        while(groupEnum.hasMoreElements())
        {
            Group someGroup = (Group) groupEnum.nextElement();
            if (someGroup instanceof PublicGroup)
            {
                hasPublicGroup = true;
                break;
            }
        }

        return hasPublicGroup;
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if this aUser has permission to view  this Section.
     *
     * @param aUser the User to check against
     *
     * @return <code>false</code> if this aUser has permission to view aSection
     */
    public boolean isViewableByUser(User aUser)
    {
        /** require  [valid_user] aUser != null;  [same_ec] editingContext().equals(aUser.editingContext()); **/

        return type().isViewableByUser(this, aUser);
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if this Section requires access to be via SSL.
     *
     * @return <code>true</code> if this Section requires access to be via SSL
     */
    public boolean requiresSSLConnection()
    {
        return type().requiresSSLConnection(this);
    }



    /**
     * Returns <code>true</code> if this Section requires public access to be via SSL.
     *
     * @return <code>true</code> if this Section requires public access to be via SSL
     */
    public boolean requiresPublicHttpsAccess()
    {
    	return type().requiresPublicHttpsAccess(this);
    }



    /**
     * SMAccessible method.  Permits access to this resource to members of the group.
     *
     * @param aGroup the Group to allow access to
     */
    public void allowAccessForGroup(Group newGroup)
    {
        /** require
        [valid_group] newGroup != null;
        [same_ec] editingContext() == newGroup.editingContext();
        [not_in_group] ! groups().containsObject(newGroup); **/

        Enumeration anEnum = new NSArray(groups()).objectEnumerator();
        addToGroups(newGroup);

        while (anEnum.hasMoreElements())
        {
            Group currentGroup = (Group)anEnum.nextElement();
            if ((newGroup instanceof PublicGroup) || (currentGroup instanceof PublicGroup))
            {
                revokeAccessForGroup(currentGroup);
            }
        }

        if (newGroup instanceof WebsiteGroup)
        {
            ((WebsiteGroup)newGroup).addToSections(this);
        }

        /** ensure
        [access_allowed] groups().containsObject(newGroup);
        [file_recorded] ( ! (newGroup instanceof WebsiteGroup)) ||
                            ((WebsiteGroup)newGroup).sections().containsObject(this); **/
    }



    /**
     * SMAccessible method.  Removes permission access to this resource by members of the group.
     *
     * @param aGroup the Group to revoke access to
     */
    public void revokeAccessForGroup(Group aGroup)
    {
        /** require [valid_group] aGroup != null;
                    [same_ec] editingContext() == aGroup.editingContext();
                    [in_group] groups().containsObject(aGroup);
                    [has_multiple_groups] groups().count() > 1;
         **/

        removeFromGroups(aGroup);
        if (aGroup instanceof WebsiteGroup)
        {
            ((WebsiteGroup)aGroup).removeFromSections(this);
        }

        /** ensure [access_not_allowed] ! groups().containsObject(aGroup);
                   [has_a_group] groups().count() > 0;
                   [file_disconnected] ( ! (aGroup instanceof WebsiteGroup)) ||
                                      ! ((WebsiteGroup)aGroup).sections().containsObject(this);
         **/
    }



    /**
     * SMAccessible method.  Returns <code>true</code> if access protection can be configured for this Section.
     *
     * @return <code>true</code> if access protection can be configured for this Section
     */
    public boolean usesAccessProtection()
    {
        return type().usesAccessProtection(this);
    }



    /**
     * Returns <code>true</code> if this Section can be emebedded in another Section.  This is true
     * if our SectionType can be embedded.
     *
     * @returns <code>true</code> if this Section can be emebedded in another Section
     */
    public boolean canBeEmbedded()
    {
        return type().canBeEmbedded();
    }



    /**
     * Returns <code>true</code> if this Section can be a source for a MixedMedia pane.  This is true
     * if our SectionType can be displayed in a MixedMedia section.
     *
     * @returns <code>true</code> if this Section can be a source for a MixedMedia pane
     */
    public boolean isAllowedAsMixedMediaSource()
    {
        return type().canBeDisplayedInMixedMediaSection();
    }

    /**
     * Return <code>true</code> if this Section is the Website's home section.
     *
     * @return <code>true</code> if this Section is the Website's home section
     */
    public boolean isHomeSection() {
        /** require [valid_site] website() != null; **/
        return website().isHomeSection(this);
    }



    /**
     * Return <code>true</code> if this Section is the Website's home section.
     *
     * @return <code>true</code> if this Section is the Website's home section
     */
    public boolean isDataAccessWithNotificationOn() {
        /** require [valid_site] website() != null;  **/

        if (type() instanceof DataAccessSectionType)
        {
            return ((DataAccess)component()).areNotificationsEnabled();
        }
        return false;
    }



    /**
     * Return <code>true</code> if this Section can become the Website's home section.
     *
     * @return <code>true</code> if this Section can become the Website's home section.
     */
    public boolean canBeHomeSection() {
        /** require [valid_site] website() != null;  **/

        return type().canBeHomeSection(this);
    }



    /**
     * Returns true if this section can be indented.  The home section can't be indented and neither
     * can a section that is already indented from the prior section.
     *
     * @return true if this section can be indented
     */
    public boolean canIndent()
    {
        return ! isHomeSection() && (website().priorSection(this).indentation().intValue() >= indentation().intValue());
    }



    /**
     * Returns true if this section can be out-dented.  Sections can't be out-dented if they are not indented.
     *
     * @return true if this section can be outdented
     */
    public boolean canOutdent()
    {
        return indentation().intValue() > NOT_INDENTED;
    }



    /**
     * Indents this section one level.
     */
    public void indent()
    {
        /** require [can_indent] canIndent(); **/
        setIndentation(new Integer(indentation().intValue() + 1));
    }



    /**
     * Outdents this section one level.
     */
    public void outdent()
    {
        /** require [can_outdent] canOutdent(); **/
        setIndentation(new Integer(indentation().intValue() - 1));
    }



    /**
     * Returns the section above us that is outdented from us by one.
     *
     * @return the section above us that is outdented from us by one
     */
    public Section closestOutdentedParentSection()
    {
        /** require
        [valid_site] website() != null;
        [is_indented] indentation().intValue() > 0; **/

        int indentValue = indentation().intValue() - 1;
        Section sectionAbove = website().priorSection(this);
        while (indentValue != sectionAbove.indentation().intValue())
        {
            sectionAbove = website().priorSection(sectionAbove);
        }
        return sectionAbove;
    }



    /**
     * Returns true iff the next section in the list of sections is indented.
     *
     * @return true iff the next section in the list of sections is indented
     */
    public boolean nextSectionIsIndented()
    {
        /** require [valid_site] website() != null; **/
        if (website().isLastSection(this))
        {
            return false;
        }
        return website().nextSection(this).indentation().intValue() > indentation().intValue();
    }



    /**
     * Returns the SectionStyle to display this section in.
     *
     * @return the SectionStyle to display this section in
     */
    public SectionStyle activeSectionStyle()
    {
        // Don't call this method sectionStyle(), as it will interfere with EOF.
        return sectionStyle() != null ? sectionStyle() : website().sectionStyle();
    }



    /**
     * Returns <code>true</code> if this Section uses passed Database Table.
     *
     * @param aTable - the database table to check
     * @return <code>true</code> if this Section uses passed Database Table.
     */
    public boolean usesDatabaseTable(Table aTable)
    {
        /** require [valid_param] aTable != null; **/
        return type().usesDatabaseTable(this, aTable);
    }



    /* (non-Javadoc)
     * @see net.global_village.eofvalidation.EditingContextNotification#hasInserted()
     */
    public void hasInserted() {
        hasUpdated();

    }



    /* (non-Javadoc)
     * @see net.global_village.eofvalidation.EditingContextNotification#willDelete()
     */
    public void willDelete() {
        // TODO Auto-generated method stub

    }



    /* (non-Javadoc)
     * @see net.global_village.eofvalidation.EditingContextNotification#hasDeleted()
     */
    public void hasDeleted() {
        hasUpdated();
    }



    /* (non-Javadoc)
     * @see net.global_village.eofvalidation.EditingContextNotification#willUpdate()
     */
    public void willUpdate() {
        // TODO Auto-generated method stub

    }



    /**
     * Tell our Website that we have changed so that it can clears any cached information about Sections
     * that may be out dated.
     */
    public void hasUpdated() {
        if (website() != null)
        {
            website().clearCachedValues();
        }
    }



    /**
     * Called after revert is invoked on our EOEditingContext.
     */
    public void hasReverted() {}



    /**
     * Convenience method to create and return a CopySectionTask with the passed sectionToCopy and taskOwner for queueing.
     *
     * @param sectionToCopy the Section that will be copied
     * @param taskOwner the User who created this task
     * @return  a CopySectionTask with the passed sectionToCopy and taskOwner
     */
    public CopySectionTask copySection(Section sectionToCopy, User taskOwner)
    {
        /** require [sectionToCopy_not_null] sectionToCopy != null;
         		   [taskOwner_not_null] taskOwner != null;
         **/

    		return CopySectionTask.newCopySectionTask(editingContext(), sectionToCopy, this, taskOwner);

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this Section a PackageSectionType.
     *
     * @return <code>true</code> if this Section a PackageSectionType
     */
    public boolean isPackageSectionPlaceHolder()
    {
    		return ((type() != null ) && (type() instanceof PackageSectionType));
    }



    /**
     * Returns <code>true</code> if this Section a CopySectionType.
     *
     * @return <code>true</code> if this Section a CopySectionType
     */
    public boolean isCopyingResourcesSection()
    {
            return ((type() != null ) && (type() instanceof CopySectionType));
    }



    /**
     * Returns <code>true</code> if this Section can be modified. This section can be modified if it is not a CopySectionType.
     *
     * @return <code>true</code> if this Section can be modified
     */
    public boolean canBeModified()
    {
    		return type().canBeModified();
    }



    /**
     * Returns <code>true</code> if this Section can be deleted. This section can be deleted if it is not a CopySectionType or a HomeSection
     *
     * @return <code>true</code> if this Section can be deleted
     */
    public boolean canBeDeleted()
    {
    		return type().canBeDeleted() && ! isHomeSection();
    }



    /**
     * Returns just the name if navigable, else appends " (hidden)" with name otherwise.  This is an implementation for RTEListSelectable.
     *
     * @return just the name if navigable, else appends " (hidden)" with name otherwise
     */
    public String listDisplayName()
    {
        String listDisplayName = name();

        if (! isNavigable().booleanValue())
        {
            listDisplayName = listDisplayName + " (hidden)";
        }

        return listDisplayName;


        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns the sectionURL.  This is an implementation for RTEListSelectable.
     *
     * @return the sectionURL
     */
    public String listURL()
    {
        return SMActionURLFactory.sectionURL(website(), this, true, null);

        /** ensure [result_not_null] Result != null; **/
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
     * Returns <code>true</code> if this section can be versioned.
     *
     * @return <code>true</code> if this section can be versioned.
     */
    public boolean isVersionable()
    {
        /** require [type_is_not_null] type() != null;  **/

        return type().isVersionable();
    }



    /**
     * Returns <code>true</code> if this Section supports editors.
     *
     * @return <code>true</code> if this Section supports editors.
     */
    public boolean supportsEditors()
    {
        /** require [valid_type] type() != null;  **/

        return type().supportsEditors();
    }



    /**
     * Returns <code>true</code> if this Section supports contributors.
     *
     * @return <code>true</code> if this Section supports contributors.
     */
    public boolean supportsContributors()
    {
        /** require [valid_type] type() != null;  **/

        return type().supportsContributors();
    }



    /**
     * Returns a newVersion for this Section based on values from sourceVersion.
     *
     * @param sourceVersion the version to copy
     * @return a newVersion for this Section based on values from sourceVersion
     */
    public SectionVersion newVersion(SectionVersion sourceVersion)
    {
        /** require
        [valid_source] ((sourceVersion == null) || (sourceVersion != null) && editingContext().equals(sourceVersion.editingContext()));
        [valid_source2] (sourceVersion == null) ? ( ! (sourceVersion.component() instanceof MixedMediaTextContentPane)) : true;
         **/

        PageComponent newComponent;

        NSMutableDictionary componentOnlyCopy = new NSMutableDictionary(PageComponent.COMPONENT_ONLY_COPY_CONTEXT, EOCopying.COPY_CONTEXT);

        PageComponent origComponent = component();
        if (sourceVersion != null)
        {
            newComponent = (PageComponent)sourceVersion.component().duplicate(componentOnlyCopy);

            //Make sure the section still points to original component after duplicate is called
            addObjectToBothSidesOfRelationshipWithKey(origComponent, "component");
        }
        else
        {
            newComponent = (PageComponent)component().duplicate(componentOnlyCopy);
        }

        //create SectionVersion object
        SectionVersion newSectionVersion = SectionVersion.newSectionVersion();
        editingContext().insertObject(newSectionVersion);

        newSectionVersion.setCreatedDate(new NSTimestamp());
        newSectionVersion.setModifiedDate(new NSTimestamp());
        newSectionVersion.setIsLocked(GVCBoolean.falseBoolean());
        newSectionVersion.setCreatedBy(website().owner());
        newSectionVersion.setModifiedBy(website().owner());
        newSectionVersion.setName(defaultVersionName());
        newSectionVersion.setVersionNumber(new Integer(latestVersionNumber() + 1));

        newSectionVersion.addObjectToBothSidesOfRelationshipWithKey(this, "section");
        newSectionVersion.addObjectToBothSidesOfRelationshipWithKey(newComponent, "component");

        return newSectionVersion;

        /** ensure [return_not_null] Result != null;**/
    }



    /**
     * Returns <code>true</code> if this Section has versions.
     *
     * @return <code>true</code> if this Section has versions.
     */
    public boolean hasVersions()
    {
        return (versions().count() > 0);
    }



    /**
     * Returns <code>true</code> if this Section has two or more versions.
     *
     * @return <code>true</code> if this Section has two or more versions.
     */
    public boolean hasMultipleVersions()
    {
        return (versions().count() > 1);
    }



    /**
     * Returns the number of the latest version for this section
     *
     * @return the number of the latest version for this section
     */
    public int latestVersionNumber()
    {
        int latest = 0;

        for (int i = 0; i < versions().count(); i++)
        {
            SectionVersion aVersion = (SectionVersion) versions().objectAtIndex(i);
            int versionNumber = ((Integer) aVersion.versionNumber()).intValue();
            if (versionNumber > latest)
            {
                latest = versionNumber;
            }
        }

        return latest;
    }



    /**
     * Returns the number of the latest version for this section
     *
     * @return the number of the latest version for this section
     */
    public String defaultVersionName()
    {
        return "Version " + (latestVersionNumber() + 1 );

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Returns true if aUser belongs to this section's editorGroups
     *
     * @param aUser the user to evaluate
     * @return true if aUser belongs to this section's editorGroups
     */
    public boolean isEditor(User aUser)
    {
        /** require
        [valid_user] aUser != null;
        [same_ec] editingContext().equals(aUser.editingContext()); **/

        boolean isEditor = false;

        Enumeration groupEnum = editorGroups().objectEnumerator();
        while(( ! isEditor) && groupEnum.hasMoreElements())
        {
            isEditor = ((Group) groupEnum.nextElement()).isMember(aUser);
        }

        return (isEditor || isEditingPublic());
    }



    /**
     * Returns true if aUser belongs to this section's contributorGroups
     *
     * @param aUser the user to evaluate
     * @return true if aUser belongs to this section's contributorGroups
     */
    public boolean isContributor(User aUser)
    {
        /** require
        [valid_user] aUser != null;
        [same_ec] editingContext().equals(aUser.editingContext()); **/

        boolean isEditor = false;

        Enumeration groupEnum = contributorGroups().objectEnumerator();
        while(( ! isEditor) && groupEnum.hasMoreElements())
        {
            isEditor = ((Group) groupEnum.nextElement()).isMember(aUser);
        }

        return (isEditor || isContributionPublic());
    }



    /**
     * Makes aVersion the current version for this Section.
     *
     * @param aVersion to make current
     */
    public void makeCurrentVersion(SectionVersion aVersion)
    {
        /** require
        [valid_user] aVersion != null;
        [valid_component] aVersion.component() != null;
        [same_ec] editingContext().equals(aVersion.editingContext());
        [same_ec_component] editingContext().equals(aVersion.component().editingContext());
         **/

        addObjectToBothSidesOfRelationshipWithKey(aVersion.component(), "component");

        /** ensure
             [correct_current_version] aVersion.isCurrent();
         **/
    }



    /**
     * Creates the default version for this section.
     *
     * @param aUser the user to evaluate
     * @return true if aUser belongs to this section's contributorGroups
     */
    public void createFirstVersion()
    {
        /** require
        [has_no_version] (! hasVersions());
        [section_is_versionable] isVersionable(); **/

        SectionVersion firstVersion = newVersion(null);
        firstVersion.setDetails("Auto-created first version");
        makeCurrentVersion(firstVersion);

        /** ensure
        [has_first_version] versions().count() == 1;
        [first_version_is_current] firstVersion().isCurrent(); **/
    }



    /**
     * Returns first version for this section
     *
     * @return first version for this section
     */
    public SectionVersion firstVersion()
    {
        /** require
        [has_no_version] (hasVersions());
        [section_is_versionable] isVersionable();
        **/

        return (SectionVersion) versions().objectAtIndex(0);

        /** ensure
        [result_not_null] Result != null;
        **/

    }



    /**
     * Returns the SectionVersion with the specified VersionNumber.  Returns null if not found.
     *
     * @param versionNumber the versionNumber of the SectionVersion to return
     * @return the SectionVersion with the specified VersionNumber.  Returns null if not found.
     */
    public SectionVersion versionWithNumber(int versionNumber)
    {
        SectionVersion version = null;

        EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("versionNumber = %@", new NSArray(new Object[]{new Integer(versionNumber)}));
        NSArray matchingVersions = EOQualifier.filteredArrayWithQualifier(versions(), qual);

        if (matchingVersions.count() == 1)
        {
            version = (SectionVersion) matchingVersions.objectAtIndex(0);
        }
        else if (matchingVersions.count() > 1)
        {
            throw new RuntimeException("Data Error: There more than one version with the same number");
        }

        return version;
    }



    /**
     * Returns true if this Section is a placeholder or temporary Section (for queued tasks)
     *
     * @returns true if this Section is a placeholder or temporary Section (for queued tasks)
     */
    public boolean isPlaceHolder()
    {
        return type().isPlaceHolder();
    }



    /**
     * Allows SectionType to determine the actual mime type.
     *
     * @see com.gvcsitemaker.core._Section#mimeType()
     * @see EmbeddedSectionSectionType
     *
     * @return the MIME type to use when displaying this section
     */
    public String actualMimeType()
    {
        return type().mimeType(this);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
     * Overridden to set the autoVersioning property to false if newValue for versioning is false.
     *
     * @param newValue the newValue for the versioning property for this section
     */
    public void setIsVersioning(net.global_village.foundation.GVCBoolean newValue)
    {
    	super.setIsVersioning(newValue);
    	if (isVersioning().isFalse())
    	{
            setIsAutoVersioning(isVersioning());
    	}
    }


}
