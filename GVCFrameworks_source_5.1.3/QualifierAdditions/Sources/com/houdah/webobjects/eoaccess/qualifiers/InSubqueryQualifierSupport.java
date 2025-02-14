package com.houdah.webobjects.eoaccess.qualifiers;

import java.util.Enumeration;

import com.houdah.webobjects.eocontrol.qualifiers.InSubqueryQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.Qualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

/**
 * <p>
 * Support class to handle SQL generation for InSubqueryQualifier instances.
 * </p>
 * 
 * <p>
 * While the implementation of the qualifier is straightforward, the
 * implementation of the support class is not. Thing get tricky when
 * relationships are involved. But the real fun comes with relationships based
 * on multiple joins. In this event the support class actually creates an
 * instance of yet another custom qualifier to serve as schema based qualifier.
 * This qualifier in turn has a support class of its own. The reason this
 * qualifier is not exposed outside the support class is that its implementation
 * provides only for the behavior needed in the context of
 * InSubQueryQualifierSupport. Most notably this qualifier does not support
 * in-memory evaluation.
 * </p>
 * 
 * <p>
 * While the InSubQueryQualifier has been tested in common situations, it has
 * not actually been tested for use on multiple join relationships. As per the
 * disclaimer: use at your own risk.
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
public class InSubqueryQualifierSupport extends QualifierGenerationSupport
{
	// Static initializer

	static {
		setSupportForClass(new ArrayInSubqueryQualifierSupport(), ArrayInSubqueryQualifier.class);
	}

	// Public instance methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#sqlStringForSQLExpression(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOSQLExpression)
	 */
	public String sqlStringForSQLExpression(EOQualifier qualifier, EOSQLExpression expression)
	{
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;
		ArrayInSubqueryQualifier aisQualifier = new ArrayInSubqueryQualifier(new NSArray(
				isQualifier.keyPath()), isQualifier.entityName(), new NSArray(isQualifier
				.attributePath()), isQualifier.subQualifier());
		EOQualifierSQLGeneration.Support support = supportForClass(ArrayInSubqueryQualifier.class);

		return support.sqlStringForSQLExpression(aisQualifier, expression);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#schemaBasedQualifierWithRootEntity(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOEntity)
	 */
	public EOQualifier schemaBasedQualifierWithRootEntity(EOQualifier qualifier, EOEntity entity)
	{
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;
		String keyPath = isQualifier.keyPath();

		if (isQualifier.entityName() == null) {
			EORelationship relationship = relationshipForPath(entity, keyPath);

			if (relationship.isFlattened()) {
				relationship = (EORelationship) relationship.componentRelationships().lastObject();
			}

			EOEntity destEntity = relationship.destinationEntity();
			NSArray joins = relationship.joins();
			int joinCount = joins.count();
			NSMutableArray srcAttributes = new NSMutableArray(joinCount);
			NSMutableArray destAttrNames = new NSMutableArray(joinCount);
			String path = Qualifier.allButLastPathComponent(keyPath);

			for (int i = joinCount - 1; i >= 0; i--) {
				EOJoin join = (EOJoin) joins.objectAtIndex(i);
				String srcAttributeName = join.sourceAttribute().name();
				String destAttributeName = join.destinationAttribute().name();

				srcAttributes.addObject(optimizeQualifierKeyPath(entity, path, srcAttributeName));
				destAttrNames.addObject(destAttributeName);
			}

			EOQualifier subQualifier = isQualifier.subQualifier();
			EOQualifierSQLGeneration.Support support = supportForClass(subQualifier.getClass());
			EOQualifier schemaBasedSubQualifier = support.schemaBasedQualifierWithRootEntity(
					subQualifier, destEntity);

			return new ArrayInSubqueryQualifier(srcAttributes, destEntity.name(), destAttrNames,
					schemaBasedSubQualifier);
		}
		else {
			EOEntity destEntity = entity.model().modelGroup().entityNamed(isQualifier.entityName());
			String attributePath = isQualifier.attributePath();

			EOQualifier subQualifier = isQualifier.subQualifier();
			EOQualifierSQLGeneration.Support support = supportForClass(subQualifier.getClass());
			EOQualifier schemaBasedSubQualifier = support.schemaBasedQualifierWithRootEntity(
					subQualifier, destEntity);

			return new ArrayInSubqueryQualifier(new NSArray(optimizeQualifierKeyPath(entity,
					Qualifier.allButLastPathComponent(keyPath), Qualifier
							.lastPathComponent(keyPath))), isQualifier.entityName(), new NSArray(
					optimizeQualifierKeyPath(destEntity, Qualifier
							.allButLastPathComponent(attributePath), Qualifier
							.lastPathComponent(attributePath))), schemaBasedSubQualifier);
		}
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
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;

		return new InSubqueryQualifier(translateKeyAcrossRelationshipPath(isQualifier.keyPath(),
				relationshipPath, entity), isQualifier.entityName(), isQualifier.attributePath(),
				isQualifier.subQualifier());
	}

	// Inner class

	protected static class ArrayInSubqueryQualifier extends Qualifier
	{
		// Private instance variables

		private NSArray keyPathArray;

		private String entityName;

		private NSArray attributePaths;

		private EOQualifier subQualifier;

		// Constructor

		public ArrayInSubqueryQualifier(NSArray keyPathArray, String entityName,
				NSArray attributePaths, EOQualifier subQualifier)
		{
			setKeyPathArray(keyPathArray);
			setEntityName(entityName);
			setAttributePaths(attributePaths);
			setSubQualifier(subQualifier);
		}

		// Public instance methods

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary,
		 *      boolean)
		 */
		public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requiresAll)
		{
			EOQualifier qualifier = subQualifier();

			if (qualifier != null) {
				EOQualifier boundQualifier = qualifier.qualifierWithBindings(bindings, requiresAll);

				if (qualifier != boundQualifier) {
					return new ArrayInSubqueryQualifier(keyPathArray(), entityName(),
							attributePaths(), boundQualifier);
				}
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
		 */
		public void validateKeysWithRootClassDescription(EOClassDescription classDescription)
		{
			Enumeration keys = keyPathArray().objectEnumerator();

			while (keys.hasMoreElements()) {
				Qualifier.validateKeyPathWithRootClassDescription((String) keys.nextElement(),
						classDescription);
			}

			if (subQualifier() != null) {
				EOClassDescription subDescription = EOClassDescription
						.classDescriptionForEntityName(entityName());

				subQualifier().validateKeysWithRootClassDescription(subDescription);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
		 */
		public void addQualifierKeysToSet(NSMutableSet keySet)
		{
			keySet.addObjectsFromArray(keyPathArray());

			if (subQualifier() != null) {
				subQualifier().addQualifierKeysToSet(keySet);
			}
		}

		// Protected instance methods

		protected NSArray keyPathArray()
		{
			return this.keyPathArray;
		}

		protected void setKeyPathArray(NSArray keyPathArray)
		{
			this.keyPathArray = keyPathArray;
		}

		protected String entityName()
		{
			return this.entityName;
		}

		protected void setEntityName(String entityName)
		{
			this.entityName = entityName;
		}

		protected NSArray attributePaths()
		{
			return this.attributePaths;
		}

		protected void setAttributePaths(NSArray attributePaths)
		{
			this.attributePaths = attributePaths;
		}

		protected EOQualifier subQualifier()
		{
			return this.subQualifier;
		}

		protected void setSubQualifier(EOQualifier subQualifier)
		{
			this.subQualifier = subQualifier;
		}
	}

	public static class ArrayInSubqueryQualifierSupport extends QualifierGenerationSupport
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
			ArrayInSubqueryQualifier aisQualifier = (ArrayInSubqueryQualifier) qualifier;
			EOEntity entity = expression.entity();
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			boolean hasMultipleElements = (keyPathCount > 1);
			StringBuffer buffer = new StringBuffer();

			if (hasMultipleElements) {
				buffer.append("(");
			}

			for (int i = 0; i < keyPathCount; i++) {
				String keyPath = (String) keyPathArray.objectAtIndex(i);
				String attributeString = expression.sqlStringForAttributeNamed(keyPath);

				if (attributeString == null) {
					throw new IllegalStateException(
							"sqlStringForKeyValueQualifier: attempt to generate SQL for "
									+ aisQualifier.getClass().getName() + " " + aisQualifier
									+ " failed because attribute identified by key '" + keyPath
									+ "' was not reachable from from entity '" + entity.name()
									+ "'");
				}

				if (i > 0) {
					buffer.append(", ");
				}

				buffer.append(expression.formatSQLString(attributeString, attributeForPath(entity,
						keyPath).readFormat()));
			}

			if (hasMultipleElements) {
				buffer.append(")");
			}

			buffer.append(" IN (");

			EOEntity subEntity = entity.model().modelGroup().entityNamed(aisQualifier.entityName());
			EOSQLExpression subExpression = expressionForEntity(subEntity);
			EOFetchSpecification subFetch = new EOFetchSpecification(subEntity.name(), aisQualifier
					.subQualifier(), null);
			NSArray attributePaths = aisQualifier.attributePaths();
			int aCount = attributePaths.count();
			NSMutableArray attributes = new NSMutableArray(aCount);

			for (int a = 0; a < aCount; a++) {
				attributes.addObject(attributeForPath(subEntity, (String) attributePaths
						.objectAtIndex(a)));
			}

			StringBuffer subBuffer = new StringBuffer();

			subExpression.prepareSelectExpressionWithAttributes(attributes, false, subFetch);

			subBuffer.append("SELECT ");
			subBuffer.append(subExpression.listString());
			subBuffer.append(" FROM ");
			subBuffer.append(subExpression.tableListWithRootEntity(subEntity));

			boolean hasWhereClause = (subExpression.whereClauseString() != null)
					&& (subExpression.whereClauseString().length() > 0);

			if (hasWhereClause) {
				subBuffer.append(" WHERE ");
				subBuffer.append(subExpression.whereClauseString());
			}

			if ((subExpression.joinClauseString() != null)
					&& (subExpression.joinClauseString().length() > 0)) {
				if (hasWhereClause) {
					subBuffer.append(" AND ");
				}

				subBuffer.append(subExpression.joinClauseString());
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#schemaBasedQualifierWithRootEntity(com.webobjects.eocontrol.EOQualifier,
		 *      com.webobjects.eoaccess.EOEntity)
		 */
		public EOQualifier schemaBasedQualifierWithRootEntity(EOQualifier qualifier, EOEntity entity)
		{
			return qualifier;
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
			ArrayInSubqueryQualifier aisQualifier = (ArrayInSubqueryQualifier) qualifier;
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			NSMutableArray migratedKeyPathArray = new NSMutableArray(keyPathCount);
			for (int i = 0; i < keyPathCount; i++) {
				String keyPath = (String) keyPathArray.objectAtIndex(i);
				migratedKeyPathArray.addObject(translateKeyAcrossRelationshipPath(keyPath,
						relationshipPath, entity));
			}

			return new ArrayInSubqueryQualifier(migratedKeyPathArray, aisQualifier.entityName(),
					aisQualifier.attributePaths(), aisQualifier.subQualifier());
		}
	}
}