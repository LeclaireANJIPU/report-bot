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

import com.endeavourmining.reportbot.settings.Settings;
import com.endeavourmining.reportbot.settings.SettingsFromPath;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Entrance.
 *
 * @since 0.1
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
        while (true) {
            try {
                new MailCrawler(
                    settings.mailSettings(),
                    new MailProcessorChain(
                        new StoreMail(settings.storagePath()),
                        new ReplyAfterSendingReportMail(settings.mailCredentials()),
                        new IdentifySiteProcessor(
                            settings.storagePath(), settings.report(), settings.sites(),
                            settings.mailCredentials()
                        )
                    )
                ).start();
            } catch (final IOException ioe) {
                // @checkstyle MethodBodyCommentsCheck (1 line)
                // do nothing
            }
        }
    }
}
