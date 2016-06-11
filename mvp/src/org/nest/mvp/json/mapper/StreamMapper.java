package org.nest.mvp.json.mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class StreamMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType, Object value) throws RPCException {
        throw new RPCException("尚未实现");
    }

    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException {
        reader.writeStructure("'"+clazz.getName() + "':{}");
    }

    public void readObjectValue(Object object, ObjectReader reader) throws IOException {
        InputStream ips = (InputStream) object;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length;
        while ((length = ips.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, length);
        }
        String s = new sun.misc.BASE64Encoder().encode(baos.toByteArray());

        reader.writeStructure(s);
    }
}
