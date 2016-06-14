package org.nest.mvp.server.builder;

import java.io.IOException;

import javax.servlet.ServletException;

import org.nest.core.exception.NestException;
import org.nest.mvp.component.Page;

public interface IPageBuilder {

	void setContextPath(String string);

	void buildPageContent() throws ServletException, IOException, NestException;

	void out() throws IOException, ServletException;

	void setOut(IPageBuilderOut out);

	void setPage(Page page);

}
