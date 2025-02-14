package com.houdah.webobjects.eoaccess.coercion;

import com.houdah.webobjects.eocontrol.qualifiers.PiggybackQualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eocontrol.EOQualifier;

/**
 * <p>
 * Support class to apply qualifier attribute coercion to instances of
 * PiggybackQualifier.
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
public class PiggybackQualifierCoercionSupport implements QualifierAttributeCoercion.Support
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
		PiggybackQualifier pbQualifier = (PiggybackQualifier) qualifier;
		PiggybackQualifier newQualifier = new PiggybackQualifier(QualifierAttributeCoercion
				.coerceQualifierAttributes(pbQualifier.qualifier(), entity));

		newQualifier.userInfo().addEntriesFromDictionary(pbQualifier.userInfo());

		return newQualifier;
	}
}