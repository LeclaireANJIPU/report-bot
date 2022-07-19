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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.mail.Folder;
import javax.mail.Message;

/**
 * Mail processor chain.
 *
 * @since 0.1
 */
public final class MailProcessorChain implements MailProcessor {

    /**
     * Processors.
     */
    private final Collection<MailProcessor> processors;

    /**
     * Ctor.
     * @param processors Processors
     */
    public MailProcessorChain(final MailProcessor... processors) {
        this.processors = Arrays.asList(processors);
    }

    @Override
    public void process(final Message email, final Folder folder) throws IOException {
        for (final MailProcessor processor : this.processors) {
            processor.process(email, folder);
        }
    }
}
