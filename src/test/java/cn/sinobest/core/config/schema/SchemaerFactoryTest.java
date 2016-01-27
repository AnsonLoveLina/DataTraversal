package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.util.PrintUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.handler.RowAnalyzerCallBackHandlerImpl;
import cn.sinobest.traverse.po.InsertParamObject;
import com.google.common.collect.Maps;
import jodd.util.StringUtil;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
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
            Set<InsertParamObject> paramObjectSet = analyzer.analyzerStr("周毅身份证： 430102198704020515 qq号码：326236882 还有一些其他信息？", new AnalyzerColumn("lxfs"));
            for (InsertParamObject paramObject:paramObjectSet){
                System.out.println("paramObject = " + paramObject);
            }
        }
    }

    @Test
    public void testCallBackHandler() throws Exception{
        HashMap<String,String> rowMap = Maps.newHashMap();
        rowMap.put("systemid","23423");
        rowMap.put("ajid","pcs234234234");
        rowMap.put("ajmc","mcsdfds");
        rowMap.put("ajbh","234234");
        rowMap.put("zjhm","43010219870402051x");
        rowMap.put("lxfs","2014年6月10日，杨少婷报案，遭入室盗窃笔记本电脑一台，现金8000多元");
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testDb.xml");
        applicationContext.start();
        Schemaer schemaer = SchemaerFactory.getSchema("B_ASJ_ZAJ_RY");
        SqlSchemaer sqlSchemaer = schemaer.getFullSchemaer();
        RowAnalyzerCallBackHandlerImpl callBackHandler = (RowAnalyzerCallBackHandlerImpl) applicationContext.getBean("analyzerCallBackHandler", schemaer);
        callBackHandler.analyzerRowMap(rowMap,sqlSchemaer.getAnalyzer(),sqlSchemaer.getAnalyzerColumns());
    }

//    @Test
    public void testGetSchema() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        applicationContext.start();
        Schemaer schemaer = SchemaerFactory.getSchema("B_ASJ_ZAJ_RY");
        RowAnalyzerCallBackHandlerImpl callBackHandler = (RowAnalyzerCallBackHandlerImpl) applicationContext.getBean("analyzerCallBackHandler", schemaer);
        SqlSchemaer sqlSchemaer = schemaer.getFullSchemaer();
        sqlSchemaer.getColumns();
        sqlSchemaer.getInsertSql();
        sqlSchemaer.getTraverseColumns();
        sqlSchemaer.getInsertColumns();
        sqlSchemaer.getEndUpdateSql();
        IAnalyzer analyzer = (IAnalyzer) applicationContext.getBean("regularAnalyzer",sqlSchemaer);
        Set<InsertParamObject> paramObjectSet = analyzer.analyzerStr("周毅身份证： 430102198704020515 qq号码：326236882 还有一些其他43010219870402051X信息？", new AnalyzerColumn("ZJHM"));
        PrintUtil.printParamObjectSet(paramObjectSet);
//        Executor executor = Executors.newCachedThreadPool();
//        for (int i=0;i<999999;i++){
//            TaskRun taskRun = new TaskRun(analyzer);
//            executor.execute(taskRun);
//        }
    }
}