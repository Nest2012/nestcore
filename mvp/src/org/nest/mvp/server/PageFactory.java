package org.nest.mvp.server;

import org.nest.core.exception.NestException;
import org.nest.mvp.component.Page;
import org.nest.mvp.server.builder.IPageBuilder;
import org.nest.mvp.server.builder.IPageBuilderOut;
import org.nest.mvp.server.builder.impl.PageBuilder;
import org.nest.mvp.server.builder.impl.PageBuilderOut;

public class PageFactory {
	public static String encoding = "UTF-8";
	public static String page = ".page";
	public static String ser = ".ser";
	public static String down = ".down";
	public static Class<? extends IPageBuilder> builderClass = null;
	public static Class<? extends IPageBuilderOut> outClass = null;

	public static IPageBuilder getBuilder(Page page) throws NestException {
		IPageBuilder builder = null;
		if (builderClass == null) {
			builder = new PageBuilder();
		} else {
			try {
				builder = (IPageBuilder) builderClass.newInstance();
			} catch (Exception e) {
				throw new NestException(e);
			}
		}
		builder.setOut(getOut());
		builder.setPage(page);
		return builder;
	}

	private static IPageBuilderOut getOut() throws NestException {
		if (builderClass == null) {
			return new PageBuilderOut();
		} else {
			try {
				return (IPageBuilderOut) outClass.newInstance();
			} catch (Exception e) {
				throw new NestException(e);
			}
		}
	}

}
