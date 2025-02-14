// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page handles adding a new section and also editing the properties of
 * an existing section. // Copyright (c) 2001-2005, The Regents of the
 * University of Michigan, Ann Arbor, MI 48109 USA All rights reserved. // This
 * software is published under the terms of the Educational Community License
 * (ECL) version 1.0, // a copy of which has been included with this
 * distribution in the LICENSE.TXT file.
 */
public class EditMultipleSections extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage, WOSMConfirmable
{
    protected EOEditingContext editingContext;
    protected Section currentSection;
    public NSArray sectionsToEdit;
    protected String message;
    protected Boolean supportsContributors;

    public Section aSection;


    /**
     * Designated constructor.
     * 
     * @param aContext WOContext to create page in
     */
    public EditMultipleSections(WOContext aContext)
    {
        super(aContext);
        DebugOut.println(10, "Entering...");

        //Remove PackageSectionType since it cannot be added in this page.
        NSMutableArray allSectionTypes = new NSMutableArray(SectionType.orderedSectionTypes(editingContext()));
        allSectionTypes.removeObject(PackageSectionType.getInstance(editingContext()));

        DebugOut.println(10, "Leaving...");
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }


    /**
     * Cancels editing the properties of an existing section
     * 
     * @return ConfigPage
     */
    public WOComponent cancelModifySection()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Action method to show error page.
     * 
     * @param errorMessage the error message to dispaly
     * @return ErrorPage
     */
    protected WOComponent error(String errorMessage)
    {
        ErrorPage errorPage = (ErrorPage) pageWithName("ErrorPage");
        errorPage.setReturnPage(this);
        errorPage.setMessage(errorMessage);
        errorPage.setReason("Invalid submission.");
        return errorPage;
    }



