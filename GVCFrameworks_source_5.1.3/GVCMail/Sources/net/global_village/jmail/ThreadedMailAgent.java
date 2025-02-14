package net.global_village.jmail;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.log4j.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;



/**
 * This class is used to send mails in a threaded way.  This is useful as sending multiple messages
 * can be time consuming.
 *
 * <p>Notifications are broadcast when messages are accepted by the SMTP server or when they fail to send.
 * </p>
 *
 * <p>These properties should be set for better recovery and notification.  The default timeouts are <b>forever</b>.</p>
 * <pre>
 * mail.smtp.connectiontimeout=15000
 * mail.smtp.timeout=15000
 * </pre>
 *
 * This code is adapted from ERMailSender from the ERJavaMail framework of Project Wonder.  This file
 * is protected under the GNU Lesser Public License.
 *
 * @see <a href="http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html">SMTP Package Summary</a>
 * @author Camille Troillard <tuscland@mac.com>
 * @author Tatsuya Kawano <tatsuyak@mac.com>
 * @author Max Muller <maxmuller@mac.com>
 * @author Sacha Mallais <sacha@global-village.net> DBC and clean up
 * @author Chuck Hill <chill@global-village.net> added failure recovery, notifications
 */
public class ThreadedMailAgent extends Thread
{
    /**
     * Notification broadcast when .
     * The notification object is the com.sun.mail.smtp.SMTPMessage that was abandoned.
     * The userInfo dictionary will have the exception that explains the latest failure under ExceptionKey
     *
     * @see #ExceptionKey
     */
    public static final String AbandonedMessageNotification = "net.global_village.jmail.ThreadedMailAgentAbandonedMessage";


    /**
     * Notification broadcast when a message is successfully accepted by the SMTP server.
     * The notification object is the com.sun.mail.smtp.SMTPMessage that was sent.
     */
    public static final String SentMessageNotification = "net.global_village.jmail.ThreadedMailAgentSentMessage";


    /**
     * Notification broadcast when a message is rejected by the SMTP server.
     * The notification object is the com.sun.mail.smtp.SMTPMessage that was rejected.
     * The userInfo dictionary will have the exception that explains the failure under ExceptionKey
     *
     * @see #ExceptionKey
     */
    public static final String FailedToSendMessageNotification = "net.global_village.jmail.ThreadedMailAgentFailedToSendMessage";


    /**
     * Notification broadcast when the SMTP server can't be contacted.  This may be a transient failure due to network or SMTP
     * server load issues.  The notification object is this ThreadedMailAgent object.
     */
    public static final String FailedToContactMailServerNotification = "net.global_village.jmail.ThreadedMailAgentFailedToContactMailServer";


    /**
     * Notification broadcast when the SMTP transport can't be obtained.  This should mean an configuration or host name problem.
     * The notification object is this ThreadedMailAgent object.
     */
    public static final String FailedToGetTransportNotification = "net.global_village.jmail.ThreadedMailAgentFailedToGetTransport";


    /**
     * Notification broadcast when this thread starts to send queued messages.  If you are watching the FailedToContactMailServerNotification,
     * this should mean the problem has been resolved until the next time FailedToContactMailServerNotification is broadcast.
     * The notification object is this ThreadedMailAgent object.
     */
    public static final String SendingMessagesNotification = "net.global_village.jmail.ThreadedMailAgentSendingMessages";


    /**
     * Key into notification userInfo() dictionary.  This is used with the FailedToSendMessageNotification and AbandonedMessageNotification.
     */
    public static final String ExceptionKey = "net.global_village.jmail.ThreadedMailAgentExceptionKey";


    protected static final long TRANSPORT_UNAVAILABLE_DELAY = 30 * 1000; // thirty seconds
    protected static final int MAX_RETRIES = 10;

    private static ThreadedMailAgent _sharedMailSender;

    private javax.mail.Session mailSession;       // Session used for all mail activities in this object

    // Holds sending messages
    private FIFOQueue messages;

    // For thread management
    private volatile boolean threadSuspended = false;
    private int milliSecondsWaitRunLoop = 5000;
    private volatile boolean shouldEnd = false;

    private Logger logger = LoggerFactory.makeLogger();



    /**
     * Internal class used to associate a Message with the number of times an attempt has been made
     * to send it.  This allows the mail agent to recover from transient errors without losing messages.
     */
    protected class MessageWithRetries
    {
        protected int retries;
        protected com.sun.mail.smtp.SMTPMessage message;

