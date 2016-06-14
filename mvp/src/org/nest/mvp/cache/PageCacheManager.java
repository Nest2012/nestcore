package org.nest.mvp.cache;

import org.nest.core.exception.NestException;
import org.nest.mvp.parse.ParseXml;

public class PageCacheManager {
	private static PageCacheManager READ_CACHE = new PageCacheManager();

	private PageCacheManager() {
	}

	private String path = null;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static PageCacheManager newInstance() {
		return READ_CACHE;
	}

	private PageCache pc = null;

	public PageCache getPc() {
		return pc;
	}

	public void initPageCache() throws NestException {
		PageCache cahce = new PageCache();
		ParseXml.getInstance().readFile(
				Class.class.getResourceAsStream(this.getPath()), cahce);
		this.pc = cahce;
	}
}
