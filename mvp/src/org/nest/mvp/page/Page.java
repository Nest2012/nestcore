package org.nest.mvp.page;

import org.nest.mvp.component.Component;

/**
 * Portal定义的页面对象，保存组织页面的基本配置信息
 * 
 * @author wengyuedon
 */
public class Page extends Component {
	private String tempid = null;

	private String path = null;

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

}
