package com.houdah.webobjects.eoaccess.coercion;

import java.util.Enumeration;

import com.houdah.webobjects.eocontrol.qualifiers.InSetQualifier;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;

/**
 * <p>
 * Support class to apply qualifier attribute coercion to instances of
 * InSetQualifier.
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
public class InSetQualifierCoercionSupport implements QualifierAttributeCoercion.Support
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
		InSetQualifier isQualifier = (InSetQualifier) qualifier;
		String keyPath = isQualifier.keyPath();
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
			Enumeration values = isQualifier.values().objectEnumerator();
			NSMutableSet coercedValues = new NSMutableSet();

			while (values.hasMoreElements()) {
				coercedValues.addObject(attribute.validateValue(values.nextElement()));
			}

			return new InSetQualifier(keyPath, coercedValues);
		}
		else {
			return qualifier;
		}
	}
}
