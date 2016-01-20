package cn.sinobest.core.config.po;

import cn.sinobest.core.common.util.SpringContextInit;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseConfigSchemaFactory    {
    private static final Log logger = LogFactory.getLog(TraverseConfigSchemaFactory.class);
    private static ImmutableMap<String, TraverseConfigSchema> schemas = ImmutableMap.of();

    static {

        try {

            InputStream is =SpringContextInit.getResource("classpath:traverseConfig.xml");
            logger.trace("成功获取资源！");

            JAXBContext jaxbContext = JAXBContext.newInstance(TraverseConfigSchemas.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TraverseConfigSchemas schemasXmlObj = (TraverseConfigSchemas) jaxbUnmarshaller.unmarshal(is);
            logger.trace("成功装载资源！");

            schemas = Maps.uniqueIndex(schemasXmlObj.getData().iterator(), new Function<TraverseConfigSchema, String>() {
                @Override
                public String apply(TraverseConfigSchema traverseConfigSchema) {
                    return traverseConfigSchema.getSchemaName();
                }
            });
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public static TraverseConfigSchema getSchema(String serverName) {
        return schemas.get(serverName);
    }
}
