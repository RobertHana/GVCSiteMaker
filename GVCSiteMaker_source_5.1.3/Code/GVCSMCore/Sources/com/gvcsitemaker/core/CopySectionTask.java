// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import net.global_village.eofextensions.*;

import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * Subclass of Task which deals with duplicating a Section.
 */
public class CopySectionTask extends _CopySectionTask implements QueuableTask
{
    
    /**
     * Factory method to create new instances of CopySectionTask.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of CopySectionTask or a subclass.
     */
    public static CopySectionTask newCopySectionTask()
    {
        return (CopySectionTask) SMEOUtils.newInstanceOf("CopySectionTask");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Factory method to create new instances of CopySectionTask. 
     * 
     * @return a new instance of CopySectionTask or a subclass.
     */
    public static CopySectionTask newCopySectionTask(EOEditingContext ec, Section sectionToCopy, Section placeHolderSection, User taskOwner)
    {
        /** require [sectionToCopy_not_null] sectionToCopy != null; 
		   	     [placeHolderSection_not_null] placeHolderSection != null;          
		   	     [taskOwner_not_null] taskOwner != null;  
		   	     [ec_not_null] ec != null;           
		   **/     	
    	
    		CopySectionTask newTask = CopySectionTask.newCopySectionTask();
		ec.insertObject(newTask);

		newTask.setOwner((User) EOUtilities.localInstanceOfObject(ec, taskOwner));
		newTask.setSourceSection((Section) EOUtilities.localInstanceOfObject(ec, sectionToCopy));
		newTask.setRelatedSection(placeHolderSection);   
		newTask.setDetails("Site " + newTask.sourceSection().website().siteID() + ", create section " + newTask.relatedSection().name() + " as copy of section " + newTask.sourceSection().name() + ".");   

		return newTask;
		
        /** ensure [result_not_null] Result != null; **/
    }    
    
    
    
    /**
     * Overriden to supply default values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setTaskType("CopySection");
        setIsNavigable(new net.global_village.foundation.GVCBoolean(true));
    }    

    
  
    /**
     * Performs actual copying of Section.
     */    
    public boolean performTask()
    {
    		boolean isSuccessful = false;
    		try
    		{
    			DebugOut.println(3,"Start copying section: " + sourceSection().name());

                
    			//Create copy
             NSMutableDictionary sectionOnlyCopy = new NSMutableDictionary(Section.SECTION_ONLY_COPY_CONTEXT, EOCopying.COPY_CONTEXT);   
    			Section newSection = (Section) sourceSection().copy(sectionOnlyCopy);
    			newSection.setName(relatedSection().name());
             newSection.setIsNavigable(isNavigable());
    			newSection.setDetails(relatedSection().details()); 
             editingContext().saveChanges();
    	        DebugOut.println(3,"End copying task...");      

    			isSuccessful = true;    			
    		}
    		catch (Exception e)
    		{
    			isSuccessful = false;

    			DebugOut.println(1, "Errors encountered: " + e.getMessage()); 
    			DebugOut.printStackTrace(10, e);
    			editingContext().revert();      			
            
    			setFailureReason(e.getMessage());    
    			editingContext().saveChanges();                 
    		}
 		
    		return isSuccessful;
    }    
   
    
    
    /**
     * Deletes the relatedSection() which is the temporary Section (a CopySectionType) that holds initial data.
     */     
    public void deleteRelatedSection()
    {
        relatedSection().website().removeSection(relatedSection());
        setRelatedSection(null);
    }      
    
    
    /**
     * Overriden to delete related Section
     */        
    public void markTaskAsCancelled()
    {
    		deleteRelatedSection();	
    	
    		super.markTaskAsCancelled();
    }     
    
    
    
    /**
     * Overriden to delete related Section
     */     
    public void markTaskAsFailed()
    {
		deleteRelatedSection();	
    		super.markTaskAsFailed();
            
    		SendEmail.sendFailedTaskProcessingMessage(this); 
    }      
    
    
  
    /**
     * Overriden to delete related Section
     */     
    public void markTaskAsCompleted()
    {
		deleteRelatedSection();	  	
    		super.markTaskAsCompleted();

         SendEmail.sendSuccessfulTaskProcessingMessage(this); 
    }          
    
    
    
    /**
     * Returns true if this taks already has a duplicate in all the Tasks submitted in the system.  Uses isEqualToTask to evaluate if there is a duplicate.
     * 
     * @return true if this taks already has a duplicate in all the Tasks submitted in the system
     */     
    public boolean hasDuplicate()
    {
        NSArray allTasks = EOUtilities.objectsWithQualifierFormat(editingContext(), "CopySectionTask", 
                        "sourceSection = %@ and relatedSection.name = %@",
                        new NSArray(new Object[]{sourceSection(), relatedSection().name()}));
        
        if (allTasks.count() == 0)
        {
            return false;
        }
        
        if (allTasks.count() > 1)
        {
            return true;
        }
        
        // If there is exactly one object and it is not this object (i.e. this object has not been saved)
        // then it must be duplicate
        return ! allTasks.containsObject(this);
    }
    
    
    
    /**
     * Compares this Task with passed otherTask.  Returns true if otherTask has the same type, sourceSection(), and name as this task
     * 
     * @param otherTask the Task to compare to
     * @return true if otherTask has the same type, sourceSection(), and name as this task
     */    
    public boolean isEqualToTask(Task otherTask) 
    {
        if ((otherTask == null) || ! (this.getClass().isInstance(otherTask)))
        {
            return false;
        }
                        
        CopySectionTask copySectionTask = (CopySectionTask) otherTask;
        return sourceSection().equals(copySectionTask.sourceSection()) &&
               relatedSection().name().equals(copySectionTask.relatedSection().name());
    }     
 
}

