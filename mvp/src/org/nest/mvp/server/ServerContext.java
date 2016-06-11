package org.nest.mvp.server;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nest.mvp.console.RCPConsole;

public class ServerContext {
	HttpServletRequest request = null;
	HttpServletResponse response = null;
	Servlet pageservlet = null;
	RCPConsole console = null;
	private static ThreadLocal<ServerContext> tl = new ThreadLocal<ServerContext>();
	
	public static ServerContext getContext(){
		return tl.get();
	}
	
	public void setContext(){
		tl.set(this);
	}
	public RCPConsole getConsole() {
		return console;
	}

	public void setConsole(RCPConsole console) {
		this.console = console;
	}

	public ServerContext(HttpServletRequest request,
			HttpServletResponse response, Servlet pageservlet) {
		this.request = request;
		this.response = response;
		this.pageservlet = pageservlet;
		setContext();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * @return
	 */
	public Servlet getServlet() {
		return this.pageservlet;
	}
}
