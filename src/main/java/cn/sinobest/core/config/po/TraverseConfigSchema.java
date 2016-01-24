package cn.sinobest.core.config.po;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.common.jaxb.XmlObject;
import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.traverse.adapter.CallBackAdapter;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.sql.DataSource;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 针对schema的这些扩展字段，扩展adapter和扩展字段以及adapter的初始化如果有空需要用一个proxy进行封装
 */
@Deprecated
public class TraverseConfigSchema extends Data {
    private static final Log logger = LogFactory.getLog(TraverseConfigSchema.class);
    private Set<String> columns = new HashSet<String>();

    private Set<String> insertColumns = null;

    private ParsedSql insertSql;

    private ParsedSql detailUpdateSql;

    private ParsedSql fullUpdateSql;

    //放这里确实不太好，原因是会减少内存，姑且把这个类看成一个schemaer
    private HashMap<ParsedSql,PreparedStatementCommiter> commiters = new HashMap<ParsedSql,PreparedStatementCommiter>();

    public PreparedStatementCommiter getCommiter(ParsedSql sql) {
        if (commiters.isEmpty()){

            commiters.put(getInsertSql(), (PreparedStatementCommiter) SpringContextInit.getBeanByAware("commiter",(DataSource)SpringContextInit.getBeanByAware("dataSource"),SqlUtil.getSubstituteNamedParameters(getInsertSql())));

        }
        return commiters.get(sql);
    }

    //在set的时候做封装columns这件事是因为getInsertSql要用到
    @Override
    public void setFullQuery(FullQuery fullQuery){
        this.columns.addAll(SqlUtil.getColumns(fullQuery.toString()));

        this.columns.add(Param.BSHID.toString().toLowerCase());
        this.columns.add(Param.BSHLX.toString().toLowerCase());
        super.setFullQuery(fullQuery);
    }

    @Override
    public void setDetailQuery(DetailQuery detailQuery) {
        this.columns.addAll(SqlUtil.getColumns(detailQuery.toString()));

        this.columns.add(Param.BSHID.toString().toLowerCase());
        this.columns.add(Param.BSHLX.toString().toLowerCase());
        super.setDetailQuery(detailQuery);
    }

    public ParsedSql getInsertSql() {
        if (insertSql == null && !getColumns().isEmpty()){
            Set<String> resultStrColumns = Sets.newHashSet(getColumns());
            if(!getAnalyzerColumns().isEmpty()){
                resultStrColumns.removeAll(getAnalyzerColumns());
            }
            resultStrColumns.remove("SYSTEMID");
//
//            Map<String, Object> paramMapTemplate = Maps.newHashMap();
//
//            try {
//                paramMapTemplate = SqlUtil.getParamTemplate(columns);
//            } catch (Exception e) {
//                logger.error("结果字段集合为空！适配器初始化出错！",e);
//            }

            String insertSqlSource = SqlUtil.getInsertSql(getResultTable(),resultStrColumns);
//            String insertSqlNew = NamedParameterUtils.substituteNamedParameters(SqlUtil.getParsedSql(insertSqlSource), new MapSqlParameterSource(paramMapTemplate));
            this.insertSql = SqlUtil.getParsedSql(insertSqlSource);
        }
        return insertSql;
    }

    public ParsedSql getDetailUpdateSql() {
        if (detailUpdateSql == null){
            detailUpdateSql = SqlUtil.getParsedSql(getDetailQuery().getDetailEndUpdateSql());
        }
        return detailUpdateSql;
    }

    public ParsedSql getFullUpdateSql() {
        if (fullUpdateSql == null){
            fullUpdateSql = SqlUtil.getParsedSql(getFullQuery().getFullEndUpdateSql());
        }
        return fullUpdateSql;
    }

    public Set<String> getColumns() {
        return columns;
    }

}
