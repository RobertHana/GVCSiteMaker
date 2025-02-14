// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.CopySectionTask;
import com.gvcsitemaker.core.CopySectionType;
import com.gvcsitemaker.core.EmbeddedSectionSectionType;
import com.gvcsitemaker.core.EmbeddedSiteSectionType;
import com.gvcsitemaker.core.ExternalURLSectionType;
import com.gvcsitemaker.core.PackageSectionType;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.SectionStyle;
import com.gvcsitemaker.core.SectionType;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.components.ErrorPage;
import com.gvcsitemaker.core.interfaces.SMSecurePage;
import com.gvcsitemaker.core.interfaces.WOSMConfirmable;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import net.global_village.foundation.GVCBoolean;


/**
 * This page handles adding a new section and also editing the properties of
 * an existing section. // Copyright (c) 2001-2005, The Regents of the
 * University of Michigan, Ann Arbor, MI 48109 USA All rights reserved. // This
 * software is published under the terms of the Educational Community License
 * (ECL) version 1.0, // a copy of which has been included with this
 * distribution in the LICENSE.TXT file.
 */
public class AddSection extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage, WOSMConfirmable
{
    protected EOEditingContext editingContext;
    protected Section currentSection;
    protected String message;
    protected Boolean supportsContributors;


    public NSArray sectionTypes;
    public SectionType aSectionType;

    /** @TypeInfo SectionStyle */
    protected NSArray styles;
    public SectionStyle aStyle;

    public Section sectionToCopy;
    public Section aSection;

    public CopySectionTask copySectionTask;


