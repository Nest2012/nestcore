
package org.nest.core.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nest.core.exception.NestException;
import org.nest.mvp.component.ComponentService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;





public class BeanUtil implements ApplicationContextAware{

    /**
     * @Fields ctx : Spring的应用程序上下文
     */
    protected static ApplicationContext ctx = null; // IfmisInitClasspathXmlApplicationContext.getThis();

    
    public static Object deepClone(Object obj) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrOs = new ByteArrayOutputStream();
        ObjectOutputStream objOs = new ObjectOutputStream(byteArrOs);
        objOs.writeObject(obj);

        ByteArrayInputStream byteArrIs = new ByteArrayInputStream(byteArrOs.toByteArray());
        ObjectInputStream objIs = new ObjectInputStream(byteArrIs);
        Object deepCopy = objIs.readObject();
        return deepCopy;
    }

    public static byte[] readObj2BateArray(Object obj) throws NestException {
        try {
            ByteArrayOutputStream byteArrOs = new ByteArrayOutputStream();
            ObjectOutputStream objOs = new ObjectOutputStream(byteArrOs);
            objOs.writeObject(obj);
            return byteArrOs.toByteArray();
        } catch (Exception e) {
            throw new NestException(e);
        }
    }

    public static Object cloneByBateArray(byte[] array) throws NestException {
        try {
            ByteArrayInputStream byteArrIs = new ByteArrayInputStream(array);
            ObjectInputStream objIs = new ObjectInputStream(byteArrIs);
            Object deepCopy = objIs.readObject();
            return deepCopy;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new NestException(e);
        } catch (Exception e) {
            throw new NestException(e);
        }

    }

    /**
     * @Description:
     * @param beanid D
     * @return Object
     */
    public static Object getBean(final String beanid) {
        return BeanUtil.ctx.getBean(beanid);
    }

    /**
     * 根据当前数据源的数据库获取实体对象 要求spring的beanid写成"oracle.beanid"，"mysql.beanid"
     * @param beanid
     * @return
     * @throws
     */
    public static Object getBean4DB(final String beanid) {
        return getBean("oracle." + beanid);
    }


    public static boolean containsBean(String beanName) {
        if (ctx == null)
            return false;
        return ctx.containsBean(beanName);
    }


    public static ComponentService getComponentBean(String beanName) {
        if (ctx == null)
            return null;
        ComponentService cmp = (ComponentService) ctx.getBean(beanName);
        return cmp;
    }

    
    
    @Override
    public void setApplicationContext(ApplicationContext ctx)
            throws BeansException {
        BeanUtil.ctx = ctx; 
    }
}
