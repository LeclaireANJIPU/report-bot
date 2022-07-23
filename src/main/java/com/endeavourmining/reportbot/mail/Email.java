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
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Email.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface Email {

    /**
     * Get unique identifier.
     * @return UID
     */
    Long uid();

    /**
     * From.
     * @return Mail address
     */
    String from();

    /**
     * To.
     * @return Mail address
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    String to();

    /**
     * Sent date.
     * @return Datetime
     */
    LocalDateTime sentDate();

    /**
     * Received date.
     * @return Datetime
     */
    LocalDateTime receivedDate();

    /**
     * Subject.
     * @return Subject
     */
    String subject();

    /**
     * Message.
     * @return Text
     */
    String message();

    /**
     * List of attached filenames.
     * @return Iterable
     */
    Iterable<String> attachmentFilenames();

    /**
     * Load an attached file.
     * @param filename Filename
     * @return Input stream
     * @throws IOException If fails
     */
    InputStream load(String filename) throws IOException;

    /**
     * Add custom metadata.
     * @param name Name
     * @param value Value
     * @throws IOException If fails
     */
    void addCustomMetadata(String name, String value) throws IOException;

    /**
     * Get a custom metadata.
     * @param name Name
     * @return Value
     * @throws IOException If fails
     */
    String customMetadata(String name) throws IOException;
}
