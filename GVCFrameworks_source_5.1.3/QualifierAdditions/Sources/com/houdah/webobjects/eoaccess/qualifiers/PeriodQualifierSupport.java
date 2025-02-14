package com.houdah.webobjects.eoaccess.qualifiers;

import com.houdah.webobjects.eocontrol.qualifiers.PeriodQualifier;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * <p>
 * Support class to handle SQL generation for the PeriodQualifier instances.
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
public class PeriodQualifierSupport extends QualifierGenerationSupport
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
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;

		Object rhs = pQualifier.period();

		EOEntity entity = expression.entity();
		String yearPath = pQualifier.yearPath();
		String sqlStringForYear = expression.sqlStringForAttributeNamed(yearPath);

		if (sqlStringForYear == null) {

			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ pQualifier.getClass().getName() + " " + pQualifier
							+ " failed because attribute identified by key '" + yearPath
							+ "' was not reachable from from entity '" + entity.name() + "'");
		}

		String monthPath = pQualifier.monthPath();
		String sqlStringForMonth = expression.sqlStringForAttributeNamed(monthPath);

		if (sqlStringForMonth == null) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ pQualifier.getClass().getName() + " " + pQualifier
							+ " failed because attribute identified by key '" + monthPath
							+ "' was not reachable from from entity '" + entity.name() + "'");
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append(expression.formatSQLString(sqlStringForYear, attributeForPath(entity,
				yearPath).readFormat()));
		buffer.append(" * 100 + ");
		buffer.append(expression.formatSQLString(sqlStringForMonth, attributeForPath(entity,
				monthPath).readFormat()));
		buffer.append(" ");
		buffer.append(expression.sqlStringForSelector(pQualifier.qualifierOperator(), rhs));
		buffer.append(" ");

		// buffer.append((rhs != NSKeyValueCoding.NullValue) ? rhs : null);

		EOAttribute periodAttribute = new EOAttribute();

		periodAttribute.setName("period");
		periodAttribute.setClassName("java.lang.Number");
		periodAttribute.setValueType("i");

		if (expression.useBindVariables()) {
			NSMutableDictionary bindVariableDictionary = expression
					.bindVariableDictionaryForAttribute(periodAttribute, rhs);

			expression.addBindVariableDictionary(bindVariableDictionary);

			buffer.append(bindVariableDictionary
					.objectForKey(EOSQLExpression.BindVariablePlaceHolderKey));
		}
		else {
			buffer.append(expression.formatValueForAttribute(rhs, periodAttribute));
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
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;
		String yearPath = pQualifier.yearPath();
		String translatedYearPath = translateKeyAcrossRelationshipPath(yearPath, relationshipPath,
				entity);
		String keyPath = translatedYearPath.substring(0, translatedYearPath
				.lastIndexOf(NSKeyValueCodingAdditions.KeyPathSeparator));

		return new PeriodQualifier(keyPath, pQualifier.yearKey(), pQualifier.monthKey(), pQualifier
				.qualifierOperator(), pQualifier.yearValue(), pQualifier.monthValue());
	}
}