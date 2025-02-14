package net.global_village.woextensions.components;

import com.webobjects.appserver.*;


/**
 * This component when used in conjunction with an HTML table and a
 * <code>WORepetition</code> will give the desired alternating background colors on rows.
 * Use this <code>WOComponent</code> instead of the standard <code>TR</code> tag.  Pass in the index of
 * the <code>WORepetition</code> that envelopes this <code>WOComponent</code>.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class AlternatingColorRowComponent extends WOComponent
{
    public Integer rowNumber;
    protected String valign;


    /**
     * Designated constructor.
     */
    public AlternatingColorRowComponent(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Returns either "<code>primaryBackgroundColor</code>" or "<code>secondaryBackgroundColor</code>"
     * depending on <code>rowNumber</code>.
     *
     * @return background color CSS class
     */
    public String backgroundCSSClass()
    {
        //rowNumber = (Integer) valueForBinding("rowNumber");
        String backgroundCSSClass;

        if (rowNumber.intValue() % 2 == 0)
        {
            backgroundCSSClass = "primaryBackgroundColor";
        }
        else
        {
            backgroundCSSClass = "secondaryBackgroundColor";
        }
         
        return backgroundCSSClass;

        /** ensure
        [result_not_null] Result != null;
        [correct_result] Result == "primaryBackgroundColor" || Result == "secondaryBackgroundColor"; **/
    }



    public String valign()
    {
        return valign;
    }



    /**
     * Only valid HTML tags are accepted.
     *
     * @param newValign one of "<code>top</code>", "<code>middle</code>" or "<code>bottom</code>"
     */
    public void setValign(String newValign)
    {
        /** require [valid_param] (newValign.equalsIgnoreCase("top") || newValign.equalsIgnoreCase("middle") || newValign.equalsIgnoreCase("bottom")); **/
        valign = newValign;
    }



}
