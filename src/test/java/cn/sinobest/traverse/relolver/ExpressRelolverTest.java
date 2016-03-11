package cn.sinobest.traverse.relolver;

import cn.sinobest.core.common.util.PrintUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.po.InsertParamObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;

/**
 * Created by zy-xx on 16/1/26.
 */
public class ExpressRelolverTest {

    @Test
    public void testExplain() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        applicationContext.start();
        HashMap<String,String> rowMap = Maps.newHashMap();
        rowMap.put("systemid","23423");
        rowMap.put("ajid","pcs234234234");
        rowMap.put("ajmc","mcsdfds");
        rowMap.put("ajbh","234234");
        rowMap.put("bshlx","003");
        rowMap.put("bshid", "430102198704020515");
        rowMap.put("zjhm","43010219870402051x");
        rowMap.put("lxfs","周毅身份证： 430102198704020515 qq号码：326236882 还有一些其他43010219870402051X信息？");
        ExpressRelolver relolver = new ExpressRelolver();
        AnalyzerColumn analyzerColumn = new AnalyzerColumn("lxfs");
        analyzerColumn.setSpecialExpress("get('bshlx')=='003' && get('ajbh')=='234234'/put('rylx','05')");
        InsertParamObject paramObject = new InsertParamObject(rowMap,analyzerColumn);
        relolver.explain(paramObject);
        PrintUtil.printParamObjectSet(Sets.newHashSet(paramObject));
    }
}