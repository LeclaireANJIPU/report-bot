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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Checks report in order to validate it.
 *
 * @since 0.2
 */
public final class CheckReport implements MailProcessor {

    /**
     * Validators.
     */
    private final Iterable<SheetValidator> validators;

    /**
     * Ctor.
     * @param validators Validators
     */
    public CheckReport(final SheetValidator... validators) {
        this.validators = Arrays.asList(validators);
    }

    @Override
    public void process(final Email email) throws IOException {
        try (InputStream input = email.report()) {
            final Map<String, Collection<String>> errors = new LinkedHashMap<>();
            final XSSFWorkbook report = new XSSFWorkbook(input);
            for (
                final String name : Arrays.asList(
                    ReportSettings.PHYSICALS_PLAN, ReportSettings.PHYSICALS_ACTUAL
                )
            ) {
                final Collection<String> serrors = new LinkedList<>();
                final XSSFSheet sheet = report.getSheet(name);
                for (final SheetValidator validator : this.validators) {
                    try {
                        validator.validate(sheet);
                    } catch (final IllegalArgumentException iae) {
                        serrors.addAll(
                            Arrays.asList(iae.getLocalizedMessage().split("<br>"))
                        );
                    }
                }
                if (!serrors.isEmpty()) {
                    errors.put(name, serrors);
                }
            }
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(CheckReport.formatError(errors));
            }
        }
    }

    /**
     * Format error message.
     * @param errors Errors by sheet
     * @return Message
     */
    private static String formatError(final Map<String, Collection<String>> errors) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, Collection<String>> entry : errors.entrySet()) {
            final StringBuilder list = new StringBuilder();
            for (final String line : entry.getValue()) {
                list.append(String.format("<li>%s</li>", line));
            }
            builder.append(String.format("<h2>%s</h2><br>", entry.getKey()))
                .append(
                    String.format("<ul>%s</ul>", list)
                );
        }
        return builder.toString();
    }
}
