package net.global_village.eofextensions;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Subclass of EOGenericRecord providing default copying behaviour.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 * @deprecated Use GenericRecord.  There does not seem to be any point in this functionality separate in this class
 */
public class CopyableGenericRecord extends EOGenericRecord implements EOCopying
{

    /**
     * Designated constuctor.
     */
    public CopyableGenericRecord()
    {
        super();
    }


    /**
     * Returns a copy of this object, copying related objects as well.  The actual copy mechanism (by reference, shallow, deep, or custom) for each object is up to the object being copied.  If a copy already exists in <code>copiedObjects</code>, then that is returned instead of making a new copy.  This allows complex graphs of objects, including those with cycles, to be copied without producing duplicate objects.  The grapch of copied objects will be the same regardless of where copy is started with two exceptions: it is started on a reference copied object or a reference copied object is the only path between two disconnected parts of the graph.  In these cases the reference copied object prevents the copy from following the graph further.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject copy(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

       return EOCopying.DefaultImplementation.copy(copiedObjects, this);

        /** ensure [copy_made] Result != null; [copy_recorded] copiedObjects.objectForKey(editingContext().globalIDForObject(this)) != null;  **/
    }



    /**
     * Convenience cover method for copy(NSMutableDictionary) that creates the dictionary internally.  You can use this to start the copying of a graph if you have no need to reference the dictionary.
     *
     * @return a copy of this object
     */
    public EOEnterpriseObject copy()
    {
        return copy(new NSMutableDictionary());

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Returns a copy of this object.  Each EOEnterpriseObject can override this this to produce the actual copy by an appropriate mechanism (reference, shallow, deep, or custom).  The default is a deep copy.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this object
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        return EOCopying.Utility.deepCopy(copiedObjects, this);

        /** ensure [copy_made] Result != null;   **/
    }

}
