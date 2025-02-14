// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;

/**
 * A Section Type for Sections that will be a copy of an existing Section.
 */
public class CopySectionType extends _CopySectionType
{
    /**
     * Returns the singleton instance of DataAccessSectionType in the passed EOEditingContext
     */
    public static CopySectionType getInstance(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
    	
        return (CopySectionType) getInstance(ec, "CopySectionType");
        
        /** ensure [return_not_null] Result != null; **/
    }

    
    
    /**
     * Factory method to create new instances of CopySectionType.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of CopySectionType or a subclass.
     */
    public static CopySectionType newCopySectionType()
    {
        return (CopySectionType) SMEOUtils.newInstanceOf("CopySectionType");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Returns <code>false</code> as there is no way to access Sections of this type.
     *
     * @param aSection - the Section to check type of access protection for
     * @return <code>false</code> as there is no way to access Sections of this type
     */
    public boolean usesAccessProtection(Section aSection)
    {
        /** require [valid_section] aSection != null;    **/
        return false;
    }
    
    
    
    /**
     * Returns false since this type cannot be modified in the Configuration Pages
     *  
     * @return <code>false</code>
     */    
    public boolean canBeModified()
    {
        return false;
    }
  
    
    
    /**
     * Returns false since this type cannot be deleted in the Configuration Pages
     *  
     * @return <code>false</code>
     */    
    public boolean canBeDeleted()
    {
        return  false;
    }     
    

    
    /**
     * Returns true since this type is placeholder for sections in a package
     *  
     * @return <code>true</code>
     */    
    public boolean isPlaceHolder()
    {
        return true;
    }        
}

