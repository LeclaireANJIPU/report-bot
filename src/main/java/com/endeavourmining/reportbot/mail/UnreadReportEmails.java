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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Unread report emails.
 *
 * @since 0.1
 */
public final class UnreadReportEmails implements Mailbox {

    /**
     * Filter.
     */
    private final String filter;

    /**
     * Original mailbox.
     */
    private final Mailbox origin;

    /**
     * If filtered.
     */
    private boolean filtered;

    /**
     * List of unread report emails.
     */
    private final Collection<Email> emails;

    /**
     * Ctor.
     * @param filter Filter
     * @param origin Origin
     */
    public UnreadReportEmails(final String filter, final Mailbox origin) {
        this.filter = filter;
        this.origin = origin;
        this.filtered = false;
        this.emails = new LinkedList<>();
    }

    @Override
    public int count() throws IOException {
        return this.find().size();
    }

    @Override
    public Iterable<Email> iterate() throws IOException {
        return this.find();
    }

    /**
     * Filter emails.
     * @return Collection of emails
     * @throws IOException If fails
     */
    private Collection<Email> find() throws IOException {
        if (!this.filtered) {
            for (final Email email : this.origin.iterate()) {
                for (final String filename : email.attachmentFilenames()) {
                    if (
                        filename.toLowerCase(Locale.ENGLISH)
                            .contains(this.filter.toLowerCase(Locale.ENGLISH))
                    ) {
                        this.emails.add(email);
                    }
                }
            }
            this.filtered = true;
        }
        return this.emails;
    }
}
