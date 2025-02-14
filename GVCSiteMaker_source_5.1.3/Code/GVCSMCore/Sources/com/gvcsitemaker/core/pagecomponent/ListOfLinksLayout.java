/**
 * Implementation of ListOfLinks common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


public class ListOfLinksLayout extends _ListOfLinksLayout
{

    /**
    * Factory method to create new instances of ListOfLinksLayout.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of ListOfLinksLayout or a subclass.
     */
    public static ListOfLinksLayout newListOfLinksLayout()
    {
        return (ListOfLinksLayout) SMEOUtils.newInstanceOf("ListOfLinksLayout");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("ListOfLinksLayout");
   }

}

