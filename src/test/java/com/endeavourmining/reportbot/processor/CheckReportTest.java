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

import com.endeavourmining.reportbot.mail.EmailFromPath;
import com.endeavourmining.reportbot.settings.ReportSettings;
import com.endeavourmining.reportbot.settings.SettingsFromPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link CheckReport}.
 *
 * @since 0.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidFileStream"})
final class CheckReportTest {

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
    void checksReportWithRowTitleUpdated(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("with_report_row_title_updated");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/with_report_row_title_updated"),
            emailp.toFile()
        );
        final Path templatep = temp.resolve("report_sample.xlsx");
        FileUtils.copyFile(
            new File("./src/test/resources/report_sample.xlsx"),
            templatep.toFile()
        );
        try (InputStream itemplate = new FileInputStream(templatep.toFile())) {
            final XSSFWorkbook template = new XSSFWorkbook(itemplate);
            final IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new MailProcessorChain(
                    new CleanReport(this.rsettings),
                    new CheckReport(
                        new CheckSheetRows(template)
                    )
                ).process(new EmailFromPath(emailp))
            );
            MatcherAssert.assertThat(
                thrown.getLocalizedMessage(),
                new IsEqual<>(
                    String.join(
                        "",
                        "<h2>Physicals Plan</h2><br>",
                        "<ul>",
                        // @checkstyle LineLengthCheck (1 line)
                        "<li>At row 83, <u>Description</u> value is <b>Total Discharge changed</b> instead of <b>Total Discharge</b></li>",
                        "</ul>",
                        "<h2>Physicals Actual</h2><br>",
                        "<ul>",
                        // @checkstyle LineLengthCheck (1 line)
                        "<li>At row 68, <u>Description</u> value is <b>Total Genset Power Consumed changed</b> instead of <b>Total Genset Power Consumed</b></li>",
                        "</ul>"
                    )
                )
            );
        }
    }
}
