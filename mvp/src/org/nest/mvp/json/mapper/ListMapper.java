
package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolArray;
import org.nest.mvp.json.protocol.ProtocolObject;

public class ListMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType, Object value) throws RPCException {

        try {
            List list = null;

            if (value == null) {
                return list;
            } else if (value instanceof ProtocolArray) {
                try {
                    list = (List) clazz.newInstance();
                } catch (Exception e) {
                    list = new java.util.ArrayList();
                }

                String subClassName = null;
                try {
                    subClassName = subclassType.keys().next().toString();
                } catch (Exception e) {
                    // 如果只定义了一个List对象而没有向里面加任何对象时此处会抛出错误
                }

                if (subClassName != null) {

                    ProtocolObject subsubClassType = subclassType.getProtocolObject(subClassName);
                    Class subClass = MapperFactory.forName(subClassName);
                    ProtocolArray array = (ProtocolArray) value;
                    Mapper mapper = MapperFactory.getMapper(subClass);

                    for (int i = 0; i < array.length(); i++) {
                        list.add(mapper.writeObject(subClass, subsubClassType, array.get(i)));
                    }
                }

                return list;
            } else {
                return list;
                // throw new RPCException("value Object isn't a instance of ProtocolArray, it is a instance of "
                // + value.getClass());
            }

        } catch (Exception e) {
            throw new RPCException(e);
        }
    }

    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException {
        List list = (List) object;

        if (object == null) {
            reader.writeStructure("'" + clazz.getName() + "':{");
        } else {
            reader.writeStructure("'" + object.getClass().getName() + "':{");
        }

        if (list.size() > 0) {
            reader.readObjectStructure(list.get(0));
        }

        reader.writeStructure('}');
    }

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {
		final long start = System.currentTimeMillis();
		List list = (List) object;
		int loop = list.size();
		if (loop == 0) {
			reader.mergeValue("[]");
			return;
		}

		reader.mergeValue('[');
		Iterator i = list.iterator();
		while (true) {
			reader.readObjectValue(i.next());
			if (i.hasNext()) {
				reader.mergeValue(',');
			} else {
				break;
			}
		}
		reader.mergeValue(']');
	}
}
