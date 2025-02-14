package net.global_village.foundation;

import java.math.*;
import java.text.*;


/**
 * Scales numeric values larger or smaller on format and parse. At present, this only works correctly for values that
 * divide into whole integers (e.g. 10 / 2 but not 10 / 3). The constructor takes a scale to convert values by. The
 * value is multiplied by this when formatting and divided by this when parsing.
 *
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class ScalingFormatter extends Format
{


    protected double scale;
    protected Format subFormatter;


    /**
     * Designated constructor
     *
     * @param aScale scale to convert values by.  The value is multiplied by this when formatting and divided by this when parsing
     * @param aFormatter Format used to arrive at correct type for parsed values
     */
    public ScalingFormatter(double aScale, Format aFormatter)
    {
        super();
        scale = aScale;
        subFormatter = aFormatter;
    }



    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition position)
    {
        if ( !(object instanceof Number))
        {
            return subFormatter.format(object, stringBuffer, position);
        }

        Number n = (Number)object;
        if (object instanceof BigDecimal)
        {
            BigDecimal b = (BigDecimal)object;
            b = b.multiply(new BigDecimal(scale), MathContext.DECIMAL32);
            return subFormatter.format(b, stringBuffer, position);
        }

        double d = n.doubleValue();
        d = d * scale;
        return subFormatter.format(new Double(d), stringBuffer, position);
    }



    /**
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     *
     * @param source
     * @param pos
     * @return parsed and scaled result
     */
    public Object parseObject(String source, ParsePosition pos)
    {
        int index = pos.getIndex();
        Object object = subFormatter.parseObject(source, pos);
        pos.setIndex(index);

        if ( ! (object instanceof Number))
        {
            return object;
        }

        Number n = (Number)object;
        if (object instanceof BigDecimal)
        {
            BigDecimal b = (BigDecimal)object;
            b = b.divide(new BigDecimal(scale));
            return subFormatter.parseObject(b.toString(), pos);
        }

        double d = n.doubleValue();
        d /= scale;
        return subFormatter.parseObject(new Integer((int)d).toString(), pos);
    }
}
