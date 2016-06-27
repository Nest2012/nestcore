package org.nest.core.bean;

public class BeanUtil {
	private static IBeanUtil ibu = null;

	public IBeanUtil getIbu() {
		return ibu;
	}

	public void setIbu(IBeanUtil ibu) {
		BeanUtil.ibu = ibu;
	}

	public static Object getBean(String id) {
		if(true){return null;}
		return ibu.getBean(id);
	}

}
