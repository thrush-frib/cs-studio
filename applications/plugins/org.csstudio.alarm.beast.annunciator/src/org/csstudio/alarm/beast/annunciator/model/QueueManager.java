/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import org.csstudio.utility.speech.Annunciator;
import org.csstudio.utility.speech.AnnunciatorFactory;
import org.csstudio.utility.speech.Translation;

/** Queue Manager for the JMS-to-speech tool, messages are read from the queue
 *  @author Katia Danilova
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class QueueManager implements Runnable
{  
    /** Code used to wake the queue manager; not spoken */
    final static private String MAGIC_EXIT_MESSAGE = "PleaseDoExitNow?!";
   
    final private JMSAnnunciatorListener listener;
    final private SpeechPriorityQueue queue;
    final private Translation translations[];
    final private int threshold;
    final private Thread thread;

    /** Set to <code>false</code> to stop thread */
    private volatile boolean run = true;
   
    /** Initialize Queue Manager
     *  @param listener Listener
     *  @param queue SpeechPriorityQueue where the messages and Severity information will arrive
     *  @param translations Translations to use or <code>null</code>
     *  @param threshold max. number of queues messages to allow
     */   
    public QueueManager(final JMSAnnunciatorListener listener,
                        final SpeechPriorityQueue queue,
                        final Translation translations[],
                        final int threshold)
    {
        this.listener = listener;
        this.queue = queue;
        this.translations = translations;
        this.threshold = threshold;
        // Handle queue in background thread
        thread = new Thread(this, "Annunciation QueueManager");
        thread.setDaemon(true);
        thread.start();      
    }
   
    /** method run is the code to be executed by new thread */
    public void run()
    {
        // The main application will NOT exit when this thread exits.
        // onMessage will continue to receive messages,
        // just this thread will no longer be around to say anything.
        // So it would be good to try whatever to keep running.
        //
        // Maybe log the exception, but put another while (true)
        // around everything so it will try again.
        //
        // OR:
        // Turn Application.run into a static, then set it to "false"
        // in case of errors in here, so that the whole application will
        // stop, because having the application continue to run while
        // the thread that does the actual talking has died will
        // lead to confusion.
        while (true) // Wait for anybody to add messages to the queue
        {
            Annunciator speech = null;
            try
            {
                // Create annunciator
                speech = AnnunciatorFactory.getAnnunciator();
                if (translations != null)
                    speech.setTranslations(translations);

                while (true) // Wait for anybody to add messages to the queue
                {
                    // Retrieves and removes the head of this queue, waiting if
                    // no elements are present on this queue.
                    AnnunciationMessage qc = queue.poll();
                    String message = qc.getMessage();

                    // Exit requested?
                    if (!run)
                        return;
                    // Log and speak message off queue
                    listener.performedAnnunciation(qc);
                    speech.say(message);

                    // See if the set threshold for messages waiting in the
                    // queue has been exceeded.
                    if (queue.size() > threshold)
                    {
                        // Speak messages marked as 'standout'
                        int flurry = 0;
                        while (queue.size() > 0)
                        {
                            qc = queue.poll();
                            if (qc.isStandoutMessage())
                            {
                                // Log and speak message off queue
                                message = qc.getMessage();
                                listener.performedAnnunciation(qc);
                                speech.say(message);
                            }
                            else
                                ++flurry;
                        }
                        final String more = "There are " + flurry
                            + " more messages";
                        listener.performedAnnunciation(new AnnunciationMessage(Severity.forInfo(), more));
                        speech.say(more);
                    }
                }
            }
            catch (Exception ex)
            {
                listener.annunciatorError(ex);
            }
            finally
            {
                if (speech != null)
                    speech.close();
            }
        }
    }

    /** Add magic message to request thread to exit */
    public void stop()
    {
        // Indicate that thread should quit
        run = false;
        queue.add(Severity.forInfo(), MAGIC_EXIT_MESSAGE);
        try
        {
            // Wait for the thread to exit
            thread.join();
        }
        catch (InterruptedException ex)
        {
            // NOPs
        }
    }
}
