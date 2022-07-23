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

import com.endeavourmining.reportbot.mail.Email;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Fake email.
 *
 * @since 0.1
 * @checkstyle MagicNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class FkEmail implements Email {

    @Override
    public Long uid() {
        return 1L;
    }

    @Override
    public String from() {
        return "toto@example.com";
    }

    @Override
    @SuppressWarnings("PMD.ShortMethodName")
    public String to() {
        return "foo@example.com";
    }

    @Override
    public LocalDateTime sentDate() {
        return LocalDateTime.of(
            2022, 07, 12,
            12, 15, 17
        );
    }

    @Override
    public LocalDateTime receivedDate() {
        return LocalDateTime.of(
            2022, 07, 12,
            12, 15, 15, 23
        );
    }

    @Override
    public String subject() {
        return "ABJS : Report of week 04/07/2022 - 10/07/2022";
    }

    @Override
    public String message() {
        return "This is my report.";
    }

    @Override
    public Iterable<String> attachmentFilenames() {
        return new LinkedList<>();
    }

    @Override
    public InputStream load(final String filename) throws IOException {
        return null;
    }

    @Override
    // @checkstyle EmptyLinesCheck (2 lines)
    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    public void addCustomMetadata(final String name, final String value) throws IOException {
    }

    @Override
    public String customMetadata(final String name) throws IOException {
        return null;
    }
}
