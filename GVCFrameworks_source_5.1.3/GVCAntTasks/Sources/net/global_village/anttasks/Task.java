package net.global_village.anttasks;


/**
 * Helper methods to make org.apache.tools.ant.Task easier to use.
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public abstract class Task extends org.apache.tools.ant.Task
{

    /**
     * Returns the value of the property in this task's project, if it is set.
     * @param propertyName The name of the property. May be null, in which case 
     * the return value is also null.
     * @return the property value, or null for no match or if a null name is provided.
     */
    public String propertyNamed(String propertyName)
    {
        return getProject().getProperty(propertyName);
    }



    /**
     * Returns <code>true</code> if there is a value for the property in this task's 
     * project.   
     * @param propertyName The name of the property. May be null, in which case the 
     * return value is false.
     * @return <code>true</code> if there is a value for the property in this task's 
     * project, or <code>false</code> if there is no value or if a null name is provided.
     */
    public boolean isPropertyDefined(String propertyName)
    {
        return (getProject().getProperty(propertyName) != null);
    }


}
