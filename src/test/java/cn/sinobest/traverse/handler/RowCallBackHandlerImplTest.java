package cn.sinobest.traverse.handler;

import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SchemaerFactory;
import cn.sinobest.core.config.schema.SqlSchemaer;
import cn.sinobest.traverse.po.InsertParamObject;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext-testDb.xml" })
public class RowCallBackHandlerImplTest {

    @Autowired
    @Qualifier(value = "analyzerCallBackHandler")
    private RowCallBackHandlerImpl callBackHandler;

    @Test
    public void testAnalyzerRowMap4JUC() throws Exception {
        Schemaer schemaer = SchemaerFactory.getSchema("B_ASJ_ZAJ_RY");
        callBackHandler.initCallBackHandler(true,schemaer);
        final SqlSchemaer sqlSchemaer = schemaer.getFullSchemaer();
        final Map<String,String> rowMap = new HashMap<>();
        rowMap.put("zymc","ry");
        rowMap.put("rylx","2");
        rowMap.put("ajbh","A4401115100002014060005");
        rowMap.put("ajmc","6月9日演示专用案件05");
        rowMap.put("ajid","PCS44201406070000000002868450");
        rowMap.put("systemid","PCS44201406070000000002868456");
        rowMap.put("zjhm","410482197906264411");
        rowMap.put("lxfs","15891445687");
        ExecutorService es = Executors.newScheduledThreadPool(50);
        for (int i = 0; i < 999999; i++) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        Set<InsertParamObject> sets = callBackHandler.analyzerRowMap(rowMap, sqlSchemaer.getAnalyzer(), sqlSchemaer.getAnalyzerColumns());
                        for (InsertParamObject object:sets){
                            if(!validate(object.getParamMap(),rowMap)){
                                System.out.println("concurrent false!");
                            }
//                            System.out.println(validate(object.getParamMap(),rowMap));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        if(es!=null){
            es.shutdown();
            for (;!es.isTerminated(););
        }
    }

    public boolean validate(Map<String,String> paramMap,Map<String,String> rowMap){
        MapDifference mapDifference  = Maps.difference(paramMap,rowMap);
        Map map = mapDifference.entriesOnlyOnLeft();
        HashMap hashMap1 = new HashMap();
        hashMap1.put("bshid", "15891445687");
        hashMap1.put("bshlx", "003");
        HashMap hashMap2 = new HashMap();
        hashMap2.put("bshid", "15891445687");
        hashMap2.put("bshlx", "003");
        MapDifference mapDifference1 = Maps.difference(map, hashMap2);
        MapDifference mapDifference2 = Maps.difference(map,hashMap1);
        return ((mapDifference1.entriesOnlyOnLeft()!=null && mapDifference1.entriesOnlyOnLeft().size()==0) || mapDifference1.entriesOnlyOnLeft() == null) || ((mapDifference2.entriesOnlyOnLeft()!=null && mapDifference2.entriesOnlyOnLeft().size()==0) || mapDifference2.entriesOnlyOnLeft() == null);
    }
}