        public MessageWithRetries(com.sun.mail.smtp.SMTPMessage aMessage)
        {
            super();
            message = aMessage;
            retries = 0;
        }


        public com.sun.mail.smtp.SMTPMessage message()
        {
            return message;
        }


        public void recordAttempt()
        {
            retries++;
        }


        public int attemptsMade()
        {
            return retries;
        }


        public boolean hasExhaustedAttempts()
        {
            return retries >= MAX_RETRIES;
        }

    }



    /**
     * Exception class for alerting about a stack overflow
     */
    public static class SizeOverflowException extends RuntimeException
    {
		private static final long serialVersionUID = 4485089124281428814L;

		public SizeOverflowException () { super (); }
    }



    /**
     * Returns <code>true</code> if there is a shared instance, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there is a shared instance, <code>false</code> otherwise
     * @deprecated Not needed if ThreadedMailAgent constructor is used
     */
    public static boolean hasInstance()
    {
        return _sharedMailSender != null;
    }



    /**
     * Creates an instance of ThreadedMailAgent that sends mail to the indicated host using SMTP.  The message queue
     * size is limited to queueSize.  This instance can be accessed from getInstance().  The ThreadedMailAgent is effectively a singleton, though it
     * can't be created without parameters to the constructor.
     *
     * @param smtpHost the name of the SMTP mail server to use
     * @param queueSize the size of the mail queue
     * @deprecated Use createInstance() instead
     */
    public static void createInstance(String smtpHost, int queueSize)
    {
        /** require
        [not_already_created] ! hasInstance();
        [smtpHost_not_null] smtpHost != null; **/

        _sharedMailSender = new ThreadedMailAgent(smtpHost, queueSize);

        /** ensure [instance_not_null] _sharedMailSender != null; **/
    }



    /**
     * Creates an instance of ThreadedMailAgent that sends mail to the indicated host using SMTP.  The message queue
     * size is unlimited.  This instance can be accessed from getInstance().  The ThreadedMailAgent is effectively a singleton, though it
     * can't be created without parameters to the constructor.
     *
     * @param smtpHost the name of the SMTP mail server to use
     * @deprecated Use createInstance() instead
     *
     */
    public static void createInstance(String smtpHost)
    {
        /** require
        [not_already_created] ! hasInstance();
        [smtpHost_not_null] smtpHost != null; **/

        _sharedMailSender = new ThreadedMailAgent(smtpHost, 0);

        /** ensure [instance_not_null] _sharedMailSender != null; **/
    }



