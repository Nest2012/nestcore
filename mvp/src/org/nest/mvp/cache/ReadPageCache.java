package org.nest.mvp.cache;

public class ReadPageCache {
	private static final ReadPageCache READ_CACHE = new ReadPageCache();

	private ReadPageCache() {
	}

	public static ReadPageCache newInstance() {
		return READ_CACHE;
	}

	private PageCache pc = null;

	public PageCache getPc() {
		return pc;
	}

	public void setPc(PageCache pc) {
		this.pc = pc;
	}

}
