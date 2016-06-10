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
import org.nest.mvp.console.RCPConsole;
import org.nest.mvp.console.RCPConsoleManager;
import org.nest.mvp.server.ServerContext;

public class AbstractComponentService implements ComponentService {
	private final static Logger logger = Logger
			.getLogger(AbstractComponentService.class);
	private static ThreadLocal tl = new ThreadLocal();
	private static ThreadLocal tlcom = new ThreadLocal();
	protected RCPConsole console = null;

	@Override
	public HttpServletRequest getRequest() {
		return ((ServerContext) tl.get()).getRequest();
	}

	@Override
	public HttpServletResponse getResponse() {
		return ((ServerContext) tl.get()).getResponse();
	}

	@Override
	public HttpSession getSession() {
		return ((ServerContext) tl.get()).getRequest().getSession();
	}

	public Servlet getPageServlet() {
		return ((ServerContext) tl.get()).getServlet();
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
		return ((ServerContext) tl.get()).getConsole();
	}

	@Override
	public void setComponent(Component com) {
		tlcom.set(com);
	}

	/**
	 * 设置服务端运行环境变量
	 */
	@Override
	public void setServerContext(ServerContext context) {
		tl.set(context);
	}

	/**
	 * 设置终端类型
	 * 
	 * @param consoletype
	 */
	@Override
	public void setConsole(String consoletype) {
		// 当前版本不区分终端类型
		((ServerContext) tl.get()).setConsole(RCPConsoleManager
				.createConsole(this.getRequest()));
	}

	@Override
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

	/**
	 * 根据serviceid，类全名，spring的beanid
	 * 
	 * @param serviceid
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws AppException
	 */
	public ComponentService getComponent(String serviceid)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NestException {
		Component com =null;// ConsoleManager.getManeger().getComponentByServiceid(serviceid);
		ComponentService cs = com.getService();
		cs.setComponent(com);
		cs.setServerContext((ServerContext) tl.get());
		return cs;
	}

	@Override
	public Class getClazz() {
		// TODO Auto-generated method stub
		return this.getClass();
	}

	@Override
	public Map loadComponent(HttpServletRequest request,
			HttpServletResponse response, Map config) throws Exception {
		// TODO Auto-generated method stub
		return config;
	}

}
