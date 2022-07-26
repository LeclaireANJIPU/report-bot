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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

/**
 * Mime message from email.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public final class MimeMessageFromEmail extends MimeMessageWrap {

    /**
     * Ctor.
     * @param email Email
     * @param session Session
     * @throws MessagingException If fails
     */
    public MimeMessageFromEmail(
        final Email email, final Session session
    ) throws MessagingException {
        super(MimeMessageFromEmail.toMessage(email, session));
    }

    /**
     * Converts email to mime message.
     * @param email Email
     * @param session Session
     * @return Mime message
     */
    private static MimeMessage toMessage(final Email email, final Session session) {
        try {
            final Store store = session.getStore();
            store.connect();
            final Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            final Message[] messages = folder.search(
                new SearchTerm() {
                    @Override
                    public boolean match(final Message msg) {
                        try {
                            return email.uid().equals(
                                ((UIDFolder) folder).getUID(msg)
                            );
                        } catch (final MessagingException msge) {
                            throw new RuntimeException(msge);
                        }
                    }
                }
            );
            if (messages.length == 0) {
                throw new IllegalArgumentException(
                    String.format(
                        // @checkstyle LineLengthCheck (1 line)
                        "Sorry the original email (with uid %s) has been deleted by the administrator !",
                        email.uid()
                    )
                );
            }
            return (MimeMessage) messages[0];
        } catch (final MessagingException nspe) {
            throw new RuntimeException(nspe);
        }
    }
}
