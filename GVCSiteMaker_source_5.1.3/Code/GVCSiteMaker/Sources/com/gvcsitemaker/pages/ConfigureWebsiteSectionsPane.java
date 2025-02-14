// Copyright (c) 2001-2009, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Collection;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.gvcsitemaker.editors.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.ajax.*;


public class ConfigureWebsiteSectionsPane extends SMAuthComponent implements WebsiteContainer, SMSecurePage
{
    protected Website website;
    public OrderedSectionList orderedSections;

    public Section aSection;
    public int sectionIndex;

    public Group aGroup;

    public NSMutableSet selectedSections;
    public NSArray selectedSectionsActions = new NSArray(new Object[] { "Select All", "Unselect All", "Show/Hide Selected", "Set Access on Selected", "Delete Selected" });
    public String anActionString;
    public String selectedSectionsAction = null;
    public NSMutableDictionary selectedSectionAction = new NSMutableDictionary();

    protected NSMutableSet sectionsShowingChildren;
    protected NSMutableSet sectionsShowingGroups;

    public boolean showDeleteModalDialog = false;
    public Section sectionToDelete; // has to be public due to being used in a precondition to a public method

    public String temp;


    public ConfigureWebsiteSectionsPane(WOContext context)
    {
        super(context);
        selectedSections = new NSMutableSet();
    }



    public static void forgetObjects(EOEditingContext ec, NSArray eos)
    {
        Enumeration eoEnum = eos.objectEnumerator();
        while (eoEnum.hasMoreElements())
        {
            EOGenericRecord eo = (EOGenericRecord) eoEnum.nextElement();
            ec.forgetObject(eo);
        }
    }


    public static void forgetObjectsAndKeypaths(EOEditingContext ec, NSArray eos, NSArray keypaths)
    {
        Enumeration eoEnum = eos.objectEnumerator();
        while (eoEnum.hasMoreElements())
        {
            EOGenericRecord eo = (EOGenericRecord) eoEnum.nextElement();
            Enumeration keypathEnum = keypaths.objectEnumerator();
            while (keypathEnum.hasMoreElements())
            {
                String keypath = (String) keypathEnum.nextElement();
                EOFaulting faulting = (EOFaulting) eo.valueForKeyPath(keypath);
                if (faulting != null && !faulting.isFault())
                {
                    if (eo.isToManyKey(keypath))
                    {
                        NSArray relEos = (NSArray) eo.valueForKeyPath(keypath);
                        forgetObjects(ec, relEos);
                    }
                    else
                    {
                        ec.forgetObject((EOCustomObject) eo.valueForKeyPath(keypath));
                    }
                }
            }
            ec.forgetObject(eo);
        }
    }



    /**
     * Refresh objects if we are auto-refreshing.
     */
    public void updateAction()
    {
        if (shouldAutoUpdate())
        {
            website().refreshRelationshipNamed("sections");
            website().clearCachedValues();
            orderedSections = new OrderedSectionList(website().orderedSections());
        }
    }



    /**
     * Saves any changes made to the section ordering.
     *
     * @return this page
     */
    public void saveOrder()
    {
        website().editingContext().saveChanges();
    }



    /**
     * Returns true the Versions link is displayed for a Section. Only show Versions link if aSection isVersioning and it has two or more versions
     *
     * @return true the Versions link is displayed for a Section. Only show Versions link if aSection isVersioning and it has two or more versions
     */
    public boolean canViewVersions()
    {
        return (aSection.isVersioning().booleanValue() && aSection.hasMultipleVersions())
                && SectionVersion.canViewNonCurrentVersions(((Session) session()).currentUser(), aSection);
    }


    /**
     * Returns the VersionMgtPage with aSection.
     *
     * @return the VersionMgtPage with aSection.
     */
    public WOComponent viewVersions()
    {
        VersionMgtPage versionsPage = (VersionMgtPage) pageWithName("VersionMgtPage");
        versionsPage.setSection(aSection);
        return versionsPage;
    }



    /**
     * Action used to edit the notifications for an existing DA section.
     *
     * @return the <code>DataAccessNotificationEditor</code> page set for <code>aSection</code>.
     */
    public DataAccessNotificationEditor editNotification()
    {
        DataAccessNotificationEditor nextPage = (DataAccessNotificationEditor) pageWithName("DataAccessNotificationEditor");
        nextPage.setSection(aSection);
        return nextPage;
    }



