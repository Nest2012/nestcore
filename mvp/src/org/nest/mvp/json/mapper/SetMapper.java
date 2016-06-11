package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.Set;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolArray;
import org.nest.mvp.json.protocol.ProtocolObject;

public class SetMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {
        try {
            Set set = null;

            if (value == null) {
                return set;
            }
            else if (value instanceof ProtocolArray) {
                try {
                    set = (Set) clazz.newInstance();
                }
                catch (Exception e) {
                    set = new java.util.HashSet();
                }

                String subClassName = null;
                try {
                    subClassName = subclassType.keys().next().toString();
                }
                catch (Exception e) {
                    // 如果只定义了一个List对象而没有向里面加任何对象时此处会抛出错误
                }

                if (subClassName != null) {

                    ProtocolObject subsubClassType = subclassType
                            .getProtocolObject(subClassName);
                    Class subClass = MapperFactory.forName(subClassName);
                    ProtocolArray array = (ProtocolArray) value;
                    Mapper mapper = MapperFactory.getMapper(subClass);

                    for (int i = 0; i < array.length(); i++) {
                        set.add(mapper.writeObject(subClass, subsubClassType,
                                array.get(i)));
                    }
                }

                return set;
            }
            else {
                return set;
                // throw new
                // RPCException("value Object isn't a instance of ProtocolArray,
                // it is a instance of "
                // + value.getClass());
            }

        }
        catch (Exception e) {
            throw new RPCException(e);
        }
    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {
        Set set = (Set) object;
        Object[] objs = set.toArray();
        int loop = set.size();

        reader.writeValue('[');
        for (int i = 0; i < loop; i++) {
            reader.readObjectValue(objs[i]);

            if ((loop - i) != 1) {
                reader.writeValue(',');
            }
        }
        reader.writeValue(']');
    }

    /**
     * 读取Set集合内容部结构。一般来说集合结构中存储的都是相同类型的对象，因此，以集合中第一个对象为准
     */
    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        Set set = (Set) object;

        if (object == null) {
            reader.writeStructure("'" + clazz.getName() + "':{");
        }
        else {
            reader.writeStructure("'" + object.getClass().getName() + "':{");
        }

        if (set.size() > 0) {
            reader.readObjectStructure(set.toArray()[0]);
        }

        reader.writeStructure('}');

    }

}
