
package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.nest.core.bean.RecordSet;
import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

/**
 * 数据集对象
 * <ul>
 * <li>1.0 将字段名抽取成数组，数据采用二维数组形式封装。到客户端在还原成对象形式。这样避免了标准List+Map的JSON串中，对象属性名重复出现，占用大量字符</li>
 * <li>1.1 将数据值集编号，数据集的二维数组中只保存数据引用的编号。通过这种方式经重复出现的数据进行压缩。例如，数据中反复出现“一般预算支持”， 转换后数据集中只保存@1，客户端在通过@1在数据值集索引中替换为原始的内容</li>
 * <li>1.2 在客户端声明全局变量N代表null，由于客户端给对象添加属性时的速度比较慢，因此，将索引数据由数组对象改为Object对象， 这样省略了客户端由数组对象转Object对象的过程
 * ，对索引数据过万的结果集性能有很大提升。同时为了控制索引数据量，将索引生成规则，由重复2次以上的数据改为重复3次以上 。增加ml属性，记录每列数据的最大字符数</li>
 * <li>1.3 尝试将索引数据生成全局变量，这样省去客户端扫描全部数据将索引索引转换会数据的过程</li>
 * <li></li>
 * </ul>
 * @author wengyuedon
 */
public class RecordSetMapper implements Mapper {
    private static Logger logger = Logger.getLogger(RecordSetMapper.class);
    private static String index = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void readObjectStructure(Class clazz, Object object, ObjectReader reader) throws IOException {

    }

    /**
     * 使用数据集对象时客户端必须加载datatable3.0.js 否则会报解析错误<br>
     * 返回值结构如下： <br>
     * new Ext.lt.recordset({prejson:ture,ver:,seqdata:[],columns:[col1,col2……],
     * datas:[[val1,val2,……],[val1,val2,……],……]})
     */
    public void readObjectValue(Object object, ObjectReader reader) throws IOException {
        long start = System.currentTimeMillis();
        RecordSet rs = (RecordSet) object;
        int compress = rs.getCompress();
        // compress = 2;
        // rs.setPerjson(false);

        if (rs.size() == 0) {
            reader.writeValue("new Ext.lt.recordset({ver:\"" + rs.VERSION + "\","
                    + (rs.isPerjson() ? "prejson:true," : "") + "columns:[],datas:[],ml:[]})");
            return;
        }

        String[] cols = rs.getColNames();

        Map seqdataMap = compressRecordSet(rs);
        logger.debug("compressRecordSet:" + (System.currentTimeMillis() - start));

        reader.writeValue("new function(){var N=null");

        // 1.3 将索引数据生成全局变量
        if (seqdataMap.size() > 0) {
            Mapper mapper = null;
            Object key = null;
            for (Iterator i = seqdataMap.entrySet().iterator(); i.hasNext();) {
                reader.writeValue(',');
                Entry e = (Entry) i.next();
                reader.writeValue((String) e.getValue());
                reader.writeValue('=');
                key = e.getKey();
                mapper = MapperFactory.getMapper(key.getClass());
                mapper.readObjectValue(key, reader);
            }
        }

        // 生成列名索引
        if (rs.isPerjson() || compress == rs.COMPRESS_MAP) {
            for (int i = 0; i < cols.length; i++) {
                reader.writeValue(",C" + i + "='" + cols[i] + "'");
            }
            reader.writeValue(",P='_locationposition:',T='{'");
        }
        reader.writeValue(";");

        // 生成列名部分
        reader.writeValue("return new Ext.lt.recordset({" + (rs.isPerjson() ? "prejson:true," : "") + "ver:\""
                + rs.VERSION + "\",compress:" + compress + ",columns:");
        reader.readObjectValue(cols);
        logger.debug("columns:" + (System.currentTimeMillis() - start));

        if (rs.isPerjson() || compress == rs.COMPRESS_MAP) {
            // 需要在客户端预先生成JSON串或设置为采用MAP方式压缩的处理分支
            reader.writeValue(",datas:[[]");
            java.util.Iterator i = rs.iterator();
            Map m = null;
            int p = 0;
            while (i.hasNext()) {
                reader.writeValue(',');
                m = (Map) i.next();
                getValueMap(cols, m, seqdataMap, reader, compress, p++);

            }
            reader.writeValue(']');
        } else {
            // 生成具体数据
            reader.writeValue(",datas:[");
            Map m = null;
            Mapper mapper = null;

            if (rs.size() > 0) {
                java.util.Iterator i = rs.iterator();

                // 为了不用增加一次判断是否需要用逗号分隔，而将第一条记录的解析过程单独写一次
                m = (Map) i.next();
                if (rs.isPerjson()) {
                    ObjectReader r = new ObjectReader(m);
                    // 便于客户端将对象还原成json格式字符串
                    m.put("_jsonstring", r.getObjectValue());
                }
                // 转换为值对象并生成数据索引
                getValueArray(cols, m, seqdataMap, reader, compress);

                while (i.hasNext()) {
                    reader.writeValue(',');
                    m = (Map) i.next();
                    if (rs.isPerjson()) {
                        ObjectReader r = new ObjectReader(m);
                        // 便于客户端将对象还原成json格式字符串
                        m.put("_jsonstring", r.getObjectValue());
                    }
                    getValueArray(cols, m, seqdataMap, reader, compress);
                }
            }
            reader.writeValue(']');
        }
        logger.debug("datas:" + (System.currentTimeMillis() - start));

        // 生成数据索引
        int[] colm = rs.getColmaxsize();
        reader.writeValue(",ml:");
        if (colm != null) {
            reader.readObjectValue(colm);
        } else {
            reader.writeValue("[]");
        }
        reader.writeValue("})}");
        seqdataMap.clear();
        logger.debug("seqdata:" + (System.currentTimeMillis() - start));

        logger.info("转换耗时：" + (System.currentTimeMillis() - start) + "ms  length:" + reader.size());
    }