    /**
     * Returns the ConfigAccessGroup page, configured for aGroup.
     *
     * @return the ConfigAccessGroup page, configured for aGroup
     */
    public void manageGroup()
    {
        /** require [knows_group] aGroup != null;  [is_WebsiteGroup] aGroup instanceof WebsiteGroup; **/
        ((ConfigTabSet) parent()).setTabSelected("Tab3");
        ((ConfigTabSet) parent()).setEditingAccessGroup(aGroup);
    }



    /**
     * Returns ", " or "" as a seperator between group names in the section area.
     *
     * @return ", " or "" as a seperator between group names in the section area
     */
    public String groupSeperator()
    {
        /** require [knows_section] aSection != null;  [knows_group] aGroup != null;  **/
        return !aGroup.equals(aSection.groups().lastObject()) ? "<br/>" : "";
    }



    /**
     * Method used to determine if the link to edit notifications should be shown.
     *
     * @return <code>true</code> when the given section is a DA section
     */
    public boolean isDASection()
    {
        return aSection.type() instanceof DataAccessSectionType;
    }



    /**
     * Returns true iff there is at least one section showing children.
     *
     * @return true iff there is at least one section showing children
     */
    public boolean hasAtLeastOneSectionExpanded()
    {
        return sectionsShowingChildren().count() > 0;
    }


    /**
     * Sets the view to expand all if no sections are expanded, otherwise sets the view to collapse all.
     */
    public void toggleExpandCollapseAll()
    {
        if (hasAtLeastOneSectionExpanded())
        {
            sectionsShowingChildren().removeAllObjects();
        }
        else
        {
            sectionsShowingChildren().removeAllObjects();
            sectionsShowingChildren().addObjectsFromArray(EOUtilities.localInstancesOfObjects(session().defaultEditingContext(), website().sections()));
        }
    }


