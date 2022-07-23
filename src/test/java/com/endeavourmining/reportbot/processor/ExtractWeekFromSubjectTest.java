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

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.endeavourmining.reportbot.mail.EmailFromPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link ExtractWeekFromSubject}.
 *
 * @since 0.1
 */
final class ExtractWeekFromSubjectTest {

    @Test
    void findsPeriod(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("with_only_good_subject");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/with_only_good_subject"),
            emailp.toFile()
        );
        new ExtractWeekFromSubject().process(
            new EmailFromPath(emailp)
        );
        final YamlMapping yaml = Yaml.createYamlInput(
            emailp.resolve("metadata.yml").toFile()
        ).readYamlMapping();
        MatcherAssert.assertThat(
            "Start week should match",
            yaml.string("start_week"),
            new IsEqual<>("04/07/2022")
        );
        MatcherAssert.assertThat(
            "End week should match",
            yaml.string("end_week"),
            new IsEqual<>("10/07/2022")
        );
    }

    @Test
    void forgotToWritePeriod(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("simple");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/simple"),
            emailp.toFile()
        );
        final IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new ExtractWeekFromSubject().process(
                new EmailFromPath(emailp)
            )
        );
        MatcherAssert.assertThat(
            thrown.getLocalizedMessage(),
            new StringStartsWith(
                // @checkstyle LineLengthCheck (1 line)
                "Sorry, you forgot to include the week of your report or you put wrong dates in the subject of your email. :-("
            )
        );
    }

    @Test
    void findsInvalidWeek(@TempDir final Path temp) throws IOException {
        final Path emailp = temp.resolve("with_invalid_week");
        FileUtils.copyDirectory(
            new File("./src/test/resources/emails/with_invalid_week"),
            emailp.toFile()
        );
        final IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new ExtractWeekFromSubject().process(
                new EmailFromPath(emailp)
            )
        );
        MatcherAssert.assertThat(
            thrown.getLocalizedMessage(),
            new StringStartsWith(
                "Sorry, the week you enter is not valid. :-("
            )
        );
    }
}
