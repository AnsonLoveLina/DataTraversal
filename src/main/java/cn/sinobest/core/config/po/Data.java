package cn.sinobest.core.config.po;

import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
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
    private DetailQuery detailQuery;
    @XmlElement(name = "fullQuery",required = true)
    private FullQuery fullQuery;
    @XmlAttribute(name = "schemaName",required = true)
    private String schemaName;
    @XmlAttribute(name = "timestampComment",required = true)
    private String timestampComment;
    @XmlAttribute(name = "timestampKey",required = true)
    private String timestampKey;
    @XmlAttribute(name = "regexSql")
    private String regexSql;
    @XmlAttribute(name = "noNumRegexSql")
    private String noNumberRegexSql;
    @XmlAttribute(name = "resultTable")
    private String resultTable;
    @XmlElement(name = "analyzerColumn")
    private List<AnalyzerColumn> analyzerColumns = new ArrayList<AnalyzerColumn>();
    @XmlElement(name = "analyzerResultColumn")
    private Set<String> analyzerResultColumns = new HashSet<String>();
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

    public Set<String> getAnalyzerResultColumns() {
        Set<String> newAnalyzerResultColumns = Sets.newHashSet();
        for (String analyzerResultColumn:analyzerResultColumns){
            newAnalyzerResultColumns.add(analyzerResultColumn.toLowerCase());
        }
        return newAnalyzerResultColumns;
    }

    public void setAnalyzerResultColumns(Set<String> analyzerResultColumns) {
        this.analyzerResultColumns = analyzerResultColumns;
    }
}
