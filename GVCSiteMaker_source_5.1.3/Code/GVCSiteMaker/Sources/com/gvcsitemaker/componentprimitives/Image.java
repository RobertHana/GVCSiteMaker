// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.pages.FileReplace;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;


/**
 * This is used to display and edit the Image sub-class of PageComponent.
 */
public class Image extends ComponentPrimitive 
{

    // For editing
    public SiteFile anImage;
    public String widthValidationError;

    /**
     * Designated constructor.
     */
    public Image(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * Overridden to clear validation messages
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        widthValidationError = null;
    }


    
    /**
     * Returns <code>true</code> if this Image is to be aligned center.  If the image is centered, it must be surrounded by &lt;center&gt;&lt;/center&gt; as this cannot be done on the IMG tag which only accepts Left | Right | Top | Texttop | Middle | Absmiddle | Baseline | Bottom | Absbottom.
     * Apparently this is the same effect as using 'middle' which is what should have been done.  As both the center tag and the align attribute on images are deprecated, fixing this has been left until later.  -ch
     *
     * @return <code>true</code> if this Image is to be aligned center.
     */
    public boolean isCentered()
    {
        return ((com.gvcsitemaker.core.pagecomponent.Image)componentObject()).align().equals("center");
    }



    /**
     * Returns a width tag for this image if a width has been specified.  This is appended to the image via otherTagString
     *
     * @return a width tag for this image if a width has been specified.
     */
    public String imageSize()
    {
        String otherTagString = "";

        if (hSize() != null)
        {
            otherTagString = "width = \"" + hSize() + "\" ";
        }

        return otherTagString;
    }



    /**
     * Returns the hSize value for this image.  Returns null if the set value is null or invalid.
     *
     * @return a width tag for this image if a width has been specified.
     */
    public Integer hSize()
    {
        String hSize = ((com.gvcsitemaker.core.pagecomponent.Image) componentObject()).hSize();
        Integer resultSize = null;
        
        if (hSize != null)
        {
            try
            {
                resultSize = new Integer(hSize);
            }
            catch (NumberFormatException e)
            {
                // Bad data, just ignore the setting.  This should only happen for historical data
            }
        }

        return resultSize;
    }

    
    
    /**
     * Sets the hSize on the Image.
     * 
     * @param newSize the new size to set
     */
    public void setHSize(Integer newSize)
    {
        if (newSize == null)
        {
            ((com.gvcsitemaker.core.pagecomponent.Image) componentObject()).setHSize((Integer)null);
        }
        else
        {
            validateTakeValueForKeyPath(newSize, "componentObject.hSize");
        }
    }
    
    
    
    /**
     * @return the SiteFile's URL customized for the domain in this request.
     */
    public String siteFileUrl() 
    {
        return componentObject().relatedFile().url(context().request());
    }
    
    
    
    /**
     * Action method to replace a SiteFile.
     *
     * @return an instance of the FileUpload page
     */
    public WOComponent replaceFile()
    {
        FileReplace nextPage = (FileReplace)pageWithName("FileReplace");
        nextPage.setCurrentFile(componentObject().relatedFile());

        return nextPage;
    }    
    
    
    
    /**
     * Returns true if replace link should be shown, false otherwise
     *
     * @return true if replace link should be shown, false otherwise
     */    
    public boolean shouldShowReplaceLink()
    {
        return componentObject().relatedFile() != null;
    }
    
    
    
    /**
     * Translates validation faliure expections into UI error messages.
     */
    public void validationFailedWithException(Throwable exception, Object value, String keyPath) 
    {
        if (keyPath.equals("hSize") ||  keyPath.equals("componentObject.hSize"))
        {
            widthValidationError = "The width must be blank, or a number larger than zero.";
        }
        else super.validationFailedWithException(exception, value, keyPath);
    }
}
