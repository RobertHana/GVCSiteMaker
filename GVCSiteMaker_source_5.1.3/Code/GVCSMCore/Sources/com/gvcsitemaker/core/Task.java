// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.foundation.NSComparator.*;

/**
 * Abstract superclass for all queued tasks. Knows the status, date, and owner of the task.  Knows how to sort itself and handles cancellation and deletion.
 * New subsclasses should implement QueuableTask in order for queueing to work.
 */
public class Task extends _Task implements QueuableTask
{

    public static final NSComparator TaskComparator = new TaskComparator();
    public static final String TRUE = "yes";
    public static final String FALSE = "no";    
    
    protected NSMutableDictionary cachedBindingDictionary = null;
    
    
    /**
     * Factory method to create new instances of Task.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Task or a subclass.
     */
    public static Task newTask()
    {
        return (Task) SMEOUtils.newInstanceOf("Task");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Compares by createdDate ascending with inProgress, then Waiting on top of the list
     *
     */ 
     static class TaskComparator extends NSComparator
     {

        /**
         * Compares Tasks by createdDate ascending with inProgress, then Waiting on top of the list
         * @throws ComparisonException 
         * 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            int result = NSComparator.OrderedSame;
            
            Task firstTask = (Task) firstObject;
            Task secondTask = (Task) secondObject;
            
            if (firstTask.isInProgress() && ( ! secondTask.isInProgress()))
            {
            		result = NSComparator.OrderedAscending;
            }
            else if (secondTask.isInProgress() && (! firstTask.isInProgress()))
            {
            		result = NSComparator.OrderedDescending;
            }
            else if (firstTask.isWaiting() && ( ! secondTask.isWaiting()))
            {
        			result = NSComparator.OrderedAscending;           	
            }
            else if (secondTask.isWaiting() && ( ! firstTask.isWaiting()))
            {
        			result = NSComparator.OrderedDescending;           	
            }            
            else
            {
            		result = NSComparator.DescendingTimestampComparator.compare(firstTask.createdDate(), secondTask.createdDate());
            }
            
            return result;
        }
     }
    
     
     
     /**
      * Returns the Tasks in the system from the last 7 says sorted according to Task.TaskComparator
      * 
      * @return Tasks in the system sorted according to Task.TaskComparator
      */		
 	public static NSArray orderedTasks(EOEditingContext ec)
 	{			
  		NSTimestamp lastWeek = Date.beginningOfDayInDefaultTimeZone(Date.dateByAddingDays(new NSTimestamp(), -7));
  		EOQualifier inLastWeek = EOQualifier.qualifierWithQualifierFormat("createdDate >= %@", new NSArray(lastWeek));
 		EOFetchSpecification fetchSpec = new EOFetchSpecification("Task", inLastWeek, null);
 		fetchSpec.setRefreshesRefetchedObjects(true);
 		fetchSpec.setPrefetchingRelationshipKeyPaths(new NSArray("status"));
 	     NSArray allTasks = ec.objectsWithFetchSpecification(fetchSpec);	
 			
        NSArray orderedTasks = new NSArray();
 	    try
 	    {
 	    		orderedTasks = allTasks.sortedArrayUsingComparator(Task.TaskComparator);
 	    }
 	    		catch (ComparisonException e)
 	    {
 	    		throw new ExceptionConverter(e);
 	    }			
 		
         return orderedTasks;
         
         /** ensure [result_not_null] Result != null; **/
 	}     
    
 	
    
    /**
     * Overidden to set defaults.
     * 
     * @see com.webobjects.eocontrol.EOEnterpriseObject#awakeFromInsertion(com.webobjects.eocontrol.EOEditingContext)
     */
    public void awakeFromInsertion(EOEditingContext ec) 
    {
        super.awakeFromInsertion(ec);
        setCreatedDate(new NSTimestamp());	
        setStatus(TaskStatus.waitingStatus(editingContext()));	
        
        /** ensure
             [created_date_set] createdDate() != null;
             [status_set] status() != null;
        **/
    }    
   
    
    
    /**
     * Returns true if this Task is the same as the otherTask.  Tasks are the same if their details are exactly the same (case-insensitive match), false otherwise
     * For subclasses, override this method to define a new equality test.
     * 
     * @return true if this Task is the same as the otherTask
     */
    public boolean isEqualToTask(Task otherTask) 
    {
        /** require 
             [otherTask_not_null] otherTask != null;
             [details_not_null] details() != null;
             [otherTask_details_not_null] otherTask.details() != null;
         	 **/    	
    	
        if (otherTask == null) 
        {
            return false;
        }
        
    		return details().equalsIgnoreCase(otherTask.details());
    }     
    
        
    
    /**
     * Returns true if this task is currently being processed, false otherwise
     * 
     * @return true if this task is currently being processed, false otherwise
     */
    public boolean isInProgress()
    {
    		return status().isInProgress();
    }
    
    
    
    /**
     * Returns true if this task is currently waiting to be processed, false otherwise
     * 
     * @return true if this task is currently waiting to be processed, false otherwise
     */
    public boolean isWaiting()
    {
    		return status().isWaiting();
    }    
 
    
    
    /**
     * Returns true if this task is either inProgress or Waiting, false otherwise
     * 
     * @return true if this task is either inProgress or Waiting, false otherwise
     */
    public boolean isActive()
    {
    		return isInProgress() || isWaiting();
    }      
    
    
    
    /**
     * Marks the status of this Task with the passed aStatus, commits the changes to the database right away.
     * 
     * @param aStatus the Status to set
     */    
    public void markTask(TaskStatus aStatus)
    {
        /** require [aStatus_not_null] aStatus != null; **/  
    	
    		setStatus(aStatus);
    		editingContext().saveChanges();
    		
        /** ensure [status_is_set] status().equals(aStatus); **/  
    }    
    
    
    
    /**
     * Marks a Task as completed.
     */     
    public void markTaskAsCompleted()
    {
    		markTask(TaskStatus.completedStatus(editingContext()));
    		
        /** ensure [status_is_set] status().equals(TaskStatus.completedStatus(editingContext())); **/     		
    }
    
    
    
    /**
     * Marks a Task as failed.
     */      
    public void markTaskAsFailed()
    {
    		markTask(TaskStatus.failedStatus(editingContext()));
    		
    		/** ensure [status_is_set] status().equals(TaskStatus.failedStatus(editingContext())); **/     		
    }    
    
    
    
    /**
     * Marks a Task as in progress.
     */      
    public void markTaskAsInProgress()
    {
    		markTask(TaskStatus.inProgressStatus(editingContext()));

    		/** ensure [status_is_set] status().equals(TaskStatus.inProgressStatus(editingContext())); **/     		
    		
    }      
   
    
    
    /**
     * Marks a Task as cancelled.
     */      
    public void markTaskAsCancelled()
    {
    		markTask(TaskStatus.cancelledStatus(editingContext()));
    		
    		/** ensure [status_is_set] status().equals(TaskStatus.cancelledStatus(editingContext())); **/     		
    }    
    
    
    
    /**
     * Marks a Task as waiting.
     */      
    public void markTaskAsWaiting()
    {
            markTask(TaskStatus.waitingStatus(editingContext()));
            
            /** ensure [status_is_set] status().equals(TaskStatus.waitingStatus(editingContext())); **/           
    }        
    
    
    
    /**
     * Returns true if there is a duplicate of this task.  The implementation in Task throws an 
     * exception and must be implemented in subclasses.
     * 
     * @return true if there is a duplicate of this task 
     */     
    public boolean hasDuplicate()
    {
        throw new RuntimeException(getClass().getName() + " must implement public boolean hasDuplicate()");
    }




    /**
     * The task to be peformed. For subclasses, override this method to specify specific tasks to be performed.  Returns true if Task is successfully performed, false otherwise.
     * 
     * @return true if Task is successfully performed, false otherwise
     */    
    public boolean performTask()
    {
    		return true;
            
    }
    
    
    /** binding methods ****/



    /**
     * Returns bindings(), which are a String, as a dictionary.  Returns an empty dictionary if bindings() is null.  See setBindings(NSDictionary) for the inverse method.
     *
     * @return bindings(), which are a String, as a dictionary.
     */
    public NSMutableDictionary bindingDictionary()
    {
        // Memory and performance optimization.  Need to refresh if this object is
        // a fault as it may have been refaulted.  If this is the first method called
        // awakeFromFetch() will not have been called yet
        if (isFault() || (cachedBindingDictionary == null))
        {
            NSDictionary deserializedDictionary = (NSDictionary)NSPropertyListSerialization.propertyListFromString(bindings());

            if (deserializedDictionary == null)
            {
                cachedBindingDictionary = new NSMutableDictionary();
            }
            else
            {
                cachedBindingDictionary = new NSMutableDictionary(deserializedDictionary);
            }
        }

        return cachedBindingDictionary;

        /** ensure [result_not_null] Result != null; **/
    }    
    
    
    
    /**
     * Returns the value for the named binding.  If the name is not bound to a value, or if it is bound to an empty string, null is returned.
     *
     * @param bindingKey - the name of a binding to return a value for.
     * @return the value for the named binding or null if there is no value
     */
    public Object valueForBindingKey(String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        Object valueForBindingKey = null;

        valueForBindingKey = bindingDictionary().objectForKey(bindingKey);

        // Treat empty strings an null
        if ((valueForBindingKey != null) &&
            (valueForBindingKey instanceof String) &&
            (((String)valueForBindingKey).length() == 0))
        {
            valueForBindingKey = null;
        }

        return valueForBindingKey;
    }


    
    /**
     * If <code>bindingValue</code> is not null, sets the binding named <code>bindingKey</code> to <code>bindingValue</code> replacing any previously set value.  If <code>bindingValue</code> is null, removes the binding named <code>bindingKey</code> from bindings().
     *
     * @param bindingValue - the value to set for the binding named <code>bindingKey</code>, or null if that binding is to be removed.
     * @param bindingKey - the name of the binding to be set, updated, or removed.
     */
    public void setBindingForKey(Object bindingValue, String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        if (bindingKey != null)
        {
            // Parse the bindings into a Dictionary
            //DebugOut.println(22,"********** bindings are: "  + bindings());
            DebugOut.println(22,"Setting binding: "  + bindingValue + " for key: "  + bindingKey);

            NSMutableDictionary theBindings = bindingDictionary();

            if (bindingValue != null)
            {
                theBindings.setObjectForKey(bindingValue, bindingKey);
            }
            else
            {
                theBindings.removeObjectForKey(bindingKey);
            }
            setBindings(theBindings);
        }

        /** ensure [correct_action_performed] (((bindingValue == null) && ! hasValueForBindingKey(bindingKey)) || (valueForBindingKey(bindingKey).equals(bindingValue))); **/
    }    
    
    
    /**
     * Returns <code>true</code> if valueForBindingKey(aKey) would return a non null value.
     *
     * @param bindingKey - the name of a binding to return a value for.
     * @return <code>true</code> if valueForBindingKey(aKey) would return a non null value.
     */
    public boolean hasValueForBindingKey(String bindingKey)
    {
        /** require [binding_key_not_null] bindingKey != null; **/
        
        return valueForBindingKey(bindingKey) != null;

        /** ensure [result_correct] (Result == (valueForBindingKey(bindingKey) != null)); **/
    }

    
    /**
     * Overriden to clear cached values.
     */
    public void setBindings(String aValue)
    {
        // Just clear this specific cached value instead of calling clearCachedValues() so that cached values in sub-classes are not reset unneccessarily
        cachedBindingDictionary = null;
        super.setBindings(aValue);
    }


    
    /**
     * Sets bindings(), which are a String, from the contents of a dictionary.  This is an overloaded version of setBindings(String).  See bindingDictionary() for the inverse method.
     *
     * @param aDictionary - the dictionary whose contents are to be stored as bindings().
     */
    protected void setBindings(NSDictionary aDictionary)
    {
        /** require [a_dictionary_not_null] aDictionary != null; **/

        setBindings(NSPropertyListSerialization.stringFromPropertyList(aDictionary));

        /** ensure [successfully_set_bindings] bindingDictionary().equals(aDictionary); **/
    }    
    
    
    
    /**
     * Returns <code>true</code> if 'bindingKey' is bound to the value TRUE.
     *
     * @param bindingKey - the key to return a boolean value from the bindings for.
     * @return <code>true</code> if 'bindingKey' is bound to the value TRUE.
     */
    public boolean booleanValueForBindingKey(String bindingKey)
    {
        /** require [has_bindingKey] bindingKey != null;  [has_binding] hasValueForBindingKey(bindingKey); **/
        
        String bindingValue = (String) valueForBindingKey(bindingKey);
        return bindingValue.equalsIgnoreCase(TRUE);
    }    
    
    
    
    /**
     * Sets the binding for 'bindingKey' to the string representation of booleanValue.
     *
     * @param booleanValue - the boolean value to set for bindingKey
     * @param bindingKey - the key to set a boolean value in the bindings for.
     */
    public void setBooleanValueForBindingKey(boolean booleanValue, String bindingKey)
    {
        /** require [has_bindingKey] bindingKey != null; **/
        setBindingForKey(booleanValue ? TRUE : FALSE, bindingKey);
        /** ensure [has_binding] hasValueForBindingKey(bindingKey); **/
    }    
}

