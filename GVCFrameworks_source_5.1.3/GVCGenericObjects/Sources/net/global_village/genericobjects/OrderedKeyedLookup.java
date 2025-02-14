
package net.global_village.genericobjects;
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

import java.util.*;
import java.math.BigDecimal;

import net.global_village.foundation.ExceptionConverter;
import net.global_village.genericobjects.OrderedLookup.OrderingComparator;


/**
 * OrderedKeyedLookup combines the features of KeyedLookup and OrderedKeyedLookup, it has a known key associated with each object and manages a list of objects that provides sorting based on its specified order in the list.  
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */ 
public class OrderedKeyedLookup extends _OrderedKeyedLookup
{
    /** Instances of OrderingComparator to be used when sorting objects. */
    static final public NSComparator OrderingComparator = new OrderingComparator();
    static final public NSComparator InverseOrderingComparator = new InverseOrderingComparator();


    /**
     * Comparator to sort <code>OrderedLookup</code> objects by ascending <code>ordering</code>.
     */
    static protected class OrderingComparator extends NSComparator
    {
        public int compare(Object object1, Object object2) throws ComparisonException
        {
            // _NumberComparator is private so we need to do this rather than sub-classing.
            return NSComparator.AscendingNumberComparator.compare(((EOEnterpriseObject)object1).valueForKey("ordering"), ((EOEnterpriseObject)object2).valueForKey("ordering"));
        }
    }
    
    
    /**
     * Comparator to sort <code>OrderedLookup</code> objects by <code>ordering</code> in descending order.
     */
    static protected class InverseOrderingComparator extends NSComparator
    {
        public int compare(Object object1, Object object2) throws ComparisonException
        {
            return OrderingComparator.compare(object1, object2) * -1;
        }
    }



    /**
     * A convenience method which returns all objects of the Entity indicated by <code>entityName</code> sorted by ordering.  
     *
     * @param entityName the kind of objects you want to fetch with this method
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @return NSArray of all objects of the entity specified by <code>entityName</code> and sorted by name
     */
    public static NSArray orderedObjects(EOEditingContext editingContext, String entityName)
    {
        /** require
        [valid_editingContext_param] editingContext != null; 
        [valid_entityName_param] entityName != null;         
        **/
        return orderedObjects(editingContext, entityName, OrderingComparator);
        /** ensure [valid_result] Result != null;  **/
    }    

}

