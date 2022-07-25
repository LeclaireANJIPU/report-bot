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

import com.endeavourmining.reportbot.mail.EmailFileStorage;
import com.endeavourmining.reportbot.mail.MailConnector;
import com.endeavourmining.reportbot.mail.RichTextMailReplier;
import com.endeavourmining.reportbot.mail.SimpleMailConnector;
import com.endeavourmining.reportbot.processor.CheckIfReportEmail;
import com.endeavourmining.reportbot.processor.CheckSiteSenderAuthorization;
import com.endeavourmining.reportbot.processor.CleanReport;
import com.endeavourmining.reportbot.processor.ExtractWeekFromSubject;
import com.endeavourmining.reportbot.processor.IdentifySite;
import com.endeavourmining.reportbot.processor.MailProcessorChain;
import com.endeavourmining.reportbot.processor.ReplyAfterSendingReportMail;
import com.endeavourmining.reportbot.settings.Settings;
import com.endeavourmining.reportbot.settings.SettingsFromPath;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Entrance.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings({"PMD.ProhibitPublicStaticMethods", "PMD.SystemPrintln"})
public final class Main {

    /**
     * Ctor.
     */
    private Main() {
        // Nothing here
    }

    /**
     * Main function.
     * @param args Arguments
     * @throws IOException If fails
     * @checkstyle MagicNumberCheck (10 lines)
     */
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public static void main(final String[] args) throws IOException {
        final Settings settings = new SettingsFromPath(
            Paths.get(
                System.getProperty("user.dir"), "settings.yml"
            )
        );
        final MailConnector connector = new SimpleMailConnector(
            settings.mailCredentials(), settings.mailSettings()
        );
        while (true) {
            try {
                new MailCrawler(
                    connector,
                    new EmailFileStorage(settings.storagePath()),
                    new MailProcessorChain(
                        new CheckIfReportEmail(
                            settings.report(),
                            new MailProcessorChain(
                                new IdentifySite(
                                    settings.storagePath(), settings.sites()
                                ),
                                new CheckSiteSenderAuthorization(settings.sites()),
                                new ExtractWeekFromSubject(),
                                new CleanReport(settings.report()),
                                new ReplyAfterSendingReportMail(
                                    settings.mailCredentials(), connector
                                )
                            )
                        )
                    ),
                    new RichTextMailReplier(
                        settings.mailCredentials().login()
                    )
                ).start();
            } catch (final IOException ioe) {
                // @checkstyle MethodBodyCommentsCheck (1 line)
                // do nothing
            }
        }
    }
}
