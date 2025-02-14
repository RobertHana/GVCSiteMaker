// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;

/**
 * Status for Tasks.
 *
 */
public class TaskStatus extends _TaskStatus
{
    public static final String waitingStatusKey = "waiting";
    public static final String completedStatusKey = "completed";
    public static final String cancelledStatusKey = "cancelled";
    public static final String failedStatusKey = "failed";
    public static final String inProgressStatusKey = "inProgress";
    
    /**
     * Factory method to create new instances of TaskStatus.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of TaskStatus or a subclass.
     */
    public static TaskStatus newTaskStatus()
    {
        return (TaskStatus) SMEOUtils.newInstanceOf("TaskStatus");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Convenience method to return "waiting" status
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Waiting TaskStatus in the indicated editing context.
     */
    public static TaskStatus waitingStatus(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
    		return (TaskStatus) TaskStatus.objectForKey(ec, "TaskStatus", waitingStatusKey);

        /** ensure [result_not_null] Result != null; **/
    } 
    
    
    
    /**
     * Convenience method to return "failed" status
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Waiting TaskStatus in the indicated editing context.
     */
    public static TaskStatus failedStatus(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
    		return (TaskStatus) TaskStatus.objectForKey(ec, "TaskStatus", failedStatusKey);

        /** ensure [result_not_null] Result != null; **/
    }     
    
    
    
    /**
     * Convenience method to return "cancelled" status
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Waiting TaskStatus in the indicated editing context.
     */
    public static TaskStatus cancelledStatus(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
    		return (TaskStatus) TaskStatus.objectForKey(ec, "TaskStatus", cancelledStatusKey);

        /** ensure [result_not_null] Result != null; **/
    }   
    
    
    
    
    /**
     * Convenience method to return "inProgress" status
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Waiting TaskStatus in the indicated editing context.
     */
    public static TaskStatus inProgressStatus(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/

    		return (TaskStatus) TaskStatus.objectForKey(ec, "TaskStatus", inProgressStatusKey);

        /** ensure [result_not_null] Result != null; **/
    }   
    
    
    
    /**
     * Convenience method to return "completed" status
     *
     * @param ec - the editing context to return the Public Group in
     * @returns the Waiting TaskStatus in the indicated editing context.
     */
    public static TaskStatus completedStatus(EOEditingContext ec)
    {
        /** require [ec_not_null] ec != null; **/
        
    		return (TaskStatus) TaskStatus.objectForKey(ec, "TaskStatus", completedStatusKey);

        /** ensure [result_not_null] Result != null; **/
    }       
    
    

    /**
     * @return <code>true</code> if this is the Waiting status
     */
    public boolean isWaiting()
    {
        return waitingStatusKey.equals(key());
    }
    
    

    /**
     * @return <code>true</code> if this is the Waiting status
     */
    public boolean isCompleted()
    {
        return completedStatusKey.equals(key());
    }    
    

    /**
     * @return <code>true</code> if this is the Waiting status
     */
    public boolean isCancelled()
    {
        return cancelledStatusKey.equals(key());
    }    
    

    /**
     * @return <code>true</code> if this is the Waiting status
     */
    public boolean isFailed()
    {
        return failedStatusKey.equals(key());
    }    
    

    /**
     * @return <code>true</code> if this is the Waiting status
     */
    public boolean isInProgress()
    {
        return inProgressStatusKey.equals(key());
    } 


    
}

