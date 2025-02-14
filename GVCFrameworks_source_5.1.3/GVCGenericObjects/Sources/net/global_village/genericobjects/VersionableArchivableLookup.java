
package net.global_village.genericobjects;


import com.webobjects.foundation.NSComparator;


/**
 * VersionableArchivableLookup adds a name to a VersionableArchivableObject.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 *
 */  
public class VersionableArchivableLookup extends _VersionableArchivableLookup
{
    /**
     * Instance of NameComparator to be used when sorting objects.
     */
    static final public NSComparator NameComparator = new NameComparator();



    /**
     * Comparator to sort objects by name, case insensitively.
     */
    static protected class NameComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            return (((VersionableArchivableLookup)object1)).name().compareToIgnoreCase(((VersionableArchivableLookup)object2).name());
        }
    }


}
