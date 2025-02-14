package net.global_village.genericobjects;

import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSComparator;

import net.global_village.foundation.ExceptionConverter;


/**
 * OrderedLookup manages a list of objects that provides sorting based on its specified order in the list. 
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */  
public class OrderedLookup extends _OrderedLookup
{

    /**
     * Instance of OrderingComparator to be used when sorting objects.
     */
    static final public NSComparator OrderingComparator = new OrderingComparator();



    /**
     * Comparator to sort <code>OrderedLookup</code> objects by <code>ordering</code>.
     */
    static protected class OrderingComparator extends NSComparator
    {
        public int compare(Object object1, Object object2)
        {
            int result;

            try
            {
                // _NumberComparator is private so we need to do this rather than sub-classing.
                result = NSComparator.AscendingNumberComparator.compare(((EOEnterpriseObject)object1).valueForKey("ordering"), ((EOEnterpriseObject)object2).valueForKey("ordering"));
            }
            catch (com.webobjects.foundation.NSComparator.ComparisonException e)
            {
                throw new ExceptionConverter(e);
            }

            return result;
        }
    }



}
