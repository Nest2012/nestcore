package org.nest.mvp.cache;

import java.util.HashMap;
import java.util.Map;

import org.nest.mvp.component.Component;
import org.nest.mvp.component.Template;
import org.nest.mvp.page.Page;

public class PageCache {
	private Map<String, Component> com = new HashMap<String, Component>();
	private Map<String, Template> tem = new HashMap<String, Template>();
	private Map<String, Page> page = new HashMap<String, Page>();
	private Map<String, Map<String, String>> pagecfg = new HashMap<String, Map<String, String>>();
	private Map<String, Map<String, Map<String, String>>> comcfg = new HashMap<String, Map<String, Map<String, String>>>();

	public void putCom(Component cmp) {
		com.put(cmp.getId(), cmp);
	}

	public Component getCom(String id) {
		return com.get(id);
	}

	public Template getTem(String key) {
		return this.tem.get(key);
	}

	public void setTem(Template tem) {
		this.tem.put(tem.getId(), tem);
	}

	public Page getPage(String key) {
		return page.get(key);
	}

	public void setPage(Page page) {
		this.page.put(page.getId(), page);
	}

	public Map<String, String> getPagecfg(String key) {
		return pagecfg.get(key);
	}

	public void setPagecfg(String id, Map<String, String> cfg) {
		this.pagecfg.put(id, cfg);
	}

	public Map<String, Map<String, String>> getComcfg(String key) {
		return comcfg.get(key);
	}

	public void addComcfg(String key, String cmpid, Map<String, String> cfg) {
		if (comcfg.get(key) == null) {
			comcfg.put(key, new HashMap<String, Map<String, String>>());
		}
		comcfg.get(key).put(cmpid, cfg);
	}

}
