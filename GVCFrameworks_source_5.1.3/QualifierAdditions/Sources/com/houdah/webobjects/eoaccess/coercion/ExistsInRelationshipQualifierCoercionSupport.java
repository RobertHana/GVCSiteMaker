package com.houdah.webobjects.eoaccess.coercion;

import com.houdah.webobjects.eoaccess.qualifiers.QualifierUtilities;
import com.houdah.webobjects.eocontrol.qualifiers.ExistsInRelationshipQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.Qualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;

/**
 * <p>
 * Support class to apply qualifier attribute coercion to instances of
 * ExistsInRelationshipQualifier.
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
public class ExistsInRelationshipQualifierCoercionSupport implements
		QualifierAttributeCoercion.Support
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
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		EOQualifier subQualifier = eirQualifier.qualifier();
		String path = Qualifier.allButLastPathComponent(eirQualifier.keyPath());
		EOEntity destinationEntity = entity;

		if (path.length() > 0) {
			EORelationship relationship = QualifierUtilities.relationshipForPath(entity, path);

			destinationEntity = relationship.destinationEntity();
		}

		if (destinationEntity != null) {
			NSDictionary substitutions = new NSDictionary(QualifierAttributeCoercion
					.coerceQualifierAttributes(subQualifier, destinationEntity),
					ExistsInRelationshipQualifier.QUALIFIER);

			return eirQualifier.clone(substitutions);
		}

		return eirQualifier;
	}
}