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
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;
import com.endeavourmining.reportbot.mail.Email;
import com.endeavourmining.reportbot.mail.EmailFromPath;
import com.endeavourmining.reportbot.mail.EmailStatus;
import com.endeavourmining.reportbot.settings.Credentials;
import com.endeavourmining.reportbot.settings.ReportSettings;
import com.endeavourmining.reportbot.settings.Site;
import com.endeavourmining.reportbot.settings.Sites;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.email.EmailBuilder;

/**
 * Processor to identity site.
 *
 * @since 0.1
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
public final class IdentifySiteProcessor implements MailProcessor {

    /**
     * Storage path.
     */
    private final Path path;

    /**
     * Report settings.
     */
    private final ReportSettings rsettings;

    /**
     * Sites.
     */
    private final Sites sites;

    /**
     * Credentials.
     */
    private final Credentials credentials;

    /**
     * Ctor.
     * @param path Storage path
     * @param rsettings Report settings
     * @param sites Sites
     * @param credentials Credentials
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public IdentifySiteProcessor(
        final Path path, final ReportSettings rsettings,
        final Sites sites, final Credentials credentials
    ) {
        this.path = path;
        this.rsettings = rsettings;
        this.sites = sites;
        this.credentials =  credentials;
    }

    @Override
    @SuppressWarnings(
        {
            "PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidFileStream",
            "PMD.AvoidDuplicateLiterals", "PMD.ExcessiveMethodLength"
        }
    )
    public void process(final Message message, final Folder folder) throws IOException {
        try {
            final Long uid = ((UIDFolder) folder).getUID(message);
            final Path emailf = this.path.resolve(
                EmailStatus.TO_TREAT.name().toLowerCase(Locale.ENGLISH)
            ).resolve(uid.toString());
            final Email email = new EmailFromPath(emailf);
            for (final String filename : email.attachmentFilenames()) {
                if (
                    filename.endsWith(
                        String.format(
                            "%s.%s", this.rsettings.suffix(), this.rsettings.extension()
                        )
                    )
                ) {
                    final String code = filename.split("_")[0];
                    if (this.sites.has(code)) {
                        final YamlMapping edited = new MergedYamlMapping(
                            Yaml.createYamlInput(
                                emailf.resolve("metadata.yml").toFile()
                            ).readYamlMapping(),
                            () -> Yaml.createYamlMappingBuilder()
                                .add("site", code)
                                .build()
                        );
                        try (
                            FileWriter writer = new FileWriter(
                                emailf.resolve("metadata.yml").toFile(),
                                StandardCharsets.UTF_8
                            )
                        ) {
                            writer.write(edited.toString());
                        }
                        final Path spath = this.path.resolve(
                            EmailStatus.SITE_IDENTIFIED.name().toLowerCase(Locale.ENGLISH)
                        );
                        spath.toFile().mkdirs();
                        Files.move(emailf, spath.resolve(uid.toString()));
                    } else {
                        final YamlMapping edited = new MergedYamlMapping(
                            Yaml.createYamlInput(
                                emailf.resolve("metadata.yml").toFile()
                            ).readYamlMapping(),
                            () -> Yaml.createYamlMappingBuilder()
                                .add("Step of failure", EmailStatus.SITE_IDENTIFIED.name())
                                .add("Reason of failure", "Wrong abbreviated site name")
                                .build()
                        );
                        try (
                            FileWriter writer = new FileWriter(
                                emailf.resolve("metadata.yml").toFile(),
                                StandardCharsets.UTF_8
                            )
                        ) {
                            writer.write(edited.toString());
                        }
                        final Path epath = this.path.resolve(
                            EmailStatus.ERROR.name().toLowerCase(Locale.ENGLISH)
                        );
                        epath.toFile().mkdirs();
                        Files.move(emailf, epath.resolve(uid.toString()));
                        // @checkstyle LineLengthCheck (50 lines)
                        Transport.send(
                            EmailConverter.emailToMimeMessage(
                                EmailBuilder.replyingTo((MimeMessage) message)
                                    .from(this.credentials.login())
                                    .prependTextHTML(
                                        String.join(
                                            "",
                                            "Hello,<br>",
                                            "<br>",
                                            "Thanks you to submit your report. ",
                                            "However, the site name abbreviation in the file name <span style=\"text-decoration: underline;\">does not match any of the registered sites</span>. :(<br>",
                                            String.format(
                                                "According to our principles, the name of the report to be attached must follow this rule: <b>&lt;abbreviated site name&gt;%s.%s<br></b>",
                                                this.rsettings.suffix(), this.rsettings.extension()
                                            ),
                                            "The sites registered are:<br>",
                                            IdentifySiteProcessor.resumeOfListOfSites(this.sites),
                                            "<br>",
                                            "Please make a correction and return the report to me.<br>",
                                            "<br>",
                                            "Best regards,<br>",
                                            "<br>",
                                            "<b>Report Bot</b><br>",
                                            "Version 0.1.0<br>",
                                            "<br>",
                                            "------------ Original email included below ------------",
                                            "<br>"
                                        )
                                    ).buildEmail(),
                                message.getSession()
                            )
                        );
                    }
                }
            }
        } catch (final MessagingException msge) {
            throw new RuntimeException(msge);
        }
    }

    /**
     * Resume list of sites.
     * @param sites Sites
     * @return Resume
     */
    private static String resumeOfListOfSites(final Sites sites) {
        final StringBuilder resume = new StringBuilder();
        for (final Site site : sites.iterate()) {
            resume.append("<li>")
                .append(String.format("%s - %s", site.abbreviated(), site.name()))
                .append("</li>");
        }
        return String.format("<ul>%s</ul>", resume);
    }
}