    /**
     * Finishes editing the properties of an existing section.
     * 
     * @return current page
     */
    public WOComponent saveChanges()
    {
        EOEditingContext saveEditingContext = smSession().newEditingContext();

        Enumeration sectionsToEditEnumerator = sectionsToEdit.objectEnumerator();
        while (sectionsToEditEnumerator.hasMoreElements())
        {
            Section theSection = (Section)sectionsToEditEnumerator.nextElement();
            theSection = (Section)EOUtilities.localInstanceOfObject(saveEditingContext, theSection);

            if (theSection.usesAccessProtection())
            {
                NSArray groups = EOUtilities.localInstancesOfObjects(saveEditingContext, currentSection().groups());
                theSection.setGroups(groups.mutableClone());
            }

            if (theSection.supportsEditors())
            {
                NSArray groups = EOUtilities.localInstancesOfObjects(saveEditingContext, currentSection().editorGroups());
                theSection.setEditorGroups(groups.mutableClone());
            }

            boolean supportsContributors = theSection.supportsContributors() &&
                theSection.isVersioning().booleanValue() && theSection.isVersionable();
            if (supportsContributors)
            {
                NSArray groups = EOUtilities.localInstancesOfObjects(saveEditingContext, currentSection().contributorGroups());
                theSection.setContributorGroups(groups.mutableClone());
            }
        }

        saveEditingContext.saveChanges();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Returns <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that can have access protection configured.
     * 
     * @return <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that can have access protection configured
     */
    public boolean atLeastOneSectionUsesAccessProtection()
    {
        Enumeration sectionsToEditEnumerator = sectionsToEdit.objectEnumerator();
        while (sectionsToEditEnumerator.hasMoreElements())
        {
            Section theSection = (Section)sectionsToEditEnumerator.nextElement();
            if (theSection.usesAccessProtection())
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports editors.
     * 
     * @return <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports editors
     */
    public boolean atLeastOneSectionSupportsEditors()
    {
        Enumeration sectionsToEditEnumerator = sectionsToEdit.objectEnumerator();
        while (sectionsToEditEnumerator.hasMoreElements())
        {
            Section theSection = (Section)sectionsToEditEnumerator.nextElement();
            if (theSection.supportsEditors())
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports contributors.
     * 
     * @return <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports contributors
     */
    public boolean atLeastOneSectionSupportsContributors()
    {
        Enumeration sectionsToEditEnumerator = sectionsToEdit.objectEnumerator();
        while (sectionsToEditEnumerator.hasMoreElements())
        {
            Section theSection = (Section)sectionsToEditEnumerator.nextElement();
            boolean supportsContributors = theSection.supportsContributors() &&
                    theSection.isVersioning().booleanValue() && theSection.isVersionable();
            if (supportsContributors)
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports contributors.
     * 
     * @return <code>true</code> if at least one section in the sectionsToEdit array
     * is of a type that supports contributors
     */
    public boolean atLeastOneSectionIsVersionable()
    {
        Enumeration sectionsToEditEnumerator = sectionsToEdit.objectEnumerator();
        while (sectionsToEditEnumerator.hasMoreElements())
        {
            Section theSection = (Section)sectionsToEditEnumerator.nextElement();
            if (theSection.isVersionable())
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Sets the Website after translating it into our editing context.  This is part of WebsiteContainerBase.
     *
     * @param value - the Website to add or edit the Database Table in.
     */
    public void setWebsite(Website value)
    {
        /** require [value_not_null] value != null; **/

        super.setWebsite((Website) EOUtilities.localInstanceOfObject(editingContext(), value));

        /** ensure [website_in_correct_ec] editingContext().equals(website().editingContext());  **/
    }



    /**
     * Handles expected validation error to prevent the log from being filled with spurious log messages
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.equals("currentSection.name"))
        {
            // Just ignore, this happens when the section type is changed and the form refreshed.
        }
        else
        {
            super.validationFailedWithException(t, value, keyPath);
        }
    }



    /**
     * Returns true if SectionType selected is a CopySectionType
     * 
     * @return true if SectionType selected is a CopySectionType
     */
    public boolean showSections()
    {
        return ((currentSection().type() != null) && (currentSection().type().equals(CopySectionType.getInstance(editingContext()))));
    }



    /**
     * Called after the Confirm Page is displayed.  Returns the QueuedTasksPage to continue creation of new Task, if false, revert all changes.
     *
     * @param value the value selected in the Confirm Page
     * @return QueuedTasksPage if true, this page if false
     */
    public WOComponent confirmAction(boolean confirmed)
    {
        if (!confirmed)
        {
            editingContext().revert();
            website().removeSection(currentSection());
        }

        editingContext().saveChanges();
        return pageWithName("ConfigTabSet");

        /** ensure [result_not_null] Result != null; **/
    }



    public void appendToResponse(WOResponse response, WOContext context)
    {
        supportsContributors = null; // See supportsContributors()
        super.appendToResponse(response, context);
    }



    /* Generic setters and getters ***************************************/

    public Section currentSection()
    {
        // Default mode is to add a new section.
        if (currentSection == null)
        {
            currentSection = Section.newSection();
            editingContext().insertObject(currentSection);
            currentSection.setType(TextImageSectionType.getInstance(editingContext()));
        }
        return currentSection;
    }


    public void setSectionsToEdit(NSArray newSectionsToEdit)
    {
        sectionsToEdit = newSectionsToEdit;
    }


    public String message()
    {
        return message;
    }


    public void setMessage(String newMessage)
    {
        message = newMessage;
    }


    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Change Access on Multiple Sections";
    }


    public GVCBoolean trueValue()
    {
        return GVCBoolean.trueBoolean();
    }


    public GVCBoolean falseValue()
    {
        return GVCBoolean.falseBoolean();
    }


    public EOEditingContext editingContext()
    {
        // Create this on demand and our super's constructor causes setWebsite to be called and it needs this.
        if (editingContext == null)
        {
            editingContext = smSession().newEditingContext();
        }

        return editingContext;
    }



}
