package cn.sinobest.core.config.po;

import cn.sinobest.core.common.jaxb.XmlObject;
import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@XmlAccessorType(value = XmlAccessType.NONE)
public class TraverseConfigSchema {
    private final String[] defaultColumnStrs = {"JQMS","ZCXX","BLXX","AJID","ZYAQ","AJBH","AJMC"};
    @XmlElement(name = "detailQuery",required = true)
    private DetailQuery detailQuery;
    @XmlElement(name = "fullQuery",required = true)
    private FullQuery fullQuery;
    @XmlAttribute(name = "schemaName",required = true)
    private String schemaName;
    private String dbSource;
//    @XmlElement(name = "detailQuery")
//    @XmlAttribute(name = "concurrentNum")
//    private int detailConcurrentNum=0;
//    @XmlElement(name = "fullQuery")
//    @XmlAttribute(name = "concurrentNum")
//    private int fullConcurrentNum=0;
    @XmlAttribute(name = "timestampComment",required = true)
    private String timestampComment;
    @XmlAttribute(name = "timestampKey",required = true)
    private String timestampKey;
//    @XmlElement(name = "detailQuery")
//    @XmlAttribute(name = "endUpdateSql")
//    private String detailEndUpdateSql;
//    @XmlElement(name = "fullQuery")
//    @XmlAttribute(name = "endUpdateSql")
//    private String fullEndUpdateSql;
    @XmlAttribute(name = "concurrentNeedCheck")
    private boolean concurrentNeedCheck;
    @XmlAttribute(name = "regexSql")
    private String regexSql;
    @XmlAttribute(name = "noNumRegexSql")
    private String noNumberRegexSql;
    @XmlAttribute(name = "resultTable")
    private String resultTable;
    @XmlElement(name = "analyzerColumn")
    private Set<AnalyzerColumn> analyzerColumns = new HashSet<AnalyzerColumn>();
    private Set<String> columns = new HashSet<String>();
    private Set<String> resultStrColumns = new HashSet<String>();

    private String insertSql;
//    private Set<ResultColumn> resultColumns = new HashSet<ResultColumn>();

    public TraverseConfigSchema() {

    }

    public boolean isFitted(){
        return columns.isEmpty();
    }

    public void fit(){
        Set<String> columnTemps = SqlUtil.processColumns(detailQuery.toString());
        columnTemps.addAll(SqlUtil.processColumns(fullQuery.toString()));
        //如果是select*这种获取不了字段，就用默认
        if (columnTemps.isEmpty()){
            columnTemps.addAll(Arrays.asList(defaultColumnStrs));
        }

        this.columns = columnTemps;

        resultStrColumns = columnTemps;
        if(!analyzerColumns.isEmpty())
            resultStrColumns.removeAll(analyzerColumns);
        resultStrColumns.remove("SYSTEMID");
        resultStrColumns.add(Param.BSHID.toString());
        resultStrColumns.add(Param.BSHLX.toString());


        this.insertSql = SqlUtil.getInsertSql(resultTable,resultStrColumns);
    }

    public Set<AnalyzerColumn> getAnalyzerColumns() {
        return analyzerColumns;
    }

    public void setAnalyzerColumns(Set<AnalyzerColumn> analyzerColumns) {
        this.analyzerColumns = analyzerColumns;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public Set<String> getResultStrColumns() {
        return resultStrColumns;
    }

//    public Set<ResultColumn> getResultColumns() {
//        return resultColumns;
//    }
//
//    public void setResultColumns(Set<ResultColumn> resultColumns) {
//        this.resultColumns = resultColumns;
//    }

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

//    public String getDetailEndUpdateSql() {
//        return detailEndUpdateSql;
//    }
//
//    public void setDetailEndUpdateSql(String detailEndUpdateSql) {
//        this.detailEndUpdateSql = detailEndUpdateSql;
//    }
//
//    public String getFullEndUpdateSql() {
//        return fullEndUpdateSql;
//    }
//
//    public void setFullEndUpdateSql(String fullEndUpdateSql) {
//        this.fullEndUpdateSql = fullEndUpdateSql;
//    }

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

//    public int getDetailConcurrentNum() {
//        return detailConcurrentNum;
//    }
//
//    public void setDetailConcurrentNum(int detailConcurrentNum) {
//        this.detailConcurrentNum = detailConcurrentNum;
//    }
//
//    public int getFullConcurrentNum() {
//        return fullConcurrentNum;
//    }
//
//    public void setFullConcurrentNum(int fullConcurrentNum) {
//        this.fullConcurrentNum = fullConcurrentNum;
//    }

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    public FullQuery getFullQuery() {
        return fullQuery;
    }

    public void setFullQuery(FullQuery fullQuery) {
        this.fullQuery = fullQuery;
    }

    public DetailQuery getDetailQuery() {
        return detailQuery;
    }

    public void setDetailQuery(DetailQuery detailQuery) {
        this.detailQuery = detailQuery;
    }

    //    public String getDetailQuery() {
//        return detailQuery;
//    }
//
//    public void setDetailQuery(String detailQuery) {
//        this.detailQuery = detailQuery;
//    }
//
//    public String getFullQuery() {
//        return fullQuery;
//    }
//
//    public void setFullQuery(String fullQuery) {
//        this.fullQuery = fullQuery;
//    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
