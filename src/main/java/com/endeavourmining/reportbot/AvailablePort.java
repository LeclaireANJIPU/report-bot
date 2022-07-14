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
    private static int generate(final int min, final int max) throws IOException {
        if (min <= 0 || max <= 0) {
            throw new IllegalArgumentException(
                "Port range must only contain positive ports !"
            );
        }
        if (min > max) {
            throw new IllegalArgumentException(
                "Port range: minimum port must be less than maximum port !"
            );
        }
        final Random random = new Random();
        do {
            try (ServerSocket srv =
                new ServerSocket(random.nextInt(max - min + 1) + max)) {
                return srv.getLocalPort();
            }
        } while (true);
    }
}
