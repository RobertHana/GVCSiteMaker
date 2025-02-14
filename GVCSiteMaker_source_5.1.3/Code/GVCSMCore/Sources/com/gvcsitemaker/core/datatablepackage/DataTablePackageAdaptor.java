// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.util.*;

import com.gvcsitemaker.core.databasetables.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * Used by DataTablePackage to setup objects to be included in a data table package.
 */
public class DataTablePackageAdaptor
{

    protected SMVirtualTable mainTable;
    protected NSArray relatedTables;
    protected NSArray tablesToIncludeData;
    protected NSArray relatedSections;
    
    protected NSArray columnsToExport;
    
    protected SMVirtualTableAdaptor mainTableAdaptor;
    protected NSMutableArray relatedTableAdaptors;


    /**
     * Designated constructor
     * 
     * @param theMainTable the SMVirtualTable that this adaptor is for
     * @param theTablesToIncludeData the Tables whose data will be included in the package
     * @param sections the Sections to be included in the package
     * @param ec the editingContext to be used by this adaptor
     */
    public DataTablePackageAdaptor(SMVirtualTable theMainTable, NSArray theTablesToIncludeData, NSArray sections, EOEditingContext ec) 
    {
        /** require 
        [valid_mainTable] theMainTable != null; 
        [valid_theTablesToIncludeData] theTablesToIncludeData != null;
        [valid_sections] sections != null;
        [valid_ec] ec != null; 
        **/
        mainTable = (SMVirtualTable) EOUtilities.localInstanceOfObject(ec, theMainTable);
        relatedTables = allRelatedTables();
        relatedSections = new NSArray();
        tablesToIncludeData = new NSArray();
        
        
        if (theTablesToIncludeData.count() > 0)
        {
            tablesToIncludeData =  EOUtilities.localInstancesOfObjects(ec, theTablesToIncludeData);
        }
        if (sections.count() > 0)
        {
            relatedSections = EOUtilities.localInstancesOfObjects(ec, sections);
        }

        mainTableAdaptor = new SMVirtualTableAdaptor(mainTable, tablesToIncludeData.containsObject(mainTable));
        
        relatedTableAdaptors = new NSMutableArray();
        Enumeration tableEnumerator = relatedTables.objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable aTable = (SMVirtualTable) tableEnumerator.nextElement();
            relatedTableAdaptors.addObject(new SMVirtualTableAdaptor(aTable, tablesToIncludeData.containsObject(aTable)));
        }
    }
    
    
    
    
    
    /**
     * Returns the list of all SMVirtualTables that it uses for lookup and all its lookup table's lookup and so on and so forth.
     *
     * @return the list of all SMVirtualTables that it uses for lookup and all its lookup table's lookup and so on and so forth.
     */
    public NSArray allRelatedTables()
    {
        NSMutableArray allRelatedTables = new NSMutableArray();
        
        relatedToOneTable(allRelatedTables, mainTable());
        return allRelatedTables;
        
        /** ensure [valid_result] Result != null;  **/      
    }      

    
    
    /**
     * Used by allRelatedTables to find all the SMVirtualTables that the mainTable uses for lookup and all its lookup table's lookup and so on and so forth and adds them to list parameter.
     */    
    public void relatedToOneTable(NSMutableArray list, SMVirtualTable table)
    {
        Enumeration tableEnumerator = table.lookupSourceTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable aTable = (SMVirtualTable) tableEnumerator.nextElement();
            
            if (! list.containsObject(aTable))
            {
                list.addObject(aTable);   
                relatedToOneTable(list, aTable);
            }
        }
    }
    
    
    /*** Generic getters/setters ****/    
    
    public SMVirtualTable mainTable()
    {
        return mainTable;
    }
    
    
 
    public void setMainTable(SMVirtualTable newMainTable)
    {
        mainTable = newMainTable;
    }
    
    
    
    public NSArray relatedTables()
    {
        return relatedTables;
    }
    
    
 
    public void setRelatedTables(NSArray newRelatedTables)
    {
        relatedTables = newRelatedTables;
    }    
  
    
    
    public NSArray tablesToIncludeData()
    {
        return tablesToIncludeData;
    }
    
    
 
    public void setTablesToIncludeData(NSArray newValue)
    {
        tablesToIncludeData = newValue;
    }    
      
    
    
    public NSArray columnsToExport()
    {
        return columnsToExport;
    }
    
    
 
    public void setColumnsToExport(NSArray newColumnsToExport)
    {
        columnsToExport = newColumnsToExport;
    }    
    
    
    public NSArray relatedSections()
    {
        return relatedSections;
    }
    
    
 
    public void setRelatedSections(NSArray newRelatedSections)
    {
        relatedSections = newRelatedSections;
    }     
  
    
    public SMVirtualTable mainTableWithData()
    {
        return null;
    }
    
    
    public SMVirtualTableAdaptor mainTableAdaptor()
    {
        return mainTableAdaptor;
    }
    
    
    
    public NSArray relatedTableAdaptors()
    {
        return relatedTableAdaptors;
    }    
    
}


