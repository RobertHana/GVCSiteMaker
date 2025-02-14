// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.admin.website;

import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.databasetables.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * This page allows user to setup and create a data table package. Data table creation is a queued task.
 */
public class CreateDataTablePackagePage extends WOComponent 
{
    protected Website website;
    protected SiteFileFolder packageDestination;
    protected SMVirtualTable mainTable;
    
    public SiteFileFolder aSiteFileFolder;
    public SMVirtualTable aDatabaseTable; 
    public SMVirtualTable aTable; 
    public Section aRelatedSection;     
    
    protected NSArray mainAndRelatedTables;
    protected NSArray relatedSections;
    
    protected NSMutableArray tablesToIncludeData;
    protected NSMutableArray sectionsToInclude;
    protected NSMutableArray requiredTablesToIncludeData;
    
    public SMVirtualTable previouslySelectedMainTable;     
    public boolean shouldDefaultToIncludeAllTableData;
    public boolean shouldDefaultToIncludeAllSections;
    public boolean shouldValidate;

    public WOComponent previousPage;

    
    /**
     * Designated constructor
     */
    public CreateDataTablePackagePage(WOContext context) 
    {
        super(context);
        
        packageDestination = null;
        mainTable = null;
        previouslySelectedMainTable = null;
        
        mainAndRelatedTables = new NSArray();
        relatedSections = new NSArray();
        
        tablesToIncludeData = new NSMutableArray();
        sectionsToInclude = new NSMutableArray();
        requiredTablesToIncludeData = new NSMutableArray();
        
        shouldDefaultToIncludeAllTableData = true;
        shouldDefaultToIncludeAllSections = true;
        shouldValidate = false;
        
        previousPage = context.page();
    }

    
    
    /**
     * Revert changes and return the ManageWebsitePage.
     *
     * @return the ManageWebsitePage
     */
    public WOComponent returnToManageWebsitePage()
    {
        session().defaultEditingContext().revert();

        ManageWebsitePage nextPage = (ManageWebsitePage)pageWithName("ManageWebsitePage");
        nextPage.setWebsite(website());
        
        return nextPage;
        
        /** ensure [result_not_null] Result != null; **/        
    }
    
    
    
    /**
     * Returns true if user has selected a Main table for export, false otherwise
     *
     * @return true if user has selected a Main table for export, false otherwise
     */
    public boolean hasSelectedMainTable()
    {
        return mainTable() != null;
    }
    
    
    
    /**
     * Returns true if there are related sections to display
     *
     * @return true if there are related sections to display
     */
    public boolean hasRelatedSections()
    {
        return relatedSections().count() > 0;
    }    
    
    
    
    
    /**
     * Returns list of tables containing the mainTable as the first on the list followed by its related tables.
     *
     * @return list of tables containing the mainTable as the first on the list followed by its related tables.
     */
    public NSArray mainAndRelatedTables()
    {
        if (hasSelectedMainTable() && ((previouslySelectedMainTable == null) || (! previouslySelectedMainTable.equals(mainTable()))))
        {
            NSMutableArray tablesToDisplay = new NSMutableArray();
            tablesToDisplay.addObject(mainTable());
            tablesToDisplay.addObjectsFromArray(mainTable().lookupSourceTables());

            mainAndRelatedTables = tablesToDisplay.immutableClone();
           
            shouldDefaultToIncludeAllTableData = true;
        }
        
        return mainAndRelatedTables;
        
        /** ensure [result_not_null] Result != null; **/           
    }        
    
    
    
    /**
     * Returns list of tables containing all the sections that the main and related tables are used in.
     *
     * @return list of tables containing all the sections that the main and related tables are used in.
     */
    public NSArray relatedSections()
    {
        if (hasSelectedMainTable() && ((previouslySelectedMainTable == null) || (! previouslySelectedMainTable.equals(mainTable()))))
        {
            NSMutableSet relatedSectionsSet = new NSMutableSet();
            
            Enumeration tableEnumerator = mainAndRelatedTables().objectEnumerator();
            while (tableEnumerator.hasMoreElements())
            {
                SMVirtualTable table = (SMVirtualTable) tableEnumerator.nextElement();
                relatedSectionsSet.addObjectsFromArray(table.sectionsUsedIn());
            }

            relatedSections = relatedSectionsSet.allObjects();
            
            //this is only set here since we want to populate related sections after populating the tables
            previouslySelectedMainTable = mainTable();      
            
            shouldDefaultToIncludeAllSections = true;
        }
        
        return relatedSections;
        
        /** ensure [result_not_null] Result != null; **/           
    }         
   
    
    
