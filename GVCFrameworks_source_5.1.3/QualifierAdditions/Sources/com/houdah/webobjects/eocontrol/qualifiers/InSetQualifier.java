package com.houdah.webobjects.eocontrol.qualifiers;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Qualifier to match objects according to the values in a provided list. For an object to match, the value
 * obtained by following the provided key path must match at least on of the provided values. It may thus be
 * used to replace a series of EOOrQualifiers</p>
 * 
 * <p>While the implementation of the qualifier is straightforward, the implementation of the support class is not.
 * Thing get tricky when relationships are involved. But the real fun comes with relationships based on multiple
 * joins. In this event the support class actually creates an instance of yet another custom qualifier to serve
 * as schema based qualifier. This qualifier in turn has a support class of its own. The reason this qualifier
 * is not exposed outside the support class is that its implementation provides only for the behavior needed in 
 * the context of InSetQualifierSupport. Most notably this qualifier does not support in-memory evaluation</p>
 *  
 * <p>CAVEAT EMPTOR: While the InSetQualifier has been tested in common situations, it has not actually been tested
 * for use on multiple join relationships. As per the disclaimer: use at your own risk</p>
 *   
 * <p>BTW, code already available in InSetQualifierSupport as well as in its super-class should make it possible to
 * create a sub-query qualifier (SELECT ... WHERE ... IN SELECT ...)</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 *  
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class InSetQualifier extends Qualifier implements Cloneable, EOQualifierEvaluation
{
	// Protected instance variables

	protected String keyPath;
	protected NSSet values;

	// Constructors

	/** Constructor.<BR><BR>
	 * 
	 * @param keyPath passed to the setKeyPath method
	 * @param values passed to the the setValues method
	 * @throws IllegalArgumentException as defined by the setter methods
	 * @see #setKeyPath(String)
	 * @see #setValues(NSSet)
	 */
	public InSetQualifier(String keyPath, NSSet values)
	{
		setKeyPath(keyPath);
		setValues(values);
	}

	// Public instance methods

	/** The path to the value to match against the specified set.
	 * 
	 * @return the key path as specified by the matching setter method
	 */
	public String keyPath()
	{
		return this.keyPath;
	}

	/** Sets the path to the value to match against the specified set.
	 * 
	 * @param keyPath the path. May not be null.
	 * @throws IllegalArgumentException if the path is null
	 */
	public void setKeyPath(String keyPath)
	{
		if (keyPath == null)
		{
			throw new IllegalArgumentException("The key path must not be null");
		}

		this.keyPath = keyPath;
	}

	/** The values to match against.
	 * 
	 * @return the set of values as specified using the setter method
	 */
	public NSSet values()
	{
		return this.values;
	}

	/** Sets the values to match against.
	 * 
	 * @param values the values to match against. May not be null. May not contain enterprise objects
	 * @throws IllegalArgumentException if the argument is null or contains enterprise objects
	 */
	public void setValues(NSSet values)
	{
		if (values == null)
		{
			throw new IllegalArgumentException("The values array must not be null");
		}

		this.values = values;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		Object value = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, keyPath());

		if (value == null)
		{
			value = NSKeyValueCoding.NullValue;
		}

		return values().containsObject(value);
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requiresAll)
	{
		Enumeration e = values().objectEnumerator();
		boolean didSubstitute = false;
		NSMutableSet values = new NSMutableSet(values().count());

		while (e.hasMoreElements())
		{
			Object object = e.nextElement();

			if (object instanceof EOQualifierVariable)
			{
				Object value = null;

				if (bindings != null)
				{
					value = bindings.valueForKeyPath(((EOQualifierVariable) object).key());
				}

				if (value != null)
				{
					values.addObject(value);
					didSubstitute = true;

					continue;
				}

				if (requiresAll)
				{
					throw new EOQualifier.QualifierVariableSubstitutionException(
						"Error in variable substitution: value for variable "
							+ object
							+ " not found");
				}
			}
			else
			{
				values.addObject(object);
			}
		}

		if (didSubstitute)
		{
			InSetQualifier clone = (InSetQualifier) clone();

			clone.setValues(values);

			return clone;
		}
		else
		{
			return this;
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(EOClassDescription classDescription)
	{
		StringTokenizer tokenizer = new StringTokenizer(keyPath(), KEY_PATH_SEPARATOR);

		while (tokenizer.hasMoreElements())
		{
			String key = tokenizer.nextToken();

			if (tokenizer.hasMoreElements())
			{
				classDescription = classDescription.classDescriptionForDestinationKey(key);

				if (classDescription == null)
				{
					throw new IllegalStateException("Invalid key '" + key + "' found");
				}
			}
			else
			{
				if (!classDescription.attributeKeys().containsObject(key))
				{
					throw new IllegalStateException("Invalid key '" + key + "' found");
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(keyPath());
	}

	public Object clone()
	{
		return new InSetQualifier(keyPath(), values());
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		Enumeration e = values().objectEnumerator();

		buffer.append("(");
		buffer.append(keyPath());
		buffer.append(" IN (");

		while (e.hasMoreElements())
		{
			Object object = e.nextElement();

			if (object == NSKeyValueCoding.NullValue)
			{
				buffer.append("null");
			}
			else if (object instanceof Number)
			{
				buffer.append(object);
			}
			else if (object instanceof EOQualifierVariable)
			{
				buffer.append("$");
				buffer.append(((EOQualifierVariable) object).key());
			}
			else
			{
				buffer.append("'");
				buffer.append(object);
				buffer.append("'");
			}

			if (e.hasMoreElements())
			{
				buffer.append(", ");
			}
		}

		buffer.append("))");

		return buffer.toString();
	}
}