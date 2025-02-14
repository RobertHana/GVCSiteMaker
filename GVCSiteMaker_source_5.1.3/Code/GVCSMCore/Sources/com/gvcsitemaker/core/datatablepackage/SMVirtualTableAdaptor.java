// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.io.*;
import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.foundation.*;


/**
 * Adaptor for packaging an SMVirtualTable.  This is specially used to handle packaging of SMVirtualTable's rows.
 */
public class SMVirtualTableAdaptor
{
    protected SMVirtualTable table;
    protected boolean shouldIncludeDataInPackage;
    
    
    /**
     * Designated constructor.
     *
     * @param aTable the SMVirtualTable that this adaptor is for
     * @param shouldIncludeData true if this adaptor will include data when returning the packagedRows, false otherwise
     */    
    public SMVirtualTableAdaptor(SMVirtualTable aTable, boolean shouldIncludeData) 
    {
        /** require [aTable_not_null] aTable != null; **/
        
        table = aTable;
        shouldIncludeDataInPackage = shouldIncludeData;
    }
    
    
    
    /**
     * Returns the String representation of all the table rows fir for inclusion to a data table package
     *
     * @return the String representation of all the table rows fir for inclusion to a data table package
     */
    public String packagedRows()
    {
        String packagedRows = null;

        if ((shouldIncludeDataInPackage) && table().hasObjects())
        {
            ByteArrayOutputStream exportStream = new ByteArrayOutputStream(table().rows().count() * 1024);
            try
            {
                //TODO: might be good to have the delimeter configuration
                table().exportTable(null,
                            null,
                            exportStream,
                            SMVirtualTable.DataTablePackageExport,
                            importableColumnsForPackage(),
                            DataAccessImportExport.COMMA_DELIMITER,
                            true);

                packagedRows = exportStream.toString();
                exportStream.close();
            }
            catch (IOException e)
            {
                throw new ExceptionConverter(e);
            }                  
        }

        
        return packagedRows;
    }     
    
    
    
    /**
     * Returns the columns that can be imported in a data table package. It returns all columns from importableColumns() with the addition of VirtualSiteFileField columns.
     *
     * @return the columns that can be imported in a data table package. It returns all columns from importableColumns() with the addition of VirtualSiteFileField columns.
     */
    public NSArray importableColumnsForPackage()
    {
        NSMutableArray importableColumnsForPackage = new NSMutableArray(table().importableColumns());

        Enumeration columnEnumerator = table().columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column)columnEnumerator.nextElement();
            if (aColumn.type().name().equals(VirtualSiteFileField.ColumnTypeName))
            {
                importableColumnsForPackage.addObject(aColumn);
            }
        }

        return importableColumnsForPackage;
        
        /** ensure [valid_result] Result != null; **/
    }     
    
    
    
    /*** Generic getters/setters ****/

    public SMVirtualTable table()
    {
        return table;
    }
    
    
 
    public void setTable(SMVirtualTable value)
    {
        table = value;
    }    
}
