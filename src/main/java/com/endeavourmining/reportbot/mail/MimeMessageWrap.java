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
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

/**
 * Mime message wrapper.
 *
 * @since 0.1
 */
public abstract class MimeMessageWrap extends MimeMessage {

    /**
     * Origin.
     */
    private final MimeMessage origin;

    /**
     * Ctor.
     * @param origin Origin
     * @throws MessagingException If fails
     */
    public MimeMessageWrap(final MimeMessage origin) throws MessagingException {
        super(origin);
        this.origin = origin;
    }

    @Override
    public final Address[] getFrom() throws MessagingException {
        return this.origin.getFrom();
    }

    @Override
    public final void setFrom() throws MessagingException {
        this.origin.setFrom();
    }

    @Override
    public final void setFrom(final Address address) throws MessagingException {
        this.origin.setFrom(address);
    }

    @Override
    public final void addFrom(final Address[] addresses) throws MessagingException {
        this.origin.addFrom(addresses);
    }

    @Override
    public final String getSubject() throws MessagingException {
        return this.origin.getSubject();
    }

    @Override
    public final void setSubject(final String subject) throws MessagingException {
        this.origin.setSubject(subject);
    }

    @Override
    public final Date getSentDate() throws MessagingException {
        return this.origin.getSentDate();
    }

    @Override
    public final void setSentDate(final Date date) throws MessagingException {
        this.origin.setSentDate(date);
    }

    @Override
    public final Date getReceivedDate() throws MessagingException {
        return this.origin.getReceivedDate();
    }

    @Override
    public final Flags getFlags() throws MessagingException {
        return this.origin.getFlags();
    }

    @Override
    public final void setFlags(final Flags flags, final boolean enable) throws MessagingException {
        this.origin.setFlags(flags, enable);
    }

    @Override
    public final Message reply(final boolean enable) throws MessagingException {
        return this.origin.reply(enable);
    }

    @Override
    public final void saveChanges() throws MessagingException {
        this.origin.saveChanges();
    }

    @Override
    public final int getSize() throws MessagingException {
        return this.origin.getSize();
    }

    @Override
    public final int getLineCount() throws MessagingException {
        return this.origin.getLineCount();
    }

    @Override
    public final String getContentType() throws MessagingException {
        return this.origin.getContentType();
    }

    @Override
    public final boolean isMimeType(final String value) throws MessagingException {
        return this.origin.isMimeType(value);
    }

    @Override
    public final String getDisposition() throws MessagingException {
        return this.origin.getDisposition();
    }

    @Override
    public final void setDisposition(final String value) throws MessagingException {
        this.origin.setDisposition(value);
    }

    @Override
    public final String getDescription() throws MessagingException {
        return this.origin.getDescription();
    }

    @Override
    public final void setDescription(final String value) throws MessagingException {
        this.origin.setDescription(value);
    }

    @Override
    public final String getFileName() throws MessagingException {
        return this.origin.getFileName();
    }

    @Override
    public final void setFileName(final String filename) throws MessagingException {
        this.origin.setFileName(filename);
    }

    @Override
    public final InputStream getInputStream() throws IOException, MessagingException {
        return this.origin.getInputStream();
    }

    @Override
    public final DataHandler getDataHandler() throws MessagingException {
        return this.origin.getDataHandler();
    }

    @Override
    public final Object getContent() throws IOException, MessagingException {
        return this.origin.getContent();
    }

    @Override
    public final void setDataHandler(final DataHandler handler) throws MessagingException {
        this.origin.setDataHandler(handler);
    }

    @Override
    public final void setContent(final Object obj, final String value) throws MessagingException {
        this.origin.setContent(obj, value);
    }

    @Override
    public final void setText(final String text) throws MessagingException {
        this.origin.setText(text);
    }

    @Override
    public final void setContent(final Multipart multipart) throws MessagingException {
        this.origin.setContent(multipart);
    }

    @Override
    public final void writeTo(final OutputStream output) throws IOException, MessagingException {
        this.origin.writeTo(output);
    }

    @Override
    public final String[] getHeader(final String name) throws MessagingException {
        return this.origin.getHeader(name);
    }

    @Override
    public final void setHeader(final String name, final String value) throws MessagingException {
        this.origin.setHeader(name, value);
    }

    @Override
    public final void addHeader(final String name, final String value) throws MessagingException {
        this.origin.addHeader(name, value);
    }

    @Override
    public final void removeHeader(final String name) throws MessagingException {
        this.origin.removeHeader(name);
    }

    @Override
    public final Enumeration<Header> getAllHeaders() throws MessagingException {
        return this.origin.getAllHeaders();
    }

    @Override
    public final Enumeration<Header> getMatchingHeaders(
        final String[] strings
    ) throws MessagingException {
        return this.origin.getMatchingHeaders(strings);
    }

    @Override
    public final Enumeration<Header> getNonMatchingHeaders(
        final String[] strings
    ) throws MessagingException {
        return this.origin.getNonMatchingHeaders(strings);
    }
}
