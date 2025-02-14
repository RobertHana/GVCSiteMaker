package com.houdah.webobjects.eocontrol.qualifiers;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Qualifier to match objects according to the result of a nested query.</p>
 * 
 * <p>An object matches the qualifier if the value at keyPath is included in or intersects with the result of
 * the subquery. If the keyPath leads to a relationship the entity and attributes to fetch by the subquery
 * are implied. Else they must be specified to the constructor.</p>
 * 
 * <p>While in-memory qualification is supported, its use is not adviseable over performance concerns.</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class InSubqueryQualifier
	extends Qualifier
	implements EOQualifierEvaluation, NSCoding, EOKeyValueArchiving, Cloneable
{
	// Public class constants

	/** Key value for the one argument clone method
	 */
	public static final String KEY_PATH = "keyPath",
		ENTITY_NAME = "entityName",
		ATTRIBUTE_PATH = "attributePath",
		SUB_QUALIFIER = "subQualifier";

	// Protected instance variables

	/** Path to an attribute or relationship of the qualified entity
	*/
	protected String keyPath;

	/** Name of the entity from which to get values to match against
	*/
	protected String entityName;

	/** Name of the attribute in the destination entity to match against
	*/
	protected String attributePath;

	/** Qualifier to limit the list of acceptable values
	*/
	protected EOQualifier subQualifier;

	// Constructors

	/** Constructor for queries off an attribute.
	 * 
	 * @param keyPath key path to an attribute of the qualified entity
	 * @param entityName name of the entity from which to get values to match against
	 * @param attributePath name of the attribute in the destination entity to match against
	 * @param subQualifier qualifier to limit the list of acceptable values
	 */
	public InSubqueryQualifier(
		String keyPath,
		String entityName,
		String attributePath,
		EOQualifier subQualifier)
	{
		if (keyPath == null)
		{
			throw new IllegalArgumentException("Argument keyPath may not be null");
		}

		if (!(((entityName == null) && (attributePath == null))
			|| ((entityName != null) && (attributePath != null))))
		{
			throw new IllegalArgumentException("Arguments entityName and attributePath must either be both null or both not null");
		}

		init(keyPath, entityName, attributePath, subQualifier);
	}

	/** Constructor for queries off a relationship
	 * 
	 * @param keyPath key path to a relationship of the qualified entity
	 * @param subQualifier qualifier to limit the list of acceptable values
	 */
	public InSubqueryQualifier(String keyPath, EOQualifier subQualifier)
	{
		if (keyPath == null)
		{
			throw new IllegalArgumentException("Argument keyPath may not be null");
		}

		init(keyPath, null, null, subQualifier);
	}

	// Public instance methods

	/** Path to an attribute or relationship of the qualified entity
	 * 
	 * @return the value as passed to the constructor
	 */
	public String keyPath()
	{
		return this.keyPath;
	}

	/** Name of the entity from which to get values to match against
	 * 
	 * @return the value as passed to the constructor
	 */
	public String entityName()
	{
		return this.entityName;
	}

	/** Name of the attribute in the destination entity to match against
	 * 
	 * @return the value as passed to the constructor
	 */
	public String attributePath()
	{
		return this.attributePath;
	}

	/** Qualifier to limit the list of acceptable values
	 * 
	 * @return the value as passed to the constructor
	 */
	public EOQualifier subQualifier()
	{
		return this.subQualifier;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requiresAll)
	{
		EOQualifier qualifier = subQualifier();

		if (qualifier != null)
		{
			EOQualifier boundQualifier = qualifier.qualifierWithBindings(bindings, requiresAll);

			if (qualifier != boundQualifier)
			{
				NSDictionary substitutions = new NSDictionary(boundQualifier, SUB_QUALIFIER);

				return (InSubqueryQualifier) clone(substitutions);
			}
		}

		return this;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(EOClassDescription classDescription)
	{
		Qualifier.validateKeyPathWithRootClassDescription(keyPath(), classDescription);

		if (subQualifier() != null)
		{
			EOClassDescription subDescription;

			if (entityName() != null)
			{
				subDescription = EOClassDescription.classDescriptionForEntityName(entityName());
			}
			else
			{
				subDescription = classDescription.classDescriptionForKeyPath(keyPath());
			}

			subQualifier().validateKeysWithRootClassDescription(subDescription);
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(keyPath());

		if (subQualifier() != null)
		{
			NSMutableSet subKeySet = new NSMutableSet();

			subQualifier().addQualifierKeysToSet(subKeySet);

			if (entityName() == null)
			{
				Enumeration subKeys = subKeySet.objectEnumerator();
				String prefix = keyPath() + NSKeyValueCodingAdditions.KeyPathSeparator;

				while (subKeys.hasMoreElements())
				{
					keySet.addObject(prefix + subKeys.nextElement());
				}
			}
			else
			{
				keySet.addObjectsFromArray(subKeySet.allObjects());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		EOEnterpriseObject eo = (EOEnterpriseObject) object;
		String entityName = entityName();

		if (entityName == null)
		{
			EOClassDescription classDescription =
				EOClassDescription.classDescriptionForEntityName(eo.entityName());
			EOClassDescription subDescription =
				classDescription.classDescriptionForKeyPath(keyPath());

			entityName = subDescription.entityName();
		}

		EOFetchSpecification fetchSpecification =
			new EOFetchSpecification(entityName, subQualifier(), null);
		NSArray matches = eo.editingContext().objectsWithFetchSpecification(fetchSpecification);

		Object value = eo.valueForKeyPath(keyPath());

		if (value instanceof NSArray)
		{
			NSSet values = new NSSet((NSArray) value);

			return values.intersectsSet(new NSSet(matches));
		}
		else
		{
			if (attributePath() != null)
			{
				matches = (NSArray) matches.valueForKeyPath(attributePath());
			}

			return matches.containsObject(value);
		}
	}

	public Object clone()
	{
		InSubqueryQualifier clone =
			new InSubqueryQualifier(keyPath(), entityName, attributePath, subQualifier());

		return clone;
	}

	public InSubqueryQualifier clone(NSDictionary newValues)
	{
		InSubqueryQualifier clone = (InSubqueryQualifier) clone();

		if (newValues != null)
		{
			Enumeration en = newValues.keyEnumerator();

			while (en.hasMoreElements())
			{
				String key = (String) en.nextElement();
				Object value = newValues.objectForKey(key);

				NSKeyValueCoding.Utility.takeValueForKey(clone, value, key);
			}
		}

		return clone;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("(");
		buffer.append(keyPath());
		buffer.append(" IN ( SELECT ");
		buffer.append((attributePath() != null) ? attributePath() : "*");

		if (entityName() != null)
		{
			buffer.append(" FROM ");
			buffer.append(entityName());
		}

		if (subQualifier() != null)
		{
			buffer.append(" WHERE ");
			buffer.append(subQualifier().toString());
		}

		buffer.append(")");

		return buffer.toString();
	}

	// Conformance with NSCoding

	public Class classForCoder()
	{
		return getClass();
	}

	public static Object decodeObject(NSCoder coder)
	{
		return new InSubqueryQualifier(
			(String) coder.decodeObject(),
			(String) coder.decodeObject(),
			(String) coder.decodeObject(),
			(EOQualifier) coder.decodeObject());
	}

	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(keyPath());
		coder.encodeObject(entityName());
		coder.encodeObject(attributePath());
		coder.encodeObject(subQualifier());
	}

	// Conformance with KeyValueCodingArchiving

	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(keyPath(), KEY_PATH);
		keyValueArchiver.encodeObject(entityName(), ENTITY_NAME);
		keyValueArchiver.encodeObject(attributePath(), ATTRIBUTE_PATH);
		keyValueArchiver.encodeObject(subQualifier(), SUB_QUALIFIER);
	}

	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new InSubqueryQualifier(
			(String) keyValueUnarchiver.decodeObjectForKey(KEY_PATH),
			(String) keyValueUnarchiver.decodeObjectForKey(ENTITY_NAME),
			(String) keyValueUnarchiver.decodeObjectForKey(ATTRIBUTE_PATH),
			(EOQualifier) keyValueUnarchiver.decodeObjectForKey(SUB_QUALIFIER));
	}

	// Protected instance methods

	protected void init(
		String keyPath,
		String entityName,
		String attributePath,
		EOQualifier subQualifier)
	{
		this.keyPath = keyPath;
		this.entityName = entityName;
		this.attributePath = attributePath;
		this.subQualifier = subQualifier;
	}
}