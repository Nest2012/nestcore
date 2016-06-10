
package org.nest.core.util;

public class PathUtil {
    private static final String ALL_PATH = PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()
            .toLowerCase();
    private static String path = null;

    public static String getWarPath() {
        // 先确定路径。判断是否是war包
        if (path == null) {
            if (ALL_PATH.indexOf(".war") > 0) {
                path = ALL_PATH.substring(0, ALL_PATH.indexOf(".war"));
            } else {
                path = ALL_PATH.substring(0, ALL_PATH.indexOf("web-inf"));
            }
        }
        return path;
    }
}
