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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.email.EmailBuilder;

/**
 * Mail replier with rich text content.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public final class RichTextMailReplier implements MailReplier {

    /**
     * From.
     */
    private final String from;

    /**
     * Ctor.
     * @param from From
     */
    public RichTextMailReplier(final String from) {
        this.from = from;
    }

    @Override
    public void reply(final Message email, final String response) {
        try {
            Transport.send(
                EmailConverter.emailToMimeMessage(
                    EmailBuilder.replyingTo((MimeMessage) email)
                        .from(this.from)
                        .prependTextHTML(
                            String.join(
                                "<br>",
                                "Hello,<br>",
                                response,
                                "Best regards,<br>",
                                "<b>Report Bot</b>",
                                "Version 0.1.0<br>",
                                "------------ Original email included below ------------<br>"
                            )
                        ).buildEmail(),
                    email.getSession()
                )
            );
        } catch (final MessagingException msge) {
            throw new RuntimeException(msge);
        }
    }
}
