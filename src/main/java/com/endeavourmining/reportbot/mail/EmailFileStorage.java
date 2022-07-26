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
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * Email file storage.
 *
 * @since 0.1
 */
public final class EmailFileStorage implements EmailStorage {

    /**
     * Text plain.
     */
    private static final String TEXT_PLAIN = "text/plain";

    /**
     * Time zone to use.
     */
    private static final String TIMEZONE = "GMT";

    /**
     * Folder where to store emails.
     */
    private final Path path;

    /**
     * Ctor.
     * @param path Folder where to store emails
     */
    public EmailFileStorage(final Path path) {
        this.path = path;
    }

    @Override
    @SuppressWarnings("PMD.AvoidFileStream")
    public Email save(final Message message, final Long uid) throws IOException {
        try {
            final Path ffolder = this.path.resolve(
                EmailStatus.TO_TREAT.name().toLowerCase(Locale.ENGLISH)
            ).resolve(uid.toString());
            ffolder.toFile().mkdirs();
            YamlSequenceBuilder attyml = Yaml.createYamlSequenceBuilder();
            if (message.getContentType().contains("multipart")) {
                final Multipart multipart = (Multipart) message.getContent();
                for (int idx = 0; idx < multipart.getCount(); idx += 1) {
                    final MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(idx);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        attyml = attyml.add(part.getFileName());
                        part.saveFile(
                            ffolder.resolve(
                                EmailFileStorage.sanitizeFilename(part.getFileName())
                            ).toFile()
                        );
                    }
                }
            }
            final YamlMapping yaml = Yaml.createYamlMappingBuilder()
                .add("uid", uid.toString())
                .add(
                    "sent_date",
                    message.getSentDate().toInstant()
                        .atZone(ZoneId.of(EmailFileStorage.TIMEZONE))
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                .add(
                    "received_date",
                    message.getReceivedDate().toInstant()
                        .atZone(ZoneId.of(EmailFileStorage.TIMEZONE))
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                .add("from", message.getFrom()[0].toString())
                .add("attachments", attyml.build())
                .build();
            Yaml.createYamlPrinter(
                new FileWriter(
                    ffolder.resolve("metadata.yml").toFile(),
                    StandardCharsets.UTF_8
                )
            ).print(yaml);
            try (
                FileWriter writer = new FileWriter(
                    ffolder.resolve("subject.txt").toFile(),
                    StandardCharsets.UTF_8
                )
            ) {
                writer.write(message.getSubject());
            }
            try (
                FileWriter writer = new FileWriter(
                    ffolder.resolve("content.txt").toFile(),
                    StandardCharsets.UTF_8
                )
            ) {
                writer.write(EmailFileStorage.textContentOf(message));
            }
            return new EmailFromPath(ffolder);
        } catch (final MessagingException exe) {
            throw new IOException(exe);
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public Iterable<Email> iterate(final EmailStatus status) throws IOException {
        try (Stream<Path> walk = Files.walk(this.path.resolve(status.name()))) {
            return walk.filter(Files::isDirectory)
                .map(
                    p -> {
                        try {
                            return new EmailFromPath(p);
                        } catch (final IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    }
                )
                .collect(Collectors.toList());
        }
    }

    /**
     * Get content of an email.
     * @param message Email
     * @return Text
     * @throws MessagingException If fails
     * @throws IOException If fails
     */
    private static String textContentOf(final Message message)
        throws MessagingException, IOException {
        final String content;
        if (message.isMimeType(EmailFileStorage.TEXT_PLAIN)) {
            content = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            content = textFromMimeMultipart((MimeMultipart) message.getContent());
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Mime type %s not supported of email content !", message.getContentType()
                )
            );
        }
        return content;
    }

    /**
     * Get text from an multipart content.
     * @param mimetype Multipart mime type
     * @return Text
     * @throws MessagingException If fails
     * @throws IOException If fails
     */
    private static String textFromMimeMultipart(
        final MimeMultipart mimetype
    ) throws MessagingException, IOException {
        final Collection<String> texts = new LinkedList<>();
        final int count = mimetype.getCount();
        for (int idx = 0; idx < count; idx += 1) {
            final BodyPart part = mimetype.getBodyPart(idx);
            if (part.isMimeType(EmailFileStorage.TEXT_PLAIN)) {
                texts.add((String) part.getContent());
                break;
            } else if (part.isMimeType("text/html")) {
                texts.add(
                    org.jsoup.Jsoup.parse((String) part.getContent()).text()
                );
            } else if (part.getContent() instanceof MimeMultipart) {
                texts.add(
                    textFromMimeMultipart((MimeMultipart) part.getContent())
                );
            }
        }
        return String.join(System.lineSeparator(), texts);
    }

    /**
     * Sanitizes filename.
     * @param name Filename
     * @return Cleaned filename
     */
    private static String sanitizeFilename(final String name) {
        return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
}
