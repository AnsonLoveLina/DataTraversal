package cn.sinobest.core.handler;

import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SqlSchemaer;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema的data对应一个callBack对象
 * 每个schema的语句由于是同步的所以不会同时使用到
 */
@Component(value = "callBackHandlerDefault")
@Scope(value = "prototype")
@Lazy
public class RowCallBackHandlerDefaultImpl implements IRowCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowCallBackHandlerDefaultImpl.class);

    private SqlSchemaer sqlSchemaer;

    private int concurrentNum;

    @Override
    public void initCallBackHandler(boolean isComplete,Schemaer schemaer) {
        Preconditions.checkNotNull(schemaer);
        //spring初始化的情况
        if (schemaer == null)
            return;
        if (isComplete) {
            sqlSchemaer = schemaer.getFullSchemaer();
        } else {
            sqlSchemaer = schemaer.getDetailSchemaer();
        }

        concurrentNum = sqlSchemaer.getTraverseQuery().getConcurrentNum();
        if (concurrentNum > 0) {
            initExecutor(concurrentNum);
        }
    }

    public void initExecutor(int concurrentNum) {
        //目前先采用队列作为缓冲区，提高安全性和吞吐
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(15000);
        ExecutorService taskServcie = new ThreadPoolExecutor(concurrentNum, concurrentNum,
                0L, TimeUnit.MILLISECONDS,
                queue, blockingPolicy);
        executor.set(taskServcie);
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

        @Override
        public void run() {

        }
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        System.out.println(resultSet.getString("AJID"));
    }

}
