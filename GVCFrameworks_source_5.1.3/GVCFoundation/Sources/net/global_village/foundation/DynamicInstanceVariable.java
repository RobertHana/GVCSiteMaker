package net.global_village.foundation;

import com.webobjects.foundation.NSMutableDictionary;


/**
 * Allows "instance variables" to be dynamically added to an object.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class DynamicInstanceVariable
{
    /**
     * A dictionary that contains the namespaces for each object (which themselves contain the instance variables for that object).
     */
    protected static NSMutableDictionary _instanceVariables;


    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private DynamicInstanceVariable()
    {
        super();
    }



    /**
     * Gets the dictionary of "instance variables".
     *
     * @return the <code>_instanceVariables</code> global
     */
    private static synchronized NSMutableDictionary instanceVariables()
    {
        if (_instanceVariables == null)
        {
            _instanceVariables = new NSMutableDictionary();
        }
        return _instanceVariables;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Makes sure the namespace for <code>anObject</code> exists. The namespace is a unique dictionary for each object in which dynamic "instance variables" are stored.
     *
     * @param anObject the object to check the namespace for
     * @return <code>true</code> if the namespace exists already in the <code>_instanceVariables</code> dictionary, <code>false</code> otherwise
     */
    protected static synchronized boolean namespaceExists(Object anObject)
    {
        /** require [valid_param] anObject != null; **/

        NSMutableDictionary instanceVariables = instanceVariables();
        return instanceVariables.objectForKey(new Integer(anObject.hashCode())) != null;
    }



    /**
     * Gets (and creates if it does not already exist) the namespace of <code>anObject</code>.
     *
     * @param anObject the object to get the namespace for
     * @return the namespace for <code>anObject</code>
     */
    public static synchronized NSMutableDictionary namespace(Object anObject)
    {
        /** require [valid_param] anObject != null; **/

        NSMutableDictionary instanceVariables = instanceVariables();
        NSMutableDictionary namespace = (NSMutableDictionary)instanceVariables.objectForKey(new Integer(anObject.hashCode()));
        if (namespace == null)
        {
            namespace = new NSMutableDictionary();
            instanceVariables.setObjectForKey(namespace, new Integer(anObject.hashCode()));
        }
        return namespace;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns true if the ivar <code>ivarName</code> exists.
     *
     * @param anObject the object to test the ivar for
     * @param ivarName the ivar to test for
     * @return <code>true</code> if the instance variable <code>ivarName</code> exists for <code>anObject</code>, <code>false</code> otherwise
     */
    public static synchronized boolean instanceVariableExists(Object anObject, String ivarName)
    {
        /** require [valid_anObject_param] anObject != null; [valid_ivarName_param] ivarName != null; **/

        boolean instanceVariableExists;

        if (namespaceExists(anObject))
        {
            NSMutableDictionary namespace = namespace(anObject);
            instanceVariableExists = namespace.objectForKey(ivarName) != null;
        }
        else
        {
            instanceVariableExists = false;
        }

        return instanceVariableExists;
    }



    /**
     * Returns the ivar <code>ivarName</code>.
     *
     * @param anObject the object to get the ivar from
     * @param ivarName the ivar to get
     * @return the instance variable given by <code>ivarName</code> on <code>anObject</code>
     */
    public static synchronized Object instanceVariable(Object anObject, String ivarName)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_ivarName_param] ivarName != null;
        [ivar_exists] instanceVariableExists(anObject, ivarName); **/

        NSMutableDictionary namespace = namespace(anObject);
        return namespace.objectForKey(ivarName);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sets the ivar <code>ivarName</code>.
     *
     * @param anObject the object to set the ivar to
     * @param ivarName the ivar to set
     * @param value the value to set
     */
    public static synchronized void setInstanceVariable(Object anObject, String ivarName, Object value)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_ivarName_param] ivarName != null;
        [valid_value_param] value != null; **/

        NSMutableDictionary namespace = namespace(anObject);
        namespace.setObjectForKey(value, ivarName);

        /** ensure [ivar_exists] instanceVariableExists(anObject, ivarName); **/
    }



    /**
     * Removes the ivar <code>ivarName</code> from <code>anObject</code>'s namespace.
     *
     * @param anObject the object to remove the ivar from
     * @param ivarName the ivar to remove
     */
    public static synchronized void removeInstanceVariable(Object anObject, String ivarName)
    {
        /** require
        [valid_anObject_param] anObject != null;
        [valid_ivarName_param] ivarName != null;
        [ivar_exists] instanceVariableExists(anObject, ivarName); **/

        NSMutableDictionary namespace = namespace(anObject);
        namespace.removeObjectForKey(ivarName);

        /** ensure [ivar_doesnt_exist] ! instanceVariableExists(anObject, ivarName); **/
    }



    /**
     * Removes from <code>instanceVariables</code> <code>anObject</code>'s namespace.
     *
     * @param anObject the object to dealloc ivars for
     */
    public static synchronized void deallocInstanceVariables(Object anObject)
    {
        /** require [valid_param] anObject != null; **/

        if (namespaceExists(anObject))
        {
            instanceVariables().removeObjectForKey(new Integer(anObject.hashCode()));
        }

        /** ensure [namespace_doesnt_exist] ! namespaceExists(anObject); **/
    }



}
