package com.tjh.swivel.model;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

class WrapperServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream isDelegate;

    public WrapperServletInputStream(String content) throws UnsupportedEncodingException {
        this.isDelegate = new ByteArrayInputStream(content.getBytes("UTF-8"));
    }

    @Override
    public int read() throws IOException { return this.isDelegate.read(); }

    //YELLOWTAG:TJH - this is here for completeness, but doesn't work very well. In particular, no
    //attention is paid to whether or not a 'line' is actually read.
    @Override
    public int readLine(byte[] b, int off, int len) throws IOException { return isDelegate.read(b, off, len); }
}
