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
    public static void main(final String[] args) throws IOException {
        final Settings settings = new SettingsFromPath(
            Paths.get(System.getProperty("user.dir"), "settings.yml")
        );
        final MailSettings msettings = settings.mailSettings();
        System.out.println(
            String.format(
                "Number of emails : %s",
                new UnreadEmails(
                    msettings.imapServerSettings().host(),
                    msettings.imapServerSettings().protocol(),
                    msettings.imapServerSettings().port(),
                    msettings.login(),
                    msettings.password()
                ).count()
            )
        );
    }
}
