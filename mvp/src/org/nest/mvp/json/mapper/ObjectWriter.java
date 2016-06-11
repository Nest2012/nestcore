package org.nest.mvp.json.mapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolArray;
import org.nest.mvp.json.protocol.ProtocolObject;
import org.nest.mvp.json.protocol.ProtocolTokener;

public class ObjectWriter {

    ProtocolObject objectStructure = null;
    Object         objectValue     = null;

    public ObjectWriter() {

    }

    public void setObjectStructure(String structure) throws ParseException {
        this.objectStructure = new ProtocolObject(structure);
    }

    public void setObjectValue(String value) throws ParseException {
        ProtocolTokener t = new ProtocolTokener(value);
        this.objectValue = t.nextValue();
    }

    public Object getObject() throws Exception {

        String classType = String.valueOf(objectStructure.keys().next());
        Class clazz = MapperFactory.forName(classType);

        Mapper mapper = MapperFactory.getMapper(clazz);
        return mapper.writeObject(clazz, objectStructure
                .getProtocolObject(classType), objectValue);

    }

    public Object getObjectWithOutStructure(String json) throws Exception {
        if (json == null) return null;
        if (json.charAt(0) == '[') {
            return toListObject(new ProtocolArray(json));
        }
        else {
            return toMapObject(new ProtocolObject(json));
        }
    }

    private List toListObject(ProtocolArray jsonObj)
            throws NoSuchElementException, RPCException {
        List list = new ArrayList(jsonObj.length());
        for (int i = 0; i < jsonObj.length(); i++) {
            list.add(toBasicObject(jsonObj.get(i)));
        }
        return list;
    }

    private Map toMapObject(ProtocolObject jsonObj)
            throws NoSuchElementException, RPCException {
        Map m = new HashMap();

        for (java.util.Iterator i = jsonObj.keys(); i.hasNext();) {
            Object k = i.next();
            m.put(k, toBasicObject(jsonObj.get((String) k)));
        }
        return m;
    }

    private Object toBasicObject(Object o) throws RPCException {
        if (o.getClass() == ProtocolArray.class) {
            return toListObject((ProtocolArray) o);
        }
        else if (o.getClass() == ProtocolObject.class) {
            return toMapObject((ProtocolObject) o);
        }
        else {
            Mapper mapper = MapperFactory.getMapper(o.getClass());
            return mapper.writeObject(o.getClass(), null, o);
        }
    }

    public static void main(String args[]) throws Exception {
        String paramjson = "['lasjdlfka',12.12,{'sdfs':'sdfsdf'},['','','']]";

        ObjectWriter writer = new ObjectWriter();
        Object l = writer.getObjectWithOutStructure(paramjson);
        System.out.println(l);
    }
}
