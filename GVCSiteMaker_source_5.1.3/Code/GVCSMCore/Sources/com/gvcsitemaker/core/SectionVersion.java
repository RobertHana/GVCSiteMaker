// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.

package com.gvcsitemaker.core;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/*
 * * Contains information of a Section's version.
 */
public class SectionVersion extends _SectionVersion
{
    /**
     * Instance of Comparators to be used when sorting SectionVersion object.
     */
    static final public NSComparator VersionNumberAscendingComparator = new VersionNumberAscendingComparator();
    static final public NSComparator VersionNumberDescendingComparator = new VersionNumberDescendingComparator();
    static final public NSComparator LastModifiedDateAscendingComparator = new LastModifiedDateAscendingComparator();
    static final public NSComparator LastModifiedDateDescendingComparator = new LastModifiedDateDescendingComparator();
    static final public NSComparator NameComparator = new NameComparator();

    static final public String AutoCreatedDetailsString = "Auto-created";


    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>versionNumber</code> ascending.
     */
    static protected class VersionNumberAscendingComparator extends CurrentVersionComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result;

            try
            {
                result = super.compare(object1, object2);
                if (result == NSComparator.OrderedSame)
                {
                    // _NumberComparator is private so we need to do this rather than sub-classing.
                    result = NSComparator.AscendingNumberComparator.compare(((EOEnterpriseObject) object1).valueForKey("versionNumber"),
                            ((EOEnterpriseObject) object2).valueForKey("versionNumber"));
                }
            }
            catch (com.webobjects.foundation.NSComparator.ComparisonException e)
            {
                throw new ExceptionConverter(e);
            }

