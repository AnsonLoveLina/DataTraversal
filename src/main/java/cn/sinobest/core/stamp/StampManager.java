package cn.sinobest.core.stamp;

/**
 * Created by zhouyi1 on 2016/3/9 0009.
 */
public interface StampManager {
    public void init(String TIMESTAMP_COMMENT, String TIMESTAMP_KEY);
    public Object getIncrementIdenti();
    public boolean isComplete();
    public void incrementBefore();
    public void incrementFinally();
    public void completeBefore();
}
