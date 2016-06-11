package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolArray;
import org.nest.mvp.json.protocol.ProtocolObject;

public class ArrayMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {
        // 2011-07-21 start
        if (value == null || value == ProtocolObject.NULL) { return null; }

        List list = new ArrayList();
        if (value instanceof ProtocolArray) {
            ProtocolArray obj = (ProtocolArray) value;
            for (int i = 0; i < obj.length(); i++) {
                Object o = obj.get(i);
                if (null == o) {
                    list.add(null);
                }
                else if (o instanceof Number) {
                    list.add(o);
                }
                else if (o instanceof String) {
                    list.add(obj.get(i));
                }
            }
        }
        return list;
        // 2011-07-21 end
    }

    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        reader.writeStructure("\"" + clazz.getName() + "\":{");

        if (Object.class.isAssignableFrom(clazz)) {
            // 对象数组
            Object[] objectList = (Object[]) (Object[]) (object);
            if (objectList.length == 0) {
                reader.readObjectStructure("null");
            }
            else
                reader.readObjectStructure(objectList[0]);
        }
        else {
            reader.writeStructure('\'');
            reader.writeStructure(clazz.getName());
            reader.writeStructure("':{}");
        }

        reader.writeStructure('}');
    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {
        Class clazz = object.getClass().getComponentType();
        if (Object.class.isAssignableFrom(clazz)) {
            Object[] objectList = (Object[]) (Object[]) (object);

            reader.writeValue('[');
            for (int i = 0; i < objectList.length; i++) {
                reader.readObjectValue(objectList[i]);
                if (objectList.length - i != 1) {
                    reader.writeValue(',');
                }
            }
            reader.writeValue(']');
        }
        else if (int.class == clazz) {
            readObjectValue((int[]) object, reader);
        }
        else if (short.class == clazz) {
            readObjectValue((short[]) object, reader);
        }
        else if (long.class == clazz) {
            readObjectValue((long[]) object, reader);
        }
        else if (double.class == clazz) {
            readObjectValue((double[]) object, reader);
        }
        else if (float.class == clazz) {
            readObjectValue((float[]) object, reader);
        }
        else if (byte.class == clazz) {
            readObjectValue((byte[]) object, reader);
        }
        else if (char.class == clazz) {
            readObjectValue((char[]) object, reader);
        }

    }

    private void readObjectValue(char[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(byte[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(float[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(double[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(long[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(short[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    private void readObjectValue(int[] ilist, ObjectReader reader) {
        reader.writeValue('[');
        for (int i = 0; i < ilist.length; i++) {
            reader.writeValue(ilist[i]);
            if (i != ilist.length - 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }
}
