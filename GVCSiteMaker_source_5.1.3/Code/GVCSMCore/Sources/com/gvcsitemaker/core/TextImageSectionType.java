/**
 * Implementation of TextImageSectionType common to all installations.
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


public class TextImageSectionType extends _TextImageSectionType
{


    /**
    * Returns the singleton instance of TextImageSectionType in the passed EOEditingContext
     */
    public static TextImageSectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        return (TextImageSectionType) getInstance(ec, "TextImageSectionType");
        /** ensure [return_not_null] Result != null; **/
    }



    /**
     * Factory method to create new instances of TextImageSectionType.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of TextImageSectionType or a subclass.
     */
    public static TextImageSectionType newTextImageSectionType()
    {
        return (TextImageSectionType) SMEOUtils.newInstanceOf("TextImageSectionType");

        /** ensure [return_not_null] Result != null; **/
    }

    
    
    /**
     * Returns <code>true</code> since this type is versionable
     *  
     * @return <code>true</code> since this type is versionable
     */    
    public boolean isVersionable()
    {
        return true;
    }    
    
    
    
    /**
     * Returns <code>true</code> since this type supports editors
     *  
     * @return <code>true</code> since this type supports editors
     */    
    public boolean supportsEditors()
    {
        return true;
    }     
    
    
    
    /**
     * Returns <code>true</code> since this type supports contributors
     *  
     * @return <code>true</code> since this type supports contributors
     */    
    public boolean supportsContributors()
    {
        return true;
    }          

}

