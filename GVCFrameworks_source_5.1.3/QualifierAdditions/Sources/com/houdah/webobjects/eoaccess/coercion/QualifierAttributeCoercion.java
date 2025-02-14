package com.houdah.webobjects.eoaccess.coercion;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * <p>
 * Qualifier attribute value coercion ensures that values referenced by
 * qualifiers match the data type specified in the model. E.g. a numeral string
 * may be replaced by a number.
 * </p>
 * 
 * <p>
 * Did you notice how WebObjects 5 is less forgiving than previous versions
 * about what bindings you pass to a fetch specification? You might be seeing
 * errors that tell you to apply a formatter to a binding even though the very
 * same code worked before conversion to WebObjects 5.
 * </p>
 * 
 * <p>
 * Well, often EOF is right. You should apply formatters, not only to convert
 * strings you read from forms, but also to validate them: i.e. make sure that
 * there actually is a number where you expect one.
 * </p>
 * 
 * <p>
 * Nonetheless there are special cases where EOF gets in your way by being this
 * pedantic. I, for example, read fetch bindings in from a PLIST file where I
 * only have strings. But, from having written out the file previously, I know
 * for sure that the bindings are valid and that what Java sees as a string is
 * actually a number.
 * </p>
 * 
 * <p>
 * The provided sample code recursively walks through a fetch specification's
 * qualifier and matches each binding against the EOModel and lets the
 * attributes defined in the EOModel apply the default coercion against these
 * bindings. Thus the forgiving behavior of pre-WO5 versions of EOF is restored.
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
public class QualifierAttributeCoercion
{
	// Static initializer

	static {
		// Register support for standard qualifiers
		// Support for custom qualifiers is typically registered by the
		// principal class
		QualifierAttributeCoercion.registerSupportForClass(
				new EOKeyValueQualifierCoercionSupport(), EOKeyValueQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EONotQualifierCoercionSupport(),
				EONotQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EOAndQualifierCoercionSupport(),
				EOAndQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EOOrQualifierCoercionSupport(),
				EOOrQualifier.class);
	}

	// Proteced class variables

	protected static NSMutableDictionary coercionSupportForQualifierClasses;

	// Public class methods

	/**
	 * Register a support class for a given qualifier class.
	 * 
	 * @param support
	 *            the support class instance
	 * @param qualifierClass
	 *            the class of qualifiers to register support for
	 */
	public static void registerSupportForClass(Support support, Class qualifierClass)
	{
		coercionSupportForQualifierClasses().setObjectForKey(support, qualifierClass);
	}

	/**
	 * Applies value coercion to values passed to qualifiers referenced by the
	 * fetch specification.<BR>
	 * 
	 * The fetch specification is MODIFIED by method.
	 * 
	 * @param fetchSpec
	 *            the fetch spec to validate
	 * @param the
	 *            MODIFIED fetch specification
	 */
	public static EOFetchSpecification coerceFetchSpecificationAttributes(
			final EOFetchSpecification fetchSpec)
	{
		EOQualifier qualifier = fetchSpec.qualifier();

		if (qualifier != null) {
			EOEntity entity = EOModelGroup.defaultGroup().entityNamed(fetchSpec.entityName());

			fetchSpec.setQualifier(coerceQualifierAttributes(qualifier, entity));
		}

		return fetchSpec;
	}

	/**
	 * Applies value coercion to values passed to the supplied qualifier.<BR>
	 * 
	 * @param qualifier
	 *            the qualifier to validate
	 * @param entity
	 *            the entity this qualifier is to be applied to
	 * @param the
	 *            updated qualifier - may share branches with the original
	 *            qualifier
	 */
	public static EOQualifier coerceQualifierAttributes(final EOQualifier qualifier,
			final EOEntity entity)
	{
		Support support = (Support) coercionSupportForQualifierClasses().objectForKey(
				qualifier.getClass());

		if (support != null) {
			return support.coerceQualifierAttributes(qualifier, entity);
		}
		else {
			return qualifier;
		}
	}

	// Protected class methods

	protected static synchronized NSMutableDictionary coercionSupportForQualifierClasses()
	{
		if (QualifierAttributeCoercion.coercionSupportForQualifierClasses == null) {
			QualifierAttributeCoercion.coercionSupportForQualifierClasses = new NSMutableDictionary();
		}

		return QualifierAttributeCoercion.coercionSupportForQualifierClasses;
	}

	// Public inner interfaces

	/**
	 * Support class to apply qualifier attribute coercion to a given qualifier.
	 */
	public static interface Support
	{
		/**
		 * Applies value coercion to values passed to the supplied qualifier.<BR>
		 * 
		 * @param qualifier
		 *            the qualifier to validate
		 * @param entity
		 *            the entity this qualifier is to be applied to
		 * @param the
		 *            updated qualifier - may share branches with the original
		 *            qualifier
		 */
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity);
	}

	// Protected inner classes

	protected static class EOKeyValueQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			EOKeyValueQualifier kvQualifier = (EOKeyValueQualifier) qualifier;
			String keyPath = kvQualifier.key();
			EOAttribute attribute = null;

			if (keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1) {
				NSArray keyArray = NSArray.componentsSeparatedByString(keyPath,
						NSKeyValueCodingAdditions.KeyPathSeparator);
				int limit = keyArray.count() - 1;
				EOEntity tmpEntity = entity;

				for (int i = 0; i < limit; i++) {
					EORelationship relationship = tmpEntity.anyRelationshipNamed((String) keyArray
							.objectAtIndex(i));

					if (relationship != null) {
						tmpEntity = relationship.destinationEntity();
					}
					else {
						tmpEntity = null;
						break;
					}
				}

				if (tmpEntity != null) {
					attribute = entity.attributeNamed((String) keyArray.objectAtIndex(limit));
				}
			}
			else {
				attribute = entity.attributeNamed(keyPath);
			}

			if (attribute != null) {
				return new EOKeyValueQualifier(keyPath, kvQualifier.selector(), attribute
						.validateValue(kvQualifier.value()));
			}
			else {
				return qualifier;
			}
		}
	}

	protected static class EONotQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			EONotQualifier notQualifier = (EONotQualifier) qualifier;

			return new EONotQualifier(QualifierAttributeCoercion.coerceQualifierAttributes(
					notQualifier.qualifier(), entity));
		}
	}

	protected static class EOOrQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			NSArray qualifierBranch = ((EOOrQualifier) qualifier).qualifiers();
			int count = qualifierBranch.count();
			NSMutableArray newBranch = new NSMutableArray(count);

			for (int i = 0; i < count; i++) {
				newBranch.addObject(QualifierAttributeCoercion.coerceQualifierAttributes(
						(EOQualifier) qualifierBranch.objectAtIndex(i), entity));
			}

			return new EOOrQualifier(newBranch);
		}
	}

	protected static class EOAndQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			NSArray qualifierBranch = ((EOAndQualifier) qualifier).qualifiers();
			int count = qualifierBranch.count();
			NSMutableArray newBranch = new NSMutableArray(count);

			for (int i = 0; i < count; i++) {
				newBranch.addObject(QualifierAttributeCoercion.coerceQualifierAttributes(
						(EOQualifier) qualifierBranch.objectAtIndex(i), entity));
			}

			return new EOAndQualifier(newBranch);
		}
	}
}