package cn.sinobest.core.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhouyi1 on 2016/1/20 0020.
 */
public class SpringContextInit implements ServletContextListener {
    private static WebApplicationContext applicationContext;
    //private static ApplicationContext appcContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        //appcContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    public static InputStream getResource(String resourcePath) throws IOException {
        Resource resource = applicationContext.getResource(resourcePath);
        return resource.getInputStream();
    }

    public static Object getBeanByAware(String beanName){
        return applicationContext.getBean(beanName);
    }
    public static Object getBeanByAware(String beanName,Object... args){
        return applicationContext.getBean(beanName,args);
    }
    public static ApplicationContext getContext() {
        return applicationContext;
    }
}

