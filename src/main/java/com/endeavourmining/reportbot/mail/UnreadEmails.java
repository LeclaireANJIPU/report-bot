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
package com.endeavourmining.reportbot.mail;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

/**
 * Unread emails.
 *
 * @since 0.1
 */
public final class UnreadEmails implements Mailbox {

    /**
     * Mail server settings.
     */
    private final Properties settings;

    /**
     * Storage.
     */
    private final EmailStorage storage;

    /**
     * Emails fetched.
     */
    private boolean fetched;

    /**
     * List of messages.
     */
    private final List<Email> messages;

    /**
     * Ctor.
     * @param settings Mail server settings
     * @param storage Email storage
     */
    public UnreadEmails(
        final Properties settings, final EmailStorage storage
    ) {
        this.settings = settings;
        this.storage = storage;
        this.fetched = false;
        this.messages = new LinkedList<>();
    }

    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public int count() throws IOException {
        this.fetch();
        return this.messages.size();
    }

    @Override
    public Iterable<Email> iterate() throws IOException {
        this.fetch();
        return this.messages;
    }

    /**
     * Fetch unread emails.
     * @throws IOException If fails
     */
    private void fetch() throws IOException {
        if (!this.fetched) {
            final Session session = Session.getInstance(
                this.settings,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            UnreadEmails.this.settings.getProperty("mail.user"),
                            UnreadEmails.this.settings.getProperty("mail.password")
                        );
                    }
                }
            );
            try {
                final Store store = session.getStore();
                store.connect();
                final Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                for (
                    final Message message : folder.search(
                        new FlagTerm(new Flags(Flags.Flag.SEEN), false)
                    )
                ) {
                    this.messages.add(
                        new EmailFromPath(this.storage.save(message, folder))
                    );
                }
                folder.close(false);
                store.close();
            } catch (final MessagingException exe) {
                throw new IOException(exe);
            }
            this.fetched = true;
        }
    }
}
