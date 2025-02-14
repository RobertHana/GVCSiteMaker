// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import java.text.Format;

import com.gvcsitemaker.core.appserver.SMApplication;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import net.global_village.woextensions.ApplicationProperties;


/**
 * Extends DataAccessColumn with support for selecting a formatter and using it to format output.
 */
public abstract class DataAccessColumnWithFormatter extends DataAccessColumn 
{

    /** @TypeInfo java.lang.String */
    protected NSArray formatPatterns;       // For configuring number format
	public String aFormatPattern;        // For configuring number format


    /**
     * Designated constructor
     */
    public DataAccessColumnWithFormatter(WOContext context)
    {
        super(context);
    }



    /**
     * Returns the list of format patterns to allow the user to select from.  Calls loadFormatPatterns if no patterns are loaded.
     *
     * @return the list of format patterns to allow the user to select from.
     */
    /** @TypeInfo java.lang.String */
    public NSArray formatPatterns()
    {
        if (formatPatterns == null)
        {
            // Although we are stateless, this is not reset after each phase as it can be shared.
            loadFormatPatterns();
        }

        return formatPatterns;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Sets formatPatterns to the list of format patterns to display in the UI for the user to select from.
     */
    protected abstract void loadFormatPatterns();
    //** ensure [format_patterns_loaded] formatPatterns() != null;  **/



    /**
     * Returns the exampleValue() formatted according to aFormatPattern().  This is to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the exampleValue() formatted according to aFormatPattern().  
     */
    public String formatExample()
    {
        /** require [has_aFormatPattern] aFormatPattern() != null;  **/
        return formatterForPattern(aFormatPattern()).format(exampleValue());
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     *
     * @return the value to use with formatExample to use in a WOPopUp to indicate what each format in formatPatterns() does.
     */
    protected abstract Object exampleValue();
    //** ensure [valid_result] Result != null;  **/



    /**
     * Returns the Format to use to format display values in this column or null if no formatter is required.
     *
     * @return the Format to use to format values in this column or null if no formatter is required.
     */
    public Format outputFormatter()
    {
        return formatterForPattern(outputFormatPattern());
    }



    /**
     * Returns the pattern to use when formatting output values in this column or null if no formatter is required.
     *
     * @return the pattern to use when formatting output values in this column or null if no formatter is required.
     */
    public abstract String outputFormatPattern();



    /**
     * Returns a Format configured with aFormatPattern.  The Format is created by createFormatterForPattern(String).
     *
     * @param aPattern the formatting pattern to configure the Format with
     * @return a Format configured with aFormatPattern.
     */
    public Format formatterForPattern(String aPattern)
    {
        /** require [valid_param] aPattern != null; **/

        ApplicationProperties appProperties = ((SMApplication)application()).properties();
        Format formatterForPattern = null;

        // In most cases the formatter will have already been created during application initialization.  However it is possible that columns were configured with a pattern that was later removed from the config file.  In this case the formatter is created on the fly so that values in these columns can be formatted correctly until a more current format is chosen.
        if (appProperties.hasPropertyForKey(aPattern))
        {
            formatterForPattern = (Format) appProperties.propertyForKey(aPattern);
        }
        else
        {
            formatterForPattern = createFormatterForPattern(aPattern);
            appProperties.addPropertyForKey(formatterForPattern, aPattern);
        }

        return formatterForPattern;

        /** ensure
        [valid_result] Result != null;
        [formatter_created] ((SMApplication)application()).properties().hasPropertyForKey(aPattern); **/
    }



    /**
     * Returns a sub-class for Format appropriate for this column, configured with aFormatPattern.
     *
     * @param aPattern the formatting pattern to configure the Format with
     * @return a sub-class for Format appropriate for this column, configured with aFormatPattern.
     */
    public abstract Format createFormatterForPattern(String aPattern);
    //** ensure [valid_result] Result != null;  **/


    //*********** Generic Get / Set methods  ***********\\


    public void setFormatPatterns(NSArray newFormatterns) {
        formatPatterns = newFormatterns;
    }

    public String aFormatPattern() {
        return aFormatPattern;
    }
    public void setFormatPattern(String newFormatPattern) {
        aFormatPattern = newFormatPattern;
    }

}
