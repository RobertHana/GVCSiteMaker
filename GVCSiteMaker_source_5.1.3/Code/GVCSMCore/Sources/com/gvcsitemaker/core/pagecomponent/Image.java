// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class Image extends _Image
{


    // Binding keys
    public static final String ALIGN_BINDINGKEY = "align";
    public static final String URL_BINDINGKEY = "url";
    public static final String VSPACE_BINDINGKEY = "vspace";
    public static final String HSPACE_BINDINGKEY = "hspace";
    public static final String TYPE_BINDINGKEY = "type";
    public static final String HSIZE_BINDINGKEY = "hsize";



    /**
     * Factory method to create new instances of Image.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Image or a subclass.
     */
    public static Image newImage()
    {
        return (Image) SMEOUtils.newInstanceOf("Image");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("Image");
        // Default bindings for use in Text/Image section
        setAlign(ALIGN_RIGHT);
        setImageType(URL_TYPE);
        setHSpace("6");
        setVSpace("6");
    }



    /**
     * Returns <code>true</code> if this Image is a link to an existing SiteFile or is an external image with a valid URL.  It is possible for it to be a file image with not associated SiteFile or an external image with no URL.  This is because in a Text / Image section the image is optional.  There might be a better way of handling this optionality, but that refactoring has been left for a later time.  If that is changed, then this functionality can be removed.
     *
     * @return <code>true</code> if this Image is a link to an existing SiteFile or is an external image with a valid URL.  
     */
    public boolean hasImageToShow()
    {
        boolean hasImageToShow = false;

        if (isExternalImage())
        {
            hasImageToShow = hasValueForBindingKey(URL_BINDINGKEY);
        }
        else if (isSiteFileImage())
        {
            hasImageToShow = relatedFile() != null;
        }

        return hasImageToShow;
    }

    

    public boolean isUrlValid()
    {
        // Allow the URL to be missing as it is optional.
        return ( isSiteFileImage() ||
                 ( ! hasValueForBindingKey(URL_BINDINGKEY)) ||
                 URLUtils.hasValidURIScheme(url()) );
    }



    /**
     * Returns <code>true</code> if this Image is a link to an image external to SiteMaker.
     *
     * @return <code>true</code> if this Image is a link to an iamge external to SiteMaker.
     */
    public boolean isExternalImage()
    {
        return imageType().equals(URL_TYPE);
    }



    /**
     * Returns <code>true</code> if this Image is a link to a SiteFile.
     *
     * @return <code>true</code> if this Image is a link to a SiteFile.
     */
    public boolean isSiteFileImage()
    {
        return imageType().equals(SITEFILE_TYPE);
    }


    
    /**
     * Validate hSize to be blank/null or an Integer > 0.
     * 
     * @param newValue the value to validate
     * @return validated value
     * @throws NSValidation.ValidationException if value is not a number or < 1
     */
    public Object validateHSize(Object newValue) throws NSValidation.ValidationException
    {
        Object value = newValue;
        if (value != null)
        {
            if (value instanceof String)
            {
                // Verify that we can coerece it into an Integer
                try
                {
                    value = new Integer((String)newValue);
                }
                catch (NumberFormatException e)
                {
                    throw new NSValidation.ValidationException("The width must be blank, or a number larger than zero.");
                }
            }

            if (((Integer)value).intValue() < 1)
            {
                throw new NSValidation.ValidationException("The width must be blank, or a number larger than zero.");
            }

        }
        
        return newValue;
    }

    
    
    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (vSpace() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("vSpace is a required binding for a Image"));
        }

        if (hSpace() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("hSpace is a required binding for a Image"));
        }

        if (align() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("align is a required binding for a Image"));
        }

        if (imageType() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Link Type is a required binding for a Image"));
        }
        else if ( ! (isSiteFileImage() || isExternalImage()))
        {
            exceptions.addObject(new NSValidation.ValidationException("'" + imageType() + "' is not a valid type for a Image"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }


    
    /**
     * Returns the HTML source for displaying this Image PageComponent. It returns the equivalent <img> tag for this Image if there is a source specified, null otherwise.
     * 
     * @return the HTML source for displaying this Image PageComponent
     */
    public String displayHTML()
    {
    	    String displayHTML = null;
    	    
    		if (hasImageToShow())
    		{
    			if (isSiteFileImage())
    			{
        			displayHTML=  "<img vspace=\"" + vSpace() + "\" align=\"" + align() + "\" alt=\"" + relatedFile().displayName() + "\" width = \"" + hSize() + "\"  hspace=\"" + hSpace() + "\" src=\"" + relatedFile().url() + "\">";
    			}
    			else //isExternalImage
    			{
        			displayHTML=  "<img vspace=\"" + vSpace() + "\" align=\"" + align() + "\" width = \"" + hSize() + "\"  hspace=\"" + hSpace() + "\" src=\"" + url() + "\">";
    			}
    		}
    		
    		return displayHTML;
    }
    
    
    
    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are
    // {vspace = "6"; align = "center"; type = "url";  hsize = "300"; canEdit = "YES"; hspace = "6"; url = "http://www.google.com/home.gif"; }
    // {vspace = "6"; align = "center"; type = "file"; hsize = "150"; canEdit = "YES"; hspace = "6"; url = ""; }

    // The align binding is legacy cruft and not used or referenced anywhere.
    // Sometimes there is a binding of componentOrder = {}.  This is due to a bug in PageComponent which has been fixed.

    // vSpace is used in display but is not editable anywhere
    public String vSpace() {
        return (String)valueForBindingKey(VSPACE_BINDINGKEY);
    }
    public void setVSpace(String aVSpace) {
        setBindingForKey(aVSpace, VSPACE_BINDINGKEY);
    }

    public String align() {
        return (String)valueForBindingKey(ALIGN_BINDINGKEY);
    }
    public void setAlign (String anAlign) {
        setBindingForKey(anAlign, ALIGN_BINDINGKEY);
    }

    public String imageType() {
        return (String)valueForBindingKey(TYPE_BINDINGKEY);
    }
    public void setImageType(String aType) {
        setBindingForKey(aType, TYPE_BINDINGKEY);
    }

    // a.k.a. width
    public String hSize() {
        return (String)valueForBindingKey(HSIZE_BINDINGKEY);
    }
    public void setHSize(Object anHSize) {
        setBindingForKey(anHSize == null ? anHSize : anHSize.toString(), HSIZE_BINDINGKEY);
    }
    
    // hSpace is used in display but is not editable anywhere
    public String hSpace() {
        return (String)valueForBindingKey(HSPACE_BINDINGKEY);
    }
    public void setHSpace(String anHSpace) {
        setBindingForKey(anHSpace, HSPACE_BINDINGKEY);
    }

    // Permitted to be null/empty string if no image is to be displayed.
    public String url() {
        return (String)valueForBindingKey(URL_BINDINGKEY);
    }
    public void setUrl(String aUrl) {
        setBindingForKey(aUrl, URL_BINDINGKEY);
    }


}
