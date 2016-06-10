package org.nest.mvp.console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nest.core.util.FileManage;
import org.nest.core.util.PathUtil;

public class RCPConsole implements java.io.Serializable {
	// 通过sessionid区分客户端
	private String sessionid = null;
	// 对象创建事件
	private final long createtime = System.currentTimeMillis();
	// 默认超时时间10分钟
	private final int timeout = 60000 * 10;
	// 消息堆栈
	private LinkedList list = null;
	// 执行进度
	private double process = 0.0;
	// 文件流
	private OutputStream out = null;
	// 下载的文件
	private File tem = null;

	private String contentType = null;

	private final HashMap header = new HashMap();

	private InputStream ipt = null;

	private RCPConsole(HttpServletRequest request) {
		sessionid = request.getSession().getId();
		list = new LinkedList();
		try {
			removeTemFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static RCPConsole getConsole(HttpServletRequest request) {
		return new RCPConsole(request);
	}

	public String getId() {
		return sessionid;
	}

	public synchronized void println(String msg) {
		if (list != null) {
			list.add(msg);
		}
	}

	/**
	 * 返回消息提示内容
	 * 
	 * @return
	 */
	public synchronized String[] getMessage() {
		if (list == null)
			return null;
		String msg[] = new String[list.size()];
		for (int i = 0; i < msg.length; i++) {
			msg[i] = list.remove(0).toString();
		}
		return msg;
	}

	/**
	 * 返回进度
	 * 
	 * @return
	 */
	public synchronized double getProcess() {
		return process;
	}

	/**
	 * 设置进度
	 * 
	 * @param p
	 *            0到1之间的数字
	 */
	public synchronized void setProcess(double p) {
		if (p < 0) {
			process = 0;
		} else if (p > 1) {
			process = 1;
		} else {
			process = p;
		}
	}

	/**
	 * 检测是否超时
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		return System.currentTimeMillis() - createtime > timeout;
	}

	public void close() {
		list.clear();
		deleteFile();
		list = null;
	}

	public OutputStream getOutputStream() throws Exception {
		if (out == null) {

			tem = new File(getDocFile(), "tempRCPConsole"
					+ System.currentTimeMillis());
			out = new FileOutputStream(tem);
		}
		return out;
	}

	private File getDocFile() {
		String warpath = PathUtil.getWarPath();
		String tempath = "temp";

		File doc = new File(warpath, tempath);
		if (!doc.exists()) {
			doc.mkdirs();
		}
		return doc;
	}

	public void temp2Zip(String filename) throws Exception {
		File endTem = new File(getDocFile(), "tempRCPConsole"
				+ System.currentTimeMillis());
		FileManage.file2Zip(tem, endTem, filename);
		deleteFile();
		tem = endTem;
	}

	public void setDowInfo(String filename) throws UnsupportedEncodingException {
		this.setContentType("application/zip");
		this.setHeader("Content-Disposition", "attachment;   filename="
				+ URLEncoder.encode(filename + ".zip", "UTF-8"));
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setHeader(String key, String value) {
		this.header.put(key, value);
	}

	public InputStream getFileInputStream() throws Exception {
		if (tem == null)
			throw new RuntimeException("没有可下载文件");
		if (ipt == null) {
			ipt = new RCPInputStream(tem);
		}
		return ipt;
	}

	public String getContentType() {
		return contentType;
	}

	public Map getHeader() {
		return header;
	}

	public boolean isDownConsole() {
		return tem != null;
	}

	public void deleteFile() {
		if (ipt != null) {
			try {
				ipt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ipt = null;
		}
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out = null;
		}
		try {
			if (tem != null)
				tem.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tem = null;
	}

	private void removeTemFile() {
		String warpath = PathUtil.getWarPath();
		String tempath = "temp";
		File doc = new File(warpath + tempath);
		long lastTime = System.currentTimeMillis() - timeout;
		if (doc.exists()) {
			File[] files = doc.listFiles();
			for (int i = files.length - 1; i >= 0; i--) {
				if (files[i].lastModified() < lastTime) {
					files[i].delete();
				}
			}
		}
	}

}
