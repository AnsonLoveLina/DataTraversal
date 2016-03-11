package cn.sinobest.traverse.analyzer;

import cn.sinobest.core.config.po.AnalyzerColumn;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by zy-xx on 16/1/21.
 */
@Component
@Deprecated
public class StringAnalyzer {
    private static final Log logger = LogFactory.getLog(StringAnalyzer.class);

    public HashMap<String, Object> analyze(String columnStr, AnalyzerColumn analyzerColumn) {
        HashMap<String,Object> hm = Maps.newHashMap();
        hm.put("bshid","1");
        hm.put("bshlx","001");
        hm.put("bshid","2");
        hm.put("bshlx","002");
        return hm;
    }
}
