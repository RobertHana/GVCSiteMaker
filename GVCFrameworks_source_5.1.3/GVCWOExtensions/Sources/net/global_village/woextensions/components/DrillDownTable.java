package net.global_village.woextensions.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * A component that displays information of an array of Enterprise Objects in a drill-down effect.
 *
 * <b>It provides the following functionalities/flexibilities:</b><br>
 * <ul>
 * <li>display data in a collapsible nested list (drill down hierarchy) based on which attributes are being displayed
 * <li>expand and collapse rows to show or hide detail data
 * <li>reorder columns when title is clicked, with order controlled by client
 * <li>perform specific client action when a row is clicked
 * <li>perform simple calculations
 * <li>allows client to control sort ordering of rows
 * <li>displays list in alternating colour rows and columns
 * </ul>
 *
 * <b>To use this component, the following bindings need to be setup:</b><br>
 * Required Bindings:
 * <ul>
 * <li>objectsToReport - the array of Enterprise Objects to be displayed in the component
 * <li>attributeKeyPaths - the ordered list of attribute key paths that determines the attributes to be displayed for each Enterprise Object
 * <li>attributesToAlwaysDisplay - the list of attribute key paths that needs to be displayed for each row
 * <li>comparators - the ordered list of compare method names that will be used for sorting its corresponding attribute key paths
 * <li>lastAttributeToExpand - tells the component the last attribute key path that can be expanded from the list of attributeKeyPaths
 * <li>titles - the ordered list of titles to be displayed as the titles for each column
 * <li>onAttributeClick - bind this to the client method to be called when an attribute or item is clicked.  This method is performed before the attribute is expanded or collapsed. In order for this method to be invoked properly, signature of method must be: <code>public WOComponent aMethod(String clickedAttribute, DataGroup aDataGroup)</code>
 * <li>onTitleClick - bind this to the client method to be called when a title is clicked. In order for this method to be invoked properly, signature of method must be: <code>public WOComponent aMethod(String clickedTitle)</code>
 * </ul>
 *
 * Note: It is assumed by the component that the list of titles, attributeKeyPaths and comparators are parallel with each column displayed.
 *
 * Optional Bindings:
 * <ul>
 * <li>titleStyle - bind to the Cascading Style Sheet (CSS) class to edit the display style for each title
 * <li>oddRowStyle - bind to the CSS class to edit the display style for all odd rows in the list
 * <li>evenRowStyle - bind to the CSS class to edit the display style for all even rows in the list
 * <li>oddColumnStyle - bind to the CSS class to edit the display style for all odd columns in the list
 * <li>evenColumnStyle - bind to the CSS class to edit the display style for all odd columns in the list
 * </ul>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class DrillDownTable extends net.global_village.woextensions.WOComponent
{
    protected NSArray currentData;
    protected NSArray currentAttributeKeyPaths;
    protected NSMutableArray dataGroups;
    public String aTitle;
    public String attributeKeyPath;
    protected DataGroup masterDataGroup;
    public DataGroup aDataGroup;
    public int rowNumber;
    public int columnNumber;
    protected boolean shouldExpandAll;


    /**
     * Designated constructor
     */
    public DrillDownTable(WOContext aContext)
    {
        super(aContext);
        currentData = new NSArray();
        dataGroups = new NSMutableArray();
        shouldExpandAll = false;
    }



    /**
     * This method is overridden to generate the initial data groups before this component is displayed.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        /** require
        [has_title_for_each_attribute] attributeKeyPaths().count() == titles().count();
        [has_comparator_for_each_attribute] attributeKeyPaths().count() == comparators().count(); **/

        //These conditions needs to be checked to avoid re-processing of the same data. 
        if ((! objectsToReport().isEqualToArray(currentData)) || (! attributeKeyPaths().isEqualToArray(currentAttributeKeyPaths)))
        {
            //Since masterDataGroup does not have any group attribute, then a dummy attribute must be added to attributeKeyPaths() and comparators() when creating it  
            String dummyAttribute = "$dummyAttribute%";

            NSMutableArray attributeKeyPathsWithDummy = new NSMutableArray(dummyAttribute);
            attributeKeyPathsWithDummy.addObjectsFromArray(attributeKeyPaths());

            NSMutableArray comparatorsWithDummy = new NSMutableArray(dummyAttribute);
            comparatorsWithDummy.addObjectsFromArray(comparators());

            setMasterDataGroup(new DataGroup(dummyAttribute,
                                             objectsToReport(),
                                             attributeKeyPathsWithDummy,
                                             comparatorsWithDummy));

            //Always show top-level
            masterDataGroup().toggleExpansion();

            currentData = objectsToReport();
            currentAttributeKeyPaths = attributeKeyPaths();
        }
        super.appendToResponse(aResponse, aContext);
    }


     
    /**
     * This method is overridden so variables are not synchronized with bindings.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Returns the objects containing the data to be displayed in the component.
     *
     * @return the objects containing the data to be displayed in the component.
     */
    public NSArray objectsToReport()
    {
        return (NSArray)valueForBinding("objectsToReport");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an array containing the key paths to be displayed from <code>objectsToReport<code>.  The order of the attributeKeyPaths in the array will be the order that it will be displayed.
     *
     * @return an array containing the key paths to be displayed from <code>objectsToReport<code>
     */
    public NSArray attributeKeyPaths()
    {
        return (NSArray)valueForBinding("attributeKeyPaths");

        /** ensure [valid_result] Result != null; [at_least_one_keypath] Result.count() > 0; **/
    }



    /**
     * Returns an array containing the titles to be displayed. The order of the titles in the array will be the order that it will be displayed and this should match the attributeKeyPath at the same index.
     *
     * @return an array containing the titles to be displayed.
     */
    public NSArray titles()
    {
        return (NSArray)valueForBinding("titles");

        /** ensure [valid_result] Result != null; [at_least_one_title] Result.count() > 0; **/
    }



    /**
     * Returns an array containing the compare selectors to be used for sorting a keyPath.  It is expected that the compare selector at a particular index will result in objects which are ordered in a sensible way with respect to the attributeKeyPath at the matching index.  If they do not match, more than one grouup with the same title will be created.
     *
     * @return an array containing the compare selectors
     */
    public NSArray comparators()
    {
        return (NSArray)valueForBinding("comparators");

        /** ensure [valid_result] Result != null; [at_least_one_comparator] Result.count() > 0; **/
    }



    /**
     * Returns an array containing the attributeKeyPaths to always display.
     *
     * @return an array containing the attributeKeyPaths to always display.
     */
    public NSArray attributesToAlwaysDisplay()
    {
        return (NSArray)valueForBinding("attributesToAlwaysDisplay");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the last attribute to expand from the <code>attributeKeyPath's</code> list
     *
     * @return last attribute to expand
     */
    public String lastAttributeToExpand()
    {
        return (String)valueForBinding("lastAttributeToExpand");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the method name to be invoked when a title is clicked. In order for this method to be invoked properly, signature of method must be: <code>public WOComponent aMethod(String clickedTitle)</code>
     *
     * @return the method name to be invoked when a title is clicked.
     */
    public String onTitleClick()
    {
        return (String)valueForBinding("onTitleClick");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the method name to be invoked when an attribute is clicked. In order for this method to be invoked properly, signature of method must be: <code>public WOComponent aMethod(String clickedAttribute, DataGroup aDataGroup)</code>
     *
     * @return the method name to be invoked when an attribute is clicked.
     */
    public String onAttributeClick()
    {
        return (String)valueForBinding("onAttributeClick");

        /** ensure [valid_result] Result != null; **/
    }

    

    public String titleStyle()
    {
        return (String)valueForBinding("titleStyle");
    }



    public String oddRowStyle()
    {
        return (String)valueForBinding("oddRowStyle");
    }



    public String evenRowStyle()
    {
        return (String)valueForBinding("evenRowStyle");
    }



    public String oddColumnStyle()
    {
        return (String)valueForBinding("oddColumnStyle");
    }



    public String evenColumnStyle()
    {
        return (String)valueForBinding("evenColumnStyle");
    }



    public boolean shouldExpandAll()
    {
        return booleanValueForBinding("shouldExpandAll", false);
    }



    /**
     * Returns the corresponding value of attributeKeyPath from aDataGroup.
     *
     * @return the corresponding value of attributeKeyPath from aDataGroup.
     */
    public Object currentAttribute()
    {
        Object theObject = null;

        if (attributeKeyPath.equals(aDataGroup.groupAttributeKeyPath()) || attributesToAlwaysDisplay().containsObject(attributeKeyPath))
        {
            theObject = currentAttributeValue();
        }
        return theObject;
    }



    /**
     * Returns the corresponding value of attributeKeyPath from aDataGroup.
     *
     * @return the corresponding value of attributeKeyPath from aDataGroup.
     */
    public Object currentAttributeValue()
    {
        Object theObject = aDataGroup.valueForKeyPath(attributeKeyPath);

        //In case valueForKeyPath() returns EONullValue.nullValue()
        if (theObject.equals(NSKeyValueCoding.NullValue))
        {
            theObject = null;
        }

        return theObject;
    }



    /**
     * Toggles the expansion of a data group.
     *
     * @return the parent's WOComponent
     */
    public com.webobjects.appserver.WOComponent toggleExpansion()
    {
        if (shouldExpandAll())
        {
            aDataGroup.expandAll();    
        }
        else
        {
            aDataGroup.toggleExpansion();            
        }

        return parent();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the data group that contains all the data groups to be displayed. 
     *
     * @return a DataGroup
     */
    public DataGroup masterDataGroup()
    {
        return masterDataGroup;

        /** ensure [valid_result] Result != null; **/
    }



    public void setMasterDataGroup(DataGroup newValue)
    {
        masterDataGroup = newValue;
    }



    /**
     * Returns <code>true</code> if value of <code>currentAttribute</code> is <code>null</code>, <code>false</code otherwise.
     *
     * @return <code>true</code> if value of <code>currentAttribute</code> is <code>null</code>, <code>false</code otherwise.
     */
    public boolean isBlank()
    {
        return (currentAttribute() == null);
    }



    /**
     * Invokes the method bound to <code>onTitleClick()</code>. 
     *
     * @return the WOComponent returned when method is invoked, it returns the parent component when the method invoked raises an exception.
     */
    public com.webobjects.appserver.WOComponent performTitleAction()
    {
        com.webobjects.appserver.WOComponent nextPage = parent().context().page();
        
        try
        {
            nextPage = (com.webobjects.appserver.WOComponent) NSSelector.invoke(onTitleClick(), String.class, parent(), aTitle);
        }
        catch (Exception e) {}

        return nextPage;
    }



    /**
     * Invokes the method bound to <code>onAttributeClick()</code>.
     *
     * @return the WOComponent returned when method is invoked, it returns the parent component when the method invoked raises an exception.
     */
    public com.webobjects.appserver.WOComponent performAttributeAction()
    {
        com.webobjects.appserver.WOComponent nextPage = parent().context().page();

        try
        {
            nextPage = (com.webobjects.appserver.WOComponent) NSSelector.invoke(onAttributeClick(),
                                                                                String.class,
                                                                                DataGroup.class,
                                                                                parent(),
                                                                                attributeKeyPath,
                                                                                aDataGroup);
        }
        catch (Exception e) {}

        //Only toggle expansion if the method onAttributeClick() returns the same page and is not the last attribute
        if ((nextPage.equals(parent().context().page())) &&
            (attributeKeyPaths().indexOfObject(attributeKeyPath) <= attributeKeyPaths().indexOfObject(lastAttributeToExpand())))
        {
            nextPage = toggleExpansion();
        }
        
        return nextPage;
    }



    /**
     * Returns the style sheet class for the column. <code>columnNumber</code> must be bound to the index of the WORepetition where this <code>columnStyle</code> will be used.
     *
     * @return the style sheet class for the column
     */
    public String columnStyle()
    {
        return (columnNumber % 2 == 0) ? evenColumnStyle() : oddColumnStyle();
    }



    /**
     * Returns the style sheet class for the row. <code>rowNumber</code> must be bound to the index of the WORepetition where this <code>rowStyle</code> will be used.
     *
     * @return the style sheet class for the row
     */
    public String rowStyle()
    {
        return (rowNumber % 2 == 0) ? evenRowStyle() : oddRowStyle();
    }

    

    /**
     * Returns the alignment to be used for the <code>currentAttribute</code>.
     *
     * @return the alignment to be used for the <code>currentAttribute</code>.
     */
    public String alignmentForAttribute()
    {
        return (currentAttribute() instanceof Number) ? "right" : "left";
    }
}
