package cn.sinobest.traverse.io;

import cn.sinobest.core.common.util.SqlUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@Component(value = "preparedStatementCommiter")
@Scope("prototype")
@Lazy
public class PreparedStatementCommiter implements IBatchCommiter {
    private static final Log logger = LogFactory.getLog(PreparedStatementCommiter.class);

    @Resource(name = "dataSource")
    private DataSource dataSource;

    private Connection conn;

    private PreparedStatement ps;

    public PreparedStatement getPreparedStatement() {
        return ps;
    }

    public PreparedStatementCommiter(String processResultSql) {
        try {
            this.conn = this.dataSource.getConnection();
            this.ps = conn.prepareStatement(processResultSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setObjects(Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                ps.setObject(i + 1, param);
            }
            ps.addBatch();
        } catch (SQLException e) {
            logger.error("ps.setObject error!",e);
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
                logger.error("commit error!",e);
            }
        }
    }
}
