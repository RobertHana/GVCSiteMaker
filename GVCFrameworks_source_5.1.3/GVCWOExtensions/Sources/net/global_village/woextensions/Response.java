package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;



/**
 * Fixes and hacks for WOResponse.
 * <p>
 * Creates a response configured to use UTF-8 for the <code>defaultFormValueEncoding()</code>.
 * </p>
 * <p>
 * Prevents disabling client caching for downloads by IE to fix the bug with IE that causes problems when displaying
 * content that cannot be displayed inline in the browswer (pdf files, for example). This was a known bug in IE 4.0 and
 * is reported fixed by MS:
 * http://support.microsoft.com/support/kb/articles/Q231/2/96.ASP?LN=EN-US&SD=gn&FR=0&qry=Internet%20Explorer%20cannot%20download&rnk=19&src=DHCS_MSPSS_gn_SRCH&SPR=IE
 * "When you try to download a .pdf file from a Web site that uses the Hypertext Transfer Protocol (HTTP) "Cache-Control =
 * 'no-cache'" directive, Internet Explorer may generate the following error message: Internet Explorer cannot down load
 * from the Internet site File_name from Computer_name."
 * </p>
 *
 * @author Copyright (c) 2001-2008  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class Response extends WOResponse
{

    public static final String ContentDispositionHeaderKey = "content-disposition";
    public static final String ContentTypeHeaderKey = "content-type";
    public static final String DisablePageCachingKey = "com.webobjects.appserver.Response.DisablePageCaching";

    boolean isIE = false;


    /**
     * Creates a response configured to use UTF-8 for the <code>defaultFormValueEncoding()</code>.
     *
     * @param context WOContext to create response for
     */
    public Response(WOContext context)
    {
        super();
        setContentEncoding("UTF-8");
        isIE = context != null && RequestUtilities.isUserAgentInternetExplorer(context.request());
    }



    /**
     * Forces all HTML pages returned to be UTF-8.
     *
     * @see com.webobjects.appserver.WOResponse#_finalizeInContext(com.webobjects.appserver.WOContext)
     * @param aContext the WOContext the request is being finalized in
     */
    public void _finalizeInContext(WOContext aContext)
    {
        // Force all HTML pages returned to be UTF-8.
        if (isHTML())
        {
            // This controls the display and is also needed to handle input via forms
            setHeader("text/html; charset=UTF-8", ContentTypeHeaderKey);
        }

        super._finalizeInContext(aContext);
    }



    /**
     * Overridden to <b>not</b> call super if trying to download an attachment to IE.
     *
     * @see com.webobjects.appserver.WOResponse#disableClientCaching()
     *
     */
    public void disableClientCaching()
    {
        boolean isIEDownloadingAttachment = isIE() && isAttachment() && ! isHTML();
        if ( ! isIEDownloadingAttachment)
        {
            NSLog.out.appendln("Disabling client caching");
            super.disableClientCaching();
        } else NSLog.out.appendln("Allowing IE client caching");
    }



    /**
     * @see #disablePageCaching()
     * @return <code>true</code> if disablePageCaching() has been called for this response
     */
    public boolean isPageCachingDisabled()
    {
        return userInfoForKey(DisablePageCachingKey) != null;
    }



    /**
     * Adds a value for DisablePageCachingKey to this response's userInfo().  This can be used later
     * to flag that this response should not be cached.
     *
     * @see WOSession#savePage(WOComponent)
     * @see #isPageCachingDisabled()
     */
    public void disablePageCaching()
    {
        setUserInfoForKey("dummy value", DisablePageCachingKey);
    }



    /**
     * WO 5.4 API
     * Sets the value for key in the user info dictionary.
     *
     * @param value value to add to userInfo()
     * @param key key to add value under
     */
    public void setUserInfoForKey(Object value, String key)
    {
        /** require [valid_value] value != null;
                    [valid_key] key != null;
         **/
        NSMutableDictionary newUserInfo = new NSMutableDictionary(value, key);
        if (userInfo() != null)
        {
            newUserInfo.addEntriesFromDictionary(userInfo());
        }
        setUserInfo(newUserInfo);
        /** ensure [value_set] userInfoForKey(key).equals(value);  **/
    }



    /**
     * WO 5.4 API
     *
     * @param key key to return value from userInfo() for
     * @return value from userInfo() for key, or null if not available
     */
    public Object userInfoForKey(String key)
    {
        /** require [valid_key] key != null;    **/
        return userInfo() != null ? userInfo().objectForKey(key) : null;
    }

    public boolean isAttachment()
    {
        String contentDisposition = contentDisposition();
        return contentDisposition != null &&
        (contentDisposition.indexOf("inline") > -1 ||
                        contentDisposition.indexOf("attachment") > -1);
    }



    /**
     * @return <code>true</code> if the content type of this response indicates HTML
     */
    public boolean isHTML()
    {
        return contentType() != null && contentType().toLowerCase().indexOf("text/html") > -1;
    }



    /**
     * @return header value for ContentDispositionHeaderKey
     */
    public String contentDisposition()
    {
        return headerForKey(ContentDispositionHeaderKey);
    }



    /**
     * @return header value for ContentTypeHeaderKey
     */
    public String contentType()
    {
        return headerForKey(ContentTypeHeaderKey);
    }



    /**
     * @return <code>true</code> if the Request this Response is for has a user agent that indicates and IE browser
     */
    public boolean isIE()
    {
        return isIE;
    }

}
