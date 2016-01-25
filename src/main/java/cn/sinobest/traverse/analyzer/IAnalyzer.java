package cn.sinobest.traverse.analyzer;

import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.po.InsertParamObject;

import java.util.HashMap;
import java.util.Set;

/**
 * @Scope(value = "prototype")
 * sqlSchemaer:IAnalyzer = 1:1
 * Created by zy-xx on 16/1/24.
 */
public interface IAnalyzer {
    public Set<InsertParamObject> analyzerStr(String analyzerSource,AnalyzerColumn analyzerColumn);
}
