package com.houdah.webobjects.eoaccess.coercion;

import com.houdah.webobjects.eoaccess.qualifiers.QualifierUtilities;
import com.houdah.webobjects.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.webobjects.eocontrol.qualifiers.Qualifier;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * <p>
 * Support class to apply qualifier attribute coercion to instances of
 * BestRelationshipMatchesQualifier.
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
public class BestRelationshipMatchesQualifierCoercionSupport implements
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
		BestRelationshipMatchesQualifier brmQualifier = (BestRelationshipMatchesQualifier) qualifier;
		EOQualifier subQualifier = brmQualifier.subQualifier();
		EOQualifier matchQualifier = brmQualifier.matchQualifier();
		String path = Qualifier.allButLastPathComponent(brmQualifier.keyPath());
		EOEntity destinationEntity = entity;

		if (path.length() > 0) {
			EORelationship relationship = QualifierUtilities.relationshipForPath(entity, path);

			destinationEntity = relationship.destinationEntity();
		}

		if (destinationEntity != null) {
			NSMutableDictionary substitutions = new NSMutableDictionary(2);

			substitutions.setObjectForKey(QualifierAttributeCoercion.coerceQualifierAttributes(
					subQualifier, destinationEntity),
					BestRelationshipMatchesQualifier.SUB_QUALIFIER);
			substitutions.setObjectForKey(QualifierAttributeCoercion.coerceQualifierAttributes(
					matchQualifier, destinationEntity),
					BestRelationshipMatchesQualifier.MATCH_QUALIFIER);

			return brmQualifier.clone(substitutions);
		}

		return brmQualifier;
	}
}

// $Log: 
//  5    Eclipse 5.21.4         9/7/07 8:28:41 PM      Chuck Hill      Updated 
//        to latest version from: 
//        http://www.bernard-web.com/pierre/webobjects/code.html
//  4    Eclipse 5.21.3         4/7/06 5:13:13 PM      Chuck Hill      Stripped
//         ^M
//  3    Eclipse 5.21.2         4/7/06 5:10:30 PM      Chuck Hill      Fixed 
//        JavaDoc comments
//  2    Eclipse 5.21.1         4/7/06 3:57:41 PM      Chuck Hill      Comments
//  1    Eclipse 5.21.0         4/7/06 12:00:26 PM     Chuck Hill      
// $
// Revision 1.1 2005/03/17 10:32:44 Pierre Bernard
// http://homepage.mac.com/i_love_my/webobjects.html
// Refactoring on qualifier attribute coercion
//