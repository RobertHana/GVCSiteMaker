package com.houdah.webobjects.eoaccess.qualifiers;

import java.util.*;

import com.houdah.webobjects.eocontrol.qualifiers.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * <p>
 * Support class to handle SQL generation for the ExistsInRelationshipQualifier
 * instances.
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
public class ExistsInRelationshipQualifierSupport extends QualifierGenerationSupport
{
	// Public instance methods

	/*
	 * (non-Javadoc)
	 *
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#sqlStringForSQLExpression(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOSQLExpression)
	 */
	public String sqlStringForSQLExpression(EOQualifier qualifier, EOSQLExpression expression)
	{
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		String keyPath = eirQualifier.keyPath();
		EOEntity entity = expression.entity();
		EORelationship relationship = relationshipForPath(entity, keyPath);
		NSArray joins = relationship.joins();
		int joinCount = joins.count();
		EOEntity subEntity = relationship.destinationEntity();
		EOQualifier subQualifier = eirQualifier.qualifier();
		StringBuffer subBuffer = new StringBuffer();
		EOSQLExpression subExpression = expressionForEntity(subEntity);
		EOFetchSpecification subFetch = new EOFetchSpecification(subEntity.name(), subQualifier,
				null);
		StringBuffer buffer = new StringBuffer();
		int minCount = eirQualifier.minCount();

		if (minCount > 1) {
			buffer.append(" ");
			buffer.append(eirQualifier.minCount());
			buffer.append(" <= ");

			subBuffer.append(" (SELECT count(*) FROM ");
		}
		else {
			buffer.append(" EXISTS (");

			subBuffer.append("SELECT 1 FROM ");
		}

		subExpression.prepareSelectExpressionWithAttributes(subEntity.primaryKeyAttributes(),
				false, subFetch);

		subBuffer.append(subExpression.tableListWithRootEntity(subEntity));
		subBuffer.append(" WHERE ");
		subBuffer.append(subExpression.whereClauseString());

		if ((subExpression.joinClauseString() != null)
				&& (subExpression.joinClauseString().length() > 0)) {
			subBuffer.append(" AND ");
			subBuffer.append(subExpression.joinClauseString());
		}

		replaceTableAliasesInExpressionBuffer(subBuffer, subExpression);

		if (joinCount > 0) {
			for (int j = 0; j < joinCount; j++) {
				EOJoin join = (EOJoin) joins.objectAtIndex(j);
				EOAttribute sourceAttribute = join.sourceAttribute();
				EOAttribute destinationAttribute = join.destinationAttribute();

				subBuffer.append(" AND ");
				subBuffer.append(expression._aliasForRelatedAttributeRelationshipPath(
						sourceAttribute, Qualifier.allButLastPathComponent(keyPath)));
				subBuffer.append(" = ");
				subBuffer.append(subExpression._aliasForRelatedAttributeRelationshipPath(
						destinationAttribute, ""));
			}
		}

		buffer.append(subBuffer);
		buffer.append(")");

		Enumeration bindVariables = subExpression.bindVariableDictionaries().objectEnumerator();

		while (bindVariables.hasMoreElements()) {
			expression.addBindVariableDictionary((NSDictionary) bindVariables.nextElement());
		}

		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#schemaBasedQualifierWithRootEntity(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOEntity)
	 */
	public EOQualifier schemaBasedQualifierWithRootEntity(EOQualifier qualifier, EOEntity entity)
	{

		NSMutableDictionary substitutions = new NSMutableDictionary(1);
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		String keyPath = eirQualifier.keyPath();
		EORelationship relationship = relationshipForPath(entity, keyPath);

		if ((relationship != null) && relationship.isFlattened()) {
            NSArray componentRelationships = relationship.componentRelationships();
			int crCount = componentRelationships.count();

            // BUG FIX
            int relationshipIndex = keyPath.indexOf(relationship.name());
            String prefix = (relationshipIndex > 0) ? keyPath.substring(0, relationshipIndex - 1) : "";
            StringBuffer pathToPivot = new StringBuffer(prefix);
            // BUG FIX

			StringBuffer pathFromPivot = new StringBuffer();

			StringBuffer path = pathToPivot;
			EOEntity subEntity = null;

			for (int cr = 0; cr < crCount; cr++) {
				EORelationship component = (EORelationship) componentRelationships
						.objectAtIndex(cr);

				if (path.length() > 0) {
					path.append(NSKeyValueCodingAdditions.KeyPathSeparator);
				}

				path.append(component.name());

				if ((subEntity == null) && component.isToMany()) {
					path = pathFromPivot;
					subEntity = component.destinationEntity();
				}
			}

			substitutions.setObjectForKey(pathToPivot.toString(),
					ExistsInRelationshipQualifier.KEY_PATH);

			InSubqueryQualifier isQualifier = new InSubqueryQualifier(pathFromPivot.toString(),
					eirQualifier.qualifier());
			Support support = supportForClass(isQualifier.getClass());

			if (support == null) {
				throw new IllegalArgumentException("Qualifier " + isQualifier
						+ " has no support class");
			}
			else {
				substitutions.setObjectForKey(support.schemaBasedQualifierWithRootEntity(
						isQualifier, subEntity), ExistsInRelationshipQualifier.QUALIFIER);
			}
		}
		else {
			EOEntity subEntity = (relationship != null) ? relationship.destinationEntity() : entity;
			EOQualifier subQualifier = eirQualifier.qualifier();

			if (subQualifier != null) {
				Support support = supportForClass(subQualifier.getClass());

				substitutions.setObjectForKey(support.schemaBasedQualifierWithRootEntity(
						subQualifier, subEntity), ExistsInRelationshipQualifier.QUALIFIER);
			}
		}

		return eirQualifier.clone(substitutions);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#qualifierMigratedFromEntityRelationshipPath(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOEntity, java.lang.String)
	 */
	public EOQualifier qualifierMigratedFromEntityRelationshipPath(EOQualifier qualifier,
			EOEntity entity, String relationshipPath)
	{
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		NSMutableDictionary substitutions = new NSMutableDictionary(1);

		substitutions.setObjectForKey(translateKeyAcrossRelationshipPath(eirQualifier.keyPath(),
				relationshipPath, entity), ExistsInRelationshipQualifier.KEY_PATH);

		return eirQualifier.clone(substitutions);
	}
}