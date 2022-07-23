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
import com.endeavourmining.reportbot.settings.Site;
import com.endeavourmining.reportbot.settings.Sites;
import java.io.IOException;
import java.util.Optional;

/**
 * Processor to check sender authorization.
 *
 * @since 0.1
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
public final class CheckSiteSenderAuthorization implements MailProcessor {

    /**
     * Sites.
     */
    private final Sites sites;

    /**
     * Ctor.
     * @param sites Sites
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public CheckSiteSenderAuthorization(final Sites sites) {
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
        if (!osite.isEmpty()) {
            final Site site = osite.get();
            if (!site.authorize(email.from())) {
                throw new IllegalArgumentException(
                    String.join(
                        "<br>",
                        String.format(
                            // @checkstyle LineLengthCheck (1 line)
                            "You are not authorized to send a report on behalf of site <b>%s</b>.<br> ",
                            site.name()
                        ),
                        "To be able to do that, please send a request to the administrator.<br>"
                    )
                );
            }
        }
    }
}
