package com.houdah.webobjects.eoaccess.qualifiers;

import java.util.Enumeration;
import java.util.StringTokenizer;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;

/**
 * <p>
 * Repository class for utility methods relative to EOQualifiers.
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
public class QualifierUtilities
{
	// Public class methods

	/**
	 * @param qualifier
	 * @return NSArray of EOQualifiers which are contained in the given
	 *         qualifier.
	 */
	public static NSArray andQualifiers(EOQualifier qualifier)
	{
		return andQualifiers(qualifier, new NSMutableArray());
	}

	/**
	 * @param qualifier
	 * @return NSArray of EOQualifiers which are contained in the given
	 *         qualifier.
	 */
	public static NSArray allQualifiers(EOQualifier qualifier)
	{
		return allQualifiers(qualifier, new NSMutableArray());
	}

	public static EORelationship relationshipForPath(EOEntity entity, String keyPath)
	{
		if (keyPath != null) {
			StringTokenizer tokenizer = new StringTokenizer(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			EORelationship relationship = null;

			while (tokenizer.hasMoreElements()) {
				String key = tokenizer.nextToken();

				relationship = entity.anyRelationshipNamed(key);

				if (relationship != null) {
					entity = relationship.destinationEntity();
				}
				else {
					return null;
				}
			}

			return relationship;
		}

		return null;
	}

	// Private class methods

	private static NSMutableArray andQualifiers(EOQualifier qualifier, NSMutableArray quals)
	{
		if (qualifier instanceof EOAndQualifier) {
			EOAndQualifier andQual = (EOAndQualifier) qualifier;
			for (Enumeration e = andQual.qualifiers().objectEnumerator(); e.hasMoreElements();) {
				EOQualifier qual = (EOQualifier) e.nextElement();
				andQualifiers(qual, quals);
			}
		}
		else {
			quals.addObject(qualifier);
		}

		return quals;
	}

	private static NSMutableArray allQualifiers(EOQualifier qualifier, NSMutableArray quals)
	{
		if (qualifier instanceof EOAndQualifier || qualifier instanceof EOOrQualifier) {
			NSArray subQuals;
			if (qualifier instanceof EOAndQualifier)
				subQuals = ((EOAndQualifier) qualifier).qualifiers();
			else
				subQuals = ((EOOrQualifier) qualifier).qualifiers();
			for (Enumeration e = subQuals.objectEnumerator(); e.hasMoreElements();) {
				EOQualifier qual = (EOQualifier) e.nextElement();
				allQualifiers(qual, quals);
			}
		}
		else {
			quals.addObject(qualifier);
		}

		return quals;
	}
}