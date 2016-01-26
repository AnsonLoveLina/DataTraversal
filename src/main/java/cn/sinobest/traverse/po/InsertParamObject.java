package cn.sinobest.traverse.po;

import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.config.po.AnalyzerColumn;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 根据priimaryKey作为标示去hashcode和equals
 * 在每次equals是true并且hashcode相等的情况下，会将analyzerColumnNames合并
 * Created by zy-xx on 16/1/24.
 */
public class InsertParamObject implements IParamObject {
    private static final Log logger = LogFactory.getLog(InsertParamObject.class);
    private Map<String, String> paramMap = Maps.newHashMap();

    private Set<AnalyzerColumn> analyzerColumnNames = Sets.newHashSet();

    private String primaryKey = "";

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public InsertParamObject(Map<String, String> paramMap, AnalyzerColumn analyzerColumn) {
        this.paramMap = paramMap;
//        for (String value:paramMap.values()){
//            if (value!=null){
//                this.primaryKey += value;
//            }
//        }
        String bshid = paramMap.get(Param.BSHID.toString().toLowerCase());
        this.primaryKey += (bshid==null?"":bshid);
//        this.analyzerColumnNames.add(analyzerColumnName);
        mergeAnalyzerColumn(analyzerColumn);

        if ("".equals(primaryKey)){
            logger.error("error paramMap!");
        }
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public int hashCode() {
        return primaryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (!(obj instanceof InsertParamObject)) return false;
        InsertParamObject temp = (InsertParamObject)obj;
        boolean result = primaryKey.equals(temp.getPrimaryKey());
        if (result && this.hashCode()==obj.hashCode()){
            temp.mergeAnalyzerColumns(getAnalyzerColumnNames());
        }
        return result;
    }

    public Set<AnalyzerColumn> getAnalyzerColumnNames() {
        return analyzerColumnNames;
    }

    public void mergeAnalyzerColumns(Set<AnalyzerColumn> analyzerColumns){
        this.analyzerColumnNames.addAll(analyzerColumns);
    }

    private void mergeAnalyzerColumn(AnalyzerColumn analyzerColumn){
        this.analyzerColumnNames.add(analyzerColumn);
    }

    @Override
    public void mergeParamMap(Map<String,String> paramMap){
        paramMap.putAll(getParamMap());
        this.paramMap = paramMap;
    }

}
