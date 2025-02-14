package net.global_village.virtualtables;

import java.util.*;

import ognl.*;


/**
 * Adds methods to java.lang.String in the manner of a category.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class StringMethodAccessor extends ObjectMethodAccessor
{

    /**
     * Adds:
     * <ul>
     * <li>max(length) to limit display to the first length characters</li>
     * <li>elipsize(length) to limit display to the first length-1 characters followed by an elipsis character</li>
     * </ul>
     *
     * @see ognl.MethodAccessor#callMethod(java.util.Map, java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object callMethod(Map map, Object target, String name, Object[] aobj) throws MethodFailedException
    {
        if ("max".equals(name))
        {
            int maxLength = ((Number)aobj[0]).intValue();
            String s = (String) target;
            return s.length() <= maxLength ? s : s.substring(0, maxLength);
        }

        if ("elipsize".equals(name))
        {
            int maxLength = ((Number)aobj[0]).intValue();
            String s = (String) target;
            return s.length() <= maxLength ? s : s.substring(0, maxLength - 1) + '\u2026';
        }

        return super.callMethod(map, target, name, aobj);
    }



}
