// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Base class for all template driven GVCSiteMaker styles.  At present there is only a single sub-class, SectionStyle.  It is anticipated that Virtual tables may use a similar mechanism.  The core information in a Style is an unique style ID, unique name, publication status, the actual template, and notes.  A Style is owned by an Org Unit and it only available at and below that OrgUnit.  The styleID is restricted to having only A-Z, a-z, 0-9, period, hyphen, and underline so that it can be used in URLs, filenames, and paths.
 */
public class Style extends _Style
{ 

    // Validation Messages
    public static final String StyleTemplateIsRequiredKey = "StyleTemplateIsRequiredKey";
    public static final String StyleTemplateIsRequiredErrorMessage = "You must upload the template for this style before saving.";

    public static final String StyleNameIsRequiredKey = "StyleNameIsRequiredKey";
    public static final String StyleNameIsRequiredErrorMessage = "You must enter a description for this style.";

    public static final String StyleIDIsRequiredKey = "StyleIDIsRequiredKey";
    public static final String StyleIDIsRequiredErrorMessage = "You must give this style a unique ID.";

    public static final String StyleIDIsInvalidKey = "StyleIDIsInvalidKey";
    public static final String StyleIDIsInvalidErrorMessage = "The style ID can only contain the characters A-Z, a-z, 0-9, period, hyphen, and underline.";


    /**
     * Returns the default ordering for lists of Styles: Owning Org Unit, Pulication Status, Name.
     *
     * @return the default ordering for lists of Styles
     */
    static public NSArray styleSortOrdering()
    {
        NSMutableArray sortOrdering = new NSMutableArray();
        sortOrdering.addObject(EOSortOrdering.sortOrderingWithKey("owningOrgUnit.unitName", EOSortOrdering.CompareAscending));
        sortOrdering.addObject(EOSortOrdering.sortOrderingWithKey("published", EOSortOrdering.CompareDescending));        	sortOrdering.addObject(EOSortOrdering.sortOrderingWithKey("name", EOSortOrdering.CompareAscending));

        return sortOrdering;

        /** ensure [return_not_null] Result != null; **/
    }


    
    /**
     * Factory method to create new instances of Style.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Style or a subclass.
     */
    public static Style newStyle()
    {
        return (Style) SMEOUtils.newInstanceOf("Style");

        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Sets default values for dateCreated (now) and isPublished (true).
     */
    public void awakeFromInsertion( EOEditingContext ec )
    {
        super.awakeFromInsertion( ec );

        setDateCreated(Date.now());
        setIsPublished(false);

    }



    /**
     * Styles are copied by reference.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a reference to this Style
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return this;

        /** ensure [copy_made] Result != null;   **/
    }


    
    /**
     * Returns name to use for the template for this style when downloading.
     * 
     * @return name to use for the template for this style when downloading
     */
    public String templateFileName() {
        return styleID() + ".html";

        /** ensure [valid_result] Result != null;   **/
    }
    
    
    
    /**
     * Returns the HTML content for the template as an NSData object.  
     *
     * @return the HTML content for the template
     */
    public NSData templateHtmlData()
    {
        /** require [has_template] template() != null;  **/
         return new NSData(template().getBytes());
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Funky non-EOF validation method used by GVSiteMaker
     */
    protected NSMutableDictionary validate()
    {
        NSMutableDictionary errorMessages = new NSMutableDictionary();

        if (template() == null)
        {
            errorMessages.setObjectForKey(StyleTemplateIsRequiredErrorMessage,
                                          StyleTemplateIsRequiredKey);
        }

        if (name() == null)
        {
            errorMessages.setObjectForKey(StyleNameIsRequiredErrorMessage,
                                          StyleNameIsRequiredKey);
        }

        if (styleID() == null)
        {
            errorMessages.setObjectForKey(StyleIDIsRequiredErrorMessage,
                                          StyleIDIsRequiredKey);
        }
        else if ( ! SMFileUtils.stringContainsOnlyValidFileNameChars(styleID()))
        {
            errorMessages.setObjectForKey(StyleIDIsInvalidErrorMessage,
                                          StyleIDIsInvalidKey);
        }

        return errorMessages;
    }


    
    /**
     * Overridden to use the results of validate() and set dateModified.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        setDateModified(Date.now());

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }
        
        NSDictionary validationFailures = validate();
        if (validationFailures.count() > 0)
        {
            exceptions.addObject(new NSValidation.ValidationException(validationFailures.allValues().toString()));
        }

        /*
        if (this == null)
        {
            exceptions.addObject(new NSValidation.ValidationException(""));
        }
         */
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Returns <code>true</code> if this style is published (publicly available).  boolean cover method for published().
     *
     * @return <code>true</code> if this style is published (publicly available).
     */
    public boolean isPublished()
    {
        return published().equals("Y");
    }



    /**
     * Controls if this style is published (publicly available) or not.  boolean cover method for setPublished().
     *
     * @param value - <code>true</code> if this style is published (publicly available).
     */
    public void setIsPublished( boolean value )
    {
        setPublished( value ? "Y" : "N");
    }


}
