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
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clean the report from other sheets and re-order.
 *
 * @since 0.1
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle NestedIfDepthCheck (500 lines)
 */
@SuppressWarnings(
    {
        "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidDuplicateLiterals"
    }
)
public final class CleanReport implements MailProcessor {

    /**
     * Physicals plan.
     */
    private static final String PHYSICALS_PLAN = "Physicals Plan";

    /**
     * Physicals actual.
     */
    private static final String PHYSICALS_ACTUAL = "Physicals Actual";

    /**
     * Report settings.
     */
    private final ReportSettings rsettings;

    /**
     * Ctor.
     * @param rsettings Report settings
     */
    public CleanReport(final ReportSettings rsettings) {
        this.rsettings = rsettings;
    }

    @Override
    public void process(final Email email) throws IOException {
        final List<String> reports = new LinkedList<>();
        for (final String filename : email.attachmentFilenames()) {
            if (filename.endsWith(String.format(".%s", this.rsettings.extension()))) {
                reports.add(filename);
            }
        }
        if (reports.isEmpty()) {
            throw new IllegalArgumentException(
                String.join(
                    "<br>",
                    "I couldn't find a report attached. :-( <br>"
                )
            );
        } else if (reports.size() > 1) {
            throw new IllegalArgumentException(
                String.join(
                    "<br>",
                    "I'm able to process only one report in a same email. :-( <br>"
                )
            );
        } else {
            try (InputStream input = email.load(reports.get(0))) {
                final XSSFWorkbook report = new XSSFWorkbook(input);
                while (report.getNumberOfSheets() > 2) {
                    for (int idx = 0; idx < report.getNumberOfSheets(); idx = idx + 1) {
                        if (
                            !report.getSheetName(idx).equals(CleanReport.PHYSICALS_PLAN)
                                && !report.getSheetName(idx).equals(CleanReport.PHYSICALS_ACTUAL)
                        ) {
                            report.removeSheetAt(idx);
                            break;
                        }
                    }
                }
                if (report.getNumberOfSheets() == 1) {
                    throw new IllegalArgumentException(
                        String.join(
                            "<br>",
                            "We are waiting for at least two sheets in the report. :-( <br>"
                        )
                    );
                } else {
                    for (int idx = 0; idx < report.getNumberOfSheets(); idx = idx + 1) {
                        if (
                            !report.getSheetName(idx).equals(CleanReport.PHYSICALS_PLAN)
                                && !report.getSheetName(idx).equals(CleanReport.PHYSICALS_ACTUAL)
                        ) {
                            throw new IllegalArgumentException(
                                String.join(
                                    "<br>",
                                    String.format(
                                        // @checkstyle LineLengthCheck (1 line)
                                        "The two required sheets have to be named <b>%s</b> and <b>%s</b>. :-( <br>",
                                        CleanReport.PHYSICALS_PLAN,
                                        CleanReport.PHYSICALS_ACTUAL
                                    )
                                )
                            );
                        }
                    }
                    if (report.getSheetName(0).equals(CleanReport.PHYSICALS_ACTUAL)) {
                        report.setSheetOrder(CleanReport.PHYSICALS_PLAN, 0);
                    }
                    try (OutputStream output = email.report(this.rsettings.extension())) {
                        report.write(output);
                    }
                }
            }
        }
    }
}
