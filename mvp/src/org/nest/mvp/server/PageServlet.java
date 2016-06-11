package org.nest.mvp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nest.core.exception.NestException;
import org.nest.core.exception.NestRuntimeException;
import org.nest.mvp.cache.PageCache;
import org.nest.mvp.component.Component;
import org.nest.mvp.component.ComponentService;
import org.nest.mvp.component.Page;
import org.nest.mvp.console.RCPConsoleManager;
import org.nest.mvp.gzip.GZIPEncodableResponse;
import org.nest.mvp.json.mapper.ObjectReader;
import org.nest.mvp.json.mapper.ObjectWriter;
import org.nest.mvp.server.builder.IPageBuilder;

public class PageServlet extends HttpServlet {

	private static final long serialVersionUID = 1918995265181860255L;

	private static final Logger logger = Logger.getLogger(PageServlet.class);

	private ServletConfig config = null;

	private static PageCache pc = PageCache.newInstance();

	/**
	 * 处理portal框架初始化设置
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// 记录服务执行日志

		response.setContentType("text/plain;charset=" + PageFactory.encoding);
		response.setCharacterEncoding(PageFactory.encoding);
		String requestURI = request.getRequestURI();
		long start = System.currentTimeMillis();
		String startDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
				.format(new Date());
		String exname = requestURI.substring(requestURI.lastIndexOf("."));
		String noserveranalyselog = request.getParameter("noserveranalyselog");
		new ServerContext(request, response, this);
		try {
			// 从URL中分离模型编码和行为
			// web远程调用与路径无关。

			// edited by zhangkai 修改路径的判断方式，原方式在在有contextname情况下有问题
			if ("webservice.rcp".equals(requestURI.substring(requestURI
					.lastIndexOf("/") + 1))) {
				webservice(request, response);
				return;
			}
			// down远程调用与路径无关。
			if (".down"
					.equals(requestURI.substring(requestURI.lastIndexOf("/") + 1))) {
				consoleDown(request, response);
				return;
			}
			if (".page".endsWith(exname)) {
				pageserver(requestURI, request, response);
				return;
			} else if (".rcp".endsWith(exname)) {
				rcpserver(requestURI, request, response);
				return;
			} else {
				response.sendError(404);
			}
		} catch (Exception e) {
			throw new NestRuntimeException(e);
		} finally {
			if (!"true".equals(noserveranalyselog)) {
				AnalyseAccessLog.getInstance().writeAnalyseLog(startDate,
						(System.currentTimeMillis() - start), request);
			}
		}
	}

	private void pageserver(String requestURI, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException,
			NestException {
		String pageid = requestURI.substring(request.getContextPath().length(),
				requestURI.lastIndexOf(".page"));
		Page page = pc.getPage(pageid);
		requestURI = null;
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", -10);

		// 如果找不到对应的页面配置，则返回404错误
		if (page == null) {
			response.sendError(404);
			return;
		}

		// 设置返回内容文档格式
		response.setContentType("text/html;application/xhtml+xml;charset="
				+ PageFactory.encoding);

		// 生成页面模板
		IPageBuilder pageBuilder = PageFactory.getBuilder(page);// new
																	// PageBuilder(page);
		pageBuilder.setContextPath(request.getScheme() + "://"
				+ request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath());
		// 生成页头加载内容
		pageBuilder.buildHeadContent();
		// 生成页面内容及脚本
		pageBuilder.buildPageContent();
		pageBuilder.out();
	}

	private void webservice(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException,
			NestException {
		String serverid = request.getParameter("serverid");
		String method = request.getParameter("method");
		String paramjson = request.getParameter("paramjson");
		String consoletype = request.getParameter("rcpconsole");

		Component component = pc.getCom(serverid);
		if (null == component) {
			response.sendError(404);
			return;

		}
		try {
			// 获取服务的实现类
			ComponentService service = component.getService();

			// 设置终端类型
			if (consoletype != null)
				service.setConsole(consoletype);
			responseWriter(response, rcpInvoke(service, method, paramjson));

		} catch (NestException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new ServletException(e);
		} finally {
			// 关闭终端
			if (consoletype != null
					&& RCPConsoleManager.getConsole(request) != null
					&& !RCPConsoleManager.getConsole(request).isDownConsole()) {
				RCPConsoleManager.colseConsole(request.getSession().getId());
			}
		}
	}

	private void rcpserver(String requestURI, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException,
			NestException {
		response.setContentType("text/html;charset=" + PageFactory.encoding);
		String serverurl = requestURI.substring(request.getContextPath()
				.length(), requestURI.lastIndexOf("/"));
		String serviceid = requestURI
				.substring(requestURI.lastIndexOf("/") + 1,
						requestURI.lastIndexOf(".rcp"));
		String method = request.getParameter("method");
		String paramjson = request.getParameter("paramjson");
		String consoletype = request.getParameter("rcpconsole");

		// 生成
		// Component component = ConsoleManager.getManeger().getComponentByRCP(
		// serverurl, serviceid);
		Component component = null;
		if (null == component) {
			response.sendError(404);
			return;
		}
		try {
			// 获取服务的实现类
			ComponentService service = component.getService();

			ServerContext context = new ServerContext(request, response, this);
			// 设置终端类型
			if (consoletype != null)
				service.setConsole(consoletype);
			responseWriter(response, rcpInvoke(service, method, paramjson));
		} catch (NestException e) {
			throw e;
		} catch (Exception e) {
			// logger.error("", e);
			ServletException se = new ServletException(e);
			se.setStackTrace(e.getStackTrace());
			throw se;
		} finally {
			// 关闭终端
			if (consoletype != null)
				RCPConsoleManager.colseConsole(request.getSession().getId());
		}
	}

	/**
	 * 执行远程调用方法
	 * 
	 * @param service
	 * @param method
	 * @param paramjson
	 * @return
	 * @return
	 * @throws NestException
	 */
	private String rcpInvoke(ComponentService service, String method,
			String paramjson) throws NestException {
		Object[] params = null;
		long start = System.currentTimeMillis();
		String startDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
				.format(new Date());
		try {
			// 构造调用参数对象
			ObjectWriter writer = new ObjectWriter();
			params = ((List) writer.getObjectWithOutStructure(paramjson))
					.toArray();
		} catch (Exception e) {
			logger.error("参数类型转换失败！", e);
			throw new RuntimeException(e);
		}

		try {
			Object rt = service.runFunction(method, params);
			ObjectReader reader = new ObjectReader(rt);
			return reader.getObjectValue();
		} catch (NestException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("组件服务运行异常", e);
			throw new RuntimeException(e);
		}
	}

