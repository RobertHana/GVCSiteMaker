package net.global_village.eofvalidation;

import java.lang.reflect.*;


/**
 * This class is part of a hack to work around RadarBug:2867501. EOEntity is hardwired to return an EOEntityClassdescription for the method classDescriptionForNewInstances, this causes a serious problem when using custom class descriptions with D2W which makes use of this method. What this hack does is use the magic of key-value coding to push our custom class description onto a given entity. In order to do this we needed to add this class to the package com.webobjects.eoaccess.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */
public class KeyValueCodingProtectedAccessor extends com.webobjects.foundation.NSKeyValueCoding.ValueAccessor
{

    /**
     * Designated constuctor.
     */
    public KeyValueCodingProtectedAccessor()
    {
        super();
    }



    public Object fieldValue(Object object, Field field) throws IllegalArgumentException, IllegalAccessException
    {
        return field.get(object);
    }



    public void setFieldValue(Object object, Field field, Object value) throws IllegalArgumentException, IllegalAccessException
    {
        field.set(object, value);
    }



    public Object methodValue( Object object, Method method) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        return method.invoke(object, (Object[]) null);
    }



    public void setMethodValue(Object object, Method method, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        method.invoke(object, new Object[] {value});
    } 



}
