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
import java.net.ServerSocket;
import java.util.Random;

/**
 * Available port.
 *
 * @since 0.1
 */
public final class AvailablePort extends Number implements Comparable<AvailablePort> {

    /**
     * Serial.
     */
    private static final long serialVersionUID = -8742448824652078965L;

    /**
     * Default minimum port.
     */
    private static final int DEFAULT_MIN_PORT = 1000;

    /***
     * Default maximum port.
     */
    private static final int DEFAULT_MAX_PORT = 2000;

    /**
     * Minimum accepted port value.
     */
    private static final int MIN_ACCEPTED_PORT = 22;

    /**
     * Maximum accepted port value.
     */
    private static final int MAX_ACCEPTED_PORT = 99_999;

    /**
     * Port.
     */
    private final int port;

    /**
     * Ctor.
     * Generates a port on default range.
     * @throws IOException If fails
     */
    public AvailablePort() throws IOException {
        this(AvailablePort.DEFAULT_MIN_PORT, AvailablePort.DEFAULT_MAX_PORT);
    }

    /**
     * Ctor.
     * @param min Minimum port
     * @param max Maximum port
     * @throws IOException If fails
     */
    public AvailablePort(final int min, final int max) throws IOException {
        this(AvailablePort.generate(min, max));
    }

    /**
     * Ctor.
     * @param port Port
     */
    private AvailablePort(final int port) {
        this.port = port;
    }

    @Override
    public int intValue() {
        return this.port;
    }

    @Override
    public long longValue() {
        return this.port;
    }

    @Override
    public float floatValue() {
        return this.port;
    }

    @Override
    public double doubleValue() {
        return this.port;
    }

    @Override
    public int compareTo(final AvailablePort other) {
        return this.port - other.intValue();
    }

    /**
     * Generate an available port.
     * @param min Minimum port
     * @param max Maximum port
     * @return Port
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static int generate(final int min, final int max) throws IOException {
        if (min < AvailablePort.MIN_ACCEPTED_PORT || max < AvailablePort.MIN_ACCEPTED_PORT) {
            throw new IllegalArgumentException(
                String.format(
                    "Port must be greater or equal to %s !",
                    AvailablePort.MIN_ACCEPTED_PORT
                )
            );
        }
        if (min > max) {
            throw new IllegalArgumentException(
                "Port range: minimum port must be less than maximum port !"
            );
        }
        if (min > AvailablePort.MAX_ACCEPTED_PORT || max > AvailablePort.MAX_ACCEPTED_PORT) {
            throw new IllegalArgumentException(
                "Port must be less or equal to 99999 !"
            );
        }
        final Random random = new Random();
        do {
            try (ServerSocket srv =
                new ServerSocket(random.nextInt(max - min) + min)) {
                return srv.getLocalPort();
            }
        } while (true);
    }
}
