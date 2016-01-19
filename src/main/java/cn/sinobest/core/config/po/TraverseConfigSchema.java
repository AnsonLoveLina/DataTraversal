package cn.sinobest.core.config.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseConfigSchema {
    private final String[] columnStrs = {"JQMS","ZCXX","BLXX","AJID","ZYAQ","AJBH","AJMC"};
    private String detailQuery;
    private String fullQuery;
    private String schemaName;
    private String dbSource;
    private int detailConcurrentNum=0;
    private int fullConcurrentNum=0;
    private String timestampComment;
    private String timestampKey;
    private String detailEndUpdateSql;
    private String fullEndUpdateSql;
    private boolean concurrentNeedCheck;
    private String regexSql;
    private String noNumberRegexSql;
    private String resultTable;
    private List<String> analyzerColumns;
    private List<String> columns = new ArrayList<String>();
    private List<String> resultStrColumns;
    private List<ResultColumn> resultColumns = new ArrayList<ResultColumn>();
    private Map<String,String> specialExpress = new HashMap<String, String>();

    public TraverseConfigSchema(String detailQuery) {
        this.detailQuery = detailQuery;
    }

    public Map<String, String> getSpecialExpress() {
        return specialExpress;
    }

    public void setSpecialExpress(Map<String, String> specialExpress) {
        this.specialExpress = specialExpress;
    }

    public boolean isConcurrentNeedCheck() {
        return concurrentNeedCheck;
    }

    public void setConcurrentNeedCheck(boolean concurrentNeedCheck) {
        this.concurrentNeedCheck = concurrentNeedCheck;
    }

    public String getResultTable() {
        return resultTable;
    }

    public void setResultTable(String resultTable) {
        this.resultTable = resultTable;
    }

    public String getNoNumberRegexSql() {
        return noNumberRegexSql;
    }

    public void setNoNumberRegexSql(String noNumberRegexSql) {
        this.noNumberRegexSql = noNumberRegexSql;
    }

    public String getRegexSql() {
        return regexSql;
    }

    public void setRegexSql(String regexSql) {
        this.regexSql = regexSql;
    }

    public String getDetailEndUpdateSql() {
        return detailEndUpdateSql;
    }

    public void setDetailEndUpdateSql(String detailEndUpdateSql) {
        this.detailEndUpdateSql = detailEndUpdateSql;
    }

    public String getFullEndUpdateSql() {
        return fullEndUpdateSql;
    }

    public void setFullEndUpdateSql(String fullEndUpdateSql) {
        this.fullEndUpdateSql = fullEndUpdateSql;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<ResultColumn> getResultColumns() {
        return resultColumns;
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

    public List<String> getAnalyzerColumns() {
        return analyzerColumns;
    }

    public void setAnalyzerColumns(List<String> analyzerColumns) {
        this.analyzerColumns = analyzerColumns;
    }

    public int getDetailConcurrentNum() {
        return detailConcurrentNum;
    }

    public void setDetailConcurrentNum(int detailConcurrentNum) {
        this.detailConcurrentNum = detailConcurrentNum;
    }

    public int getFullConcurrentNum() {
        return fullConcurrentNum;
    }

    public void setFullConcurrentNum(int fullConcurrentNum) {
        this.fullConcurrentNum = fullConcurrentNum;
    }

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }


    public String getDetailQuery() {
        return detailQuery;
    }

    public void setDetailQuery(String detailQuery) {
        this.detailQuery = detailQuery;
    }

    public String getFullQuery() {
        return fullQuery;
    }

    public void setFullQuery(String fullQuery) {
        this.fullQuery = fullQuery;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
