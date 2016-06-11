package org.nest.mvp.server.builder.impl;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.nest.mvp.server.ServerContext;
import org.nest.mvp.server.builder.IPageBuilderOut;

public class PageBuilderOut implements IPageBuilderOut {

	protected String contextPath = null;
	protected String page = null;
	protected ArrayList<String> jsList = new ArrayList<String>();
	protected ArrayList<String> cssList = new ArrayList<String>();
	protected String bodyStr = null;
	private static final long nowtime = System.currentTimeMillis();

	public String getContextPath() {
		return contextPath;
	}
	@Override
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	private void initLoadInfo(StringBuilder sb) {
		sb.append("<script>var _ROOT_PATH_='").append(contextPath)
				.append("';</script>\r\n");
	}

	private void buildJsContent(StringBuilder sbs) {
		for (String js : jsList) {
			sbs.append("<script type=\"text/javascript\" src=\"")
					.append(contextPath).append(js).append("?").append(nowtime)
					.append("\"></script>").append("\r\n");
		}
	}

	/**
	 * 操作台加载当前css元素
	 * 
	 * @param page
	 * @param sb
	 */
	private void buildCssContent(StringBuilder sbs) {
		for (String css : cssList) {
			sbs.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			sbs.append(contextPath);
			sbs.append(css).append("?").append(nowtime).append("\"/>\r\n");
		}
	}

	public PageBuilderOut() {
	}

	@Override
	public void setJs(String js) {
		if (js == null || js.trim().length() == 0) {
			return;
		}
		if (jsList.indexOf(js) == -1) {
			jsList.add(js);
		}
	}

	@Override
	public void setCss(String css) {
		if (css == null || css.trim().length() == 0) {
			return;
		}
		if (cssList.indexOf(css) == -1) {
			cssList.add(css);
		}
	}

	@Override
	public void setBody(String content) {
		bodyStr = content;
	}

	private void buildHead(StringBuilder sbs) {
		this.buildCssContent(sbs);
		this.buildJsContent(sbs);
	}

	@Override
	public void setPage(String page) {
		if (this.page == null && page != null && page.trim().length() > 0) {
			this.page = page;
		}
	}

	@Override
	public void flash() throws IOException, ServletException {

		StringBuilder sbs = new StringBuilder(11240);
		initLoadInfo(sbs);
		buildHead(sbs);
		if (page == null) {
			// page = DEFAULT_PAGE;
		} else {
			ServerContext
					.getContext()
					.getRequest()
					.getRequestDispatcher(page)
					.include(ServerContext.getContext().getRequest(),
							ServerContext.getContext().getResponse());
		}
		ServerContext.getContext().getResponse().getWriter().append(sbs);
		ServerContext.getContext().getResponse().getWriter().append(bodyStr);
		ServerContext.getContext().getResponse().getWriter().flush();
	}
}
