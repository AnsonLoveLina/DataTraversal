package cn.sinobest.traverse.analyzer;

import cn.sinobest.core.config.schema.SqlSchemaer;
import cn.sinobest.traverse.po.InsertResultObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/24.
 * 每个SqlSchemaer有自己独立的Analyzer
 */
@Component(value = "regularAnalyzer")
@Lazy(value = true)
public class RegularAnalyzer implements IAnalyzer {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private SqlSchemaer sqlSchemaer;

    public RegularAnalyzer(SqlSchemaer sqlSchemaer) {
        this.sqlSchemaer = sqlSchemaer;
    }

    @Override
    public Set<InsertResultObject> analyzer(String analyzerSource, HashMap<String, Object> paramMap) {

        return null;
    }

}
