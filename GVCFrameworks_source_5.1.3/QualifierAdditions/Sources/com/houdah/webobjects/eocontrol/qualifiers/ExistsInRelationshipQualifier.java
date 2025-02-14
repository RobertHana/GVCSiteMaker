package com.houdah.webobjects.eocontrol.qualifiers;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Qualifier to check for the presence of objects matching a given qualifier in a to-many relationship.</p>
 *  
 * <p>An object matches the qualifier if the to-many relationship identified by at keyPath contains at least one
 * object matching the nested qualifier as passed to the constructor.</p>
 * 
 * <p>This qualifier actually os of most interest when nested within an EONotQalifier. This allows for verifying
 * that a relationship does not contain any object matching a given qualifier.</p>
 *
 * <p>While in-memory qualification is supported, its use is not adviseable over performance concers.</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class ExistsInRelationshipQualifier
	extends Qualifier
	implements EOQualifierEvaluation, NSCoding, EOKeyValueArchiving, Cloneable
{
	// Public class constants

	/** Key value for the one argument clone method
	 */
	public static final String KEY_PATH = "keyPath",
		QUALIFIER = "qualifier",
		MIN_COUNT = "minCount";

	// Protected instance variables

	/** Path to the to-many relationship to search
	 */
	protected String keyPath;

	/** Qualifier to narrow down on elements of the relationship to check for
	 */
	protected EOQualifier qualifier;

	/** Minimum number of matches in the to-many for this qualifier to match
	 */
	protected int minCount;

	// Constructors

	/** Constructor
	 * 
	 * @param keyPath Path to the to-many relationship to search. Not null
	 * @param qualifier Qualifier to narrow down on elements of the relationship to check for
	 */
	public ExistsInRelationshipQualifier(String keyPath, EOQualifier qualifier)
	{
		this(keyPath, qualifier, 1);
	}

	/** Constructor
	 * 
	 * @param keyPath Path to the to-many relationship to search. Not null
	 * @param qualifier Qualifier to narrow down on elements of the relationship to check for
	 * @param minCount Minimum number ( > 0 )of matches in the to-many for this qualifier to match
	 */
	public ExistsInRelationshipQualifier(String keyPath, EOQualifier qualifier, int minCount)
	{
		this.keyPath = keyPath;
		this.qualifier = qualifier;
		this.minCount = (minCount < 1) ? 1 : minCount;
	}

	// Public instance qualifier

	/** Path to the to-many relationship to search
	 * 
	 * @return the value as passed to the constructor
	 */
	public String keyPath()
	{
		return this.keyPath;
	}

	/** Qualifier to narrow down on elements of the relationship to check for
	 * 
	 * @return the value as passed to the constructor
	 */
	public EOQualifier qualifier()
	{
		return this.qualifier;
	}

	/** Minimum number of matches in the to-many for this qualifier to match
	 * 
	 * @return the value as passed to the constructor
	 */
	public int minCount()
	{
		return this.minCount;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requiresAll)
	{
		EOQualifier qualifier = qualifier();

		if (qualifier != null)
		{
			EOQualifier boundQualifier = qualifier.qualifierWithBindings(bindings, requiresAll);

			if (qualifier != boundQualifier)
			{
				NSDictionary substitutions = new NSDictionary(boundQualifier, QUALIFIER);

				return (ExistsInRelationshipQualifier) clone(substitutions);
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

		if (qualifier() != null)
		{
			String path = Qualifier.allButLastPathComponent(keyPath());
			EOClassDescription subDescription = classDescription.classDescriptionForKeyPath(path);

			qualifier().validateKeysWithRootClassDescription(subDescription);
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(keyPath());

		if (qualifier() != null)
		{
			NSMutableSet subKeySet = new NSMutableSet();

			qualifier().addQualifierKeysToSet(subKeySet);

			Enumeration subKeys = subKeySet.objectEnumerator();
			String prefix = keyPath() + NSKeyValueCodingAdditions.KeyPathSeparator;

			while (subKeys.hasMoreElements())
			{
				keySet.addObject(prefix + subKeys.nextElement());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		NSArray values =
			(NSArray) NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, keyPath());

		return EOQualifier.filteredArrayWithQualifier(values, qualifier()).count() >= this.minCount;
	}

	public Object clone()
	{
		ExistsInRelationshipQualifier clone =
			new ExistsInRelationshipQualifier(keyPath(), qualifier(), minCount());

		return clone;
	}

	public ExistsInRelationshipQualifier clone(NSDictionary newValues)
	{
		ExistsInRelationshipQualifier clone = (ExistsInRelationshipQualifier) clone();

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
		return "("
			+ keyPath()
			+ " CONTAINS at least "
			+ this.minCount
			+ ((qualifier() != null)
				? (" match(es) for (" + qualifier().toString() + ")")
				: " elements");
	}

	// Conformance with NSCoding

	public Class classForCoder()
	{
		return getClass();
	}

	public static Object decodeObject(NSCoder coder)
	{
		String keyPath = (String) coder.decodeObject();
		EOQualifier qualifier = (EOQualifier) coder.decodeObject();

		try
		{
			int minCount = coder.decodeInt();

			return new ExistsInRelationshipQualifier(keyPath, qualifier, minCount);
		}
		catch (Exception e)
		{
			return new ExistsInRelationshipQualifier(keyPath, qualifier);
		}
	}

	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(keyPath());
		coder.encodeObject(qualifier());
		coder.encodeInt(minCount());
	}

	// Conformance with KeyValueCodingArchiving

	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(keyPath(), KEY_PATH);
		keyValueArchiver.encodeObject(qualifier(), QUALIFIER);
		keyValueArchiver.encodeInt(minCount(), MIN_COUNT);
	}

	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new ExistsInRelationshipQualifier(
			(String) keyValueUnarchiver.decodeObjectForKey(KEY_PATH),
			(EOQualifier) keyValueUnarchiver.decodeObjectForKey(QUALIFIER),
			(int) keyValueUnarchiver.decodeIntForKey(MIN_COUNT));
	}
}