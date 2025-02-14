package net.global_village.foundation;

import com.webobjects.foundation.NSArray;


/**
 * Implements an arbitrarily complex key for use with <code>NSDictionary</code>.  Note that the order of the keys is important.  The same keys in a different order will result in two <code>GVCComplexKeys</code> comparing as not equal.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class ComplexKey extends Object implements Cloneable
{
    /**
     * The keys of this <code>ComplexKey</code>.
     */
    protected NSArray keys;


    /**
     * Designated initializer.  Initializes the <code>GVCComplexKey</code> with keys from an NSArray.
     *
     * @param someKeys an ordered array of keys
     */
    public ComplexKey(NSArray someKeys)
    {
        super();
        /** require [valid_param] someKeys != null; **/

        keys = someKeys;

        /** ensure [keys_set] keys == someKeys; **/
    }



    /**
     * Returns the <code>keys</code> array.
     *
     * @return the <code>keys</code> array
     */
    public NSArray keys()
    {
        return keys;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Override equals so that it compares the keys themselves.
     *
     * @param otherValue the object to compare this to
     * @return <code>true</code> if this is equal to <code>otherValue</code>, <code>false</code> otherwise
     */
    public boolean equals(Object otherValue)
    {
        /** require [valid_param] otherValue != null; **/

        boolean equals = false;

        if (getClass().isInstance(otherValue))
        {
            equals = keys.isEqualToArray(((ComplexKey)otherValue).keys());
        }

        return equals;

        /** ensure [definition] /# Return true iff this equals <code>otherValue</code> #/ true; **/
    }



    /**
     * Override clone so that others can see it.
     *
     * @return a clone of this
     */
    public Object clone()
    {
        Object rv = null;
        try
        {
            rv = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // never happens, since we support it.  Throw an Error
            throw new Error("CloneNotSupportedException was thrown, but we do support cloning!");
        }
        return rv;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a string representation of the key.
     *
     * @return a string representation of this
     */
    public String toString()
    {
        return "<" + getClass().getName() + " keys: " + keys.toString() + ">";

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a hash code for this object - making sure that two different complex keys with identical contents return the same hash.
     *
     * @return the hash code for this complex key
     */
    public int hashCode()
    {
        return keys.hashCode();
    }



    /** invariant
    [has_keys] keys != null;
    [has_at_least_one_key] keys.count() >= 1; **/



}
