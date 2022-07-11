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

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mail settings from path.
 *
 * @since 0.1
 */
public final class MailSettingsFromPath implements MailSettings {

    /**
     * Origin.
     */
    private final MailSettings origin;

    /**
     * Ctor.
     * @param path Path
     * @throws IOException If fails
     */
    public MailSettingsFromPath(final Path path) throws IOException {
        this.origin = new YamlMailSettings(
            MailSettingsFromPath.load(path)
        );
    }

    @Override
    public String login() {
        return this.origin.login();
    }

    @Override
    public String mailAddress() {
        return this.origin.mailAddress();
    }

    @Override
    public String password() {
        return this.origin.password();
    }

    @Override
    public String host() {
        return this.origin.host();
    }

    @Override
    public String protocol() {
        return this.origin.protocol();
    }

    @Override
    public int port() {
        return this.origin.port();
    }

    /**
     * Load YAML file.
     * @param path Path
     * @return YAML content
     * @throws IOException If fails
     */
    private static YamlMapping load(final Path path) throws IOException {
        if (!Files.exists(path)) {
            new JavaResource("example/receive_mailbox_settings.yml").copy(path);
        }
        return Yaml.createYamlInput(path.toFile()).readYamlMapping()
            .yamlMapping("mailbox");
    }
}
