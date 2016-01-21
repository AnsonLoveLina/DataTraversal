package cn.sinobest.traverse.handler;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema都有独立的callBack
 * 在schema中每次定时的轮训都使用同一个callBack对象
 * 任务同步：那么一个callBack对象是不会同时运行schema一样的任务
 * 任务异步：那么一个callBack对象是会同时运行schema一样的任务
 */
@Component(value = "analyzerCallBackHandler")
@Scope(value = "prototype")
public class RowAnalyzerCallBackHandlerImpl implements IRowAnalyzerCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowAnalyzerCallBackHandlerImpl.class);

    @Resource(name = "dataSource")
    private DataSource dataSource;

    private TraverseConfigSchema traverseConfigSchema;

    private boolean isComplete;

    private int concurrentNum;

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

    @PostConstruct
    public void init(){
        concurrentNum = isComplete?traverseConfigSchema.getFullQuery().getFullConcurrentNum():traverseConfigSchema.getDetailQuery().getDetailConcurrentNum();
        //目前先采用队列作为缓冲区，提高安全性和吞吐
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(15000);
        ExecutorService taskServcie = new ThreadPoolExecutor(concurrentNum, concurrentNum,
                0L, TimeUnit.MILLISECONDS,
                queue, blockingPolicy);
        executor.set(taskServcie);
    }

    RejectedExecutionHandler blockingPolicy = new RejectedExecutionHandler(){

        public void rejectedExecution(Runnable task,
                                      ThreadPoolExecutor executor) {
            if(!executor.isShutdown())
                try {
                    executor.getQueue().put(task);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

    };
    ThreadLocal<ExecutorService> executor = new ThreadLocal<ExecutorService>();

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        int concurrentNum = isComplete?traverseConfigSchema.getFullQuery().getFullConcurrentNum():traverseConfigSchema.getDetailQuery().getDetailConcurrentNum();
        if(concurrentNum==0){

        }else if(concurrentNum>0){

        }
        System.out.println(resultSet.getObject("AJMC"));
    }


//    private HashMap<String,PreparedStatement> psCache = new HashMap<String,PreparedStatement>();

    /**
     * 本来准备cache的，但是挺难的，还是使用连接池的psCache
     * @param sql
     * @param conn
     * @return
     * @throws SQLException
     */
    public PreparedStatement getPreparedStatement(String sql,Connection conn) throws SQLException {
        return conn.prepareStatement(sql);
    }

    //暂时先不加对于schemaName的锁
    public void processResult(Connection conn,ParsedSql processResultSql,HashMap paramMap) throws Exception{
        String sqlNew = NamedParameterUtils.substituteNamedParameters(processResultSql, new MapSqlParameterSource(paramMap));
        Object[] params = NamedParameterUtils.buildValueArray(processResultSql, new MapSqlParameterSource(paramMap), (List)null);
        PreparedStatement ps = null;
//        try {
            ps = getPreparedStatement(sqlNew,conn);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i+1,params[i]);
            }

            ps.executeBatch();

//            conn.commit();
//        }catch (Exception e){
//            logger.error(e.getMessage(),e);
//        }finally{
//            JdbcUtils.close(conn);
//            JdbcUtils.close(ps);
//        }

    }

}
