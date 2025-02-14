/**
 * Implementation of DataAccessSiteFileColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */  
package com.gvcsitemaker.core.pagecomponent;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * DataAccessSiteFileColumn wraps a File Column (or VirtualSiteFileColumn) and provides extra settings controlling the addition of new Fields in this column (defaults etc.).
 */
public class DataAccessSiteFileColumn extends _DataAccessSiteFileColumn
{

    // Binding keys
    public static final String SHOW_FILE_CONTENTS_BINDINGKEY = "showFileContents";
    public static final String WIDTH_BINDINGKEY = "width";


    /**
     * Factory method to create new instances of DataAccessSiteFileColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessSiteFileColumn or a subclass.
     */
    public static DataAccessSiteFileColumn newDataAccessSiteFileColumn()
    {
        return (DataAccessSiteFileColumn) SMEOUtils.newInstanceOf("DataAccessSiteFileColumn");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Validates that the value is of the correct type by attempting to coerce it.  Returns the coerced value if successful, raises an exception if not.
     *
     * @param dataAccess the DataAccess PageComponent implementing the Section that the search is
     * being performed in
     * @param column the column that we are validating
     * @param value the value that we are validating
     * @return the validated and coerced value
     * @exception NSValidation.ValidationException if the value was not valid enough to be coerced to the right type
     */
    public static Object validateColumnSearchValue(DataAccess dataAccess, Column column, String value) throws NSValidation.ValidationException
    {
        /** require  [valid_column_param] column != null; **/

        // Can't ever validate a column of this type, I don't think...
        return value;
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessSiteFileColumn");
        setShowFileContents(false);

        /** ensure [defaults_set] ! showFileContents();  **/
    }



    /**
     * Creates and returns a new SiteFile attached to this Website and in the Database Tables Folder.
     */
    public SiteFile createSiteFile()
    {
        SiteFile newFile = SiteFile.newSiteFile();
        editingContext().insertObject(newFile);

        // Link to Website
        sectionUsedIn().website().addObjectToBothSidesOfRelationshipWithKey(newFile,"files");

        // Link to folder.
        newFile.addObjectToBothSidesOfRelationshipWithKey(sectionUsedIn().website().databaseTablesFolder(),"folder");

        return newFile;
        /** ensure
        [valid_result] Result != null;
        [attached_to_website] Result.website().equals(sectionUsedIn().website());
        [in_database_tables_folder] Result.folder().equals(sectionUsedIn().website().databaseTablesFolder());
        **/
    }


    
    /**
     * EOF Validation method for the width binding.
     */
    public Object validateWidth(Object value) throws NSValidation.ValidationException
    {
        return validateInteger(value, 1, 4096, true, "Width", "width");
    }



    /**
     * Performs some extra validations, paritcularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\


    /**
     * Returns the width of the edit control to use when editing the value in this column.
     *
     * @return the width of the edit control to use when editing the value in this column.
     */
    public String width()
    {
        return (String) valueForBindingKey(WIDTH_BINDINGKEY);
    }

    /**
     * Sets the width of the edit control to use when editing the value in this column.
     *
     * @param newDefault - the width of the edit control to use when editing the value in this column.
     */
    public void setWidth(String newDefault)
    {
        setBindingForKey(newDefault, WIDTH_BINDINGKEY);
    }


    /**
     * Returns <code>true</code> if the contents of this file should be displayed, rather than a link, when it is an image file.
     *
     * @return <code>true</code> if the contents of this file should be displayed, rather than a link, when it is an image file.
     */
    public boolean showFileContents()
    {
        return booleanValueForBindingKey(SHOW_FILE_CONTENTS_BINDINGKEY);
    }

    /**
     * Sets whether the contents of this file should be displayed, rather than a link, when it is an image file.
     *
     * @param booleanValue - <code>true</code> if the contents of this file should be displayed, rather than a link, when it is an image file.
     */
    public void setShowFileContents(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, SHOW_FILE_CONTENTS_BINDINGKEY);
    }


}

