package cn.sinobest.core.service;

import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.handler.IRowCallBackHandler;
import cn.sinobest.core.stamp.StampManager;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* Created by zhouyi1 on 2014/12/11 0011.
* 仅仅为了遍历数据，并且约束结果处理的核心方法
*/
@Component
@Scope(value = "prototype")
@Lazy
public class TraverseDataService implements ITraverseDataService {
    private static final Log logger = LogFactory.getLog(TraverseDataService.class);

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    protected Schemaer schemaer;

    @Autowired
    private StampManager timeStampManager;

    private IRowCallBackHandler rowCallbackHandler;

    public void initService(Schemaer schemaer,IRowCallBackHandler rowCallbackHandler){
        Preconditions.checkNotNull(schemaer);
        Preconditions.checkNotNull(rowCallbackHandler);
        this.schemaer = schemaer;
        this.rowCallbackHandler = rowCallbackHandler;
        if(schemaer==null)
            logger.error(schemaer.getFullSchemaer().toString()+"in traverseConfig didn't exists!");

        timeStampManager.init(schemaer.getFullSchemaer().getTimestampComment(), schemaer.getFullSchemaer().getTimestampKey());
    }

    public void execute(){
        try {
//            Long start = System.currentTimeMillis();
            if(!timeStampManager.isComplete()) {
                timeStampManager.increment();
//                try{
                    rowCallbackHandler.initCallBackHandler(false, schemaer);
                    jdbcTemplate.query(schemaer.getDetailSchemaer().getTraverseQuery().toString(), new Object[]{timeStampManager.getIncrementIdenti()}, rowCallbackHandler);
//                }finally {
//                    timeStampManager.incrementFinally();
//                }
            }else{
                timeStampManager.complete();
                rowCallbackHandler.initCallBackHandler(true, schemaer);
                jdbcTemplate.query(schemaer.getFullSchemaer().getTraverseQuery().toString(), rowCallbackHandler);
            }
//            Long end = System.currentTimeMillis()-start;
//            System.out.println("countTime = " + end/1000);
        } catch (Exception e) {
            logger.error("运行语句出错！",e);
        }
    }

}
