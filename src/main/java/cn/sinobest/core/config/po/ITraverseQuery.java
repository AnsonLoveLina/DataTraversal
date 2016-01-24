package cn.sinobest.core.config.po;

/**
 * Created by zy-xx on 16/1/24.
 */
public interface ITraverseQuery {

    public String toString();

    public void setTraverseQuery(String traverseQuery);

    public int getConcurrentNum();

    public void setConcurrentNum(int concurrentNum);

    public String getEndUpdateSql();

    public void setEndUpdateSql(String endUpdateSql);
}
