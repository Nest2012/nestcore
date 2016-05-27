
package org.nest.core.dbutil;

import org.nest.core.exception.NestRuntimeException;


public class DBDriverInfo {
    public final static String ORACLE_DIRVER = "oracle.jdbc.driver.OracleDriver";
    public final static String MYSQL_DIRVER = "com.mysql.jdbc.Driver";
    public final static String DM_DIRVER = "dm.jdbc.driver.DmDriver";

    private final static String ORACLE_LINK = "jdbc:oracle:thin:@$ip:$port:$sid";
    private final static String MYSQL_LINK = "jdbc:mysql://$ip:$port/$sid?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8";
    private final static String DM_LINK = "jdbc:dm://$ip:$port?ignoreCase=true&user=$username&password=$pwd";

    public static String getDirver(DBEnum e) {
        if (e == DBEnum.ORACLE) {
            return ORACLE_DIRVER;
        } else if (e == DBEnum.MYSQL) {
            return MYSQL_DIRVER;
        } else if (e == DBEnum.DM) {
            return DM_DIRVER;
        } else {
            throw new NestRuntimeException("目前仅支持ORACLE,MYSQL,DM数据库类型！");
        }
    }

    public static String getLink(DBEnum e) {
        if (e == DBEnum.ORACLE) {
            return ORACLE_LINK;
        } else if (e == DBEnum.MYSQL) {
            return MYSQL_LINK;
        } else if (e == DBEnum.DM) {
            return DM_LINK;
        } else {
            throw new NestRuntimeException("目前仅支持ORACLE,MYSQL,DM数据库类型！");
        }
    }

    public static String getLink(DBEnum e, String ip, String port, String sid, String username, String pwd) {
        if (e == DBEnum.ORACLE) {
            return getOracleLink(ip, port, sid);
        } else if (e == DBEnum.MYSQL) {
            return getMySqlLink(ip, port, sid);
        } else if (e == DBEnum.DM) {
            return getDMLink(ip, port, username, pwd);
        } else {
            throw new NestRuntimeException("目前仅支持ORACLE,MYSQL,DM数据库类型！");
        }
    }

    public static String getOracleLink(String ip, String port, String sid) {
        return ORACLE_LINK.replaceFirst("\\$ip", ip).replaceFirst("\\$port", port).replaceFirst("\\$sid", sid);
    }

    public static String getMySqlLink(String ip, String port, String sid) {
        return MYSQL_LINK.replaceFirst("\\$ip", ip).replaceFirst("\\$port", port).replaceFirst("\\$sid", sid);
    }

    public static String getDMLink(String ip, String port, String username, String pwd) {
        return DM_LINK.replaceFirst("\\$ip", ip).replaceFirst("\\$port", port).replaceFirst("\\$username", username)
                .replaceFirst("\\$pwd", pwd);
    }

}
