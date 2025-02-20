package com.houdah.webobjects.eoaccess.qualifiers;

import java.util.Enumeration;

import com.houdah.webobjects.eocontrol.qualifiers.InSetQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.Qualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

/**
 * <p>
 * Support class to handle SQL generation for InSetQualifier instances.
 * </p>
 * 
 * <p>
 * The code for this qualifier contains one ugly bit. In order to determine the
 * string used to represent a match value in the SQL query I have the JDBC
 * adaptor generate SQL for a key-value qualifier and extract relevant bits from
 * it.
 * </p>
 * 
 * <p>
 * As it stands, the adaptor plugins apply database vendor specific
 * transformation to values in a key-value qualifier. One such transformation is
 * right padding values when querying against a fixed width char column.
 * Unfortunately these transformations are not directly available from the
 * public methods in the adaptor or plugin
 * </p>
 * 
 * This leaves two alternatives. The above described hack or reproducing the
 * desired behavior with code like the one below. The second approach has two
 * distinct disadvantages. For one it is only partial: it takes care only of
 * right padding. Other transformations may be needed. Most importantly though
 * the below code is database vendor specific. In spite of having vendor
 * specific behavior grouped in the plugin it gets scattered over various
 * qualifier SQL generation support classes
 * </p>
 * 
 * <pre>
 * protected boolean needsPadding(EOSQLExpression expression, String keyPath)
 * {
 * 	EOEntity entity = expression.entity();
 * 	EOAttribute attribute = attributeForPath(entity, keyPath);
 * 
 * 	return needsPadding(attribute);
 * }
 * 
 * protected boolean needsPadding(EOAttribute attribute)
 * {
 * 	String valueType = attribute.valueType();
 * 
 * 	return ((valueType != null) &amp;&amp; (valueType.startsWith(&quot;c&quot;)));
 * }
 * 
 * protected String padSqlStringForValueAsNeeded(EOSQLExpression expression, String sqlStringForValue,
 * 		String keyPath)
 * {
 * 	EOEntity entity = expression.entity();
 * 	EOAttribute attribute = attributeForPath(entity, keyPath);
 * 
 * 	if (needsPadding(attribute)) {
 * 		sqlStringForValue = padSqlStringForValue(attribute, sqlStringForValue);
 * 	}
 * 
 * 	return sqlStringForValue;
 * }
 * 
 * protected String padSqlStringForValue(EOAttribute attribute, String sqlStringForValue)
 * {
 * 	// TODO: This is ugly as it supposes that we have an Oracle database.
 * 	// This should be handled by the plugin
 * 	return &quot;RPAD(&quot; + sqlStringForValue + &quot;, &quot; + attribute.width() + &quot;)&quot;;
 * }
 * </pre>
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
public class InSetQualifierSupport extends QualifierGenerationSupport
{
	// Static initializer

	static {
		setSupportForClass(new ArrayInSetQualifierSupport(), ArrayInSetQualifier.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#sqlStringForSQLExpression(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOSQLExpression)
	 */
	public String sqlStringForSQLExpression(EOQualifier qualifier, EOSQLExpression expression)
	{
		InSetQualifier isQualifier = (InSetQualifier) qualifier;
		String keyPath = isQualifier.keyPath();
		NSSet values = isQualifier.values();
		Enumeration e = values.objectEnumerator();
		NSMutableSet valueArrays = new NSMutableSet(values.count());

		while (e.hasMoreElements()) {
			valueArrays.addObject(new NSArray(e.nextElement()));
		}

		ArrayInSetQualifier aisQualifier = new ArrayInSetQualifier(new NSArray(keyPath),
				valueArrays);
		EOQualifierSQLGeneration.Support support = supportForClass(ArrayInSetQualifier.class);

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
		InSetQualifier isQualifier = (InSetQualifier) qualifier;
		String keyPath = isQualifier.keyPath();
		EORelationship relationship = relationshipForPath(entity, keyPath);

		if (relationship != null) {
			if (relationship.isFlattened()) {
				relationship = (EORelationship) relationship.componentRelationships().lastObject();
			}

			NSArray joins = relationship.joins();
			int joinCount = joins.count();
			NSMutableArray destAttrNames = new NSMutableArray(joinCount);
			NSMutableArray optimizedPaths = new NSMutableArray(joinCount);

			for (int i = joinCount - 1; i >= 0; i--) {
				String destAttributeName = ((EOJoin) joins.objectAtIndex(i)).destinationAttribute()
						.name();

				destAttrNames.addObject(destAttributeName);
				optimizedPaths.addObject(optimizeQualifierKeyPath(entity, keyPath,
						destAttributeName));
			}

			NSMutableSet newValues = new NSMutableSet(isQualifier.values().count());
			Enumeration values = isQualifier.values().objectEnumerator();

			while (values.hasMoreElements()) {
				Object value = values.nextElement();

				if (value == NSKeyValueCoding.NullValue || (value instanceof EOQualifierVariable)) {
					NSMutableArray destValues = new NSMutableArray(joinCount);

					for (int j = 0; j < joinCount; j++) {
						destValues.addObject(value);
					}

					newValues.addObject(destValues);
				}
				else {
					EOEnterpriseObject enterpriseObject = (EOEnterpriseObject) value;
					EOObjectStoreCoordinator objectStoreCoordinator = (EOObjectStoreCoordinator) enterpriseObject
							.editingContext().rootObjectStore();
					NSDictionary destValues = objectStoreCoordinator.valuesForKeys(destAttrNames,
							enterpriseObject);

					newValues.addObject(destValues.objectsForKeys(destAttrNames,
							NSKeyValueCoding.NullValue));
				}
			}

			if (destAttrNames.count() == 1) {
				NSMutableSet singleValues = new NSMutableSet(newValues.count());
				Enumeration newValEnum = newValues.objectEnumerator();

				while (newValEnum.hasMoreElements()) {
					NSArray newValue = (NSArray) newValEnum.nextElement();

					singleValues.addObject(newValue.objectAtIndex(0));
				}

				return nullValueAwareQualifier(new InSetQualifier((String) optimizedPaths
						.objectAtIndex(0), singleValues));
			}
			else {
				return new ArrayInSetQualifier(optimizedPaths, newValues);
			}
		}
		else {
			return nullValueAwareQualifier(isQualifier);
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
		InSetQualifier isQualifier = (InSetQualifier) qualifier;

		return new InSetQualifier(translateKeyAcrossRelationshipPath(isQualifier.keyPath(),
				relationshipPath, entity), isQualifier.values());
	}

	// Protected instance methods

	protected EOQualifier nullValueAwareQualifier(InSetQualifier isQualifier)
	{
		if (isQualifier.values().containsObject(NSKeyValueCoding.NullValue)) {
			EOQualifier nullQualifier = new EOKeyValueQualifier(isQualifier.keyPath(),
					EOQualifier.QualifierOperatorEqual, NSKeyValueCoding.NullValue);

			return new EOOrQualifier(new NSArray(new EOQualifier[] {
					nullQualifier,
					new InSetQualifier(isQualifier.keyPath(), isQualifier.values()
							.setBySubtractingSet(new NSSet(NSKeyValueCoding.NullValue))) }));
		}
		else {
			return isQualifier;
		}
	}

	// Inner class

	protected static class ArrayInSetQualifier extends Qualifier
	{
		// Private instance variables

		private NSArray keyPathArray;

		private NSSet values;

		// Constructor

		public ArrayInSetQualifier(NSArray keyPathArray, NSSet values)
		{
			setKeyPathArray(keyPathArray);
			setValues(values);
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
			Enumeration e1 = values().objectEnumerator();
			boolean didSubstitute = false;
			NSMutableSet boundValues = new NSMutableSet(values().count());

			while (e1.hasMoreElements()) {
				NSArray objects = (NSArray) e1.nextElement();
				NSMutableArray values = new NSMutableArray(objects.count());
				Enumeration e2 = objects.objectEnumerator();

				while (e2.hasMoreElements()) {
					Object object = e2.nextElement();

					if (object instanceof EOQualifierVariable) {
						Object value = null;

						if (bindings != null) {
							value = bindings.valueForKeyPath(((EOQualifierVariable) object).key());
						}

						if (value != null) {
							values.addObject(value);
							didSubstitute = true;

							continue;
						}

						if (requiresAll) {
							throw new EOQualifier.QualifierVariableSubstitutionException(
									"Error in variable substitution: value for variable " + object
											+ " not found");
						}
					}
					else {
						values.addObject(object);
					}
				}

				boundValues.addObject(values);
			}

			if (didSubstitute) {
				return new ArrayInSetQualifier(keyPathArray(), boundValues);
			}
			else {
				return this;
			}
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
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
		 */
		public void addQualifierKeysToSet(NSMutableSet keySet)
		{
			keySet.addObjectsFromArray(keyPathArray());
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

		protected NSSet values()
		{
			return this.values;
		}

		protected void setValues(NSSet values)
		{
			this.values = values;
		}
	}

	public static class ArrayInSetQualifierSupport extends QualifierGenerationSupport
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#sqlStringForSQLExpression(com.webobjects.eocontrol.EOQualifier,
		 *      com.webobjects.eoaccess.EOSQLExpression)
		 */
		public String sqlStringForSQLExpression(EOQualifier qualifier, EOSQLExpression expression)
		{
			ArrayInSetQualifier aisQualifier = (ArrayInSetQualifier) qualifier;
			EOEntity entity = expression.entity();
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			boolean hasMultipleElements = (keyPathCount > 1);
			int[] cutPoints = new int[keyPathCount];
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

				// Initialize to prepare for nex loop
				cutPoints[i] = -1;
			}

			if (hasMultipleElements) {
				buffer.append(")");
			}

			buffer.append(" IN (");

			Enumeration e = aisQualifier.values().objectEnumerator();
			boolean firstIteration = true;

			while (e.hasMoreElements()) {
				NSArray value = (NSArray) e.nextElement();

				if (value.count() != keyPathCount) {
					throw new IllegalStateException(
							"sqlStringForKeyValueQualifier: key and value counts don't match");
				}

				if (firstIteration) {
					firstIteration = false;
				}
				else {
					buffer.append(", ");
				}

				if (hasMultipleElements) {
					buffer.append("(");
				}

				for (int i = 0; i < keyPathCount; i++) {
					Object oneValue = value.objectAtIndex(i);
					String keyPath = (String) keyPathArray.objectAtIndex(i);

					if (oneValue instanceof EOQualifierVariable) {
						throw new IllegalStateException(
								"sqlStringForKeyValueQualifier: attempt to generate SQL for "
										+ aisQualifier.getClass().getName() + " " + aisQualifier
										+ " failed because the qualifier variable '$"
										+ ((EOQualifierVariable) oneValue).key() + "' is unbound.");
					}

					if (i > 0) {
						buffer.append(", ");
					}

					// This is ugly: We generate SQL for a key-value qualifier
					// and then extract
					// the part representing the value. Unfortunately, there are
					// case where the
					// database adaptor must apply vendor specific magic to the
					// value. One such
					// transformation is padding values for qualifying against
					// fixed width CHAR
					// columns. These transformations are not publicly exposed
					// by the adaptor.
					//
					// I guess the below code is the best we can get while still
					// database vendor
					// independant.

					EOKeyValueQualifier kvQualifier = new EOKeyValueQualifier(keyPath,
							EOQualifier.QualifierOperatorEqual, oneValue);
					String sqlStringForQualifier = expression
							.sqlStringForKeyValueQualifier(kvQualifier);

					if (cutPoints[i] == -1) {
						String equalString = expression.sqlStringForSelector(
								EOQualifier.QualifierOperatorEqual, value);

						cutPoints[i] = 1 + sqlStringForQualifier.indexOf(equalString);
					}

					buffer.append(sqlStringForQualifier.substring(cutPoints[i],
							sqlStringForQualifier.length()));
				}

				if (hasMultipleElements) {
					buffer.append(")");
				}
			}

			buffer.append(")");

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
			return nullValueAwareQualifier((ArrayInSetQualifier) qualifier);
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
			ArrayInSetQualifier aisQualifier = (ArrayInSetQualifier) qualifier;
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			NSMutableArray migratedKeyPathArray = new NSMutableArray(keyPathCount);

			for (int i = 0; i < keyPathCount; i++) {
				String keyPath = (String) keyPathArray.objectAtIndex(i);

				migratedKeyPathArray.addObject(translateKeyAcrossRelationshipPath(keyPath,
						relationshipPath, entity));
			}

			return new ArrayInSetQualifier(migratedKeyPathArray, aisQualifier.values());
		}

		// Protected instance methods

		protected EOQualifier nullValueAwareQualifier(ArrayInSetQualifier aisQualifier)
		{
			Enumeration values = aisQualifier.values().objectEnumerator();
			NSMutableSet plainValues = new NSMutableSet(aisQualifier.values().count());
			NSMutableArray otherQualifiers = new NSMutableArray();

			while (values.hasMoreElements()) {
				NSArray value = (NSArray) values.nextElement();

				if (value.containsObject(NSKeyValueCoding.NullValue)) {
					otherQualifiers.addObject(EOQualifier
							.qualifierToMatchAllValues(new NSDictionary(value, aisQualifier
									.keyPathArray())));
				}
				else {
					plainValues.addObject(value);
				}
			}

			if (otherQualifiers.count() == 0) {
				return aisQualifier;
			}
			else {
				// Could optimize this by grouping into a minimal number of
				// InSetQualifiers

				otherQualifiers.addObject(new ArrayInSetQualifier(aisQualifier.keyPathArray(),
						plainValues));

				return new EOAndQualifier(otherQualifiers);
			}
		}
	}
}