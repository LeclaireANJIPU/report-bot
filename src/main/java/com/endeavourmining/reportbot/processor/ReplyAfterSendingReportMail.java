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
package com.endeavourmining.reportbot.processor;

import com.endeavourmining.reportbot.mail.Email;
import com.endeavourmining.reportbot.mail.MailConnector;
import com.endeavourmining.reportbot.mail.MimeMessageFromEmail;
import com.endeavourmining.reportbot.mail.RichTextMailReplier;
import com.endeavourmining.reportbot.settings.Credentials;
import java.io.IOException;
import javax.mail.MessagingException;

/**
 * Reply after sending report mail.
 *
 * @since 0.1
 */
public final class ReplyAfterSendingReportMail implements MailProcessor {

    /**
     * Credentials.
     */
    private final Credentials credentials;

    /**
     * Connector.
     */
    private final MailConnector connector;

    /**
     * Ctor.
     * @param credentials Credentials
     * @param connector Mail connector
     */
    public ReplyAfterSendingReportMail(
        final Credentials credentials, final MailConnector connector
    ) {
        this.credentials = credentials;
        this.connector = connector;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void process(final Email email) throws IOException {
        try {
            new RichTextMailReplier(this.credentials.login()).reply(
                new MimeMessageFromEmail(email, this.connector.session()),
                "Thanks you to submit your report. It will be processed very soon. :)<br>"
            );
        } catch (final MessagingException msge) {
            throw new IOException(msge);
        }
    }
}