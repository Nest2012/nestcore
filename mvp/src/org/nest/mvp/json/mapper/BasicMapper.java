package org.nest.mvp.json.mapper;

import java.io.IOException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

/**
 * 处理各种基础类型的数据转换
 * 
 * @author wengyuedong
 * 
 */
public class BasicMapper implements Mapper {

    public void readObjectValue(int i, ObjectReader reader) throws IOException {
        reader.writeValue(String.valueOf(i));
    }

    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        reader.writeStructure('\'');
        reader.writeStructure(clazz.getName());
        reader.writeStructure("':{}");
    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {
        reader.writeValue(String.valueOf(object));

    }

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {
        if (int.class == clazz) {
            if (value == null) {
                return new java.lang.Integer(0);
            }
            else if (int.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Integer(value.toString());
            else
                return new java.lang.Integer(0);

        }
        else if (short.class == clazz) {
            if (value == null) {
                return new java.lang.Short((short) 0);
            }
            else if (double.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Short(value.toString());
            else
                return new java.lang.Short((short) 0);

        }
        else if (long.class == clazz) {
            if (value == null) {
                return new java.lang.Long(0);
            }
            else if (long.class == value.getClass()) {
                return value;
            }
            else if (value instanceof Double) {
                return new java.lang.Long(((Double) value).longValue());
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Long(value.toString());
            else
                return new java.lang.Long((long) 0);

        }
        else if (double.class == clazz) {
            if (value == null) {
                return new java.lang.Double(0);
            }
            else if (double.class == value.getClass()) {
                return value;
            }
            else if (Double.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Double(value.toString());
            else
                return new Double(0);

        }
        else if (float.class == clazz) {
            if (value == null) {
                return new java.lang.Float(0);
            }
            else if (float.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Float(value.toString());
            else
                return new Float(0);

        }
        else if (byte.class == clazz) {
            if (value == null) {
                return new java.lang.Byte((byte) 0);
            }
            else if (byte.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Byte(value.toString());
            else
                return new Byte((byte) 0);

        }
        else if (char.class == clazz) {
            if (value == null) {
                return new java.lang.Character((char) 0);
            }
            else if (byte.class == value.getClass()) {
                return value;
            }
            else if (value.toString().length() > 0
                    && !value.toString().equalsIgnoreCase("null"))
                return new java.lang.Character(value.toString().charAt(0));
            else
                return new Character((char) 0);

        }
        else if (boolean.class == clazz) {
            if (value == null) {
                return Boolean.FALSE;
            }
            else if (boolean.class == value.getClass()) {
                return value;
            }
            else
                return new java.lang.Boolean(value.toString().equalsIgnoreCase(
                        "true"));

        }

        return null;
    }

}
