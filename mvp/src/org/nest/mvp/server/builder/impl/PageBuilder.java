package org.nest.mvp.server.builder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.nest.core.exception.NestException;
import org.nest.mvp.cache.PageCache;
import org.nest.mvp.cache.PageCacheManager;
import org.nest.mvp.component.Component;
import org.nest.mvp.component.ComponentService;
import org.nest.mvp.component.Page;
import org.nest.mvp.component.Template;
import org.nest.mvp.json.mapper.ObjectReader;
import org.nest.mvp.server.builder.IPageBuilder;
import org.nest.mvp.server.builder.IPageBuilderOut;

/**
 * 创建操作台页面内容
 * 
 */
public class PageBuilder implements IPageBuilder {
	// private final static Logger logger = Logger.getLogger(PageBuilder.class);
	private Page page = null;
	private List<Page> pes = new ArrayList<Page>();
	private List<Map> pesconfig = new ArrayList<Map>();
	private Map<String, Map> conconfig = new HashMap<String, Map>();
	private Map<String, String> conbeans = new HashMap<String, String>();
	private IPageBuilderOut out = null;
	private PageCache pc = PageCacheManager.newInstance().getPc();

	private StringBuffer sb = new StringBuffer(102400);

	public PageBuilder() {

	}

	public IPageBuilderOut getOut() {
		return out;
	}

	@Override
	public void setOut(IPageBuilderOut out) {
		this.out = out;
	}

	public Page getPage() {
		return page;
	}

	@Override
	public void setPage(Page page) {
		this.page = page;
	}

	public void buildHeadContent() {
		for (int i = pes.size() - 1; i >= 0; i--) {
			Page p = pes.get(i);
			String jsStr = p.getJsLib();
			String[] jss = jsStr == null ? new String[0] : jsStr.split(",");
			for (String js : jss) {
				out.setJs(js);
			}
			String cssStr = p.getCssLib();
			String[] csss = cssStr == null ? new String[0] : cssStr.split(",");
			for (String css : csss) {
				out.setCss(css);
			}
		}
		
	}

	public void buildPageContent() throws ServletException, IOException,
			NestException {
		sb.append("<script>");
		this.setPageInfo(page);
		buildHeadContent();
		// 循环创建并调用com
		sb.append("</script>");
	}

	private void buildCompoents() throws IOException {
		Set<String> views = conbeans.keySet();
		for (String view : views) {
			Map config = conconfig.get(view);
			config.put("serverid", conbeans.get(view));
			Component com = pc.getCom(conbeans.get(view));
            if (com.getJsLib() != null) {
                String js[] = com.getJsLib().split(",");
                for (String j : js) {
                    out.setJs(j);
                }
            }
			ObjectReader reader = new ObjectReader(config);
			sb.append("var " + view + "cfg=").append(reader.getObjectValue())
					.append(";\r\n");
			sb.append("var ").append(view).append("= new ")
					.append(com.getJsObjectName()).append("(").append(view)
					.append("cfg").append(",'").append(conbeans.get(view))
					.append("'").append(");\r\n");
		}
	}

	private void buildPages() throws NestException {
		for (int i = pes.size() - 1; i >= 0; i--) {
			Page p = this.pes.get(i);
			ComponentService cs = p.getService();
			if (cs != null) {
				try {
					Map config = cs.loadComponent(this.pesconfig.get(i));
					
					String tid=page.getTempid();
					
					Template temp=pc.getTem(tid);
					String js[] = temp.getJsLib().split(",");
	                for (String j : js) {
	                    out.setJs(j);
	                }
					ObjectReader reader = new ObjectReader(config);
					sb.append("var tempconfig=").append(reader.getObjectValue()).append(";\r\n");
					sb.append("var template= new ");
					sb.append(temp.getJsObjectName() + "(tempconfig)").append(";\r\n");
					if (page.getRegin() != null
							&& page.getRegin().trim().length() > 0) {
						sb.append("var region=document.getElementById('"
								+ page.getRegin() + "');\r\n");
						sb.append("if(region==null){region=document.body};\r\n");
					} else {
						sb.append("var region=document.body;\r\n");
					}
					sb.append("template.draw(region);\r\n");
				} catch (Exception e) {
					throw new NestException(e);
				}

			}
		}
	}

	private void setPageInfo(Page p) {
		if (p == null) {
			return;
		}
		this.pes.add(p);
		Map m = new HashMap();
		if (pc.getPagecfg(p.getId()) != null) {
			m.putAll(pc.getPagecfg(p.getId()));
		}
		m.put("regin", page.getRegin());
		// 合并页面配置信息
		Map<String, Map<String, String>> ccfg = pc.getComcfg(p.getId());
		Map<String, String> viewinfo = pc.getViewCfg(p.getId());
		Set<String> keys = ccfg.keySet();
		for (String key : keys) {
			if (conbeans.get(key) == null) {
				conbeans.put(key, viewinfo.get(key));
			}
			Map cc = new HashMap();
			cc.putAll(ccfg.get(key));
			if (conconfig.get(key) != null) {
				cc.putAll(conconfig.get(key));
			}
			conconfig.put(key, cc);
		}
		this.pesconfig.add(m);
		this.setPageInfo(pc.getPage(p.getSuperid()));
	}

	public void out() throws IOException, ServletException {
		out.setBody(sb.toString());
		out.flash();
	}

	public void setContextPath(String contextPath) {
		out.setContextPath(contextPath);
	}

	public static void main(String[] s) {
		Map m = new HashMap();
		m.put("a", "a");
		Map m1 = new HashMap();
		m1.putAll(m);
		m1.put("a", "b");
		System.out.println(m.get("a"));

	}
}
