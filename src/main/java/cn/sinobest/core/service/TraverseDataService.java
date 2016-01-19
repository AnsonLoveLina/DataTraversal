package cn.sinobest.core.service;

import cn.sinobest.core.TimeManager;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.core.handler.impl.RowAnalyzerCallBackHandlerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
* Created by zhouyi1 on 2014/12/11 0011.
* 仅仅为了遍历数据，分为多线程和单线程，并且约束结果处理的核心方法
*/
public class TraverseDataService {
    private static final Log logger = LogFactory.getLog(TraverseDataService.class);

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    protected TraverseConfigSchema traverseConfigSchema;

    protected String schemaName = "";

    @Resource
    private TimeManager timeManager;

    private IRowAnalyzerCallBackHandler rowCallbackHandler;

    public TraverseDataService(TraverseConfigSchema traverseConfigSchema,String schemaName,IRowAnalyzerCallBackHandler rowCallbackHandler) {
        this.schemaName = schemaName;
        this.traverseConfigSchema = traverseConfigSchema;
        this.rowCallbackHandler = rowCallbackHandler;
        if(traverseConfigSchema==null)
            logger.error(schemaName+"in traverseConfig didn't exists!");
        timeManager.init(traverseConfigSchema.getTimestampComment(), traverseConfigSchema.getTimestampKey());
//        initRegex();
    }

    public void execute(){
        try {
            String lastTime = timeManager.getTimestamp();
            if(!"".equals(lastTime)) {
                timeManager.updateTimestamp(lastTime);
                try{
                    rowCallbackHandler.setComplete(false);
                    jdbcTemplate.query(traverseConfigSchema.getDetailQuery(), new Object[]{lastTime}, rowCallbackHandler);
                }finally {
                    timeManager.overTimestamp();
                }
            }else{
                timeManager.insertTimestamp();
                rowCallbackHandler.setComplete(true);
                jdbcTemplate.query(traverseConfigSchema.getFullEndUpdateSql(), rowCallbackHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
