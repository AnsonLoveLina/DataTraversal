package cn.sinobest.traverse.handler;

import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.core.config.schema.ResultSql;
import cn.sinobest.core.handler.IRowCallBackHandler;
import cn.sinobest.core.handler.RowCallBackHandlerDefaultImpl;
import cn.sinobest.traverse.analyzer.IAnalyzer;
import cn.sinobest.traverse.po.InsertParamObject;
import cn.sinobest.traverse.relolver.IExpressRelolver;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jodd.typeconverter.Convert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema的data对应一个callBack对象
 * 每个schema的语句由于是同步的所以不会同时使用到
 */
@Component(value = "analyzerCallBackHandler")
@Scope(value = "prototype")
@Lazy
public class RowCallBackHandlerImpl extends RowCallBackHandlerDefaultImpl implements IRowCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowCallBackHandlerImpl.class);

    @Resource(name = "expressRelolver")
    IExpressRelolver relolver;

    @Autowired
    private DataSource dataSource;

    @Override
    public void destoryCallBackHandler() {

    }

//    public RowAnalyzerCallBackHandlerImpl(Schemaer schemaer) {
//        Preconditions.checkNotNull(schemaer);
//        this.schemaer = schemaer;
//    }

    private class Task implements Runnable {

        private HashMap<String, String> rowMaps;
        private IAnalyzer analyzer;
        private List<AnalyzerColumn> analyzerColumns;

        public Task(HashMap<String, String> rowMaps, IAnalyzer analyzer, List<AnalyzerColumn> analyzerColumns) {
            this.rowMaps = rowMaps;
            this.analyzer = analyzer;
            this.analyzerColumns = analyzerColumns;
        }

        @Override
        public void run() {
            Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps, analyzer, analyzerColumns);
            processBiz(sqlSchemaer.getInsertSql(), sqlSchemaer.getEndUpdateSql(), paramObjects);
        }
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //没把分析字段并且获取结果放到JUC环境中，主要是因为这个方法大部分都是纯CPU计算的、没有太多的IO和非CPU等待，放入到JUC后不一定能提高多大效率

        HashMap<String, String> rowMaps = getRowMap(resultSet);
        if (rowMaps.isEmpty()) {
            logger.info("该条数据没有有效数据!");
            return;
        }
