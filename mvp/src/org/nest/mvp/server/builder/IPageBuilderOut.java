package org.nest.mvp.server.builder;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IPageBuilderOut {

	void setJs(String js);

	void setCss(String css);

	void flash() throws IOException, ServletException;

	void setPage(String page);

	void setBody(String content);

	void setContextPath(String contextPath);

}
