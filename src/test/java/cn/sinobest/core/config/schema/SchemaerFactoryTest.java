package cn.sinobest.core.config.schema;

import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.po.InsertParamObject;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by zy-xx on 16/1/24.
 */
public class SchemaerFactoryTest {

    class TaskRun implements Runnable{
        private IAnalyzer analyzer;

        public TaskRun(IAnalyzer analyzer) {
            this.analyzer = analyzer;
        }

        @Override
        public void run() {
            Set<InsertParamObject> paramObjectSet = analyzer.analyzerStr("", new AnalyzerColumn("lxfs"));
            for (InsertParamObject paramObject:paramObjectSet){
                System.out.println("paramObject = " + paramObject);
            }
        }
    }

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
        IAnalyzer analyzer = (IAnalyzer) applicationContext.getBean("regularAnalyzer",sqlSchemaer);
        Executor executor = Executors.newCachedThreadPool();
        for (int i=0;i<999999;i++){
            TaskRun taskRun = new TaskRun(analyzer);
            executor.execute(taskRun);
        }
        System.out.println("schemaer = " + schemaer);
    }
}