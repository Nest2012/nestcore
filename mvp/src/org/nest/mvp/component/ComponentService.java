package org.nest.mvp.component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nest.core.exception.NestException;
import org.nest.mvp.console.RCPConsole;
import org.nest.mvp.server.ServerContext;

/**
 * 组件服务接口，定义了组件服务的标准接口
 * 
 * @author wengyuedon
 */
public interface ComponentService {

	/**
	 * 操作台加载时框架调用该方法，产生组件初始化配置
	 * 
	 * @param request
	 * @param response
	 * @param config
	 *            组件默认配置
	 * @return
	 */
	public Map loadComponent(Map config) throws Exception;

	/**
	 * 获取组件定义
	 * 
	 * @param component
	 */
	public Component getComponent();

	/**
	 * 设置终端类型
	 * 
	 * @param consoletype
	 */
	public void setConsole(String consoletype);

	/**
	 * 设置终端类型
	 * 
	 * @param consoletype
	 */
	public RCPConsole getConsole();

	public void setComponent(Component component);

	public HttpServletRequest getRequest();

	public HttpServletResponse getResponse();

	public HttpSession getSession();

	/**
	 * 实现服务接口的反射调用机制
	 * 
	 * @param mothedname
	 * @param params
	 * @return
	 * @throws AppException
	 * @throws Exception
	 * @throws
	 */
	public Object runFunction(String mothedname, Object[] params)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NestException, Exception;
}
