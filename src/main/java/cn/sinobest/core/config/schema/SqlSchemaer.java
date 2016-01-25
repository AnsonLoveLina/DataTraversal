package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.core.config.po.ITraverseQuery;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.analyzer.RegularAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zy-xx on 16/1/24.
 * 每个语句一个sqlSchemaer
 */
public class SqlSchemaer {
    public final String primaryKeyName = "SYSTEMID";
    private String schemaName;
    private ITraverseQuery traverseQuery;
    private String timestampComment;
    private String timestampKey;
    private String regexSql;
    private String noNumberRegexSql;
    private String resultTable;
    private Set<AnalyzerColumn> analyzerColumns = new HashSet<AnalyzerColumn>();
    private Set<String> analyzerResultColumns = new HashSet<String>();

    private Set<String> columns = new HashSet<String>();
    private Set<String> traverseColumns = new HashSet<String>();
    private Set<String> insertColumns = new HashSet<String>();
    private ParsedSql insertSql;
    private ParsedSql endUpdateSql;

    private HashMap<ParsedSql,PreparedStatementCommiter> commiters = new HashMap<ParsedSql,PreparedStatementCommiter>();

    private IAnalyzer analyzer;

    public PreparedStatementCommiter getCommiter(ParsedSql sql) {
        if (commiters.isEmpty()){
            DataSource ds = (DataSource)SpringContextInit.getBeanByAware("dataSource");
            PreparedStatementCommiter commiter =(PreparedStatementCommiter) SpringContextInit.getBeanByAware("commiter",ds,SqlUtil.getSubstituteNamedParameters(getInsertSql()));
            commiters.put(getInsertSql(), commiter);
        }
        return commiters.get(sql);
    }

    @Override
    public String toString() {
        return schemaName;
    }

    public IAnalyzer getAnalyzer() {
        if (analyzer==null){
            analyzer = (IAnalyzer) SpringContextInit.getBeanByAware("regularAnalyzer",this);
        }
        return analyzer;
    }

    public Set<String> getColumns() {
        if (this.columns.isEmpty()){
            this.columns.addAll(SqlUtil.getColumns(getTraverseQuery().toString()));

            this.columns.addAll(getAnalyzerResultColumns());
        }
        return columns;
    }

    public ParsedSql getInsertSql() {
        if (insertSql == null && !getInsertColumns().isEmpty()){
            String insertSqlSource = SqlUtil.getInsertSql(getResultTable(),getInsertColumns());
            this.insertSql = SqlUtil.getParsedSql(insertSqlSource);
        }
        return insertSql;
    }

    public Set<String> getTraverseColumns(){
        if (traverseColumns.isEmpty()){
            traverseColumns.addAll(getColumns());
            traverseColumns.removeAll(getAnalyzerResultColumns());
        }
        return traverseColumns;
    }

    public Set<String> getInsertColumns() {
        if (insertColumns.isEmpty()){
            insertColumns.addAll(getColumns());
            if(!getAnalyzerColumns().isEmpty()){
                insertColumns.removeAll(getAnalyzerColumns());
            }
            insertColumns.remove(primaryKeyName);
        }
        return insertColumns;
    }

    public ParsedSql getEndUpdateSql() {
        if (endUpdateSql == null && !"".equals(getTraverseQuery().getEndUpdateSql())){
            endUpdateSql = SqlUtil.getParsedSql(getTraverseQuery().getEndUpdateSql());
        }
        return endUpdateSql;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public ITraverseQuery getTraverseQuery() {
        return traverseQuery;
    }

    public void setTraverseQuery(ITraverseQuery traverseQuery) {
        this.traverseQuery = traverseQuery;
    }

    public String getTimestampComment() {
        return timestampComment;
    }

    public void setTimestampComment(String timestampComment) {
        this.timestampComment = timestampComment;
    }

    public String getTimestampKey() {
        return timestampKey;
    }

    public void setTimestampKey(String timestampKey) {
        this.timestampKey = timestampKey;
    }

    public String getRegexSql() {
        return regexSql;
    }

    public void setRegexSql(String regexSql) {
        this.regexSql = regexSql;
    }

    public String getNoNumberRegexSql() {
        return noNumberRegexSql;
    }

    public void setNoNumberRegexSql(String noNumberRegexSql) {
        this.noNumberRegexSql = noNumberRegexSql;
    }

    public String getResultTable() {
        return resultTable;
    }

    public void setResultTable(String resultTable) {
        this.resultTable = resultTable;
    }

    public Set<AnalyzerColumn> getAnalyzerColumns() {
        return analyzerColumns;
    }

    public void setAnalyzerColumns(Set<AnalyzerColumn> analyzerColumns) {
        this.analyzerColumns = analyzerColumns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public void setInsertColumns(Set<String> insertColumns) {
        this.insertColumns = insertColumns;
    }

    public void setInsertSql(ParsedSql insertSql) {
        this.insertSql = insertSql;
    }

    public void setEndUpdateSql(ParsedSql endUpdateSql) {
        this.endUpdateSql = endUpdateSql;
    }

    public HashMap<ParsedSql, PreparedStatementCommiter> getCommiters() {
        return commiters;
    }

    public void setCommiters(HashMap<ParsedSql, PreparedStatementCommiter> commiters) {
        this.commiters = commiters;
    }

    public void setAnalyzer(IAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Set<String> getAnalyzerResultColumns() {
        return analyzerResultColumns;
    }

    public void setAnalyzerResultColumns(Set<String> analyzerResultColumns) {
        this.analyzerResultColumns = analyzerResultColumns;
    }
}
