// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * MixedMediaPaneArrangement is a PageComponent with child for each of four
 * panes (initially MixedMediaUnknownPane and will be replaced by a subclass of
 * MixedMediaPane when set), knows different pane arrangements, knows selected
 * pane arrangement, manages root panel width, has methods to return each
 * individual pane
 */
public class MixedMediaPaneArrangement extends _MixedMediaPaneArrangement
{
    // Binding keys
    public static final String EditorModeKey = "editorMode";

    // Editor modes
    public static final String PlainTextMode = "Plain Text Editor";
    public static final String RichTextMode = "Rich Text Editor";

    // Layout modes
    public static final String OnePaneLayout = "One pane";
    public static final String TwoPanesSideBySideLayout = "Two panes, side by side";
    public static final String TwoPanesTopAndBottomLayout = "Two panes, top and bottom";
    public static final String ThreePanesSideBySideOnBottomLayout = "Three panes,side by side on bottom";
    public static final String ThreePanesSideBySideOnTopLayout = "Three panes, side by side on top";
    public static final String ThreePanesTopAndBottomOnLeftLayout = "Three panes, top and bottom on left";
    public static final String ThreePanesTopAndBottomOnRightLayout = "Three panes, top and bottom on right";
    public static final String FourPanesLayout = "Four panes";
    public static final NSArray Layouts = new NSArray(new String[]
    { OnePaneLayout, TwoPanesSideBySideLayout, TwoPanesTopAndBottomLayout, ThreePanesSideBySideOnBottomLayout, ThreePanesSideBySideOnTopLayout,
            ThreePanesTopAndBottomOnLeftLayout, ThreePanesTopAndBottomOnRightLayout, FourPanesLayout });

    // Defaults
    public static final String DefaultRootPanelWidth = "50";

    // Binding keys
    public static final String RootPanelWidthKey = "rootPanelWidth";

    protected NSArray cachedOrderedPanes = null;


    /**
     * Factory method to create new instances of MixedMediaPaneArrangement.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaPaneArrangement or a subclass.
     */
    public static MixedMediaPaneArrangement newMixedMediaPaneArrangement()
    {
        return (MixedMediaPaneArrangement) SMEOUtils.newInstanceOf("MixedMediaPaneArrangement");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("MixedMediaPaneArrangement");

        // Create children for each of the pane. maximum allowed is four, at this point, content type for panes are still unknown
        addChild(MixedMediaUnknownPane.newMixedMediaUnknownPane());
        addChild(MixedMediaUnknownPane.newMixedMediaUnknownPane());
        addChild(MixedMediaUnknownPane.newMixedMediaUnknownPane());
        addChild(MixedMediaUnknownPane.newMixedMediaUnknownPane());

        // Default Layout
        setSubType(SMApplication.appProperties().stringPropertyForKey("DefaultMixedMediaLayoutName"));
        initializeLayout();

        //Reset default panelWidth
        setRootPanelWidth(DefaultRootPanelWidth);

        // Default Mode
        setEditorMode(RichTextMode);
    }



    /**
     * Clears all cached values.
     */
    public void clearCachedValues()
    {
        cachedOrderedPanes = null;
        super.clearCachedValues();
    }



    /**
     * Overriden to supply width and visibility requirements.
     */
    public void setSubType(String newSubType)
    {
        super.setSubType(newSubType);
    }



    /**
     * Initialize the layouts with UI specific requirements
     */
    public void initializeLayout()
    {
        //First pane is always visible, initialize width requirement to false
        pane1().setIsVisible(true);
        pane1().setIsWidthRequired(false);
        pane2().setIsWidthRequired(false);
        pane3().setIsWidthRequired(false);
        pane4().setIsWidthRequired(false);

        //One pane, width 100% always
        if (isDisplayedInOnePane())
        {
            pane2().setIsVisible(false);
            pane3().setIsVisible(false);
            pane4().setIsVisible(false);
        }
        //Two panes, side by side, width settable only on pane 1
        if (isDisplayedInTwoPanesSideBySide())
        {
            pane1().setIsWidthRequired(true);
            pane2().setIsVisible(true);
            pane3().setIsVisible(false);
            pane4().setIsVisible(false);
        }
        //Two panes, top and bottom, width 100% always
        if (isDisplayedInTwoPanesTopAndBottom())
        {
            pane2().setIsVisible(true);
            pane3().setIsVisible(false);
            pane4().setIsVisible(false);
        }
        //Three panes, side by side on bottom, width settable only on pane 2
        if (isDisplayedInThreePanesSideBySideOnBottom())
        {
            pane2().setIsVisible(true);
            pane2().setIsWidthRequired(true);
            pane3().setIsVisible(true);
            pane4().setIsVisible(false);
        }
        //Three panes, side by side on top, width settable only on pane 1
        if (isDisplayedInThreePanesSideBySideOnTop())
        {
            pane1().setIsWidthRequired(true);
            pane2().setIsVisible(true);
            pane3().setIsVisible(true);
            pane4().setIsVisible(false);
        }
        //Three panes, top and bottom on left, width settable only on pane 1
        if (isDisplayedInThreePanesTopAndBottomOnLeft())
        {
            pane1().setIsWidthRequired(true);
            pane2().setIsVisible(true);
            pane3().setIsVisible(true);
            pane4().setIsVisible(false);
        }
        //Three panes, top and bottom on right, width settable only on pane 1
        if (isDisplayedInThreePanesTopAndBottomOnRight())
        {
            pane1().setIsWidthRequired(true);
            pane2().setIsVisible(true);
            pane3().setIsVisible(true);
            pane4().setIsVisible(false);
        }
        //Four panes.  Width settable only on pane 1
        if (isDisplayedInFourPanes())
        {
            pane1().setIsWidthRequired(true);
            pane2().setIsVisible(true);
            pane3().setIsVisible(true);
            pane4().setIsVisible(true);
        }
    }



