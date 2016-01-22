package cn.sinobest.traverse.handler;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.traverse.adapter.CallBackAdapter;
import cn.sinobest.traverse.analyzer.StringAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema的每个语句都有独立的callBack
 * 在schema的每个语句中每次定时的轮训都使用同一个callBack对象
 * 任务同步：那么一个callBack对象是不会同时运行schema和语句一样的任务
 * 任务异步：那么一个callBack对象是会同时运行schema和语句一样的任务
 */
@Component(value = "analyzerCallBackHandler")
@Scope(value = "prototype")
public class RowAnalyzerCallBackHandlerImpl implements IRowAnalyzerCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowAnalyzerCallBackHandlerImpl.class);

    private TraverseConfigSchema traverseConfigSchema;

    private boolean isComplete = false;

    private int concurrentNum = 0;

    private ParsedSql endUpdateSql;

    @Override
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * 给spring用
     */
    public RowAnalyzerCallBackHandlerImpl() {
    }

    public RowAnalyzerCallBackHandlerImpl(TraverseConfigSchema traverseConfigSchema) {
        Preconditions.checkNotNull(traverseConfigSchema);
        this.traverseConfigSchema = traverseConfigSchema;
    }

    public void initExecutor(int concurrentNum){
        //目前先采用队列作为缓冲区，提高安全性和吞吐
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(15000);
        ExecutorService taskServcie = new ThreadPoolExecutor(concurrentNum, concurrentNum,
                0L, TimeUnit.MILLISECONDS,
                queue, blockingPolicy);
        executor.set(taskServcie);
    }

    @PostConstruct
    public void init() {
        //spring初始化的情况
        if (traverseConfigSchema==null)
            return;
        if (isComplete) {
            concurrentNum = traverseConfigSchema.getFullQuery().getFullConcurrentNum();
            endUpdateSql = traverseConfigSchema.getFullSql();
        } else {
            concurrentNum = traverseConfigSchema.getDetailQuery().getDetailConcurrentNum();
            endUpdateSql = traverseConfigSchema.getDetailSql();
        }

        if (concurrentNum>0){
            initExecutor(concurrentNum);
        }

    }

    RejectedExecutionHandler blockingPolicy = new RejectedExecutionHandler() {

        public void rejectedExecution(Runnable task,
                                      ThreadPoolExecutor executor) {
            if (!executor.isShutdown())
                try {
                    executor.getQueue().put(task);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

    };
    ThreadLocal<ExecutorService> executor = new ThreadLocal<ExecutorService>();

    private class Task implements Runnable {

        private List<HashMap<String, Object>> paramMaps;

        public Task(List<HashMap<String, Object>> paramMaps) {
            this.paramMaps = paramMaps;
        }

        @Override
        public void run() {
            adapter.processBiz(traverseConfigSchema.getInsertSql(), endUpdateSql, paramMaps);
        }
    }

    @Autowired
    private CallBackAdapter adapter;

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //没把分析字段并且获取结果放到JUC环境中，主要是因为这个方法大部分都是纯CPU计算的、没有太多的IO和非CPU等待，放入到JUC后不一定能提高多大效率
       /* List<HashMap<String, Object>> paramMaps = getAnalyzedMapWithSchema(resultSet);
        if (paramMaps.isEmpty()) {
            logger.error("schema：" + traverseConfigSchema.getSchemaName() + "字段解析错误!");
            return;
        }
        if (concurrentNum == 0) {
            adapter.processBiz(traverseConfigSchema.getInsertSql(), endUpdateSql, paramMaps);
        } else if (concurrentNum > 0) {
            executor.get().execute(new Task(paramMaps));
        }*/
        System.out.println(resultSet.getObject("ajmc"));
    }

    private List<HashMap<String, Object>> getAnalyzedMapWithSchema(ResultSet resultSet) throws SQLException {
        Preconditions.checkNotNull(resultSet);
        HashMap<String, Object> dbMap = new HashMap<String, Object>();
        Set<String> columns = traverseConfigSchema.getColumns();
        for (String column : columns) {
            Object value = resultSet.getObject(column);
            dbMap.put(column, value);
        }

        if (dbMap.isEmpty()) {
            logger.error("schema：" + traverseConfigSchema.getSchemaName() + "xml解析错误!");
            return new ArrayList();
        }
        List<HashMap<String, Object>> paramMap = adapter.analyze(dbMap, traverseConfigSchema.getAnalyzerColumns());
        return paramMap;
    }

}
