package com.houdah.webobjects.eoaccess.utilities;

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * <p>
 * Utility class used to perform speciality fetches.
 * </p>
 * 
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
public class SpecialityFetches
{
	// Private class constants

	private static CountDelegate countDelegate = null;

	private static ExistsDelegate existsDelegate = null;

	private static EOAttribute dummyAttribute = null;

	// Constructor

	/**
	 * Designated constructor
	 */
	private SpecialityFetches()
	{
		throw new IllegalStateException("Do not instantiate this utility class");

	}

	// Public class methods

	/**
	 * Counts the number of database rows matching a given fetch specification.<br>
	 * 
	 * @param editingContext
	 *            editing context to use
	 * @param fetchSpecification
	 *            fetch specification to match
	 * @return number of matching rows
	 */
	public static int objectCountWithFetchSpecification(EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		EOObjectStoreCoordinator objectStoreCoordinator = (EOObjectStoreCoordinator) editingContext
				.rootObjectStore();
		EOEntity entity = EOUtilities.entityNamed(editingContext, fetchSpecification.entityName());
		NSArray rows = Utilities.fetchAttributes(objectStoreCoordinator,
				entity.attributesToFetch(), fetchSpecification, countDelegate());
		NSDictionary row = (NSDictionary) rows.objectAtIndex(0);
		NSArray keys = row.allKeys();
		Integer result = (Integer) row.objectForKey(keys.objectAtIndex(0));

		return result.intValue();
	}

	/**
	 * Determines the maximum value for a given attribute in database rows
	 * matching a given fetch specification.<br>
	 * 
	 * @param editingContext
	 *            editing context to use
	 * @param fetchSpecification
	 *            fetch specification to match
	 * @param attributeName
	 *            name of the attribute
	 * @return maximum value for the named attribute in the matched rows
	 */
	public static Object attributeMaxWithFetchSpecification(EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification, String attributeName)
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext, fetchSpecification.entityName());
		EOAttribute attribute = entity.attributeNamed(attributeName);
		EOAttribute maxAttribute = new EOAttribute();

		maxAttribute.setName("p_max" + attribute.name());
		maxAttribute.setColumnName("p_max" + attribute.name());
		maxAttribute.setClassName(attribute.className());
		maxAttribute.setValueType(attribute.valueType());
		maxAttribute.setReadFormat("max(" + attribute.columnName() + ")");

		EOObjectStoreCoordinator objectStoreCoordinator = (EOObjectStoreCoordinator) editingContext
				.rootObjectStore();
		NSArray rows = Utilities.fetchAttributes(objectStoreCoordinator, new NSArray(maxAttribute),
				fetchSpecification, null);
		NSDictionary row = (NSDictionary) rows.objectAtIndex(0);

		return row.objectForKey(maxAttribute.name());
	}

	/**
	 * Fetches distinct values for a given attribute in database rows matching a
	 * given fetch specification.<br>
	 * 
	 * @param editingContext
	 *            editing context to use
	 * @param fetchSpecification
	 *            fetch specification to match
	 * @param attributeName
	 *            name of the attribute
	 * @return array of unique values for the attribute
	 */
	public static NSArray distinctAttributeWithFetchSpecification(EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification, String attributeName)
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext, fetchSpecification.entityName());
		EOAttribute attribute = entity.attributeNamed(attributeName);
		EOObjectStoreCoordinator objectStoreCoordinator = (EOObjectStoreCoordinator) editingContext
				.rootObjectStore();
		EOFetchSpecification distinctFetchSpecification = (EOFetchSpecification) fetchSpecification
				.clone();

		distinctFetchSpecification.setUsesDistinct(true);

		NSArray rows = Utilities.fetchAttributes(objectStoreCoordinator, new NSArray(attribute),
				distinctFetchSpecification, null);

		return (NSArray) rows.valueForKey(attribute.name());
	}

	/**
	 * Determines if there are any database rows matching a given fetch
	 * specification.<br>
	 * 
	 * @param editingContext
	 *            editing context to use
	 * @param fetchSpecification
	 *            fetch specification to match
	 * @return true if at least one row matches
	 */
	public static boolean existsWithFetchSpecification(EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		EOObjectStoreCoordinator objectStoreCoordinator = (EOObjectStoreCoordinator) editingContext
				.rootObjectStore();
		NSArray rows = Utilities.fetchAttributes(objectStoreCoordinator, new NSArray(
				dummyAttribute()), fetchSpecification, existsDelegate());

		return (rows.count() != 0);
	}

	// Private class methods

	private synchronized static CountDelegate countDelegate()
	{
		if (SpecialityFetches.countDelegate == null) {
			SpecialityFetches.countDelegate = new CountDelegate();
		}

		return SpecialityFetches.countDelegate;
	}

	private synchronized static ExistsDelegate existsDelegate()
	{
		if (SpecialityFetches.existsDelegate == null) {
			SpecialityFetches.existsDelegate = new ExistsDelegate();
		}

		return SpecialityFetches.existsDelegate;
	}

	private synchronized static EOAttribute dummyAttribute()
	{
		if (SpecialityFetches.dummyAttribute == null) {
			SpecialityFetches.dummyAttribute = new EOAttribute();
			SpecialityFetches.dummyAttribute.setName("p_dummyAttribute");
			SpecialityFetches.dummyAttribute.setColumnName("p_dummyAttribute");
			SpecialityFetches.dummyAttribute.setExternalType("NUMBER");
			SpecialityFetches.dummyAttribute.setClassName("java.lang.Number");
			SpecialityFetches.dummyAttribute.setValueType("i");
			SpecialityFetches.dummyAttribute.setReadFormat("1");
		}

		return SpecialityFetches.dummyAttribute;
	}

	// Public inner classes

	/**
	 * Intentionally undocumented. For internal use only.
	 */
	public static class CountDelegate
	{
		public void adaptorChannelDidSelectAttributes(EOAdaptorChannel channel, NSArray attributes,
				EOFetchSpecification fetchSpec, boolean lock, EOEntity entity)
		{
			channel.setAttributesToFetch(new NSArray(SpecialityFetches.dummyAttribute()));
		}

		public boolean adaptorChannelShouldEvaluateExpression(EOAdaptorChannel channel,
				EOSQLExpression expression)
		{
			expression.setStatement("SELECT count(*) FROM (" + expression.statement() + ")");

			return true;
		}
	}

	/**
	 * Intentionally undocumented. For internal use only.
	 */
	public static class ExistsDelegate
	{
		public boolean adaptorChannelShouldEvaluateExpression(EOAdaptorChannel channel,
				EOSQLExpression expression)
		{
			EOEntity entity = expression.entity();
			String tableName = entity.externalName();
			StringBuffer statement = new StringBuffer();

			statement.append("SELECT 1 FROM ");
			statement.append("(SELECT count(*) FROM ");
			statement.append(tableName);
			statement.append(" WHERE 1=0)");
			statement.append(" WHERE EXISTS (");
			statement.append(expression.statement());
			statement.append(")");

			expression.setStatement(statement.toString());
			return true;
		}
	}
}