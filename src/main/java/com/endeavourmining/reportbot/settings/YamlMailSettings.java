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

import com.amihaiemil.eoyaml.YamlMapping;

/**
 * Mail settings in YAML content.
 *
 * @since 0.1
 */
public final class YamlMailSettings implements MailSettings {

    /**
     * YAML file content.
     */
    private final YamlMapping content;

    /**
     * Ctor.
     * @param content YAML content
     */
    public YamlMailSettings(final YamlMapping content) {
        this.content = content;
    }

    @Override
    public Credentials credentials() {
        return new YamlCredentials(
            this.content.yamlMapping("credentials")
        );
    }

    @Override
    public String address() {
        return this.content.string("address");
    }

    @Override
    public MailServerSettings smtpServerSettings() {
        return new YamlSmtpServerSettings(
            this.content.yamlMapping("smtp_server")
        );
    }

    @Override
    public MailServerSettings imapServerSettings() {
        return new YamlImapServerSettings(
            this.content.yamlMapping("imap_server")
        );
    }
}
