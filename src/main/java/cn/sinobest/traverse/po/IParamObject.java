package cn.sinobest.traverse.po;

import cn.sinobest.core.config.po.AnalyzerColumn;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/25 0025.
 */
public interface IParamObject {
    public void mergeParamMap(Map<String, String> paramMap);
    public Set<AnalyzerColumn> getAnalyzerColumns();
}
