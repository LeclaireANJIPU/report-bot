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
 * YAML credentials.
 *
 * @since 0.1
 */
public final class YamlCredentials implements Credentials {

    /**
     * YAML content.
     */
    private final YamlMapping yaml;

    /**
     * Ctor.
     * @param yaml YAML content
     */
    public YamlCredentials(final YamlMapping yaml) {
        this.yaml = yaml;
    }

    @Override
    public String login() {
        return this.yaml.string("login");
    }

    @Override
    public String password() {
        return this.yaml.string("password");
    }
}
