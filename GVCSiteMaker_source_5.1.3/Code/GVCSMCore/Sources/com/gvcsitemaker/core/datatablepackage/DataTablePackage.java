// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.datatablepackage;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.digester.*;
import org.xml.sax.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.appserver.xml.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Date;
import net.global_village.virtualtables.*;


/**
 * Main class for creating and installing a DataTablePackage
 */
public class DataTablePackage extends Object
{
    public static final String DataTablePackageMappingModelFile = "DataTablePackageXMLModel.txt";   // Name of Mapping Model to create data table package files
    public static final String DataTablePackageDTDFile = "DataTablePackage.dtd";   // Name of Mapping Model to create data table package files

    //Different install options for a table
    public static final String InstallTableIncludingDataOption = "Install table from package, including data";
    public static final String InstallTableExcludingDataOption = "Install table from package, excluding data";
    public static final String InstallMainTableIncludingDataOption = "Install main table from package, including data";
    public static final String InstallMainTableExcludingDataOption = "Install main table from package, excluding data";
    public static final String InstallTableOption = "Install table from package";
    public static final String UseExistingtableOptionPrefix = "Use Existing table named ";


    protected SMVirtualTable mainTableInPackage;
    protected NSMutableDictionary relatedTablesMapping;
    protected NSMutableArray importedDataTables;
    protected NSArray tablesToInstallData;
    protected Website website;

    protected NSMutableArray orderedCreatedTables;

