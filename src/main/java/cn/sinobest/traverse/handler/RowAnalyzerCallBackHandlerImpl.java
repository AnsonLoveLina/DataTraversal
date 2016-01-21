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

    private String endUpdateSql;

    //init的时候初始化
    private ImmutableMap<String, Object> paramMapTemplate = null;

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

    @PostConstruct
    public void init() {
        if (isComplete){
            concurrentNum = traverseConfigSchema.getFullQuery().getFullConcurrentNum();
            endUpdateSql = traverseConfigSchema.getFullQuery().getFullEndUpdateSql();
        }else{
            concurrentNum = traverseConfigSchema.getDetailQuery().getDetailConcurrentNum();
            endUpdateSql = traverseConfigSchema.getDetailQuery().getDetailEndUpdateSql();
        }
        //目前先采用队列作为缓冲区，提高安全性和吞吐
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(15000);
        ExecutorService taskServcie = new ThreadPoolExecutor(concurrentNum, concurrentNum,
                0L, TimeUnit.MILLISECONDS,
                queue, blockingPolicy);
        executor.set(taskServcie);
        if (paramMapTemplate == null){
            paramMapTemplate = (ImmutableMap<String, Object>) Maps.asMap(traverseConfigSchema.getResultStrColumns(), new Function<String, Object>() {
                @Override
                public Object apply(String s) {
                    return null;
                }
            });
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

    private class Task implements Runnable{

        private List<HashMap<String,Object>> paramMaps;

        public Task(List<HashMap<String,Object>> paramMaps) {
            this.paramMaps = paramMaps;
        }

        @Override
        public void run() {
            processBiz(traverseConfigSchema.getInsertSql(), endUpdateSql, paramMaps);
        }
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        List<HashMap<String,Object>> paramMaps = getAnalyzedMapWithSchema(resultSet);
        if (paramMaps.isEmpty()){
            logger.error("schema："+traverseConfigSchema.getSchemaName()+"字段解析错误!");
            return;
        }
        if (concurrentNum == 0) {
            processBiz(traverseConfigSchema.getInsertSql(), endUpdateSql, paramMaps);
        } else if (concurrentNum > 0) {
            executor.get().execute(new Task(paramMaps));
        }
    }

    @Autowired
    private StringAnalyzer analyzer;

    private List<HashMap<String,Object>> getAnalyzedMapWithSchema(ResultSet resultSet) throws SQLException {
        HashMap<String,Object> dbMap = new HashMap<String,Object>();
        Set<String> columns = traverseConfigSchema.getColumns();
        for (String column:columns){
            Object value = resultSet.getObject(column);
            dbMap.put(column,value);
        }

        if (dbMap.isEmpty()){
            logger.error("schema："+traverseConfigSchema.getSchemaName()+"xml解析错误!");
            return new ArrayList();
        }
        List<HashMap<String,Object>> paramMap = analyzer.analyze(dbMap,traverseConfigSchema.getAnalyzerColumns());
        return paramMap;
    }

    //暂时先不加对于schemaName的锁
    private void processBiz(String insertSql,String endUpdateSql,List<HashMap<String,Object>> paramMap) {
        try {
            String insertSqlNew = NamedParameterUtils.substituteNamedParameters(getParsedSql(insertSql), new MapSqlParameterSource(paramMapTemplate));
            processResult(insertSqlNew, paramMap);
            if (endUpdateSql!=null){
                String endUpdateSqlNew = NamedParameterUtils.substituteNamedParameters(getParsedSql(endUpdateSql), new MapSqlParameterSource(paramMapTemplate));
                processResult(endUpdateSqlNew, paramMap);
            }
        } catch (Exception e) {
            logger.error("结果语句preparedStatement！",e);
        }

    }

    @Autowired
    private PreparedStatementCommiter commiter;

    private void processResult(String processResultSql, List<HashMap<String,Object>> paramMaps) throws Exception {
        PreparedStatement ps = commiter.getPrepareStatement(processResultSql);

        for (HashMap<String,Object> paramMap:paramMaps){
            Object[] params = NamedParameterUtils.buildValueArray(getParsedSql(processResultSql), new MapSqlParameterSource(paramMap), (List) null);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.addBatch();
            commiter.addBatch(processResultSql);
        }
    }

    //初始化一个10的缓存
    private final Map<String, ParsedSql> parsedSqlCache = new HashMap<String, ParsedSql>(10);

    private ParsedSql getParsedSql(String sql) {
//        synchronized (this.parsedSqlCache) {
        ParsedSql parsedSql = (ParsedSql) this.parsedSqlCache.get(sql);
        if (parsedSql == null) {
            parsedSql = NamedParameterUtils.parseSqlStatement(sql);
            this.parsedSqlCache.put(sql, parsedSql);
        }

        return parsedSql;
//        }
    }

}
