// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.components;

import net.global_village.foundation.StringAdditions;

import com.gvcsitemaker.core.*;
import com.webobjects.appserver.*;


/**
 * The component either shows a regular TextArea or a RichTextComponent. It
 * displays the RichTextComponent if the value bound to shouldUseRichTextEditor
 * is true.
 */
public class SMWOTextComponent extends net.global_village.woextensions.WOComponent
{
    public static final String defaultColumnSize = "60";
    public static final String defaultRowSize = "26";
    public static final String defaultName = "textArea";


    /**
     * Designated constructor
     */
    public SMWOTextComponent(WOContext context)
    {
        super(context);
    }



    /**
     * This component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * This component does use binding synchronization.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * The value to be displayed in the text editor.
     */
    public String value()
    {
        return (String) valueForBinding("value");
    }



    /**
     * Sets the newValue from the text editor
     */
    public void setValue(String newValue)
    {
        setValueForBinding(newValue, "value");
    }



    /**
     * If true displays component with Rich Text, false otherwise
     */
    public boolean shouldUseRichTextEditor()
    {
        return booleanValueForBinding("shouldUseRichTextEditor", false);
    }



    /**
     * Returns true if there is CSS to be used by the RichText Editor, false otherwise
     */
    public boolean hasEditorCSS()
    {
        return !StringAdditions.isEmpty(editorCSS());
    }



    /**
     * The CSS to be used by the RichText Editor
     */
    public String editorCSS()
    {
        return (String) valueForBinding("editorCSS");
    }



    /**
     * Returns the cols for the textArea
     */
    public String cols()
    {
        return hasBinding("cols") ? valueForBinding("cols").toString() : defaultColumnSize;
    }



    /**
     * Returns the rows for the textArea
     */
    public String rows()
    {
        return hasBinding("rows") ? valueForBinding("rows").toString() : defaultRowSize;
    }



    /**
     * Returns the name of this component
     */
    public String name()
    {
        return hasBinding("name") ? (String) valueForBinding("name") : defaultName;
    }



    /**
     * Returns the wysiwygEditorMode of this component
     */
    public String wysiwygEditorMode()
    {
        return (String) valueForBinding("wysiwygEditorMode");

        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the index of this component with respect to the number of SMWOTextComponent displayed in the page.  You can bind this value to a WORepetition's index or set this value to 0 if there is only one.
     */
    public int index()
    {
        return ((Integer) valueForBinding("index")).intValue();

        /** ensure [result_valid] valueForBinding("index") != null; **/
    }



    /**
     * Returns the Website displaying this component.
     *
     * @return the Website displaying this component.
     */
    public Website website()
    {
        return ((Website) valueForBinding("website"));

        /** enusre [valid_result]  Result != null;  **/
    }



    /**
     * Returns the Section displaying this component.
     *
     * @return the Section displaying this component.
     */
    public Section section()
    {
        return ((Section) valueForBinding("section"));

        /** enusre [valid_result]  Result != null;  **/
    }



    /**
     * If this component is displaying a DataAccessStringColumn, returns the columnName of this DataAccessStringColumn.  Used by TinyMCEComponent.
     *
     * @return the columnName of the DataAccessStringColumn
     */
    public String columnName()
    {
        return ((String) valueForBinding("columnName"));
    }



    /**
     * If this component is displaying a DataAccessStringColumn, returns the current DataAccessMode used in the page. Used by TinyMCEComponent.
     *
     * @return current DataAccessMode used in the page
     */
    public String currentDataAccessMode()
    {
        return ((String) valueForBinding("currentDataAccessMode"));
    }
}
