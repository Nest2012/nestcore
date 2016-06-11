package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class DateMapper implements Mapper {

    public Object writeObject(Class clazz, ProtocolObject subclassType,
            Object value) throws RPCException {

        long time = 0;
        if (value == null) {
            return null;
        }

        else if (value instanceof Number) {
            Number number = (Number) value;
            time = number.longValue();
        }
        else if (value instanceof String) {
            // 匹配日期型格式，日期型格式包括 yyyymmdd、new Date(xxxxxxxxx)
            String v = (String) value;
            if ("".equals(value)) {
                return null;
            }
            else if (v.startsWith("new Date(")) {
                // javascript 日期对象，分离出时间值
                time = (long) java.lang.Double.parseDouble(v.substring(9, v
                        .lastIndexOf(')')));
            }
            else if (v.length() == 8) {
                // 匹配yyyymmdd格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    return sdf.parse(v);
                }
                catch (ParseException e) {
                    return null;
                }
            }
            else if (v.length() == 10) {
                // 匹配yyyymmdd格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return sdf.parse(v);
                }
                catch (ParseException e) {
                    return null;
                }
            }
            else {
                try {
                    // 尝试转换成时间
                    time = (long) java.lang.Double.parseDouble(v);
                }
                catch (Exception e) {
                    return null;
                }
            }

        }
        else {
            // 尝试转换成时间
            time = (long) java.lang.Double.parseDouble(value.toString());
        }

        try {
            Date d = (Date) clazz.newInstance();
            d.setTime(time);
            return d;
        }
        catch (Exception e) {
            throw new RPCException(e);
        }
    }

    public void readObjectStructure(Class clazz, Object object,
            ObjectReader reader) throws IOException {
        reader.writeStructure('\'');
        reader.writeStructure(clazz.getName());
        reader.writeStructure("':{}");
    }

    public void readObjectValue(Object object, ObjectReader reader)
            throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#");
        Date d = (Date) object;
        long time = d.getTime();
        reader.writeValue(decimalFormat.format(time));
    }
}
