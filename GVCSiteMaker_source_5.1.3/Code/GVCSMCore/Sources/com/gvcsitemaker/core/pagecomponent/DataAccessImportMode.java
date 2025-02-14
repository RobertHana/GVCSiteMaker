/**
 * Implementation of DataAccessImportMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */  
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;



/**
 * DataAccessImportMode represents the UI and configuration specific to the Import mode of a DataAccess PageComponent.  It controls the specific bindings, template, etc.  See corresponding ComponentPrimitive DataAccessImportMode for UI.
 */
public class DataAccessImportMode extends _DataAccessImportMode
{


    /**
     * Factory method to create new instances of DataAccessImportMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessImportMode or a subclass.
     */
    public static DataAccessImportMode newDataAccessImportMode()
    {
        return (DataAccessImportMode) SMEOUtils.newInstanceOf("DataAccessImportMode");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessImportMode");

    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode.
     */
    public String mode()
    {
        return DataAccessMode.IMPORT_MODE;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> as this mode will allow edits to the underlying data.
     *
     * @return <code>true</code> as this mode will allow edits to the underlying data.
     */
    public boolean isEditable()
    {
        return true;
    }



    /**
     * Returns the default custom HTML WOComponent template for this mode.  
     *
     * @return the default custom HTML WOComponent template for this mode.
     */
    public String defaultCustomTemplate()
    {
        return SMApplication.appProperties().stringPropertyForKey("DA_ImportModeCustomTemplate");
    }



    /**
     * Returns the WOD file bindings to use with this mode.
     *
     * @return the WOD file bindings to use with this mode.
     */
    public String generatedBindings()
    {
        StringBuffer wodFile = new StringBuffer(2048);

        addCommonBindings(wodFile);
        addFieldBindings(wodFile, editableFields());

        // Import function specific to Import Record Mode
        wodFile.append("DataAccess_ImportButton: WOSubmitButton {\n");
        wodFile.append("    action = importRecords;\n");
        wodFile.append("    value = \"Import\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportName: SubmitName {\n");
        wodFile.append("    action = importRecords;\n");
        wodFile.append("    inputName = \"DataAccess_ImportName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Cancel function specific to Import Record Mode
        wodFile.append("DataAccess_CancelButton: WOSubmitButton {\n");
        wodFile.append("    action = cancelChanges;\n");
        wodFile.append("    value = \"Cancel\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_CancelName: SubmitName {\n");
        wodFile.append("    action = cancelChanges;\n");
        wodFile.append("    inputName = \"DataAccess_CancelName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Import file upload
        wodFile.append("DataAccess_ImportFileUplod: WOFileUpload {\n");
        wodFile.append("    data = uploadedFile;");
        wodFile.append("    filePath = uploadedFileName;");
        wodFile.append("    size = \"60\";");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Delimiter selection
        wodFile.append("DataAccess_ImportDelimiterComma: WORadioButton {\n");
        wodFile.append("    selection = delimiter;");
        wodFile.append("    value = \"comma\";");
        wodFile.append("    name = \"DataAccess_ImportDelimiter\";");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportDelimiterTab: WORadioButton {\n");
        wodFile.append("    selection = delimiter;");
        wodFile.append("    value = \"tab\";");
        wodFile.append("    name = \"DataAccess_ImportDelimiter\";");
        wodFile.append("}\n");
        wodFile.append("\n");

        // Retain or discard existing data
        wodFile.append("DataAccess_ImportRetainDataYes: WORadioButton {\n");
        wodFile.append("    name = \"DataAccess_ImportRetainData\";");
        wodFile.append("    selection = retainExistingData;");
        wodFile.append("    value = true;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportRetainDataNo: WORadioButton {\n");
        wodFile.append("    name = \"DataAccess_ImportRetainData\";");
        wodFile.append("    selection = retainExistingData;");
        wodFile.append("    value = false;");        
        wodFile.append("}\n");
        wodFile.append("\n");

        // Display conditionals
        wodFile.append("DataAccess_IsImportingData: WOConditional {\n");
        wodFile.append("    condition = isImporting;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_IsViewingResults: WOConditional {\n");
        wodFile.append("    condition = isImporting;");
        wodFile.append("    negate = true;");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        // Import result messages
        wodFile.append("DataAccess_ImportNumberOfRecords: WOString {\n");
        wodFile.append("    value = numberOfRowsImported;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportErrorMessages: WORepetition {\n");
        wodFile.append("    item = anErrorMessage;");
        wodFile.append("    list = errorMessages;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportErrorMessage: WOString {\n");
        wodFile.append("    value = anErrorMessage;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportDidSucceed: WOConditional {\n");
        wodFile.append("    condition = didImportSucceed;");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_ImportDidFail: WOConditional {\n");
        wodFile.append("    condition = didImportFail;");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        return wodFile.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    //************** Binding Cover Methods **************\\



}

