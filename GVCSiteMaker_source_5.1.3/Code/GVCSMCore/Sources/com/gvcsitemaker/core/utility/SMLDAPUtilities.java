// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.utility;

import com.webobjects.foundation.*;

/**
 * A collection of methods to ease handling eo objects from an LDAP data source.
 */
public class SMLDAPUtilities
{

    /**
     * Returns the value from the first key-value pair in a Distinguished Name or Relative 
     * Distinguished Name.  For example, given 'uid=somebody,ou=Users,dc=foo,dc=com' it will return 
     * 'somebody'.
     * 
     * @param rdn the Distinguished Name or Relative Distinguished Name to extract the first value from
     * @return the value from the first key-value pair
     */
    public static String firstLdapAttributeValue(String rdn)
    {
        /** require [valid_rdn] rdn != null;
                    [valid_format] rdn.indexOf("=") + 1 < rdn.indexOf(",");        **/
        return rdn.substring(rdn.indexOf("=") + 1, rdn.indexOf(","));
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns an NSArray holding the value(s) from a potentially multi-valued LDAP attribute.  The 
     * JNDI adaptor will return these as (for example) null, String, and NSArray if there are zero,
     * one or more values.  This method makes these into a consistent NSArray return type.  The
     * NSArray will contain zero, one, or more instances respectively.
     *  
     * @param object <code>Object</code> which is a potentially multi-valued LDAP attribute
     * @return an NSArray holding the value(s) from <code>object</code>
     */
    public static NSArray ldapMultiValue(Object object)
    {
        NSArray ldapMultiValue;
        
        if (object == null)
        {
            ldapMultiValue = NSArray.EmptyArray;
        }
        else
        if (object instanceof NSArray)
        {
            ldapMultiValue = (NSArray)object;
        }
        else
        {
            ldapMultiValue = new NSArray(object);
        }
        
        return ldapMultiValue;
        
        /** ensure [valid_result] Result != null;
                   [same_number] ((object == null) && (Result.count() == 0)) || 
                                 ((object instanceof NSArray) && (Result.count() == ((NSArray)object).count())) ||
                                 (Result.count() == 1);         **/
    } 
}
