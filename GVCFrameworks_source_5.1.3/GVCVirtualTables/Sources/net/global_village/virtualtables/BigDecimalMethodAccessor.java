package net.global_village.virtualtables;

import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import ognl.*;

import com.webobjects.foundation.*;


/**
 * Adds methods to java.math.BigDecimal in the manner of a category.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class BigDecimalMethodAccessor extends ObjectMethodAccessor
{

    static protected NSMutableDictionary methods = new NSMutableDictionary();



    /**
     * Adds:
     * <ul>
     * <li>all functions in java.lang.Math that take a single double parameter</li>
     * </ul>
     *
     * @see ognl.MethodAccessor#callMethod(java.util.Map, java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object callMethod(Map map, Object target, String name, Object[] aobj) throws MethodFailedException
    {
        try
        {
            // Hack around OGNL not finding int parameter method when aobj contains an Integer
            if ("pow".equals(name))
            {
                int power = ((Number)aobj[0]).intValue();
                return ((BigDecimal)target).pow(power);
            }
            return super.callMethod(map, target, name, aobj);
        }
        catch (MethodFailedException e)
        {
            try
            {
                // Some caching for better performance
                Method mathMethod;
                synchronized (methods)
                {
                    mathMethod = (Method)methods.objectForKey(name);
                    if (mathMethod == null)
                    {
                        mathMethod = java.lang.Math.class.getMethod(name, new Class[] {java.lang.Double.TYPE});
                        methods.setObjectForKey(mathMethod, name);
                    }
                }
                Number numberObject = (Number) mathMethod.invoke(null, new Double(((BigDecimal)target).doubleValue()));
                return BigDecimal.valueOf(numberObject.doubleValue());
            }
            catch (Exception e1)
            {
                throw e;  // Original exception!
            }
        }
    }



}
