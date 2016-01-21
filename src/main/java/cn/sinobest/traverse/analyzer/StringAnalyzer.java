package cn.sinobest.traverse.analyzer;

import cn.sinobest.core.config.po.AnalyzerColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/21.
 */
@Component
public class StringAnalyzer {
    private static final Log logger = LogFactory.getLog(StringAnalyzer.class);

    public List<HashMap<String, Object>> analyze(HashMap<String, Object> dbMap, Set<AnalyzerColumn> analyzerColumns) {
        return null;
    }
}
