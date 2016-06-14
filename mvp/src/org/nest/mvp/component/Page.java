package org.nest.mvp.component;

/**
 * Portal定义的页面对象，保存组织页面的基本配置信息
 * 
 * @author wengyuedon
 */
public class Page extends Component {
	private String tempid = null;

	private String csslib = null;

	private String superid = null;

	private String path = null;

	private String regin = null;

	public String getRegin() {
		return regin;
	}

	public void setRegin(String regin) {
		this.regin = regin;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTempid() {
		return tempid;
	}

	public void setTempid(String tempid) {
		this.tempid = tempid;
	}

	public String getCssLib() {
		return csslib;
	}

	public void setCssLib(String csslib) {
		this.csslib = csslib;
	}

	public String getSuperid() {
		return superid;
	}

	public void setSuperid(String superid) {
		this.superid = superid;
	}

}
