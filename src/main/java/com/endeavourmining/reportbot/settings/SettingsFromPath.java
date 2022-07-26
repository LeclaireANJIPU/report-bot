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
import com.amihaiemil.eoyaml.YamlMapping;
import com.endeavourmining.reportbot.JavaResource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Settings from path.
 *
 * @since 0.1
 */
public final class SettingsFromPath implements Settings {

    /**
     * Configuration file path.
     */
    private final Path path;

    /**
     * YAML content.
     */
    private final YamlMapping content;

    /**
     * Ctor.
     * @param path Path
     * @throws IOException If fails
     */
    public SettingsFromPath(final Path path) throws IOException {
        this.path = path;
        this.content = SettingsFromPath.load(path);
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public Properties mailSettings() {
        try {
            return new MailSettingsFromConfig(this.path.toFile());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Credentials mailCredentials() {
        return new YamlCredentials(
            this.content.yamlMapping("mailbox")
                .yamlMapping("credentials")
        );
    }

    @Override
    public ReportSettings report() {
        return new YamlReportSettings(
            this.content.yamlMapping("report")
        );
    }

    @Override
    public Sites sites() {
        return new YamlSites(
            this.content.yamlSequence("sites")
        );
    }

    @Override
    public Path storagePath() {
        final File file = new File(
            this.content.yamlMapping("storage").string("path")
        );
        final Path stopath;
        if (file.isAbsolute()) {
            stopath = file.toPath();
        } else {
            stopath = Paths.get(System.getProperty("user.dir"), file.getName());
        }
        return stopath;
    }

    /**
     * Load YAML file.
     * @param path Path
     * @return YAML content
     * @throws IOException If fails
     */
    private static YamlMapping load(final Path path) throws IOException {
        if (!Files.exists(path)) {
            new JavaResource("example/settings.yml").copy(path);
        }
        return Yaml.createYamlInput(path.toFile()).readYamlMapping();
    }
}
