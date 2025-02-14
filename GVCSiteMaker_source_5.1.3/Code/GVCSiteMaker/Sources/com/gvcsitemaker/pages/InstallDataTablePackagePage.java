// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.pages;

import java.util.Enumeration;

import org.xml.sax.SAXParseException;

import com.gvcsitemaker.appserver.Session;
import com.gvcsitemaker.core.InstallDataTablePackageTask;
import com.gvcsitemaker.core.SiteFile;
import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.databasetables.SMVirtualTable;
import com.gvcsitemaker.core.datatablepackage.DataTablePackage;
import com.gvcsitemaker.core.datatablepackage.PackageContainer;
import com.gvcsitemaker.core.datatablepackage.SectionContainer;
import com.gvcsitemaker.core.datatablepackage.TableContainer;
import com.gvcsitemaker.core.support.SMActionURLFactory;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WORedirect;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;


/**
 * This page allows users to setup and install a data table package.
 */
public class InstallDataTablePackagePage extends com.gvcsitemaker.core.components.WebsiteContainerBase 
{
    public static final String easyInstallType = "Easy";
    public static final String customInstallType = "Custom";
    
    public SiteFile aPackage;
    protected SiteFile selectedPackage;
    protected PackageContainer packageContainer;
    public SiteFile previouslySelectedPackage;
    public TableContainer aTable;
    protected String installType; 
    protected boolean needToChangeInstallType;
    
    protected NSArray mainAndRelatedTables;
    protected NSArray relatedSections;

    public NSArray tableInstallOptions;
    
    protected NSMutableArray sectionsToInstall;
    public SectionContainer aRelatedSection;
    
    public boolean shouldValidate;

    public WOComponent previousPage;
    protected NSMutableDictionary tableInstallOptionsDictionary;
    protected NSMutableDictionary matchingExistingTablesDictionary;    
    
    protected NSMutableArray requiredTablesToIncludeData;
    
    protected String errors;
    
    protected NSDictionary existingTableMappings;

    
    /**
     * Designated constructor.
     */
    public InstallDataTablePackagePage(WOContext context) {
        super(context);

        mainAndRelatedTables = new NSArray();
        relatedSections = new NSArray();
        
        previouslySelectedPackage = null;
        
        installType = easyInstallType;
        needToChangeInstallType = false;
        
        packageContainer = null;
        
        sectionsToInstall= new NSMutableArray();
        
        shouldValidate = false;
        
        previousPage = context.page();
        
        tableInstallOptionsDictionary = new NSMutableDictionary();
        matchingExistingTablesDictionary = new NSMutableDictionary();
        
        requiredTablesToIncludeData = new NSMutableArray();
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        editingContext().revert();
        return pageWithName("ConfigTabSet");
    }



