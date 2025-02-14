// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.io.*;

import com.gvcsitemaker.core.appserver.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.woextensions.*;


/**
 * A collection of MIME type related utility methods.
 */
public class MIMEUtils extends Object
{

    private static NSDictionary mimeTypeDictionary = null;
    private static NSArray allMimeTypes = null;
    private static final NSArray imageMimeTypes = SMApplication.appProperties().arrayPropertyForKey("ImageMimeTypes");
    public static final String DEFAULT_MIMETYPE = "www/unknown";

    public static final String HTML_MIMETYPE = "text/html";



    /**
     * Given a path to an uploaded file on the local file system and the original path it was uploaded from this method determines the MIME type.  First it examines the file contents to see if it is a common image type.  If not, it grabs the ending and compares it to a list of known file endings (taken from MimeTypes.plist).  If it still can not determine the MIME type it returns DEFAULT_MIMETYPE.
     *
     * @param data - NSData containing files data
     * @param path - path of uploaded file including file name and extension
     * @return the MIME type if it can determine it or DEFAULT_MIMETYPE if not.
     */
    public static String mimeTypeFromDataOrPath(NSData data, String path)
    {
        /** require [data_valid] data != null;  [path_valid] path != null;  **/

        return mimeTypeFromStreamOrPath(data.stream(), path);

        /** ensure [valid_result] Result != null;  [valid_MIME_type]  isValidMimeType(Result);  **/
    }



    /**
     * Given an NSData containing an uploaded file and the original path it was uploaded from this method determines the MIME type.  First it examines the file contents to see if it is a common image type.  If not, it grabs the ending and compares it to a list of known file endings (taken from MimeTypes.plist).  If it still can not determine the MIME type it returns DEFAULT_MIMETYPE.
     *
     * @param fileName - path to file on local file system including file name and extension
     * @param path - path of uploaded file including file name and extension
     * @return the MIME type if it can determine it or DEFAULT_MIMETYPE if not.
     */
    public static String mimeTypeFromFileOrPath(String fileName, String path)
    {
        /** require [fileName_valid] fileName != null;  [path_valid] path != null;  **/

        String mimeType = null;

        // First try and get image types from the file contents
        try
        {
            FileInputStream inputStream  = new FileInputStream(fileName);
            mimeType = mimeTypeFromStreamOrPath(inputStream, path);
        }
        catch (java.io.FileNotFoundException e)
        {
            mimeType = mimeTypeFromPath(path);
        }

        return mimeType;

        /** ensure [valid_result] Result != null;  [valid_MIME_type]  isValidMimeType(Result);  **/
    }




    /**
     * Given a stream containing an uploaded file and the original path it was uploaded from this method determines the MIME type.  First it examines the file contents to see if it is a common image type.  If not, it grabs the ending and compares it to a list of known file endings (taken from MimeTypes.plist).  If it still can not determine the MIME type it returns DEFAULT_MIMETYPE.
     *
     * @param stream - input stream containing contents of uploaded file.
     * @param path - path of uploaded file including file name and extension
     * @return the MIME type if it can determine it or DEFAULT_MIMETYPE if not.
     */
    public static String mimeTypeFromStreamOrPath(InputStream stream, String path)
    {
        /** require [stream_valid] stream != null;  [path_valid] path != null;  **/

        String mimeType = null;

        // First try and get image types from the file contents
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(stream);
        if (imageInfo.check())
        {
            mimeType = imageInfo.getMimeType();
        }
        else // If we can not determine it from the file contents (probably not an image then), check the file extension
        {
            mimeType = mimeTypeFromPath(path);
        }

        return mimeType;

        /** ensure [valid_result] Result != null;  [valid_MIME_type]  isValidMimeType(Result);  **/
    }



