package org.nest.mvp.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nest.core.exception.NestException;
import org.nest.mvp.cache.PageCacheManager;
import org.nest.mvp.console.RCPConsole;
import org.nest.mvp.console.RCPConsoleManager;
import org.nest.mvp.server.ServerContext;

public class AbstractComponentService implements ComponentService {
	private final static Logger logger = Logger
			.getLogger(AbstractComponentService.class);

	private static ThreadLocal tlcom = new ThreadLocal();

	@Override
	public HttpServletRequest getRequest() {
		return ServerContext.getContext().getRequest();
	}

	@Override
	public HttpServletResponse getResponse() {
		return ServerContext.getContext().getResponse();
	}

	@Override
	public HttpSession getSession() {
		return ServerContext.getContext().getRequest().getSession();
	}

	public Servlet getPageServlet() {
		return ServerContext.getContext().getServlet();
	}

	/**
	 * 获取组件定义
	 * 
	 * @param component
	 */
	@Override
	public Component getComponent() {
		return (Component) tlcom.get();
	}

	@Override
	public RCPConsole getConsole() {
		// TODO Auto-generated method stub
		return ServerContext.getContext().getConsole();
	}

	@Override
	public void setComponent(Component com) {
		tlcom.set(com);
	}

	/**
	 * 设置终端类型
	 * 
	 * @param consoletype
	 */
	@Override
	public void setConsole(String consoletype) {
		// 当前版本不区分终端类型
		ServerContext.getContext().setConsole(
				RCPConsoleManager.createConsole(this.getRequest()));
	}

	public Object runFunction(String mothedname, Object[] params)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NestException, Exception {
		Method invokeMethod = null;

		if (params == null) {
			invokeMethod = this.getClass().getMethod(mothedname, null);
		} else {
			Class[] paramscls = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramscls[i] = params[i].getClass();
			}

			invokeMethod = this.getClass().getMethod(mothedname, paramscls);
		}
		Object invoke = null;
		try {
			invoke = invokeMethod.invoke(this, params);
		} catch (Exception e) {
			throw e;
		}
		return invoke;
	}

	public ComponentService getComponent(String serviceid)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NestException {
		Component cs =PageCacheManager.newInstance().getPc().getCom(serviceid);
		return cs.getService();
	}

	@Override
	public Map loadComponent(Map config) throws Exception {
		return config;
	}

}
