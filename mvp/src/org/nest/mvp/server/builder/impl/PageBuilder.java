package org.nest.mvp.server.builder.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.nest.core.exception.NestException;
import org.nest.mvp.component.Page;
import org.nest.mvp.server.builder.IPageBuilder;
import org.nest.mvp.server.builder.IPageBuilderOut;

/**
 * 创建操作台页面内容
 * 
 * @author wengyuedon
 */
public class PageBuilder implements IPageBuilder {
	private final static Logger logger = Logger.getLogger(PageBuilder.class);
	private Page page = null;
	private List<Page> pes = null;
	private IPageBuilderOut out = null;

	public PageBuilder() throws NestException {

	}

	public IPageBuilderOut getOut() {
		return out;
	}

	@Override
	public void setOut(IPageBuilderOut out) {
		this.out = out;
	}

	public Page getPage() {
		return page;
	}

	@Override
	public void setPage(Page page) {
		this.page = page;
	}

	public void buildHeadContent() {
		for (int i = pes.size() - 1; i >= 0; i--) {
			Page p = pes.get(i);
			String jsStr = p.getJsLib();
			String[] jss = jsStr == null ? new String[0] : jsStr.split(",");
			for (String js : jss) {
				out.setJs(js);
			}
			String cssStr = p.getCssLib();
			String[] csss = cssStr == null ? new String[0] : cssStr.split(",");
			for (String css : csss) {
				out.setCss(css);
			}
		}
	}

	public void buildPageContent() throws ServletException, IOException,
			NestException {
		// String content = createBody();
		// out.setBody(content);
	}

	public void out() throws IOException, ServletException {
		out.flash();

	}

	public void setContextPath(String contextPath) {
		// this.contextPath = contextPath;
		// out.setContextPath(contextPath);
	}
}
