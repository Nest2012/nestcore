package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.math.BigDecimal;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class DoubleMapper implements Mapper {

    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException {
        reader.writeStructure("'java.math.BigDecimal':{}");
    }

    public void readObjectValue(Object object, ObjectReader reader) throws IOException {
        reader.writeValue("\"" + String.valueOf(object) + "\"");
    }

    public Object writeObject(Class clazz, ProtocolObject subclassType, Object value) throws RPCException {
        if (value == null) {
            return null;
        }
        BigDecimal bigDecimal = null;
        try {
            bigDecimal = new BigDecimal(value.toString());
        }
        catch (Exception e) {
        }
        return bigDecimal;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO 自动生成方法存根

    }

}
