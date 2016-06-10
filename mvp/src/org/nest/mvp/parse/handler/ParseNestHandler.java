package org.nest.mvp.parse.handler;

import org.nest.mvp.cache.PageCache;
import org.nest.mvp.page.Page;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseNestHandler extends DefaultHandler {
	private static String pages = "pages";
	private static String component = "component";
	private static String page = "page";
	private static String template = "template";
	private static String config = "config";

	private String url = null;

	private PageCache pc = null;

	public PageCache getPc() {
		return pc;
	}

	public void setPc(PageCache pc) {
		this.pc = pc;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(component.equals(qName)){
			
		}
		if(template.equals(qName)){
			
		}
		if (page.equals(qName)) {
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}
	
	private void putComponent(Attributes attributes){
		
	}
	private void putTemplate(Attributes attributes){
		
	}
	private void putPage(Attributes attributes){
		
	}
}
