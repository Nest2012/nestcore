
package org.nest.mvp.json.mapper;

import java.util.HashMap;
import java.util.Map;

import org.nest.core.bean.RecordSet;

public class MapperFactory {

    private static Map content = new HashMap();

    public static final Mapper MAPPER_NULLOBJECT = new NullMapper();

    // 默认加载的Mapper类
    static {
        content.put(java.util.List.class, new ListMapper());
        content.put(java.util.Map.class, new MapMapper());
        content.put(java.lang.String.class, new StringMapper());
        content.put(java.util.Arrays.class, new ArrayMapper());
        content.put(java.lang.Exception.class, new ExceptionMapper());
        content.put(java.io.InputStream.class, new StreamMapper());
        content.put(java.util.Date.class, new DateMapper());
        content.put(String.class, new StringMapper());
        content.put(java.math.BigDecimal.class, new BigDecimalMapper());
        content.put(java.lang.Short.class, new ShortMapper());
        content.put(java.lang.Long.class, new LongMapper());
        content.put(java.lang.Double.class, new DoubleMapper());
        content.put(java.lang.Integer.class, new IntegerMapper());
        content.put(java.lang.Byte.class, new ByteMapper());
        content.put(java.lang.Boolean.class, new BooleanMapper());
        content.put(java.lang.Character.class, new CharacterMapper());
        content.put(java.lang.Float.class, new FloatMapper());
        content.put(java.util.Set.class, new SetMapper());
        content.put(RecordSet.class, new RecordSetMapper());

        content.put(int.class, new BasicMapper());
        content.put(short.class, new BasicMapper());
        content.put(long.class, new BasicMapper());
        content.put(double.class, new BasicMapper());
        content.put(float.class, new BasicMapper());
        content.put(byte.class, new BasicMapper());
        content.put(char.class, new BasicMapper());
        content.put(boolean.class, new BasicMapper());
    }

    public synchronized static Mapper getMapper(Class clazz) {

        if (clazz == null) {
            return null;
        }

        // 按命名规则查找Mapper
        Object mapper = content.get(clazz);
        if (mapper != null) {
            return (Mapper) mapper;
        }

        // 特定类型判断
        if (clazz.isArray()) {
            content.put(clazz, content.get(java.util.Arrays.class));
            return (Mapper) content.get(java.util.Arrays.class);
        } else if (java.util.List.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.util.List.class));
            return (Mapper) content.get(java.util.List.class);
        } else if (java.util.Map.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.util.Map.class));
            return (Mapper) content.get(java.util.Map.class);
        } else if (java.util.Set.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.util.Set.class));
            return (Mapper) content.get(java.util.Set.class);
        } else if (java.lang.Exception.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.lang.Exception.class));
            return (Mapper) content.get(java.lang.Exception.class);
        } else if (java.io.InputStream.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.io.InputStream.class));
            return (Mapper) content.get(java.io.InputStream.class);
        } else if (java.lang.String.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.lang.String.class));
            return (Mapper) content.get(java.lang.String.class);
        } else if (java.util.Date.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(java.util.Date.class));
            return (Mapper) content.get(java.util.Date.class);
        } else if (Integer.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(Integer.class));
            return (Mapper) content.get(Integer.class);
        } else if (int.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(int.class));
            return (Mapper) content.get(int.class);
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(Boolean.class));
            return (Mapper) content.get(Boolean.class);
        } else if (boolean.class.isAssignableFrom(clazz)) {
            content.put(clazz, content.get(boolean.class));
            return (Mapper) content.get(boolean.class);
        }

        String name = clazz.getName();
        String shortname = null;
        int p = name.lastIndexOf('.');
        if (p > -1) {
            shortname = name.substring(p + 1);
        }

        // 没有匹配到特定类型，根据命名规则加载Mapper实现类
        try {
            mapper = Class.forName("org.nest.mvp.json.mapper." + shortname + "Mapper").newInstance();
            content.put(clazz, mapper);
            return (Mapper) mapper;
        } catch (Exception e) {
            // 没有实现类，则使用缺省Mapper
            content.put(clazz, new JavaBeanMapper());
            return (Mapper) content.get(clazz);
        }

    }

    public static Mapper getNullMapper() {
        return MapperFactory.MAPPER_NULLOBJECT;
    }

    public static Mapper getMapper(String className) throws ClassNotFoundException {
        return MapperFactory.getMapper(MapperFactory.forName(className));
    }

    public static Class forName(String className) throws ClassNotFoundException {
        if (className == null || "null".equalsIgnoreCase(className)) {
            return null;
        }

        if ("void".equals(className)) {
            return null;
        }

        // 验证基本类型
        if (int.class.getName().equals(className)) {
            return int.class;
        } else if (short.class.getName().equals(className)) {
            return short.class;
        } else if (long.class.getName().equals(className)) {
            return long.class;
        } else if (double.class.getName().equals(className)) {
            return double.class;
        } else if (float.class.getName().equals(className)) {
            return float.class;
        } else if (byte.class.getName().equals(className)) {
            return byte.class;
        } else if (char.class.getName().equals(className)) {
            return int.class;
        } else if (boolean.class.getName().equals(className)) {
            return boolean.class;
        } else {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
    }

    public static void regClassMapper(Class clazz, Mapper mapper) {
        content.put(clazz, mapper);
    }

}
