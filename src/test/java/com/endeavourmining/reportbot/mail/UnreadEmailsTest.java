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

import com.endeavourmining.reportbot.AvailablePort;
import com.endeavourmining.reportbot.FkCredentials;
import com.endeavourmining.reportbot.settings.Credentials;
import com.endeavourmining.reportbot.settings.MailSettingsFromConfig;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link UnreadEmails}.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class UnreadEmailsTest {

    /**
     * Mail server.
     */
    private GreenMail server;

    /**
     * Mail settings.
     */
    private Properties settings;

    @BeforeEach
    void setUp() throws IOException {
        this.server = new GreenMail(
            new ServerSetup[] {
                ServerSetup.SMTP.withPort(new AvailablePort().intValue()),
                ServerSetup.IMAP.withPort(new AvailablePort().intValue()),
            }
        );
        this.server.start();
        this.settings = new MailSettingsFromConfig(
            new File("./src/test/resources/settings.yml")
        );
        final int iport = this.server.getImap().getPort();
        this.settings.setProperty("mail.imap.port", String.valueOf(iport));
        this.settings.setProperty("mail.imap.socketFactory.port", String.valueOf(iport));
        final int sport = this.server.getSmtp().getPort();
        this.settings.setProperty("mail.smtp.port", String.valueOf(sport));
        this.settings.setProperty("mail.smtp.socketFactory.port", String.valueOf(sport));
    }

    @Test
    void getNumberOfUnreadEmails(@TempDir final Path temp) throws Exception {
        final Credentials credentials = new FkCredentials();
        final GreenMailUser user = this.server.setUser(
            "foo@example.com",
            credentials.login(),
            credentials.password()
        );
        final Session session = this.server.getSmtp().createSession();
        final Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("bar@example.com"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        msg.setSubject("Test 1");
        msg.setText("This is email for test 1 purpose.");
        Transport.send(msg);
        MatcherAssert.assertThat(
            new UnreadEmails(
                new SimpleMailConnector(credentials, this.settings),
                new EmailFileStorage(temp)
            ).count(),
            new IsEqual<>(1)
        );
    }

    @AfterEach
    void tearDown() {
        this.server.stop();
    }
}
