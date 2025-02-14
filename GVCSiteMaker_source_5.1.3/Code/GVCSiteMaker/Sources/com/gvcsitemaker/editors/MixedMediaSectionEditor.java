// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.editors;

import java.util.Enumeration;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPane;
import com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement;
import com.gvcsitemaker.core.pagecomponent.MixedMediaUnknownPane;
import com.gvcsitemaker.pages.*;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;


/*
 * * This editor used by EditSection to edit a MixedMedia Pane Arrangement.
 */
public class MixedMediaSectionEditor extends BaseEditor
{
    public String aLayout; // Used in a WORepetition for to display the alternative layouts for a MixedMedia section.
    public NSArray layouts; // Alternative layouts for a MixedMedia section.
    public NSDictionary layoutImageDictionary;// Contains alternative layouts and its corresponding image name
    public MixedMediaPane aComponent;


    /**
     * Designated constructor
     */
    public MixedMediaSectionEditor(WOContext aContext)
    {
        super(aContext);

        // Initialize list of layouts
        layouts = MixedMediaPaneArrangement.Layouts;

        NSArray layoutImages = new NSMutableArray(new String[]
        { "OnePaneLayout", "TwoPanesSideBySideLayout", "TwoPanesTopAndBottomLayout", "ThreePanesSideBySideOnBottomLayout", "ThreePanesSideBySideOnTopLayout",
                "ThreePanesTopAndBottomOnLeftLayout", "ThreePanesTopAndBottomOnRightLayout", "FourPanesLayout" });

        layoutImageDictionary = new NSDictionary(layoutImages, layouts);
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Returns the URL for the image layouts
     * 
     * @return the URL for the image layouts
     */
    public String imageURLForLayout()
    {
        return "/GVCSiteMaker/Application/MMImages/" + layoutImageDictionary.objectForKey(aLayout) + ".gif";

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overridden to handle deleting unused criteria.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);
        layout().initializeLayout();
    }



    /**
     * Returns the LayoutComponent for this MixedMedia section.
     *
     * @return the LayoutComponent for this MixedMedia section.
     */
    public com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement layout()
    {
        return (com.gvcsitemaker.core.pagecomponent.MixedMediaPaneArrangement) section().component();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Update unknown panes with selected pane types..
     */
    public void updateSelectedPaneTypes()
    {
        NSArray origOrderedComponents = layout().orderedPanes();
        Enumeration paneEnumerator = origOrderedComponents.objectEnumerator();
        while (paneEnumerator.hasMoreElements())
        {
            MixedMediaPane aPane = (MixedMediaPane) paneEnumerator.nextElement();
            if ((aPane instanceof MixedMediaUnknownPane) && (aPane.typeName() != null))
            {
                layout().replacePane(aPane, aPane.typeName());
            }
        }
    }



    /**
     * Overriden to handle panes with selected types.
     */
    public WOComponent updateSettings()
    {
        updateSelectedPaneTypes();

        return super.updateSettings();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Overriden to handle panes with selected types.
     *
     * @return preview page
     */
    public WOComponent updateSettingsAndPreview()
    {
        updateSelectedPaneTypes();

        return super.updateSettingsAndPreview();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Save changes if there is no error and returns ConfigTabSet, returns this page otherwise
     *
     * @return returns ConfigTabSet if no errors, returns this page otherwise
     */
    public WOComponent updateAndClose()
    {
        WOComponent nextPage = updateSettings();

        if (nextPage.name().indexOf("ErrorPage") == -1)
        {
            nextPage = pageWithName("ConfigTabSet");
            ((ConfigTabSet)nextPage).setWebsite(website());
            ((Session)session()).setEditingWebsite(website());
        }

        return nextPage;

        /** ensure [valid_result] Result != null;  **/
    }
}
