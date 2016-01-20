package cn.sinobest.core.handler.impl;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@Component(value = "analyzerCallBackHandler")
@Scope(value = "prototype")
public class RowAnalyzerCallBackHandlerImpl implements IRowAnalyzerCallBackHandler {

    @Resource(name = "dataSource")
    private DataSource dataSource;

    private TraverseConfigSchema traverseConfigSchema;

    private boolean isComplete;

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * 给spring用
     */
    public RowAnalyzerCallBackHandlerImpl() {
    }

    public RowAnalyzerCallBackHandlerImpl(TraverseConfigSchema traverseConfigSchema) {
        this.traverseConfigSchema = traverseConfigSchema;
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        System.out.println(resultSet.getObject("AJMC",String.class));
    }
}
