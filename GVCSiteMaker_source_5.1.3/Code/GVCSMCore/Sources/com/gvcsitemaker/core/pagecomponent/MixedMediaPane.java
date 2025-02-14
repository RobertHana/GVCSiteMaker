// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.

package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.mixedmedia.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/*
 * * MixedMediaPane is PageComponent that child of MixedMediaPaneArrangement,
 * abstract superclass for all panes in MixedMediaPaneArrangement, handles media
 * type, knows if has a media type or unknown, knows if media type is
 * displayable in iFrame, manages iFrame height, manages description of pane
 */
public class MixedMediaPane extends _MixedMediaPane
{

    protected MixedMediaContentConfiguration contentConfiguration;

    // Binding keys
    public static final String HeightKey = "iFrameHeight";
    public static final String IsWidthRequiredKey = "isWidthRequired";
    public static final String IsVisibleKey = "isVisible";
    public static final String TypeNameKey = "typeName";
    public static final String DescriptionKey = "description";
    public static final String UsesIFrameKey = "usesIFrame";
    public static final String RequiresIFrameKey = "requiresIFrame";
    public static final String DisplayInIFrameKey = "displayInIFrame";
    public static final String URIBindingKey = "uri";
    public static final String SourceTypeKey = "sourceType";


    /**
     * Factory method to create new instances of MixedMediaPane.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaPane or a subclass.
     */
    public static MixedMediaPane newMixedMediaPane()
    {
        return (MixedMediaPane) SMEOUtils.newInstanceOf("MixedMediaPane");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("MixedMediaPane");

        //Set defaults
        setIsWidthRequired(false);
        setIsVisible(false);
        setUsesIFrame(false);
        setRequiresIFrame(false);
        setDisplayInIFrame(false);
    }



