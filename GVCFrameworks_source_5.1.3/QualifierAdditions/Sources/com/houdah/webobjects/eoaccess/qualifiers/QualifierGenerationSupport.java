package com.houdah.webobjects.eoaccess.qualifiers;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;

import com.houdah.webobjects.eocontrol.qualifiers.*;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.*;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * <p>
 * Support methods for SQL generation.
 * </p>
 *
 * <p>
 * Provides methods commonly used during in-memory evaluation or SQL generation.
 * Please accept my apologies for the lack of documentation on many of these
 * methods. Hey, you are getting code for free! It should however be
 * straightforward to match these methods with documented methods from
 * EOQualifier, EOQualifierSQLGeneration.Support and EOExpression whose
 * implementation is eased by the provide source code.
 * </p>
 *
 * <p>
 * QualifierGenerationSupport also provide support for writing SQL generation
 * support classes generating sub-selects. Some of the code might appear ugly.
 * E.g. sub-queries are generated in their own separate table alias name space.
 * Table aliases are the changed to prevent conflict with enclosing query
 * elements. Yet this approach is likely to be the best available as much of
 * Apple's code in this domain is private and provides no means of customization
 * short of a rewrite.
 * </p>
 *
 * <p>
 * This sample code is provided for educational purposes. It is mainly to be
 * considered a source of information and a way to spark off discussion. I make
 * no warranty, expressed or implied, about the quality of this code or its
 * usefulness in any particular situation. Use this code or any code based on it
 * at your own risk. Basically, enjoy the read, but don't blame me for anything.
 * </p>
 *
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public abstract class QualifierGenerationSupport extends EOQualifierSQLGeneration.Support
{
	// Protected class variables

	protected static WeakHashMap prefixesByExpression;

	protected static String[] prefixes;

	protected static WeakReference[] locks;

	// Static initializer

	static {
		char start = 'A', end = 'Z';
		int count = end - start;
		int offset = 0;

		QualifierGenerationSupport.prefixes = new String[count];

		for (int i = 0; i < count; i++) {
			char prefix = (char) (start + i);

			if (prefix == 'T') {
				offset += 1;
			}
			else {
				QualifierGenerationSupport.prefixes[i] = new Character(prefix).toString();
			}
		}

		QualifierGenerationSupport.locks = new WeakReference[count - offset];
		QualifierGenerationSupport.prefixesByExpression = new WeakHashMap(count - offset);
	}

	// Protected instance methods

	protected EOAttribute attributeForPath(EOEntity entity, String keyPath)
	{
		if (keyPath != null) {
			StringTokenizer tokenizer = new StringTokenizer(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			EORelationship relationship = null;

			while (tokenizer.hasMoreElements()) {
				String key = tokenizer.nextToken();

				if (tokenizer.hasMoreElements()) {
					relationship = entity.anyRelationshipNamed(key);
				}
				else {
					return entity.anyAttributeNamed(key);
				}

				if (relationship != null) {
					entity = relationship.destinationEntity();
				}
				else {
					return null;
				}
			}

			return null;
		}

		return null;
	}

	protected EORelationship relationshipForPath(EOEntity entity, String keyPath)
	{
		if (keyPath != null) {
			StringTokenizer tokenizer = new StringTokenizer(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			EORelationship relationship = null;

			while (tokenizer.hasMoreElements()) {
				String key = tokenizer.nextToken();

				relationship = entity.anyRelationshipNamed(key);

				if (relationship != null) {
					entity = relationship.destinationEntity();
				}
				else {
					return null;
				}
			}

			return relationship;
		}

		return null;
	}

	protected String optimizeQualifierKeyPath(EOEntity entity, String keyPath, String attributeName)
	{
		if ((keyPath == null) || (keyPath.length() == 0)) {
			return attributeName;
		}
		else {
			EORelationship relationship = (entity == null) ? null : relationshipForPath(entity,
					keyPath);

			if (relationship != null) {
				NSArray joins = relationship.joins();
				int joinCount = (joins == null) ? 0 : joins.count();

				for (int i = joinCount - 1; i >= 0; i--) {
					EOJoin join = (EOJoin) joins.objectAtIndex(i);

					if (join.destinationAttribute().name().equals(attributeName)) {
						String newPath = Qualifier.allButLastPathComponent(keyPath);
						String newAttributeName = join.sourceAttribute().name();

						return optimizeQualifierKeyPath(entity, newPath, newAttributeName);
					}
				}
			}

			return (keyPath + Qualifier.KEY_PATH_SEPARATOR + attributeName);
		}
	}

	protected String translateKeyAcrossRelationshipPath(String keyPath, String relationshipPath,
			EOEntity entity)
	{
		String flattenedKeyPath = flattenRelationshipPath(keyPath, entity);
		String flattenedRelationshipPath = flattenRelationshipPath(relationshipPath, entity);
		String translationPath = flattenedRelationshipPath + Qualifier.KEY_PATH_SEPARATOR;

		if (flattenedKeyPath.startsWith(translationPath)) {
			String translatedKeyPath = flattenedKeyPath.substring(translationPath.length());
			EOEntity destinationEntity = relationshipForPath(entity, flattenedRelationshipPath)
					.destinationEntity();

			return optimizeQualifierKeyPath(destinationEntity, Qualifier
					.allButLastPathComponent(translatedKeyPath), Qualifier
					.lastPathComponent(translatedKeyPath));
		}
		else {
			StringTokenizer tokenizer = new StringTokenizer(flattenedRelationshipPath,
					Qualifier.KEY_PATH_SEPARATOR);
			EOEntity tmpEntity = entity;
			StringBuffer invertedRelationshipPath = new StringBuffer(flattenedKeyPath.length());

			while (tokenizer.hasMoreElements()) {
				String relationshipName = tokenizer.nextToken();
				EORelationship relationship = tmpEntity.anyRelationshipNamed(relationshipName);

				try {
					if (invertedRelationshipPath.length() > 0) {
						invertedRelationshipPath.insert(0, Qualifier.KEY_PATH_SEPARATOR_CHAR);
					}

					invertedRelationshipPath
							.insert(0, relationship.anyInverseRelationship().name());
				}
				catch (Exception exception) {
					throw new NSForwardException(exception);
				}

				tmpEntity = relationship.destinationEntity();
			}

			if (flattenedKeyPath.indexOf(Qualifier.KEY_PATH_SEPARATOR_CHAR) >= 0) {
				invertedRelationshipPath.append(Qualifier.KEY_PATH_SEPARATOR_CHAR);
				invertedRelationshipPath
						.append(Qualifier.allButLastPathComponent(flattenedKeyPath));
			}

			return optimizeQualifierKeyPath(tmpEntity, invertedRelationshipPath.toString(),
					Qualifier.lastPathComponent(flattenedKeyPath));
		}
	}

	protected String flattenRelationshipPath(String path, EOEntity entity)
	{
		if (path.indexOf(Qualifier.KEY_PATH_SEPARATOR_CHAR) >= 0) {
			String relationshipPath = null;
			StringTokenizer tokenizer = new StringTokenizer(path, Qualifier.KEY_PATH_SEPARATOR);

			while (tokenizer.hasMoreElements()) {
				String relationshipName = tokenizer.nextToken();
				EORelationship relationship = entity.anyRelationshipNamed(relationshipName);

				if (relationship == null) {
					if (!tokenizer.hasMoreElements()) {
						return relationshipPath + Qualifier.KEY_PATH_SEPARATOR + relationshipName;
					}
					else {
						throw new IllegalArgumentException("No relationship '" + relationshipName
								+ "' was found in entity '" + entity.name()
								+ "' while evaluating relationship path '" + path);
					}
				}
				else {
					String element;

					if (relationship.isFlattened()) {
						element = relationship.relationshipPath();
					}
					else {
						element = relationship.name();
					}

					if (relationshipPath != null) {
						relationshipPath = relationshipPath + Qualifier.KEY_PATH_SEPARATOR
								+ element;
					}
					else {
						relationshipPath = element;
					}

					entity = relationship.destinationEntity();
				}
			}

			return relationshipPath;
		}
		else {
			EORelationship relationship = entity.anyRelationshipNamed(path);

			if (relationship == null) {
				return path;
			}
			else {
				if (relationship.isFlattened()) {
					return relationship.relationshipPath();
				}
				else {
					return relationship.name();
				}
			}
		}
	}

	protected NSMutableDictionary replaceTableAliasesInExpressionBuffer(StringBuffer buffer,
			EOSQLExpression expression)
	{
		NSMutableDictionary aliasesByRelationshipPath = expression.aliasesByRelationshipPath();
		NSArray subExpressionAliases = aliasesByRelationshipPath.allKeys();
		int aliasCount = subExpressionAliases.count();

		for (int i = 0; i < aliasCount; i++) {
			String relationshipPath = (String) subExpressionAliases.objectAtIndex(i);
			String alias = (String) aliasesByRelationshipPath.objectForKey(relationshipPath);
			String newAlias = aliasPrefixForExpression(expression) + i;
			RegularExpression regExp = new RegularExpression("[ ,(]" + alias + "[ ,\\,,.]");
			int start = 0;
			Match match = new Match();
			String currentString;

			while (regExp.matches(currentString = buffer.toString(), start, currentString.length(),
					match)) {
				start = match.getBeginning(0) + 1;
				buffer.replace(start, match.getEnd(0) - 1, newAlias);
			}

			aliasesByRelationshipPath.setObjectForKey(newAlias, relationshipPath);
		}

		return aliasesByRelationshipPath;
	}

	protected EOSQLExpression expressionForEntity(EOEntity entity)
	{
		try {
			Class expressionClass =  EOAdaptor.adaptorWithModel(entity.model())
					.expressionClass();
			Constructor constructor = expressionClass
					.getConstructor(new Class[] { EOEntity.class });
			EOSQLExpression expression = (EOSQLExpression) constructor
					.newInstance(new Object[] { entity });

			return expression;
		}
		catch (Exception exception) {
			throw new NSForwardException(exception);
		}
	}

	// Protected class methods

	protected static synchronized String aliasPrefixForExpression(EOSQLExpression expression)
	{
		String prefix = (String) QualifierGenerationSupport.prefixesByExpression.get(expression);

		if (prefix == null) {
			prefix = assignPrefixToExpression(expression);

			if (prefix == null) {
				System.gc();

				prefix = assignPrefixToExpression(expression);
			}

			if (prefix == null) {
				throw new RuntimeException("The number of available alias prefixes was exceeded. "
						+ "Fix me if you please");
			}
		}

		return prefix;
	}

	// Private class methods

	private static synchronized String assignPrefixToExpression(EOSQLExpression expression)
	{
		int count = QualifierGenerationSupport.locks.length;
		int availableIndex = 0;

		while (availableIndex < count) {
			WeakReference lock = QualifierGenerationSupport.locks[availableIndex];

			if ((lock == null) || (lock.get() == null)) {
				QualifierGenerationSupport.locks[availableIndex] = null;

				break;
			}
			else {
				availableIndex++;
			}
		}

		if (availableIndex < count) {
			String prefix = QualifierGenerationSupport.prefixes[availableIndex];

			QualifierGenerationSupport.locks[availableIndex] = new WeakReference(expression);
			QualifierGenerationSupport.prefixesByExpression.put(expression, prefix);

			return prefix;
		}
		else {
			return null;
		}
	}
}