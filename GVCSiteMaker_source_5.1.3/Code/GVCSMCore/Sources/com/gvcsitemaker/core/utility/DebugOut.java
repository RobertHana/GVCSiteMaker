// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.utility;

import java.io.*;
import java.text.*;
import java.util.*;

import com.webobjects.foundation.*;


/**
 * The DebugOut class provides a convenient way to generate reams of
 * debugging information from your application. Instead of using
 * System.out.println, just use the class method:
 * <pre>
 * DebugOut.println(1, "foo");
 * </pre>
 * That's all there is to it. Everything is set to a reasonable
 * default out of the box (so to speak). If you want more control over
 * your debugging output (and there's no reason why you wouldn't!),
 * there are methods for setting the debug threshold (an integer which
 * must be greater than or equal to in order for output to be
 * generated). <p>
 *
 * Now for the neat stuff: Depending on the ``mode'' that DebugOut is
 * in, you get different messages printed. Given the above statement
 * (DebugOut.println(1, "foo");) called from your Session's awake
 * method, here's what you'll see printed for each mode:
 *
 * <dl>
 * <dt><b>TERSE</b>:
 * <dd>foo
 *
 * <dt><b>NSLOG</b>:
 * <dd>Mar 02 10:26:33 javaApp[395] foo
 *
 * <dt><b>VERBOSE</b>:
 * <dd>Session.awake - foo
 *
 * <dt><b>PONTIFICAL</b>:
 * <dd>Mar 02 10:26:33 javaApp[395] - Session.awake - foo
 * </dl>
 *
 * What I usually do is develop in <b>PONTIFICAL</b> mode and deploy in <b>TERSE</b>
 * mode with a debugLevel of 1 <p>
 * 
 * I also like to set some general numbering standards for my
 * debugging output. I (usually) use the following guidelines for
 * debugginglevels:
 *<pre>
 *  1      General application logging
 *  2 - 10 Controller level debugging in action methods
 * 11 - 15 Controller level debugging in setters, getters, takeValuesFromRequest,
 *         invokeAction, and appendToResponse
 * 16 - 25 Framework level debugging stuff
 * 25 - up Messages that I'm going to want to see when I'm totally lost in a bug 
 *         and grasping at straws. One step away from tossing my workstation out
 *         the window. 
 * </pre>
 * Note also that the ``mode'' is set to <b>PONTIFICAL</b> by default. It is
 * recommended that you set the mode to <b>NSLOG</b> or <b>TERSE</b> when you deploy
 * your app as I'm sure that you don't want the overhead of all those
 * stack traces slowing you down. <p>
 *
 * The <b>PONTIFICAL</b> and <b>NSLOG</b> modes prints out the date, name of the app
 * ("javaApp" by default) and a "pseudo" PID. This is merely to allow
 * you to discern which app is printing what if you (like me) are
 * running multiple instances of an app and funneling their log output
 * to a single file. For a while I was just cheating and using
 * WOApplication.logString, but I don't want to be dependent on the
 * WOF. <p>
 * 
 * And no, I still haven't forgiven java for not giving me getPid(). <p>
 *
 * The <b>PONTIFICAL</b> and <b>VERBOSE</b> modes also print out the Class and
 * method where DebugOut.println was invoked. While this was a real
 * piece of cake in Objective C, thanks to the lack of a true
 * preprocessor, it's a real pain in Java. In order to get the class
 * and method, we need to generate a stack trace and muck through it
 * frame by frame to find out who our caller is. <p>
 * 
 * But wait Ian, THERE'S MORE! By default, it prints to the STDERR
 * outputstream (System.err), but you can set it to STDOUT or a file
 * or whatever you want. Just give it a printstream. Go nuts.  
 *
 * @author   B.W. Fitzpatrick &lt;fitz@apple.com&gt;
 * @version $Revision: 1.1 $
 */
 public class DebugOut extends Object
{

     public static final int TERSE = 0;
     public static final int NSLOG = 1;
     public static final int VERBOSE = 2;
     public static final int PONTIFICAL = 3;

     protected static String applicationName = new String("javaApp");
     protected static int debugLevel         = 1;
     protected static int mode               = PONTIFICAL;
     protected static PrintStream out        = System.err;

     //  messageTypeList is a dictionary for hash table efficiency.
     protected static NSMutableDictionary messageTypeList = null;

