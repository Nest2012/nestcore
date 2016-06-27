
package org.nest.core.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nest.core.util.RandomGUID;

/**
 * 与页面数据集Ext.lt.recordset功能对应<br>
 * 数据集中保存的数据结构与数据库查询结果相同。List内部保存Map对象，Map对象的Key值对应数据库的字段
 * @author wengyuedon
 */
public class RecordSet extends LinkedList {
    public static int COMPRESS_AUTO = 0;
    public static int COMPRESS_ARRAY = 2;
    public static int COMPRESS_MAP = 1;

    // 数据集ID，客户端可以通过该ID异步加载数据集中的数据
    private String rsid;
    private String[] colnames = null;
    private int[] colmaxsize = null;
    private LinkedList seqdata = new LinkedList();
    // 设置是否做导出JSON的服务端优化
    private boolean perjson = false;
    // 默认列名是否全部遍历
    private boolean cycleCol = false;
    // 强制列名转成小写
    private boolean lowerName = false;
    // 版本号
    public final static String VERSION = "1.4";
    // 控制数据压缩强度，默认为自动处理
    private int compress = 0;

    /**
     * 当数据超过8K行以后降低压缩比，换取浏览器的性能
     * @return
     * @throws
     */
    public int getCompress() {
        if (compress == 0 && this.size() > 20000) {
            return COMPRESS_MAP;
        } else if (compress == 0) {
            return COMPRESS_ARRAY;
        }

        return compress;
    }

    public void setCompress(int compress) {
        if (compress != COMPRESS_AUTO && compress != COMPRESS_MAP && compress != COMPRESS_ARRAY)
            return;
        this.compress = compress;
    }

    public boolean isPerjson() {
        return perjson;
    }

    public void setPerjson(boolean perjson) {
        this.perjson = perjson;
    }

    public RecordSet() {
        rsid = RandomGUID.geneGuid();
    }

    public RecordSet(List data) {
        this();
        this.addAll(data);
    }

    public RecordSet(List data, boolean perjson) {
        this();
        this.setPerjson(perjson);
        this.addAll(data);
    }

    /**
     * 设置数据集列明
     * @param names
     */
    public void setColNames(String[] names) {
        this.colnames = new String[perjson ? names.length + 1 : names.length];
        for (int i = 0; i < names.length; i++) {
            colnames[i] = names[i];
        }
        if (perjson)
            colnames[names.length] = "_jsonstring";
    }

    public void setColNames(Collection names) {
        Object[] cols = names.toArray();
        colnames = new String[perjson ? names.size() + 1 : names.size()];
        for (int i = 0; i < cols.length; i++) {
            if (cols[i] != null) {
                colnames[i] = lowerName ? ((String) cols[i]).toLowerCase() : (String) cols[i];
            }
        }
        if (perjson)
            colnames[names.size()] = "_jsonstring";
    }

    public int[] getColmaxsize() {
        return colmaxsize;
    }

    public void setColmaxsize(int[] colmaxsize) {
        this.colmaxsize = colmaxsize;
    }

    /**
     * 获取数据集列明
     * @return
     */
    public String[] getColNames() {
        if (colnames != null)
            return colnames;
        if (this.size() == 0)
            return null;
        if (cycleCol) {
            Set s = new HashSet();
            for (int i = 0, l = this.size(); i < l; i++) {
                Map m = (Map) get(i);
                s.addAll(m.keySet());
            }
            this.setColNames(s);
        } else {
            Map m = (Map) get(0);
            this.setColNames(m.keySet());
        }
        return colnames;
    }

    /**
     * 返回指定列中的值集，包括空值
     * @param gcn
     * @return
     */
    public Set getColumnValueSet(String gcn) {
        Map m = new HashMap();
        java.util.Iterator i = this.iterator();
        while (i.hasNext()) {
            m.put(((Map) i.next()).get(gcn), "");
        }
        return m.keySet();
    }

    public void setCycleCol(boolean cycleCol) {
        this.cycleCol = cycleCol;
    }

    public void setLowerColumnName(boolean lowerName) {
        this.lowerName = lowerName;
    }
}
