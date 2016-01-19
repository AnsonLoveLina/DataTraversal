package cn.sinobest.core.handler;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.sql.DataSource;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public interface IRowAnalyzerCallBackHandler extends RowCallbackHandler {
    public void setComplete(boolean isComplete);
}
