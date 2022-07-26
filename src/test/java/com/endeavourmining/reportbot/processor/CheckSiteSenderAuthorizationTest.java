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

import com.endeavourmining.reportbot.AvailablePort;
import com.endeavourmining.reportbot.mail.EmailFileStorage;
import com.endeavourmining.reportbot.settings.SettingsFromPath;
import com.endeavourmining.reportbot.settings.Sites;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.IOException;
import java.nio.file.Path;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link CheckSiteSenderAuthorization}.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class CheckSiteSenderAuthorizationTest {

    /**
     * Mail server.
     */
    private GreenMail server;

    /**
     * Sites.
     */
    private Sites sites;

    @BeforeEach
    void setUp() throws IOException {
        final AvailablePort port = new AvailablePort();
        this.server = new GreenMail(
            new ServerSetup[] {
                ServerSetup.SMTP.withPort(port.intValue()),
                ServerSetup.IMAP.withPort(port.intValue()),
            }
        );
        this.server.start();
        this.sites = new SettingsFromPath(Path.of("./src/test/resources/settings.yml")).sites();
    }

    @Test
    void recognizesSender(@TempDir final Path temp) throws MessagingException, IOException {
        final Session ssmtp = this.server.getSmtp().createSession();
        final Message msg = new MimeMessage(ssmtp);
        msg.setFrom(new InternetAddress("roland.koffi@example.com"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("bar@example.com"));
        msg.setSubject("ABJS : Recognize me");
        msg.setText("Please, could you recognize me ?");
        Transport.send(msg);
        new CheckSiteSenderAuthorization(this.sites).process(
            new EmailFileStorage(temp).save(
                this.server.getReceivedMessages()[0], 1L
            )
        );
    }

    @Test
    void rejectsSender(@TempDir final Path temp) throws MessagingException {
        final Session ssmtp = this.server.getSmtp().createSession();
        final Message msg = new MimeMessage(ssmtp);
        msg.setFrom(new InternetAddress("toto@example.com"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("tata@example.com"));
        msg.setSubject("ABJS : Reject me");
        msg.setText("I'm testing you. :)");
        Transport.send(msg);
        final IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new CheckSiteSenderAuthorization(this.sites).process(
                new EmailFileStorage(temp).save(
                    this.server.getReceivedMessages()[0], 1L
                )
            )
        );
        MatcherAssert.assertThat(
            thrown.getLocalizedMessage(),
            new StringStartsWith(
                "You are not authorized to send a report on behalf of site <b>Abidjan Sud</b>"
            )
        );
    }

    @AfterEach
    void tearDown() {
        this.server.stop();
    }
}
