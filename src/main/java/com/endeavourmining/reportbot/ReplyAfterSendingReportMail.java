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
import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.email.EmailBuilder;

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
     * Ctor.
     * @param credentials Credentials
     */
    public ReplyAfterSendingReportMail(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void process(final Message email) throws IOException {
        try {
            Transport.send(
                EmailConverter.emailToMimeMessage(
                    EmailBuilder.replyingTo((MimeMessage) email)
                        .from(this.credentials.login())
                        .prependTextHTML(
                            String.join(
                                "",
                                "Hello,<br>",
                                "<br>",
                                "Thanks you to submit your report. ",
                                "It will be processed very soon. :)<br>",
                                "<br>",
                                "Best regards,<br>",
                                "<br>",
                                "<b>Report Bot</b>",
                                "<br>",
                                "<br>",
                                "------------ Original email included below ------------",
                                "<br>"
                            )
                        )
                        .buildEmail(),
                    email.getSession()
                )
            );
        } catch (final MessagingException mes) {
            throw new IOException(mes);
        }
    }
}
