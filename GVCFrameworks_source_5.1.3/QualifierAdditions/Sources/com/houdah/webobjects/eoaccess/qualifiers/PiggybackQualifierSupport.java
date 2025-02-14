package com.houdah.webobjects.eoaccess.qualifiers;

import com.houdah.webobjects.eocontrol.qualifiers.PiggybackQualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.Support;
import com.webobjects.eocontrol.EOQualifier;

/**
 * <p>
 * Support class for the PiggybackQualifier.
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
public class PiggybackQualifierSupport extends QualifierGenerationSupport
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
		PiggybackQualifier pbQualifier = (PiggybackQualifier) qualifier;
		EOQualifier subQualifier = pbQualifier.qualifier();
		Support support = supportForClass(subQualifier.getClass());

		if (support == null) {
			throw new IllegalArgumentException("Qualifier " + subQualifier
					+ " has no support class");
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append("(");
		buffer.append(support.sqlStringForSQLExpression(subQualifier, expression));
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
		PiggybackQualifier pbQualifier = (PiggybackQualifier) qualifier;
		EOQualifier subQualifier = pbQualifier.qualifier();
		Support support = supportForClass(subQualifier.getClass());

		if (support == null) {
			throw new IllegalArgumentException("Qualifier " + subQualifier
					+ " has no support class");
		}

		EOQualifier schemaBasedSubQualifier = support.schemaBasedQualifierWithRootEntity(
				subQualifier, entity);

		if (subQualifier == schemaBasedSubQualifier) {
			return pbQualifier;
		}
		else {
			PiggybackQualifier schemaBasedQualifier = new PiggybackQualifier(
					schemaBasedSubQualifier);

			schemaBasedQualifier.userInfo().addEntriesFromDictionary(pbQualifier.userInfo());

			return schemaBasedQualifier;
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
		PiggybackQualifier pbQualifier = (PiggybackQualifier) qualifier;
		EOQualifier subQualifier = pbQualifier.qualifier();
		Support support = supportForClass(subQualifier.getClass());

		if (support == null) {
			throw new IllegalArgumentException("Qualifier " + subQualifier
					+ " has no support class");
		}

		EOQualifier migratedSubQualifier = support.qualifierMigratedFromEntityRelationshipPath(
				subQualifier, entity, relationshipPath);

		if (subQualifier == migratedSubQualifier) {
			return pbQualifier;
		}
		else {
			PiggybackQualifier migratedQualifier = new PiggybackQualifier(migratedSubQualifier);

			migratedQualifier.userInfo().addEntriesFromDictionary(pbQualifier.userInfo());

			return migratedQualifier;
		}
	}
}