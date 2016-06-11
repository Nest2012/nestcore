package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sf.cglib.beans.BeanMap;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class JavaBeanMapper implements Mapper {

	Class objectType = null;

	Vector getMethods = null;

	Hashtable setMethods = null;

	Hashtable ignore = new Hashtable();

	BeanMap beanMap = null;

	List attributes = new ArrayList();

	private Object set_lock = new Object();

	public Object writeObject(Class clazz, ProtocolObject subclassType,
			Object value) throws RPCException {
		Object obj = null;

		if (value == null || value == ProtocolObject.NULL) {
			return null;
		}

		if (clazz != this.objectType || setMethods == null) {
			synchronized (set_lock) {
				if (clazz != this.objectType || setMethods == null) {
					this.objectType = clazz;
					this.setMethods = null;
					this.setMethods = new Hashtable();

					Method[] ms = clazz.getMethods();
					for (int i = 0; i < ms.length; i++) {
						Method method = ms[i];
						if (method.getDeclaringClass() == Object.class) {
							continue;
						}
						// 只处理get开头的方法，并且只有一个参数
						if (method.getName().startsWith("set")
								&& method.getParameterTypes().length == 1) {
							this.setMethods.put(method.getName(), method);
						}
					}
				}
			}
		}

		try {
			try {
				obj = clazz.newInstance();
			} catch (Exception e) {
				return null;
			}

			if (!(value instanceof ProtocolObject)) {
				return null;
			}

			ProtocolObject values = (ProtocolObject) value;
			for (java.util.Iterator i = subclassType.keys(); i.hasNext();) {
				String key = i.next().toString();
				String attributeClassType = subclassType.getProtocolObject(key)
						.keys().next().toString();
				Class attributeClass = MapperFactory
						.forName(attributeClassType);
				Mapper mapper = MapperFactory.getMapper(attributeClass);
				String valuekey = key.substring(0, 1).toLowerCase()
						+ key.substring(1);

				Object v = null;
				if (!values.has(valuekey))
					continue;
				try {
					v = mapper.writeObject(
							attributeClass,
							subclassType.getProtocolObject(key)
									.getProtocolObject(attributeClassType),
							(values.isNull(valuekey)) ? null : values
									.get(valuekey));
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				if (v != null) {
					String setMethodName = "set"
							+ key.substring(0, 1).toUpperCase()
							+ key.substring(1);

					Method m = (Method) setMethods.get(setMethodName);
					if (m == null) {
						// 如果找不到方法，则从对象中重新查询方法
						try {
							// 检查是否忽略此方法
							if (ignore.get(key) == null) {
								m = clazz.getMethod(setMethodName,
										new Class[] { attributeClass });
								setMethods.put(setMethodName, m);
							}
						} catch (Exception e) {
							// 如果找不到该方法，则将方法名加到忽略列表中
							ignore.put(setMethodName, "");
							// throw new RPCException("Method" + clazz.getName()
							// + ".set" + key + " not find! ");
							// e.printStackTrace();
						}
					}

					if (m != null) {
						m.invoke(obj, new Object[] { v });
					}
				}
			}
		} catch (Exception e) {
			throw new RPCException(e);
		}

		return obj;

	}

	public void readObjectStructure(Class clazz, Object object,
			ObjectReader reader) throws IOException {
		String name = null;
		try {
			if (clazz != this.objectType || getMethods == null) {
				initObjectMethods(clazz);
			}

			reader.writeStructure("'" + clazz.getName() + "':{");
			for (int i = 0; i < this.getMethods.size(); i++) {
				Method method = (Method) this.getMethods.get(i);

				name = method.getName();

				// 判断是is还是get方法
				reader.writeStructure("'"
						+ name.substring((name.charAt(0) == 'i') ? 2 : 3));

				reader.writeStructure("':");
				reader.writeStructure('{');
				Class returnClass = method.getReturnType();

				if (!returnClass.isArray()
						&& !Object.class.isAssignableFrom(returnClass)) {
					reader.writeStructure(returnClass.getName() + ":{}");
				} else {
					Object returnValue = method.invoke(object, new Object[] {});
					reader.readObjectStructure(returnClass, returnValue);
				}
				reader.writeStructure('}');

				if (i + 1 != this.getMethods.size()) {
					reader.writeStructure(',');
				}
			}
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		reader.writeStructure('}');

	}

	public void readObjectValue1(Object object, ObjectReader reader)
			throws IOException {
		if (object == null) {
			reader.writeValue("null");
			return;
		}

		// 初始化BeanMap对象
		Class clazz = object.getClass();
		try {
			initBeanMapByClass(clazz);
		} catch (InstantiationException e1) {
			throw new RuntimeException(e1);
		} catch (Exception e) {
			reader.writeValue("null");
			throw new RuntimeException(e);
		}

		// 没有属性，直接返回{}
		if (attributes.size() == 0) {
			reader.writeValue("{}");
			return;
		}

		reader.writeValue('{');

		// 拼装第一个属性
		Object attrname, v = null;
		boolean isFirst = true;

		// 拼装其他属性
		for (int i = 0; i < attributes.size(); i++) {
			attrname = attributes.get(i);
			v = beanMap.get(attrname);
			if (v != null) {
				if (isFirst) {
					isFirst = false;
				} else {
					reader.writeValue(',');
				}
				reader.writeValue("\"" + (String) attrname + "\"");
				reader.writeValue(':');
				reader.readObjectValue(v, v == null ? null : v.getClass());
			}
		}

		reader.writeValue('}');
	}

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {
		Object v = null;
		String name = null;
		reader.writeValue('{');

		try {
			Class clazz = object.getClass();
			if (clazz != this.objectType || getMethods == null) {
				initObjectMethods(clazz);
			}

			boolean isFirst = true;
			int p = 3;
			for (int i = 0; i < this.getMethods.size(); i++) {
				Method method = (Method) this.getMethods.get(i);

				name = method.getName();
				p = (name.charAt(0) == 'i') ? 2 : 3;

				try {
					v = method.invoke(object, new Object[] {});
				} catch (RuntimeException e) {
					v = null;
				}
				Class valueType = method.getReturnType();

				if (v != null) {
					if (isFirst) {
						isFirst = false;
					} else {
						reader.writeValue(',');
					}

					reader.writeValue("\""
							+ name.substring(p, p + 1).toLowerCase()
							+ name.substring(p + 1) + "\"");
					reader.writeValue(':');
					// 如果返回对象不为null，则使用实际返回对象的类型进行解析
					if (valueType == Object.class && v != null)
						valueType = v.getClass();
					reader.readObjectValue(v, valueType);
					// }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		reader.writeValue('}');
	}

	private synchronized BeanMap initBeanMapByClass(Class clazz)
			throws Exception {
		if (beanMap == null) {
			beanMap = BeanMap.create(clazz.newInstance());

			Object attr = null;
			for (Iterator it = beanMap.keySet().iterator(); it.hasNext();) {
				attr = it.next();
				if ("class".equals(attr))
					continue;
				attributes.add(attr);
			}
		}
		return beanMap;
	}

	/**
	 * 读取对象中名称为get开头，且没有参数的方法缓存起来
	 * 
	 * @param clazz
	 * @throws SecurityException
	 */
	private synchronized void initObjectMethods(Class clazz)
			throws SecurityException {
		// 如果 getMethods 对象已经不为空则说明它已经被其他线程初始化过了
		if (this.getMethods != null)
			return;

		Vector v = new Vector();

		Method[] ms = clazz.getMethods();
		String methodName = null;
		for (int i = 0; i < ms.length; i++) {
			Method method = ms[i];
			if (method.getDeclaringClass() == Object.class) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				// 如果getxxx方法需要传参数则跳过
				continue;
			}
			// 只处理get开头的方法
			methodName = method.getName();
			if ((methodName.startsWith("get") || methodName.startsWith("is"))
					&& !"getClass".equals(methodName)
					&& !"getClassLoader".equals(methodName)) {
				if (!clazz.equals(method.getReturnType())
						&& method.getReturnType() != java.lang.Class.class) {
					v.add(method);
				}
			}
		}
		this.getMethods = v;
		this.objectType = clazz;
	}
}
