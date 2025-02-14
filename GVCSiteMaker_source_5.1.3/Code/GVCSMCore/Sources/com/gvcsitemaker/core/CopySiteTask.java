// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * Subclass of Task which deals with copying a Website.
 */
public class CopySiteTask extends _CopySiteTask
{
    static public final String SiteIDBindingKey = "siteID";
    static public final String OwnerIDBindingKey = "ownerID";
    static public final String QuotaBindingKey = "quota";
    static public final String RedirectUrlBindingKey = "redirectUrl";
    static public final String ExampleSiteIDBindingKey = "exampleSiteID";
    static public final String SiteSubjectBindingKey = "siteSubject";
    static public final String SiteTemplateBindingKey = "siteTemplate";
    static public final String SkipMessageBindingKey = "skipMessage";
    static public final String AdminNoteBindingKey = "adminNote";    
    

    /**
     * Factory method to create new instances of CopySiteTask.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of CopySiteTask or a subclass.
     */
    public static CopySiteTask newCopySiteTask()
    {
        return (CopySiteTask) SMEOUtils.newInstanceOf("CopySiteTask");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Factory method to create new instances of CopySiteTask. 
     * 
     * @return a new instance of CopySiteTask or a subclass.
     */
    public static CopySiteTask newCopySiteTask(OrgUnit associatedUnit,
                                                String siteID,
                                                String ownerID,
                                                Number quota,
                                                String redirectUrl,
                                                EOEditingContext ec,
                                                User adminUser,
                                                String exampleSiteID,
                                                String newSiteSubject,
                                                String newSiteTemplate,
                                                boolean skipMessage,
                                                WebsiteRequest request,
                                                String adminNote)
    {
        /** require
        [valid_associatedUnit] associatedUnit != null;
        [valid_editingContext] ec != null;
        [valid_example_site_id]  exampleSiteID != null;
        [valid_adminUser] adminUser != null;
        [all_info_valid] Website.validateWebsiteCreationBasedOnExampleSite(associatedUnit, siteID, ownerID, quota, redirectUrl, ec, adminUser, exampleSiteID ) == null;
        **/  
        
        CopySiteTask newTask = CopySiteTask.newCopySiteTask();
        ec.insertObject(newTask);

        newTask.setRelatedOrgUnit((OrgUnit) EOUtilities.localInstanceOfObject(ec, associatedUnit));
        newTask.setSiteID(siteID);        
        newTask.setOwnerID(ownerID);        
        newTask.setQuota(quota.toString());        
        newTask.setRedirectUrl(redirectUrl);        
        newTask.setExampleSiteID(exampleSiteID); 
        newTask.setSiteSubject(newSiteSubject);
        newTask.setSiteTemplate(newSiteTemplate);
        newTask.setShouldSkipMessageBindingKey(skipMessage);
        newTask.setOwner((User) EOUtilities.localInstanceOfObject(ec, adminUser));
        if (request != null)
        {
            newTask.setRelatedRequest((WebsiteRequest) EOUtilities.localInstanceOfObject(ec, request));
        }
        newTask.setAdminNote(adminNote); 
        newTask.setDetails("Create new site " + newTask.siteID() + " based on example site " + newTask.exampleSiteID() + ".");   

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
        setTaskType("CopySite");
    }    

    
  
    /**
     * Performs actual copying of Section.
     */    
    public boolean performTask()
    {
            boolean isSuccessful = false;
            try
            {
                DebugOut.println(3,"Start copying site: " + siteID());
                
                //Create site copy
                Integer quotaAsInt = null;
                if (quota() != null)
                {
                    quotaAsInt = new Integer(quota());
                }                
                //verify again if values are valid, in case, there are changes to the site (like siteID not unique) before this task is run
                NSDictionary errorMessages = Website.validateWebsiteCreationBasedOnExampleSite(relatedOrgUnit(),  siteID(), ownerID(), quotaAsInt, redirectUrl(),
                        editingContext(), owner(), exampleSiteID());

                if (errorMessages != null)
                {
                    throw new Exception(errorMessages.allValues().componentsJoinedByString(", "));
                }
                
                Website.createWebsiteBasedOnExampleSite(relatedOrgUnit(),  siteID(), ownerID(), quotaAsInt, redirectUrl(),
                        editingContext(), owner(), exampleSiteID(),
                        siteSubject(), siteTemplate(), shouldSkipMessage());
                
                editingContext().saveChanges();
                
                // Damnation!  Create method don't return the website!
                Website createdSite = Website.websiteForSiteID(siteID(), editingContext());
                
                // Log site creation
                createdSite.logSiteCreation(owner(), 
                                            adminNote(),
                                            relatedRequest(),
                                            exampleSiteID());
                // If this site was created as the result of a Website Request, then mark it granted.                            
                if (relatedRequest() != null)
                {
                    relatedRequest().grant(createdSite);
                }
                
                editingContext().saveChanges();
                
                DebugOut.println(3,"End copying site task...");      

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
     * Overriden to delete related Section
     */        
    public void markTaskAsCancelled()
    {
        super.markTaskAsCancelled();
    }     
    
    
    
    /**
     * Overriden to delete related Section
     */     
    public void markTaskAsFailed()
    {
        super.markTaskAsFailed();
            
        SendEmail.sendFailedTaskProcessingMessage(this); 
    }      
    
    
  
    /**
     * Overriden to delete related Section
     */     
    public void markTaskAsCompleted()
    {
        super.markTaskAsCompleted();

        SendEmail.sendSuccessfulTaskProcessingMessage(this); 
    }          
    
    
    
    //************** Binding Cover Methods **************\\
    public String siteID() 
    {
        return (String) valueForBindingKey(SiteIDBindingKey);
    }
    
    public void setSiteID(String newBinding) 
    {
        setBindingForKey(newBinding, SiteIDBindingKey);
    }      
    
    public String ownerID() 
    {
        return (String) valueForBindingKey(OwnerIDBindingKey);
    }
    
    public void setOwnerID(String newBinding) 
    {
        setBindingForKey(newBinding, OwnerIDBindingKey);
    }      
    
    public String quota() 
    {
        return (String) valueForBindingKey(QuotaBindingKey);
    }
    
    public void setQuota(String newBinding) 
    {
        setBindingForKey(newBinding, QuotaBindingKey);
    }  
    
    public String redirectUrl() 
    {
        return (String) valueForBindingKey(RedirectUrlBindingKey);
    }
    
    public void setRedirectUrl(String newBinding) 
    {
        setBindingForKey(newBinding, RedirectUrlBindingKey);
    }  
    
    public String exampleSiteID() 
    {
        return (String) valueForBindingKey(ExampleSiteIDBindingKey);
    }
    
    public void setExampleSiteID(String newBinding) 
    {
        setBindingForKey(newBinding, ExampleSiteIDBindingKey);
    }      
    
    public String siteSubject() 
    {
        return (String) valueForBindingKey(SiteSubjectBindingKey);
    }
    
    public void setSiteSubject(String newBinding) 
    {
        setBindingForKey(newBinding, SiteSubjectBindingKey);
    }      
    
    public String siteTemplate() 
    {
        return (String) valueForBindingKey(SiteTemplateBindingKey);
    }
    
    public void setSiteTemplate(String newBinding) 
    {
        setBindingForKey(newBinding, SiteTemplateBindingKey);
    }          
   
    public boolean shouldSkipMessage()
    {
        return booleanValueForBindingKey(SkipMessageBindingKey);
    }

    public void setShouldSkipMessageBindingKey(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, SkipMessageBindingKey);
    }   
   
    public String adminNote() 
    {
        return (String) valueForBindingKey(AdminNoteBindingKey);
    }
    
    public void setAdminNote(String newBinding) 
    {
        setBindingForKey(newBinding, AdminNoteBindingKey);
    }       
}

