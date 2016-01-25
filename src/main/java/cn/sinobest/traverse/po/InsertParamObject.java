package cn.sinobest.traverse.po;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/24.
 */
public class InsertParamObject implements IParamObject {
    private static final Log logger = LogFactory.getLog(InsertParamObject.class);
    private Map<String, String> paramMap = new HashMap<String,String>();

    private String analyzerColumnName;

    private String primaryKey = "";

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public InsertParamObject(Map<String, String> paramMap, String analyzerColumnName) {
        this.paramMap = paramMap;
        for (String value:paramMap.values()){
            if (value!=null){
                this.primaryKey += value;
            }
        }
        this.analyzerColumnName = analyzerColumnName;

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
        return primaryKey.equals(temp.getPrimaryKey());
    }

    @Override
    public void mergeParamMap(Map<String,String> paramMap){
        paramMap.putAll(getParamMap());
        this.paramMap = paramMap;
    }

}