	private void responseWriter(HttpServletResponse response, String s)
			throws IOException {
		OutputStream out = null;
		if (s.getBytes().length < 1024 * 100) {
			out = response.getOutputStream();
		} else {
			GZIPEncodableResponse gzipResponse = new GZIPEncodableResponse(
					response);
			out = gzipResponse.getOutputStream();
		}
		out.write(s.getBytes(PageFactory.encoding));
		out.flush();
	}

	private void responseWriter(HttpServletResponse response, InputStream s)
			throws IOException {
		OutputStream out = null;

		out = response.getOutputStream();
		byte[] b = new byte[1024];
		int l = 0;
		while ((l = s.read(b)) != -1) {
			out.write(b, 0, l);
		}
		out.flush();
		s.close();
	}

	private void responseWriter(HttpServletResponse response, byte[] s)
			throws IOException {
		OutputStream out = null;
		if (s == null) {
			return;
		}
		out = response.getOutputStream();
		out.write(s);
		out.flush();
	}

	private void consoleDown(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		try {
			response.reset();
			response.setContentType(RCPConsoleManager.getConsole(request)
					.getContentType());
			Map header = RCPConsoleManager.getConsole(request).getHeader();
			Iterator it = header.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				response.setHeader(key, (String) header.get(key));
			}
			this.responseWriter(response, RCPConsoleManager.getConsole(request)
					.getFileInputStream());
		} catch (Exception e) {
			logger.error("", e);
			throw new ServletException(e);
		} finally {
			RCPConsoleManager.colseConsole(request.getSession().getId());
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		return config;
	}
}
