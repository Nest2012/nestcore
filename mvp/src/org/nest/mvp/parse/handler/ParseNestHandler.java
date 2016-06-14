package org.nest.mvp.parse.handler;

import java.util.HashMap;
import java.util.Map;

import org.nest.core.bean.BeanUtil;
import org.nest.core.exception.NestException;
import org.nest.mvp.cache.PageCache;
import org.nest.mvp.component.Component;
import org.nest.mvp.component.ComponentService;
import org.nest.mvp.component.Page;
import org.nest.mvp.component.Template;
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
	private String cmpid=null;
	private String url = null;

	private PageCache pc = null;

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
				cmpid=att.getValue("cmpid");
				view = att.getValue("view");
				comcfg = new HashMap<String, String>();
				comcfg.put("id", view);
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
			comcfg = null;
			url = null;
			cmpid=null;
		} else if (cmp.equals(qName)) {
			this.pc.addComcfg(url,cmpid, view, this.comcfg);
			comcfg = null;
			cmpid=null;
			view = null;
		}
	}

	// <component id="123" beanid="" class="" jslib="" jsobjname="">
	private void putComponent(Attributes att) throws NestException {
		Component com = new Component();
		try {
			com.setJsObjectName(att.getValue("jsobjname"));
			com.setId(att.getValue("id"));
			com.setJsLib(att.getValue("jslib"));
			String cls = att.getValue("class");
			String beanid = att.getValue("beanid");
			ComponentService cs = null;
			if (cls == null || cls.trim().length() == 0) {
				try {
					cs = (ComponentService) Class.forName(cls).newInstance();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (beanid != null && beanid.trim().length() > 0) {
				cs = (ComponentService) BeanUtil
						.getBean(att.getValue("beanid"));
			} else {
				throw new NullPointerException("没有找到bean注册信息");
			}
			com.setService(cs);
			this.getPc().setCom(com);
		} catch (Exception e) {
			throw new NestException(com.getId() + "加载失败，没有成功加载bean", e);
		}
	}

	// <template id="123" jslib="" jsobjname="">
	private void putTemplate(Attributes att) throws NestException {
		Template com = new Template();
		com.setId(att.getValue("id"));
		com.setJsLib(att.getValue("jslib"));
		com.setJsObjectName(att.getValue("jsobjname"));
		this.getPc().setTem(com);
	}

	// <page url="123" beanid="" class="" jslib="" csslib="" templateid="123"
	// path="">
	private void putPage(Attributes att) throws NestException {
		Page com = new Page();
		try {
			com.setCssLib(att.getValue("csslib"));
			com.setTempid(att.getValue("templateid"));
			com.setSuperid(att.getValue("superurl"));
			com.setJsLib(att.getValue("jslib"));
			String cls = att.getValue("class");
			String beanid = att.getValue("beanid");
			ComponentService cs = null;
			if (cls != null && cls.trim().length() > 0) {
				try {
					cs = (ComponentService) Class.forName(cls).newInstance();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (beanid != null && beanid.trim().length() > 0) {
				cs = (ComponentService) BeanUtil
						.getBean(att.getValue("beanid"));
			}
			com.setService(cs);
			com.setId(att.getValue("url"));
			com.setRegin(att.getValue("regin"));
			url = com.getId();
			this.getPc().setPage(com);
		} catch (Exception e) {
			throw new NestException(com.getId() + "加载失败，没有成功加载bean", e);
		}
		url = com.getId();
		pagecfg = new HashMap<String, String>();
	}

	public static void main(String[] args) throws NestException {
		ParseXml.getInstance()
				.readFile(
						Class.class
								.getResourceAsStream("/org/nest/mvp/parse/xsd/NewFile.xml"),
						new PageCache());
		// System.out.println(PageCache.newInstance());
	}
}
