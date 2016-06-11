
package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.List;

/**
 * 读取对象结构和数据，按照协议要求组装 对象的值抽象为数组和Map两种数据结构 对象结构则使用Map结构保存 如果参数的结构比较复杂则采用下面的结构说明。如list+map的数据结构
 * [{ArrayList:{HashMap:{String:{},String:{},String:{},String:{}}}}] list+javaBean的数据结构
 * [{ArrayList:{TestBean:{String:{},int,double,Date}}}] list+javaBean+list+javaBean 的数据结构
 * [{ArrayList:{TestBean:{String:{},int:{},double:{},Date:{},ArrayList:{javaBean:{String:{},int:{}}}}}}]
 * @author wengyuedong
 */
public class ObjectReader {

    // 保存对象值
    StringBuffer objectvalue = null;
    private boolean doobjectvalue = false;

    // 保存对象结构
    StringBuffer objectstructure = null;
    private boolean doobjectstructure = false;

    // 缓存需要序列化的对象
    Object _object = null;

    public ObjectReader(Object object) throws IOException {
        if (object != null && java.util.List.class.isAssignableFrom(object.getClass())) {
            objectvalue = new StringBuffer(1024 * 2 * ((List) object).size());
            objectstructure = new StringBuffer(1024);
        } else {
            objectvalue = new StringBuffer(1024);
            objectstructure = new StringBuffer(1024);
        }

        readObject(object);
    }

    private void readObject(Object object) throws IOException {
        _object = object;
    }

    /**
     * 读取对象的值
     * @param object
     * @throws IOException
     */
    protected void readObjectValue(Object object) throws IOException {
        if (doobjectvalue)
            return;
        Mapper mapper = null;
        if (object == null) {
            mapper = MapperFactory.getNullMapper();
        } else {
            mapper = MapperFactory.getMapper(object.getClass());
        }

        mapper.readObjectValue(object, this);
        // doobjectvalue = true;
    }

    /**
     * 根据指定的类型读取对象值
     * @param object
     * @param clazz
     * @throws IOException
     */
    protected void readObjectValue(Object object, Class clazz) throws IOException {
        Mapper mapper = null;
        if (object == null || clazz == null) {
            mapper = MapperFactory.getNullMapper();
        } else {
            mapper = MapperFactory.getMapper(clazz);
        }

        mapper.readObjectValue(object, this);
    }

    protected void readObjectStructure(Object object) throws IOException {
        if (doobjectstructure)
            return;
        Mapper mapper = null;
        if (object == null) {
            mapper = MapperFactory.getNullMapper();
            mapper.readObjectStructure(null, object, this);
        } else {
            mapper = MapperFactory.getMapper(object.getClass());
            mapper.readObjectStructure(object.getClass(), object, this);
        }
        // doobjectstructure = true;
    }

    protected void readObjectStructure(Class clazz, Object object) throws IOException {
        Mapper mapper = null;
        if (object == null) {
            mapper = MapperFactory.getNullMapper();
        } else {
            mapper = MapperFactory.getMapper(object.getClass());
        }

        mapper.readObjectStructure(clazz, object, this);
    }

    public String getObjectValue() throws IOException {
        readObjectValue(_object);

        // return objectvalue.toString().replaceAll("\r",
        // "\\\r").replaceAll("\n",
        // "\\\n").replaceAll("\"", "\\\"");
        // 由于使用String的toString方法速度和内存消耗过大，因此，重载StringBuffer的append方法
        /*
         * int l = objectvalue.length(); int m = l; StringBuffer sb = new StringBuffer(l + (m >> 2)); char c; for (int i
         * = 0; i < l; i++) { c = objectvalue.charAt(i); switch (c) { case '\r': { sb.append('\\').append('r');
         * continue; } case '\n': { sb.append('\\').append('n'); continue; } case '"': { sb.append('"'); continue; }
         * default: { sb.append(c); } } } return sb.toString();
         */
        return objectvalue.toString();
    }

    public String getObjectStructure() throws IOException {
        readObjectStructure(_object);
        StringBuffer r = new StringBuffer(objectstructure.length() + 2);
        r.append('{').append(objectstructure).append('}');
        return r.toString();
    }

    void mergeValue(String s) {
        objectvalue.append(s);
    }

    void mergeValue(char s) {
        objectvalue.append(s);
    }

    public void writeValue(String s) {
        int l = s.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = s.charAt(i);
            switch (c) {
            case '\r': {
                objectvalue.append('\\').append('r');
                continue;
            }
            case '\n': {
                objectvalue.append('\\').append('n');
                continue;
            }
            case '"': {
                objectvalue.append('"');
                continue;
            }
            default: {
                objectvalue.append(c);
            }
            }
        }
    }

    public void writeStructure(String s) {
        objectstructure.append(s);
    }

    public void writeValue(char c) {
        switch (c) {
        case '\r': {
            objectvalue.append('\\').append('r');
            break;
        }
        case '\n': {
            objectvalue.append('\\').append('n');
            break;
        }
        case '"': {
            objectvalue.append('"');
            break;
        }
        default: {
            objectvalue.append(c);
        }
        }
    }

    public void writeStructure(char c) {
        objectstructure.append(c);
    }

    public void backValue(int i) {
        objectvalue.setLength(objectvalue.length() - i);
    }

    public void writeValue(int i) {
        objectvalue.append(i);
    }

    public void writeValue(long l) {
        objectvalue.append(l);
    }

    public void writeValue(double d) {
        objectvalue.append(d);
    }

    public long size() {
        return objectvalue.length();
    }

    public boolean isDoobjectvalue() {
        return doobjectvalue;
    }

    public void setDoobjectvalue(boolean doobjectvalue) {
        this.doobjectvalue = doobjectvalue;
    }

    public boolean isDoobjectstructure() {
        return doobjectstructure;
    }

    public void setDoobjectstructure(boolean doobjectstructure) {
        this.doobjectstructure = doobjectstructure;
    }

}
