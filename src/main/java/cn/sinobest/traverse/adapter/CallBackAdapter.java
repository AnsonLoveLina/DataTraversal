package cn.sinobest.traverse.adapter;

import cn.sinobest.traverse.analyzer.StringAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zy-xx on 16/1/21.
 */
@Component
public class CallBackAdapter {

    @Autowired
    private PreparedStatementCommiter commiter;

    public void processBiz(String insertSql, String endUpdateSql, List<HashMap<String, Object>> paramMaps) {

    }
}
