package cn.sinobest.core.handler;

import cn.sinobest.core.config.schema.Schemaer;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public interface IRowCallBackHandler extends RowCallbackHandler {
    public void initCallBackHandler(boolean isComplete, Schemaer schemaer);
    public void destoryCallBackHandler();
}
