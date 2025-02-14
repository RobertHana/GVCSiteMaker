package com.houdah.webobjects.eocontrol.qualifiers;

import java.io.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/** 
 * <p>Allows for attaching information to a qualifier by means of a userInfo dictionary.</p>
 * 
 * <p>Serves for piggybacking values on a qualifier or for tagging it so it can be easily 
 * retrieved in a qualifier tree. This might come in handy were all two pieces of code 
 * share is a qualifier and yet they need to pass each other some parameters.</p>
 * 
 * <p>Yes, I know, this is hackish.</p>
 * 
 * <p>This sample code is provided for educational purposes. It is mainly to be considered a source of information 
 * and a way to spark off discussion. I make no warranty, expressed or implied, about the quality of this code or 
 * its usefulness in any particular situation. Use this code or any code based on it at your own risk. Basically, 
 * enjoy the read, but don't blame me for anything.</p>
 * 
 * @author Pierre Bernard http://homepage.mac.com/i_love_my/webobjects.html
 */
public class PiggybackQualifier
	extends Qualifier
	implements Serializable, NSCoding, EOKeyValueArchiving
{
	// Protected instance variables

	protected NSMutableDictionary userInfo = null;
	protected EOQualifier qualifier;

	// Constructor

	public PiggybackQualifier(EOQualifier qualifier)
	{
		this.qualifier = qualifier;
	}

	// Public instance methods

	public NSMutableDictionary userInfo()
	{
		if (this.userInfo == null)
		{
			synchronized (this)
			{
				if (this.userInfo == null)
				{
					this.userInfo = new NSMutableDictionary();
				}
			}
		}

		return this.userInfo;
	}

	public EOQualifier qualifier()
	{
		return this.qualifier;
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary, boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings, boolean requireAll)
	{
		EOQualifier boundQualifier = qualifier().qualifierWithBindings(bindings, requireAll);

		if (boundQualifier != null)
		{
			if (boundQualifier == qualifier())
			{
				return this;
			}
			else
			{
				PiggybackQualifier pbQualifier = new PiggybackQualifier(boundQualifier);

				pbQualifier.userInfo().addEntriesFromDictionary(userInfo());

				return pbQualifier;
			}
		}
		else
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(EOClassDescription classDescription)
	{
		qualifier().validateKeysWithRootClassDescription(classDescription);
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		qualifier().addQualifierKeysToSet(keySet);
	}

	/* (non-Javadoc)
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		return qualifier().evaluateWithObject(object);
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof PiggybackQualifier)
		{
			return qualifier().equals(((PiggybackQualifier) obj).qualifier());
		}
		else
		{
			return false;
		}
	}

	public String toString()
	{
		return "(" + qualifier().toString() + ")";
	}

	public Class classForCoder()
	{
		return getClass();
	}

	public static Object decodeObject(NSCoder coder)
	{
		PiggybackQualifier pbQualifier = new PiggybackQualifier((EOQualifier) coder.decodeObject());

		pbQualifier.userInfo = (NSMutableDictionary) coder.decodeObject();

		return pbQualifier;
	}

	public void encodeWithCoder(NSCoder nscoder)
	{
		nscoder.encodeObject(qualifier());
		nscoder.encodeObject(userInfo());
	}

	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(qualifier(), "qualifier");
		keyValueArchiver.encodeObject(userInfo(), "userInfo");
	}

	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyvalueUnarchiver)
	{
		PiggybackQualifier pbQualifier =
			new PiggybackQualifier(
				(EOQualifier) keyvalueUnarchiver.decodeObjectForKey("qualifier"));

		pbQualifier.userInfo =
			(NSMutableDictionary) keyvalueUnarchiver.decodeObjectForKey("qualifier");

		return pbQualifier;
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException
	{
		ObjectOutputStream.PutField putfield = objectOutputStream.putFields();

		putfield.put("qualifier", qualifier());
		putfield.put("userInfo", userInfo());
		objectOutputStream.writeFields();
	}

	private void readObject(ObjectInputStream objectInputStream)
		throws IOException, ClassNotFoundException
	{
		ObjectInputStream.GetField getfield = null;

		getfield = objectInputStream.readFields();
		this.qualifier = (EOQualifier) getfield.get("qualifier", null);
		this.userInfo = (NSMutableDictionary) getfield.get("userInfo", null);
	}
}