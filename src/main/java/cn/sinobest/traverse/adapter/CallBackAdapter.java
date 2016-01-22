package cn.sinobest.traverse.adapter;

import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.analyzer.StringAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import oracle.sql.CLOB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by zy-xx on 16/1/21.
 */
@Component
public class CallBackAdapter {
    private static final Log logger = LogFactory.getLog(CallBackAdapter.class);

    @Autowired
    private PreparedStatementCommiter commiter;

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
                return analyzerColumns.contains(s.toLowerCase());
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
                noAnalyzerDbMap.putAll(analyzerResults);
                paramMaps.add(noAnalyzerDbMap);
//                Maps.
            }
        }
        return paramMaps;
    }

    //暂时先不加对于schemaName的锁
    public void processBiz(ParsedSql insertSql,ParsedSql endUpdateSql,List<HashMap<String,Object>> paramMap) {
        Preconditions.checkNotNull(insertSql);
        try {
            processResult(insertSql, paramMap);
            if (endUpdateSql!=null){
                processResult(endUpdateSql, paramMap);
            }
        } catch (Exception e) {
            logger.error("结果语句preparedStatement！",e);
        }

    }

    private void processResult(ParsedSql processResultSql, List<HashMap<String,Object>> paramMaps) throws Exception {
        PreparedStatement ps = commiter.getPrepareStatement(processResultSql);

        for (HashMap<String,Object> paramMap:paramMaps){
            Object[] params = NamedParameterUtils.buildValueArray(processResultSql, new MapSqlParameterSource(paramMap), (List) null);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.addBatch();
            commiter.addBatch(processResultSql);
        }
    }
}
