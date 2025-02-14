// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;



public class WebsiteChangeLog extends _WebsiteChangeLog
{


    /**
     * Factory method to create new instances of WebsiteChangeLog.  Use this rather than calling the 
     * constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static WebsiteChangeLog newWebsiteChangeLog()
    {
        return (WebsiteChangeLog) SMEOUtils.newInstanceOf("WebsiteChangeLog");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overridden to set the dateRecorded.
     * 
     * @see com.webobjects.eocontrol.EOEnterpriseObject#awakeFromFetch(com.webobjects.eocontrol.EOEditingContext)
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromFetch(ec);
        setDateRecorded(new NSTimestamp());
        /** ensure [date_set] dateRecorded() != null;  **/
    }



    /**
     * One of changes() or notes() must be present.
     * 
     * @see com.webobjects.eocontrol.EOValidation#validateForSave()
     */
    public void validateForSave() throws ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();
        
        try 
        {
          super.validateForSave();
        } 
        catch(NSValidation.ValidationException e) 
        {
          exceptions.addObject(e);
        }

        if ((changes() == null) && (notes() == null)) 
        {
            exceptions.addObject(new NSValidation.ValidationException("Either notes or changes must be entered"));
        }
  
        if(exceptions.count() > 0) 
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }

}

