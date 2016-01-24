package cn.sinobest.core.config.po;

import javax.xml.bind.annotation.*;

/**
 * Created by zhouyi1 on 2016/1/20 0020.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DetailQuery implements ITraverseQuery {
    @XmlValue
    private String detailQuery;
    @XmlAttribute(name = "concurrentNum")
    private int detailConcurrentNum=0;
    @XmlAttribute(name = "endUpdateSql")
    private String detailEndUpdateSql;

    @Override
    public String toString() {
        return detailQuery.toString();
    }

    @Override
    public void setTraverseQuery(String detailQuery) {
        this.detailQuery = detailQuery;
    }

    @Override
    public int getConcurrentNum() {
        return detailConcurrentNum;
    }

    @Override
    public void setConcurrentNum(int detailConcurrentNum) {
        this.detailConcurrentNum = detailConcurrentNum;
    }

    @Override
    public String getEndUpdateSql() {
        return detailEndUpdateSql;
    }

    @Override
    public void setEndUpdateSql(String detailEndUpdateSql) {
        this.detailEndUpdateSql = detailEndUpdateSql;
    }

    public void setDetailQuery(String detailQuery) {
        this.detailQuery = detailQuery;
    }

    public int getDetailConcurrentNum() {
        return detailConcurrentNum;
    }

    public void setDetailConcurrentNum(int detailConcurrentNum) {
        this.detailConcurrentNum = detailConcurrentNum;
    }

    public String getDetailEndUpdateSql() {
        return detailEndUpdateSql;
    }

    public void setDetailEndUpdateSql(String detailEndUpdateSql) {
        this.detailEndUpdateSql = detailEndUpdateSql;
    }
}
