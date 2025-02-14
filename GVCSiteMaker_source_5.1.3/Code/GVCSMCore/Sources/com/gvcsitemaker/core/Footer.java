// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;



public class Footer extends _Footer 
{

    /**
     * Factory method to create new instances of Footer.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Footer or a subclass.
     */
    public static Footer newFooter()
    {
        return (Footer) SMEOUtils.newInstanceOf("Footer");

        /** ensure [return_not_null] Result != null; **/
    }


    public boolean isLink1Visible()
    {
        return ((linkText1() != null) && (linkURL1() != null));
    }



    public boolean isLink2Visible()
    {
        return ((linkText2() != null) && (linkURL2() != null));
    }



    public boolean isLink3Visible()
    {
        return ((linkText3() != null) && (linkURL3() != null));
    }



    public boolean isLink4Visible()
    {
        return ((linkText4() != null) && (linkURL4() != null));
    }



    public boolean hasCustomFooter()
    {
        return customFooter() != null;
    }



}
