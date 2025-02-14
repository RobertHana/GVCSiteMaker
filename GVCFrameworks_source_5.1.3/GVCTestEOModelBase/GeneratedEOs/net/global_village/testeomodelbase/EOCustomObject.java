package net.global_village.testeomodelbase;



/**
 * Work around for bug (?) in EOF where validateValueForKey does not call into the class description.  Either inherit from this class, or implement this method in your base class for EO objects:
 * <pre>
 *     public Object validateValueForKey( Object value, String key)
 * {
 *     return classDescription().validateValueForKey(value, key);
 * }
 * </pre>

 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class EOCustomObject extends com.webobjects.eocontrol.EOCustomObject
{

    /**
     * Standard EOF constuctor.
     */
    public EOCustomObject()
    {
        super();
    }



    /**
     * Hack for bug (?) in EOF where this is NOT calling into the class description!
     */
    public Object validateValueForKey( Object value, String key)
    {
        Object tempObject = classDescription().validateValueForKey(value, key);

        // We do this just for the call to validate<em>Key</em>.  In the future, we should change this to an invocation of that method directly.
        Object tempObject2 = super.validateValueForKey(tempObject, key);

        return tempObject2;
    }



}
