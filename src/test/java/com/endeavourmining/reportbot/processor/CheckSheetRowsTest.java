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

import com.endeavourmining.reportbot.settings.ReportSettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link CheckSheetRowsTest}.
 *
 * @since 0.2
 */
@SuppressWarnings("PMD.AvoidFileStream")
final class CheckSheetRowsTest {

    /**
     * Temp dir.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @TempDir
    static Path temp;

    /**
     * Input stream of template.
     */
    private static InputStream itemplate;

    /**
     * Template in xslx.
     */
    private static XSSFWorkbook template;

    @BeforeAll
    static void setUp() throws IOException {
        final Path templatep = CheckSheetRowsTest.temp.resolve("report_sample.xlsx");
        FileUtils.copyFile(
            new File("./src/test/resources/report_sample.xlsx"),
            templatep.toFile()
        );
        CheckSheetRowsTest.itemplate = new FileInputStream(templatep.toFile());
        CheckSheetRowsTest.template = new XSSFWorkbook(CheckSheetRowsTest.itemplate);    }

    @Test
    void checksGoodReport() throws IOException {
        new CheckSheetRows(CheckSheetRowsTest.template)
            .validate(CheckSheetRowsTest.template.getSheet(ReportSettings.PHYSICALS_PLAN));
        new CheckSheetRows(CheckSheetRowsTest.template)
            .validate(CheckSheetRowsTest.template.getSheet(ReportSettings.PHYSICALS_ACTUAL));
    }

    @Test
    void checksReportWithMissingRows() throws IOException {
        final Path path = CheckSheetRowsTest.temp.resolve("report_missing_rows.xlsx");
        FileUtils.copyFile(
            new File(
                "src/test/resources/emails/with_report_missing_rows/report_missing_rows.xlsx"
            ),
            path.toFile()
        );
        try (InputStream input = new FileInputStream(path.toFile())) {
            final XSSFWorkbook report = new XSSFWorkbook(input);
            final IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new CheckSheetRows(CheckSheetRowsTest.template)
                    .validate(report.getSheet(ReportSettings.PHYSICALS_ACTUAL))
            );
            MatcherAssert.assertThat(
                thrown.getLocalizedMessage(),
                new IsEqual<>(
                    "Found 91 rows where expecting 92. :-( <br>"
                )
            );
        }
    }

    @Test
    void checksReportWithRowTitlesUpdated() throws IOException {
        final Path path = CheckSheetRowsTest.temp.resolve("report_row_title_updated.xlsx");
        FileUtils.copyFile(
            new File(
                // @checkstyle LineLengthCheck (1 line)
                "src/test/resources/emails/with_report_row_title_updated/report_row_title_updated.xlsx"
            ),
            path.toFile()
        );
        try (InputStream input = new FileInputStream(path.toFile())) {
            final XSSFWorkbook report = new XSSFWorkbook(input);
            final IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new CheckSheetRows(CheckSheetRowsTest.template)
                    .validate(report.getSheet(ReportSettings.PHYSICALS_PLAN))
            );
            MatcherAssert.assertThat(
                thrown.getLocalizedMessage(),
                new IsEqual<>(
                    // @checkstyle LineLengthCheck (1 line)
                    "At row 83, <u>Description</u> value is <b>Total Discharge changed</b> instead of <b>Total Discharge</b>"
                )
            );
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        CheckSheetRowsTest.itemplate.close();
    }
}
