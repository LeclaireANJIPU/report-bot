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
package com.endeavourmining.reportbot.processor;

import com.endeavourmining.reportbot.mail.Email;
import com.endeavourmining.reportbot.mail.EmailStatus;
import com.endeavourmining.reportbot.settings.Site;
import com.endeavourmining.reportbot.settings.Sites;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

/**
 * Processor to identity site.
 *
 * @since 0.1
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
public final class IdentifySite implements MailProcessor {

    /**
     * Current step.
     */
    private static final String STEP = "Identify site";

    /**
     * Storage path.
     */
    private final Path path;

    /**
     * Sites.
     */
    private final Sites sites;

    /**
     * Ctor.
     * @param path Storage path
     * @param sites Sites
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public IdentifySite(final Path path, final Sites sites) {
        this.path = path;
        this.sites = sites;
    }

    @Override
    @SuppressWarnings(
        {
            "PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidFileStream",
            "PMD.AvoidDuplicateLiterals", "PMD.ExcessiveMethodLength"
        }
    )
    public void process(final Email email) throws IOException {
        final Optional<Site> osite = this.sites.siteOf(email);
        if (osite.isEmpty()) {
            email.addCustomMetadata("Step of failure", IdentifySite.STEP);
            email.addCustomMetadata("Reason of failure", "Unable to identify the site");
            final Path dest = this.path.resolve(
                EmailStatus.ERROR.name().toLowerCase(Locale.ENGLISH)
            );
            dest.toFile().mkdirs();
            Files.move(
                this.path.resolve(
                    EmailStatus.TO_TREAT.name().toLowerCase(Locale.ENGLISH)
                ).resolve(email.uid().toString()),
                dest.resolve(email.uid().toString())
            );
            throw new IllegalArgumentException(
                String.join(
                    "<br>",
                    "The site could not be identified.",
                    "According to our principles, the subject of your email must follow this rule:",
                    // @checkstyle LineLengthCheck (1 line)
                    "<b>&lt;site abbreviated or full name&gt; : Report of week &lt;begin date&gt; - &lt;end date&gt;<br></b>",
                    "<i>Example: </i> Ity : Report of week 11/07/2022 - 17/07/2022<br>",
                    "The sites registered are:",
                    IdentifySite.resumeOfListOfSites(this.sites),
                    "Please fix the issue and bring back the report."
                )
            );
        } else {
            final Site site = osite.get();
            email.addCustomMetadata("site", site.abbreviated());
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
