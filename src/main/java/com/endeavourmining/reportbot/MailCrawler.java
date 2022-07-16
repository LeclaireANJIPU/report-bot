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

import com.endeavourmining.reportbot.settings.Credentials;
import com.endeavourmining.reportbot.settings.MailServerSettings;
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
     * Mail server settings.
     */
    private final MailServerSettings settings;

    /**
     * Credentials.
     */
    private final Credentials credentials;

    /**
     * Mail processor.
     */
    private final MailProcessor processor;

    /**
     * Ctor.
     * @param settings Mail server settings
     * @param credentials Credentials
     * @param processor Mail processor
     */
    public MailCrawler(
        final MailServerSettings settings, final Credentials credentials,
        final MailProcessor processor
    ) {
        this.settings = settings;
        this.credentials = credentials;
        this.processor = processor;
    }

    /**
     * Start crawling.
     * @throws IOException If fails
     */
    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.EmptyCatchBlock"})
    public void start() throws IOException {
        final Properties props = System.getProperties();
        props.setProperty(
            String.format("mail.%s.socketFactory.fallback", this.settings.protocol()), "false"
        );
        props.setProperty(
            String.format("mail.%s.port", this.settings.protocol()),
            String.valueOf(this.settings.port())
        );
        props.setProperty(
            String.format("mail.%s.socketFactory.port", this.settings.protocol()),
            String.valueOf(this.settings.port())
        );
        props.put(
            String.format("mail.%s.host", this.settings.protocol()), this.settings.host()
        );
        final Session session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        MailCrawler.this.credentials.login(),
                        MailCrawler.this.credentials.password()
                    );
                }
            }
        );
        try {
            final Store store = session.getStore(this.settings.protocol());
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
                                this.processor.process(message);
                            } catch (final IOException ioe) {
                                throw new RuntimeException(ioe);
                            }
                        }
                    );
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
                                        MailCrawler.this.processor.process(message);
                                    } catch (final IOException ioe) {
                                        throw new RuntimeException(ioe);
                                    }
                                }
                            );
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
