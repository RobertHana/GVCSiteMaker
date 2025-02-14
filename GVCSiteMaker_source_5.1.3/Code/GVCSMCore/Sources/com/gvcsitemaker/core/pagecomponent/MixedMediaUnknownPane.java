// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;

/**
 * Subclass of MixedMediaPane. The initial child of MixedMediaPaneArrangement when type is unknown, holder for a pane with unknown type
 */
public class MixedMediaUnknownPane extends _MixedMediaUnknownPane
{

	/**
     * Factory method to create new instances of MixedMediaUnknownPane.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaUnknownPane or a subclass.
     */
    public static MixedMediaUnknownPane newMixedMediaUnknownPane()
    {
        return (MixedMediaUnknownPane) SMEOUtils.newInstanceOf("MixedMediaUnknownPane");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("MixedMediaUnknownPane");
    }    
   
    
}