     protected static SimpleDateFormat formatter = new SimpleDateFormat ("[yyyy-MM-dd HH:mm:ss zzz]");
     
     private static boolean isInList( String aType ) {
         return messageTypeList.objectForKey( aType ) != null;
     }

     public static void println( int appDebugLevel, String theMessageType, String message) {
         /**
          *   emit the message if appDebugLevel below the threshold
          *                    if there is no messageType
          *                    if there is no messageTypeList
          *                     if there is a messageTypeList and messageType is in it
          */

         String messageType = "";

         if( theMessageType != null )
             messageType = "<" + theMessageType + ">";

         if (message.length() > 3 * 1024 )
             message = message.substring(0, 3 * 1024 - 5) + " ...";

         if ((appDebugLevel <= DebugOut.debugLevel) &&
             ((messageType == null) || (messageTypeList == null) ||
              (messageTypeList != null && messageType != null && isInList( messageType )))) {
             switch( mode ) {
                 case DebugOut.TERSE:
                     DebugOut.out.println( message );
                     break;

                 case DebugOut.NSLOG:
                     DebugOut.out.println( DebugOut._formattedDate() +
                                           " <" + Thread.currentThread().getName() + ">" +
                                           " " + messageType +
                                           " " + message );
                     break;

                 case DebugOut.VERBOSE:
                     DebugOut.out.println(DebugOut._formattedDate() +
                                          " <" + Thread.currentThread().getName() + ">" +
                                          " " + messageType +
                                          DebugOut._callerDescription() +
                                          " " + message );
                     break;

                 case DebugOut.PONTIFICAL:
                     DebugOut.out.println( DebugOut._formattedDate() +
                                           " <" + Thread.currentThread().getName() + ">" +
                                           " " + applicationName +
                                           " " + messageType +
                                           DebugOut._callerDescription() +
                                           " - " + message );
             }
         }
     }


     /** This is the method that you'll use to log anything with the 
      * DebugOut class. Just make sure that the appDebugLevel is lower
      * than DebugOut.debugLevel if you want the message to print out. 
      * 
      * @param int appDebugLevel Prints out the message if the
      * application's debug level is greater than or equal to this number
      * @param String message The message that you would like to print.
      * @see #setDebugLevel
      * @see #setMode
      **/
     public static void println (int appDebugLevel, String message) {
         println(appDebugLevel, null, message);
     }


     //  This method allows us to print stack traces for exceptions with
     //  the same run-time filtering as the println methods.
     public static void printStackTrace( int appDebugLevel, String messageType, Throwable e ) {
         if ((appDebugLevel <= DebugOut.debugLevel) && ((messageType == null) ||
                                                        (messageTypeList != null && messageType != null && isInList( messageType )))) {
             e.printStackTrace( out() );
         }
     }

     public static void printStackTrace( int appDebugLevel, Throwable e ) {
         printStackTrace( appDebugLevel, null, e );
     }

     protected static String _callerDescription() {
         String stackTrace = null;
         Throwable except = new Throwable("debug");
         CharArrayWriter cw = new CharArrayWriter();
         PrintWriter pw = new PrintWriter(cw);
         String frame = null;

         except.printStackTrace(pw);
         stackTrace = cw.toString();

         // System.out.println("====================================");
         // System.out.println(stackTrace);
         // System.out.println("====================================");

         // String scrubbing. Muck through stackTrace for our frame
         if (stackTrace != null) {
             int i, index;
             int whichFrame = 4; // The frame that we want to get Class.method from.
             String tmp = null;

             // Weird lossage here. After touching EOControl, the
             // stackTrace has an extra frame at the beginning. See
             // comment at bottom of this class for an example
             if (stackTrace.indexOf("at java.lang.Throwable") > -1)
                 whichFrame++;

             tmp = stackTrace;
             for (i = 0; i < whichFrame; i++) {
                 index = tmp.indexOf("at ");
                 frame = tmp.substring(index + 3);
                 tmp = frame; 
             }
             index = frame.indexOf("(");
             frame = tmp.substring(0, index);
         }
         return frame;
     }

     protected static String _formattedDate() {	
         return formatter.format(new Date());
     }



