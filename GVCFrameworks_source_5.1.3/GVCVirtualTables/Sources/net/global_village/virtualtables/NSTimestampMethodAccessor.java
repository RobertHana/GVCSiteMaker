package net.global_village.virtualtables;

import java.util.*;

import ognl.*;

import com.webobjects.foundation.*;


/**
 * Adds methods to NSTimestamp in the manner of a category.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class NSTimestampMethodAccessor extends ObjectMethodAccessor
{

    /**
     * Adds:
     * <ul>
     * <li>addYMDHMS(years, months, days, hours, minutes, seconds) as alias for timestampByAddingGregorianUnits</li>
     * </ul>
     *
     * @see ognl.MethodAccessor#callMethod(java.util.Map, java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object callMethod(Map map, Object target, String name, Object[] aobj) throws MethodFailedException
    {
        if ("addYMDHMS".equals(name))
        {
            return ((NSTimestamp)target).timestampByAddingGregorianUnits(((Number)aobj[0]).intValue(),
                                                                         ((Number)aobj[1]).intValue(),
                                                                         ((Number)aobj[2]).intValue(),
                                                                         ((Number)aobj[3]).intValue(),
                                                                         ((Number)aobj[4]).intValue(),
                                                                         ((Number)aobj[5]).intValue());
        }

        return super.callMethod(map, target, name, aobj);
    }



}
