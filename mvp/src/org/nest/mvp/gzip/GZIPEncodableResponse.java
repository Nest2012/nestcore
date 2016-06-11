package org.nest.mvp.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZIPEncodableResponse extends HttpServletResponseWrapper {
    private GZIPServletStream   gzipStream;
    private HttpServletResponse response = null;
    private ServletOutputStream out;
    private PrintWriter         writer;

    public GZIPEncodableResponse(HttpServletResponse response)
            throws IOException {
        super(response);
        this.response = response;
        response.addHeader("Content-Encoding", "gzip");
        gzipStream = new GZIPServletStream(this.response.getOutputStream());
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (null == out) {
            if (null != writer) { throw new IllegalStateException(
                    "getWriter() has already been "
                            + "called on this response."); }
            out = gzipStream;
        }
        return out;
    }

    public PrintWriter getWriter() throws IOException {
        if (null == writer) {
            if (null != out) { throw new IllegalStateException(
                    "getOutputStream() has "
                            + "already been called on this response."); }
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream()));
        }
        return writer;
    }

    public void flushBuffer() throws IOException {
        try {
            if (this.getWriter() != null) {
                this.getWriter().flush();
            }
            if (out != null) {
                out.flush();
            }
            if (this.gzipStream != null) {
                this.gzipStream.finish();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
