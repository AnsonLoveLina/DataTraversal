package cn.sinobest.core.config.po;

import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@XmlAccessorType(value = XmlAccessType.NONE)
public class Data {
    private static final Log logger = LogFactory.getLog(Data.class);
    @XmlElement(name = "detailQuery",required = true)
    protected DetailQuery detailQuery;
    @XmlElement(name = "fullQuery",required = true)
    protected FullQuery fullQuery;
    @XmlAttribute(name = "schemaName",required = true)
    protected String schemaName;
    @XmlAttribute(name = "timestampComment",required = true)
    protected String timestampComment;
    @XmlAttribute(name = "timestampKey",required = true)
    protected String timestampKey;
    @XmlAttribute(name = "concurrentNeedCheck")
    protected boolean concurrentNeedCheck;
    @XmlAttribute(name = "regexSql")
    protected String regexSql;
    @XmlAttribute(name = "noNumRegexSql")
    protected String noNumberRegexSql;
    @XmlAttribute(name = "resultTable")
    protected String resultTable;
    @XmlElement(name = "analyzerColumn")
    protected Set<AnalyzerColumn> analyzerColumns = new HashSet<AnalyzerColumn>();
    public Data() {

    }

    public DetailQuery getDetailQuery() {
        return detailQuery;
    }

    public void setDetailQuery(DetailQuery detailQuery) {
        this.detailQuery = detailQuery;
    }

    public FullQuery getFullQuery() {
        return fullQuery;
    }

    public void setFullQuery(FullQuery fullQuery) {
        this.fullQuery = fullQuery;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
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

    public boolean isConcurrentNeedCheck() {
        return concurrentNeedCheck;
    }

    public void setConcurrentNeedCheck(boolean concurrentNeedCheck) {
        this.concurrentNeedCheck = concurrentNeedCheck;
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
}
