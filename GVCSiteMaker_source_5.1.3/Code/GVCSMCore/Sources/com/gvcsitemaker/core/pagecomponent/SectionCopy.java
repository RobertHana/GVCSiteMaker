// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA

package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;

/**
 * The PageComponent for copying existing Sections.
 */
public class SectionCopy extends _SectionCopy
{


    /**
     * Factory method to create new instances of SectionCopy.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of SectionCopy or a subclass.
     */
    public static SectionCopy newSectionCopy()
    {
        return (SectionCopy) SMEOUtils.newInstanceOf("SectionCopy");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("SectionCopy");
    }    
}

