package cn.sinobest.core.service;

import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.handler.IRowCallBackHandler;

/**
 * Created by zhouyi1 on 2016/3/10 0010.
 */
public interface ITraverseDataService {
    public void initService(Schemaer schemaer, IRowCallBackHandler rowCallbackHandler);
    public void execute();
}
