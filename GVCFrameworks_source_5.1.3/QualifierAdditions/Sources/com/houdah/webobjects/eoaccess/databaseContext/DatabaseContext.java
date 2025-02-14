package com.houdah.webobjects.eoaccess.databaseContext;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * <p>
 * Custom suass to EODatabaseContext.
 * </p>
 *
 * <p>
 * This class needs to be registered as default database context class by
 * calling
 * EODatabaseContext.setContextClassToRegister(MBSDatabaseContext.class); This
 * is done in the PrincipalClass of this framework.
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
public class DatabaseContext extends EODatabaseContext
{
	public DatabaseContext(EODatabase database)
	{
		super(database);
	}

	/**
	 * Internal method that handles prefetching of to-many relationships.<BR>
	 *  // TBD This is a workaround to what looks like a bug in WO 5.1 & WO 5.2.
	 * Remove as soon as it's no longer needed
	 *
	 * The problem is that even refreshing fetches don't refresh the to-many
	 * relationships they prefetch.
	 */
	public void _followToManyRelationshipWithFetchSpecification(EORelationship relationship,
			EOFetchSpecification fetchspecification, NSArray objects,
			EOEditingContext editingcontext)
	{
		int count = objects.count();

		for (int i = 0; i < count; i++) {
			EOEnterpriseObject object = (EOEnterpriseObject) objects.objectAtIndex(i);
			EOGlobalID sourceGlobalID = editingcontext.globalIDForObject(object);
			String relationshipName = relationship.name();

			if (!object.isFault()) {
				EOFaulting toManyArray = (EOFaulting) object.storedValueForKey(relationshipName);

				if (!toManyArray.isFault()) {
					EOFaulting tmpToManyArray = (EOFaulting) arrayFaultWithSourceGlobalID(
							sourceGlobalID, relationshipName, editingcontext);

					// Turn the existing array back into a fault by assigning it
					// the fault handler of the newly created fault
					toManyArray.turnIntoFault(tmpToManyArray.faultHandler());
				}
			}
		}

		super._followToManyRelationshipWithFetchSpecification(relationship, fetchspecification,
				objects, editingcontext);
	}

	public void setDelegate(Object obj)
	{
		lock();

		try {
			super.setDelegate(obj);
		}
		finally {
			unlock();
		}
	}
}