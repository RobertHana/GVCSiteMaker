package net.global_village.foundation;

import org.apache.log4j.*;


/**
 * Based on code from Dr Heinz Max Kabutz's The Java Specialists' Newsletter [Issue 137].  Rather than cut and paste
 * the standard code:
 * <pre>
 * private final static Logger logger = Logger.getLogger(Application.class.getName());
 * </pre>
 * and risk forgetting to update the classname, use this  cut and paste safe class:
 * <pre>
 * private final static Logger logger = LoggerFactory.makeLogger();
 * </pre>
 * 
 * @see <a href="http://www.javaspecialists.co.za/archive/newsletter.do?issue=137">The Java Specialists' Newsletter [Issue 137]</a>
 * @author Copyright (c) 2006-2006 Harte-Hanks Shoppers, Inc.
 */
public class LoggerFactory
{

      public static Logger makeLogger()
    {
        Throwable t = new Throwable();
        StackTraceElement directCaller = t.getStackTrace()[1];
        return Logger.getLogger(directCaller.getClassName());
    }
}
