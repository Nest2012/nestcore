package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class NullMapper implements Mapper {

    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        if (clazz == null) {
            reader.writeStructure("'null':{}");
        }
        else {
            reader.writeStructure(clazz.getName() + ":{}");
        }

    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {
        reader.writeValue("null");
    }

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {
        if (value != null) {
            System.out.println("空接点对应数据" + value);
        }
        return null;
    }

}
