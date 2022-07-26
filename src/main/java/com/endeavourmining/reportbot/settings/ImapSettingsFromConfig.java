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
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * IMAP settings from configuration file.
 *
 * @since 0.1
 */
public final class ImapSettingsFromConfig extends Properties {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 4112578634029874840L;

    /**
     * Ctor.
     * @param config Configuration file path
     * @throws IOException If fails
     */
    public ImapSettingsFromConfig(final File config) throws IOException {
        super(ImapSettingsFromConfig.transform(config));
    }

    /**
     * Transform config file to properties.
     * @param config Configuration file path
     * @return Properties
     * @throws IOException If fails
     */
    private static Properties transform(final File config) throws IOException {
        final Properties props = System.getProperties();
        final YamlMapping mailbox = Yaml.createYamlInput(config)
            .readYamlMapping()
            .yamlMapping("mailbox");
        final YamlMapping yaml = mailbox.yamlMapping("imap_server");
        final String protocol;
        final String authtype = yaml.string("authentication_type");
        if (authtype.equals("ssl")) {
            protocol = "imaps";
        } else {
            protocol = "imap";
        }
        if (Boolean.valueOf(authtype.equals("tls"))) {
            props.put(
                String.format("mail.%s.starttls.enable", protocol),
                "true"
            );
        }
        final int port = yaml.integer("port");
        props.setProperty(
            String.format("mail.%s.socketFactory.fallback", protocol), "false"
        );
        props.setProperty(
            String.format("mail.%s.port", protocol),
            String.valueOf(port)
        );
        props.setProperty(
            String.format("mail.%s.socketFactory.port", protocol),
            String.valueOf(port)
        );
        props.put(
            String.format("mail.%s.host", protocol), yaml.string("host")
        );
        props.put("mail.store.protocol", protocol);
        final YamlMapping credentials = mailbox.yamlMapping("credentials");
        props.put("mail.user", credentials.string("login"));
        props.put("mail.password", credentials.string("password"));
        return props;
    }
}
