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
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Email from a path.
 *
 * @since 0.1
 */
@SuppressWarnings(
    {
        "PMD.AvoidThrowingRawExceptionTypes", "PMD.TooManyMethods",
        "PMD.AvoidFileStream"
    }
)
public final class EmailFromPath implements Email {

    /**
     * Metadata file name.
     */
    private static final String METADATA_FILENAME = "metadata.yml";

    /**
     * Path.
     */
    private final Path path;

    /**
     * Mail metadata.
     */
    private final AtomicReference<YamlMapping> metadata;

    /**
     * Ctor.
     * @param path Path
     * @throws IOException If fails
     */
    public EmailFromPath(final Path path) throws IOException {
        this.path = path;
        this.metadata = new AtomicReference<>(
            Yaml.createYamlInput(
                path.resolve(EmailFromPath.METADATA_FILENAME).toFile()
            ).readYamlMapping()
        );
    }

    @Override
    public Long uid() {
        return this.metadata.get().longNumber("uid");
    }

    @Override
    public String from() {
        return this.metadata.get().string("from");
    }

    @Override
    @SuppressWarnings("PMD.ShortMethodName")
    public String to() {
        return this.metadata.get().string("to");
    }

    @Override
    public LocalDateTime sentDate() {
        return this.metadata.get().dateTime("sent_date");
    }

    @Override
    public LocalDateTime receivedDate() {
        return this.metadata.get().dateTime("received_date");
    }

    @Override
    public String subject() {
        try {
            return Files.readString(
                this.path.resolve("subject.txt"),
                StandardCharsets.UTF_8
            );
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidFileStream"})
    public String message() {
        try {
            return Files.readString(
                this.path.resolve("content.txt"),
                StandardCharsets.UTF_8
            );
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Iterable<String> attachmentFilenames() {
        return this.metadata.get().yamlSequence("attachments")
            .values().stream().map(n -> n.asScalar().value())
            .collect(Collectors.toList());
    }

    @Override
    public InputStream load(final String filename) throws IOException {
        return new FileInputStream(this.path.resolve(filename).toFile());
    }

    @Override
    public void addCustomMetadata(final String name, final String value) throws IOException {
        this.metadata.set(
            new MergedYamlMapping(
                Yaml.createYamlInput(
                    this.path.resolve(EmailFromPath.METADATA_FILENAME).toFile()
                ).readYamlMapping(),
                () -> Yaml.createYamlMappingBuilder()
                    .add(name, value)
                    .build()
            )
        );
        Yaml.createYamlPrinter(
            new FileWriter(
                this.path.resolve(EmailFromPath.METADATA_FILENAME).toFile(),
                StandardCharsets.UTF_8
            )
        ).print(this.metadata.get());
    }

    @Override
    public String customMetadata(final String name) throws IOException {
        return this.metadata.get().string(name);
    }

    @Override
    public InputStream report() {
        try (Stream<Path> walk = Files.walk(this.path)) {
            final Optional<Path> rpath = walk
                .filter(
                    p -> Files.isRegularFile(p)
                        && p.toString().contains(String.format("report_%s", this.uid()))
                ).findFirst();
            if (rpath.isEmpty()) {
                throw new IllegalArgumentException("The report hasn't been cleaned yet !");
            }
            return new FileInputStream(rpath.get().toFile());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public OutputStream report(final String extension) {
        try {
            return new FileOutputStream(
                this.path.resolve(String.format("report_%s.%s", this.uid(), extension)).toFile()
            );
        } catch (final FileNotFoundException fnf) {
            throw new RuntimeException(fnf);
        }
    }
}
