/**
 * Implementation of DataAccessColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;
import net.global_village.virtualtables.*;


/**
 * DataAccessColumn wraps a Column (or VirtualColumn) and provides extra settings controlling how it is displayed and edited.
 */
public class DataAccessColumn extends _DataAccessColumn
{
    // Binding keys
    public static final String SHOW_FIELD_BINDINGKEY = "showField";
    public static final String IS_EDITABLE_BINDINGKEY = "isEditable";
    public static final String DEFAULT_COMPARISON_KEY = "defaultComparison";


    /**
     * Factory method to create new instances of DataAccessColumn.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessColumn or a subclass.
     */
    public static DataAccessColumn newDataAccessColumn()
    {
        return (DataAccessColumn) SMEOUtils.newInstanceOf("DataAccessColumn");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("DataAccessColumn");
        setShowField(true);
        setIsEditable(false);
        setDefaultComparison(DataAccessSearchCriterion.IS_EQUAL_TO_COMPARISON_TYPE);
        /** ensure [defaults_set] showField() && ! isEditable();  **/
    }



    /**
     * Returns a copy of an DataAccessColumn sub-class.  The copy is made manually as the relationship column needs to be copied.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this DataAccessColumn
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        EOEnterpriseObject copy = super.duplicate(copiedObjects);

        EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();

        if (Section.isSectionOnlyCopy(copiedObjects))
        {
            ((DataAccessColumn) copy).addObjectToBothSidesOfRelationshipWithKey(column(), "column");
        }
        else
        {
            EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("column"));
        }

        // Copying the column has the unfortunate side effect of calling setDefaultsForNewColumn in DataAccessMode and overwriting the copied value for showField().  The bindings are copied again here to recover from this.
        copy.takeValueForKey(bindings(), "bindings");

        return copy;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Sets the default value (if any) for this column into newRow.
     *
     * @param newRow - the EOEnterpriseObject to set the default value for
     */
    public void setDefaultFor(EOEnterpriseObject newRow)
    {
        /** require [valid_param] newRow != null;  **/
        newRow.takeValueForKey(defaultValue(), normalizedFieldName());
    }



    /**
     * Returns the default value (if any) for new fields of this column.  Returns null if there is no default value.
     *
     * @return the default value (if any) for new fields of this column or null if there is no default value.
     */
    public Object defaultValue()
    {
        return null;
    }