//        System.out.println(rowMaps);
//        Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps,sqlSchemaer.getAnalyzer(),sqlSchemaer.getAnalyzerColumns());
        if (concurrentNum == 0) {
            Set<InsertParamObject> paramObjects = analyzerRowMap(rowMaps, sqlSchemaer.getAnalyzer(), sqlSchemaer.getAnalyzerColumns());
            processBiz(sqlSchemaer.getInsertSql(), sqlSchemaer.getEndUpdateSql(), paramObjects);
        } else if (concurrentNum > 0) {
//            Set<AnalyzerColumn> analyzerColumns = Sets.newHashSet(sqlSchemaer.getAnalyzerColumns());
            processJucBiz(new Task(rowMaps, sqlSchemaer.getAnalyzer(), sqlSchemaer.getAnalyzerColumns()));
        }
    }

    /**
     * 处理业务
     * 这么不优雅的方式，早晚改了它
     * @param insertSql
     * @param endUpdateSql
     * @param paramSet
     */
    private void processBiz(ResultSql insertSql, ResultSql endUpdateSql, Set<InsertParamObject> paramSet) {
        Preconditions.checkNotNull(insertSql);
        Connection connection = null;
        PreparedStatement insertPS = null;
        try{
            connection = dataSource.getConnection();
            insertPS = connection.prepareStatement(insertSql.getResultSql().toString());
            for (InsertParamObject paramObject:paramSet){
                Map<String,String> param = paramObject.getParamMap();
                Object[] params = SqlUtil.getParam(insertSql.getResultSql(),param);
                for (int i=0;i<params.length;i++){
                    insertPS.setObject(i + 1, params[i]);
                }
                insertPS.addBatch();
            }
                insertPS.executeBatch();
                connection.commit();
         }catch (SQLException e){
            //如果是唯一约束导致的错误则再运行一次
            if(e.getMessage().toLowerCase().indexOf("ora-00001")!=-1){
                processBizExact(insertSql, endUpdateSql, paramSet,insertPS,connection);
            }else{
                logger.error("结果数据存库出错！",e);
            }
        }finally {
            JdbcUtils.close(insertPS);
            JdbcUtils.close(connection);
        }
    }

    /**
     * 本来准备放一个方法的，后面觉得怕看不懂，分开吧，代码是给人看的
     * @param insertSql
     * @param endUpdateSql
     * @param paramSet
     * @param insertPS
     * @param connection
     */
    private void processBizExact(ResultSql insertSql, ResultSql endUpdateSql, Set<InsertParamObject> paramSet, PreparedStatement insertPS, Connection connection) {
        Preconditions.checkNotNull(insertSql);
        try{
            for (InsertParamObject paramObject:paramSet){
                Map<String,String> param = paramObject.getParamMap();
                Object[] params = SqlUtil.getParam(insertSql.getResultSql(),param);
                for (int i=0;i<params.length;i++){
                    insertPS.setObject(i + 1, params[i]);
                }
                try {
                    insertPS.execute();
                }catch (SQLException e){
                    if(e.getMessage().toLowerCase().indexOf("ora-00001")!=-1){
                        logger.error(Arrays.toString(params)+"重复！");
                    }else{
                        logger.error("结果数据存库出错！",e);
                    }
                }
                connection.commit();
            }
        }catch (SQLException e){
            logger.error("结果数据存库出错！",e);
        }finally {
            JdbcUtils.close(insertPS);
            JdbcUtils.close(connection);
        }
    }

    //遍历使用的迭代遍历，如果集合在JUC环境外，线程不安全
    public Set<InsertParamObject> analyzerRowMap(Map<String, String> rowMap, IAnalyzer analyzer, final List<AnalyzerColumn> analyzerColumns) {
        Set<InsertParamObject> paramObjectSet = Sets.newHashSet();
//        for (AnalyzerColumn analyzerColumn:analyzerColumns){
        for (int i = 0; i < analyzerColumns.size(); i++) {
            AnalyzerColumn analyzerColumn = analyzerColumns.get(i);
            String analyzerSource = rowMap.get(analyzerColumn.toString());
            paramObjectSet.addAll(analyzer.analyzerStr(analyzerSource, analyzerColumn));
        }

        rowMap = Maps.filterKeys(rowMap, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return !analyzerColumns.contains(new AnalyzerColumn(s));
            }
        });

        for (InsertParamObject paramObject : paramObjectSet) {
            Map<String, String> paramMap = Maps.newHashMap(rowMap);
            paramObject.mergeParamMap(paramMap);
            relolver.explain(paramObject);
        }
//        PrintUtil.printParamObjectSet(paramObjectSet);
        return paramObjectSet;
    }

    private HashMap<String, String> getRowMap(ResultSet resultSet) throws SQLException {
        Preconditions.checkNotNull(resultSet);
        HashMap<String, String> rowMap = new HashMap<String, String>();
        List<AnalyzerColumn> analyzerColumns = sqlSchemaer.getAnalyzerColumns();
        Set<String> columns = sqlSchemaer.getColumns();
        Set<String> traverseColumns = sqlSchemaer.getTraverseColumns();
        if (analyzerColumns.isEmpty() || analyzerColumns.size()==0){
            return new HashMap<String, String>();
        }
        int count = 0;
        for (String columnName : columns) {
            String columnValue = null;
            if (traverseColumns.contains(columnName)) {
                columnValue = Convert.toString(resultSet.getObject(columnName));
                if (columnValue != null && analyzerColumns.contains(new AnalyzerColumn(columnName))) {
                    count++;
                }
            }
            rowMap.put(columnName, columnValue);
        }

        //加这么个判断只是为了安全！其实不必要
        return analyzerColumns.size() >= count ? rowMap : new HashMap<String, String>();
    }


}
