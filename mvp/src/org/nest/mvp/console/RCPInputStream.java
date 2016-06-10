
package org.nest.mvp.console;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RCPInputStream extends FileInputStream {

    private File f = null;

    public RCPInputStream(File file) throws FileNotFoundException {
        super(file);
        f = file;
        // TODO Auto-generated constructor stub
    }

    public RCPInputStream(FileDescriptor fdObj) {
        super(fdObj);
        // TODO Auto-generated constructor stub
    }

    public RCPInputStream(String name) throws FileNotFoundException {
        super(name);
        f = new File(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void close() throws IOException {
        super.close();
        f.delete();
    }
}
