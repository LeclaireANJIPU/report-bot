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

import com.amihaiemil.eoyaml.YamlMapping;

/**
 * IMAP settings in YAML.
 *
 * @since 0.1
 */
public final class YamlImapServerSettings implements MailServerSettings {

    /**
     * YAML content.
     */
    private final YamlMapping content;

    /**
     * Ctor.
     * @param content YAML content
     */
    public YamlImapServerSettings(final YamlMapping content) {
        this.content = content;
    }

    @Override
    public String host() {
        return this.content.string("host");
    }

    @Override
    public String protocol() {
        final String ptl;
        if (
            Boolean.parseBoolean(
                this.content.string("ssl_tls")
            )
        ) {
            ptl = "imaps";
        } else {
            ptl = "imap";
        }
        return ptl;
    }

    @Override
    public int port() {
        return this.content.integer("port");
    }
}
