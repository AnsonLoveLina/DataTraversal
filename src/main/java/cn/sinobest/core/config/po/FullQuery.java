package cn.sinobest.core.config.po;

import javax.xml.bind.annotation.*;

/**
 * Created by zhouyi1 on 2016/1/20 0020.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FullQuery implements ITraverseQuery {
    @XmlValue
    private String fullQuery;
    @XmlAttribute(name = "concurrentNum")
    private int fullConcurrentNum=0;
    @XmlAttribute(name = "endUpdateSql")
    private String fullEndUpdateSql;

    @Override
    public String toString() {
        return fullQuery.toString();
    }

    @Override
    public void setTraverseQuery(String fullQuery) {
        this.fullQuery = fullQuery;
    }

    @Override
    public int getConcurrentNum() {
        return fullConcurrentNum;
    }

    @Override
    public void setConcurrentNum(int fullConcurrentNum) {
        this.fullConcurrentNum = fullConcurrentNum;
    }

    @Override
    public String getEndUpdateSql() {
        return fullEndUpdateSql;
    }

    @Override
    public void setEndUpdateSql(String fullEndUpdateSql) {
        this.fullEndUpdateSql = fullEndUpdateSql;
    }

    public void setFullQuery(String fullQuery) {
        this.fullQuery = fullQuery;
    }

    public int getFullConcurrentNum() {
        return fullConcurrentNum;
    }

    public void setFullConcurrentNum(int fullConcurrentNum) {
        this.fullConcurrentNum = fullConcurrentNum;
    }

    public String getFullEndUpdateSql() {
        return fullEndUpdateSql;
    }

    public void setFullEndUpdateSql(String fullEndUpdateSql) {
        this.fullEndUpdateSql = fullEndUpdateSql;
    }
}
