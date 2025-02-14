package com.houdah.webobjects.eoaccess.databaseContext;

import com.webobjects.eoaccess.EODatabaseChannel;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSNotification;

/**
 * <p>
 * Default database context delegate.
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
public class DatabaseContextDelegate implements DatabaseContextDelegateInterface
{
	// Protected class constants

	/**
	 * The maximum number of database channels to open per database context.<BR>
	 * Apparently a channel is needed per level of editing context nesting.
	 */
	protected static final int MAX_CHANNELS = 2;

	// Public constructor

	/**
	 * Designated constructor
	 */
	public DatabaseContextDelegate()
	{
		super();
	}

	// Public instance methods

	/**
	 * EODatabaseContext.Delegate implementation.<BR>
	 * 
	 * Called if a fault fired but no corresponding database row could be found.
	 * In EOF 5, no exception is throw, but an empty enterprise object is
	 * returned. This may lead to an exception later on when the application
	 * later tries to save an object graph that requires the missing fault.
	 */
	public boolean databaseContextFailedToFetchObject(EODatabaseContext databaseContext,
			Object object, EOGlobalID globalID)
	{
		throw new IllegalStateException("INTEGRITY ERROR: failed to retrieve object for global ID "
				+ globalID);
	}

	/**
	 * Listener for EODatabaseContext.DatabaseChannelNeededNotification
	 * notifications.<BR>
	 * 
	 * Creates database channels as needed up to a limit of MAX_CHANNELS per
	 * database context
	 */
	public void createAdditionalDatabaseChannel(NSNotification notification)
	{
		EODatabaseChannel databaseChannel;
		EODatabaseContext databaseContext = (EODatabaseContext) notification.object();

		if (databaseContext != null) {
			if (databaseContext.registeredChannels().count() < MAX_CHANNELS) {
				databaseChannel = new EODatabaseChannel(databaseContext);

				if (databaseChannel != null) {
					databaseContext.registerChannel(databaseChannel);

					NSLog.out
							.appendln("Application - createAdditionalDatabaseChannel: channel count = "
									+ databaseContext.registeredChannels().count());
				}
			}
			else {
				NSLog.out
						.appendln("Application - createAdditionalDatabaseChannel: channel count limit exceeded");
			}
		}
	}
}