// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;

import java.util.*;

import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.datatablepackage.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Subclass of Task which deals with creating a data table package.
 */
public class CreateDataTablePackageTask extends _CreateDataTablePackageTask
{


    /**
     * Factory method to create new instances of CreateDataTablePackageTask.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of CreateDataTablePackageTask or a subclass.
     */
    public static CreateDataTablePackageTask newCreateDataTablePackageTask()
    {
        return (CreateDataTablePackageTask) SMEOUtils.newInstanceOf("CreateDataTablePackageTask");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Factory method to create new instances of CreateDataTablePackageTask. 
     * 
     * @return a new instance of CreateDataTablePackageTask or a subclass.
     */
    public static CreateDataTablePackageTask newCreateDataTablePackageTask(EOEditingContext ec, 
                                                                            SMVirtualTable mainTable, 
                                                                            NSArray tablesToIncludeData,
                                                                            NSArray sectionsToInclude,
                                                                            SiteFileFolder packageDestination,
                                                                            User taskOwner)
    {
        /** require 
                 [ec_not_null] ec != null;   
                 [mainTable_not_null] mainTable != null; 
                 [tablesToIncludeData_not_null] tablesToIncludeData != null;   
                 [sectionsToInclude_not_null] sectionsToInclude != null;          
                 [packageDestination_not_null] packageDestination != null;  
                 [taskOwner_not_null] taskOwner != null;  
           **/      
        
        CreateDataTablePackageTask newTask = CreateDataTablePackageTask.newCreateDataTablePackageTask();
        ec.insertObject(newTask);

        newTask.setOwner((User) EOUtilities.localInstanceOfObject(ec, taskOwner));
        newTask.setRelatedTable((SMVirtualTable) EOUtilities.localInstanceOfObject(ec, mainTable));
        
        Enumeration tableEnumerator = tablesToIncludeData.objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable aTable = (SMVirtualTable) tableEnumerator.nextElement();
            newTask.addObjectToBothSidesOfRelationshipWithKey(EOUtilities.localInstanceOfObject(ec, aTable), "tables");
        }

        Enumeration sectionEnumerator = sectionsToInclude.objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            Section aSection = (Section) sectionEnumerator.nextElement();
            newTask.addObjectToBothSidesOfRelationshipWithKey(EOUtilities.localInstanceOfObject(ec, aSection), "sections");
        }
        
        newTask.setRelatedSiteFileFolder((SiteFileFolder) EOUtilities.localInstanceOfObject(ec, packageDestination));

        newTask.setDetails("Site " + mainTable.website().siteID() + ", create data table package from main table " + newTask.relatedTable().name());   

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
        setTaskType("CreateDataTablePackage");
    }    

    
  
    /**
     * Performs actual creation of package
     */    
    public boolean performTask()
    {
        boolean isSuccessful = false;
        try
        {
            DebugOut.println(3,"Start creating package for main table " + relatedTable().name());

                
            //Create package
            String resultingXML = DataTablePackage.xmlPackage((SMVirtualTable) relatedTable(), 
                                                              tables(), 
                                                              sections(),   
                                                              editingContext());          
            
            NSData xmlAsData = new NSData(resultingXML , _NSUtilities.UTF8StringEncoding);
            
            DebugOut.println(3,"Resulting XML size: " + xmlAsData.length());
            
            SiteFile siteFile = SiteFile.newSiteFile();
            editingContext().insertObject(siteFile);
            
            siteFile.setUploadedFilename(DataTablePackage.uniqueFileName(((SMVirtualTable) relatedTable()).website(), relatedTable().name().toLowerCase()));
            siteFile.setMimeType("text/xml");
            siteFile.setFileSizeUsage(new Integer(xmlAsData.length()));  
            siteFile.addObjectToBothSidesOfRelationshipWithKey(relatedSiteFileFolder(), "folder");
            ((SMVirtualTable) relatedTable()).website().addObjectToBothSidesOfRelationshipWithKey(siteFile,"files");
            siteFile.setData(xmlAsData);            
            
            editingContext().saveChanges();
            DebugOut.println(3,"End creating package...");      

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
     * Removes references not needed after this task completes.
     */     
    public void cleanupReferences()
    {
        setSections(new NSMutableArray());
        setTables(new NSMutableArray());
        setRelatedSiteFileFolder(null);
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
     * Overridden to send message and cleanup
     */     
    public void markTaskAsFailed()
    {
        cleanupReferences();
        super.markTaskAsFailed();
        
        SendEmail.sendFailedTaskProcessingMessage(this); 
    }      
    
    
  
    /**
     * Overridden to send message and cleanup
     */     
    public void markTaskAsCompleted()
    {
        cleanupReferences();
        super.markTaskAsCompleted();

        SendEmail.sendSuccessfulTaskProcessingMessage(this); 
    }          
}