    /**
     * Validate rootPanelWidth to be not blank or an Integer > 0.
     *
     * @param newValue the value to validate
     * @return validated value
     * @throws NSValidation.ValidationException if value is not a number or < 1
     */
    public Object validateRootPanelWidth(Object newValue) throws NSValidation.ValidationException
    {
        Object value = newValue;
        if (value != null)
        {
            if (value instanceof String)
            {
                // Verify that we can coerece it into an Integer
                try
                {
                    value = new Integer((String) newValue);
                }
                catch (NumberFormatException e)
                {
                    throw new NSValidation.ValidationException("The width must not be blank, or a number larger than zero.");
                }
            }

            if ((((Integer) value).intValue() < 1) || (((Integer) value).intValue() > 100))
            {
                throw new NSValidation.ValidationException("The width must not be blank, or a number larger than zero.");
            }

        }
        else
        {
            throw new NSValidation.ValidationException("The width must not be blank, or a number larger than zero.");
        }

        return newValue;
    }



    /**
     * Performs some extra validations, particularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch (NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if ((subType().equals(TwoPanesSideBySideLayout) || subType().equals(ThreePanesSideBySideOnBottomLayout)
                || subType().equals(ThreePanesSideBySideOnTopLayout) || subType().equals(ThreePanesTopAndBottomOnLeftLayout)
                || subType().equals(ThreePanesTopAndBottomOnRightLayout) || subType().equals(FourPanesLayout))
                && (rootPanelWidth() == null))
        {
            //TODO: joee what is right exception:"
            exceptions.addObject(new NSValidation.ValidationException("rootPanelWidth is a required binding for a MexedMediaPaneArrangement"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Returns panes ordered by componentOrder. Implemented for optimization
     *
     * @return bindings(), which are a String, as a dictionary.
     */
    public NSArray orderedPanes()
    {
        // Memory and performance optimization.  Need to refresh if this object is
        // a fault as it may have been refaulted.  If this is the first method called
        // awakeFromFetch() will not have been called yet
        if (isFault() || (cachedOrderedPanes == null))
        {
            cachedOrderedPanes = super.orderedComponents();
        }

        return cachedOrderedPanes;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the first MixedMediaPane in this MixedMediaPaneArrangement.
     *
     * @return the first MixedMediaPane in this MixedMediaPaneArrangement.
     */
    public MixedMediaPane pane1()
    {
        /** require [orderedPanes_count_is_greater_than_zero] orderedPanes().count() > 0; **/

        return (MixedMediaPane) orderedPanes().objectAtIndex(0);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the second MixedMediaPane in this MixedMediaPaneArrangement.
     *
     * @return the second MixedMediaPane in this MixedMediaPaneArrangement.
     */
    public MixedMediaPane pane2()
    {
        /** require [orderedPanes_count_is_greater_than_one] orderedPanes().count() > 1; **/

        return (MixedMediaPane) orderedPanes().objectAtIndex(1);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the third MixedMediaPane in this MixedMediaPaneArrangement.
     *
     * @return the third MixedMediaPane in this MixedMediaPaneArrangement.
     */
    public MixedMediaPane pane3()
    {
        /** require [orderedPanes_count_is_greater_than_two] orderedPanes().count() > 2; **/

        return (MixedMediaPane) orderedPanes().objectAtIndex(2);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the fourth MixedMediaPane in this MixedMediaPaneArrangement.
     *
     * @return the fourth MixedMediaPane in this MixedMediaPaneArrangement.
     */
    public MixedMediaPane pane4()
    {
        /** require [orderedPanes_count_is_greater_than_three] orderedPanes().count() > 3; **/

        return (MixedMediaPane) orderedPanes().objectAtIndex(3);

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Replaces the oldPane with a new pane based on the passed type
     *
     * @param oldPane the pane to replace
     * @param typeName the typeName for the new pane
     */
    public void replacePane(MixedMediaPane oldPane, String typeName)
    {
        /** require [oldPane_is_not_null] oldPane != null;
         		   [toChildren_contains_oldPane] toChildren().containsObject(oldPane);
         **/

        if (typeName == null)
        {
            addChild(MixedMediaUnknownPane.newMixedMediaUnknownPane());
        }
        else if (typeName.equals(MixedMediaTextContentPane.TypeName))
        {
            MixedMediaTextContentPane newPane = MixedMediaTextContentPane.newMixedMediaTextContentPane();
            SectionVersion version = oldPane.arrangement().version();
            int componentOrder = oldPane.arrangement().orderedPanes().indexOf(oldPane);
            if (version.componentOrder() == null || version.componentOrder().intValue() > componentOrder)
            {
                version.setComponentOrder(Integer.valueOf(componentOrder));
            }
            addChild(newPane);
        }
        else
        {
            addChild(MixedMediaConfigurableContentPane.newMixedMediaConfigurableContentPane());
        }

        MixedMediaPane newPane = ((MixedMediaPane) toChildren().lastObject());
        newPane.setTypeName(typeName);
        newPane.setIsVisible(oldPane.isVisible());
        newPane.setIsWidthRequired(oldPane.isWidthRequired());

        NSMutableArray orderedPanes = new NSMutableArray(orderedPanes());

        int componentIndex = orderedPanes.indexOfIdenticalObject(oldPane);
        orderedPanes.removeObject(newPane);
        orderedPanes.insertObjectAtIndex(newPane, componentIndex);

        setComponentOrderFrom(orderedPanes);
        removeChild(oldPane);

        //update cached orderedPanes
        clearCachedValues();

        /** ensure [toChildren_does_not_contain_oldPane] ( ! toChildren().containsObject(oldPane)); **/
    }



    /**
     * Returns true if this TextImageLayout is in RichTextMode.
     *
     * @return true if this TextImageLayout is in RichTextMode.
     */
    public boolean isInRichTextMode()
    {
        return ((editorMode() != null) && editorMode().equals(TextImageLayout.RichTextMode));
    }



    /**
     * Returns true if this TextImageLayout is in PlainTextMode.
     *
     * @return true if this TextImageLayout is in PlainTextMode.
     */
    public boolean isInPlainTextMode()
    {
        return ((editorMode() != null) && editorMode().equals(TextImageLayout.PlainTextMode));
    }



    //************** Convenience conditionals that evaluates subType **************\\
    // only 1 will return true

    public boolean isDisplayedInOnePane()
    {
        return subType().equals(OnePaneLayout);
    }


    public boolean isDisplayedInTwoPanesSideBySide()
    {
        return subType().equals(TwoPanesSideBySideLayout);
    }


    public boolean isDisplayedInTwoPanesTopAndBottom()
    {
        return subType().equals(TwoPanesTopAndBottomLayout);
    }


    public boolean isDisplayedInThreePanesSideBySideOnBottom()
    {
        return subType().equals(ThreePanesSideBySideOnBottomLayout);
    }


    public boolean isDisplayedInThreePanesSideBySideOnTop()
    {
        return subType().equals(ThreePanesSideBySideOnTopLayout);
    }


    public boolean isDisplayedInThreePanesTopAndBottomOnLeft()
    {
        return subType().equals(ThreePanesTopAndBottomOnLeftLayout);
    }


    public boolean isDisplayedInThreePanesTopAndBottomOnRight()
    {
        return subType().equals(ThreePanesTopAndBottomOnRightLayout);
    }


    public boolean isDisplayedInFourPanes()
    {
        return subType().equals(FourPanesLayout);
    }


    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are
    // {rootPanelWidth = "50"}

    public String rootPanelWidth()
    {
        return (String) valueForBindingKey(RootPanelWidthKey);
    }


    public void setRootPanelWidth(Object aRootPanelWidth)
    {
        setBindingForKey(aRootPanelWidth == null ? aRootPanelWidth : aRootPanelWidth.toString(), RootPanelWidthKey);
    }


    public String editorMode()
    {
        return (String) (hasValueForBindingKey(EditorModeKey) ? valueForBindingKey(EditorModeKey) : PlainTextMode);
    }


    public void setEditorMode(Object aMode)
    {
        setBindingForKey(aMode == null ? aMode : aMode.toString(), EditorModeKey);
    }


}
