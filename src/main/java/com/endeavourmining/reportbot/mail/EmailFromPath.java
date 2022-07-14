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
package com.endeavourmining.reportbot.mail;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Email from a path.
 *
 * @since 0.1
 */
public final class EmailFromPath implements Email {

    /**
     * Path.
     */
    private final Path path;

    /**
     * Mail metadata.
     */
    private final YamlMapping metadata;

    /**
     * Ctor.
     * @param path Path
     * @throws IOException If fails
     */
    public EmailFromPath(final Path path) throws IOException {
        this.path = path;
        this.metadata = Yaml.createYamlInput(
            path.resolve("metadata.yml").toFile()
        ).readYamlMapping();
    }

    @Override
    public String from() {
        return this.metadata.string("from");
    }

    @Override
    @SuppressWarnings("PMD.ShortMethodName")
    public String to() {
        return this.metadata.string("to");
    }

    @Override
    public LocalDateTime sentDate() {
        return this.metadata.dateTime("sent_date");
    }

    @Override
    public LocalDateTime receivedDate() {
        return this.metadata.dateTime("received_date");
    }

    @Override
    public String subject() {
        return this.metadata.string("subject");
    }

    @Override
    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidFileStream"})
    public String message() {
        try (
            FileReader reader = new FileReader(
                this.path.resolve("content.txt").toFile(), Charset.forName("UTF-8")
            )
        ) {
            return reader.toString();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Iterable<String> attachmentFilenames() {
        return this.metadata.yamlSequence("attachments")
            .values().stream().map(n -> n.asScalar().value())
            .collect(Collectors.toList());
    }

    @Override
    public InputStream load(final String filename) throws IOException {
        return null;
    }
}
