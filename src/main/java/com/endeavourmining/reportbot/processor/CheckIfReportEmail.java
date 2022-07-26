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
import com.endeavourmining.reportbot.settings.ReportSettings;
import java.io.IOException;
import java.util.Locale;

/**
 * This processor checks if email is about reporting.
 *
 * @since 0.1
 */
public final class CheckIfReportEmail implements MailProcessor {

    /**
     * Report settings.
     */
    private final ReportSettings rsettings;

    /**
     * Action if email is about reporting.
     */
    private final MailProcessor action;

    /**
     * Ctor.
     * @param rsettings Report settings
     * @param action Action
     */
    public CheckIfReportEmail(
        final ReportSettings rsettings,
        final MailProcessor action
    ) {
        this.rsettings = rsettings;
        this.action = action;
    }

    @Override
    public void process(final Email email) throws IOException {
        boolean hasreport = false;
        for (final String filename : email.attachmentFilenames()) {
            if (
                filename.endsWith(
                    String.format(".%s", this.rsettings.extension())
                )
            ) {
                hasreport = true;
                break;
            }
        }
        if (
            hasreport || email.subject().toLowerCase(Locale.ENGLISH).contains("report")
        ) {
            this.action.process(email);
        }
    }
}
