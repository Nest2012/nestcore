package org.nest.mvp.component;

import org.apache.log4j.Logger;

/**
 * 组件配置对象
 * 
 * @author wengyuedon
 */
public class Component implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 3002354627492770638L;

	// 实例编码
	private String id = null;
	// 组件界面js库文件名
	private String jsLib = null;
	// 组件对象名称
	private String jsObjectName = null;

	private ComponentService service = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJsLib() {
		return jsLib;
	}

	public void setJsLib(String jsLib) {
		this.jsLib = jsLib;
	}

	public String getJsObjectName() {
		return jsObjectName;
	}

	public void setJsObjectName(String jsObjectName) {
		this.jsObjectName = jsObjectName;
	}

	public ComponentService getService() {
		return service;
	}

	public void setService(ComponentService service) {
		this.service = service;
	}

}