    /**
     * Returns the shared instance.
     *
     * @return the shared instance of the singleton ThreadedMailAgent object
     */
    public static ThreadedMailAgent getInstance()
    {
        if (_sharedMailSender == null)
        {
            _sharedMailSender = new ThreadedMailAgent();
        }
        return _sharedMailSender;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Constructs a ThreadedMailAgent based on the System properties with an unlimited queue size.  Some of the relevant properties are:
     * <ul>
     * <li>mail.smtp.host</li>
     * <li>mail.transport.protocol</li>
     * <li>mail.smtp.connectiontimeout</li>
     * <li>mail.smtp.connectiontimeout</li>
     * <li>mail.smtp.timeout</li>
     * </ul>
     */
    protected ThreadedMailAgent()
    {
        super("ThreadedMailAgent");

        setPriority(Thread.MIN_PRIORITY);
        messages = new FIFOQueue(0);
        mailSession = javax.mail.Session.getInstance(System.getProperties(), null);
        logger.info("Created ThreadedMailAgent from system properties, protocol is " + mailSession.getProperties().getProperty("mail.transport.protocol") + ", host is " + mailSession.getProperties().getProperty("mail.smtp.host"));
    }



    /**
     * Constructs a ThreadedMailAgent that sends mail to the indicated host using SMTP.
     * The message queue size is limited to queueSize.
     *
     * @param smtpHost the name of the SMTP mail server to use
     * @param queueSize the size of the mail queue
     * @deprecated use ThreadedMailAgent()
     */
    protected ThreadedMailAgent(String smtpHost, int queueSize)
    {
        super("ThreadedMailAgent");
        logger.info("Creating ThreadedMailAgent for SMTP, host is " + smtpHost);
        setPriority(Thread.MIN_PRIORITY);
        messages = new FIFOQueue(queueSize);

        Properties sessionProperties = (Properties) System.getProperties().clone();
        sessionProperties.put("mail.smtp.host", smtpHost);
        sessionProperties.put("mail.transport.protocol", "SMTP");

        mailSession = javax.mail.Session.getInstance(sessionProperties, null);
    }



    /**
     * Convenience method to create a new net.global_village.jmail.Message with this agent's session.
     *
     * @see Message
     * @return a new message with this agent's session
     */
    public Message newMessage()
    {
        return new Message(mailSession());
    }



    /**
     * Convenience method to create a new net.global_village.jmail.Message with this agent's session.
     *
     * @see Message
     * @param in the data of the message
     * @return a new message with this agent's session
     */
    public Message newMessage(InputStream in) throws MessagingException
    {
        return new Message(mailSession(), in);
    }



    /**
     * Sends a message in a non-blocking way.<br>
     * This means that the thread won't be blocked.  Instead the message will be queued for later delivery.
     *
     * <p>When the message is accepted by the transport without exception, the SentMessageNotification will be
     * broadcast for message.</p>
     *
     * @param message the message to send
     */
    public void sendMessageDeferred(com.sun.mail.smtp.SMTPMessage message) throws ThreadedMailAgent.SizeOverflowException
    {
        /** require [valid_message] message != null;  **/
        queueMessage(message);
        setThreadSuspended(false);

        // If we have not started to send mails, start the thread
        if ( ! isAlive())
        {
            start();
            return;
        }
    }



    /**
     * Queues the message for later delivery.
     *
     * @param message the message to queue
     * @throws ThreadedMailAgent.SizeOverflowException if the mail queue size is exceeded
     */
    protected void queueMessage(com.sun.mail.smtp.SMTPMessage message) throws ThreadedMailAgent.SizeOverflowException
    {
        /** require [valid_message] message != null;  **/
        try
        {
            messages().push(new MessageWithRetries(message));
            if (logger.isDebugEnabled())
            {
                try
                {
                   logger.debug("Added message titled " + message.getSubject() + ", to: " + prettyPrintAddresses(message.getAllRecipients()) + " to the mail queue.");
                }
                catch (MessagingException e)
                {
                    logger.debug("Failed to log adding message to queue", e);
                }
            }
        }
        catch(FIFOQueue.SizeOverflowException e)
        {
            logger.error("Mail queue is full, failed to add message to queue", e);
            throw new ThreadedMailAgent.SizeOverflowException();
        }
    }



    /**
     * Sends a message immediately.<br> This means that the thread could be blocked if the message
     * takes time to be delivered.  If the mail server can't be contacted the message is queued for
     * future delivery attempts.
     *
     * <p>When the message is accepted by the transport without exception, the SentMessageNotification will be
     * broadcast for message.</p>
     *
     * @param message the message to send
     * @throws ThreadedMailAgent.SizeOverflowException if the message can't be sent immediately and the mail queue size is exceeded
     */
    public void sendMessageNow(com.sun.mail.smtp.SMTPMessage message) throws ThreadedMailAgent.SizeOverflowException
    {
        Transport transport = null;
        try
        {
           transport = connectedTransport();
           if (transport != null)
           {
               _sendMessageNow(message, transport);
           }
           else
           {
               // If we cannot connect then queue the message and try again later.
               sendMessageDeferred(message);
           }
        }
        catch (MessagingException e)
        {
            logger.error("Caught exception when sending mail in a non-blocking manner.", e);
            throw new NSForwardException(e, "Caught exception when sending mail in a non-blocking manner.");
        }
        finally
        {
            if (transport != null)
            {
                try
                {
                    transport.close();
                }
                catch (MessagingException e)
                {
                    // Fatal exception ... we must at least notify the user
                    logger.error("Caught exception when closing transport.", e);
                    throw new NSForwardException(e, "Unable to open nor close the messaging transport channel.");
                }
            }
        }
    }



    /**
     * All messages are sent from this method.  It is used by 'sendMessageNow' and 'sendMessageDeffered' and
     * is the meat of the 'run' method when the thread is running.<br> If an exception occurs while
     * sending the mail, it is logged and thrown.
     *
     * <p>If the message is accepted by the transport without exception, the SentMessageNotification will be
     * broadcast for message.</p>
     * <p>If the message is rejected by the transport or sending fails for any reason, the FailedToSendMessageNotification will be
     * broadcast for message.</p>
     */
    protected void _sendMessageNow(com.sun.mail.smtp.SMTPMessage message, Transport transport) throws MessagingException
    {
        /** require [valid_message] message != null;
                    [valid_transport] transport != null;   **/

        try
        {
            transport.sendMessage(message, message.getAllRecipients());
            NSNotificationCenter.defaultCenter().postNotification(SentMessageNotification, message);

            if (logger.isDebugEnabled())
            {
                logger.debug("Message titled " + message.getSubject() + " sent to " + prettyPrintAddresses(message.getAllRecipients()));
            }
        }
        catch (SendFailedException e)
        {
            // BUG NOTE: this will not attempt to resend this message to e.getValidUnsentAddresses()
            NSNotificationCenter.defaultCenter().postNotification(FailedToSendMessageNotification, message, new NSDictionary(e, ExceptionKey));
            logger.error("Failed to send message titled " + message.getSubject() + " to " + prettyPrintAddresses(e.getInvalidAddresses()));
            logger.error("  - successfully sent message to " + prettyPrintAddresses(e.getValidSentAddresses()));
            logger.error("  - stopped before sending message to " + prettyPrintAddresses(e.getValidUnsentAddresses()), e);
        }
        catch (MessagingException e)
        {
            logger.error("Error while sending message titled " + message.getSubject() + " to " + prettyPrintAddresses(message.getAllRecipients()), e);
            logger.error("Cause: ", e.getCause());
            logger.error("Next: ", e.getNextException());
            throw e;
        }
        catch (Throwable t)
        {
            NSNotificationCenter.defaultCenter().postNotification(FailedToSendMessageNotification, message, new NSDictionary(t, ExceptionKey));
            logger.error("Unexpected error - failed to send message titled " + message.getSubject() + " to " + prettyPrintAddresses(message.getAllRecipients()), t);
            throw new ExceptionConverter(t);
        }
    }



    /**
     * Utility method that gets the SMTP Transport method for a session and connects the Transport
     * before returning it. Returns null if the transport can NOT be connected!  Can be retried later
     * if null is returned.
     *
     * <p>If the transport for SMTP is not found, the SendingMessagesNotification will be  broadcast for this ThreadedMailAgent.</p>
     * <p>If the transport can't be connected to, the FailedToContactMailServer will be  broadcast for this ThreadedMailAgent.</p>
     *
     * @return null or connected mail transport.
     */
    protected Transport connectedTransport()
    {
        Transport transport = null;
        try
        {
            transport = mailSession().getTransport("smtp");
        }
        catch (NoSuchProviderException e)
        {
            NSNotificationCenter.defaultCenter().postNotification(FailedToGetTransportNotification, this);

            logger.error("Unable to get the mail transport from the session.", e);
            throw new NSForwardException(e, "Unable to get the mail transport from the session.");
        }

        if ( ! transport.isConnected())
        {
            try
            {
                transport.connect();
            }
            catch (MessagingException e)
            {
                NSNotificationCenter.defaultCenter().postNotification(FailedToContactMailServerNotification, this);
                logger.error("Unable to connect transport due to", e);
                transport = null;
            }
        }

        return transport;
    }



    /**
     * Don't call this method, this is the thread run loop and is automatically called.
     *
     * <p>When transport is established and a cycle of sending queued messages is started, the SendingMessagesNotification will be
     * broadcast for this ThreadedMailAgent.</p>
     * <p>If a SocketException occurs while sending messages, the FailedToContactMailServer will be broadcast for this ThreadedMailAgent.</p>
     */
    public void run()
    {
        while( ! (messages().empty() && shouldEnd()))
        {
            if (shouldEnd())
            {
                logger.info("ThreadedMailAgent signaled to end, but still has to process " + messages().size() + " messages in queue.");
            }

            // The thread sleeps until sendMessageDeferred wakes it up.
            try
            {
                if(threadSuspended())
                {
                    synchronized(this)
                    {
                        while (threadSuspended())
                        {
                            sleep(milliSecondsWaitRunLoop());
                        }
                    }
                }
            }
            catch(InterruptedException e)
            {
                setThreadSuspended(true);  // Does this serve any useful purpose?
            }

            // If there are still messages pending ...
            if( ! messages().empty())
            {
                // This is a try block of last resort.  We must not allow run() to throw.
                try
                {
                    Transport transport = connectedTransport();

                    // If the transport is not available, sleep a bit and we will try again later.
                    if (transport == null)
                    {
                        logger.error("ThreadedMailAgent can't connect to SMTP server, sleeping...");
                        try
                        {
                            sleep(TRANSPORT_UNAVAILABLE_DELAY);
                            continue;
                        }
                        catch(InterruptedException e)
                        {
                            setThreadSuspended(true); // Does this serve any useful purpose?
                            continue;
                        }
                    }

                    NSNotificationCenter.defaultCenter().postNotification(SendingMessagesNotification, this);

                    while( ! messages().empty())
                    {
                        MessageWithRetries message = (MessageWithRetries)messages().pop();
                        try
                        {
                            _sendMessageNow(message.message(), transport);
                        }
                        catch(MessagingException e)
                        {
                            // Here we get all the exceptions that are not 'SendFailedException's.
                            // This does not seem to happen any more.  - CH 2008-4-3
                            if (e.getNextException() instanceof java.net.SocketException)
                            {
                                logger.error("Lost connection to mail server, non-fatal Socket Exception.");
                                // Hey!  Someone pulled the plug on us!  Bail out of the loop so a new connection will be made
                                // Re-queue the message, will try it again later.
                                messages().push(message);
                                // Discard the transport so that we don't attempt to close it.
                                transport = null;
                                break;
                            }

                            // Only record the attempt if it was not a socket exception.  It might still be a transient (network) failure,
                            // but we will assume it is not.
                            message.recordAttempt();

                            if (message.hasExhaustedAttempts())
                            {
                                NSNotificationCenter.defaultCenter().postNotification(FailedToContactMailServerNotification, this);
                                logger.error("Fatal Messaging Exception. Can't send the message after " + message.attemptsMade() + " attempts, abandoning.");
                                NSNotificationCenter.defaultCenter().postNotification(AbandonedMessageNotification, message.message(), new NSDictionary(e, ExceptionKey));
                            }
                            else
                            {
                                logger.error("Non-fatal Messaging Exception. Failed to send message on attempt " + message.attemptsMade());
                                // Re-queue the message, will try it again later.
                                messages().push(message);
                            }

                            // Get the transport to close and re-open so that we avoid sending nested messages
                            break;
                        }
                    }

                    try
                    {
                        if (transport != null)
                        {
                            transport.close();
                        }
                    }
                    catch(MessagingException e)
                    {
                        logger.error("Unable to close transport.  Perhaps it has already been closed? ", e);
                    }
                }
                // Can't allow this to happen, the thread will die.
                catch (Throwable t)
                {
                    logger.error("ThreadedMailAgent run() loop trying to throw unhandled exception: ", t);
                }
            }
            setThreadSuspended(messages().empty());
        }
    }



    /**
     * Call this to signal this tread to end as soon as it sends all of the messages in the queue.
     * Depending on what is in the queue, the thread might run for while
     */
    public void end()
    {
        logger.info("ThreadedMailAgent told to end, messages in queue: " + messages().size());
        shouldEnd = true;
        interrupt();
        /** ensure [shouldEnd] shouldEnd();  **/
    }



    /**
     * @return <code>true</code> if end() has been called
     */
    public boolean shouldEnd()
    {
        return shouldEnd;
    }



    /**
     * Debugging support method to return a nice string from an array of InternetAddress.
     *
     * @param addresses the addresses to pretty print
     * @return nicely formatted display string of addresses
     */
    protected String prettyPrintAddresses(Address[] addresses)
    {
        if ((addresses == null) || (addresses.length == 0))
        {
            return "(none)";
        }

        return InternetAddress.toString(addresses);
    }



    /**
     * @return session used for all mail activities
     */
    public javax.mail.Session mailSession()
    {
        return mailSession;
    }



    /**
     * @return FIFOQueue holding messages waiting to be sent
     */
    protected FIFOQueue messages()
    {
        return messages;
    }



    /**
     * Returns <code>true</code> if the thread was suspended due to an interrupt or
     * lack of messages to send.
     *
     * @return <code>true</code> if the thread was suspended
     */
    protected boolean threadSuspended()
    {
        return threadSuspended;
    }



    /**
     * Call to activate the mail sending function, or to suspend it if the thread was suspended due to an interrupt or
     * lack of messages to send.
     *
     * @param suspended <code>true</code> if the thread should be suspended
     */
    protected void setThreadSuspended(boolean suspended)
    {
        threadSuspended = suspended;
    }



    /**
     * @return number of milliseconds to wait before checking for new messages to send
     */
    protected int milliSecondsWaitRunLoop()
    {
        return milliSecondsWaitRunLoop;
    }



    /**
     * Sets the number of milliseconds to wait before checking for new messages to send.
     *
     * @param milliSeconds number of milliseconds to wait before checking for new messages to send
     */
    public void setMilliSecondsWaitRunLoop(int milliSeconds)
    {
        milliSecondsWaitRunLoop = milliSeconds;
    }


}