    /**
     * 统计recordset中重复数据出现的次数和频度，将出现两次以上的相同记录，按出现频度排序返回
     * @param rs
     * @return
     */
    private Map compressRecordSet(RecordSet rs) {
        Map m = new HashMap(rs.size() >> 2), o, tmpMap = new HashMap(rs.size() >> 2);
        String[] cols = rs.getColNames();
        int colsize = cols.length, loop = 0;
        Object value;
        String str;
        Integer integer = null;
        int colm[] = new int[cols.length];
        for (java.util.Iterator i = rs.iterator(); i.hasNext();) {
            o = (Map) i.next();
            for (loop = 0; loop < colsize; loop++) {
                value = o.get(cols[loop]);
                if (value != null) {
                    str = value.toString();
                    int strlength = str.getBytes().length;
                    // 获取最大长度
                    if (strlength > colm[loop]) {
                        colm[loop] = strlength;
                    }
                    if (strlength > 2) {
                        integer = (Integer) m.get(str);
                        if (integer == null) {
                            integer = new Integer(0);
                            tmpMap.put(str, value);
                        }
                        // 计算文本出现频度
                        m.put(str, new Integer(integer.intValue() + 1));
                    }
                }
            }
        }
        // 设置最大长度
        rs.setColmaxsize(colm);
        Map returnMap = new ListOrderedMap();

        Map.Entry en;
        // 删除出现频度两次一下的数据
        for (Iterator e = m.entrySet().iterator(); e.hasNext();) {
            en = (Map.Entry) e.next();

            if (((Integer) en.getValue()).intValue() > 2) {
                returnMap.put(tmpMap.get(en.getKey()), getIndexChar(returnMap.size()));
            }
        }
        return returnMap;
    }

    /**
     * 将Map中的数值以指定的顺序转换成数组返回
     * @param cols
     * @param m
     * @param reader
     * @return
     * @throws IOException
     */
    private void getValueArray(String[] cols, Map m, Map seqdata, ObjectReader reader, int compress) throws IOException {
        int l = cols.length;
        Object to = null, seq = null;

        if (compress == RecordSet.COMPRESS_ARRAY) {
            // 完整压缩，将数据保存成二位数组
            reader.writeValue('[');
            for (int j = 0; j < l; j++) {
                to = m.get(cols[j]);

                seq = seqdata.get(to);
                if (seq != null) {
                    // 使用索引保存
                    reader.writeValue((String) seq);
                } else if (to != null) {
                    Mapper mapper = MapperFactory.getMapper(to.getClass());
                    mapper.readObjectValue(to, reader);
                }

                if (j < l - 1) {
                    reader.writeValue(',');
                }
            }
            reader.writeValue(']');
        } else if (compress == RecordSet.COMPRESS_MAP) {
            // 数据保存为对象接口，节约客户端将数组还原为对象的时间
            reader.writeValue('{');
            String cname = null;
            for (int j = 0; j < l; j++) {
                cname = cols[j];
                reader.writeValue(cname);
                reader.writeValue(":");

                to = m.get(cname);
                seq = seqdata.get(to);
                if (seq != null) {
                    // 使用索引保存
                    reader.writeValue((String) seq);
                } else if (to != null) {
                    Mapper mapper = MapperFactory.getMapper(to.getClass());
                    mapper.readObjectValue(to, reader);
                } else {
                    reader.writeValue("N");
                }

                if (j < l - 1) {
                    reader.writeValue(',');
                }
            }
            reader.writeValue('}');
        }
    }

