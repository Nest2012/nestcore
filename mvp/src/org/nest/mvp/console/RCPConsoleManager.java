package org.nest.mvp.console;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RCPConsoleManager {

    static private Map consolemap = new HashMap();

    // 使用守护线程清理终端
    static {
        Thread angle = new angle();
        angle.setDaemon(false);
        angle.start();
    }

    public static RCPConsole getConsole(HttpServletRequest request) {
        String sessionid = request.getSession().getId();
        return (RCPConsole) consolemap.get(sessionid);
    }

    public static RCPConsole createConsole(HttpServletRequest request) {
        String sessionid = request.getSession().getId();
        if (getConsole(request) != null) {
            getConsole(request).close();
        }
        consolemap.put(sessionid, RCPConsole.getConsole(request));
        return (RCPConsole) consolemap.get(sessionid);
    }

    public static void colseConsole(String sessionid) {
        if (consolemap.get(sessionid) == null) return;

        ((RCPConsole) consolemap.remove(sessionid)).close();
    }

    static class angle extends Thread {
        private static Object _clock = new Object();

        public void run() {
            while (true) {
                Object keys[] = consolemap.keySet().toArray();
                for (int i = 0; i < keys.length; i++) {
                    RCPConsole consoel = (RCPConsole) consolemap.get(keys[i]);
                    if (consoel.isTimeout()) {
                        colseConsole((String) keys[i]);
                    }
                }

                // 这里暂停线程，等待下次启动
                synchronized (_clock) {
                    try {
                        _clock.wait(60000);
                    }
                    catch (Throwable e) {
                        e.printStackTrace();

                        // 一旦出错，重新启动守护进程
                        Thread angle = new angle();
                        angle.setDaemon(false);
                        angle.start();

                        return;
                    }
                }
            }
        }
    }
}
