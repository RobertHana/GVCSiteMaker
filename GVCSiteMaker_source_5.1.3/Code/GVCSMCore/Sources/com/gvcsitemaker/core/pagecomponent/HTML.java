// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class HTML extends _HTML
{

    // Binding keys
    public static final String HTML_BINDINGKEY = "html";


    /**
     * Factory method to create new instances of HTML.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of HTML or a subclass.
     */
    public static HTML newHTML()
    {
        return (HTML) SMEOUtils.newInstanceOf("HTML");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("HTML");

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

        if (html() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("HTML is a required binding for HTML"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\

    // NOTE
    // The actual contents of the bindings at this point in time is
    // {size = "3"; componentOrder = {}; canEdit = "NO"; html = "<P></P>"; font = "Arial"; }
    // So there are more cover methods that could be added.  However, as HTML does not seem to be in use only these ones were done.
    public String html() {
        return (String) valueForBindingKey(HTML_BINDINGKEY);
    }
    public void setHtml(String newHTML) {
        setBindingForKey(newHTML, HTML_BINDINGKEY);
    }


}