     /**
      * Convenience method to aid in debugging tests.  Turns EOAdaptor SQL logging on or off.
      *
      * @param shouldLog - <code>true</code> if SQL logging should be turned on, <code>false</code> if it should be turned off.
      */
     public static void logSQL(boolean shouldLog)
     {
         if (shouldLog)
         {
             NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration
                                              | NSLog.DebugGroupDatabaseAccess
                                              | NSLog.DebugGroupEnterpriseObjects);
             NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelDetailed);
         }
         else
         {
             NSLog.refuseDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration
                                               | NSLog.DebugGroupDatabaseAccess
                                               | NSLog.DebugGroupEnterpriseObjects);
             NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelOff);
         }
     }

     
     /* Setters and Getters ***********************************************/

     /** Returns the application name that is printed in some of debugging modes. Default is "javaApp". */
     public static String applicationName() {
         return applicationName;
     }
     /** Sets the application name that is printed in some of debugging
      * modes. Application.init is a good place for this. (Note that
      * SMApplication does this for you!)  */
     public static void setApplicationName(String value) {
         applicationName = value;
     }

     /** The application-wide debug level. Calls to DebugOut.println
      * with the first parameter less than this will be printed. */
     public static int debugLevel() {
         return debugLevel;
     }
     /** Sets the application-wide debug level. */
     public static void setDebugLevel(int value) {
         debugLevel = value;
     }

     /** The debugging mode that you are in. Choices are <b>TERSE</b>, <b>NSLOG</b>,
      * <b>VERBOSE</b>, and <b>PONTIFICAL</b>. The mode determines the format that
      * your messages are printed in. It is recommended that you do not
      * use <b>VERBOSE</b> or <b>PONTIFICAL</b> in Deployment for performance
      * reasons. Default is <b>PONTIFICAL</b>. */
     public static int mode() {
         return mode;
     }
     /** Sets the debugging mode which determines the format that your
      * messages are printed in. Choices are <b>TERSE</b>, <b>NSLOG</b>, <b>VERBOSE</b>, and
      * <b>PONTIFICAL</b>. Default is <b>PONTIFICAL</b>. */
     public static void setMode(int value) {
         mode = value;
     }

     /** The printstream where you would like your output to
      * be sent. Default is System.err. */
     public static PrintStream out() {
         return out;
     }

     /** Sets the printstream where you would like your output to be
      *  sent. Default is System.err, but you can set it to any
      *  Printstream that you like, including System.out. */
     public static void setOut(PrintStream value) {
         out = value;
     }

     //  --------------------------------------------------------

     public static NSDictionary getMessageTypeList() {
         return messageTypeList;
     }

     public static void setMessageTypeList( NSDictionary aList ) {
         messageTypeList = new NSMutableDictionary( aList );
     }

     public static void addMessageType( String aType ) {
         if( messageTypeList == null ) {
             setMessageTypeList( new NSDictionary() );
         }
         messageTypeList.setObjectForKey( aType, aType );
     }

     public static void removeMessageType( String aType ) {
         if( isInList( aType ) )
             messageTypeList.removeObjectForKey( aType );
     }

     public static void resetMessageTypeList() {
         messageTypeList = null;
     }
 }

/*
Odd Stack trace problem.

OK. Here's an example of my output:

--------------------8-<-------cut-here---------8-<-----------------------
Mar 02 10:26:33 sitemaker[395] - Session.awake - NO CURRENT USER...
====================================
java.lang.Throwable: debug
	at com.apple.chicago.DebugOut.callerDescription(Compiled Code)
	at com.apple.chicago.DebugOut.println(DebugOut.java:65)
	at Session.fetchUserForUserID(Session.java:91)
	at Session.awake(Session.java:70)

====================================
Mar 02 10:26:33 sitemaker[395] - Session.fetchUserForUserID - Entering...
====================================
java.lang.Throwable: debug
	at java.lang.Throwable.<init>(Compiled Code)
	at com.apple.chicago.DebugOut.callerDescription(Compiled Code)
	at com.apple.chicago.DebugOut.println(DebugOut.java:65)
	at Session.fetchUserForUserID(Session.java:100)
	at Session.awake(Session.java:70)
--------------------8-<-------cut-here---------8-<-----------------------


Notice how suddenly the stack trace has the

      "	at java.lang.Throwable.<init>(Compiled Code)" 

line in it! This behavior started in WO 4.5 using JDK 1.1.8 and
Jikes. If you have any idea what's up, I'd love to know. -Fitz

*/
