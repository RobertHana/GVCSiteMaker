// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


public class ErrorLog extends _ErrorLog
{

    /**
     * Factory method to create new instances of ErrorLog.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of ErrorLog or a subclass.
     */
    public static ErrorLog newErrorLog()
    {
        return (ErrorLog) SMEOUtils.newInstanceOf("ErrorLog");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns all the error log entries in the order they were created.
     * 
     * @param ec the EOEditingContext to return the error log entries in
     * @return all the error log entries in the order they were created
     */
    public static NSArray orderedLogEntries(EOEditingContext ec)
    {
        /** require [valid_ec] ec != null;  **/
        NSArray sortOrderings = new NSArray(EOSortOrdering.sortOrderingWithKey("oid", EOSortOrdering.CompareDescending));
        EOFetchSpecification fs = new EOFetchSpecification("ErrorLog", null, sortOrderings);

        return ec.objectsWithFetchSpecification(fs);

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Throws a RuntimeException as this has not been implemented for ErrorLog.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this PageComponent
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        throw new RuntimeException("duplicate not implemented");
    }



    public String oidAsString()
    {
        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            return oid().toString();
        }
        else
        {
            Object downcaster = oid();
            return ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
        }
    }



}
