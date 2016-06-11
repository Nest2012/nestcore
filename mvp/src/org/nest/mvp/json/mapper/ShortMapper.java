package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class ShortMapper implements Mapper {

    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException {
        reader.writeStructure("'java.lang.Short':{}");
    }

    public void readObjectValue(Object object, ObjectReader reader) throws IOException {
        reader.writeValue("\"" + String.valueOf(object) + "\"");
    }

    public Object writeObject(Class clazz, ProtocolObject subclassType, Object value) throws RPCException {
        if (value == null) {
            return null;
        }
        Short shot = null;
        try {
            shot = new Short(value.toString());
        }
        catch (Exception e) {
        }
        return shot;
    }

}
