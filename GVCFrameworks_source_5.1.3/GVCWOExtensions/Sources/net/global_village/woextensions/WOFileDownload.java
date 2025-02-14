package net.global_village.woextensions;

import java.io.*;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * <code>WOFileDownload</code> is a simple hyperlink that can either download a file on the file system, or source the data from a provided <code>NSData</code> object.  See the API file for more details.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 7$
 */
public class WOFileDownload extends net.global_village.woextensions.WOComponent
{
	private static final long serialVersionUID = -7126989444334479290L;

	boolean shouldDownload;

    // Bindings for the component
    final String displayAsButtonBinding = "displayAsButton";
    final String fileNameBinding = "fileName";
    final String dataBinding = "data";
    final String filePathBinding = "filePath";
    final String contentTypeBinding = "contentType";
    final String downloadTitleBinding = "downloadTitle";
    final String parentsActionOnDownloadBinding = "action";

    // Buffer size to use when reading in files.
    static final public int FileBufferSize = 1024 * 1024 * 8;   // Eight MB.


    /**
     * Designated constructor.
     */
    public WOFileDownload(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Should we use the button or the link.
     *
     * @return value of the shouldUseLink binding
     */
    public boolean displayAsButton()
    {
        return booleanValueForBinding(displayAsButtonBinding, false);
    }



    /**
     * The text to display for the download hyperlink
     *
     * @return the text to display for download hyperlink
     */
    public String downloadTitleText()
    {
        String downloadTitleText = (String)valueForBinding(downloadTitleBinding);

        // defect in valueForBinding hack protects against returning null
        if (downloadTitleText == null)
        {
            downloadTitleText = downloadTitle();
        }

        return downloadTitleText;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * The hyperlink action method.  <code>invokeActionForRequest()</code> resets this flag, this method sets it, and <code>appendToResponse()</code> processes it.
     *
     * @return this page again
     */
    public WOComponent doDownload()
    {
        shouldDownload = true;
        notifyParentOfDownload();

        return this;  // do not return context().page() or the bottom of the page will be appended to the download!

        /** NOTensure [valid_result] Result != null; **/
    }



    /**
     * Use the filename as the text to display if the user does not specify it.  This should work but valueForBinding: seems busted.
     *
     * @return String the title of the downloaded file
     */
    private String downloadTitle()
    {
        return (String)valueForBinding(fileNameBinding);
    }



    /**
     * The content type as a string
     *
     * @return String the content type text.
     */
    private String contentTypeText()
    {
        String contentTypeText = (String)valueForBinding(contentTypeBinding);

        // valueForBinding defect hack protects against returning null
        if (contentTypeText == null)
        {
            contentTypeText = contentType();
        }

        return contentTypeText;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * The default content type if the user does not specify it.  This should work but <code>valueForBinding()</code> seems busted.
     *
     * @return String the content type
     */
    private String contentType()
    {
        return "application/octet-stream";
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the data from either the file indicated, or the data stream indicated.  A data stream takes precedence over a filename.
     *
     * @return NSData the data to be downloaded
     */
    private NSData dataToDownload()
    {
        /** require [has_filename_binding] hasNonNullBindingFor(fileNameBinding);  **/
        // This is also a requirement, but can be very expensive to evaluate (i.e. if the data is created on demand!) and so have been removed from being checked
        // [data_is_null_or_is_NSData] ( ! hasNonNullBindingFor(dataBinding)) || (valueForBinding(dataBinding) instanceof NSData); **/


        // First, try and get the data.  If there is no data, get it from a file.
        NSData dataToDownload = null;
        if (hasBinding(dataBinding))
        {
            dataToDownload = (NSData)valueForBinding(dataBinding);
        }

        if (dataToDownload == null)
        {
            String fileName = (String)valueForBinding(fileNameBinding);
            String qualifiedFileName;

            if (hasNonNullBindingFor(filePathBinding))
            {
                String interimString = (String) valueForBinding(filePathBinding);
                qualifiedFileName = NSPathUtilities.stringByAppendingPathComponent(interimString, fileName);
            }
            else
            {
                qualifiedFileName = fileName;
            }

            // check that the file indicated actually exists at runtime.  A precondition check is unlikely to be sufficient.
            File theFile = new File(qualifiedFileName);

            if ( ! theFile.exists())
            {
                throw new RuntimeException("Unable to find file: " + qualifiedFileName);
            }

            FileInputStream fileInputStream;
            BufferedInputStream bufferedInputStream;
            try
            {
                // Doing this with large files can result in a OutOfMemoryException which kills the instance.  The code below tries to mitigate this as much as possible, but there will be a finite limit to the size of file that can be handled for a given JVM heap size.
                fileInputStream = new FileInputStream(theFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);

                // The default buffer size is that of the entire file.
                int bufferSize = bufferedInputStream.available();

                // If the source file is larger than the designated buffer size, use the the designated buffer size.
                if (bufferSize > FileBufferSize)
                {
                    bufferSize = FileBufferSize;
                }

                dataToDownload = new NSData(bufferedInputStream, bufferSize);

                // Big files use a lot of memory, help the gc.  Should we force GC here if it was a large file?  -ch
                bufferedInputStream.close();
                fileInputStream.close();
                bufferedInputStream = null;
                fileInputStream = null;
                theFile = null;
            }
            catch (java.io.FileNotFoundException e)
            {
                // This should not happen as we check that it exists above.
                throw new ExceptionConverter(e);
            }
            catch (java.io.IOException e)
            {
                throw new ExceptionConverter(e);
            }
        }

        return dataToDownload;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Invoke the parents call back, if any.  This allows the parent to take action when a download is performed.
     */
    private void notifyParentOfDownload()
    {
        valueForBinding(parentsActionOnDownloadBinding);
    }



    /**
     * We pull bindings when we need them!
     *
     * @return always false
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Resets the download flag before the action is invoked
     *
     * @param aRequest the WORequest to the component
     * @param aContext the WOContext of the request
     * @return calls super.invokeAction()
     */
    public WOActionResults invokeAction(WORequest aRequest, WOContext aContext)
    {
        shouldDownload = false;
        
        return super.invokeAction(aRequest, aContext);
    }



    /**
     * Processes download if flag is set.
     *
     * @param aResponse the WOResponse to add file to
     * @param aContext the WOContext of the request
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        /** require [has_filename_binding] hasNonNullBindingFor(fileNameBinding);  **/
        // This is also a requirement, but can be very expensive to evaluate (i.e. if the data is created on demand!) and so have been removed from being checked
        // [data_is_null_or_is_NSData] ( ! hasNonNullBindingFor(dataBinding)) || (valueForBinding(dataBinding) instanceof NSData);

        if (shouldDownload)
        {
            String fileName = (String)valueForBinding(fileNameBinding);
            String contentDisposition = "attachment; filename=\"" + fileName + "\"";

            aResponse.setHeader(contentDisposition, Response.ContentDispositionHeaderKey);
            aResponse.setHeader(contentTypeText(), Response.ContentTypeHeaderKey);
            aResponse.setContent(dataToDownload());

            // A hack to get IE to download over SSL.  From http://www.omnigroup.com/mailman/archive/webobjects-dev/2001-November/016769.html
            // "Basically, the WOResponse method "disableClientCaching" adds the "cache-control" headers. This seems to be called sometime after the component/application appendToResponse methods (at the end of the these methods, the cache-control headers are not there).  However, it also seems that WO will only call this method once. So, if you call disableClientCaching in your component's appendToResponse and then remove the offending headers, they aren't included in the final stream back to the browser - welcome happy PDF files in IE with https."
            // NOTE: This does have the effect of leaving the downloaded file in the browser's cache which might be a problem for sensitive downloads.  I am not aware of any work around that avoids this security issue while allowing downloads to succeed.
            aResponse.disableClientCaching();
            aResponse.removeHeadersForKey("Cache-Control");
            aResponse.removeHeadersForKey("pragma");

            shouldDownload = false;
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }
    }



}
