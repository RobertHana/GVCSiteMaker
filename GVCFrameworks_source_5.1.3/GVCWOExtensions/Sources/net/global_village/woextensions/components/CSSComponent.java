package net.global_village.woextensions.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.global_village.woextensions.*;


/**
 * <p><code>CSSComponent</code> is a <code>WOComponent</code> which can be added to
 * a page to generate the HTML for:<br>
 * <code>&lt;link rel="stylesheet" href="some.css" type="text/css"&gt;</pre></code><br>
 * where the <code>href</code> can vary depending on the OS, browser, and browser
 * version that the user is using.  This will allow you to have very fine grained
 * control over which style sheet is used. It also optionally adds a style sheet
 * link for each page (for example, "LoginPage.css" and "MainPage.css"), if one is
 * specified.</p>
 *
 * <p>If the path to the style sheet is relative (doesn't begin with a "/"), then the
 * URI will be full qualified with the application URL prefix.  For example,
 * "/ie_win_4.css" will become "/ie_win_4.css", while "ie_win_4.css" will become
 * "http://www.global_village.net/cgi-bin/WebObjects/MyApp/ie_win_4.css".</p>
 *
 * <p><b>How to Use</b></p>
 * <ul>
 * <li>Add a dictionary to your application with entries as outlined below.  You can
 *     set-up code to get this dictionary from a plist file, refer to
 *     <code>ApplicationProperties</code> class documentation for details.  This will
 *     allow the dictionary to be supplied with a simple bindig like
 *     <code>application.properties.storeStyles</pre></li>
 * <li>Add this component to the HEAD section of pages using CSS.  This component must
 *     be placed manually inside the HTML HEAD tag for the style sheet to work properly.
 *     This can <b>NOT</b> be done in the WYSIWYG mode and must be manually added to the
 *     .html and .wod files.</li>
 * <li>Bind the dictionary binding to the dictionary that holds the mapping between
 *     browser and style sheet that you setup in step 1.</li>
 * <li>Optionally set the framework binding if the global CSS files are not in the
 *     application bundle. Note that the per-page CSS files are always looked for in
 *     the framework that defines the page itself.
 * </ul>
 *
 * <p><b>Matching Browsers, Browser Versions, and Operating Systems to Sytle Sheets</b></p>
 * <p>This component associates sytle sheets with browser/OS/version using a dictionary
 * with entries formed as Browser/OS/BrowserVersion.  The component will look through
 * the dictionary to find the best match from most to least specific, trying in order:</p>
 * <code><pre>
 *     ie/win/5
 *     ie/win
 *     ie
 *     default
 * </pre></code>
 *
 * <p>Where default indicates the style sheet to use if there is no match for the
 * Browser/OS/BrowserVersion.  Here is an example dictionary:</p>
 * <code><pre>
 * {
 *     default = "/default.css";
 *     ie = "/ie/ie.css";
 *     ie/win = "/ie/ie_win.css";
 *     ie/win/4 = "/ie/ie_win_4.css";
 *     ie/win/5 = "/ie/ie_win_5.css";
 *     ie/mac = "/ie/ie_mac.css";
 *     ie/mac = "/ie/ie_mac.css";
 *     ie/mac/4 = "/ie/ie_mac_4.css";
 *     ie/mac/5 = "/ie/ie_mac_5.css";
 *     netcape = "/netscape/netscape.css";
 *     // etc.
 * }
 * </pre></code>
 *
 * <p><b>Limitations and Extensions</b></p>
 * <p>The only two browsers currently detected are Microsoft Internet Explorer (indicated
 * by 'ie') and Netscape Navigator (indicated by 'netscape').  Only major browser version
 * numbers are detected (e.g. 4, 5, 6), sub-versions are ignored at this time. The only
 * two operating systems currently detected are Microsoft Windows (all versions) which is
 * indicated as 'win', and Apple Mac OS (all versions) which is indicated as 'mac'.</p>
 *
 * <p>To extend this code to handle other browsers, more exact browser versions, and other
 * operating systems see accessor methods in <code>ApplicationProperties</code> class:</p>
 * <code><pre>
 *    public Object propertyForKey(String key);
 *    public String stringPropertyForKey(String key);
 *    public int intPropertyForKey(String key);
 *    public NSArray arrayPropertyForKey(String aKey);
 *    public NSDictionary dictionaryPropertyForKey(String aKey);
 *    public float floatValuePropertyForKey(String aKey);
 * </pre></code>
 *
 * <p><b>Adding style sheets on a per-page basis</b></p>
 * <p>This component associates sytle sheets with pages by getting the page name, looking
 * up that page name in the <code>styleDictionary</code> binding under the
 * <code>pageDictionary</code> key and generating a link from the value in that
 * dictionary if a key exists in that dictionary for this page.  If the page name doesn't
 * exist in the dictionary, no link is created.  For example:</p>
 * <code><pre>
 * {
 *     default = "/default.css";
 *     LoginPage = "/LoginPage.css";
 *     MainPage = "/mostPages.css";
 *     LogoutPage = "/mostPages.css";
 * }
 * </pre></code><br>
 * <p>This allows you to separate style sheets by page, thus reducing the global style
 * sheet's complexity.</p>
 *
 * <p><b>How it Works</b></p>
 * <p>During <code>appendToResponse()</code> of a page with this component, it will
 * evaluate the browser, OS, and version from the request using the
 * <code>RequestUtilities</code> class. Then, the component will look through the
 * dictionary to find the best match.</p>
 *
 * @see com.webobjects.appserver.WORequest#applicationURLPrefix
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class CSSComponent extends net.global_village.woextensions.WOComponent
{
    private static final long serialVersionUID = -7445155741231303808L;

    protected static final String defaultKey = "default";

    /** Binding for the CSS repetition. */
    public String aCSSFile;


    /**
     * Designated constructor.
     */
    public CSSComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * This method is overridden so variables are not synchronized with bindings.
     *
     * @return false
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Overriden to make this a stateless component.
     *
     * @return true
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Returns an array of strings that indicate CSS files to link to the page.
     *
     * @return an array of CSS files to link to the page
     */
    public NSArray cssFiles()
    {
        NSMutableArray cssFiles = new NSMutableArray(globalCSSFile());

        String pageCSS = pageCSSFile();
        if (pageCSS != null)
        {
            cssFiles.addObject(pageCSS);
        }

        return cssFiles;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the CSS file for the page that we are on, if one exists in the style dictionary.  Returns null otherwise.
     *
     * @return the CSS file for the page that we are on
     */
    public String pageCSSFile()
    {
        String pageCSSFile = (String)styleDictionary().objectForKey(topLevelComponent().name());
        if ((pageCSSFile != null) && ( ! pageCSSFile.startsWith("/")))
        {
            WORequest aRequest = context().request();
            String frameworkName = NSBundle.bundleForClass(topLevelComponent().getClass()).name();
            pageCSSFile = application().resourceManager().urlForResourceNamed(pageCSSFile, frameworkName, aRequest.browserLanguages(), aRequest);
        }
        return pageCSSFile;
    }



    /**
     * Returns the most specific css file reference we can find.
     *
     * @return the most specific css file reference we can find
     */
    public String globalCSSFile()
    {
        WORequest aRequest = context().request();
        String globalCSSFile = null;

        //Fetch all the necessary browser information from the user agent of the request
        String userAgentName = RequestUtilities.userAgentNameFromRequest(aRequest);
        String osName = RequestUtilities.userOSFromRequest(aRequest);
        String userAgentVersion = RequestUtilities.userAgentMajorVersionNumberFromRequest(aRequest);

        NSDictionary styleDictionary = styleDictionary();

        globalCSSFile = (String)styleDictionary.valueForKeyPath(userAgentName + "/" + osName + "/" + userAgentVersion);
        if (globalCSSFile == null)
        {
            globalCSSFile = (String)styleDictionary.valueForKeyPath(userAgentName + "/" + osName);
            if (globalCSSFile == null)
            {
                globalCSSFile = (String)styleDictionary.valueForKeyPath(userAgentName);
                if (globalCSSFile == null)
                {
                    globalCSSFile = (String)styleDictionary.valueForKeyPath(defaultKey);
                }
            }
        }

        if ((globalCSSFile != null) && ( ! globalCSSFile.startsWith("/")))
        {
            globalCSSFile = application().resourceManager().urlForResourceNamed(globalCSSFile, framework(), aRequest.browserLanguages(), aRequest);
        }

        return globalCSSFile;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the dictionary bound to <code>styleDictionary<code> which contains the values to be used to generate the HTML.
     *
     * @return the value for the styleDictionary binding
     */
    protected NSDictionary styleDictionary()
    {
        return (NSDictionary)valueForBinding("styleDictionary");

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the framework to find the CSS files in.
     *
     * @return the framework in which to find the CSS files
     */
    protected String framework()
    {
        return (String)valueForBinding("framework");
    }



}