    /**
     * Returns true if aTable in the list is the main table.
     *
     * @return true if aTable in the list is the main table.
     */
    public boolean isMainTable()
    {
        return aTable.equals(mainTable());
    }    

    
    
    /**
     * Returns <code>true</code> if aTable() is in the list of tables to include data.
     *
     * @return <code>true</code> if aTable() is in the list of tables to include data.
     */
    public boolean isDataIncluded()
    {
        return tablesToIncludeData().containsObject(aTable);
    }

   
    
    /**
     * Includes or excludes the currentColumn() from the list of selected columns.
     *
     * @param shouldIncludeColumn - <code>true</code> if the currentColumn() should be included in the list of selected columns, <code>false</code> if the currentColumn() should be excluded from the list of selected columns.
     */
    public void setIsDataIncluded(boolean shouldIncludeData)
    {
        if (shouldIncludeData && ! isDataIncluded())
        {
            tablesToIncludeData().addObject(aTable);
        }
        else if (isDataIncluded() && ! shouldIncludeData)
        {
            tablesToIncludeData().removeObject(aTable);
        }
        
        updateRequiredTables();
        
        /** ensure [table_included_correctly] (shouldIncludeData && isDataIncluded()) || ( ! shouldIncludeData && ! isDataIncluded()); **/
    }
          

        
    /**
     * Returns <code>true</code> if aTable() is in the list of tables to include data.
     *
     * @return <code>true</code> if aTable() is in the list of tables to include data.
     */
    public boolean isSectionIncluded()
    {
        return sectionsToInclude().containsObject(aRelatedSection);
    }

   
    
    /**
     * Includes or excludes the currentColumn() from the list of selected columns.
     *
     * @param shouldIncludeColumn - <code>true</code> if the currentColumn() should be included in the list of selected columns, <code>false</code> if the currentColumn() should be excluded from the list of selected columns.
     */
    public void setIsSectionIncluded(boolean shouldIncludeSection)
    {
        //This gets called when mainTable is selected from UI, since this is still unchecked, it tries to set all the sections to unchecked even if the default should be checked.  This is a work-around avoid being set to unchecked when new table is selected.
        if (! shouldDefaultToIncludeAllSections)
        {
            if (shouldIncludeSection && ! isSectionIncluded())
            {
                sectionsToInclude().addObject(aRelatedSection);
            }
            else if (isSectionIncluded() && ! shouldIncludeSection)
            {
                sectionsToInclude().removeObject(aRelatedSection);
            }
        }
    }      
    
    
    
    /**
     * Overriden to add work-around.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        //Work-around to prevent the relatedSections to be unchecked as defaults
        shouldDefaultToIncludeAllSections = false;
    }    
    
    
    
    /**
     * Add all tables to include data
     *
     * @return this page
     */    
    public WOComponent checkAllTables()
    {
        tablesToIncludeData().removeAllObjects();
        tablesToIncludeData().addObject(mainTable());
        updateRequiredTables();
       
        return context().page();
        
        /** ensure [result_not_null] Result != null; **/         
    }

    
    
    /**
     * Removes all tables to include data
     *
     * @return this page
     */    
    public WOComponent uncheckAllTables()
    {
        tablesToIncludeData().removeAllObjects();
        updateRequiredTables();        
        
        return context().page();
        
        /** ensure [result_not_null] Result != null; **/         
    }
    
    
    
    /**
     * Add all tables to include data
     *
     * @return this page
     */    
    public WOComponent checkAllSections()
    {
        sectionsToInclude().removeAllObjects();
        sectionsToInclude().addObjectsFromArray(relatedSections());
       
        return context().page();
        
        /** ensure [result_not_null] Result != null; **/         
    }

    
    
    /**
     * Removes all tables to include data
     *
     * @return this page
     */    
    public WOComponent uncheckAllSections()
    {
        sectionsToInclude().removeAllObjects();
       
        return context().page();
        
        /** ensure [result_not_null] Result != null; **/         
    }    
    
    
    
    /**
     * Returns true if current table data is required to be included in the package, false otherwise
     *
     * @return true if current table data is required to be included in the package, false otherwise
     */        
    public boolean isRequiredToIncludeData()
    {
        return (requiredTablesToIncludeData().containsObject(aTable) && ( ! isMainTable()));
    }

    
    