    /**
     * Returns the name of the column bound to this DataAccessColumn.
     *
     * @return the name of the column bound to this DataAccessColumn.
     */
    public String fieldName()
    {
        /** require [has_column] column() != null; **/
        return column().name();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the normalized name of the column bound to this DataAccessColumn.  This should be used for bindings in preference to column().normalizedName().  See nameInTemplate() for why.
     *
     * @return the normalized name of the column bound to this DataAccessColumn.
     */
    public String normalizedFieldName()
    {
        /** require [has_column] column() != null; **/
        return column().normalizedName();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the name to use for this column with a WebObject component in a template.  e.g. &lt;WebObject name=[nameInTemplate]&gt;.  This was added to separate this form normalizedFieldName() so that DataAccessRecordIDColumn could have one name in the UI/template (RecordID) and another name (virtualRowID) behind the scenes.
     *
     * @return the name to use for this column with a WebObject component in a template.
     */
    public String nameInTemplate()
    {
        /** require [has_column] column() != null; **/
        return column().normalizedName();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the data type of the column bound to this DataAccessColumn.
     *
     * @return the data type of the column bound to this DataAccessColumn.
     */
    public String fieldType()
    {
        /** require [has_column] column() != null; **/
        return column().type().textDescription();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the Table containing the Column bound to this DataAccessColumn.
     *
     * @return the Table containing the Column bound to this DataAccessColumn.
     */
    public Table databaseTable()
    {
        /** require
        [has_parents] (toParent() != null) && (toParent().toParent() != null);
        [parent_is_correct_type] (toParent().toParent() instanceof DataAccess);
        **/
        return ((DataAccessMode)toParent()).databaseTable();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the Table containing the Column bound to this DataAccessColumn.
     *
     * @return the Table containing the Column bound to this DataAccessColumn.
     */
    public DataAccessMode parentMode()
    {
        /** require [has_parents] toParent() != null;
        [parent_is_correct_type] (toParent() instanceof DataAccessMode);   **/
        return (DataAccessMode)toParent();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Returns the DataAccess containing the Column bound to this DataAccessColumn.
     *
     * @return the DataAccess containing the Column bound to this DataAccessColumn.
     */
    public DataAccess dataAccessComponent()
    {
        /** require
        [has_parents] (toParent() != null) && (parentMode().toParent() != null);
        [parent_is_correct_type] (parentMode().toParent() instanceof DataAccess);
        **/
        return (DataAccess)parentMode().toParent();
        /** ensure [result_valid] Result != null; **/
    }



    /**
     * Sets the Column this DataAccessColumn is bound to.
     *
     * @param aColumn - the Column this DataAccessColumn is bound to.
     */
    public void setColumn(Column aColumn)
    {
        /** require [column_is_from_table] aColumn == null || databaseTable().columns().containsObject(aColumn); **/

        if (column() != aColumn)
        {
            super.setColumn(aColumn);

            if (aColumn != null)
            {
                ((DataAccessMode)toParent()).setDefaultsForNewColumn(this);
            }
        }
    }



    /**
     * Returns <code>true</code> if the associated Column contains an editable value
     *
     * @return <code>true</code> if the associated Column contains an editable value
     */
    public boolean isEditableColumn()
    {
        /** require [has_column] column() != null;  **/
        return column().isEditableColumn().booleanValue();
    }



    /**
     * Returns <code>true</code> if the associated Column is a system column.
     *
     * @return <code>true</code> if the associated Column is a system column.
     */
    public boolean isSystemColumn()
    {
        /** require [has_column] column() != null;  **/
        return column().isSystemColumn().booleanValue();
    }



    /**
     * Returns <code>true</code> if data in this column can be edited in this mode and it is not a system column (which are never editable).  This should not be confused with isEditable (which records the user's preferences for non-system column) or canEdit on PageComponent (which is used internally by the Component Primitives).
     *
     * @return <code>true</code> if data in this column can be edited in this mode and it is not a system column (which are never editable).
     */
    public boolean canEditValue()
    {
        return isEditable() && ! isSystemColumn();
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

        if (column() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("DataAccessColumn is missing column"));
        }
        else if (! databaseTable().columns().containsObject(column()))
        {
            exceptions.addObject(new NSValidation.ValidationException("DataAccessColumn's column is from foreign table."));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }

    }



    //************** Binding Cover Methods **************\\

    /**
     * Returns <code>true</code> if this column should be displayed in this mode.
     *
     * @return <code>true</code> if this column should be displayed in this mode.
     */
    public boolean showField()
    {
        return booleanValueForBindingKey(SHOW_FIELD_BINDINGKEY);
    }

    /**
     * Sets whether this column should be displayed in this mode.
     *
     * @param booleanValue - <code>true</code> if this column should be displayed in this mode, <code>false</code> if it should not be displayed.
     */
    public void setShowField(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, SHOW_FIELD_BINDINGKEY);
    }



    /**
     * Returns <code>true</code> if the user wishes to allow data in this column to be edited in this mode.  This is not applicable to system columns.  This should not be confused with canEditValue (which should be used when determining if the value can actually be edited as it also handles system columns) or canEdit on PageComponent (which is used internally by the Component Primitives).
     *
     * @return <code>true</code> if the user wishes to allow data in this column to be edited in this mode.
     */
    public boolean isEditable()
    {
        return booleanValueForBindingKey(IS_EDITABLE_BINDINGKEY);
    }

    /**
     * Sets whether the user wishes to allow data in this column to be edited in this mode.
     *
     * @param booleanValue - <code>true</code> if  data in this column can be edited in this mode, <code>false</code> if it should not be displayed.
     */
    public void setIsEditable(boolean booleanValue)
    {
        setBooleanValueForBindingKey(booleanValue, IS_EDITABLE_BINDINGKEY);
    }


    /**
     * Returns the default comparison for this column in user searches
     *
     * @return the default comparison for this column in user searches
     */
    public String defaultComparison()
    {
        return (String)valueForBindingKey(DEFAULT_COMPARISON_KEY);
        /** ensure [valid_result] Result != null;
                   [valid_comparison] DataAccessSearchCriterion.COMPARISON_TYPES.containsObject(Result);   **/
    }



    /**
     * Sets the default comparison for this column in user searches
     *
     * @param newComparison the default comparison for this column in user searches
     */
    public void setDefaultComparison(String newComparison)
    {
        /** require [valid_param] newComparison != null;
                    [valid_comparison] DataAccessSearchCriterion.COMPARISON_TYPES.containsObject(newComparison);
         **/

        setBindingForKey(newComparison, DEFAULT_COMPARISON_KEY);

        /** ensure [type_set] defaultComparison().equals(newComparison);                     **/
    }




    /**
     * Returns the order of this component with respect to its parentMode's ordering
     *
     * @param the order of this component with respect to its parentMode's ordering
     */
    public int componentOrder()
    {
        /** require [is_part_of_components] parentMode().orderedComponents().containsObject(this);  **/

        return parentMode().orderedComponents().indexOfObject(this);
    }

}

