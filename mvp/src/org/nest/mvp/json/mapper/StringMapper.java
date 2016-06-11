package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class StringMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {

        if (value == null) return null;
        if (value == ProtocolObject.NULL) return null;
        return value.toString();
    }

    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        reader.writeStructure("'java.lang.String':{}");

    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {

        // 避免影响现有功能，先采用<br>替换回车
        reader.writeValue('"');
        String temp = String.valueOf(object);
        if (temp == null) {
            reader.writeValue(temp);
        }
        else {
            // 采用循环方式替换正则表达式方式替换
            // reader.writeValue(temp.replaceAll("\\\"", "\\\\\"").replaceAll(
            // "\r\n", "<br>"));
            char c;
            for (int i = 0, l = temp.length(); i < l; i++) {
                c = temp.charAt(i);
                switch (c) {
                    case '\r': {
                        reader.writeValue("<br>");
                        if (i + 1 < l && temp.charAt(i + 1) == '\n') i++;
                        continue;
                    }
                    case '\n': {
                        reader.writeValue("<br>");
                        continue;
                    }
                    case '"': {
                        reader.writeValue("\\\"");
                        continue;
                    }
                    case '\\': {
                        reader.writeValue("\\\\");
                        continue;
                    }
                    default: {
                        reader.writeValue(c);
                    }
                }
            }
        }

        reader.writeValue('"');
    }
}
