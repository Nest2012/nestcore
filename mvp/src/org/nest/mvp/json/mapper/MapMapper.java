package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolArray;
import org.nest.mvp.json.protocol.ProtocolObject;


public class MapMapper implements Mapper {

	public Object writeObject(Class clazz, ProtocolObject subclassType,
			Object value) throws RPCException {

		if (value == null || value == ProtocolObject.NULL) {
			return null;
		}

		try {
			// Map map = (Map) clazz.newInstance();
			// 这里只要是返回Map对象都使用CaseInsensitiveMap这种不区分大小写的好东东
			// 但工程运行需要依赖commons-collections 3.0 以上的版本
			Map map = new CaseInsensitiveMap();

			if (value instanceof ProtocolObject) {
				ProtocolObject obj = (ProtocolObject) value;
				for (java.util.Iterator i = obj.keys(); i.hasNext();) {
					String key = String.valueOf(i.next());
					Object v = obj.opt(key);

					if (subclassType.opt(key) == null) {
						if (v != null && v != ProtocolObject.NULL) {
							if (v instanceof ProtocolArray) {
								Mapper mapper = new ArrayMapper();
								map.put(key, mapper.writeObject(clazz,
										subclassType, v));
							} else {
								map.put(key, v.toString());
							}
						}

					} else {
						// 处理结构中有记录的字段
						String subClassName = subclassType
								.getProtocolObject(key).keys().next()
								.toString();
						ProtocolObject subsubClassType = subclassType
								.getProtocolObject(key).getProtocolObject(
										subClassName);

						Class subClass = MapperFactory.forName(subClassName);
						Mapper mapper = MapperFactory.getMapper(subClass);

						if (mapper == null) {
							mapper = MapperFactory.getMapper(String.class);
							map.put(key, mapper.writeObject(String.class,
									subsubClassType, v));
						} else {
							if (obj.opt(key) != null) {
								map.put(key, mapper.writeObject(subClass,
										subsubClassType, obj.get(key)));
							}
						}
					}

				}

				return map;
			} else {
				throw new RPCException(
						"value Object isn't a instance of ProtocolObject, it is a instance of "
								+ value.getClass());
			}

		} catch (Exception e) {
			throw new RPCException(e);
		}
	}

	/**
	 * map 型对象结构表述扩展为
	 * 
	 * {MapClassType:{key1:{<valueClassType>:{}},key2:{<valueClassType>:{}}}}
	 * 
	 */
	public void readObjectStructure(Class clazz, Object object,
			ObjectReader reader) throws IOException {

		reader.writeStructure("\"" + clazz.getName() + "\":{");
		Map map = (Map) object;

		Object key = null;

		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {

			key = it.next();
			reader.writeStructure("\"" + key.toString() + "\":{");
			reader.readObjectStructure(map.get(key));

			reader.writeStructure('}');
			if (it.hasNext()) {
				reader.writeStructure(",");
			}
		}
		reader.writeStructure('}');

	}

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {
		Map map = (Map) object;

		Object key = null;
		Iterator it = map.keySet().iterator();
		reader.writeValue('{');
		boolean hasNext = it.hasNext();
		while (hasNext) {

			key = it.next();
			reader.writeValue('\"');
			reader.writeValue(key.toString());
			reader.writeValue('\"');
			reader.writeValue(':');
			reader.readObjectValue(map.get(key));

			if (hasNext = it.hasNext()) {
				reader.writeValue(',');
			}
		}
		reader.writeValue('}');
	}
}
