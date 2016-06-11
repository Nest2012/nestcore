package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class IntegerMapper implements Mapper {

	public void readObjectStructure(Class clazz, Object object,
			ObjectReader reader) throws IOException {
		reader.writeStructure("'java.lang.Integer':{}");

	}

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {

		reader.writeValue((object == null) ? "null" : ""
				+ ((Integer) object).intValue());

	}

	public Object writeObject(Class clazz, ProtocolObject subclassType,
			Object value) throws RPCException {
		return (value == null) ? null : new Integer(value.toString());
	}

}
