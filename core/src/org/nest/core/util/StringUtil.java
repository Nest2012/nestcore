package org.nest.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class StringUtil {
    /**
     * @Description:得到当前的日期,共8位 返回格式：yyyyMMdd
     * @return String
     */
    public static String getCurrentDate() {
        Date NowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(NowDate);
    }

    /**
     * 方法描述
     * @return
     * @return String
     */
    public static String createGUID() {
        return RandomGUID.geneGuid().toUpperCase();
    }

    /**
     * 检查字符串里是否只包含字母和数字.
     * @param str
     *            字符串
     * @return boolean true-只含有字母和数字;false-含有其它非字母和数字
     * @throws
     */
    public static boolean checkContainsLettersAndNums(final String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (c >= '0' && c <= '9') {
                continue;
            } else if (c >= 'a' && c <= 'z') {
                continue;
            } else if (c >= 'A' && c <= 'Z') {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 检查字符串里是否只包含字母,数字和下划线.
     * @param str
     *            字符串
     * @return boolean true-只含有字母，数字和下划线;false-含有其它非字母，数字，下划线
     * @throws
     */
    public static boolean checkContainsLettersAndNumsAndUnderline(
            final String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (c >= '0' && c <= '9') {
                continue;
            } else if (c >= 'a' && c <= 'z') {
                continue;
            } else if (c >= 'A' && c <= 'Z') {
                continue;
            } else if (c == '_') {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 检查字符是否为数字 .
     * @param 值
     * @return boolean true-是;false-不 是
     * @throws
     */
    public static boolean checkCharIsNum(char v) {
        if ("".equals(v)) {
            return false;
        }
        switch (v) {
        case '0':
            return true;
        case '1':
            return true;
        case '2':
            return true;
        case '3':
            return true;
        case '4':
            return true;
        case '5':
            return true;
        case '6':
            return true;
        case '7':
            return true;
        case '8':
            return true;
        case '9':
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * @param key
     *            需要校验的类型
     * @param set
     *            特殊字符的set集合。当set为null时 则值为,.!@#$%^&*()/\\;':\"?><{}[]|`~
     * @return 存在特殊字符返回true
     * @throws
     */
    public static boolean checkSpecialChar(String key, Set<Character> set) {
        // 判断KEY是否空值
        if (key != null) {
            char[] keys = key.toCharArray();
            if (set == null) {
                set = special1;
            }
            for (int i = 0; i < keys.length; i++) {
                if (set.contains(keys[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void init() {
        String SPECIAL_1 = ",.!@#$%^&*()/\\;':\"?><{}[]|`~";
        special1 = new HashSet<Character>();
        char[] specialc = SPECIAL_1.toCharArray();
        for (int j = 0; j < specialc.length; j++) {
            special1.add(specialc[j]);
        }
    }

    public static Set<Character> special1 = null;
    static {
        init();
    }

    public static void main(String[] args) {
        System.out.println(15242 - (59782 - 46088));
    }
}
