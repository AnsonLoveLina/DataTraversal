package cn.sinobest.traverse.io;

import cn.sinobest.core.common.util.SqlUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zy-xx on 16/1/21.
 * 好吧，叫这个名字肯定要比较吊
 */
@Component(value = "commiter")
@Scope("prototype")
public class PreparedStatementCommiter {
    private static final Log logger = LogFactory.getLog(PreparedStatementCommiter.class);

    private Connection conn;

    private PreparedStatement ps;

    //给spring用
    public PreparedStatementCommiter() {
    }

    public PreparedStatement getPreparedStatement() {
        return ps;
    }

    public PreparedStatementCommiter(DataSource dataSource, String processResultSql) {
        try {
            this.conn = dataSource.getConnection();
            this.ps = conn.prepareStatement(processResultSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private AtomicInteger count = new AtomicInteger();

    public void executeAndCommit() {
//        logger.info("当前占用链接："+sqlPs.size());
        synchronized (this) {
            logger.info("当前" + count.get());
            try {
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 目前是限时自动commit
     * 这个方法是给次数commit用的
     *
     * @param processResultSql
     */
    public void addBatch(ParsedSql processResultSql) {
        count.incrementAndGet();
        if (count.compareAndSet(10000, 0)) {
            executeAndCommit();
        }
    }
}
