package org.nest.mvp.parse.handler;

import java.util.HashMap;
import java.util.Map;

import org.nest.core.bean.BeanUtil;
import org.nest.core.exception.NestException;
import org.nest.mvp.cache.PageCache;
import org.nest.mvp.component.Component;
import org.nest.mvp.component.ComponentService;
import org.nest.mvp.component.Template;
import org.nest.mvp.page.Page;
import org.nest.mvp.parse.ParseXml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseNestHandler extends DefaultHandler {
	private static String map = "map";
	private static String component = "component";
	private static String page = "page";
	private static String template = "template";
	private static String config = "config";
	private static String cmp = "cmp";

	private String url = null;

	private PageCache pc = null;

	private String cmpid = null;
	private String view = null;

	private Map<String, String> pagecfg = null;
	private Map<String, String> comcfg = null;

	public PageCache getPc() {
		return pc;
	}

	public void setPc(PageCache pc) {
		this.pc = pc;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes att) throws SAXException {
		try {
			if (component.equals(qName)) {
				putComponent(att);
			} else if (template.equals(qName)) {
				putTemplate(att);
			} else if (page.equals(qName)) {
				putPage(att);
			} else if (map.equals(qName)) {
				if (url == null) {
					return;
				}
				pagecfg.put(att.getValue("key"), att.getValue("value"));
			} else if (cmp.equals(qName)) {
				if (url == null) {
					return;
				}
				// cmpid='123' view='123'
				cmpid = att.getValue("cmpid");
				view = att.getValue("view");
				comcfg = new HashMap<String, String>();
				comcfg.put("view", view);
			} else if (config.equals(qName)) {
				if (url == null) {
					return;
				}
				comcfg.put(att.getValue("key"), att.getValue("value"));

			}
		} catch (NestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (page.equals(qName)) {
			this.pc.setPagecfg(url, pagecfg);
			pagecfg = null;
			view = null;
			cmpid = null;
			comcfg = null;
			url = null;
		} else if (cmp.equals(qName)) {
			this.pc.addComcfg(url, cmpid, this.comcfg);
			cmpid = null;
			comcfg = null;
		}
	}

	// <component id="123" beanid="" class="" jslib="" jsobjname="">
	private void putComponent(Attributes att) throws NestException {
		Component com = new Component();
		try {
			com.setJsObjectName(att.getValue("jsobjname"));
			setAttr(com, att);
			this.getPc().setCom(com);
		} catch (Exception e) {
			throw new NestException(com.getId() + "加载失败，没有成功加载bean", e);
		}
	}

	// <template id="123" beanid="" class="" jslib="" jsobjname="">
	private void putTemplate(Attributes att) throws NestException {
		Template com = new Template();
		try {
			com.setJsObjectName(att.getValue("jsobjname"));
			setAttr(com, att);
			this.getPc().setTem(com);
		} catch (Exception e) {
			throw new NestException(com.getId() + "加载失败，没有成功加载bean", e);
		}
	}

	// <page url="123" beanid="" class="" jslib="" csslib="" templateid="123"
	// path="">
	private void putPage(Attributes att) throws NestException {
		Page com = new Page();
		try {
			com.setCssid(att.getValue("csslib"));
			com.setTempid(att.getValue("templateid"));
			com.setSuperid(att.getValue("superurl"));
			setAttr(com, att);
			com.setId(att.getValue("url"));
			url=com.getId();
			this.getPc().setPage(com);
		} catch (Exception e) {
			throw new NestException(com.getId() + "加载失败，没有成功加载bean", e);
		}
		url = com.getId();
		pagecfg = new HashMap<String, String>();
	}

	private void setAttr(Component com, Attributes att) {
		com.setId(att.getValue("id"));
		com.setJsLib(att.getValue("jslib"));
		String cls = att.getValue("class");
		ComponentService cs = null;
		if (cls == null || cls.trim().length() == 0) {
			try {
				cs = (ComponentService) Class.forName(cls).newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			BeanUtil.getBean(att.getValue("beanid"));
		}
		com.setService(cs);
	}

	public static void main(String[] args) throws NestException {
		ParseXml.getInstance()
				.readFile(
						Class.class
									.getResourceAsStream("/org/nest/mvp/parse/xsd/NewFile.xml"));
		System.out.println(PageCache.newInstance());
	}
}
