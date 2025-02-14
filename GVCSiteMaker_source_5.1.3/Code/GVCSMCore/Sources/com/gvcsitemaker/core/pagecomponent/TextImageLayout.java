// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * TextImageLayout extends Layout by constraining it to an ordered list of two
 * child PageComponents: and Image and a Text. The ordering of these components
 * and the image's alignment are controlled so that the text appears on the
 * left, right, above, or below the image.
 */
public class TextImageLayout extends _TextImageLayout
{
    // Layout modes
    public static final String TextAfterImageLayout = "Text after image";
    public static final String TextBeforeImageLayout = "Text before image";
    public static final String TextOverImageLayout = "Text over image";
    public static final String TextUnderImageLayout = "Text under image";

    // Binding keys
    public static final String EditorModeKey = "editorMode";

    // Editor modes
    public static final String PlainTextMode = "Plain Text Editor";
    public static final String RichTextMode = "Rich Text Editor";


    /**
     * Factory method to create new instances of TextImageLayout.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of TextImageLayout or a subclass.
     */
    public static TextImageLayout newTextImageLayout()
    {
        return (TextImageLayout) SMEOUtils.newInstanceOf("TextImageLayout");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("TextImageLayout");

        addChild(Text.newText());
        addChild(Image.newImage());

        // Default Layout
        setSubType(TextUnderImageLayout);

        // Default Mode
        setEditorMode(RichTextMode);
    }



    /**
     * Orders the the child components based on the subType() of this object.
     */
    public void layoutComponents()
    {
        DebugOut.println(22, "Entering ...");

        // The re-ordered list of components
        NSMutableArray orderedComponents = new NSMutableArray();

        // Define placeholders for the child components
        Text theText = textComponent();
        Image theImage = imageComponent();

        if (subType().equals(TextAfterImageLayout))
        {
            orderedComponents.addObject(theImage);
            orderedComponents.addObject(theText);
            theImage.setAlign(Image.ALIGN_LEFT);

        }
        else if (subType().equals(TextBeforeImageLayout))
        {
            orderedComponents.addObject(theImage);
            orderedComponents.addObject(theText);
            theImage.setAlign(Image.ALIGN_RIGHT);
        }
        else if (subType().equals(TextOverImageLayout))
        {
            orderedComponents.addObject(theText);
            orderedComponents.addObject(theImage);
            theImage.setAlign(Image.ALIGN_CENTER);

        }
        else if (subType().equals(TextUnderImageLayout))
        {
            orderedComponents.addObject(theImage);
            orderedComponents.addObject(theText);
            theImage.setAlign(Image.ALIGN_CENTER);
        }
        else
        {
            throw new RuntimeException("Unkown subType: " + subType());
        }

        setComponentOrderFrom(orderedComponents);

        /** ensure [correct_number_of_components] orderedComponents().count() == toChildren().count(); **/
    }



    /**
     * Returns the Image in this TextImageLayout.
     *
     * @return the Image in this TextImageLayout.
     */
    public Image imageComponent()
    {
        Image imageComponent = null;

        Enumeration componentEnumerator = toChildren().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent thisComponent = (PageComponent) componentEnumerator.nextElement();

            if (thisComponent instanceof Image)
            {
                imageComponent = (Image) thisComponent;
                break;
            }
        }

        return imageComponent;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the Text in this TextImageLayout.
     *
     * @return the Text in this TextImageLayout.
     */
    public Text textComponent()
    {
        Text textComponent = null;

        Enumeration componentEnumerator = toChildren().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent thisComponent = (PageComponent) componentEnumerator.nextElement();

            if (thisComponent instanceof Text)
            {
                textComponent = (Text) thisComponent;
                break;
            }
        }

        return textComponent;

        /** ensure [result_not_null] Result != null; **/
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



    //************** Binding Cover Methods **************\\

    public String editorMode()
    {
        return (String) (hasValueForBindingKey(EditorModeKey) ? valueForBindingKey(EditorModeKey) : PlainTextMode);
    }


    public void setEditorMode(Object aMode)
    {
        setBindingForKey(aMode == null ? aMode : aMode.toString(), EditorModeKey);
    }


}
