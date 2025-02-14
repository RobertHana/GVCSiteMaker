package com.houdah.webobjects.eocontrol.qualifiers;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * <p>Superclass of custom qualifiers</p>
 *
 * <p>Provides methods commonly used during in-memory evaluation or SQL generation. Please accept my
 * apologies for the lack of documentation on many of these methods. Hey, you are getting code for free! It should
 * however be straightforward to match these methods with documented methods from EOQualifier,
 * EOQualifierSQLGeneration.Support and EOExpression whose implementation is eased by the provide source code.</p>
 *
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically,
 * enjoy the read, but don't blame me for anything.</p>
 *
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public abstract class Qualifier extends EOQualifier
{
	// Public class constants

	public static final String KEY_PATH_SEPARATOR = NSKeyValueCodingAdditions.KeyPathSeparator;
	public static final char KEY_PATH_SEPARATOR_CHAR = KEY_PATH_SEPARATOR.charAt(0);
	// supposes single character separator

	// Public class methods

	public static String allButLastPathComponent(String path)
	{
		int i = path.lastIndexOf(KEY_PATH_SEPARATOR_CHAR);

		return (i < 0) ? "" : path.substring(0, i);
	}

	public static String lastPathComponent(String path)
	{
		int i = path.lastIndexOf(KEY_PATH_SEPARATOR_CHAR);

		return (i < 0) ? path : path.substring(i + 1);
	}

	public static void validateKeyPathWithRootClassDescription(String keyPath, EOClassDescription classDescription)
	{
		StringTokenizer tokenizer = new StringTokenizer(keyPath, KEY_PATH_SEPARATOR);

		while (tokenizer.hasMoreElements())
		{
			String key = tokenizer.nextToken();

			if (tokenizer.hasMoreElements())
			{
				classDescription = classDescription.classDescriptionForDestinationKey(key);

				if (classDescription == null)
				{
					throw new IllegalStateException("Invalid key '" + key + "' found");
				}
			}
			else
			{
				if (!classDescription.attributeKeys().containsObject(key))
				{
					throw new IllegalStateException("Invalid key '" + key + "' found");
				}
			}
		}
	}
}