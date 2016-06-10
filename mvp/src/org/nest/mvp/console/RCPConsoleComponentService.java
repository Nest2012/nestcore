
package org.nest.mvp.console;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nest.mvp.component.AbstractComponentService;

public class RCPConsoleComponentService extends AbstractComponentService {

    @Override
    public Map loadComponent(HttpServletRequest request, HttpServletResponse response, Map config) throws Exception {

        return null;
    }

    /**
     * 获取控制台消息
     * @return
     */
    public String[] getConsoleMessage() {
        RCPConsole console = RCPConsoleManager.getConsole(this.getRequest());
        if (console == null) {
            return null;
        } else {
            return console.getMessage();
        }
    }

    /**
     * 返回进度显示
     * @return
     */
    public double getProcess() {
        RCPConsole console = RCPConsoleManager.getConsole(this.getRequest());
        if (console == null) {
            return 0;
        } else {
            return console.getProcess();
        }
    }

    public Map getProcessMessage() {
        Map m = new HashMap();
        m.put("text", getConsoleMessage());
        m.put("process", new Double(getProcess()));
        return m;
    }

}
