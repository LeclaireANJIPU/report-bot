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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Mail settings from configuration file.
 *
 * @since 0.1
 */
public final class MailSettingsFromConfig extends Properties {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 4112578634029874840L;

    /**
     * Ctor.
     * @param config Configuration file path
     * @throws IOException If fails
     */
    public MailSettingsFromConfig(final File config) throws IOException {
        super(MailSettingsFromConfig.transform(config));
    }

    /**
     * Transform configuration file to properties.
     * @param config Configuration file
     * @return Properties
     * @throws IOException If fails
     */
    private static Properties transform(final File config) throws IOException {
        final Properties props = System.getProperties();
        props.putAll(new ImapSettingsFromConfig(config));
        props.putAll(new SmtpSettingsFromConfig(config));
        return props;
    }
}
