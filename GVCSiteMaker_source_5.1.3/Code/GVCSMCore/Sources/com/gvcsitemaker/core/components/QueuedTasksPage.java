// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 
package com.gvcsitemaker.core.components;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

/**
 * Page for viewing and managing queued Tasks.  Set pageToReturnTo() to set the page it will display after clicking Done at the top of the page.
 */
public class QueuedTasksPage extends WOComponent
{
	public Task aTask;
	protected Task newRequest;
	protected NSArray tasks;
    protected WOComponent pageToReturnTo;		
	
    
    /**
     * Designated constructor.
     */  
    public QueuedTasksPage(WOContext context) 
    {
        super(context);
        
        //Initialize task list
        tasks = Task.orderedTasks(session().defaultEditingContext());
        //Return this page if not set
        pageToReturnTo = null;
    }

    
    
    /**
     * Overridden to check if the user is authenticated and redirect them to the login page if not.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        SMSession session = ((SMSession)session());
        User currentUser = session.currentUser();
        if (((SMSession)session()).isUserAuthenticated())
        {
            if (currentUser.isUnitAdmin() || currentUser.isSystemAdmin())
            {
                super.appendToResponse(response, context);
                DebugOut.println(3,"User can view Queued Tasks.");
            }
            else
            {
                ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
                errorPage.setMessage("Access Refused.");
                errorPage.setReason("This area is for unit and system administrators only.");
                response.setContent(errorPage.generateResponse().content());
            }
        }
        else
        {
            SMAuthComponent.redirectToLogin(response, context, null);
        }
    }    
    
    
    
    /**
     * Returns all Tasks to be displayed in the page
     * 
     * @return the Tasks
     */   
    public NSArray tasks()
    {
    		return tasks;
    		
        /** ensure [result_not_null] Result != null; **/    		
    }

    
  
    /**
     * Returns true if aTask can be deleted.  A Task can be deleted if it is active and the logged in user is a System Administrator or the owner of the Task. 
     *
     * @return true if aTask can be deleted.
     */    
    public boolean canBeDeleted()
    {
    		User localOwner = (User) EOUtilities.localInstanceOfObject(((SMSession)session()).currentUser().editingContext(), aTask.owner());
    		boolean isTaskOwner = ((SMSession)session()).currentUser().equals(localOwner);
    		
        return aTask.isActive() && (((SMSession)session()).currentUser().isSystemAdmin() || isTaskOwner);
    }
    
    
    
    /**
     * Deletes the selected Task and refresh the Task list.  Returns this page.
     * 
     * @return this pages
     */        
    public WOComponent deleteTask()
    {
    		session().defaultEditingContext().deleteObject(aTask);
    		session().defaultEditingContext().saveChanges();    
    		
        tasks = Task.orderedTasks(session().defaultEditingContext());
    		
    		return context().page();
    }
    
    
    
    /**
     * Returns true if aTask can be cancelled.  A Task can be cancelled if it is still waiting and the logged in user is a System Administrator or the owner of the Task. 
     *
     * @return true if aTask can be cancelled.
     */        
    public boolean canBeCancelled()
    {
		User localOwner = (User) EOUtilities.localInstanceOfObject(((SMSession)session()).currentUser().editingContext(), aTask.owner());
		boolean isTaskOwner = ((SMSession)session()).currentUser().equals(localOwner);

		return aTask.isWaiting() && (((SMSession)session()).currentUser().isSystemAdmin() || isTaskOwner);
    }
    
    
    
    
    /**
     * Cancels the selected Task and refresh the Task list.  Returns this page.
     * 
     * @return this pages
     */     
    public WOComponent cancelTask()
    {
    		aTask.markTaskAsCancelled();
    		
        tasks = Task.orderedTasks(session().defaultEditingContext());
    		
    		return context().page();
    }    
    
    
    
    /**
     * Returns the next page to display.
     * 
     * @return the next page to display.
     */  
    public WOComponent showNextPage()
    {
    		return pageToReturnTo();
    }    
    
    
    
    /**
     * Returns the page title
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Queued Tasks";
        
        /** ensure [result_not_null] Result != null; **/        
    }        
    
    

    /**
     * Returns true if this page has a new Task added to the queue.
     */    
    public boolean hasNewRequest()
    {
    		return newRequest() != null;
    }    
    
    
    
    /* ********** Generic setters and getters ************** */
    public Task newRequest() {
    		return newRequest;
    }

    public void setNewRequest(Task aNewRequest) {
    		newRequest = aNewRequest;
    }     
    
    public WOComponent pageToReturnTo() {
        return pageToReturnTo;
    }

    public void setPageToReturnTo(WOComponent value) {
        pageToReturnTo = value;
    }      
}