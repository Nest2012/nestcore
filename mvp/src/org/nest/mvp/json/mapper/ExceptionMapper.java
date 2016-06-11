package org.nest.mvp.json.mapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.nest.mvp.json.RPCException;
import org.nest.mvp.json.protocol.ProtocolObject;

public class ExceptionMapper implements Mapper {

	public Object writeObject(Class clazz, ProtocolObject subclassType,Object value) throws RPCException {
		String s = "";
		try {
			s = new String(new sun.misc.BASE64Decoder().decodeBuffer(value.toString()));
		} catch (IOException e) {
			throw new RPCException(e);
		}
		return new Exception(s);
	}

	public void readObjectStructure(Class clazz, Object object,
			ObjectReader reader) throws IOException {
	    Throwable e = (Throwable) object;
	    
	 // 如果是反射方法产生的异常则将真实异常解析出来
        if(InvocationTargetException.class.isAssignableFrom(object.getClass())){
            e = ((InvocationTargetException)object).getTargetException();
        }
	    
		reader.writeStructure('"');
		reader.writeStructure(e.getClass().getName());
		reader.writeStructure("\":{}");
	}

	public void readObjectValue(Object object, ObjectReader reader)
			throws IOException {

		Throwable e = (Throwable) object;
		
		// 如果是反射方法产生的异常则将真实异常解析出来
		if(InvocationTargetException.class.isAssignableFrom(object.getClass())){
			e = ((InvocationTargetException)object).getTargetException();
		}
		
//		e.getStackTrace();
//		StringWriter w = new StringWriter();
//		PrintWriter out = new PrintWriter(w);
//		e.printStackTrace(out);

		// 由于异常信息中的格式比较复杂，因此这里将
		String s = new sun.misc.BASE64Encoder().encode(e.getMessage().getBytes());
//		 reader.writeValue("\""+w.toString().replaceAll("\r","\\\\r").replaceAll("\n", "\\\\n").replaceAll("'", "\'")+"\"");
		reader.writeValue("\"" + s + "\"");
	}
}
