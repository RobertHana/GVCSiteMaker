package net.global_village.woextensions.components;

import java.util.*;

import org.apache.log4j.*;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.*;

/**
 * A simplified version of ERXLog4JConfiguration component which configures and manages the log4j logging system.
 * This component does not require ERXExtensions to get initialized by ERXApplication.
 *
 */
public class Log4JConfigurationPage extends WOComponent
{
    public final static EOSortOrdering NAME_SORT_ORDERING = new EOSortOrdering("name",
                                                                             EOSortOrdering.CompareAscending);
    public final static NSMutableArray SORT_BY_NAME = new NSMutableArray(NAME_SORT_ORDERING);
    public final static NSArray LOGGER_LEVELS = new NSArray(new Object[]{Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF});
    private final static NSDictionary BG_COLORS = new NSDictionary(new NSArray(new Object[]{ "#ff0000",
                                                                                             "#ffbbbb",
                                                                                             "#eeccbb",
                                                                                             "#ddddbb",
                                                                                             "#cceebb",
                                                                                             "#bbffbb",
                                                                                             "#808080"}),
                                                                                             LOGGER_LEVELS);

    private Logger logger;
    private boolean showAll = false;
    private NSMutableArray allLoggers;
    private NSMutableArray explicitLevelLoggers;

    /**
     * Constructor.
     *
     * @param context of current page
     */
    public Log4JConfigurationPage(WOContext context)
    {
        super(context);
    }



    /**
     * List of loggers defined in the application.  If showAll, returns all the loggers, otherwise just the explicit level loggers
     *
     * @return the list of loggers defined in the application.
     */
    public NSMutableArray loggers()
    {
        if (allLoggers == null)
        {
            allLoggers = new NSMutableArray();
            explicitLevelLoggers = new NSMutableArray();
            for (Enumeration e= LogManager.getCurrentLoggers(); e.hasMoreElements();)
            {
                Logger log = (Logger) e.nextElement();
                while (log !=null)
                {
                    if (!allLoggers.containsObject(log))
                    {
                        allLoggers.addObject(log);
                        if (log.getLevel() != null)
                        {
                            explicitLevelLoggers.addObject(log);
                        }
                    }
                    log = (Logger) log.getParent();
                }
            }
            EOSortOrdering.sortArrayUsingKeyOrderArray(allLoggers, SORT_BY_NAME);
            EOSortOrdering.sortArrayUsingKeyOrderArray(explicitLevelLoggers, SORT_BY_NAME);
        }
        return showAll() ? allLoggers : explicitLevelLoggers;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * @return the logger levels
     */
    public NSArray loggerLevels()
    {
        return LOGGER_LEVELS;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Updates the log configuration
     *
     * @return returns this page
     */
    public WOComponent update()
    {
        ERXExtensions.configureAdaptorContext();
        return null;
    }



    /**
     * @return the selected level of the current logger
     */
    public Level selectedLevel()
    {
        return logger().getLevel();
    }



    /**
     * Sets the selected level
     *
     * @param newLevel the new Level to set
     */
    public void setSelectedLevel(Level newLevel)
    {
        logger().setLevel(newLevel);
    }



    /**
     * @return true if only explicit level loggers need to be displayed, false otherwise
     */
    public boolean showExplicit()
    {
        return ! showAll;
    }



    /**
     * @return true if all loggers need to be displayed
     */
    public boolean showAll()
    {
        return showAll;
    }



    /**
     * Sets the showAll with newValue
     *
     * @param newValue the new value to set for showAll
     */
    public void setShowAll(boolean newValue)
    {
        showAll = newValue;
    }



    /**
     * @return the corresponding background color based on the selected level of the current logger
     */
    public String bgColor()
    {
        return (String) BG_COLORS.objectForKey(selectedLevel());
    }



    /**
     * @return the current logger in the list
     */
    public Logger logger()
    {
        return logger;
    }



    /**
     * Sets the logger with newValue
     *
     * @param newValue the new value to set for logger
     */
    public void setLogger(Logger newValue)
    {
        logger = newValue;
    }
}