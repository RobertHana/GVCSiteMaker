/**
 * Implementation of ListOfLinksSectionType common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


public class ListOfLinksSectionType extends _ListOfLinksSectionType
{


    /**
     * Returns the singleton instance of ListOfLinksSectionType in the passed EOEditingContext
     */
    public static ListOfLinksSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (ListOfLinksSectionType) getInstance(ec, "ListOfLinksSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of ListOfLinksSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of ListOfLinksSectionType or a subclass.
     */
    public static ListOfLinksSectionType newListOfLinksSectionType()
    {
        return (ListOfLinksSectionType) SMEOUtils.newInstanceOf("ListOfLinksSectionType");

        /** ensure [return_not_null] Result != null; **/
    }


}

