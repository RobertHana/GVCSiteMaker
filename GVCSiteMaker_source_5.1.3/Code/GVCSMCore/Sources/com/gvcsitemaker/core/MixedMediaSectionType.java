// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;


/**
 * Section Type to be used for MixedMedia Sections
 */
public class MixedMediaSectionType extends _MixedMediaSectionType
{


    /**
	 * Returns the singleton instance of MixedMediaSectionType in the passed EOEditingContext
     * 
     * @return the singleton instance of MixedMediaSectionType in the passed EOEditingContext
	 */
	public static MixedMediaSectionType getInstance(EOEditingContext ec)
	{
		/** require [ec_not_null] ec != null; **/
		return (MixedMediaSectionType) getInstance(ec, "MixedMediaSectionType");
	    /** ensure [return_not_null] Result != null; **/
	}



    /**
     * Factory method to create new instances of MixedMediaSectionType.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaSectionType or a subclass.
     */
    public static MixedMediaSectionType newMixedMediaSectionType()
    {
        return (MixedMediaSectionType) SMEOUtils.newInstanceOf("MixedMediaSectionType");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden since Section of this type is already a MixedMedia
     *
     * @returns false
     */
    public boolean canBeDisplayedInMixedMediaSection()
    {
        return false;
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



}
