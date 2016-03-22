package cn.sinobest.core.handler;

import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SqlSchemaer;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 每个schema的data对应一个callBack对象
 * 每个schema的语句由于是同步的所以不会同时使用到
 */
public abstract class RowCallBackHandlerDefaultImpl implements IRowCallBackHandler {
    private static final Log logger = LogFactory.getLog(RowCallBackHandlerDefaultImpl.class);

    protected SqlSchemaer sqlSchemaer;

    protected int concurrentNum;

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

    @Override
    public void destoryCallBackHandler() {

    }

    public void initExecutor(int concurrentNum) {
        //目前先采用队列作为缓冲区，提高安全性和吞吐
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(15000);
        taskServcie = new ThreadPoolExecutor(concurrentNum, concurrentNum,
                0L, TimeUnit.MILLISECONDS,
                queue, blockingPolicy);
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
    private ExecutorService taskServcie = null;

    /**
     * 并发处理业务，一直到所有结束才结束
     * @param task
     */
    public void processJucBiz(Runnable task){
        taskServcie.execute(task);
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        System.out.println(resultSet.getString("AJID"));
    }

}
