package cn.sinobest.core.service;

import cn.sinobest.core.TimeManager;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.config.po.TraverseConfigSchemaFactory;
import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.traverse.handler.RowAnalyzerCallBackHandlerImpl;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Component
@Scope(value = "prototype")
@Lazy
public class TraverseDataService {
    private static final Log logger = LogFactory.getLog(TraverseDataService.class);

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    protected Schemaer schemaer;

    @Autowired
    private TimeManager timeManager;

    private IRowAnalyzerCallBackHandler rowCallbackHandler;

    public TraverseDataService(Schemaer schemaer,IRowAnalyzerCallBackHandler rowCallbackHandler) {
        Preconditions.checkNotNull(schemaer);
        Preconditions.checkNotNull(rowCallbackHandler);
        this.schemaer = schemaer;
        this.rowCallbackHandler = rowCallbackHandler;
        if(schemaer==null)
            logger.error(schemaer.getFullSchemaer().toString()+"in traverseConfig didn't exists!");
//        initRegex();
    }

    @PostConstruct
    public void init(){
        timeManager.init(schemaer.getFullSchemaer().getTimestampComment(), schemaer.getFullSchemaer().getTimestampKey());
    }

    public void execute(){
        try {
            String lastTime = timeManager.getTimestamp();
//            Long start = System.currentTimeMillis();
            if(!"".equals(lastTime)) {
                timeManager.updateTimestamp(lastTime);
                try{
                    rowCallbackHandler.setComplete(false);
//                    System.out.println(schemaer.getDetailSchemaer().getTraverseQuery().toString());
//                    System.out.println(lastTime);
                    jdbcTemplate.query(schemaer.getDetailSchemaer().getTraverseQuery().toString(), new Object[]{lastTime}, rowCallbackHandler);
                }finally {
                    timeManager.overTimestamp();
                }
            }else{
                timeManager.insertTimestamp();
                rowCallbackHandler.setComplete(true);
                jdbcTemplate.query(schemaer.getFullSchemaer().getTraverseQuery().toString(), rowCallbackHandler);
            }
//            Long end = System.currentTimeMillis()-start;
//            System.out.println("countTime = " + end/1000);
        } catch (Exception e) {
            logger.error("时间戳或运行语句出错！",e);
        }
    }

}
