package de.eiswind.magnolia.thymeleaf.workaraounds;

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
