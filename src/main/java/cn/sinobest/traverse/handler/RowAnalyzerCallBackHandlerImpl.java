package cn.sinobest.traverse.handler;

import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SqlSchemaer;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.traverse.adapter.CallBackAdapter;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.analyzer.StringAnalyzer;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import cn.sinobest.traverse.po.InsertParamObject;
import cn.sinobest.traverse.relolver.IExpressRelolver;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import jodd.typeconverter.Convert;
import jodd.util.CollectionUtil;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema的data对应一个callBack对象
 * 每个schema的语句由于是同步的所以不会同时使用到
 */
@Component(value = "analyzerCallBackHandler")
@Scope(value = "prototype")
@Lazy
public class RowAnalyzerCallBackHandlerImpl implements IRowAnalyzerCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowAnalyzerCallBackHandlerImpl.class);

    @Resource(name = "expressRelolver")
    IExpressRelolver relolver;

    private Schemaer schemaer;

    private SqlSchemaer sqlSchemaer;

    private int concurrentNum;

    private boolean isComplete = false;

    @Override
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public RowAnalyzerCallBackHandlerImpl(Schemaer schemaer) {
        Preconditions.checkNotNull(schemaer);
        this.schemaer = schemaer;
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
        if (schemaer==null)
            return;
        if (isComplete) {
            sqlSchemaer = schemaer.getFullSchemaer();
        } else {
            sqlSchemaer = schemaer.getDetailSchemaer();
        }

        concurrentNum = sqlSchemaer.getTraverseQuery().getConcurrentNum();
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

        private HashMap<String, String> rowMaps;
        private IAnalyzer analyzer;
        private Set<AnalyzerColumn> analyzerColumns;

        public Task(HashMap<String, String> rowMaps, IAnalyzer analyzer, Set<AnalyzerColumn> analyzerColumns) {
            this.rowMaps = rowMaps;
            this.analyzer = analyzer;
            this.analyzerColumns = analyzerColumns;
        }

        @Override
        public void run() {
            Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps,sqlSchemaer.getAnalyzer(),analyzerColumns);
            processBiz(sqlSchemaer.getInsertSql(), sqlSchemaer.getEndUpdateSql(), paramObjects);
        }
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //没把分析字段并且获取结果放到JUC环境中，主要是因为这个方法大部分都是纯CPU计算的、没有太多的IO和非CPU等待，放入到JUC后不一定能提高多大效率
        HashMap<String, String> rowMaps = getRowMap(resultSet);
        if (rowMaps.isEmpty()) {
            logger.trace("该条数据没有有效数据!");
            return;
        }
//        Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps,sqlSchemaer.getAnalyzer(),sqlSchemaer.getAnalyzerColumns());
        if (concurrentNum == 0) {
            Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps,sqlSchemaer.getAnalyzer(),sqlSchemaer.getAnalyzerColumns());
            processBiz(sqlSchemaer.getInsertSql(), sqlSchemaer.getEndUpdateSql(), paramObjects);
        } else if (concurrentNum > 0) {
            Set<AnalyzerColumn> analyzerColumns = Sets.newHashSet(sqlSchemaer.getAnalyzerColumns());
            executor.get().execute(new Task(rowMaps,sqlSchemaer.getAnalyzer(),analyzerColumns));
        }
    }

    private void processBiz(ParsedSql insertSql,ParsedSql endUpdateSql,Set<InsertParamObject> paramMap) {
        Preconditions.checkNotNull(insertSql);
//        PreparedStatementCommiter commiter = null;
//        try {
//            adapter.processResult(insertSql, paramMap,commiter);
//            if (endUpdateSql!=null){
//                adapter.processResult(endUpdateSql, paramMap,commiter);
//            }
//        } catch (Exception e) {
//            logger.error("结果语句preparedStatement！",e);
//        }

    }

    //遍历使用的迭代遍历，如果集合在JUC环境外，线程不安全
    public Set<InsertParamObject> analyzerRowMap(Map<String, String> rowMap,IAnalyzer analyzer,final Set<AnalyzerColumn> analyzerColumns){
        Set<InsertParamObject> paramObjectSet = Sets.newHashSet();
        for (AnalyzerColumn analyzerColumn:analyzerColumns){
            String analyzerSource = rowMap.get(analyzerColumn.toString());
            paramObjectSet.addAll(analyzer.analyzerStr(analyzerSource, analyzerColumn));
//            System.out.println("");
        }

        rowMap = Maps.filterKeys(rowMap, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return !analyzerColumns.contains(new AnalyzerColumn(s));
            }
        });

        for (InsertParamObject paramObject:paramObjectSet){
            Map<String, String> paramMap = Maps.newHashMap(rowMap);
            paramObject.mergeParamMap(paramMap);
            relolver.explain(paramObject);
        }
        return paramObjectSet;
    }

    private HashMap<String,String> getRowMap(ResultSet resultSet) throws SQLException{
        Preconditions.checkNotNull(resultSet);
        HashMap<String, String> rowMap = new HashMap<String, String>();
        Set<AnalyzerColumn> analyzerColumns = sqlSchemaer.getAnalyzerColumns();
        Set<String> columns = sqlSchemaer.getColumns();
        Set<String> traverseColumns = sqlSchemaer.getTraverseColumns();
        int count = 0;
        for (String columnName : columns){
            String columnValue = null;
            if (traverseColumns.contains(columnName)){
                columnValue = Convert.toString(resultSet.getObject(columnName));
                if (columnValue!=null && analyzerColumns.contains(new AnalyzerColumn(columnName))){
                    count++;
                }
            }
            rowMap.put(columnName,columnValue);
        }

        return analyzerColumns.size()>count?rowMap:new HashMap<String, String>();
    }


}
