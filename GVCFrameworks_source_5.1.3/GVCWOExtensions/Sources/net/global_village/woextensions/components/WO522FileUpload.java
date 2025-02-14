package net.global_village.woextensions.components;

import com.webobjects.appserver.*;
import com.webobjects.appserver._private.*;
import com.webobjects.foundation.*;

/**
 * <p>This is a hack around a bug in <code>WOFileUpload</code> introduced in WO 5.2.2.  Use this 
 * instead of <code>WOFileUpload</code>.  The effect of the bug is to raise a Class Cast exception 
 * in <code>WOFileUpload.takeValuesFromRequest</code> when no file is uploaded:</p>
 * <pre>
 * Exception: Value in request was of type 'java.lang.String' instead of NSData. Verify that 
 * the WOForm's 'enctype' binding is set to 'multipart/form-data'
 * <br>
 * Stack trace:  
 * <table border="0">
 * <th>
 *   <td>File</td><td>Line#</td><td>Method</td><td>Package</td>
 * </th> 
 * <tr>
 *   <td>WOFileUpload.java</td><td>134</td><td>takeValuesFromRequest</td><td>com.webobjects.appserver._private</td> 
 * </tr>
 * <tr>
 *   <td>WODynamicGroup.java</td><td>81</td><td>takeChildrenValuesFromRequest</td><td>com.webobjects.appserver._private</td> 
 * </tr>
 * <tr>
 *   <td>WODynamicGroup.java</td><td>89</td><td>takeValuesFromRequest</td><td>com.webobjects.appserver._private</td>
 * </tr>
 * <tr>
 *   <td>WOComponent.java</td><td>914</td><td>takeValuesFromRequest</td><td>com.webobjects.appserver</td>
 * </tr>
 * </table> 
 * etc.
 * </pre>
 * <p>This only happens when using the legacy file upload parse by setting:</p>
 * <code>System.setProperty("WOUseLegacyMultipartParser", "YES");</code>
 * The hack simply make the call to <code>takeValuesFromRequest</code> into a no-op when there is no data 
 * uploaded.</p>
 * <p><b>Note:</b>:It does not seem to affect the streaming upload, though there have been reports 
 * of a bug when there is no file upload when using Opera:</p>  
 * <code>http://www.omnigroup.com/mailman/archive/webobjects-dev/2003-November/037423.html</code>
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class WO522FileUpload extends WOFileUpload
{

    public WO522FileUpload(String string, NSDictionary dictionary, WOElement element)
    {
        super(string, dictionary, element);
    }




    /**
     * Hack around a bug in <code>WOFileUpload</code> introduced in WO 5.2.2.  If the uploaded data
     * is not null, but is a String, then no data was actually uploaded so do nothing.  This is 
     * probably the result of a bug introduced into WOFileUploadSupport in which new String() is 
     * returned instead of new NSData.  
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        String componentName = nameInContext(aContext, aContext.component());
        Object uploadedData = aRequest.formValueForKey(componentName);
        
        if ((uploadedData != null) && (uploadedData instanceof String))
        {
            // an empty string indicates that no data was uploaded so there are no values to take.
            // The value for uploadedData should properly be either null or an empty NSData 
        }
        else 
        {
            super.takeValuesFromRequest(aRequest, aContext);
        }
    }


}
