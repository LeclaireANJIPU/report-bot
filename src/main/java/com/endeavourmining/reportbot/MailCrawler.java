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

import com.endeavourmining.reportbot.mail.EmailStorage;
import com.endeavourmining.reportbot.mail.MailConnector;
import com.endeavourmining.reportbot.mail.MailReplier;
import com.endeavourmining.reportbot.processor.MailProcessor;
import com.sun.mail.imap.IMAPFolder;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.UIDFolder;
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
    private final MailConnector connector;

    /**
     * Storage for emails.
     */
    private final EmailStorage storage;

    /**
     * Mail processor.
     */
    private final MailProcessor processor;

    /**
     * Mail replier.
     */
    private final MailReplier replier;

    /**
     * Ctor.
     * @param connector Connector
     * @param storage Storage
     * @param processor Processor
     * @param replier Replier
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public MailCrawler(
        final MailConnector connector, final EmailStorage storage,
        final MailProcessor processor, final MailReplier replier
    ) {
        this.connector = connector;
        this.storage = storage;
        this.processor = processor;
        this.replier = replier;
    }

    /**
     * Start crawling.
     * @throws IOException If fails
     */
    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.EmptyCatchBlock"})
    public void start() throws IOException {
        try {
            final Store store = this.connector.session().getStore();
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
                        // @checkstyle AnonInnerLengthCheck (30 lines)
                        () -> {
                            try {
                                this.processor.process(
                                    this.storage.save(
                                        message, ((UIDFolder) folder).getUID(message)
                                    )
                                );
                            } catch (final IllegalArgumentException iae) {
                                this.replier.reply(
                                    message, iae.getLocalizedMessage()
                                );
                            } catch (final IOException | MessagingException exe) {
                                throw new RuntimeException(exe);
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
                                        MailCrawler.this.processor.process(
                                            MailCrawler.this.storage.save(
                                                message, ((UIDFolder) folder).getUID(message)
                                            )
                                        );
                                    } catch (final IllegalArgumentException iae) {
                                        MailCrawler.this.replier.reply(
                                            message, iae.getLocalizedMessage()
                                        );
                                    } catch (final IOException | MessagingException exe) {
                                        throw new RuntimeException(exe);
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
