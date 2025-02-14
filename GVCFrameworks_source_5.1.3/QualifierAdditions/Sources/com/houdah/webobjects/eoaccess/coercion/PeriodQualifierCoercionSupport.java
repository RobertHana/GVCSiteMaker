package com.houdah.webobjects.eoaccess.coercion;

import com.houdah.webobjects.eocontrol.qualifiers.PeriodQualifier;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * <p>
 * Support class to apply qualifier attribute coercion to instances of
 * PeriodQualifier.
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
public class PeriodQualifierCoercionSupport implements QualifierAttributeCoercion.Support
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
	public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
	{
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;
		String keyPath = pQualifier.keyPath();
		EOEntity periodEntity = entity;

		if (keyPath != null) {
			NSArray keyArray = NSArray.componentsSeparatedByString(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);

			int limit = keyArray.count() - 1;

			for (int i = 0; i < limit; i++) {
				EORelationship relationship = periodEntity.anyRelationshipNamed((String) keyArray
						.objectAtIndex(i));

				if (relationship != null) {
					entity = relationship.destinationEntity();
				}
				else {
					entity = null;
					break;
				}
			}
		}

		if (periodEntity != null) {
			EOAttribute yearAttribute = entity.attributeNamed(pQualifier.yearKey());
			Object yearValue = pQualifier.yearValue();

			if (!(yearValue instanceof EOQualifierVariable)) {
				yearValue = yearAttribute.validateValue(yearValue);
			}

			EOAttribute monthAttribute = entity.attributeNamed(pQualifier.monthKey());
			Object monthValue = pQualifier.monthValue();

			if (!(monthValue instanceof EOQualifierVariable)) {
				monthValue = monthAttribute.validateValue(monthValue);
			}

			return new PeriodQualifier(pQualifier.keyPath(), pQualifier.yearKey(), pQualifier
					.monthKey(), pQualifier.qualifierOperator(), yearValue, monthValue);
		}

		return qualifier;
	}
}