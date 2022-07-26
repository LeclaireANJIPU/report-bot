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
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Processor that extracts week from email subject.
 *
 * @since 0.1
 */
public final class ExtractWeekFromSubject implements MailProcessor {

    /**
     * HTML new line.
     */
    private static final String HTML_NEW_LINE = "<br>";

    /**
     * Number of days in a week.
     */
    private static final int DAYS_OF_WEEK = 7;

    /**
     * Date pattern.
     */
    private static final Pattern DATE_PATTERN =
        Pattern.compile("(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}");

    @Override
    public void process(final Email email) throws IOException {
        final Matcher matcher = ExtractWeekFromSubject.DATE_PATTERN.matcher(email.subject());
        final List<MatchResult> results = matcher.results().collect(Collectors.toList());
        if (results.size() == 2) {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final LocalDate start = LocalDate.parse(results.get(0).group(), formatter);
            final LocalDate end = LocalDate.parse(results.get(1).group(), formatter);
            if (
                Period.between(start, end).getDays() + 1 == ExtractWeekFromSubject.DAYS_OF_WEEK
                    && start.getDayOfWeek().getValue() == 1
                    && end.getDayOfWeek().getValue() == ExtractWeekFromSubject.DAYS_OF_WEEK
            ) {
                email.addCustomMetadata("start_week", start.format(formatter));
                email.addCustomMetadata("end_week", end.format(formatter));
            } else {
                throw new IllegalArgumentException(
                    String.join(
                        ExtractWeekFromSubject.HTML_NEW_LINE,
                        "Sorry, the week you enter is not valid. :-(",
                        // @checkstyle LineLengthCheck (1 line)
                        "Make sure the start and end date matches the start and end of the week."
                    )
                );
            }
        } else {
            throw new IllegalArgumentException(
                String.join(
                    ExtractWeekFromSubject.HTML_NEW_LINE,
                    // @checkstyle LineLengthCheck (1 line)
                    "Sorry, you forgot to include the week of your report or you put wrong dates in the subject of your email. :-(",
                    "A valid sample of subject could be: ",
                    "Boungou : Report of week 18/07/2022 to 24/07/2022"
                )
            );
        }
    }
}
