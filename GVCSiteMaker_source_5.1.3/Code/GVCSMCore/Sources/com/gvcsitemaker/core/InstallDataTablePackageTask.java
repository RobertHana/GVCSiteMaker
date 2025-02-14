// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import java.util.*;

import net.global_village.foundation.GVCBoolean;

import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.datatablepackage.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Subclass of Task which deals with insalling a data table package.
 */
public class InstallDataTablePackageTask extends _InstallDataTablePackageTask
{
    static public final String TablesToInstallBindingKey = "tablesToInstall";    
    static public final String TablesToInstallDataBindingKey = "tablesToInstallData";
    static public final String SectionsToInstallBindingKey = "sectionToInstall";
    

    /**
     * Factory method to create new instances of InstallDataTablePackageTask.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of InstallDataTablePackageTask or a subclass.
     */
    public static InstallDataTablePackageTask newInstallDataTablePackageTask()
    {
        return (InstallDataTablePackageTask) SMEOUtils.newInstanceOf("InstallDataTablePackageTask");

        /** ensure [result_not_null] Result != null; **/
    }
  


    /**
     * Factory method to create new instances of InstallDataTablePackageTask. 
     * 
     * @return a new instance of InstallDataTablePackageTask or a subclass.
     */
    public static InstallDataTablePackageTask newInstallDataTablePackageTask(EOEditingContext ec, 
                                                                             SiteFile xmlPackageFile, 
                                                                             NSArray theTablesToInstall,                                                                             
                                                                             NSArray theTablesToInstallData,
                                                                             NSArray theSectionsToInstall,
                                                                             SMVirtualTable theExistingTableToUse, 
                                                                             User taskOwner)
    {
        /** require 
        [ec_not_null] ec != null;   
        [xmlPackageFile_not_null] xmlPackageFile != null; 
        [theTablesToInstall_not_null] theTablesToInstall != null;   
        [theSectionsToInstall_not_null] theSectionsToInstall != null;          
        [taskOwner_not_null] taskOwner != null;  
        **/      

        InstallDataTablePackageTask newTask = InstallDataTablePackageTask.newInstallDataTablePackageTask();
        ec.insertObject(newTask);
        
        newTask.setOwner((User) EOUtilities.localInstanceOfObject(ec, taskOwner));
        newTask.setRelatedSiteFile((SiteFile) EOUtilities.localInstanceOfObject(ec, xmlPackageFile));
        newTask.setTablesToInstall(theTablesToInstall);        
        newTask.setTablesToInstallData(theTablesToInstallData);
        newTask.setSectionsToInstall((NSArray) theSectionsToInstall.valueForKeyPath("name"));
        
        if (theExistingTableToUse != null)
        {
            newTask.setRelatedTable((SMVirtualTable) EOUtilities.localInstanceOfObject(ec, theExistingTableToUse));
        }
        newTask.setDetails("Site " + newTask.relatedSiteFile().website().siteID() + ", install data table package: " + newTask.relatedSiteFile().uploadedFilename());   
        
        //Create placeholder sections for display in ConfigPage
        Enumeration sectionEnumerator = theSectionsToInstall.objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            SectionContainer aSectionContainer = (SectionContainer) sectionEnumerator.nextElement();
            Section newSection = Section.newSection();
            ec.insertObject(newSection);
            newSection.setName(aSectionContainer.name());
            newSection.setType(PackageSectionType.getInstance(ec));
            newSection.setIsNavigable(new GVCBoolean(false));
            xmlPackageFile.website().attachSection(newSection);
            newTask.addObjectToBothSidesOfRelationshipWithKey(newSection, "sections");
            newSection.type().createComponents(newSection);
        }    
        
        return newTask;

        /** ensure [result_not_null] Result != null; **/
    }     
    
 
    
    /**
     * Overriden to supply default values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setTaskType("InstallDataTablePackage");
        
        // Ensure that bindings contains the required binding COMPONENT_ORDER_BINDINGKEY
        if ( ! hasValueForBindingKey(TablesToInstallDataBindingKey))
        {
            setTablesToInstallData(NSArray.EmptyArray);
        }        
        if ( ! hasValueForBindingKey(SectionsToInstallBindingKey))
        {
            setSectionsToInstall(NSArray.EmptyArray);
        }    
        if ( ! hasValueForBindingKey(TablesToInstallBindingKey))
        {
            setTablesToInstall(NSArray.EmptyArray);
        }    
    }    

    
  
    /**
     * Performs actual installation of package
     */    
    public boolean performTask()
    {
        boolean isSuccessful = false;
        
        try
        {
            
            DebugOut.println(3,"Start installing package for main table" + relatedSiteFile().uploadedFilename());

            DataTablePackage newDataTablePackage = new DataTablePackage();
            newDataTablePackage.installPackage(relatedSiteFile().website(),
                                                relatedSiteFile(), 
                                                tablesToInstallData(),
                                                sectionsToInstall(),
                                                (SMVirtualTable) relatedTable(),
                                                owner(), 
                                                editingContext());
            
            editingContext().saveChanges();

            DebugOut.println(3,"End installing package...");      

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
     * Deletes the sections() which are temporary Section (a PackageSectionType) that holds initial data
     * and other references not needed after this task completes.
     */     
    public void cleanupReferences()
    {
        Enumeration sectionEnumerator = sections().objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            Section aSection = (Section) sectionEnumerator.nextElement();
            aSection.website().removeSection(aSection);
        }
        
        setSections(new NSMutableArray());
        
        setRelatedSiteFile(null);
        setRelatedTable(null);
    }      
    
    
    
    /**
     * Overridden to cleanup
     */        
    public void markTaskAsCancelled()
    {
        cleanupReferences(); 
        
        super.markTaskAsCancelled();
    } 
    
    
    
    /**
     * Overridden to send message
     */     
    public void markTaskAsFailed()
    {
        cleanupReferences();
        
        super.markTaskAsFailed();
        
        SendEmail.sendFailedTaskProcessingMessage(this); 
    }      
    
    
  
    /**
     * Overridden to send message
     */     
    public void markTaskAsCompleted()
    {
        cleanupReferences();
        
        super.markTaskAsCompleted();

        SendEmail.sendSuccessfulTaskProcessingMessage(this); 
    }

    
    //************** Binding Cover Methods **************\\

    public NSArray tablesToInstall() 
    {
        return (NSArray) valueForBindingKey(TablesToInstallBindingKey);
    }
    
    public void setTablesToInstall(NSArray newBinding) 
    {
        setBindingForKey(newBinding, TablesToInstallBindingKey);
    }
    
    public NSArray tablesToInstallData() 
    {
        return (NSArray) valueForBindingKey(TablesToInstallDataBindingKey);
    }
    
    public void setTablesToInstallData(NSArray newBinding) 
    {
        setBindingForKey(newBinding, TablesToInstallDataBindingKey);
    }    
    
    public NSArray sectionsToInstall() 
    {
        return (NSArray) valueForBindingKey(SectionsToInstallBindingKey);
    }
    
    public void setSectionsToInstall(NSArray newBinding) 
    {
        setBindingForKey(newBinding, SectionsToInstallBindingKey);
    }
}

