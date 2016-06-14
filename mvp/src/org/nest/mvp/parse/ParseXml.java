package org.nest.mvp.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.nest.core.exception.NestException;
import org.nest.mvp.cache.PageCache;
import org.nest.mvp.parse.handler.ParseNestHandler;

public class ParseXml {

	private ParseXml() {
	}

	private static final ParseXml PIX = new ParseXml();

	public static final ParseXml getInstance() {
		return PIX;
	}
	
	private ParseNestHandler handler = new ParseNestHandler();

	public void readFile(InputStream io,PageCache cache) throws NestException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			handler.setPc(cache);
			parser.parse(io, handler);
		} catch (Exception e) {
			throw new NestException(e);
		}
	}

	public void readFile(File io,PageCache cache) throws NestException {
		try {
			this.readFile(new FileInputStream(io),cache);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new NestException(e);
		}
	}
}
