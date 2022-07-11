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

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.Security;
import java.util.Random;
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

/**
 * Test case for {@link UnreadMailbox}.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class UnreadMailboxTest {

    /**
     * Mail server.
     */
    private GreenMail server;

    @BeforeEach
    void setUp() throws IOException {
        Security.setProperty(
            "ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName()
        );
        this.server = new GreenMail(
            new ServerSetup[] {
                ServerSetup.SMTPS.withPort(UnreadMailboxTest.availablePort()),
                ServerSetup.IMAPS.withPort(UnreadMailboxTest.availablePort()),
            }
        );
        this.server.start();
    }

    @Test
    void getNumberOfUnreadEmails() throws Exception {
        final GreenMailUser user = this.server.setUser(
            "bar@example.com",
            "bar",
            "pwd"
        );
        final Session session = this.server.getSmtps().createSession();
        final Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("foo@example.com"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        msg.setSubject("Test 1");
        msg.setText("This is email for test 1 purpose.");
        Transport.send(msg);
        MatcherAssert.assertThat(
            new UnreadMailbox(
                this.server.getImaps().getBindTo(), this.server.getImaps().getPort(),
                user.getLogin(), user.getPassword()
            ).count(),
            new IsEqual<>(1)
        );
    }

    @AfterEach
    void tearDown() {
        this.server.stop();
    }

    /**
     * Get available port.
     * @return Port
     * @throws IOException If fails
     */
    private static int availablePort() throws IOException {
        final int min = 1000;
        final int max = 2000;
        final Random random = new Random();
        do {
            try (ServerSocket srv =
                new ServerSocket(random.nextInt(max - min + 1) + max)) {
                return srv.getLocalPort();
            }
        } while (true);
    }
}