    /**
     * Designated constructor.
     * 
     * @param aContext WOContext to create page in
     */
    public AddSection(WOContext aContext)
    {
        super(aContext);
        DebugOut.println(10, "Entering...");

        styles = SectionStyle.stylesAdvailableForWebsite(website());

        //Remove PackageSectionType since it cannot be added in this page.
        NSMutableArray allSectionTypes = new NSMutableArray(SectionType.orderedSectionTypes(editingContext()));
        allSectionTypes.removeObject(PackageSectionType.getInstance(editingContext()));
        sectionTypes = allSectionTypes.mutableClone();

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
     * Cancels the addition of a new section.
     * 
     * @return ConfigPage
     */
    public WOComponent cancelAddSection()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Finishes the creation of a new section.
     * 
     * @return editor for section contents
     */
    public WOComponent createSection()
    {
        DebugOut.println(10, "Entering...");

        if (currentSection().type() == null)
            return error("You must choose a section type to create a new section.");

        if (currentSection().name() == null)
            return error("You must enter a section name to create a new section.");

        DebugOut.println(15, "====New section name is: " + currentSection().name());
        DebugOut.println(15, "====New section normal name is: " + currentSection().normalName());

        // Make sure that our name doesn't equal the name of an existing section.
        // The normalized name is checked rather than the name as this check is
        // more strict (two different names might normalize to the same name.
        if (website().hasSectionWithNormalizedName(currentSection().normalName()))
        {
            return error("A section by that name already exists");
        }

        // Generate rest of section before attaching to website
        currentSection().type().createComponents(currentSection());
        website().attachSection(currentSection());

        // Create a version if section is set to versioning
        if (currentSection().isVersionable())
        {
            DebugOut.println(15, "First version is created...");
            currentSection().createFirstVersion();
        }

        DebugOut.println(1, "Saving Changes...");
        // Save changes so that we can call layoutComponents() if needed (it uses the primary keys)
        editingContext().saveChanges();

        WOComponent nextPage = null;

        if (!showSections())
        {
            if (currentSection().component() instanceof com.gvcsitemaker.core.pagecomponent.Layout)
            {
                ((com.gvcsitemaker.core.pagecomponent.Layout) currentSection().component()).layoutComponents();
                editingContext().saveChanges();
            }

            nextPage = pageWithName("EditSection");
            ((EditSection) nextPage).setSection(currentSection());
            if (currentSection().isVersionable())
            {
                ((EditSection) nextPage).setVersion(currentSection().firstVersion());
            }
        }
        else
        {
            nextPage = createNewCopySection();
        }

        return nextPage;
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
     * Cancels editing the properties of an existing section
     * 
     * @return ConfigPage
     */
    public WOComponent cancelModifySection()
    {
        DebugOut.println(10, "Entering...");
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Finishes editing the properties of an existing section.
     * 
     * @return current page
     */
    public WOComponent saveChanges()
    {
        if (currentSection().name() == null)
            return error("The section name is required.");


        // TODO check if they rename the same as one of the others?
        // We can't use hasSectionWithName since the section we're editing is already
        // saved in the ec. Hmm. 

        // Nasty hack for SCR 336.  Need to remove Link Text (a.k.a name) to fix this properly.  Note that currentSection().component() is null for Embedded Sites.
        if ((currentSection().component() != null) && (currentSection().type() instanceof ExternalURLSectionType))
        {
            currentSection().component().setBindingForKey(currentSection().name(), "name");
        }

        DebugOut.println(1, "Section name is " + currentSection().name());

        DebugOut.println(1, "Saving Changes...");
        editingContext().saveChanges();
        return null;
    }



    /**
     * Returns editor page for the contents of this section
     * 
     * @return EditSection
     */
    public EditSection editSection()
    {
        EditSection nextPage = (EditSection) pageWithName("EditSection");
        editingContext().revert();
        nextPage.setSection(currentSection());
        if (currentSection().isVersionable())
        {
            nextPage.setVersion(currentSection().component().version());
        }
        return nextPage;
    }



    /**
     * Returns <code>true</code> if the selected section type or section is of a type that can have
     * access protection configured.
     * 
     * @return <code>true</code> if the selected section type or section is of a type that can have
     * access protection configured
     */
    public boolean usesAccessProtection()
    {
        boolean usesAccessProtection = false;

        if ((currentSection() != null) && (currentSection().type() != null))
        {
            usesAccessProtection = currentSection().usesAccessProtection();
        }

        return usesAccessProtection;
    }



    /**
     * Returns <code>true</code> if the selected section uses a section style
     * 
     * @return <code>true</code> if the selected section uses a section style
     */
    public boolean canHaveOwnStyle()
    {
        boolean canHaveOwnStyle = false;

        if ((currentSection() != null) && (currentSection().type() != null))
        {
            canHaveOwnStyle = ((!(currentSection().type() instanceof com.gvcsitemaker.core.ExternalURLSectionType)) && (!(currentSection().type() instanceof com.gvcsitemaker.core.CopySectionType)));
        }

        return canHaveOwnStyle;
    }



    /**
     * @return <code>true</code> if the selected section type allows HTTPS to be required
     */
    public boolean canRequireHTTPS()
    {
        return !((currentSection().type() instanceof CopySectionType) || (currentSection().type() instanceof ExternalURLSectionType)
                || (currentSection().type() instanceof EmbeddedSectionSectionType) || (currentSection().type() instanceof EmbeddedSiteSectionType));
    }



    /**
     * @return <code>true</code> if enable/disable navigation is applicable to this section
     */
    public boolean canControlNavigation()
    {
        boolean canControlNavigation = true;

        // Can't disable navigation to the home section
        if ((currentSection() != null) && (currentSection().website() != null))
        {
            canControlNavigation = !currentSection().isHomeSection();
        }

        return canControlNavigation;
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
     * @return <code>true</code> if the properties of an existing section are being edited, false
     * if a new section is being created
     */
    public boolean isModifyingSection()
    {
        return !currentSection().globalID().isTemporary();
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
     * Creates a new Section of type CopySectionType and creates a Task for queueing.  Displays a confirmation page if there is already a duplicate Task else displays the QueuedTasksPage with the newly created Task on top of the page.
     *
     * @return QueuedTasksPage if no duplicate, else displays the ConfirmAction page
     */
    public WOComponent createNewCopySection()
    {
        WOComponent nextPage = null;
        copySectionTask = currentSection().copySection(sectionToCopy, ((Session) session()).currentUser());

        // Copy what the user specified and set it back to false so it won't be accessible when Section is displayed in the site
        copySectionTask.setIsNavigable(currentSection().isNavigable());
        currentSection().setIsNavigable(new GVCBoolean(false));

        if (copySectionTask.hasDuplicate())
        {
            ConfirmAction confirmCopy = (ConfirmAction) pageWithName("ConfirmAction");
            confirmCopy.setCallingComponent(this);
            confirmCopy.setCustomMessage("There is already an existing request to copy the same section. Do you want to continue?");
            confirmCopy.setActionName("Submit");

            nextPage = confirmCopy;
        }
        else
        {
            nextPage = confirmAction(true);
        }

        return nextPage;

        /** ensure [result_not_null] Result != null; **/
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



    /**
     * Returns <code>true</code> if the selected section type or section is of a type that supports editors
     * 
     * @return <code>true</code> if the selected section type or section is of a type that supports editors
     */
    public boolean supportsEditors()
    {
        return ((currentSection() != null) && (currentSection().type() != null) && currentSection().supportsEditors());
    }



    /**
     * Returns <code>true</code> if the selected section type or section is of a type that supports contributors
     * 
     * @return <code>true</code> if the selected section type or section is of a type that supports contributors
     */
    public boolean supportsContributors()
    {
        // Boolean supportsContributors is used to wrap this so that changing of the value can be delayed until
        // appendToResponse.  If this is updated as soon as the Allow Versioning button is read, all the 
        // contributors are removed so they need to be added back if versioning is re-enabled
        if (supportsContributors == null)
        {
            supportsContributors = new Boolean(((currentSection() != null) && (currentSection().type() != null) && currentSection().supportsContributors()
                    && currentSection().isVersioning().booleanValue() && currentSection().isVersionable()));
        }
        return supportsContributors.booleanValue();
    }



    public void appendToResponse(WOResponse response, WOContext context)
    {
        supportsContributors = null; // See supportsContributors()
        super.appendToResponse(response, context);
    }



    /**
     * Returns <code>true</code> if the selected section type or section is of a type that supports contributors
     * 
     * @return <code>true</code> if the selected section type or section is of a type that supports contributors
     */
    public boolean showVersioningOptions()
    {
        return ((currentSection() != null) && (currentSection().type() != null) && currentSection().isVersionable());
    }



    /**
     * Returns <code>true</code> if the selected section should automatically create versions.
     * 
     * @return <code>true</code> if the selected section should automatically create versions.
     */
    public boolean isAutoVersioning()
    {
        return currentSection().isAutoVersioning().booleanValue();
    }



    /**
     * Sets the current sections autoVersioning property.
     * 
     * @param newValue the newValue for the autoVersioning property for this section
     */
    public void setIsAutoVersioning(boolean newValue)
    {
        currentSection().setIsAutoVersioning(new net.global_village.foundation.GVCBoolean(newValue));
    }


    /* Generic setters and getters ***************************************/

    public Section currentSection()
    {
        // Default mode is to add a new section.
        if (currentSection == null)
        {
            currentSection = Section.newSection();
            editingContext().insertObject(currentSection);
        }
        return currentSection;
    }


    public void setCurrentSection(Section newCurrentSection)
    {
        if (newCurrentSection != currentSection)
        {
            editingContext().revert();
            currentSection = (Section) EOUtilities.localInstanceOfObject(editingContext(), newCurrentSection);
        }
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
        return SMApplication.appProperties().propertyForKey("ProductName") + ": " + (isModifyingSection() ? "Change Section Properties" : "Add a new section");
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


    /** @TypeInfo SectionStyle */
    public NSArray styles()
    {
        return styles;
    }
}
