package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class CharacterMapper implements Mapper {
    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        reader.writeStructure("'java.lang.Character':{}");

    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {

        reader.writeValue((object == null) ? "null" : object.toString());

    }

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {
        return (value == null) ? null : new Character(value.toString()
                .charAt(0));
    }
}
