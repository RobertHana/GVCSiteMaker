package com.houdah.webobjects.eocontrol.qualifiers;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Qualifier matching no values. Used where an actual qualifier is needed, but no objects should be matched.</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class FalseQualifier extends Qualifier
{
	// Constructor

	public FalseQualifier()
	{
	}

	// Public instance methods

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requireAll)
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(EOClassDescription classDescription)
	{
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		return false;
	}
}