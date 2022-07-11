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

import java.io.IOException;
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
 * Mailbox with only unread emails.
 *
 * @since 0.1
 */
public final class UnreadMailbox implements Mailbox {

    /**
     * Username or mail address.
     */
    private final String user;

    /**
     * Password.
     */
    private final String password;

    /**
     * Host.
     */
    private final String host;

    /**
     * Port.
     */
    private final int port;

    /**
     * Ctor.
     * @param host Host
     * @param port Port
     * @param user Username or mail address
     * @param password Password
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public UnreadMailbox(
        final String host, final int port,
        final String user, final String password
    ) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public int count() throws IOException {
        final Properties props = System.getProperties();
        props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        props.setProperty("mail.imaps.port", String.valueOf(this.port));
        props.setProperty("mail.imaps.socketFactory.port", String.valueOf(this.port));
        props.put("mail.imaps.host", this.host);
        final Session session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        UnreadMailbox.this.user,
                        UnreadMailbox.this.password
                    );
                }
            }
        );
        try {
            final Store store = session.getStore("imaps");
            store.connect();
            final Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            final Message[] messages = folder.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false)
            );
            folder.close(false);
            store.close();
            return messages.length;
        } catch (final MessagingException exe) {
            throw new IOException(exe);
        }
    }
}
