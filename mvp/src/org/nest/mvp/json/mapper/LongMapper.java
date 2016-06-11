package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class LongMapper implements Mapper {

	public void readObjectStructure(Class clazz, Object object,
			ObjectReader reader) throws IOException {
		reader.writeStructure("'java.lang.Long':{}");

	}

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {

		reader.writeValue((object == null) ? "null" : ""
				+ ((Long) object).longValue());

	}

	public Object writeObject(Class clazz, ProtocolObject subclassType,
			Object value) throws RPCException {
        if(value != null){
            if(value instanceof Double){
                return new Long(((Double)value).longValue());
            }else{
                return new Long(value.toString());
            }
        }
        return null;
	}

}