            return result;
        }
    }



    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>versionNumber</code> ascending.
     */
    static protected class VersionNumberDescendingComparator extends CurrentVersionComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result;

            try
            {
                result = super.compare(object1, object2);
                if (result == NSComparator.OrderedSame)
                {
                    result = NSComparator.AscendingNumberComparator.compare(((EOEnterpriseObject) object2).valueForKey("versionNumber"),
                            ((EOEnterpriseObject) object1).valueForKey("versionNumber"));
                }
            }
            catch (com.webobjects.foundation.NSComparator.ComparisonException e)
            {
                throw new ExceptionConverter(e);
            }

            return result;
        }
    }



    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>name</code> ascending.
     */
    static protected class NameComparator extends CurrentVersionComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result = super.compare(object1, object2);
            if (result == NSComparator.OrderedSame)
            {
                result = ((SectionVersion) object1).name().compareToIgnoreCase(((SectionVersion) object2).name());
            }

            return result;
        }


    }



    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>versionNumber</code> ascending.
     */
    static protected class LastModifiedDateAscendingComparator extends CurrentVersionComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result = super.compare(object1, object2);
            if (result == NSComparator.OrderedSame)
            {
                result = ((SectionVersion) object1).modifiedDate().compareTo(((SectionVersion) object2).modifiedDate());
            }

            return result;
        }
    }



    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>versionNumber</code> ascending.
     */
    static protected class LastModifiedDateDescendingComparator extends CurrentVersionComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result = super.compare(object1, object2);
            if (result == NSComparator.OrderedSame)
            {
                result = ((SectionVersion) object2).modifiedDate().compareTo(((SectionVersion) object1).modifiedDate());
            }

            return result;
        }
    }



    /**
     * Comparator to sort <code>SectionVersion</code> objects by <code>isCurrent</code> ascending.
     */
    static protected class CurrentVersionComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            SectionVersion version1 = (SectionVersion) object1;
            SectionVersion version2 = (SectionVersion) object2;

            if (version1.isCurrent())
            {
                return NSComparator.OrderedAscending;
            }
            else if (version2.isCurrent())
            {
                return NSComparator.OrderedDescending;
            }
            else
                return NSComparator.OrderedSame;
        }
    }



    /**
     * Returns a copy of the passed in Text pane
     *
     * @param component the component to copy
     * @return a copy of the component
     */
    public static PageComponent duplicate(PageComponent component)
    {
        /** require [component_not_null] component != null;  **/

        PageComponent copy = (PageComponent)EOCopying.Utility.newInstance(component);
        EOCopying.Utility.copyAttributes(component, copy);
        SMEOUtils.setPrimaryKeyForNewObject(copy);

        // copyAttributes uses takeStoredValueForKey which does not call setBindings(String) etc. so we need to manually clear any cached values
        ((PageComponent)copy).clearCachedValues();

        return copy;
        /** ensure [copy_made] Result != null;   **/
    }


    /**
     * Returns a newVersion of this SectionVersion based on values from sourceVersion.
     * Implementation copied from Section because don't want a deep copy of the
     * component.
     *
     * @param sourceVersion the version to copy
     * @return a newVersion for this Section based on values from sourceVersion
     */
    public static SectionVersion newVersion(SectionVersion sourceVersion)
    {
        /** require
        [valid_source] (sourceVersion == null) ? ( ! (sourceVersion.component() instanceof MixedMediaTextContentPane)) : true;
        **/

        PageComponent newComponent;

        PageComponent origComponent = sourceVersion.section().component();
        newComponent = duplicate(sourceVersion.component());

        //Make sure the section still points to original component after duplicate is called
        sourceVersion.section().addObjectToBothSidesOfRelationshipWithKey(origComponent, "component");

        //create SectionVersion object
        SectionVersion newSectionVersion = SectionVersion.newSectionVersion();
        sourceVersion.editingContext().insertObject(newSectionVersion);

        newSectionVersion.setCreatedDate(new NSTimestamp());
        newSectionVersion.setModifiedDate(new NSTimestamp());
        newSectionVersion.setDetails(null);
        newSectionVersion.setIsLocked(GVCBoolean.falseBoolean());
        newSectionVersion.setCreatedBy(sourceVersion.section().website().owner());
        newSectionVersion.setModifiedBy(sourceVersion.section().website().owner());
        newSectionVersion.setName("Version " + (sourceVersion.section().latestVersionNumber() + 1 ));
        newSectionVersion.setVersionNumber(new Integer(sourceVersion.section().latestVersionNumber() + 1));
        newSectionVersion.setComponentOrder(sourceVersion.componentOrder());
        newSectionVersion.addObjectToBothSidesOfRelationshipWithKey(sourceVersion.section(), "section");
        newSectionVersion.addObjectToBothSidesOfRelationshipWithKey(newComponent, "component");

        return newSectionVersion;

        /** ensure [return_not_null] Result != null;**/
    }



    /**
     * Returns <code>true</code> if the User can view non-current versions.  User can view this version if user can configure the site, an editor or a contributor. 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can view non-current versions
     */
    public static boolean canViewNonCurrentVersions(User aUser, Section aSection)
    {
        /** require [a_user_not_null] aUser != null; [section_not_null] aSection != null; **/

        return (aSection.website().canBeConfiguredByUser(aUser) || aSection.isEditor(aUser) || aSection.isContributor(aUser));
    }



    /**
     * Factory method to create new instances of SectionVersion.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SectionVersion or a subclass.
     */
    public static SectionVersion newSectionVersion()
    {
        return (SectionVersion) SMEOUtils.newInstanceOf("SectionVersion");

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

        if (isLocked() == null)
        {
            setIsLocked(net.global_village.foundation.GVCBoolean.falseBoolean());
        }

        /** ensure [isLocked_not_null] isLocked() != null; **/
    }



    /**
     * Returns true if this version is the current version
     * 
     * @return true if this version is the current version
     */
    public boolean isCurrent()
    {
        return section().component().equals(component()) || section().component().toChildren().contains(component());
    }



    /**
     * Returns <code>true</code> if the User can make this version current. User can make this version current if  user is a Site Owner or Site Editor 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can make this version current
     */
    public boolean canUserMakeCurrent(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);
        }

        return (section().website().canBeConfiguredByUser(aUser) || section().isEditor(aUser));
    }



    /**
     * Returns <code>true</code> if the User can make a new version. User can make this version current if  user is a Site Owner or Site Editor 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can make a new version
     */
    public boolean canCreateNewVersion(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);
        }

        return (section().website().canBeConfiguredByUser(aUser) || section().isEditor(aUser) || section().isContributor(aUser));
    }



    /**
     * Returns <code>true</code> if the User can alter or edit this version.  User can edit this version if user can configure the site or an editor. 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can alter or edit this version
     */
    public boolean canAlterVersion(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);
        }

        return (section().website().canBeConfiguredByUser(aUser) || ((!isCurrent()) || (isCurrent() && section().isEditor(aUser))));
    }



    /**
     * Returns <code>true</code> if the User can view non-current versions.  User can view this version if user can configure the site, an editor or a contributor. 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can view non-current versions
     */
    public boolean canViewNonCurrentVersions(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);
        }
        return canViewNonCurrentVersions(aUser, section());
    }



    /**
     * Returns <code>true</code> if the User can delete this version.  User can delete this version if user can configure the site or an editor. 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User can view non-current versions
     */
    public boolean canDelete(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);

        }
        return ((section().website().canBeConfiguredByUser(aUser)) || section().isEditor(aUser));
    }



    /**
     * Returns <code>true</code> if the User change the lock status of this SectionVersion. 
     *
     * @param aUser the user to check for rights
     * @return <code>true</code> if the User change the lock status of this SectionVersion.
     */
    public boolean canChangeLockStatus(User aUser)
    {
        /** require [a_user_not_null] aUser != null; **/

        // Ensure the user is in the same editing context
        if (aUser.editingContext() != editingContext())
        {
            aUser = (User) EOUtilities.localInstanceOfObject(editingContext(), aUser);

        }
        return ((section().website().canBeConfiguredByUser(aUser)) || section().isEditor(aUser));
    }
}
