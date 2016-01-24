package cn.sinobest.core.config.schema;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by zy-xx on 16/1/24.
 */
public class SchemaerFactoryTest {

    @Test
    public void testGetSchema() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        applicationContext.start();
        Schemaer schemaer = SchemaerFactory.getSchema("B_ASJ_ZAJ_RY");
        SqlSchemaer sqlSchemaer = schemaer.getFullSchemaer();
        sqlSchemaer.getColumns();
        sqlSchemaer.getInsertSql();
        sqlSchemaer.getTraverseColumns();
        sqlSchemaer.getInsertColumns();
        sqlSchemaer.getEndUpdateSql();
        System.out.println("schemaer = " + schemaer);
    }
}