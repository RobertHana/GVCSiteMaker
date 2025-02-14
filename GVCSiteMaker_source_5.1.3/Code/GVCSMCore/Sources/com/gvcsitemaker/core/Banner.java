// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;



public class Banner extends _Banner 
{

    /**
     * Factory method to create new instances of Banner.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Banner or a subclass.
     */
    public static Banner newBanner()
    {
        return (Banner) SMEOUtils.newInstanceOf("Banner");

        /** ensure [result_not_null] Result != null; **/
    }

}
