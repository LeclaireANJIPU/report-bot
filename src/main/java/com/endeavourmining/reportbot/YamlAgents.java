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

import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Agents in YAML.
 *
 * @since 0.1
 */
public final class YamlAgents implements Agents {

    /**
     * YAML content.
     */
    private final YamlSequence content;

    /**
     * Ctor.
     * @param content YAML content
     */
    public YamlAgents(final YamlSequence content) {
        this.content = content;
    }

    @Override
    public int count() {
        return this.content.size();
    }

    @Override
    public Iterable<Agent> iterate() {
        final Collection<Agent> items = new LinkedList<>();
        for (final YamlNode yaml : this.content.values()) {
            items.add(new YamlAgent(yaml.asMapping()));
        }
        return items;
    }
}
