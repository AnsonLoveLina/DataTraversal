package cn.sinobest.core.common.util;

import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.po.InsertParamObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/27 0027.
 */
public class PrintUtil {

    public static void printParamObjectSet(Set<InsertParamObject> paramObjectSet){
        for (InsertParamObject paramObject:paramObjectSet){
            System.out.println("\nparamObject = " + paramObject);
            for (Map.Entry<String,String> entry:paramObject.getParamMap().entrySet()){
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.print("analyzerColumn ：");
            for (AnalyzerColumn analyzerColumn:paramObject.getAnalyzerColumns()){
                System.out.print(analyzerColumn.toString()+"，");
            }
            System.out.println("");
        }

    }
}
