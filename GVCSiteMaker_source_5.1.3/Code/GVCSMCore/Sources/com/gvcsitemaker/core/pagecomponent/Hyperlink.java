// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;



public class Hyperlink extends _Hyperlink
{

    // Binding keys
    public static final String HREF_BINDINGKEY = "href";
    public static final String TARGET_BINDINGKEY = "target";
    public static final String NAME_BINDINGKEY = "name";
    public static final String LINK_TYPE_BINDINGKEY = "type";
    
    // Binding values
    public static final String NEW_PAGE_TARGET = "_blank";


    /**
     * Factory method to create new instances of Hyperlink.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Hyperlink or a subclass.
     */
    public static Hyperlink newHyperlink()
    {
        return (Hyperlink) SMEOUtils.newInstanceOf("Hyperlink");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("Hyperlink");
        setLinkType(URL_TYPE);
        // Default bindings for use in External URL section.  If Hyperlink is needed for another purpose, create a subclass of called ExternalURLHyperlink to handle that specific use.  As present it is fully generic.
    }

    
    
    /**
     * Returns the URL that this hyperlink represents.  SiteFile links are adjusted relative to the 
     * domain in the request.
     * 
     * @param request the WORequest asking for a link
     * @return the URL that this hyperlink represents
     */
    public String urlForRequest(WORequest request)
    {
        if (isExternalLink())
        {
            return href();
        }
        else if (isSiteFileLink())
        {
            // Handle having the linked file delete.
            if (relatedFile() == null)
            {
                return SiteFile.MISSING_FILE_URL;
            }
            return relatedFile().url(request);
        }
        
        return null;
    }

    

    /**
     * Returns <code>true</code> if this is linked to a file or if there is an href which has an valid scheme.
     *
     * @return <code>true</code> if a valid URL can be returned
     */
    public boolean isHrefValid()
    {
        return isSiteFileLink() || ( (href() != null) && URLUtils.hasValidURIScheme(href()) );
    }



    /**
     * Returns <code>true</code> if all required bindings have a value.
     *
     * @return <code>true</code> if all required bindings have a value.
     */
    public boolean hasAllRequiredBindings()
    {
        return (isExternalLink() && (href() != null)) || (isSiteFileLink() && (relatedFile() != null));
    }



    /**
     * Returns <code>true</code> if this is a link external to SiteMaker.
     *
     * @return <code>true</code> if this is a link external to SiteMaker
     */
    public boolean isExternalLink()
    {
        return linkType().equals(URL_TYPE);
    }



    /**
     * Returns <code>true</code> if this is a link to a SiteFile.
     *
     * @return <code>true</code> if this is a link to a SiteFile.
     */
    public boolean isSiteFileLink()
    {
        return linkType().equals(SITEFILE_TYPE);
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

        /**
          * These should be required but it appears that is it possible to create a Hyperlink like this:
          * {componentOrder = {}; canEdit = "YES"; }
          * via a bug in the Create Section functionality.  Uncomment this when that bug is fixed.  -ch
        if (name() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Name is a required binding for a Hyperlink"));
        }

        if (href() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("href is a required binding for a Hyperlink"));
        }

         */

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are
    // {href = "http://www.google.com"; target = "_self"; canEdit = "YES"; name = "Google Internet Search"; }
    // Sometimes there is a binding of componentOrder = {}.  This is due to a bug in PageComponent which has been fixed.

    public String href() {
        return (String)valueForBindingKey(HREF_BINDINGKEY);
    }
    public void setHref(String aHref) {
        setBindingForKey(aHref, HREF_BINDINGKEY);
    }

    public String target() {
        return (String)valueForBindingKeyWithDefault(TARGET_BINDINGKEY, NEW_PAGE_TARGET);	// Default migrated here from UI.  Is this needed?  -ch
    }
    public void setTarget(String aTarget) {
        setBindingForKey(aTarget, TARGET_BINDINGKEY);
    }

    public String name() {
        return (String)valueForBindingKey(NAME_BINDINGKEY);
    }
    public void setName(String aName) {
        setBindingForKey(aName, NAME_BINDINGKEY);
    }

    public String linkType() {
        return (String)valueForBindingKey(LINK_TYPE_BINDINGKEY);
    }
    public void setLinkType(String aType) {
        setBindingForKey(aType, LINK_TYPE_BINDINGKEY);
    }
}
