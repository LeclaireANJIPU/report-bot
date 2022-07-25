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
import com.endeavourmining.reportbot.mail.EmailFromPath;
import com.endeavourmining.reportbot.settings.ReportSettings;
import com.endeavourmining.reportbot.settings.SettingsFromPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link CleanReport}.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class CleanReportTest {

    /**
     * Report settings.
     */
    private ReportSettings rsettings;

    @BeforeEach
    void setUp() throws IOException {
        this.rsettings = new SettingsFromPath(
            Path.of("./src/test/resources/settings.yml")
        ).report();
    }

    @Test
    void cleansLargeReport(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("with_large_report");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/with_large_report"),
            emailp.toFile()
        );
        final Email email = new EmailFromPath(emailp);
        new CleanReport(this.rsettings).process(email);
        try (InputStream input = email.report()) {
            final XSSFWorkbook workbook = new XSSFWorkbook(input);
            MatcherAssert.assertThat(
                "Physicals Plan should be at the first position",
                workbook.getSheetName(0),
                new IsEqual<>("Physicals Plan")
            );
            MatcherAssert.assertThat(
                "Physicals Actual should be at the second position",
                workbook.getSheetName(1),
                new IsEqual<>("Physicals Actual")
            );
        }
    }

    @Test
    void cleansUnorderedReport(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("with_simple_report_unordered");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/with_simple_report_unordered"),
            emailp.toFile()
        );
        final Email email = new EmailFromPath(emailp);
        new CleanReport(this.rsettings).process(email);
        try (InputStream input = email.report()) {
            final XSSFWorkbook workbook = new XSSFWorkbook(input);
            MatcherAssert.assertThat(
                "Physicals Plan should be at the first position",
                workbook.getSheetName(0),
                new IsEqual<>("Physicals Plan")
            );
            MatcherAssert.assertThat(
                "Physicals Actual should be at the second position",
                workbook.getSheetName(1),
                new IsEqual<>("Physicals Actual")
            );
        }
    }
}
