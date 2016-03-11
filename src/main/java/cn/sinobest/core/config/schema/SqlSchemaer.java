package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.core.config.po.ITraverseQuery;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import jodd.util.StringUtil;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by zy-xx on 16/1/24.
 * 每个语句一个sqlSchemaer
 * ITraverseQuery:SqlSchemaer = 1:1
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
    private List<AnalyzerColumn> analyzerColumns = new ArrayList<AnalyzerColumn>();
    private Set<String> analyzerResultColumns = new HashSet<String>();

    private Set<String> columns = new HashSet<String>();
    private Set<String> traverseColumns = new HashSet<String>();
    private Set<String> insertColumns = new HashSet<String>();

    private ResultSql insertSql;
    private ResultSql endUpdateSql;

    private IAnalyzer analyzer;

    @Override
    public String toString() {
        return schemaName;
    }

    public void parseAndSetOtherVar(){
        if (this.columns.isEmpty()){
            this.columns.addAll(SqlUtil.getColumns(getTraverseQuery().toString()));

            this.columns.addAll(getAnalyzerResultColumns());
        }
        if (insertSql == null && !getInsertColumns().isEmpty()){
            String insertSqlSource = SqlUtil.getInsertSql(getResultTable(),getInsertColumns());
            this.insertSql = new ResultSql(insertSqlSource);
        }
        if (endUpdateSql == null && StringUtil.isNotBlank(getTraverseQuery().getEndUpdateSql())){
            endUpdateSql = new ResultSql(getTraverseQuery().getEndUpdateSql());
        }
        if (traverseColumns.isEmpty()){
            traverseColumns.addAll(getColumns());
            traverseColumns.removeAll(getAnalyzerResultColumns());
        }
        if (insertColumns.isEmpty()){
            insertColumns.addAll(getColumns());
            if(!getAnalyzerColumns().isEmpty()){
                insertColumns.removeAll(getAnalyzerColumns());
            }
            insertColumns.remove(primaryKeyName);
        }
        if (analyzer==null){
            analyzer = (IAnalyzer) SpringContextInit.getBeanByAware("regularAnalyzer",this);
        }
    }

    public IAnalyzer getAnalyzer() {
        return analyzer;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public ResultSql getInsertSql() {
        return insertSql;
    }

    public ResultSql getEndUpdateSql() {
        return endUpdateSql;
    }

    public Set<String> getTraverseColumns(){
        return traverseColumns;
    }

    public Set<String> getInsertColumns() {
        return insertColumns;
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

    public List<AnalyzerColumn> getAnalyzerColumns() {
        return analyzerColumns;
    }

    public void setAnalyzerColumns(List<AnalyzerColumn> analyzerColumns) {
        this.analyzerColumns = analyzerColumns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public void setInsertColumns(Set<String> insertColumns) {
        this.insertColumns = insertColumns;
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