    /**
     * Returns true iff this section shows any indented sections after this one.
     *
     * @return true iff this section shows any indented sections after this one
     */
    public boolean sectionShowsIndentedChildren()
    {
        if (website().isLastSection(aSection))
        {
            return false;
        }

        Section theSection = (Section) EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aSection);
        return sectionsShowingChildren().contains(theSection);
    }


    /**
     * Toggles showing indented children.
     */
    public void toggleShowIndentedChildren()
    {
        Section theSection = (Section) EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aSection);
        if (sectionsShowingChildren().contains(theSection))
        {
            sectionsShowingChildren().removeObject(theSection);
        }
        else
        {
            sectionsShowingChildren().addObject(theSection);
        }
    }


    /**
     * Returns true iff the given section is visible.
     *
     * @return true iff the given section is visible
     */
    protected boolean sectionIsVisible(Section theSection)
    {
        if (theSection.indentation().intValue() <= 0) // there's a bug that causes negative indentation values, this alleviates the symptoms
        {
            return true;
        }
        else
        {
            Section sectionAbove = theSection;
            do
            {
                sectionAbove = (Section)EOUtilities.localInstanceOfObject(session().defaultEditingContext(),
                        (Section)sectionAbove.closestOutdentedParentSection());
                if (!sectionsShowingChildren().contains(sectionAbove))
                {
                    return false;
                }
            } while (sectionAbove.indentation().intValue() > 0);
            return true;
        }
    }


    /**
     * Returns true iff the current section is visible.
     *
     * @return true iff the current section is visible
     */
    public boolean sectionIsVisible()
    {
        return sectionIsVisible(aSection);
    }


    /**
     * Returns an ordered array of visible sections.
     *
     * @return an ordered array of visible sections
     */
    protected NSArray visibleSections()
    {
        NSMutableArray visibleSections = new NSMutableArray();
        NSArray allSections = orderedSections.ordered();
        Enumeration sectionEnumerator = allSections.objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            Section section = (Section) sectionEnumerator.nextElement();
            if (sectionIsVisible(section))
            {
                visibleSections.addObject(section);
            }
        }
        return visibleSections;
    }


    /**
     * Returns "none" if the section should not be displayed, returns "" otherwise.
     *
     * @return "none" if the section should not be displayed, returns "" otherwise
     */
    public String sectionDisplay()
    {
        if (sectionIsVisible(aSection))
        {
            return "";
        }
        else
        {
            return "display:none;";
        }
    }



    /**
     * Returns true iff the current section has at least 2 groups.
     *
     * @return true iff the current section has at least 2 groups
     */
    public boolean hasAtLeastTwoGroups()
    {
        return aSection.groups().count() > 1;
    }


    /**
     * Returns true iff there is at least one group showing children.
     *
     * @return true iff there is at least one group showing children
     */
    public boolean hasAtLeastOneGroupExpanded()
    {
        return sectionsShowingGroups().count() > 0;
    }


    /**
     * Sets the view to expand all if no groups are expanded, otherwise sets the view to collapse all.
     */
    public void toggleExpandCollapseAllGroups()
    {
        if (hasAtLeastOneGroupExpanded())
        {
            sectionsShowingGroups().removeAllObjects();
        }
        else
        {
            sectionsShowingGroups().removeAllObjects();
            sectionsShowingGroups().addObjectsFromArray(EOUtilities.localInstancesOfObjects(session().defaultEditingContext(), website().sections()));
        }
    }


    /**
     * Returns true iff this section shows its groups.
     *
     * @return true iff this section shows its groups
     */
    public boolean sectionShowsGroups()
    {
        Section theSection = (Section) EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aSection);
        return sectionsShowingGroups().contains(theSection);
    }


    /**
     * Toggles showing groups.
     */
    public void toggleShowGroups()
    {
        Section theSection = (Section) EOUtilities.localInstanceOfObject(session().defaultEditingContext(), aSection);
        if (sectionsShowingGroups().contains(theSection))
        {
            sectionsShowingGroups().removeObject(theSection);
        }
        else
        {
            sectionsShowingGroups().addObject(theSection);
        }
    }



    /**
     * Returns the width of the spacer used to visually indent section names.
     *
     * @return width of the spacer used to visually indent section names
     */
    public String sectionIndentationStyle()
    {
        int indentation = aSection.indentation().intValue();
        int total = indentation * 18;

        return "padding-left: " + total + "px;";
    }



    /**
     * Indents the section.
     *
     * @return this page
     */
    public void indentSection()
    {
        if (aSection.canIndent())
        {
            website().indentSection(aSection);
            website().editingContext().saveChanges();
        }
    }



    /**
     * Outdents the section.
     *
     * @return this page
     */
    public void outdentSection()
    {
        if (aSection.canOutdent())
        {
            website().outdentSection(aSection);
            website().editingContext().saveChanges();
        }
    }



    /**
     * Action used to open a given section for editing.
     *
     * @return the <code>EditSection</code> page set for the given section.
     */
    public EditSection editSection()
    {
        EditSection nextPage = (EditSection) pageWithName("EditSection");
        nextPage.setSection(aSection);
        if (aSection.isVersionable())
        {
            nextPage.setVersion(aSection.component().version());
        }
        return nextPage;
    }



    /**
     * Method to configure an existing section.
     *
     * @return the <code>AddSection</code> page is returned and configured for modifying the given section.
     */
    public WOComponent configSection()
    {
        AddSection nextPage = (AddSection) pageWithName("AddSection");
        nextPage.setCurrentSection(aSection);
        return nextPage;
    }



    /**
     * Method to configure mulitple sections.
     *
     * @return the EditMultipleSections page
     */
    public WOComponent configMultipleSections()
    {
        EditMultipleSections nextPage = (EditMultipleSections) pageWithName("EditMultipleSections");

        Enumeration sectionEnumerator = selectedSections.objectEnumerator();
        NSMutableArray sectionsWithAccessProperties = new NSMutableArray();
        while (sectionEnumerator.hasMoreElements())
        {
            Section section = (Section)sectionEnumerator.nextElement();
            if (section.type().usesAccessProtection(section))
            {
                sectionsWithAccessProperties.addObject(section);
            }
        }

        if (sectionsWithAccessProperties.count() > 0)
        {
            nextPage.setSectionsToEdit(sectionsWithAccessProperties);
            return nextPage;
        }
        else
        {
            return context().page();
        }
    }



    /**
     * Returns the URL to preview this section.
     *
     * @return the URL to preview this section
     */
    public String sectionPreviewUrl()
    {
        return aSection.sectionURL(context().request()) + (aSection.isExternalReference() ? "" : SectionDisplay.configFlagForSession(session()));
    }



    /**
     * Adds or removes the current section from the list of selected sections.
     *
     * @param selected is the current section selected?
     */
    public void setSectionSelected(boolean selected)
    {
        if (selected)
        {
            selectedSections.addObject(aSection);
        }
        else
        {
            selectedSections.removeObject(aSection);
        }
    }


    /**
     * Returns true iff the current section is in the list of selected sections.
     *
     * @return true iff the current section is in the list of selected sections
     */
    public boolean isSectionSelected()
    {
        return selectedSections.containsObject(aSection);
    }


    /**
     * Returns the CSS class to use for the current section in the list.
     *
     * @return the CSS class to use for the current section in the list
     */
    public String sectionListCSSClass()
    {
        return (selectedSections.contains(aSection)) ? "selectedSection" : "";
    }



    /**
     * Returns the CSS class to use for the current section in the list.
     *
     * @return the CSS class to use for the current section in the list
     */
    public String viewSectionHyperlinkCSSClass()
    {
        return aSection.isNavigable().booleanValue() ? "" : "sectionNameHidden";
    }



    /**
     * Toggles the show/hide setting of each selected section.
     */
    public void toggleShowHideSelectedSections()
    {
        NSMutableSet sectionsToToggle = selectedSections.mutableClone();
        // can't hide the home section
        if (sectionsToToggle.contains(website().homeSection()))
        {
            sectionsToToggle.removeObject(website().homeSection());
        }

        EOEditingContext toggleContext = ((SMSession) session()).newEditingContext();

        Enumeration sectionsToToggleEnumerator = sectionsToToggle.objectEnumerator();
        while (sectionsToToggleEnumerator.hasMoreElements())
        {
            Section sectionToToggle = (Section)EOUtilities.localInstanceOfObject(toggleContext, (Section)sectionsToToggleEnumerator.nextElement());
            sectionToToggle.setIsNavigable(new GVCBoolean(!sectionToToggle.isNavigable().booleanValue()));
            DebugOut.println(3, "Toggling show/hide of " + sectionToToggle.name());
        }
        toggleContext.saveChanges();
        ((SMSession) session()).unregisterEditingContext(toggleContext);

        // Need to do this so that we get a new version of sections()
        // invalidateObject does not seem to result in a call to clearCachedValues()
        SMEOUtils.invalidateObject(website());
        website().clearCachedValues();
        orderedSections = new OrderedSectionList(website().orderedSections());
        selectedSections = new NSMutableSet();
    }


    /**
     * Delete the selected sections.
     */
    public void deleteSelectedSections()
    {
        NSMutableSet sectionsToDelete = selectedSections.mutableClone();
        // can't delete the home section
        if (sectionsToDelete.contains(website().homeSection()))
        {
            sectionsToDelete.removeObject(website().homeSection());
        }

        EOEditingContext deleteContext = ((SMSession) session()).newEditingContext();
        Website localSite = (Website) EOUtilities.localInstanceOfObject(deleteContext, website());

        Enumeration sectionsToDeleteEnumerator = sectionsToDelete.objectEnumerator();
        while (sectionsToDeleteEnumerator.hasMoreElements())
        {
            Section sectionToDelete = (Section) EOUtilities.localInstanceOfObject(deleteContext, (Section)sectionsToDeleteEnumerator.nextElement());
            localSite.removeSection((Section) EOUtilities.localInstanceOfObject(deleteContext, sectionToDelete));
            DebugOut.println(3, "Deleting " + sectionToDelete.name());
        }
        deleteContext.saveChanges();
        ((SMSession) session()).unregisterEditingContext(deleteContext);

        // Need to do this so that we get a new version of sections()
        // invalidateObject does not seem to result in a call to clearCachedValues()
        SMEOUtils.invalidateObject(website());
        website().clearCachedValues();
        orderedSections = new OrderedSectionList(website().orderedSections());
        selectedSections = new NSMutableSet();
    }


    /**
     * Returns a warning string about not being able to delete the home section iff
     * the home section is selected, null otherwise.
     *
     * @return a warning string about not being able to delete the home section
     */
    public String cantDeleteHomeSection()
    {
        return selectedSections.contains(website().homeSection()) ? "The home section will not be deleted.<br/>" : "";
    }



    /**
     * Main dispatch for actions applying to all sections (as checked by the user).
     */
    public void selectedSectionsObserverAction()
    {
        if ("Select All".equals(selectedSectionsAction))
        {
            sectionsShowingChildren().addObjectsFromArray(website().sections());
            selectedSections.addObjectsFromArray(website().sections());
            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteSectionsPane", context());
        }
        else if ("Unselect All".equals(selectedSectionsAction))
        {
            selectedSections.removeAllObjects();
            AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteSectionsPane", context());
        }
        else if ("Set Access on Selected".equals(selectedSectionsAction))
        {
            if (selectedSections.count() > 0)
            {
                AjaxUtils.redirectTo(configMultipleSections());
                selectedSections = new NSMutableSet();
            }
        }
        else if ("Show/Hide Selected".equals(selectedSectionsAction))
        {
            if (selectedSections.contains(website().homeSection()))
            {
                AjaxModalDialog.open(context(), "ToggleShowHideSectionsModalDialog");
            }
            else
            {
                toggleShowHideSelectedSections();
                AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteSectionsPane", context());
            }
        }
        else if ("Delete Selected".equals(selectedSectionsAction))
        {
            if (selectedSections.count() > 0)
            {
                AjaxModalDialog.open(context(), "ConfirmDeleteSectionsModalDialog");
            }
        }

        selectedSectionsAction = null;
    }



    /**
     * Returns the selected choice in the Actions column for the current section.
     *
     * @return the selected choice in the Actions column for the current section
     */
    public String selectedSectionAction()
    {
        return (String) selectedSectionAction.objectForKey(aSection);
    }


    /**
     * Sets the selected action in the Actions column for the current section.
     *
     * @param newAction the action to set
     */
    public void setSelectedSectionAction(String newAction)
    {
        if (newAction != null)
        {
            selectedSectionAction.setObjectForKey(newAction, aSection);
        }
        else
        {
            selectedSectionAction.removeObjectForKey(aSection);
        }
    }



    /**
     * Delete the current section.
     */
    public void deleteSection()
    {
        /** require [sectionToDelete_not_null] sectionToDelete != null; **/

        EOEditingContext deleteContext = ((SMSession) session()).newEditingContext();
        Website localSite = (Website) EOUtilities.localInstanceOfObject(deleteContext, website());

        localSite.removeSection((Section) EOUtilities.localInstanceOfObject(deleteContext, sectionToDelete));
        DebugOut.println(3, "Deleting " + sectionToDelete.name());

        deleteContext.saveChanges();
        ((SMSession) session()).unregisterEditingContext(deleteContext);

        // Need to do this so that we get a new version of sections()
        // invalidateObject does not seem to result in a call to clearCachedValues()
        SMEOUtils.invalidateObject(website());
        website().clearCachedValues();
        orderedSections = new OrderedSectionList(website().orderedSections());

        sectionToDelete = null;
    }



    /**
     * Returns the list of actions appropriate for the selected section.
     *
     * @return a list of actions as strings
     */
    public NSArray actionsList()
    {
        NSMutableArray actionsList = new NSMutableArray();
        if (aSection.canBeModified())
        {
            actionsList.addObject("Edit Content"); // editSection
            actionsList.addObject("Edit Properties"); // configSection

            if (isDASection())
            {
                actionsList.addObject("Email Notification"); // editNotification
            }

            if (canViewVersions() && (!(aSection.component() instanceof MixedMediaPaneArrangement)))
            {
                actionsList.addObject("Manage Versions"); // viewVersions
            }

            if (aSection.isNavigable().booleanValue() && (!aSection.isHomeSection()))
            {
                actionsList.addObject("Hide");
            }
            else if (!aSection.isHomeSection())
            {
                actionsList.addObject("Unhide");
            }

            if (aSection.canBeDeleted())
            {
                actionsList.addObject("Delete"); // deleteSection
            }
        }

        return actionsList;
    }


    public void testAction()
    {
        AjaxUtils.redirectTo(editSection());
    }



    /**
     * Main dispatch for actions applying to a single section.
     */
    public WOComponent sectionRepetitionObserverAction()
    {
        if (selectedSectionAction.count() > 0)
        {
            // should only be a single key and value in the dictionary
            aSection = (Section) selectedSectionAction.allKeys().lastObject();
            String actionForCurrentSection = (String) selectedSectionAction.objectForKey(aSection);

            if ("Edit Content".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(editSection());
                return null;
            }
            else if ("Edit Properties".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(configSection());
                return null;
            }
            else if ("Email Notification".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(editNotification());
                return null;
            }
            else if ("Manage Versions".equals(actionForCurrentSection))
            {
                AjaxUtils.redirectTo(viewVersions());
                return null;
            }
            else if ("Hide".equals(actionForCurrentSection) || "Unhide".equals(actionForCurrentSection))
            {
                aSection.setIsNavigable(new GVCBoolean(!aSection.isNavigable().booleanValue()));
                website().editingContext().saveChanges();
            }
            else if ("Delete".equals(actionForCurrentSection))
            {
                sectionToDelete = aSection;
                AjaxModalDialog.open(context(), "ConfirmDeleteSectionModalDialog");
            }

            selectedSectionAction = new NSMutableDictionary();
        }

        // update even if there is no action, as we might be called when the user checks a checkbox
        AjaxUpdateContainer.updateContainerWithID("ConfigureWebsiteSectionsPane", context());
        return context().page();
    }



    /**
     * Returns the class for the outdent image.
     *
     * @return the class for the outdent image
     */
    public String outdentClass()
    {
        return aSection.canOutdent() ? "" : "inactiveButton";
    }


    /**
     * Returns the class for the indent image.
     *
     * @return the class for the indent image
     */
    public String indentClass()
    {
        return aSection.canIndent() ? "" : "inactiveButton";
    }



    /**
     * Returns the sections that are showing children. When initially null, gets
     * the view data from the session.
     *
     * @return the sections that are showing children
     */
    public NSMutableSet sectionsShowingChildren()
    {
        // if it is null, get the value from the session
        if (sectionsShowingChildren == null)
        {
            NSDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("ConfigureWebsiteSectionsPane", website().siteID());
            sectionsShowingChildren = (NSMutableSet) viewData.objectForKey("sectionsShowingChildren");
        }

        // if it is still null, set it to the empty set
        if (sectionsShowingChildren == null)
        {
            NSMutableDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("ConfigureWebsiteSectionsPane", website().siteID());
            sectionsShowingChildren = new NSMutableSet();
            viewData.setObjectForKey(sectionsShowingChildren, "sectionsShowingChildren");
        }

        return sectionsShowingChildren;

        /** ensure Result != null; **/
    }



    /**
     * Returns the sections that are showing groups. When initially null, gets
     * the view data from the session.
     *
     * @return the sections that are showing groups
     */
    public NSMutableSet sectionsShowingGroups()
    {
        // if it is null, get the value from the session
        if (sectionsShowingGroups == null)
        {
            NSDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("ConfigureWebsiteSectionsPane", website().siteID());
            sectionsShowingGroups = (NSMutableSet) viewData.objectForKey("sectionsShowingGroups");
        }

        // if it is still null, set it to the empty set
        if (sectionsShowingGroups == null)
        {
            NSMutableDictionary viewData = ((SMSession) session()).viewDataForPageNameAndEditingObject("ConfigureWebsiteSectionsPane", website().siteID());
            sectionsShowingGroups = new NSMutableSet();
            viewData.setObjectForKey(sectionsShowingGroups, "sectionsShowingGroups");
        }

        return sectionsShowingGroups;

        /** ensure Result != null; **/
    }



    public Website website()
    {
        return website;
    }



    public void setWebsite(Website newWebsite)
    {
        website = newWebsite;

        if (website() != null)
        {
            orderedSections = new OrderedSectionList(website().orderedSections());
        }
    }



    // selector used in next method
    protected static final NSSelector canBeModified = new NSSelector("canBeModified");


    /**
     * Returns true iff the page should auto-update.
     *
     * @return true iff the page should auto-update
     */
    public boolean shouldAutoUpdate()
    {
        try
        {
            if (Collection.doAnyObjectsRespond(orderedSections.ordered(), new Boolean(false), canBeModified, null))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return false;
    }


    /**
     * Returns the interval between automatic refreshes on the page.
     *
     * @return the interval between automatic refreshes on the page
     */
    public int autoUpdateFrequency()
    {
        return shouldAutoUpdate() ? 10 : 0;
    }


    public String copyingResourcesLongResponseId()
    {
        return "copyingResourcesLongResponseId" + sectionIndex;
    }


    public String twistSectionsHyperlinkOnLoadingScript()
    {
        return "function () { var busyRow = document.getElementById('" + busyRowID() + "'); busyRow.style.display=''; }";
    }


    public String twistSectionsHyperlinkOnCompleteScript()
    {
        return "function () { var busyRow = document.getElementById('" + busyRowID() + "'); busyRow.style.display='none'; }";
    }


    public String busyRowID()
    {
        return "busyRow" + sectionIndex;
    }


}
