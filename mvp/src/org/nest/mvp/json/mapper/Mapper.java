package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public interface Mapper {
    
    public Object writeObject(Class clazz ,ProtocolObject subclassType , Object value) throws RPCException;

    public void readObjectValue(Object object, ObjectReader reader) throws IOException;
    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException;
}
