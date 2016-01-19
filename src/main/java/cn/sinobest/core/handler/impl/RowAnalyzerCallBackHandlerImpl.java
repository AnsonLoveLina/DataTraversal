package cn.sinobest.core.handler.impl;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@Component(value = "analyzerCallBackHandler")
public class RowAnalyzerCallBackHandlerImpl implements IRowAnalyzerCallBackHandler {

    private DataSource ds;

    private TraverseConfigSchema traverseConfigSchema;

    private boolean isComplete;

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public RowAnalyzerCallBackHandlerImpl(DataSource dataSource, TraverseConfigSchema traverseConfigSchema) {
        this.ds = ds;
        this.traverseConfigSchema = traverseConfigSchema;
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {

    }
}
