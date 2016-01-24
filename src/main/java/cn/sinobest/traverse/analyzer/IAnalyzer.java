package cn.sinobest.traverse.analyzer;

import cn.sinobest.traverse.po.InsertResultObject;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/24.
 */
public interface IAnalyzer {
    public Set<InsertResultObject> analyzer(String analyzerSource,HashMap<String,Object> paramMap);
}