    /**
     * Returns the page title which varies whether the user can or cannot modify the file.
     * 
     * @return the page title which varies whether the user can or cannot modify the file.
     */
    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Install Data Access Package";
    }
 
    

    /**
     * Returns the corresponding PackageContainer based on the currently selected package.
     * 
     * @return the corresponding PackageContainer based on the currently selected package.
     */
    public PackageContainer packageContainer()
    {
        return packageContainer;
    }    

    
    
    /**
     * Sets the packageContainer and populates the list of related tables and sections based on the parsed package
     * 
     * @param container the PackageContainer to set
     */    
    public void setPackageContainer(PackageContainer container)
    {
        packageContainer = container;
        
        if (packageContainer != null)
        {
            NSMutableArray tablesToDisplay = new NSMutableArray();
            tablesToDisplay.addObject(packageContainer().mainTable());
            tablesToDisplay.addObjectsFromArray(packageContainer().tables());

            mainAndRelatedTables = tablesToDisplay.immutableClone();

            relatedSections = packageContainer().sections();
            
            sectionsToInstall.removeAllObjects();
            sectionsToInstall.addObjectsFromArray(relatedSections);
        }
    }    
    

    
    /**
     * Returns the mainTable in the recent PackageContainer
     * 
     * @param the mainTable in the recent PackageContainer
     */        
    public TableContainer mainTable()
    {
        /** require [packageContainer_not_null]  packageContainer() != null; **/        

        return packageContainer().mainTable();
    }       
    
    
    
    /**
     * Create a queued task for this package installation.
     * 
     * @return the QueuedTaskPage
     */       
    public WOComponent performPackageInstallation()
    {
        WOComponent nextPage = context().page();
        
        shouldValidate = true;
        
        if (hasNoErrors())
        {
            String mainTableInstallOption = (String) tableInstallOptionsDictionary().objectForKey(packageContainer().mainTable().name());
            SMVirtualTable useExistingTable = (SMVirtualTable) matchingExistingTablesDictionary.objectForKey(mainTableInstallOption); 

            NSMutableArray tablesToInstall = new NSMutableArray();
            
            if (useExistingTable == null)
            {
                tablesToInstall.addObjectsFromArray((NSArray) mainAndRelatedTables().valueForKeyPath("name"));
            }
            
            NSMutableArray tablesToInstallData = new NSMutableArray();
            
            Enumeration tableKeyEnumerator = tableInstallOptionsDictionary().keyEnumerator();
            while (tableKeyEnumerator.hasMoreElements())
            {
                String aTableNameKey = (String) tableKeyEnumerator.nextElement();
                String installOption = (String) tableInstallOptionsDictionary().objectForKey(aTableNameKey);
                if (installOption.equals(DataTablePackage.InstallTableIncludingDataOption) || installOption.equals(DataTablePackage.InstallMainTableIncludingDataOption))
                {
                    tablesToInstallData.addObject(aTableNameKey);
                }
            } 

            InstallDataTablePackageTask.newInstallDataTablePackageTask(editingContext(), 
                    selectedPackage(), 
                    tablesToInstall,
                    tablesToInstallData,
                    sectionsToInstall(),
                    useExistingTable,
                    ((Session)session()).currentUser());
            
            editingContext().saveChanges();

            //Redirect to ConfigPage instead of just returning pageWithName("ConfigPage") so that refreshing will redo this redirect instead of resubmitting the request.
            WORedirect configPageRedirect = new WORedirect(context());
            String secureConfigURL = SMActionURLFactory.configSiteURL(context().request(), website().siteID());
            configPageRedirect.setUrl(secureConfigURL);
            
            nextPage = configPageRedirect;
        }

        return nextPage;
        
        /** ensure [result_not_null] Result != null; **/        
    }
    
    
    
    /**
     * Returns the ConfigPage
     * 
     * @return the ConfigPage
     */     
    public WOComponent returnToConfigPage()
    {
        return pageWithName("ConfigTabSet");
    }
    
    

    /**
     * Returns true if there is a package selected
     * 
     * @return  true if there is a package selected
     */     
    public boolean hasSelectedPackage()
    {
        return selectedPackage() != null;
    }
    
    
    
    /**
     * Returns list of tables containing the mainTable as the first on the list followed by its related tables.
     *
     * @return list of tables containing the mainTable as the first on the list followed by its related tables.
     */
    public NSArray mainAndRelatedTables()
    {
        return mainAndRelatedTables;
        
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
     * Returns true if package contents can be displayed, false otherwise
     *
     * @return true if package contents can be displayed, false otherwise
     */    
    public boolean displayPackageContents()
    {
        return ((packageContainer() != null) && (installType() != null));
    }
    
    

    /**
     * Returns true if selected install type is "Easy" install.
     *
     * @return true if selected install type is "Easy" install.
     */    
    public boolean isEasyInstall()
    {
        return ((installType() != null) && installType().equals(easyInstallType));
    }
    
    
    
    /**
     * Sets installType based on value of flag
     */    
    public void setIsEasyInstall(boolean flag)
    {
        if (flag && ! isEasyInstall())
        {
            needToChangeInstallType = true;
            setInstallType(easyInstallType);
        }
    }

    
    
    /**
     * Returns true if selected install type is "Custom" install.
     *
     * @return true if selected install type is "Custom" install.
     */    
    public boolean isCustomInstall()
    {
        return ((installType() != null) && installType().equals(customInstallType));
    }    
    
    
    
    /**
     * Sets installType based on value of flag
     */     
    public void setIsCustomInstall(boolean flag)
    {
        if (flag && ! isCustomInstall())
        {
            needToChangeInstallType = true;
            setInstallType(customInstallType);
        }
    }
    
    
    
    /**
     * Overriden to delay setting of install type
     */    
    public void appendToResponse(WOResponse aResponse, WOContext aContext)    
    {
        // This changes the component graph on this page so it needs to be delayed until here 
        if (needToChangeInstallType)
        {
            needToChangeInstallType = false;
            if (isEasyInstall())
            {
                setupEasyInstall();
            }
            else
            {
                setupCustomInstall();
            }
        }

        super.appendToResponse(aResponse, aContext);
        previouslySelectedPackage = selectedPackage();
        
    }



    /**
     * Returns the selected package
     *
     * @return the selected package
     */     
    public SiteFile selectedPackage()
    {
        return selectedPackage;
    }

    

    /**
     * Sets the selected package with aValue
     *
     * @param aValue the new selected package
     */     
    public void setSelectedPackage(SiteFile aValue)
    {
        if (aValue != selectedPackage)
        {
            selectedPackage = aValue;
            if (aValue != null)
            {
                setupEasyInstall();
            }
        }
    }
    
    

    /**
     * Returns the install options of current aTable
     *
     * @return the install options of current aTable
     */  
    public NSArray tableInstallOptions()
    {
        return tableInstallOptions(aTable);
    }
    
    
    
    /**
     * Returns the install options available for passed theTable
     *
     * @param theTable the TableContainer to be evaluated
     * @return the install options available for passed theTable
     */     
    public NSArray tableInstallOptions(TableContainer theTable)
    {
        NSMutableArray installOptions = new NSMutableArray();

        if (theTable.hasRowData())
        {
            if (packageContainer().mainTable().equals(theTable))
            {
                installOptions.addObject(DataTablePackage.InstallMainTableIncludingDataOption);
                installOptions.addObject(DataTablePackage.InstallMainTableExcludingDataOption);            
            }
            else    
            {
                installOptions.addObject(DataTablePackage.InstallTableIncludingDataOption);
                installOptions.addObject(DataTablePackage.InstallTableExcludingDataOption);            
            }
        }
        else
        {
            installOptions.addObject(DataTablePackage.InstallTableOption);             
        }
        
        if (packageContainer().mainTable().equals(theTable))
        {
            matchingExistingTablesDictionary = new NSMutableDictionary();
            
            NSArray matchingTables = DataTablePackage.orderedMatchingTables(website(), theTable, packageContainer());
            Enumeration tableEnumerator = matchingTables.objectEnumerator();
            while (tableEnumerator.hasMoreElements())
            {
                SMVirtualTable currentTable = (SMVirtualTable) tableEnumerator.nextElement();
                String option = DataTablePackage.UseExistingtableOptionPrefix + currentTable.name();
                installOptions.addObject(option);
                matchingExistingTablesDictionary.setObjectForKey(currentTable, option);
            }
        }
        
        return installOptions;
    }    


    
    /**
     * Returns the option with the corresponding existing table of a related table.  This is only used if the main table's
     * install option is to use an existing  table.
     *
     * @return the option with the corresponding existing table of a related table.
     */    
    public String existingTableOption()
    {
        String mainTableInstallOption = (String) tableInstallOptionsDictionary().objectForKey(packageContainer().mainTable().name());
        SMVirtualTable useExistingTable = (SMVirtualTable) matchingExistingTablesDictionary.objectForKey(mainTableInstallOption); 
        setExistingTableMappings(DataTablePackage.existingTableMapping(useExistingTable, packageContainer().mainTable(), packageContainer()));
        
        SMVirtualTable matchingTable = (SMVirtualTable) existingTableMappings().objectForKey(aTable.name());
        
        return DataTablePackage.UseExistingtableOptionPrefix + matchingTable.name();
        
        /** ensure [result_not_null] Result != null; **/  
    }
    
    
    
    /**
     * Returns true if existing table option is to be displayed or not.  It should be displayed if the main table's install
     * option is to use an existing table.
     *
     * @return true if existing table option is to be displayed or not. 
     */      
    public boolean showExistingTableOption()
    {
        boolean showExistingTableOption = false;
        
        String mainTableInstallOption = (String) tableInstallOptionsDictionary().objectForKey(packageContainer().mainTable().name());
        if ((! isMainTable()) && (mainTableInstallOption != null) && mainTableInstallOption.startsWith(DataTablePackage.UseExistingtableOptionPrefix))
        {
            showExistingTableOption = true;
        }
        
        return showExistingTableOption;
    }
    
    
    
    /**
     * Returns true if multiple install options should be displayed, false otherwise
     *
     * @return true if multiple install options should be displayed, false otherwise
     */    
    public boolean showMultipleInstallOptions()
    {
        return ((! isRequiredToIncludeData()) && (! showExistingTableOption()));
    }
    
    
    
    /**
     * Add all tables to include data
     *
     * @return this page
     */    
    public WOComponent checkAllSections()
    {
        sectionsToInstall().removeAllObjects();
        sectionsToInstall().addObjectsFromArray(relatedSections());
       
        return context().page();
    }

    
    
    /**
     * Removes all tables to include data
     *
     * @return this page
     */    
    public WOComponent uncheckAllSections()
    {
        sectionsToInstall().removeAllObjects();
       
        return context().page();
    }    
    
    
    
    /**
     * Returns <code>true</code> if aTable() is in the list of tables to include data.
     *
     * @return <code>true</code> if aTable() is in the list of tables to include data.
     */
    public boolean isSectionIncluded()
    {
        return sectionsToInstall().containsObject(aRelatedSection);
    }

   
    
    /**
     * Includes or excludes the currentColumn() from the list of selected columns.
     *
     * @param shouldIncludeColumn - <code>true</code> if the currentColumn() should be included in the list of selected columns, <code>false</code> if the currentColumn() should be excluded from the list of selected columns.
     */
    public void setIsSectionIncluded(boolean shouldIncludeSection)
    {
        if (shouldIncludeSection && ! isSectionIncluded())
        {
            sectionsToInstall().addObject(aRelatedSection);
        }
        else if (isSectionIncluded() && ! shouldIncludeSection)
        {
            sectionsToInstall().removeObject(aRelatedSection);
        }

        /** ensure [table_included_correctly] (shouldIncludeSection && isSectionIncluded()) || ( ! shouldIncludeSection && ! isSectionIncluded()); **/
    }    
    
    
    
    /**
     * Returns list of tables containing all the sections that the main and related tables are used in.
     *
     * @return list of tables containing all the sections that the main and related tables are used in.
     */
    public NSArray relatedSections()
    {
        return relatedSections;
 
        /** ensure [result_not_null] Result != null; **/           
    }         

   
    
    
    /**
     * Returns true if page has no errors, false otherwise
     *
     * @return true if page has no errors, false otherwise
     */  
    public boolean hasNoErrors()
    {
       return (( ! displayMissingPackageToInstall()) && (! displayMissingInstallType()) && (! hasOtherErrors()));
    }
    
    
    
    /**
     * Returns the editingContext to be used for this page which is the Website's editingContext.
     *
     * @return the editingContext to be used for this page which is the Website's editingContext.
     */     
    public EOEditingContext editingContext()
    {
        return website().editingContext();
    }

    
    
    /**
     * Returns the currently selected option for aTable
     *
     * @return the currently selected option for aTable
     */     
    public String selectedInstallOption()
    {
        return (String) tableInstallOptionsDictionary.objectForKey(aTable.name());
    }
    
    
    
    /**
     * Sets selection as the new selected install option
     *
     * @param selection the new selection
     */     
    public void setSelectedInstallOption(String selection)
    {
        if (selection != null)
        {
            if (isMainTable())
            {
                NSMutableArray tablesToDisplay = new NSMutableArray();
                tablesToDisplay.addObject(packageContainer().mainTable());
                tablesToDisplay.addObjectsFromArray(packageContainer().tables());

                mainAndRelatedTables = tablesToDisplay.immutableClone();
                
                tableInstallOptionsDictionary = new NSMutableDictionary();
                
                Enumeration tableEnumerator = mainAndRelatedTables().objectEnumerator();
                while (tableEnumerator.hasMoreElements())
                {
                    TableContainer table = (TableContainer) tableEnumerator.nextElement();
                    tableInstallOptionsDictionary().setObjectForKey(tableInstallOptions(table).objectAtIndex(0), table.name()); 
                }            
            }
                
                relatedSections = packageContainer().sections();
                
                sectionsToInstall.removeAllObjects();
                sectionsToInstall.addObjectsFromArray(relatedSections);                
            
            tableInstallOptionsDictionary.setObjectForKey(selection, aTable.name());
            updateRequiredTables();
        }
    }
    
    

    /**
     * Returns true if the selected package is changed, false otherwise
     *
     * @return true if the selected package is changed, false otherwise
     */ 
    public boolean hasSelectedPackageChanged()
    {
        //start where previouslySelectedPackage is null and selectedPackage is not null
        boolean noPreviouslySelected = ((previouslySelectedPackage == null) && (selectedPackage() != null));

        //start where previouslySelectedPackage is null and selectedPackage is not null
        boolean unselectedPackage = ((previouslySelectedPackage != null) && (selectedPackage() == null));
        
        //both are not null but changed
        boolean packageChanged = ((previouslySelectedPackage != null) && (selectedPackage() != null) && ( ! previouslySelectedPackage.equals(selectedPackage())));
        
        return (noPreviouslySelected || unselectedPackage || packageChanged);
    }
    

    
    /**
     * Returns true if error message for missing package to install should be displayed, false otherwise.
     *
     * @return true if error message for missing package to install should be displayed, false otherwise.
     */     
    public boolean displayMissingPackageToInstall() 
    {
        return (shouldValidate && (selectedPackage() == null));
    }      
    
    
    
    /**
     * Returns true if error message for missing install type should be displayed, false otherwise.
     *
     * @return true if error message for missing install type should be displayed, false otherwise.
     */        
    public boolean displayMissingInstallType()
    {
        return (shouldValidate && (! displayMissingPackageToInstall()) && (installType() == null) && (! hasOtherErrors()));
    }
    
    
    
    /**
     * Overridden to initialize validation messages
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        errors = null;
        super.takeValuesFromRequest(aRequest, aContext);
        shouldValidate = false;
    }
    
    
    
    /**
     * Sets up the page and other options for Easy install.  Easy install basically installs everything in the package and cannot be modified by the User.
     */     
    public void setupEasyInstall()
    {
        shouldValidate = true;
        
        try
        {
            if (! displayMissingPackageToInstall())
            {
                setPackageContainer(DataTablePackage.parsePackage(selectedPackage()));
            }
            else
            {
                setPackageContainer(null);
            }
        } 
        catch (SAXParseException spe) 
        {
            // Error generated by the parser
            errors = "The file " + selectedPackage().uploadedFilename() + " does not contain a valid package.<br/>Error found on line " + spe.getLineNumber() +": " + WOMessage.stringByEscapingHTMLString(spe.getMessage()); 
            setSelectedPackage(null);
            setPackageContainer(null);
        } 
        catch (Throwable t) 
        {
            t.printStackTrace();
        } 
 
        if (hasNoErrors())
        {
            tableInstallOptionsDictionary = new NSMutableDictionary();
            
            Enumeration tableEnumerator = mainAndRelatedTables().objectEnumerator();
            while (tableEnumerator.hasMoreElements())
            {
                TableContainer table = (TableContainer) tableEnumerator.nextElement();
                
                tableInstallOptionsDictionary().setObjectForKey(tableInstallOptions(table).objectAtIndex(0), table.name()); 
            }
            
            updateRequiredTables();
            
            sectionsToInstall().removeAllObjects();
            sectionsToInstall().addObjectsFromArray(relatedSections());
        }
    }
    
    
    
    public void setupCustomInstall()
    {
        shouldValidate = true;

        try
        {
            if (! displayMissingPackageToInstall())
            {
                setPackageContainer(DataTablePackage.parsePackage(selectedPackage()));
            }
            else
            {
                setPackageContainer(null);
            }
        }
        catch (SAXParseException spe) 
        {
            // Error generated by the parser
            errors = "The file " + selectedPackage().uploadedFilename() + " does not contain a valid package.<br/>Error found on line " + spe.getLineNumber() +": " + WOMessage.stringByEscapingHTMLString(spe.getMessage()); 
            setSelectedPackage(null);
            setPackageContainer(null);
        } 
        catch (Throwable t) 
        {
            t.printStackTrace();
        } 

        if (hasNoErrors())
        {
            tableInstallOptionsDictionary = new NSMutableDictionary();
            
            Enumeration tableEnumerator = mainAndRelatedTables().objectEnumerator();
            while (tableEnumerator.hasMoreElements())
            {
                TableContainer table = (TableContainer) tableEnumerator.nextElement();
                tableInstallOptionsDictionary().setObjectForKey(tableInstallOptions(table).objectAtIndex(0), table.name()); 
            }
            updateRequiredTables();
         }
     }        
    
    

    /**
     * Returns true if errors() is not null
     * 
     * @return true if errors() is not null
     */    
    public boolean hasOtherErrors()
    {
        return (errors() != null); 
    }
    
    
    
    /**
     * Updates the required tables based on the selected tables to include data
     */    
    public void updateRequiredTables()
    {
        requiredTablesToIncludeData.removeAllObjects();
        NSMutableSet newRequireTables = new NSMutableSet();

        Enumeration tableEnumerator = mainAndRelatedTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            TableContainer table = (TableContainer) tableEnumerator.nextElement();
            
            if ((tableInstallOptionsDictionary.objectForKey(table.name()) != null) && ((tableInstallOptionsDictionary.objectForKey(table.name()).equals(DataTablePackage.InstallMainTableIncludingDataOption)) ||
            (tableInstallOptionsDictionary.objectForKey(table.name()).equals(DataTablePackage.InstallTableIncludingDataOption))))
            {
                newRequireTables.addObjectsFromArray(table.lookupSourceTables());
                Enumeration lookupTablesEnumerator = table.lookupSourceTables().objectEnumerator();
                while (lookupTablesEnumerator.hasMoreElements())
                {
                    String aTableName = (String) lookupTablesEnumerator.nextElement();
                    TableContainer aTableContainer = packageContainer().tableContainerWithName(aTableName);
                    tableInstallOptionsDictionary().setObjectForKey(tableInstallOptions(aTableContainer).objectAtIndex(0), aTableName); 
                }
            }
        }
        
        requiredTablesToIncludeData = new NSMutableArray(newRequireTables.mutableClone().allObjects());
    }


    
    /**
     * Returns true if current table data is required to be included in the package, false otherwise
     *
     * @return true if current table data is required to be included in the package, false otherwise
     */        
    public boolean isRequiredToIncludeData()
    {
        return (requiredTablesToIncludeData().containsObject(aTable.name()) && ( ! isMainTable()) && ( ! showExistingTableOption()));
    }


    
    /*** Generic getters/setters ****/
    public String installType()
    {
        return installType;
    }

    public void setInstallType(String aValue)
    {
        installType = aValue;
    }        
    
    public NSDictionary existingTableMappings()
    {
        return existingTableMappings;
    }   
    
    public void setExistingTableMappings(NSDictionary newValue)
    {
        existingTableMappings = newValue;
    }     
    
    public NSMutableArray sectionsToInstall()
    {
        return sectionsToInstall;
    }
    
    public NSMutableDictionary tableInstallOptionsDictionary()
    {
        return tableInstallOptionsDictionary;
    }
    
    public String errors()
    {
        return errors;
    }
    
    public NSMutableArray requiredTablesToIncludeData()
    {
        return requiredTablesToIncludeData;
        
        /** ensure [result_not_null] Result != null; **/ 
    }        
}
