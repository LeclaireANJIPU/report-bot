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
package com.endeavourmining.reportbot.settings;

import com.amihaiemil.eoyaml.Yaml;
import com.endeavourmining.reportbot.FkEmail;
import java.io.File;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Satisfies;

/**
 * Test case for {@link YamlSites}.
 *
 * @since 0.1
 */
final class YamlSitesTest {

    @Test
    void getSiteOfEmail() throws IOException {
        MatcherAssert.assertThat(
            new YamlSites(
                Yaml.createYamlInput(
                    new File("./src/test/resources/settings.yml")
                ).readYamlMapping().yamlSequence("sites")
            ).siteOf(new FkEmail()),
            Matchers.allOf(
                new Satisfies<>(osite -> osite.isPresent()),
                new Satisfies<>(
                    osite -> {
                        final Site site = osite.get();
                        return site.name().equals("Abidjan Sud")
                            && site.abbreviated().equals("ABJS");
                    }
                )
            )
        );
    }
}