    /**
     * Returns a dictionary of file extensions and their associated MIME type (defined in MimeTypes.plist).
     *
     * @return a dictionary of file extensions and their associated MIME type (defined in MimeTypes.plist).
     */
    public static NSDictionary mimeTypeDictionary()
    {
        if (mimeTypeDictionary == null)
        {
			String dictionaryString = ResourceManagerAdditions.stringFromResource("MimeTypes.plist", "GVCSMCore");

            if (dictionaryString != null)
            {
                mimeTypeDictionary = NSPropertyListSerialization.dictionaryForString(dictionaryString);
            }
            else
            {
				DebugOut.println(1, "SERIOUS ERROR: No mimeTypes plist");
				DebugOut.println(1, "All calls to mimeTypeFromPath will return DEFAULT_MIMETYPE");
            }
        }

        return mimeTypeDictionary;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns a list of the recognized MIME types (defined in MimeTypes.plist).
     *
     * @return a list of the recognized MIME types (defined in MimeTypes.plist).
     */
    public static NSArray allMimeTypes()
    {
        if (allMimeTypes == null)
        {
            NSMutableSet uniqueMimeTypes = new NSMutableSet();
            uniqueMimeTypes.addObjectsFromArray(mimeTypeDictionary().allValues());
            uniqueMimeTypes.addObject(DEFAULT_MIMETYPE);  // We just made this up!

            allMimeTypes = ArrayExtras.sortedArrayUsingComparator(uniqueMimeTypes.allObjects(), NSComparator.AscendingCaseInsensitiveStringComparator);
        }
        return allMimeTypes;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a list of the recognized URI schemes (referred to in the application as prefixes).  See <a href="http://www.w3.org/Addressing/schemes.html">Addressing Schemes</a> for details.
     *
     * @return a list of the recognized URI schemes (referred to in the application as prefixes).
     */
    public static NSArray imageMimeTypes()
    {
        return imageMimeTypes;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if the passed MIME type is in the list of imageMimeTypes.
     *
     * @return <code>true</code> if the passed MIME type is in the list of imageMimeTypes.
     */
    public static boolean isImageMimeType(String aMimeType)
    {
        /** require [valid_param] aMimeType != null;  **/
        return imageMimeTypes.containsObject(aMimeType);
    }



    /**
     * Returns <code>true</code> if the passed string in in the list of recognized MIME types (defined in MimeTypes.plist).
     *
     * @return <code>true</code> if the passed string in in the list of recognized MIME types (defined in MimeTypes.plist).
     */
    public static boolean isValidMimeType(String aMimeType)
    {
        /** require [valid_param] aMimeType != null;  **/
        return allMimeTypes().containsObject(aMimeType);
    }



    /**
     * Returns <code>true</code> if the passed string indicates an XML MIME type (text/xml).
     *
     * @param aMimeType the mime type string to test
     * @return <code>true</code> if the passed string in in the list of recognized MIME types (defined in MimeTypes.plist).
     */
    public static boolean isXMLMimeType(String aMimeType)
    {
        /** require [valid_param] aMimeType != null;  **/
        return aMimeType.toLowerCase().indexOf("xml") > -1;
    }



    /**
     * Returns <code>true</code> if the passed string indicates an HTML MIME type (text/html).
     *
     * @param aMimeType the mime type string to test
     * @return <code>true</code> if the passed string in in the list of recognized MIME types (defined in MimeTypes.plist).
     */
    public static boolean isHTMLMimeType(String aMimeType)
    {
        /** require [valid_param] aMimeType != null;  **/
        return aMimeType.toLowerCase().indexOf("html") > -1;
    }



    /**
     * Given a path, this method grabs the ending and compares it to a list of known file endings (taken from MimeTypes.plist) and returns the mimeType if it can determine it. If not, it returns DEFAULT_MIMETYPE.  This is very nice for determining the mimeType of a file that you get via a WOFileUpload. Of course, if the file ending is incorrect, you're going to get the wrong mimeType.
     */
    public static String mimeTypeFromPath(String path)
    {
        /** require [valid_param] path != null;  **/

        String mimeTypeFromPath = DEFAULT_MIMETYPE;

        int dotIndex = path.lastIndexOf(".");

        if (dotIndex > -1)
        {
            String pathExtension = path.substring(dotIndex + 1).toLowerCase();
            mimeTypeFromPath = mimeTypeForExtension(pathExtension);
        }

        DebugOut.println(20,"Deduced mime type for " + path + " as '" +  mimeTypeFromPath + "'");

        return mimeTypeFromPath;
        /** ensure [valid_result] Result != null;  [valid_MIME_type]  isValidMimeType(Result);  **/
    }



    /**
     * Given a file extension, this method compares it to a list of known file endings (taken from MimeTypes.plist) and returns the mimeType if it can determine it. If not, it returns DEFAULT_MIMETYPE.
     */
    public static String mimeTypeForExtension(String extension)
    {
        /** require [valid_param] extension != null;  **/

        String mimeTypeForExtension = (String) mimeTypeDictionary().valueForKey(extension);
        return (mimeTypeForExtension != null) ? mimeTypeForExtension : DEFAULT_MIMETYPE;

        /** ensure [valid_result] Result != null;  [valid_MIME_type]  isValidMimeType(Result);  **/
    }





}
