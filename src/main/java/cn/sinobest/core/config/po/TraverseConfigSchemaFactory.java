package cn.sinobest.core.config.po;

import cn.sinobest.core.common.init.WebResourceLoaderAware;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseConfigSchemaFactory    {
    private static final Log logger = LogFactory.getLog(TraverseConfigSchemaFactory.class);
    private static ImmutableMap<String, TraverseConfigSchema> schemas = ImmutableMap.of();

    static {

        try {

            Resource resource = WebResourceLoaderAware.getLoader().getResource("classpath:traverseConfig.xml");
            InputStream is = resource.getInputStream();
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
        TraverseConfigSchema schema = schemas.get(serverName);
        if (!schema.isFitted()){
            schema.fit();
        }
        logger.trace("schema装配完成！");
        return schema;
    }
}