    /**
     * Returns the content type as "typeName: description" for display if description is not null, else returns the typeName
     *
     * @return the content type as "typeName: description" for display
     */
    public String contentDescription()
    {
        String contentDescription = typeName();

        if (description() != null)
        {
            contentDescription = contentDescription + ": " + description();
        }

        return contentDescription;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Convenience method that returns the arrangement (section) that this pane belongs to.
     * 
     * @return returns the arrangement that this pane belongs to.
     */
    public MixedMediaPaneArrangement arrangement()
    {
        return (MixedMediaPaneArrangement) toParent();

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the order of this pane based on the arrangement
     *
     * @return the order of this pane based on the arrangement
     */
    public int order()
    {
        /** require
        [arrangement_orderedPanes_is_not_null] arrangement().orderedPanes() != null;
        [arrangement_orderedPanes_contains_this] arrangement().orderedPanes().containsObject(this); **/

        return arrangement().orderedPanes().indexOfObject(this) + 1;
    }



    /**
     * Returns true if this pane is a MixedMediaConfigurableContentPane
     *
     * @return true if this pane is a MixedMediaConfigurableContentPane
     */
    public boolean isMixedMediaConfigurableContentPane()
    {
        return (this instanceof MixedMediaConfigurableContentPane);
    }



    /**
     * Returns true if this pane is a MixedMediaUnknownPane
     *
     * @return true if this pane is a MixedMediaUnknownPane
     */
    public boolean isMixedMediaUnknownPane()
    {
        return (this instanceof MixedMediaUnknownPane);
    }



    /**
     * Returns true if this pane is a MixedMediaTextContentPane
     *
     * @return true if this pane is a MixedMediaTextContentPane
     */
    public boolean isMixedMediaTextContentPane()
    {
        return (this instanceof MixedMediaTextContentPane);
    }



    /**
     * Returns the MixedMediaContentConfiguration for this type of pane.
     * 
     * @return MixedMediaContentConfiguration for this type of pane
     */
    public MixedMediaContentConfiguration contentConfiguration()
    {
        if (contentConfiguration == null)
        {
            contentConfiguration = MixedMediaContentConfiguration.configurableContent(SMApplication.mixedMediaContentConfigurations(), typeName());
        }

        return contentConfiguration;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to access this MixedMediaPane using the domain defined in 
     * GVCSiteMaker.plist as DomainName.  http:// or https:// is used as 
     * indicated by requiresSSLConnection().
     *
     * @return the URL to access this SiteFile using the domain defined in 
     * GVCSiteMaker.plist as DomainName  
     */
    public String contentURL(WORequest aRequest)
    {
        return SMActionURLFactory.paneContentsURL(aRequest, componentIdAsString());
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the template for this pane when displayed in an iFrame
     * 
     * @return the template for this pane when displayed in an iFrame
     */
    public String iFrameTemplate()
    {
        return "<IFRAME src=\"<webobject name=SourceURL></webobject>\" height=\"<webobject name=Height></webobject>\" width=\"100%\"></IFRAME>";
        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the wod bindings for this pane when displayed in an iFrame
     * 
     * @return the wod bindings for this pane when displayed in an iFrame
     */
    public String iFrameBindings(WORequest aRequest)
    {
        StringBuffer wodFile = new StringBuffer(2048);

        //binding for _SourceURLAsDownload_
        wodFile.append("SourceURL" + ": WOString {\n");
        wodFile.append("    value = contentURL;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        //binding for _SourceURLAsDownload_
        wodFile.append("Height" + ": WOString {\n");
        wodFile.append("    value = displayPane.height;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        return wodFile.toString();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Validate height to be valid and must not be blank if required.
     * 
     * @param newValue the value to validate
     * @return validated value
     * @throws NSValidation.ValidationException
     */
    public Object validateHeight(Object newValue) throws NSValidation.ValidationException
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
                    throw new NSValidation.ValidationException("The height must be blank, or a number larger than zero.");
                }
            }

            if (((Integer) value).intValue() < 1)
            {
                throw new NSValidation.ValidationException("The height must be blank, or a number larger than zero.");
            }

        }
        else if (displayInIFrame() || (isMixedMediaConfigurableContentPane() && contentConfiguration().requiresIFrame()))
        {
            throw new NSValidation.ValidationException("A height greater than zero is required.");
        }

        return newValue;
    }


    //************** Binding Cover Methods **************\\

    public String height()
    {
        return (String) valueForBindingKey(HeightKey);
    }


    public void setHeight(Object aHeight)
    {
        setBindingForKey(aHeight == null ? aHeight : aHeight.toString(), HeightKey);
    }


    public boolean isWidthRequired()
    {
        return booleanValueForBindingKey(IsWidthRequiredKey);
    }


    public void setIsWidthRequired(boolean isWidthRequired)
    {
        setBooleanValueForBindingKey(isWidthRequired, IsWidthRequiredKey);
    }


    public boolean isVisible()
    {
        return booleanValueForBindingKey(IsVisibleKey);
    }


    public void setIsVisible(boolean isVisible)
    {
        setBooleanValueForBindingKey(isVisible, IsVisibleKey);
    }


    public String typeName()
    {
        return (String) valueForBindingKey(TypeNameKey);
    }


    public void setTypeName(Object aTypeName)
    {
        setBindingForKey(aTypeName == null ? aTypeName : aTypeName.toString(), TypeNameKey);
    }


    public String description()
    {
        return (String) valueForBindingKey(DescriptionKey);
    }


    public void setDescription(Object aDescription)
    {
        setBindingForKey(aDescription == null ? aDescription : aDescription.toString(), DescriptionKey);
    }


    public boolean usesIFrame()
    {
        return booleanValueForBindingKey(UsesIFrameKey);
    }


    public void setUsesIFrame(boolean aValue)
    {
        setBooleanValueForBindingKey(aValue, UsesIFrameKey);
    }


    public boolean requiresIFrame()
    {
        return booleanValueForBindingKey(RequiresIFrameKey);
    }


    public void setRequiresIFrame(boolean aValue)
    {
        setBooleanValueForBindingKey(aValue, RequiresIFrameKey);
    }


    public boolean displayInIFrame()
    {
        return booleanValueForBindingKey(DisplayInIFrameKey);
    }


    public void setDisplayInIFrame(boolean aValue)
    {
        setBooleanValueForBindingKey(aValue, DisplayInIFrameKey);
    }


    public String uri()
    {
        return (String) valueForBindingKey(URIBindingKey);
    }


    public void setUri(String aURI)
    {
        setBindingForKey(aURI, URIBindingKey);
    }


    public String sourceType()
    {
        return (String) valueForBindingKey(SourceTypeKey);
    }


    public void setSourceType(String aType)
    {
        setBindingForKey(aType, SourceTypeKey);
    }

}
