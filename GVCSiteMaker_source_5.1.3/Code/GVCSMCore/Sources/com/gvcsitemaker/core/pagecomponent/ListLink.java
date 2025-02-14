// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class ListLink extends _ListLink
{

    // Binding keys
    public static final String NAME_BINDINGKEY = "name";
    public static final String TYPE_BINDINGKEY = "type";
    public static final String URI_BINDINGKEY = "uri";
    public static final String DESCRIPTION_BINDINGKEY = "description";
    public static final String OPEN_IN_NEW_WINDOW_BINDINGKEY = "openInNewWindow";


    /**
     * Factory method to create new instances of ListLink.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of ListLink or a subclass.
     */
    public static ListLink newListLink()
    {
        return (ListLink) SMEOUtils.newInstanceOf("ListLink");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("ListLink");
        setLinkType("external");
        setShouldOpenInNewWindow(true);
        setShouldOpenInNewWindow(false);
    }



    /**
     * Returns <code>true</code> if this ListLink is a link to an URI external to SiteMaker.
     *
     * @return <code>true</code> if this ListLink is a link to an URI external to SiteMaker.
     */
    public boolean isExternalLink()
    {
        return linkType().equals(EXTERNAL_TYPE);
    }



    /**
     * Returns <code>true</code> if this ListLink is a link to a SiteFile.
     *
     * @return <code>true</code> if this ListLink is a link to a SiteFile.
     */
    public boolean isSiteFileLink()
    {
        return linkType().equals(SITEFILE_TYPE);
    }



    /**
     * Returns <code>true</code> if this ListLink is a link to a SiteFile but there is no associated file.  This will happen when a SiteFile that we are a link to is deleted.  At present it is permissible to delete a SiteFile that is in use.  If that is changed (restricted), then this functionality can be removed.
     *
     * @return <code>true</code> if this ListLink is a link to a SiteFile but there is no associated file.
     */
    public boolean isFileMissing()
    {
        return isSiteFileLink() && (toRelatedFile() == null);  // Could also check existence on disk  - ch
    }



    /**
     * Returns <code>true</code> if the URI in the bindings is present and has a valid scheme.  This is used for validation here and in the UI.
     *
     * @return <code>true</code> if the URI in the bindings is present and has a valid scheme.
     */
    public boolean isUriValid()
    {
        return ((linkType() != null) &&  ! isExternalLink()) ||
                ( (uri() != null) &&
                  URLUtils.hasValidURIScheme(uri()) );
    }


    
    /**
     * This method is a big "if" statement that performs the validation for required fields.
     *
     * @return <code>true</code> only when the required fields are all valid.
     */
    public boolean isMissingAnyRequiredField()
    {
        return ((name() == null) ||
                (isExternalLink()  && (uri() == null) ) ||
                (isSiteFileLink()  && (toRelatedFile() == null)) );
    }


    /**
     * Returns the URI for this ListLink, either to an an internally held file or to an external URI.
     *
     * @return the URI for this ListLink, either to an an internally held file or to an external URI.
     *
     * @return != null
     */
    public String linkURI(WORequest request)
    {
        String linkURI;

        if (isSiteFileLink())
        {
            linkURI = isFileMissing() ? SiteFile.MISSING_FILE_URL : toRelatedFile().url(request);
        }
        else
        {
            linkURI = uri();
        }

        return linkURI;
    }



    /**
     * Returns some extra descriptive information on this ListLink for display in the UI (URI, file name and size as applicable).
     *
     * @return some extra descriptive information on this ListLink for display in the UI.
     *
     * @return != null
     */
    public String linkInfo()
    {
        String linkInfo;

        // This is the extra information to show when in display mode (about the link's contents)
        DebugOut.println(4,"Binding linkType is " + linkType());
        if (isExternalLink())
        {
            // Return the URL information
            linkInfo = uri();
        }
        else
        {
            // Return the file information, if not null ...
            if (isFileMissing())
            {
                linkInfo = "File is no longer available";
            }
            else
            {
                linkInfo = toRelatedFile().uploadedFilename() + ", " + toRelatedFile().fileSizeInKilobytes() + " kb";
            }
        }

        return linkInfo;
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

        if (name() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Name is a required binding for a ListLink"));
        }

        if (linkType() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("Link Type is a required binding for a ListLink"));
        }
        else if ( ! (isSiteFileLink() || isExternalLink()))
        {
            exceptions.addObject(new NSValidation.ValidationException("'" + linkType() + "' is not a valid type for a ListLink"));
        }

        /* This is currently allowed to happen when a SiteFile used by a ListLink is deleted.
        if (isSiteFileLink() && (toRelatedFile() == null))
        {
            exceptions.addObject(new NSValidation.ValidationException("SiteFile link missing related file"));
        }
        */

        if (! isUriValid())
        {
            exceptions.addObject(new NSValidation.ValidationException("External URI link '" + uri() + "' has bad scheme or is missing."));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }


    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are one of these:
    // {name = "Google";         type = "external"; canEdit = "YES"; uri = "http://www.google.com"; description = "Search Here"; }
    // {name = "The Cover Logo"; type = "file";     canEdit = "YES";                                description = "This was the original logo."; }

    public String name() {
        return (String)valueForBindingKey(NAME_BINDINGKEY);
    }
    public void setName(String aName) {
        setBindingForKey(aName, NAME_BINDINGKEY);
    }

    // Rename 'type' to 'linkType' so that it does not conflict with type() on PageComponent
    public String linkType() {
        return (String)valueForBindingKey(TYPE_BINDINGKEY);
    }
    public void setLinkType(String aType) {
        setBindingForKey(aType, TYPE_BINDINGKEY);
    }

    // For EXTERNAL_TYPE only
    public String uri() {
        return (String)valueForBindingKey(URI_BINDINGKEY);
    }
    public void setUri(String aLinkType) {
        setBindingForKey(aLinkType, URI_BINDINGKEY);
    }

    public String description() {
        return (String)valueForBindingKey(DESCRIPTION_BINDINGKEY);
    }
    public void setDescription(String aDescription) {
        setBindingForKey(aDescription, DESCRIPTION_BINDINGKEY);
    }

    public boolean shouldOpenInNewWindow() {
        return booleanValueForBindingKey(OPEN_IN_NEW_WINDOW_BINDINGKEY);
    }
    public void setShouldOpenInNewWindow(boolean shouldOpenInNewWindow) {
        setBooleanValueForBindingKey(shouldOpenInNewWindow, OPEN_IN_NEW_WINDOW_BINDINGKEY);
    }
}