    /**
     * 将Map中的数值转换成对象数组
     * @param cols
     * @param m
     * @param reader
     * @return
     * @throws IOException
     */
    private void getValueMap(String[] cols, Map m, Map seqdata, ObjectReader reader, int compress, int p)
            throws IOException {
        int l = cols.length;
        Object to = null, seq = null;

        // 完整压缩，将数据保存成二位数组
        reader.writeValue("[T,P,");
        reader.writeValue(p);
        reader.writeValue(",'");
        for (int j = 0; j < l; j++) {
            reader.writeValue(",',");
            reader.writeValue("C" + j + ",':");
            to = m.get(cols[j]);

            if (to == null) {
                reader.writeValue("null");
                continue;
            }

            seq = seqdata.get(to);
            if (String.class == to.getClass()) {
                if (seq == null) {
                    readObjectValue2String(to, reader);
                } else {
                    reader.writeValue("\"'," + seq + ",'\"");
                }
            } else {
                if (seq == null) {
                    Mapper mapper = MapperFactory.getMapper(to.getClass());
                    if (mapper.getClass() == this.getClass()) {
                        throw new RuntimeException("RecordSet结果级中不能嵌套RecordSet结果集");
                    }
                    mapper.readObjectValue(to, reader);
                } else {
                    reader.writeValue("'," + seq + ",'");
                }
            }
        }
        reader.writeValue("}']");
    }

    public void readObjectValue2String(Object object, ObjectReader reader) throws IOException {
        reader.writeValue("\"");
        // 避免影响现有功能，先采用<br>替换回车
        String temp = String.valueOf(object);
        if (temp == null) {
            reader.writeValue(temp);
        } else {
            // 采用循环方式替换正则表达式方式替换
            // reader.writeValue(temp.replaceAll("\\\"", "\\\\\"").replaceAll(
            // "\r\n", "<br>"));
            char c;
            for (int i = 0, l = temp.length(); i < l; i++) {
                c = temp.charAt(i);
                switch (c) {
                case '\r': {
                    reader.writeValue("<br>");
                    if (i + 1 < l && temp.charAt(i + 1) == '\n')
                        i++;
                    continue;
                }
                case '\n': {
                    reader.writeValue("<br>");
                    continue;
                }
                case '"': {
                    reader.writeValue("\\\\\\\\\\\\\\\"");
                    continue;
                }
                case '\\': {
                    reader.writeValue("\\\\");
                    continue;
                }
                default: {
                    reader.writeValue(c);
                }
                }
            }
        }
        reader.writeValue("\"");
    }

    public Object writeObject(Class clazz, ProtocolObject subclassType, Object value) throws RPCException {
        if (true)
            throw new RPCException("RecordSet 对象不支持Json转对象");
        return null;
    }

    /**
     * 将数字转为为 62进制
     * @param i
     * @return
     */
    public String getIndexChar(int i) {

        String c = "";
        int n = 0, p = 7;
        while (i >= 62) {
            c = index.charAt(i % 62) + c;
            i = (i - i % 62) / 62;
        }
        c = index.charAt(i % 62) + c;

        return "$" + c;

        // return "@"+i;
    }

    public static void main(String arg[]) throws IOException {
        long start = System.currentTimeMillis();
        List l = new ArrayList(30000);
        Map obj = new HashMap();
        obj.put("aaa", "cd\\d");
        for (int i = 0; i < 10; i++) {
            Map m = new HashMap(40);
            for (int j = 10000; j < 10003; j++) {
                if (j == 10000) {
                    m.put("" + j, null);
                } else if (j == 10001) {
                    m.put("" + j, new Integer(i));
                } else {
                    m.put("" + j, "\\" + j);
                }
            }
            l.add(m);
        }
        RecordSet rs = new RecordSet(l);
        rs.setPerjson(false);
        rs.setCompress(1);

        // long start = System.currentTimeMillis();
        ObjectReader reader = new ObjectReader(rs);
        String json = reader.getObjectValue();
        //NTFSHelper.writeToFile("c:\\temp\\test.txt", json);
        logger.debug("total: " + (System.currentTimeMillis() - start));
        logger.debug(json.length() + "  " + (json.length() > 1000 ? json.substring(0, 1000) : json));

        //NTFSHelper.writeToFile("c:/test.txt", json);

        // int num // 3;
        // System.out.println(getIndexChar(10));

    }
}
