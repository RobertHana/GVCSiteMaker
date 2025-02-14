package com.houdah.webobjects.eocontrol.qualifiers;

import java.lang.reflect.*;

import com.webobjects.foundation.*;


/** 
 * <p>Support for key-value coding access to protected methods and variables.</p>
 *
 * <p>Concrete implementation of NSKeyValueCoding.ValueAccessor as suggested in the
 * documentation of that class. A similar implementation is provided by Apple for
 * the default package.</p>
 *
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author		Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 * @see			com.houdah.webobjects.foundation.NSKeyValueCoding$ValueAccessor
 */
public class KeyValueCodingProtectedAccessor
	extends NSKeyValueCoding.ValueAccessor
{
	// Constructor

	public KeyValueCodingProtectedAccessor()
	{
		super();
	}


	// Public instance methods
	
	public Object fieldValue(Object object, Field field)
		throws IllegalArgumentException, IllegalAccessException
	{
		return field.get(object);
	}

	public void setFieldValue(Object object, Field field, Object value)
		throws IllegalArgumentException, IllegalAccessException
	{
		field.set(object, value);
	}

	public Object methodValue(Object object, Method method)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		return method.invoke(object, null);
	}

	public void setMethodValue(Object object, Method method, Object value)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		method.invoke(object, new Object[] {value});
	}
}