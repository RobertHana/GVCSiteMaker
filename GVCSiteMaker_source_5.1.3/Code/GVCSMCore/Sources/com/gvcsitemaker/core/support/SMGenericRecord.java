package com.gvcsitemaker.core.support;

import net.global_village.eofextensions.*;

import com.webobjects.eocontrol.*;

/*
 * Adds methods to catch actions performed by EOF that can alter the values
 * underlying cached values and to reset any values cached in instance
 * variables. The central method is clearCachedValues. This method should be
 * overridden in subclasses. It should also be called when the object knows that
 * it has changed values that the cached values are derived from. // Copyright
 * (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109
 * USA All rights reserved. // This software is published under the terms of the
 * Educational Community License (ECL) version 1.0, // a copy of which has been
 * included with this distribution in the LICENSE.TXT file.
 */
public class SMGenericRecord extends GenericRecord
{


    public SMGenericRecord()
    {
        super();
    }


    /**
     * Overriden to clear cached values when object changed in another ec.
     */
    public void awakeFromFetch(EOEditingContext ec)
    {
        super.awakeFromFetch(ec);
        clearCachedValues();
    }



    /**
     * Clears all cached values.  This method should be overridden in subclasses to clear the actual cached values.
     */
    public void clearCachedValues()
    {
        super.clearCachedValues();
    }



}
