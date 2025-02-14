// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * Main container for a data table package.   
 */
public class PackageContainer
{
    protected TableContainer mainTable;
    protected NSMutableArray tables;
    protected NSMutableArray sections;

    
    /**
     * Designated constructor
     */    
    public PackageContainer()
    {
        tables = new NSMutableArray();
        sections = new NSMutableArray();        
    }

    
    
    /**
     * Returns the TableContainer contained in the package with the passed tableName. Returns null if no TableContainer is found.
     * 
     * @param tableName the name of the table to match
     * @return  the TableContainer contained in the package with the passed tableName.
     */    
    public TableContainer tableContainerWithName(String tableName)
    {
        /** require [tableName_not_null]  tableName != null; **/
        
        TableContainer matchingTableContainer = null;
        
        Enumeration tablesEnumerator = tables.objectEnumerator();
        while (tablesEnumerator.hasMoreElements())
        {
            TableContainer aTable = (TableContainer) tablesEnumerator.nextElement();
            if (aTable.name().equals(tableName))
            {
                matchingTableContainer = aTable;
            }
        }
        
        return matchingTableContainer;
    }     


    
    /**
     * Returns the SectionContainers that has the passed tableName as their databaseTableName.
     * 
     * @param tableName the name of the table to match
     * @return the SectionContainers that has the passed tableName as their databaseTableName.
     */    
    public NSArray sectionContainers(String tableName)
    {
        /** require [tableName_not_null]  tableName != null; **/
        
        NSMutableArray matchingSectionContainers = new NSMutableArray();
        
        Enumeration sectionEnumerator = sections.objectEnumerator();
        while (sectionEnumerator.hasMoreElements())
        {
            SectionContainer aSection = (SectionContainer) sectionEnumerator.nextElement();
            if (aSection.component().databaseTableName().equals(tableName))
            {
                matchingSectionContainers.addObject(aSection);
            }
        }
        
        return matchingSectionContainers;
    }
    
    
    
    /**
     * Returns true if the package contains sections.
     * 
     * @return true if the package contains sections.
     */    
    public boolean hasSections()
    {
        return sections().count() > 0;
    }
    
    
    
    /**
     * Returns an ordered list of Sections contained in this package.  The Sections are ordered by sectionOrder ascending.
     * 
     * @return an ordered list of Sections contained in this package.  The Sections are ordered by sectionOrder ascending.
     */     
    public NSArray orderedSections()
    {
        EOSortOrdering sectionOrdering = EOSortOrdering.sortOrderingWithKey("sectionOrder", EOSortOrdering.CompareAscending);
        NSArray sortOrdering = new NSArray(sectionOrdering);

        EOSortOrdering.sortArrayUsingKeyOrderArray(sections, sortOrdering);

        return sections;
        
        /** ensure [valid_result] Result != null;  **/        
    }    

    
    /*** Generic getters/setters ****/

    public TableContainer mainTable()
    {
        return mainTable;
        
        /** ensure [valid_result] Result != null;  **/        
    }
    
    
    
    public void setMainTable(TableContainer aTableContainer)
    {
        mainTable = aTableContainer;
    }
    
    
    
    public NSArray tables()
    {
        return tables;
        
        /** ensure [valid_result] Result != null;  **/        
    }

    
    
    public void addTable(TableContainer newTable) 
    {
        NSMutableArray array = (NSMutableArray)tables();
        array.addObject(newTable);
    }
    
    
    
    public NSArray sections()
    {
        return sections;
        
        /** ensure [valid_result] Result != null;  **/        
    }    

    
    
    public void addSection(SectionContainer newSection) 
    {
        NSMutableArray array = (NSMutableArray)sections();
        array.addObject(newSection);
    }        
}