    /**
     * Updates the required tables based on the selected tablesToIncludeData()
     */    
    public void updateRequiredTables()
    {
        requiredTablesToIncludeData.removeAllObjects();
        NSMutableSet newRequireTables = new NSMutableSet();

        Enumeration tableEnumerator = tablesToIncludeData().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable table = (SMVirtualTable) tableEnumerator.nextElement();
            newRequireTables.addObjectsFromArray(table.lookupSourceTables());
        }      
        requiredTablesToIncludeData = new NSMutableArray(newRequireTables.mutableClone().allObjects());
    }
    
    
 
    /**
     * Returns true if the error message for missing package destination should be displayed, false otherwise
     * 
     * @return true if the error message for missing package destination should be displayed, false otherwise
     */        
    public boolean displayMissingPackageDestinationErrorMessage() 
    {
        return (shouldValidate && (packageDestination() == null));
    }

    

    /**
     * Returns true if the error message for missing main table should be displayed, false otherwise
     * 
     * @return true if the error message for missing main table should be displayed, false otherwise
     */        
    public boolean displayMissingMainTableErrorMessage() 
    {
        return (shouldValidate && (mainTable() == null));
    }    
    

   
    /**
     * Validates form requirements and adds a CreatePackageTask to the queue.
     *
     * @return the queued task Page
     */       
    public WOComponent performPackageCreation()
    {
        WOComponent nextPage = context().page();
        shouldValidate = true;
        
        if (hasNoErrors())
        {
            //Join all the tables the user added to include and the required ones
            NSMutableSet allTablesToIncludeData = new NSMutableSet(tablesToIncludeData());
            allTablesToIncludeData.addObjectsFromArray(requiredTablesToIncludeData());
            
            CreateDataTablePackageTask packageTask = CreateDataTablePackageTask.newCreateDataTablePackageTask(editingContext(), 
                    mainTable(), 
                    allTablesToIncludeData.allObjects(),
                    sectionsToInclude(),
                    packageDestination(),
                    currentAdmin());
            
            editingContext().saveChanges();
            
            WOComponent queuedTaskPage = pageWithName("QueuedTasksPage");
            ((QueuedTasksPage) queuedTaskPage).setPageToReturnTo(previousPage);
            ((QueuedTasksPage) queuedTaskPage).setNewRequest(packageTask);            
            
            nextPage = queuedTaskPage;
        }

        return nextPage;
        
        /** ensure [result_not_null] Result != null; **/          
    }
    
    
    
    /**
     * Returns true if page has no errors, false otherwise
     *
     * @return true if page has no errors, false otherwise
     */  
    public boolean hasNoErrors()
    {
        return ( ! (displayMissingPackageDestinationErrorMessage() || displayMissingMainTableErrorMessage()));
    }
   
    
    
    /**
     * Overridden to initialize validation messages
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);
        shouldValidate = false;
    }    

    
    
    /**
     * Returns the tables set to be included in the package.
     *
     * @return the tables set to be included in the package.
     */     
    public NSMutableArray tablesToIncludeData()
    {
        if (shouldDefaultToIncludeAllTableData)
        {
            tablesToIncludeData.removeAllObjects();
            tablesToIncludeData.addObject(mainTable());
            requiredTablesToIncludeData = new NSMutableArray(mainTable().lookupSourceTables());

            shouldDefaultToIncludeAllTableData = false;
        }
        return tablesToIncludeData;
        
        /** ensure [result_not_null] Result != null; **/         
    } 


   
    /**
     * Returns the Sections set to be included in the package.
     *
     * @return the Sections set to be included in the package.
     */      
    public NSMutableArray sectionsToInclude()
    {
        if (shouldDefaultToIncludeAllSections)
        {
            sectionsToInclude.removeAllObjects();
            sectionsToInclude.addObjectsFromArray(relatedSections());
        }

        return sectionsToInclude;        
        
        /** ensure [result_not_null] Result != null; **/         
    }     

    /****** Getter/Setter methods *****/
    
    public void setWebsite(Website aWebsite)
    {
        website = aWebsite;
        
    }
    
    
    public Website website()
    {
        return website;
    }    
    
    
    
    public void setPackageDestination(SiteFileFolder aPackageDestination)
    {
        packageDestination = aPackageDestination;
        
    }
    
    
    public SiteFileFolder packageDestination()
    {
        return packageDestination;
    } 
    
    
    
    public void setMainTable(SMVirtualTable aMainTable)
    {
        mainTable = aMainTable;
        
    }
    
    
    public SMVirtualTable mainTable()
    {
        return mainTable;
    } 

    
    
    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
        
        /** ensure [result_not_null] Result != null; **/ 
    }    
    
    
    public User currentAdmin() 
    {
        return ((SMSession)session()).currentUser();
        
        /** ensure [result_not_null] Result != null; **/ 
    }    
    
    
    
    public NSMutableArray requiredTablesToIncludeData()
    {
        return requiredTablesToIncludeData;
        
        /** ensure [result_not_null] Result != null; **/ 
    }
}