    /**
     * Creates the data table package based on passed parameters
     *
     * @param delimiter the UI representation of a delimiter to translated into the actual delimiter
     * @return the actual characters that the name represents
     */
    static public String xmlPackage(SMVirtualTable mainTable,
                                       NSArray tablesToIncludeData,
                                       NSArray sections,
                                       EOEditingContext ec)
    {
        /** require
            [valid_mainTable] mainTable != null;
            [valid_tablesToIncludeData] tablesToIncludeData != null;
            [valid_sections] sections != null;
            [valid_ec] ec != null;
         **/

        //create adaptor with passed parameters
        DebugOut.println(1, "Creating XML package...");
        DataTablePackageAdaptor packageAdaptor = new DataTablePackageAdaptor(mainTable, tablesToIncludeData, sections, ec);

        String modelFile = SMApplication.application().resourceManager().pathURLForResourceNamed(DataTablePackageMappingModelFile, "GVCSMCore",  null).toString();
        WOXMLCoder coder = WOXMLCoder.coderWithMapping(modelFile);

        DebugOut.println(1, "Before encoding...");

        String xml = coder.encodeRootObjectForKey(packageAdaptor, "databaseTable");

        DebugOut.println(1, "XML successfully encoded.");

        String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF8\"?>";
        String dtdFile = new String(SMApplication.application().resourceManager().bytesForResourceNamed(DataTablePackageDTDFile, "GVCSMCore",  null));
        xml = xmlDeclaration + "\n" + dtdFile + "\n" + xml;

        return xml;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the PackageContainer parsed with the passed packageFile
     *
     * @param the packageFile the XML SiteFile containing the package
     * @return the PackageContainer parsed with the passed packageFile
     * @throws IOException
     * @throws SAXException
     */
    static public PackageContainer parsePackage(SiteFile packageFile) throws SAXException, IOException
    {
        /** require [packageFile_not_null]  packageFile != null; **/

        return parsePackage(new String(packageFile.data().bytes()));

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the PackageContainer parsed with the passed xml
     *
     * @param the xml XML containing the package
     * @return the PackageContainer parsed with the passed xml
     * @throws IOException
     */
    static public PackageContainer parsePackage(String xml) throws SAXException, IOException
    {
        /** require [xml_not_null]  xml != null; **/

        PackageContainer packageContainer = null;

        Digester digester = new Digester();

        digester.setValidating(true);
        DigesterErrorHandler errorHandler = new DigesterErrorHandler();
        digester.setErrorHandler(errorHandler);

        try
        {
            // must be turned off after validation
            digester.setFeature("http://xml.org/sax/features/external-general-entities", false);
            digester.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        catch (Exception e)
        {
            System.out.println("Exception configuring the SAX parser: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        digester.addObjectCreate( "package", PackageContainer.class );
        digester.addSetProperties( "package");

        digester.addObjectCreate( "package/relatedTable", TableContainer.class );
        digester.addSetProperties( "package/relatedTable");

        digester.addObjectCreate( "package/relatedTable/column", ColumnContainer.class );
        digester.addSetProperties( "package/relatedTable/column");
        digester.addSetNext( "package/relatedTable/column", "addColumn" );

        digester.addBeanPropertySetter( "package/relatedTable/rowData", "rowData" );

        digester.addSetNext( "package/relatedTable", "addTable" );

        digester.addObjectCreate( "package/relatedSection", SectionContainer.class );
        digester.addSetProperties( "package/relatedSection");

        digester.addObjectCreate( "package/relatedSection/component", ComponentContainer.class );
        digester.addSetProperties( "package/relatedSection/component");
        digester.addSetNext( "package/relatedSection/component", "setComponent" );

        //data access modes
        digester.addObjectCreate( "package/relatedSection/component/component", ComponentContainer.class );
        digester.addSetProperties( "package/relatedSection/component/component");
        digester.addSetNext( "package/relatedSection/component/component", "addToChildren" );

        digester.addBeanPropertySetter( "package/relatedSection/component/component/template", "template" );

        //data access columns
        digester.addObjectCreate( "package/relatedSection/component/component/component", ComponentContainer.class );
        digester.addSetProperties( "package/relatedSection/component/component/component");
        digester.addSetNext( "package/relatedSection/component/component/component", "addToChildren" );

        digester.addSetNext( "package/relatedSection", "addSection" );

        digester.addObjectCreate( "package/mainTable", TableContainer.class );
        digester.addSetProperties( "package/mainTable");

        digester.addObjectCreate( "package/mainTable/column", ColumnContainer.class );
        digester.addSetProperties( "package/mainTable/column");
        digester.addSetNext( "package/mainTable/column", "addColumn" );

        digester.addBeanPropertySetter( "package/mainTable/rowData", "rowData" );

        digester.addSetNext( "package/mainTable", "setMainTable" );

        Reader reader = new StringReader(xml);

        packageContainer = (PackageContainer) digester.parse(reader);

        return packageContainer;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the unique file name to be used when a package is created based on the website and prefix.
     *
     * @param website the site that contains the data to create package to
     * @param prefix the name of the main table of the package
     * return the unique file name to be used when a package is created based on the website and prefix.
     */
    static public String uniqueFileName(Website website, String prefix)
    {
        /** require
        [valid_website] website != null;
        [valid_prefix] prefix != null;
        **/
        NSTimestampFormatter formatter = new NSTimestampFormatter("%m-%d-%Y");
        String currentDateAsString = formatter.format(new NSTimestamp());

        String uniqueFileName = prefix + "_" + currentDateAsString + ".xml";

        int counter = 0;
        while (website.fileForFilename(uniqueFileName) != null)
        {
            counter++;
            uniqueFileName = prefix + "_" + currentDateAsString + "_" + counter + ".xml";
        }

        return uniqueFileName;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * The list of matching existing tables in the passed website based on the tableToMatch in the packageContainer.
     *
     * @param website the site that contains the existing tables to match
     * @param tableToMatch the TableContainer to match
     * @param packageContainer the packageContainer of tableToMatch
     * @return list of matching existing tables in the passed website based on the tableToMatch in the packageContainer.
     */
    static public NSArray orderedMatchingTables(Website website, TableContainer tableToMatch, PackageContainer packageContainer)
    {
        /** require
        [valid_website] website != null;
        [valid_tableToMatch] tableToMatch != null;
        [valid_packageContainer] packageContainer != null;
        **/
        NSMutableArray matchingTables = new NSMutableArray();
        Enumeration tableEnumerator = website.orderedDatabaseTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable aTable = (SMVirtualTable) tableEnumerator.nextElement();
            if (isTableMatching(tableToMatch, aTable, packageContainer))
            {
                matchingTables.addObject(aTable);
            }
        }
        return matchingTables;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns true if all columns of tableContainerToMatch matches actualTable.  Each column should have the same column names and types.
     * Aside from its columns, if tableContainerToMatch has lookup columns, they should also match their corresponding table.
     *
     * @param tableContainerToMatch the TableContainer to match
     * @param actualTable the existing table to match against
     * @param packageContainer the packageContainer of tableToMatch
     * @return list of matching existing tables in the passed website based on the tableToMatch in the packageContainer.
     */
    static public boolean isTableMatching(TableContainer tableContainerToMatch, Table actualTable, PackageContainer packageContainer)
    {
        /** require
        [valid_tableContainerToMatch] tableContainerToMatch != null;
        [valid_tactualTable] actualTable != null;
        [valid_packageContainer] packageContainer != null;
        **/

       boolean matchesAllColumns = true;

       Enumeration columnEnumerator = tableContainerToMatch.columns().objectEnumerator();
       while (columnEnumerator.hasMoreElements() && (matchesAllColumns))
       {
           ColumnContainer aColumn = (ColumnContainer) columnEnumerator.nextElement();
           boolean hasMatchingColumn = false;

           Enumeration actualColumnEnumerator = actualTable.orderedUserColumns().objectEnumerator();
           while (actualColumnEnumerator.hasMoreElements() && (! hasMatchingColumn))
           {
               VirtualColumn actualColumn = (VirtualColumn) actualColumnEnumerator.nextElement();
               if ((aColumn.name().equalsIgnoreCase(actualColumn.name())) && (aColumn.type().equalsIgnoreCase(actualColumn.type().name())))
               {
                   if (actualColumn instanceof VirtualLookupColumn)
                   {
                       if ((aColumn.lookupColumn().equalsIgnoreCase(((VirtualLookupColumn) actualColumn).lookupColumn().name())) &&
                               (aColumn.lookupColumnType()).equalsIgnoreCase(((VirtualLookupColumn) actualColumn).lookupColumn().type().name()))
                       {
                           hasMatchingColumn = isTableMatching(packageContainer.tableContainerWithName(aColumn.lookupTable()), ((VirtualLookupColumn) actualColumn).lookupColumn().table(), packageContainer);
                       }
                   }
                   else
                   {
                       hasMatchingColumn = true;
                   }
               }
           }

           matchesAllColumns = hasMatchingColumn;
       }

       return matchesAllColumns;
    }



    /**
     * Returns the NSDictionary of mappings of the existing Tables and their matching table names from the package
     *
     * @param existingTable the existing table to map
     * @param packageTable the tableContainer to match
     * @param packageContainer the container with the package
     * @return the NSDictionary of mappings of the existing Tables and their matching table names from the package
     */
    static public NSDictionary existingTableMapping(Table existingTable, TableContainer packageTable, PackageContainer packageContainer)
    {
        /** require
        [valid_existingTable] existingTable != null;
        [valid_packageTable] packageTable != null;
        [valid_packageContainer] packageContainer != null;
        **/

        NSMutableDictionary existingTableMapping = new NSMutableDictionary();

        //add mapping for main table
        existingTableMapping.setObjectForKey(existingTable, packageTable.name());


        Enumeration columnEnumerator = packageTable.columns().objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            ColumnContainer aColumn = (ColumnContainer) columnEnumerator.nextElement();

            Enumeration actualColumnEnumerator = existingTable.orderedUserColumns().objectEnumerator();
            while (actualColumnEnumerator.hasMoreElements())
            {
                VirtualColumn actualColumn = (VirtualColumn) actualColumnEnumerator.nextElement();
                if ((aColumn.name().equalsIgnoreCase(actualColumn.name())) && (aColumn.type().equalsIgnoreCase(actualColumn.type().name())))
                {
                    if (actualColumn instanceof VirtualLookupColumn)
                    {
                        if ((aColumn.lookupColumn().equalsIgnoreCase(((VirtualLookupColumn) actualColumn).lookupColumn().name())) &&
                                (aColumn.lookupColumnType()).equalsIgnoreCase(((VirtualLookupColumn) actualColumn).lookupColumn().type().name()))
                        {
                            existingTableMapping.addEntriesFromDictionary(existingTableMapping(((VirtualLookupColumn) actualColumn).lookupColumn().table(), packageContainer.tableContainerWithName(aColumn.lookupTable()), packageContainer));
                        }
                    }
                }
            }
        }

        return existingTableMapping;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Designated constructor
     */
    public DataTablePackage()
    {
        relatedTablesMapping = new NSMutableDictionary();
        importedDataTables = new NSMutableArray();
        orderedCreatedTables = new NSMutableArray();
    }



    /**
     * Returns the matching SMVirtualTable that has been created with the passed name
     *
     * @param name the name of the table to match
     * @return  the matching SMVirtualTable that has been created with the passed name
     */
    public SMVirtualTable tableWithName(String name)
    {
        /** require [name_not_null]  name != null; **/

        return (SMVirtualTable) relatedTablesMapping().objectForKey(name);
    }



    /**
     * Returns the matching SMVirtualTable that has been included in the package.
     *
     * @param name the name of the table to match
     * @return  the matching SMVirtualTable that has been included in the package.
     */
    public SMVirtualTable importedDataTableWithName(String name)
    {
        /** require [name_not_null]  name != null; **/

        SMVirtualTable tableWithName = null;

        Enumeration tableEnumerator = importedDataTables().objectEnumerator();
        while (tableEnumerator.hasMoreElements())
        {
            SMVirtualTable aTable = (SMVirtualTable) tableEnumerator.nextElement();
            if (aTable.name().equalsIgnoreCase(tableWithName(name).name()))
            {
                tableWithName = aTable;
            }
        }

        return tableWithName;
    }




    /**
     * Installs the SiteFile xmlPackage to theWebsite based on the passed install parameters.
     *
     * @param theWebsite the website to install the xmlPackage
     * @param xmlPackage the SiteFile of type xml which contains the data to install
     * @param tablesToInstall the array of tables names contained in the package whose data should be installed.
     * @param sectionsToInstall the array of section names contained in the package to be installed
     * @param existingTableToUse the existing table to be used instead of the main table contained in the package
     * @param currentUser the user who initiated the installation of this package
     * @param ec the editingContext to use in processing the install
     * @throws IOException
     * @throws SAXException
     */
    public void installPackage(Website theWebsite,
                                SiteFile xmlPackage,
                                NSArray tablesToInstall,
                                NSArray sectionsToInstall,
                                SMVirtualTable existingTableToUse,
                                User currentUser,
                                EOEditingContext ec) throws SAXException, IOException
    {
        /** require
        [valid_theWebsite] theWebsite != null;
        [valid_tablesToInstallData] tablesToInstall != null;
        [valid_sectionsToInstall] sectionsToInstall != null;
        [valid_currentUser] currentUser != null;
        [valid_ec] ec != null;
        **/

        PackageContainer packageContainer = parsePackage(xmlPackage);
        website = (Website) EOUtilities.localInstanceOfObject(ec, theWebsite);
        setTablesToInstallData(tablesToInstall);

        DebugOut.println(1, "Tables to install data: " + tablesToInstall);

        try
        {
            if (existingTableToUse == null)
            {
                createTable(website, packageContainer.mainTable(), currentUser, packageContainer, ec);

                importTableData(packageContainer.mainTable(), currentUser, packageContainer, ec);
                Enumeration sectionsEnumerator = packageContainer.orderedSections().objectEnumerator();
                while (sectionsEnumerator.hasMoreElements())
                {
                    SectionContainer aSection = (SectionContainer) sectionsEnumerator.nextElement();
                    if (sectionsToInstall.containsObject(aSection.name))
                    {
                        createSection(xmlPackage.website(), aSection, currentUser, null, ec);
                    }
                }
            }
            else
            {
                NSDictionary mappings = DataTablePackage.existingTableMapping(existingTableToUse, packageContainer.mainTable(), packageContainer);

                Enumeration sectionsEnumerator = packageContainer.orderedSections().objectEnumerator();
                while (sectionsEnumerator.hasMoreElements())
                {
                    SectionContainer aSection = (SectionContainer) sectionsEnumerator.nextElement();
                    if (sectionsToInstall.containsObject(aSection.name))
                    {
                        createSection(xmlPackage.website(), aSection, currentUser, (SMVirtualTable) mappings.objectForKey(aSection.component().databaseTableName()), ec);
                    }
                }
            }

            ec.saveChanges();
        }
        catch (Exception e)
        {
            DebugOut.println(1, "Import failed: ");
            DebugOut.printStackTrace(1, e);

            // Delete table data that has been created.
            try
            {
                ec.revert();
                Enumeration tablesEnumerator = orderedCreatedTables.objectEnumerator();
                while (tablesEnumerator.hasMoreElements())
                {
                    SMVirtualTable aTableToDelete = (SMVirtualTable) tablesEnumerator.nextElement();
                    DebugOut.println(1, "Cleaning up data in " + aTableToDelete.name());

                    // The data may just be in the database so we need to refresh the in-memory version
                    aTableToDelete.refreshTableData();
                    aTableToDelete.deleteRows();

                    ec.deleteObject(aTableToDelete);
                    ec.saveChanges();
                }
            }
            catch (Exception e2)
            {
                DebugOut.println(1, "Failed to cleanup import: ");
                DebugOut.printStackTrace(1, e2);
            }

            throw new ExceptionConverter(e);
        }
    }



    /**
     * Creates the table contained in the passed TableContainer.
     *
     * @param site the website to install the table
     * @param table the TableContainer containing contents of new table
     * @param packageContainer the packageContainer of the package being installed
     * @param ec the editingContext to use in processing the install*
     */
    public void createTable(Website site, TableContainer table, User currentUser, PackageContainer packageContainer, EOEditingContext ec) throws IOException, ParseException
    {
        /** require
        [valid_site] site != null;
        [valid_table] table != null;
        [valid_packageContainer] packageContainer != null;
        [valid_ec] ec != null;
        **/
        DebugOut.println(1, "Creating table: " + table.name());

        SMVirtualTable newTable = SMVirtualTable.newSMVirtualTable();
        ec.insertObject(newTable);
        site.addDatabaseTable(newTable);
        newTable.setCreatedBy(currentUser);
        newTable.setModifiedBy(currentUser);
        newTable.setName(site.uniqueTableName(table.name()));
        newTable.setTextDescription(table.textDescription());

        orderedCreatedTables.addObject(newTable);

        Enumeration columnsEnumerator = table.columns().objectEnumerator();
        while (columnsEnumerator.hasMoreElements())
        {
            ColumnContainer aColumn = (ColumnContainer) columnsEnumerator.nextElement();
            Column newColumn = newTable.newColumn(aColumn.name(), ColumnType.columnTypeWithName(ec, aColumn.type()));
            if ( ! StringAdditions.isEmpty(aColumn.textDescription()))
            {
                newColumn.setTextDescription(aColumn.textDescription());
            }

            if (newColumn instanceof VirtualLookupColumn)
            {
                SMVirtualTable lookupTable = tableWithName(aColumn.lookupTable());
                if (lookupTable == null)
                {
                    createTable(site, packageContainer.tableContainerWithName(aColumn.lookupTable()), currentUser, packageContainer, ec);
                }

                lookupTable = tableWithName(aColumn.lookupTable());
                Column lookupColumn = lookupTable.columnNamed(aColumn.lookupColumn());
                ((VirtualLookupColumn) newColumn).setLookupColumn(lookupColumn);
            }

            if (newColumn instanceof VirtualCalculatedColumn)
            {
                ((VirtualCalculatedColumn)newColumn).setExpression(aColumn.expression());
            }

            newTable.setModifiedBy((User) EOUtilities.localInstanceOfObject(ec, currentUser));
            newTable.setDateModified(Date.now());
        }

        relatedTablesMapping().setObjectForKey(newTable, table.name());
    }



    /**
     * Imports the the table data for the passed tableContainer.
     *
     * @param tableContainer the TableContainer containing data to install
     * @param currentUser the current user
     * @param packageContainer the packageContainer of the package being installed
     * @param ec the editingContext to use in processing the install*
     */
    public void importTableData(TableContainer tableContainer, User currentUser, PackageContainer packageContainer, EOEditingContext ec) throws IOException, ParseException
    {
        /** require
        [valid_tableContainer] tableContainer != null;
        [valid_currentUser] currentUser != null;
        [valid_packageContainer] packageContainer != null;
        [valid_ec] ec != null;
        **/

        DebugOut.println(1, "Importing data for table: " + tableContainer.name());

        Enumeration columnsEnumerator = tableContainer.columns().objectEnumerator();
        while (columnsEnumerator.hasMoreElements())
        {
            ColumnContainer aColumn = (ColumnContainer) columnsEnumerator.nextElement();

            if (aColumn.type().equals("Lookup")) //get static string
            {
                SMVirtualTable lookupTable = importedDataTableWithName(aColumn.lookupTable());

                if (lookupTable == null)
                {
                    importTableData(packageContainer.tableContainerWithName(aColumn.lookupTable()), currentUser, packageContainer, ec);
                }
            }

        }

        if (shouldInstallData(tableContainer) && ( ! net.global_village.foundation.StringAdditions.isEmpty(tableContainer.rowData())))
        {
            ec.saveChanges();

            SMVirtualTable table = tableWithName(tableContainer.name());

            NSMutableArray rowDataColumns = new NSMutableArray();
            Enumeration rowDataColumnsEnumerator = tableContainer.rowDataColumnNames().objectEnumerator();
            while (rowDataColumnsEnumerator.hasMoreElements())
            {
                String aColumnName = (String) rowDataColumnsEnumerator.nextElement();
                rowDataColumns.addObject(table.columnNamed(aColumnName));
            }

            ColumnType.addToValidImportTypes("SiteFile");

            DebugOut.println(1, "Before importTable...");
            table.importTable((new NSData(tableContainer.rowData(), _NSUtilities.UTF8StringEncoding)).stream(),
                    null,
                    rowDataColumns,
                    DataAccessImportExport.COMMA_DELIMITER,
                    true,
                    false,
                    currentUser);

            ec.saveChanges();

            DebugOut.println(1, "Finished importing for data for table: " + tableContainer.name());

            addToImportedDataTables(table);
        }
    }



    /**
     * Creates the section contained in the passed sectionContainer.
     *
     * @param site the site to install the new section
     * @param sectionContainer the SectionContainer containing the contents of the new section to create
     * @param currentUser the current user
     * @param existingTableToUse the name of an existing table that will be used instead of the mainTable in the package.
     * @param ec the editingContext to use in processing the install
     */
    public void createSection(Website site, SectionContainer sectionContainer, User currentUser, SMVirtualTable existingTableToUse, EOEditingContext ec)
    {
        /** require
        [valid_site] site != null;
        [valid_sectionContainer] sectionContainer != null;
        [valid_currentUser] currentUser != null;
        [valid_ec] ec != null;
        **/

        DebugOut.println(1, "Creating section: " + sectionContainer.name());

        Section newSection = Section.newSection();
        ec.insertObject(newSection);
        newSection.setType(DataAccessSectionType.getInstance(ec));

        newSection.setDetails(sectionContainer.details());
        newSection.setName(site.uniqueSectionName(sectionContainer.name()));
        newSection.setIsNavigable(new net.global_village.foundation.GVCBoolean(sectionContainer.isNavigable()));

        // Use the default if we are importing packages from previous versions
        if (sectionContainer.mimeType() != null)
        {
            newSection.setMimeType(sectionContainer.mimeType());
        }

        newSection.allowAccessForGroup(site.ownersGroup());

        newSection.type().createComponents(newSection);
        site.attachSection(newSection);

        DataAccess dataAccessComponent = ((DataAccess) newSection.component());
        //Table has to be set before the bindings since synchronizeWithTable() gets called which update defaults
        if (existingTableToUse == null)
        {
            dataAccessComponent.setDatabaseTable(tableWithName(sectionContainer.component().databaseTableName()));
        }
        else
        {
            dataAccessComponent.setDatabaseTable(existingTableToUse);
        }

        //update components
        newSection.component().setBindings(sectionContainer.component().bindings());

        Enumeration dataAccessModeEnumerator = dataAccessComponent.toChildren().objectEnumerator();
        while (dataAccessModeEnumerator.hasMoreElements())
        {
            DataAccessMode aDataAccessMode = (DataAccessMode) dataAccessModeEnumerator.nextElement();
            ComponentContainer dataAccessModeContainer = sectionContainer.component().childComponentWithType(aDataAccessMode.componentType());

            aDataAccessMode.setCanEdit(dataAccessModeContainer.canEdit());
            aDataAccessMode.setUseCustomTemplate(dataAccessModeContainer.useCustomTemplate());

            if (aDataAccessMode instanceof DataAccessSearchMode)
            {
                ((DataAccessSearchMode) aDataAccessMode).setSearchFormType(dataAccessModeContainer.searchFormType());

                //Delete default sortOrders
                aDataAccessMode.removeChildren(((DataAccessSearchMode) aDataAccessMode).orderedSortOrders());
            }

            DebugOut.println(1, "Setting up data access mode children for " + aDataAccessMode.componentType() + aDataAccessMode.toChildren().count());

            Enumeration dataAccessColumnEnumerator = aDataAccessMode.toChildren().objectEnumerator();
            while (dataAccessColumnEnumerator.hasMoreElements())
            {
                DataAccessColumn aDataAccessColumn = (DataAccessColumn) dataAccessColumnEnumerator.nextElement();
                DebugOut.println(1, "Setting up data access column for " + aDataAccessColumn.componentType());

                ComponentContainer componentContainer = dataAccessModeContainer.childComponentForColumn(aDataAccessColumn.column().name(), aDataAccessColumn.componentType());

                if (componentContainer != null)
                {
                    aDataAccessColumn.setBindings(componentContainer.bindings());

                    //Set up default values
                    if ((aDataAccessMode instanceof DataAccessAddMode) && (aDataAccessColumn instanceof DataAccessLookupColumn))
                    {
                        DataAccessLookupColumn lookupColumn = (DataAccessLookupColumn) aDataAccessColumn;
                        String defaultFieldValue = componentContainer.defaultFieldValue();

                        if (defaultFieldValue != null)
                        {
                            Enumeration lookupValueEnumerator = lookupColumn.allLookupValues().objectEnumerator();
                            while (lookupValueEnumerator.hasMoreElements())
                            {
                                VirtualField aLookupValue = (VirtualField) lookupValueEnumerator.nextElement();
                                if (aLookupValue.value().toString().equals(defaultFieldValue))
                                {
                                    if (lookupColumn.defaultField() == null)
                                    {
                                        lookupColumn.setDefaultField(aLookupValue);
                                    }
                                    else
                                    {
                                        throw new RuntimeException("Import error: The default lookup value for " +  aDataAccessColumn.column().name() + " cannot be determined.");
                                    }
                                }
                            }
                        }
                    }
                }

            }

            Enumeration childrenEnumerator =  dataAccessModeContainer.children().objectEnumerator();
            while (childrenEnumerator.hasMoreElements())
            {
                ComponentContainer aComponent = (ComponentContainer) childrenEnumerator.nextElement();

                //Add search value columns
                if ((aDataAccessMode instanceof DataAccessSearchMode) && (aComponent.componentType().equals("DataAccessColumnSearchValue")))
                {
                    DataAccessColumnSearchValue searchValue = DataAccessColumnSearchValue.newDataAccessColumnSearchValue();

                    searchValue.setToParent(aDataAccessMode);
                    searchValue.setColumn(dataAccessComponent.databaseTable().columnNamed(aComponent.columnName()));
                    searchValue.setBindings(aComponent.bindings());
                    aDataAccessMode.addChild(searchValue);
                }

                //Add sort order columns
                if ((aDataAccessMode instanceof DataAccessSearchMode) && (aComponent.componentType().equals("DataAccessColumnSearchSortOrder")))
                {
                    DataAccessColumnSearchSortOrder sortOrder = DataAccessColumnSearchSortOrder.newDataAccessColumnSearchSortOrder();

                    sortOrder.setToParent(aDataAccessMode);
                    sortOrder.setColumn(dataAccessComponent.databaseTable().columnNamed(aComponent.columnName()));
                    sortOrder.setBindings(aComponent.bindings());
                    aDataAccessMode.addChild(sortOrder);
                }

            }

            if (aDataAccessMode.useCustomTemplate() && (dataAccessModeContainer.template() != null))
            {
                aDataAccessMode.setCustomTemplateHtml(new NSData(dataAccessModeContainer.template(), _NSUtilities.UTF8StringEncoding));
            }

            //Go through each column and re-order based on package.  I think it is safer to do it here to make sure all column components are already created

            dataAccessColumnEnumerator = aDataAccessMode.toChildren().objectEnumerator();
            while (dataAccessColumnEnumerator.hasMoreElements())
            {
                DataAccessColumn aDataAccessColumn = (DataAccessColumn) dataAccessColumnEnumerator.nextElement();
                ComponentContainer componentContainer = dataAccessModeContainer.childComponentForColumn(aDataAccessColumn.column().name(), aDataAccessColumn.componentType());

                aDataAccessMode.moveComponentToPosition(aDataAccessColumn, (new Integer(componentContainer.componentOrder())).intValue());
            }
        }
    }



    /**
     * Returns true if aTable should be installed, false otherwise
     *
     * @param aTable the TableContainer to evaluate
     * @return true if aTable should be installed, false otherwise
     */
    public boolean shouldInstallData(TableContainer aTable)
    {
        /** require [aTable_not_null]  aTable != null; **/

        return tablesToInstallData().containsObject(aTable.name());
    }



    /*** generic setters and getters ****/

    public NSArray tablesToInstallData()
    {
        return tablesToInstallData;
    }



    public void setTablesToInstallData(NSArray value)
    {
        tablesToInstallData = value;
    }



    public Website website()
    {
        return website;
    }




    public NSMutableDictionary relatedTablesMapping()
    {
        return relatedTablesMapping;
    }



    public void addToImportedDataTables(SMVirtualTable newTable)
    {
        NSMutableArray array = (NSMutableArray)importedDataTables();
        array.addObject(newTable);
    }



    public NSArray importedDataTables()
    {
        return importedDataTables;
    }



    public SMVirtualTable mainTableInPackage()
    {
        return mainTableInPackage;
    }



    public void setMainTableInPackage(SMVirtualTable aValue)
    {
        mainTableInPackage = aValue;
    }
}
