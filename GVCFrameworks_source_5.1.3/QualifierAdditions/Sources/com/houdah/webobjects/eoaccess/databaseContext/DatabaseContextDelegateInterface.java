package com.houdah.webobjects.eoaccess.databaseContext;

import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSNotification;

/**
 * <p>
 * Keeps the compiler happy.
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
public interface DatabaseContextDelegateInterface
{
	public boolean databaseContextFailedToFetchObject(EODatabaseContext databaseContext,
			Object object, EOGlobalID globalID);

	public void createAdditionalDatabaseChannel(NSNotification notification);
}