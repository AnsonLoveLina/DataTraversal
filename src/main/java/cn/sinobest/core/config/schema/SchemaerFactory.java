package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.init.WebResourceLoaderAware;
import cn.sinobest.core.config.po.Data;
import cn.sinobest.core.config.po.Datas;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import jodd.bean.BeanTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/24.
 */
public class SchemaerFactory {

    private static final Log logger = LogFactory.getLog(SchemaerFactory.class);
    private static Map<String, Schemaer> schemas = Maps.newHashMap();

    static {

        try {

            Resource resource = WebResourceLoaderAware.getLoader().getResource("classpath:traverseConfig.xml");
            InputStream is = resource.getInputStream();
            logger.trace("成功获取资源！");

            JAXBContext jaxbContext = JAXBContext.newInstance(Datas.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Datas datas = (Datas) jaxbUnmarshaller.unmarshal(is);
            logger.trace("成功装载资源！");

            for (Data data:datas.getData()){
                Schemaer schemaer = new Schemaer(data);
                schemas.put(data.getSchemaName(),schemaer);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public static Schemaer getSchema(String serverName) {
        Schemaer schemaer = schemas.get(serverName);
        return schemaer;
    }
}
