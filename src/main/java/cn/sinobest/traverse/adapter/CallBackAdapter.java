package cn.sinobest.traverse.adapter;

import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.analyzer.StringAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import oracle.sql.CLOB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.stereotype.Component;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/21.
 */
@Component
@Deprecated
public class CallBackAdapter {
    private static final Log logger = LogFactory.getLog(CallBackAdapter.class);

    @Autowired
    private StringAnalyzer analyzer;

    /**
     * 暂时不考虑JUC
     * @param dbMap
     * @param analyzerColumns
     * @return
     */
    public List<HashMap<String, Object>> analyze(HashMap<String, Object> dbMap, final Set<AnalyzerColumn> analyzerColumns) {
        HashMap<String,Object> noAnalyzerDbMap = Maps.newHashMap(Maps.<String, Object>filterKeys(dbMap, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                boolean isAnalyzer = analyzerColumns.contains(new AnalyzerColumn(s));
                return !isAnalyzer;
            }
        }));
        List<HashMap<String, Object>> paramMaps = new ArrayList<>();
        for (AnalyzerColumn analyzerColumn:analyzerColumns){
            Object columnValue = dbMap.get(analyzerColumn.toString());
            if (columnValue!=null){
                String columnStr = "";
                if(columnValue.getClass()==String.class){
                    columnStr = columnValue.toString();
                }else if(columnValue.getClass()==CLOB.class){
                    columnStr = SqlUtil.ClobToString((Clob) columnValue);
                }
                HashMap<String,Object> analyzerResults = analyzer.analyze(columnStr, analyzerColumn);
                if (!analyzerResults.isEmpty()){
                    noAnalyzerDbMap.putAll(analyzerResults);
                    paramMaps.add(noAnalyzerDbMap);
                }
//                Maps.
            }
        }
        return paramMaps;
    }

    public void processResult(ParsedSql processResultSql, List<HashMap<String, Object>> paramMaps, PreparedStatementCommiter commiter) throws Exception {
        PreparedStatement ps = commiter.getPreparedStatement();
//            String insertSqlNew = NamedParameterUtils.substituteNamedParameters(SqlUtil.getParsedSql(insertSqlSource), new MapSqlParameterSource(paramMapTemplate));

        for (HashMap<String,Object> paramMap:paramMaps){
            Object[] params = NamedParameterUtils.buildValueArray(processResultSql, new MapSqlParameterSource(paramMap), (List) null);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.addBatch();
//            commiter.addBatch(processResultSql);
        }
    }
}
