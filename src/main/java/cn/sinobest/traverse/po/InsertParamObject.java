package cn.sinobest.traverse.po;

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
    private HashMap<String, String> paramMap = new HashMap<String,String>();

    private String analyzerColumnName;

    private String primaryKey;

    public HashMap<String, String> getParamMap() {
        return paramMap;
    }

    public InsertParamObject(HashMap<String, String> paramMap, String analyzerColumnName) {
        this.paramMap = paramMap;
        for (String value:paramMap.values()){
            if (value!=null){
                this.primaryKey += value.hashCode();
            }
        }
        this.analyzerColumnName = analyzerColumnName;

        if (primaryKey==null){
            primaryKey = this.toString();
            logger.error("error paramMap!");
        }
    }

    @Override
    public int hashCode() {
        return primaryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return primaryKey.equals(obj);
    }

    @Override
    public void mergeParamMap(HashMap<String,String> paramMap){

    }

}
