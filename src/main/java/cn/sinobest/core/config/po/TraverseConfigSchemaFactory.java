package cn.sinobest.core.config.po;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseConfigSchemaFactory    {
    private static HashMap<String,TraverseConfigSchema> schemas = new HashMap<String,TraverseConfigSchema>();

    public static TraverseConfigSchema getSchema(String serverName) {
        return schemas.get(serverName);
    }
}
