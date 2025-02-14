// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.components;

import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.databasetables.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * Wrapper for template engine to generate the body of Data Access
 * notification messages.
 */
public class DANotificationMessage extends TemplatedTextGenerator
{
    public VirtualColumn aColumn;
    protected NSTimestampFormatter dateFormatter;
    protected DataAccess dataAccessComponent;


    /**
     * This is the method to use to generate the body for a website creation message.
     * 
     * @param aWebsite the Website that was created
     * @param aTemplate the template defining the message to be produced
     * 
     * @return the message produced from aWebsite and aTemplate
     */
    public static String messageFor(DataAccessParameters dataAccessParameters, String aTemplate)
    {
        /** require [has_dataAccessParameters] dataAccessParameters != null;                     **/

        return messageFor("DANotificationMessage", dataAccessParameters, aTemplate);

        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Validates aTemplate to ensure that it is valid for the current bindings.
     * 
     * @param dataAccess the DataAccess page component that provides the column definitions to be
     * used when validating
     * @param aTemplate the template to be validated
     * 
     * @throws NSValidation.ValidationException if the template is not valid
     */
    public static void validateTemplate(DataAccess dataAccess, String aTemplate) throws NSValidation.ValidationException
    {
        /** require [has_dataAccess] dataAccess != null;                     **/

        DANotificationMessage messageEngine = (DANotificationMessage) newInstance("DANotificationMessage", null, aTemplate);
        messageEngine.setDataAccessComponent(dataAccess);
        messageEngine.validateTemplate();
    }



    /**
     * Designated constructor
     */
    public DANotificationMessage(WOContext aContext)
    {
        super(aContext);
        dateFormatter = SMApplication.appProperties().timestampFormatterForKey("DateAndTimeFormatter");
    }



    /**
     * Returns an ordered list of colums in the associated VirtualTable.  This is used to order and
     * control what appears in the message body.  This gets bound to an WORepetition with aColumn
     * as the item.
     *  
     * @return ordered list of colums in the associated VirtualTable
     */
    public NSArray columns()
    {
        /** require [valid_row] row() != null;          **/
        return row().table().columnsOrderedBy(new SMVirtualTable.DefaultUIComparator());
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the value in this row for aColumn, converting the value to display format.
     * 
     * @return display version of the value in this row for aColumn
     */
    public Object fieldValue()
    {
        /** require [valid_row] row() != null;   [valid_column] aColumn != null; **/

        // This is needed as RecordID has a different internal and external name.
        if (aColumn.name().equals(SMVirtualTable.RECORD_ID_COLUMN_NAME))
        {
            if (SMApplication.smApplication().isUsingIntegerPKs())
            {
                return row().virtualRowID();
            }
            else
            {
                Object downcaster = row().virtualRowID();
                return ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
            }
        }

        return valueConvertedForDisplay(row().valueForKey(aColumn.name()));
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Nasty bit of code to convert between the raw VirtualField value and something suitable for
     * use in the notification message.  Returns empty string in place of null.  This is needed as
     * there is no DataAccessColumn to perform this function.
     *  
     * @param aValue the value to convert into a display value
     * 
     * @return display representation for <code>aValue</code>
     */
    protected Object valueConvertedForDisplay(Object aValue)
    {
        if (aValue == null)
        {
            return "";
        }

        Object convertedValue = aValue;
        if (convertedValue instanceof NSTimestamp)
        {
            convertedValue = dateFormatter.format(convertedValue);
        }
        else if (convertedValue instanceof SiteFile)
        {
            convertedValue = ((SiteFile) convertedValue).uploadedFilename();
        }
        else if (convertedValue instanceof Boolean)
        {
            convertedValue = ((Boolean) convertedValue).booleanValue() ? "true" : "false";
        }
        else if (convertedValue instanceof VirtualField)
        {
            convertedValue = valueConvertedForDisplay(((VirtualField) convertedValue).value());
        }
        else if (convertedValue instanceof User)
        {
            convertedValue = ((User) convertedValue).userID();
        }

        return convertedValue;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the URL to view the record this message is generated for in single record mode.
     * 
     * @return single record mode URL for record in message
     */
    public String url()
    {
        /** require [has_dataAccessParameters] dataAccessParameters() != null;          **/
        return SMDataAccessActionURLFactory.currentRecordInSingleMode(dataAccessParameters());
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the String containing the bindings between this object and the tags in 
     * templateString().  This is created by the DataAccess page compoponent.
     * 
     * @see com.gvcsitemaker.core.support.TemplatedTextGenerator#bindingsString()
     */
    public String bindingsString()
    {
        /** require [has_dataAccessComponent] dataAccessComponent() != null;          **/
        return dataAccessComponent().notificationBindings();
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns the DataAccess PageComponent to be used when rendering this message.
     * 
     * @return the DataAccess PageComponent to be used when rendering this message
     */
    public DataAccess dataAccessComponent()
    {
        // This is ipmlemented like this as dataAccessComponent() is set directly when validating, 
        // but via dataAccessParameters() when rendering messages
        if (dataAccessComponent == null)
        {
            dataAccessComponent = dataAccessParameters().dataAccessComponent();
        }

        return dataAccessComponent;
    }



    /**
     * Overridden to allow the bindings to request fields directly by column name.
     * 
     * @see com.webobjects.appserver.WOComponent#handleQueryWithUnboundKey(java.lang.String)
     */
    public Object handleQueryWithUnboundKey(String key)
    {
        if (row().hasColumnNamed(key))
        {
            // More special handling for Record ID
            if (key.equals(SMVirtualTable.RECORD_ID_COLUMN_NAME))
            {
                if (SMApplication.smApplication().isUsingIntegerPKs())
                {
                    return row().virtualRowID();
                }
                else
                {
                    Object downcaster = row().virtualRowID();
                    return ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
                }
            }
            return valueConvertedForDisplay(row().valueForKey(key));
        }
        else
        {
            return super.handleQueryWithUnboundKey(key);
        }
    }


    /* ********** Generic setters and getters ************** */

    public DataAccessParameters dataAccessParameters()
    {
        return (DataAccessParameters) object();
    }


    public VirtualRow row()
    {
        return (VirtualRow) dataAccessParameters().currentRow();
    }


    public void setDataAccessComponent(DataAccess newDataAccessComponent)
    {
        dataAccessComponent = newDataAccessComponent;
    }

}
