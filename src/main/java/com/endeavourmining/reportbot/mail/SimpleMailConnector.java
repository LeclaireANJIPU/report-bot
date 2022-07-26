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

import com.endeavourmining.reportbot.settings.Credentials;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * Simple mail connector.
 *
 * @since 0.1
 */
public final class SimpleMailConnector implements MailConnector {

    /**
     * Session.
     */
    private final Session sess;

    /**
     * Ctor.
     * @param credentials Credentials
     * @param props Properties
     */
    public SimpleMailConnector(
        final Credentials credentials, final Properties props
    ) {
        this.sess = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        credentials.login(),
                        credentials.password()
                    );
                }
            }
        );
    }

    @Override
    public Session session() {
        return this.sess;
    }
}
