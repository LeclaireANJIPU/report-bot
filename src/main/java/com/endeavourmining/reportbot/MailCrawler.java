/*
 * Copyright (c) 2022 Endeavour Mining
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.endeavourmining.reportbot;

import com.sun.mail.imap.IMAPFolder;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.search.FlagTerm;

/**
 * Mail crawler.
 *
 * @since 0.1
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
public final class MailCrawler {

    /**
     * Ping delay.
     */
    private static final int PING_DELAY = 25;

    /**
     * Settings.
     */
    private final Properties settings;

    /**
     * Mail processor.
     */
    private final MailProcessor processor;

    /**
     * Ctor.
     * @param settings Mail server settings
     * @param processor Mail processor
     */
    public MailCrawler(
        final Properties settings, final MailProcessor processor
    ) {
        this.settings = settings;
        this.processor = processor;
    }

    /**
     * Start crawling.
     * @throws IOException If fails
     */
    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.EmptyCatchBlock"})
    public void start() throws IOException {
        final Session session = Session.getInstance(
            this.settings,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        MailCrawler.this.settings.getProperty("mail.user"),
                        MailCrawler.this.settings.getProperty("mail.password")
                    );
                }
            }
        );
        try {
            final Store store = session.getStore();
            store.connect();
            final Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            do {
                for (
                    final Message message : folder.search(
                        new FlagTerm(new Flags(Flags.Flag.SEEN), false)
                    )
                ) {
                    new Thread(
                        () -> {
                            try {
                                this.processor.process(message, folder);
                            } catch (final IOException ioe) {
                                throw new RuntimeException(ioe);
                            }
                        }
                    ).start();
                }
            } while (folder.hasNewMessages());
            folder.addMessageCountListener(
                new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(final MessageCountEvent evs) {
                        for (final Message message : evs.getMessages()) {
                            new Thread(
                                () -> {
                                    try {
                                        MailCrawler.this.processor.process(message, folder);
                                    } catch (final IOException ioe) {
                                        throw new RuntimeException(ioe);
                                    }
                                }
                            ).start();
                        }
                    }
                }
            );
            while (folder.isOpen()) {
                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final Runnable ping = () -> {
                    try {
                        folder.getMessageCount();
                    } catch (final MessagingException exe) {
                        // @checkstyle MethodBodyCommentsCheck (1 line)
                        // do nothing
                    }
                };
                scheduler.schedule(ping, MailCrawler.PING_DELAY, TimeUnit.MINUTES);
                ((IMAPFolder) folder).idle();
            }
            folder.close(false);
            store.close();
        } catch (final MessagingException exe) {
            throw new IOException(exe);
        }
    }
}
