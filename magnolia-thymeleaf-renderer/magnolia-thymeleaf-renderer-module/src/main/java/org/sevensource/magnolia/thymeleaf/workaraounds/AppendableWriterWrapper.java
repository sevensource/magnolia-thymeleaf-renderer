package org.sevensource.magnolia.thymeleaf.workaraounds;

/*-
 * #%L
 * Magnolia Thymeleaf Renderer
 * %%
 * Copyright (C) 2017 SevenSource, pgaschuetz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import info.magnolia.rendering.util.AppendableWriter;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * wraps magnolias AppendableWriter, because its badly misdesigned.
 * https://jira.magnolia-cms.com/browse/MAGNOLIA-6583
 * Created by thomas on 06.03.16.
 */
public class AppendableWriterWrapper extends Writer {

    private AppendableWriter appendableWriter;

    /**
     * wraps magnolias AppendableWriter, because its badly misdesigned.
     */
    public AppendableWriterWrapper(AppendableWriter appendableWriter) {
        this.appendableWriter = appendableWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        appendableWriter.write(cbuf, off, off + len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        appendableWriter.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        appendableWriter.close();
    }
}
