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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Test case for AvailablePort.
 *
 * @since 0.1
 * @checkstyle MagicNumberCheck (500 lines)
 */
final class AvailablePortTest {

    @RepeatedTest(100)
    void generatesAnAvailablePort() throws IOException {
        final int min = 1050;
        final int max = 1100;
        final int port = new AvailablePort(min, max).intValue();
        MatcherAssert.assertThat(
            port,
            Matchers.allOf(Matchers.greaterThanOrEqualTo(min), Matchers.lessThanOrEqualTo(max))
        );
    }

    @Test
    void generateMinLessThanTwentyTwo() {
        final Throwable exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new AvailablePort(21, 1050).intValue()
        );
        MatcherAssert.assertThat(
            "Port must be greater or equal to 22 !",
            new IsEqual<>(exception.getMessage())
        );
    }

    @Test
    void generateMaxLessThanMin() {
        final Throwable exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new AvailablePort(1150, 1050).intValue()
        );
        MatcherAssert.assertThat(
            "Port range: minimum port must be less than maximum port !",
            new IsEqual<>(exception.getMessage())
        );
    }

}
