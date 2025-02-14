package com.houdah.webobjects.eoaccess.qualifiers;

import java.util.Enumeration;

import com.houdah.webobjects.eocontrol.qualifiers.BestMatchQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.Qualifier;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.Support;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * <p>
 * Support class to handle SQL generation for the BestMatchQualifier instances.
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
public class BestMatchQualifierSupport extends QualifierGenerationSupport
{
	// Public instance methods

	public String sqlStringForSQLExpression(EOQualifier qualifier, EOSQLExpression expression)
	{
		BestMatchQualifier bmQualifier = (BestMatchQualifier) qualifier;
		String keyPath = bmQualifier.keyPath();
		EOEntity entity = expression.entity();
		EOAttribute attribute = attributeForPath(entity, keyPath);
		String path = Qualifier.allButLastPathComponent(keyPath);
		String key = Qualifier.lastPathComponent(keyPath);
		EORelationship relationship = relationshipForPath(entity, path);
		EOEntity subEntity = (relationship != null) ? relationship.destinationEntity() : entity;
		String mainExpressionAttributeString = expression.sqlStringForAttributeNamed(keyPath);

		if (mainExpressionAttributeString == null) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for " + bmQualifier
							+ " failed because attribute identified by key '" + keyPath
							+ "' was not reachable from from entity '" + entity.name() + "'");
		}

		Object limit = bmQualifier.limit();

		if (limit instanceof EOQualifierVariable) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ bmQualifier.getClass().getName() + " " + bmQualifier
							+ " failed because the qualifier variable '$"
							+ ((EOQualifierVariable) limit).key() + "' is unbound.");
		}

		StringBuffer buffer = new StringBuffer();
		EOQualifier subQualifier = bmQualifier.subQualifier();

		// Qualify the main expression to match only items that also match the
		// sub expression
		if (subQualifier != null) {
			if (subQualifier instanceof EOQualifierSQLGeneration) {
				buffer.append(((EOQualifierSQLGeneration) subQualifier)
						.sqlStringForSQLExpression(expression));
			}
			else {
				Support support = supportForClass(subQualifier.getClass());

				if (support == null) {
					throw new IllegalArgumentException("Qualifier " + subQualifier
							+ " has no support class");
				}
				else {
					buffer.append(support.sqlStringForSQLExpression(subQualifier, expression));
				}
			}

			buffer.append(" AND ");
		}

		StringBuffer subBuffer = new StringBuffer();
		EOSQLExpression subExpression = expressionForEntity(subEntity);
		NSMutableArray subQualifiers = new NSMutableArray(2);

		if (limit != NSKeyValueCoding.NullValue) {
			subQualifiers.addObject(new EOKeyValueQualifier(key, bmQualifier.qualifierOperator(),
					limit));
		}

		if (subQualifier != null) {
			subQualifiers.addObject(subQualifier);
		}

		EOFetchSpecification subFetch = new EOFetchSpecification(subEntity.name(), (subQualifiers
				.count() != 0) ? new EOAndQualifier(subQualifiers) : null, null);

		subExpression
				.prepareSelectExpressionWithAttributes(new NSArray(attribute), false, subFetch);

		buffer.append(mainExpressionAttributeString);
		buffer.append(" IN (");

		// Begin sub-select
		subBuffer.append("SELECT ");

		if (bmQualifier.qualifierOperator().equals(EOQualifier.QualifierOperatorLessThan)
				|| bmQualifier.qualifierOperator().equals(
						EOQualifier.QualifierOperatorLessThanOrEqualTo)) {
			subBuffer.append("MAX");
		}
		else {
			subBuffer.append("MIN");
		}

		subBuffer.append("(");
		subBuffer.append(subExpression.listString());
		subBuffer.append(") FROM ");
		subBuffer.append(subExpression.tableListWithRootEntity(subEntity));
		subBuffer.append(" WHERE ");
		subBuffer.append(subExpression.whereClauseString());

		if ((subExpression.joinClauseString() != null)
				&& (subExpression.joinClauseString().length() > 0)) {
			subBuffer.append(" AND ");
			subBuffer.append(subExpression.joinClauseString());
		}

		NSArray uniquingPaths = bmQualifier.uniquingPaths();
		int uCount = uniquingPaths.count();

		if (uCount > 0) {
			String comma = ", ";

			subBuffer.append(" GROUP BY ");

			for (int u = 0; u < uCount; u++) {
				String uniquingPath = (String) uniquingPaths.objectAtIndex(u);

				if (u > 0) {
					subBuffer.append(comma);
				}

				subBuffer.append(subExpression.sqlStringForAttributeNamed(uniquingPath));
			}
		}

		replaceTableAliasesInExpressionBuffer(subBuffer, subExpression);

		buffer.append(subBuffer);
		buffer.append(")");

		Enumeration bindVariables = subExpression.bindVariableDictionaries().objectEnumerator();

		while (bindVariables.hasMoreElements()) {
			expression.addBindVariableDictionary((NSDictionary) bindVariables.nextElement());
		}

		return buffer.toString();
	}

	public EOQualifier schemaBasedQualifierWithRootEntity(EOQualifier qualifier, EOEntity entity)
	{
		BestMatchQualifier bmQualifier = (BestMatchQualifier) qualifier;
		String keyPath = bmQualifier.keyPath();

		if (relationshipForPath(entity, keyPath) == null) {
			NSMutableDictionary substitutions = new NSMutableDictionary(2);
			String key = bmQualifier.keyPath();
			String path = Qualifier.allButLastPathComponent(key);
			EORelationship relationship = relationshipForPath(entity, path);
			EOEntity subEntity = (relationship != null) ? relationship.destinationEntity() : entity;
			EOQualifier subQualifier = bmQualifier.subQualifier();
			NSArray uniquingPaths = bmQualifier.uniquingPaths();
			int uCount = uniquingPaths.count();

			if (subQualifier != null) {
				Support support = supportForClass(subQualifier.getClass());

				substitutions.setObjectForKey(support.schemaBasedQualifierWithRootEntity(
						subQualifier, subEntity), BestMatchQualifier.SUB_QUALIFIER);
			}

			if (uCount > 0) {
				NSMutableArray optimizedPaths = new NSMutableArray(uCount);

				for (int u = 0; u < uCount; u++) {
					String uniquingPath = (String) uniquingPaths.objectAtIndex(u);
					EORelationship uRelationship = relationshipForPath(subEntity, uniquingPath);

					if (uRelationship != null) {
						NSArray joins = uRelationship.joins();
						int joinCount = joins.count();

						for (int j = 0; j < joinCount; j++) {
							String sourceAttributeName = ((EOJoin) joins.objectAtIndex(j))
									.sourceAttribute().name();

							optimizedPaths.addObject(sourceAttributeName);
						}
					}
					else if (attributeForPath(subEntity, keyPath) != null) {
						optimizedPaths.addObject(uniquingPath);
					}
					else {
						throw new IllegalStateException("The uniquing key path " + uniquingPath
								+ " cannot be reached from entity " + subEntity.name());
					}
				}

				substitutions.setObjectForKey(optimizedPaths, BestMatchQualifier.UNIQUING_PATHS);
			}

			return bmQualifier.clone(substitutions);
		}
		else {
			throw new IllegalStateException("The key path may not lead to a relationship. "
					+ "A comparison on joins makes no sense");
		}
	}

	public EOQualifier qualifierMigratedFromEntityRelationshipPath(EOQualifier qualifier,
			EOEntity entity, String relationshipPath)
	{
		BestMatchQualifier bmQualifier = (BestMatchQualifier) qualifier;
		NSArray uniquingPaths = bmQualifier.uniquingPaths();
		int uCount = uniquingPaths.count();
		NSMutableDictionary substitutions = new NSMutableDictionary(2);

		if (uCount > 0) {
			NSMutableArray paths = new NSMutableArray(uCount);

			for (int u = 0; u < uCount; u++) {
				String uniquingPath = (String) uniquingPaths.objectAtIndex(u);

				paths.addObject(translateKeyAcrossRelationshipPath(uniquingPath, relationshipPath,
						entity));
			}

			substitutions.setObjectForKey(paths, BestMatchQualifier.UNIQUING_PATHS);
		}

		substitutions.setObjectForKey(translateKeyAcrossRelationshipPath(bmQualifier.keyPath(),
				relationshipPath, entity), BestMatchQualifier.KEY_PATH);

		return bmQualifier.clone(substitutions);
	}
}