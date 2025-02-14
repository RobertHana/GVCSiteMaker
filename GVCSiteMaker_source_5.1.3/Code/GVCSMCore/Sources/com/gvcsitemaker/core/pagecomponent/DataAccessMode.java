/**
 * Implementation of DataAccessMode common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.virtualtables.*;


/**
 * DataAccessMode represents one of the four modes of a DataAccess PageComponent: Single Record, List of Records, Add Record, and Search for Records.
 */
public class DataAccessMode extends _DataAccessMode
{

    // Data Access Modes
    public static final String SINGLE_MODE = "single";
    public static final String LIST_MODE = "list";
    public static final String ADD_MODE = "add";
    public static final String SEARCH_MODE = "search";
    public static final String IMPORT_MODE = "import";

    // Binding keys
    public static final String USE_CUSTOM_TEMPLATE_BINDINGKEY = "useCustomTemplate";


    // Cacheable values
    protected NSMutableDictionary columnsByName = null;


    /**
     * Factory method to create new instances of DataAccessMode.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessMode or a subclass.
     */
    public static DataAccessMode newDataAccessMode()
    {
        return (DataAccessMode) SMEOUtils.newInstanceOf("DataAccessMode");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the passed mode name is valid.
     *
     * @param modeName - the of the mode to check for validity.
     * @return <code>true</code> if the passed mode name is valid.
     */
    public static boolean isValidMode(String modeName)
    {
        return (modeName != null && (modeName.equals(SINGLE_MODE) ||
                                     modeName.equals(LIST_MODE) ||
                                     modeName.equals(ADD_MODE) ||
                                     modeName.equals(SEARCH_MODE)) ||
                                     modeName.equals(IMPORT_MODE));
    }



    /**
     * Overriden to supply default binding values.
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessMode");
        setUseCustomTemplate(false);
    }




    /**
     * Returns a copy of an DataAccessMode sub-class.  The copy is made manually as customTemplate needs to be added to the copy.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this DataAccessMode
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = super.duplicate(copiedObjects);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("customTemplate"));

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Clears any cached values in this mode.  Call this when toChildren changes.
     */
    public void clearCachedValues()
    {
        super.clearCachedValues();
        columnsByName = null;
    }



    /**
     * Returns the Table that this mode is displaying data for.
     *
     * @return the Table that this mode is displaying data for.
     */
    public Table databaseTable()
    {
        /** require
        [has_parent] toParent() != null;
        [parent_is_correct_type] (toParent() instanceof DataAccess);
        [parent_has_table] dataAccessComponent().databaseTable() != null;
        **/

        return dataAccessComponent().databaseTable();

        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the DataAccess PageComponent that contains this DataAccessMode.
     *
     * @return the DataAccess PageComponent that contains this DataAccessMode
     */
    public DataAccess dataAccessComponent()
    {
        /** require
        [has_parents] toParent() != null;
        [parent_is_correct_type] toParent() instanceof DataAccess;
        **/
        return (DataAccess)toParent();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this mode is the named mode.
     *
     * @param modeName - the name of the mode to compare to this mode
     * @return <code>true</code> if this mode is the named mode.
     */
    public boolean isMode(String modeName)
    {
        /** require [valid_mode] isValidMode(modeName);  **/
        return modeName.equals(mode());
    }



    /**
     * Returns the symbolic name of this mode this mode.
     *
     * @return the symbolic name of this mode this mode.
     */
    public String mode()
    {
        throw new RuntimeException("Subclass responsibility");
    }



    /**
     * Returns the HTML WOComponent template to use for this mode.
     *
     * @return the HTML WOComponent template to use for this mode.
     */
    public String template()
    {
        /** require [has_custom_template_if_needed] ( ! useCustomTemplate()) || (customTemplate() != null);  **/
        String template = useCustomTemplate() ? customTemplate().html() : defaultTemplate();

        // These tags are added manually to reduce the potential for user error and to allow DA sections to
        // generate formless output
        if (hasEditableFields() || isEditable())
        {
            template = "  <WebObject name=DataAccess_Form>\n" + template + "</WebObject name=DataAccess_Form>\n";
        }

        template = template.intern();  // Try to save some memory.

        return template;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the default HTML WOComponent template for this mode.
     *
     * @return the default HTML WOComponent template for this mode.
     */
    public String defaultTemplate()
    {
        return SMApplication.appProperties().stringPropertyForKey("DA_" + mode() + "ModeDefaultTemplate");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the default custom HTML WOComponent template for this mode.
     *
     * @return the default custom HTML WOComponent template for this mode.
     */
    public String defaultCustomTemplate()
    {
        throw new RuntimeException("sub-class responsibility");
    }



    /**
     * Creates a custom template if one does not already exist.  Sets the HTML for the template,
     * turns useCustomTemplate on.
     *
     * @param htmlString - optional string to use for the HTML of the template.
     */
    protected void createCustomTemplate(String htmlString)
    {
        /** require [valid_param] htmlString != null;  **/

        if (customTemplate() == null)
        {
            DataAccessModeTemplate template = DataAccessModeTemplate.newDataAccessModeTemplate();
            editingContext().insertObject(template);
            addObjectToBothSidesOfRelationshipWithKey(template, "customTemplate");
        }
        customTemplate().setHtml(htmlString);
        setUseCustomTemplate(true);

        /** ensure [has_custom_template] customTemplate() != null;
                   [template_has_layout] htmlString.equals(customTemplate().html());
                   [is_using_custom_template] useCustomTemplate();  **/
    }



    /**
     * Deletes the custom template if one exists and sets this mode to use the default template.
     */
    public void deleteCustomTemplate()
    {
        if (customTemplate() != null)
        {
            setUseCustomTemplate(false);
            removeObjectFromBothSidesOfRelationshipWithKey(customTemplate(), "customTemplate");
            setCustomTemplate(null);
        }

        /** ensure [use_default_template] ! useCustomTemplate();  [no_custom_template] customTemplate() == null; **/
    }



    /**
     * Returns the HTML content for the Custom template as an NSData object.  The defaultCustomTemplate() is returned if one has not been created.  This does not affect the status of useCustomTemplate().
     *
     * @return the HTML content for the Custom template.
     */
    public NSData customTemplateHtml()
    {
        String templateString = (customTemplate() != null) ? customTemplate().html() : defaultCustomTemplate();
        return new NSData(templateString.getBytes());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Set the HTML content of the Custom template from an NSData object.  The Custom template is created if it does not exist.  This does not affect the status of useCustomTemplate().
     *
     * @param newUploadedTemplate - the new HTML content of the Custom template.
     * @exception NSValidation.ValidationException - if the template is not valid for the bindings
     */
    public void setCustomTemplateHtml(NSData newTemplate) throws NSValidation.ValidationException
    {
        /** require [valid_param] newTemplate != null;  [no_empty_data] newTemplate.length() > 0;  **/
        String templateAsString = new String(newTemplate.bytes());
        HtmlTemplateUtils.validateTemplateWithBindings(templateAsString,
                                                       generatedBindings(),
                                                       NSArray.EmptyArray);
        createCustomTemplate(templateAsString);
        /** ensure [custom_template_created] customTemplate() != null;  [template_set] true;   **/
    }



    /**
     * Returns the file name to use for the HTML of this mode's customTemplate.
     *
     * @return the file name to use for the HTML of this mode's customTemplate.
     */
    public String templateFileName()
    {
        /** require [in_section] sectionUsedIn() != null;  **/
        return mode() + "_form.html";
        /** ensure [valid_result] Result != null;  **/

    }



    /**
     * Returns the WOD file bindings to use with this mode.  Sub-classes must implement this to return bindings for both the default and custom template.
     *
     * @return the WOD file bindings to use with this mode.
     */
    public String generatedBindings()
    {
        throw new RuntimeException("Sub-class responsibility not handled by " + getClass());
    }



    /**
     * Adds the bindings common to all Data Access modes to the end of passed StringBuffer.
     *
     * @param wodFile - the StringBuffer to add the bindings to
     */
    protected void addCommonBindings(StringBuffer wodFile)
    {
        /** require [valid_param] wodFile != null;  **/

        wodFile.append("DataAccess_Form: WOForm {\n");
        wodFile.append("    enctype = \"multipart/form-data\";\n");
        wodFile.append("    multipleSubmit = true;\n");
        wodFile.append("    action = saveChanges;\n");
        wodFile.append("    name = \"DataAccess_Form\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_TableName: WOString {\n");
        wodFile.append("    value = modeComponent.toParent.databaseTable.name;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CurrentUser: WOString {\n");
        wodFile.append("    value = session.currentUser.userID;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SiteID: WOString {\n");
        wodFile.append("    value = componentObject.sectionUsedIn.website.siteID;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SectionName: WOString {\n");
        wodFile.append("    value = componentObject.sectionUsedIn.name;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_OrgUnit: WOString {\n");
        wodFile.append("    value = componentObject.sectionUsedIn.website.parentOrgUnit.unitName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_TableHasData: WOConditional {\n");
        wodFile.append("    condition = dataAccessParameters.hasRows;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_TableIsEmpty: WOConditional {\n");
        wodFile.append("    condition = dataAccessParameters.hasRows;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CanEditRecords: WOConditional {\n");
        wodFile.append("    condition = hasEditableFields;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CanBrowseOrSearchRecords: WOConditional {\n");
        wodFile.append("    condition = canBrowseOrSearchRecords;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SaveButton: WOSubmitButton {\n");
        wodFile.append("    action = saveChanges;\n");
        wodFile.append("    value = \"Save\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        wodFile.append("DataAccess_SaveName: SubmitName {\n");
        wodFile.append("    action = saveChanges;\n");
        wodFile.append("    inputName = \"DataAccess_SaveName\";\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ShouldNotSendNotification: WOCheckBox {\n");
        wodFile.append("    checked = shouldNotSendNotification;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ShowNotificationNotNeededCheckBox: WOConditional {\n");
        wodFile.append("    condition = showNotificationNotNeededCheckBox;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ListView: WOHyperlink {\n");
        wodFile.append("    href = listModeUrl;\n");
        wodFile.append("    disabled = modeComponent.cannotBrowseOrSearchRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SingleView: WOHyperlink {\n");
        wodFile.append("    href = singleModeUrl;\n");
        wodFile.append("    disabled = modeComponent.cannotBrowseOrSearchRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ToggleView: WOHyperlink {\n");
        wodFile.append("    href = toggleModeUrl;\n");
        wodFile.append("    disabled = modeComponent.cannotBrowseOrSearchRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_SearchForRecords: WOHyperlink {\n");
        wodFile.append("    href = searchForRecordsUrl;\n");
        wodFile.append("    disabled = modeComponent.cannotBrowseOrSearchRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ShowAllRecords: WOHyperlink {\n");
        wodFile.append("    href = showAllRecordsUrl;\n");
        wodFile.append("    disabled = modeComponent.cannotBrowseOrSearchRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_CanImportRecords: WOConditional {\n");
        wodFile.append("    condition = canImportRecords;\n");
        wodFile.append("    negate = false;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_ImportRecords: WOHyperlink {\n");
        wodFile.append("    href = importRecordsUrl;\n");
        wodFile.append("    disabled = cannotImportRecords;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

    }



    /**
     * Adds the bindings for the tags in addDataAccessActions to the end of passed StringBuffer.
     *
     * @param wodFile - the StringBuffer to add the bindings to
     * @param fields - NSArray of DataAccessColumn giving the fields to add bindings for
     */
    protected void addFieldBindings(StringBuffer wodFile, NSArray fields)
    {
        /** require [valid_wodFile] wodFile != null;  [valid_fields] fields != null;  **/

        // For default / standard templates
        wodFile.append("DataAccess_FieldName: WOString {\n");
        wodFile.append("    value = aDataAccessColumn.fieldName;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_FieldValue: WOSwitchComponent {\n");
        wodFile.append("    currentMode = \"display\";\n");
        wodFile.append("    field = field;\n");
        wodFile.append("    fieldValue = fieldValue;\n");
        wodFile.append("    index = index;\n");
        wodFile.append("    dataAccessParameters = dataAccessParameters;\n");
        wodFile.append("    componentObject = aDataAccessColumn;\n");
        wodFile.append("    WOComponentName = aDataAccessColumn.componentType;\n");
        wodFile.append("    currentDataAccessMode = componentObject.mode;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_FieldIsEmpty: WOConditional {\n");
        wodFile.append("    condition = fieldValue;\n");
        wodFile.append("    negate = true;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        wodFile.append("DataAccess_FieldIsNotEmpty: WOConditional {\n");
        wodFile.append("    condition = fieldValue;\n");
        wodFile.append("}\n");
        wodFile.append("\n");

        // For custom templates
        Enumeration componentEnum = fields.objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            wodFile.append(aComponent.nameInTemplate());
            wodFile.append(": WOSwitchComponent {\n");
            wodFile.append("    currentMode = \"display\";\n");
            wodFile.append("    dataAccessParameters = dataAccessParameters;\n");
            wodFile.append("    index = index;\n");
            wodFile.append("    fieldValue = currentRow." + aComponent.normalizedFieldName() + ";\n");
            wodFile.append("    field = fieldNamed." + aComponent.normalizedFieldName() + ";\n");

            // Don't use aComponent.normalizedFieldName() for this as this need to look up column name not internal name
            wodFile.append("    componentObject = modeComponent.columnsByName." + aComponent.column().normalizedName() + ";\n");
            wodFile.append("    WOComponentName = \"" + aComponent.componentType() + "\";\n");
            wodFile.append("    currentDataAccessMode = componentObject.mode;\n");
            wodFile.append("}\n");
            wodFile.append("\n");


            // Same as above, but has xmlEncodingOff = true
            wodFile.append(aComponent.nameInTemplate() + "_XMLEncodingOff");
            wodFile.append(": WOSwitchComponent {\n");
            wodFile.append("    currentMode = \"display\";\n");
            wodFile.append("    xmlEncodingOff = true;\n");
            wodFile.append("    dataAccessParameters = dataAccessParameters;\n");
            wodFile.append("    index = index;\n");
            wodFile.append("    fieldValue = currentRow." + aComponent.normalizedFieldName() + ";\n");
            wodFile.append("    field = fieldNamed." + aComponent.normalizedFieldName() + ";\n");

            // Don't use aComponent.normalizedFieldName() for this as this need to look up column name not internal name
            wodFile.append("    componentObject = modeComponent.columnsByName." + aComponent.column().normalizedName() + ";\n");
            wodFile.append("    WOComponentName = \"" + aComponent.componentType() + "\";\n");
            wodFile.append("    currentDataAccessMode = componentObject.mode;\n");
            wodFile.append("}\n");
            wodFile.append("\n");


            wodFile.append(aComponent.nameInTemplate() + "_IsEmpty: WOConditional {\n");
            wodFile.append("    condition = currentRow." + aComponent.normalizedFieldName() + ";\n");
            wodFile.append("    negate = true;\n");
            wodFile.append("}\n");
            wodFile.append("\n");

            wodFile.append(aComponent.nameInTemplate() + "_IsNotEmpty: WOConditional {\n");
            wodFile.append("    condition = currentRow." + aComponent.normalizedFieldName() + ";\n");
            wodFile.append("}\n");
            wodFile.append("\n");


        }
    }



    /**
     * Returns the list of children (DataAccessColumn) which are visible, in their set order.  This should be used for display.
     *
     * @return the list of children (DataAccessColumn) which are visible, in their set order.
     */
    public NSArray visibleFields()
    {
        NSMutableArray visibleFields = new NSMutableArray();
        Enumeration componentEnum = orderedComponents().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            if (aComponent.showField())
            {
                visibleFields.addObject(aComponent);
            }
        }

        return visibleFields;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the list of children (DataAccessColumn) which are editable, in their set order.
     *
     * @return the list of children (DataAccessColumn) which are editable, in their set order.
     */
    public NSArray editableFields()
    {
        NSMutableArray editableFields = new NSMutableArray();
        Enumeration componentEnum = orderedComponents().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            if (aComponent.canEditValue() && aComponent.showField())
            {
                editableFields.addObject(aComponent);
            }
        }

        return editableFields;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if any of the children (DataAccessColumn) are editable.
     *
     * @return <code>true</code> if any of the children (DataAccessColumn) are editable.
     */
    public boolean hasEditableFields()
    {
        boolean hasEditableFields = false;

        Enumeration componentEnum = toChildren().objectEnumerator();
        while (componentEnum.hasMoreElements() && ! hasEditableFields)
        {
            DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();

            if (aComponent.canEditValue() && aComponent.showField())
            {
                hasEditableFields = true;
            }
        }

        return hasEditableFields;
    }



    /**
     * Returns a dictionary of the DataAccessColumn children keyed on their Column's normalized name.
     *
     * @return a dictionary of the DataAccessColumn children keyed on their Column's normalized name.
     */
    public NSDictionary columnsByName()
    {
        if (isFault() || (columnsByName == null))
        {
            columnsByName = new NSMutableDictionary(toChildren().count());
            Enumeration componentEnum = toChildren().objectEnumerator();
            while (componentEnum.hasMoreElements())
            {
                DataAccessColumn aComponent = (DataAccessColumn) componentEnum.nextElement();
                columnsByName.setObjectForKey(aComponent, aComponent.column().normalizedName());
            }
        }

        return columnsByName;

        /** ensure [valid_result] Result != null;  [has_all_columns] Result.count() == toChildren().count();  **/
    }



    /**
     * Overridden to clear cached values.
     */
    public void addChild(PageComponent aComponent)
    {
        super.addChild(aComponent);
        clearCachedValues();
    }



    /**
     * Sets the defaults when a new DataAccessColumn's database Column is set.
     *
     * @param dataAccessColumn - the column to set defaults for.
     */
    public void setDefaultsForNewColumn(DataAccessColumn dataAccessColumn)
    {
        /** require [valid_param] dataAccessColumn != null;  **/
        dataAccessColumn.setShowField(! dataAccessColumn.isSystemColumn());
    }



    /**
     * Overridden to clear cached values.
     */
    public void removeChild(PageComponent aComponent)
    {
        super.removeChild(aComponent);
        clearCachedValues();
    }



    /**
     * Returns the DataAccessColumn from this mode's children that corresponds to normalizedName.
     *
     * @param name the name of the Column to return the corresponding DataAccessColumn for
     * @return the DataAccessColumn from this mode's children that corresponds to normalizedName
     */
    public DataAccessColumn dataAccessColumnForColumnNamed(String name)
    {
        /** require
        [valid_param] name != null;
        [has_column] hasDataAccessColumnForColumnNamed(name); **/

        String normalizedName = Column.normalizeStringForColumnNames(name);
        return (DataAccessColumn) columnsByName().objectForKey(normalizedName);

        /** ensure
        [valid_result] Result != null;
        [column_matches] Result.column().normalizedName().equals(Column.normalizeStringForColumnNames(name)); **/
    }



    /**
     * Returns the DataAccessColumn from this modes children that corresponds to aColumn, or null if there is no corresponding child.
     *
     * @param aColumn the Column to return the corresponding DataAccessColumn for
     * @return the DataAccessColumn from this modes children that corresponds to aColumn, or null if there is no corresponding child
     */
    public DataAccessColumn dataAccessColumnForColumn(Column aColumn)
    {
        /** require [valid_param] aColumn != null; [has_column] hasDataAccessColumnForColumn(aColumn); **/
        return dataAccessColumnForColumnNamed(aColumn.normalizedName());
        /** ensure [valid_result] Result != null; [column_matches] Result.column() == aColumn; **/
    }



    /**
     * Returns <code>true</code> if there is a DataAccessColumn in this mode's children that corresponds to the given name, or <code>false</code> if there is no corresponding child.
     *
     * @param name the name of the Column to check for a corresponding DataAccessColumn
     * @return <code>true</code> if there is a DataAccessColumn in this mode's children that corresponds to name, or <code>false</code> if there is no corresponding child
     */
    public boolean hasDataAccessColumnForColumnNamed(String name)
    {
        /** require [valid_param] name != null; **/
        String normalizedName = Column.normalizeStringForColumnNames(name);
        return columnsByName().objectForKey(normalizedName) != null;
    }



    /**
     * Returns <code>true</code> if there is a DataAccessColumn in this mode's children that corresponds to aColumn, or <code>false</code> if there is no corresponding child.
     *
     * @param aColumn the Column to check for a corresponding DataAccessColumn
     * @return <code>true</code> if there is a DataAccessColumn in this mode's children that corresponds to aColumn, or <code>false</code> if there is no corresponding child
     */
    public boolean hasDataAccessColumnForColumn(Column aColumn)
    {
        /** require [valid_param] aColumn != null; **/
        return hasDataAccessColumnForColumnNamed(aColumn.normalizedName());
    }



    /**
     * Synchronizes the DataAccessColumns in this mode (toChildred()) with the Columns in databaseTable(): creates a DataAccessColumn for each Column in databaseTable() and which does not already have a corresponding DataAccessColumn in this mode and removes any DataAccessColumns which do not have a corresponding Column in databaseTable().
     */
    public void synchronizeColumnsWithTable()
    {
        /** require [has_table] databaseTable() != null;  **/

        // Don't synch based on cached info!
        clearCachedValues();

        addColumns(databaseTable().userColumns());
        addColumns(databaseTable().systemColumns());
        removeColumnsNotIn(databaseTable().columns());

        /** ensure
        [all_columns_captured] toChildren().count() == databaseTable().columns().count();
        [each_column_added] (forall i : {0 .. databaseTable().columns().count() - 1} # 		hasDataAccessColumnForColumn((Column)databaseTable().columns().objectAtIndex(i)));
        [extra_columns_removed] (forall i : {0 .. toChildren().count() - 1} # databaseTable().columns().containsObject(((DataAccessColumn) toChildren().objectAtIndex(i)).column()));
        **/
    }



    /**
     * Creates a DataAccessColumn for each Column in tableColumns that does not already have a corresponding DataAccessColumn, and adds it as a child of this component.
     *
     * @param tableColumns - array of Columns to create DataAccessColumn for.
     */
    protected void addColumns(NSArray tableColumns)
    {
        /** require [has_array] tableColumns != null;  **/

        Enumeration columnEnumerator = tableColumns.objectEnumerator();
        while (columnEnumerator.hasMoreElements())
        {
            Column aColumn = (Column) columnEnumerator.nextElement();

            if ( ! hasDataAccessColumnForColumn(aColumn))
            {
                addColumn(aColumn);
            }
        }

        /** ensure [each_column_added] (forall i : {0 .. tableColumns.count() - 1} # hasDataAccessColumnForColumn((Column)tableColumns.objectAtIndex(i)));  **/
    }



    /**
     * Creates a DataAccessColumn for the passed Column and adds it as a child of this component..
     *
     * @param aColumn - the Column to create DataAccessColumn for.
     */
    protected void addColumn(Column aColumn)
    {
        /** require
        [valid_param] aColumn != null;
        [column_in_table] databaseTable().columns().containsObject(aColumn);
        [column_does_not_exist] ! hasDataAccessColumnForColumn(aColumn);
        **/

        DebugOut.println(30, "Adding column " + aColumn.name() + " of type " + aColumn.type().name());

        String dataAccessColumnName = "DataAccess" + aColumn.type().name() +  "Column";
        DataAccessColumn dataAccessColumn = (DataAccessColumn) SMEOUtils.newInstanceOf(dataAccessColumnName);
        addChild(dataAccessColumn);
        dataAccessColumn.setColumn(aColumn);

        /** ensure [column_added] hasDataAccessColumnForColumn(aColumn);  **/
    }



    /**
     * Removes any children for which a Column does not exist in tableColumns
     *
     * @param tableColumns - array of Columns to compare to children
     */
    protected void removeColumnsNotIn(NSArray tableColumns)
    {
        /** require [has_array] tableColumns != null;  **/

        Enumeration childEnumerator = new NSArray(toChildren()).objectEnumerator();
        while (childEnumerator.hasMoreElements())
        {
            DataAccessColumn aChild = (DataAccessColumn) childEnumerator.nextElement();

            if ( ! tableColumns.containsObject(aChild.column()))
            {
                removeChild(aChild);
            }
        }
        /** ensure [extra_columns_removed] (forall i : {0 .. toChildren().count() - 1} # tableColumns.containsObject(((DataAccessColumn) toChildren().objectAtIndex(i)).column()));  **/
    }



    /**
     * Returns <code>true</code> if users can browse through the records in this DataAccess mode.
     *
     * @return <code>true</code> if users can browse through the records in this DataAccess mode.
     */
    public boolean canBrowseOrSearchRecords()
    {
        return dataAccessComponent().canBrowseOrSearchRecords();
    }



    /**
     * Returns <code>true</code> if users cannot browse through the records in this DataAccess mode.
     *
     * @return <code>true</code> if users cannot browse through the records in this DataAccess mode.
     */
    public boolean cannotBrowseOrSearchRecords()
    {
        return ! canBrowseOrSearchRecords();
    }



    /**
     * Returns <code>true</code> if users can add new records in this DataAccess mode.
     *
     * @return <code>true</code> if users can add new records in this DataAccess mode.
     */
    public boolean canAddRecords()
    {
        return dataAccessComponent().canAddRecords();
    }



    /**
     * Returns <code>true</code> if users can import records in this DataAccess mode.
     *
     * @return <code>true</code> if users can import records in this DataAccess mode.
     */
    public boolean canImportRecords()
    {
        return dataAccessComponent().canImportRecords();
    }



    /**
     * Returns <code>true</code> if users cannot add new records in this DataAccess mode.
     *
     * @return <code>true</code> if users cannot add new records in this DataAccess mode.
     */
    public boolean cannotAddRecords()
    {
        return ! canAddRecords();
    }



    /**
     * Returns <code>true</code> if users can delete records from this DataAccess mode.
     *
     * @return <code>true</code> if users can delete new records from this DataAccess mode.
     */
    public boolean canDeleteRecords()
    {
        return dataAccessComponent().canDeleteRecords();
    }



    /**
     * Returns <code>true</code> if users cannot delete records from this DataAccess mode.
     *
     * @return <code>true</code> if users cannot delete new records from this DataAccess mode.
     */
    public boolean cannotDeleteRecords()
    {
        return ! canDeleteRecords();
    }



    /**
     * Returns <code>true</code> if anything in this mode will allow edits to the underlying data.
     *
     * @return <code>true</code> if anything in this mode will allow edits to the underlying data.
     */
    public boolean isEditable()
    {
        return canDeleteRecords() || hasEditableFields();
    }



    /**
     * Returns <code>true</code> if this mode uses component actions (e.g. form submits).  The display layer (Component Primitives) may need to handle such modes specially.
     *
     * @return <code>true</code> this mode uses component actions (e.g. form submits)
     */
    public boolean usesComponentActions()
    {
        // By default all editable modes use component actions
        return isEditable();
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

        /* Can't do this until save is not done midstream while creating Section.
        if (toChildren().count() < 1)
        {
            exceptions.addObject(new NSValidation.ValidationException(getClass() + " is missing children"));
        }
        */

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\

    /**
     * Returns <code>true</code> if this mode will use the custom template, rather than the default one.
     *
     * @return <code>true</code> if this mode will use the custom template, rather than the default one.
     */
    public boolean useCustomTemplate()
    {
        return booleanValueForBindingKey(USE_CUSTOM_TEMPLATE_BINDINGKEY);
    }

    /**
     * Sets whether this mode will use the custom template or the default one.
     *
     * @param booleanValue - <code>true</code> if this mode will use the custom template, <code>false</code> if this mode will use the default template.
     */
    public void setUseCustomTemplate(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, USE_CUSTOM_TEMPLATE_BINDINGKEY);
        if (useCustomTemplate() && (customTemplate() == null))
        {
            createCustomTemplate(defaultCustomTemplate());
        }

        /** ensure [has_custom_template_if_needed] ( ! useCustomTemplate()) || (customTemplate() != null);  **/
    }


}

