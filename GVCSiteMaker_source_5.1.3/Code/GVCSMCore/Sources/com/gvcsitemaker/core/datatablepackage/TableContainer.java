// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.io.*;
import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.support.*;
import com.webobjects.foundation.*;

/**
 * Container that represents an SMVirtualTable in a data table package.
 */
public class TableContainer extends BaseContainer
{
    protected NSMutableArray columns;
    protected String rowData;    

    /**
     * Designated constructor.
     */     
    public TableContainer()
    {
        columns = new NSMutableArray();
    }

    
    
    /**
     * Returns the column names of the included in the row data.  The array returned is the same ordering as it is included in the package.
     * 
     * @return the column names of the included in the row data. 
     */       
    public NSArray rowDataColumnNames() throws IOException
    {
        NSArray rowDataColumnNames = new NSArray();
        
        if (rowData() != null)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader((new NSData(rowData(), _NSUtilities.UTF8StringEncoding)).stream(), VirtualTable.STRING_ENCODING));
            String columnHeader = reader.readLine();
            //** check [same_number_of_columns] dataElements.count() == columnsToImport.count(); **/
            rowDataColumnNames = NSArray.componentsSeparatedByString(columnHeader, DataAccessImportExport.COMMA_DELIMITER); 
        }
        
        return rowDataColumnNames;
        
        /** ensure [valid_result] Result != null;  **/
    }    
    
    
    
    /**
     * Returns the related tables are any tables that this table takes its lookup values from, and any tables those tables take lookup values from, and so on, and so forth.
     * 
     * @return the related tables are any tables that this table takes its lookup values from, and any tables those tables take lookup values from, and so on, and so forth.
     */
    public NSArray lookupSourceTables()
    {
        NSMutableSet lookupSourceTables = new NSMutableSet();

        Enumeration columnEnumerator = columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            ColumnContainer aColumn = (ColumnContainer) columnEnumerator.nextElement();
            if (! StringAdditions.isEmpty(aColumn.lookupTable()))
            {
                lookupSourceTables.addObject(aColumn.lookupTable());
            }
        }

        return lookupSourceTables.immutableClone().allObjects();
        
        /** ensure [valid_result] Result != null;  **/      
    }      

    /*** Generic getters/setters ****/
    
    public NSArray columns() 
    {
        return columns;
    }

    
    
    public void addColumn(ColumnContainer newColumn) 
    {
        NSMutableArray array = (NSMutableArray)columns();
        array.addObject(newColumn);
    }

    
    
    public String rowData()
    {
        return rowData;
    }
    
    
 
    public void setRowData(String newValue)
    {
        rowData = newValue;
    }
   
    
    
    public boolean hasRowData()
    {
        return ( ! StringAdditions.isEmpty(rowData()));
    }    
    
    
    
   

}
