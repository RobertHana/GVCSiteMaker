
package net.global_village.genericobjects;


import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import net.global_village.foundation.GVCBoolean;

/**
 * VersionableArchivableKeyedLookup differs from VersionableArchivableObject in that a known key is associated with each
 * object. This key, which must be unique where isActive is true, is used by code to retrieve a specific instance of
 * this class while maintaining a name that has meaning to the user. These keys must be pre-determined and defined in
 * the program code. For example:
 * <p>
 * 
 * <pre>
 *   
 *   public static final String cancelledStatusKey = @&quot;cancelled&quot;;
 *    
 *   public static MyVersionableArchivableKeyedLookup cancelledStatusWithEditingContext( EOEditingContext editingContext )
 *   {
 *       return VersionableArchivableKeyedLookup.objectForKeyWithEditingContextAndEntityName( cancelledStatusKey, editingContext, &quot;MyVersionableArchivableKeyedLookup&quot; );
 *   }
 * </pre>
 * 
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */  
public class VersionableArchivableKeyedLookup extends _VersionableArchivableKeyedLookup
{
    /**
     * Returns the object of entity where key == aKey and isActive == YES.  Throws an exception if there are no matching objects, or more than one matching object.  Subclasses may want to wrap this method to pass in their own entity name.
     *
     * @param editingContext the <code>EOEditingContext</code> to fetch from
     * @param aKey key of specific object to look for.
     * @param entityName the kind of objects you want to fetch with this method
     * @return object associated with <code>aKey</code>
     */
    static public VersionableArchivableKeyedLookup objectForKey( EOEditingContext editingContext, String entityName, String aKey)
    {
        /** require
        [aKey_not_null] aKey != null;
        [editingContext_not_null] editingContext != null;
        [entityName_not_null] entityName != null;   **/
        
        return (VersionableArchivableKeyedLookup) EOUtilities.objectWithQualifierFormat( editingContext, entityName, "key = %@ AND isActive = %@", new NSArray( new Object [] { aKey, GVCBoolean.trueBoolean() } ) );

        /** ensure [Result_not_null] Result != null; **/
    }
}
