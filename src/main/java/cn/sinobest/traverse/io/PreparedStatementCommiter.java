package cn.sinobest.traverse.io;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * Created by zy-xx on 16/1/21.
 * 好吧，叫这个名字肯定要比较吊
 */
public class PreparedStatementCommiter {
    private static final Log logger = LogFactory.getLog(PreparedStatementCommiter.class);

    private DataSource dataSource;

    public PreparedStatementCommiter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static PreparedStatementCommiter getInstance(DataSource dataSource){
        return new PreparedStatementCommiter(dataSource);
    }

    class Commiter{
        private Connection conn;

        private PreparedStatement ps;

        public Commiter(Connection conn) {
            this.conn = conn;
        }

        public void setPs(String sql) throws SQLException {
            this.ps = conn.prepareStatement(sql);
        }

        public PreparedStatement getPs() {
            return ps;
        }

        public Connection getConn() {
            return conn;
        }
    }

    private final ConcurrentHashMap<String,Commiter> sqlPs = new ConcurrentHashMap<String,Commiter>(64);

    public PreparedStatement getPrepareStatement(ParsedSql processResultSql) {
        Preconditions.checkNotNull(processResultSql);
        String sqlStr = processResultSql.toString();
        Commiter commiter = sqlPs.get(sqlStr);
        if (commiter==null){
            try {
                commiter = new Commiter(dataSource.getConnection());
                commiter.setPs(sqlStr);
                sqlPs.put(sqlStr,commiter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return commiter.getPs();
    }

    public void executeAndCommit(){
        logger.info("当前占用链接："+sqlPs.size());
        for (Commiter commiter:sqlPs.values()){
            try {
                commiter.ps.executeBatch();
                commiter.conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 目前是限时自动commit
     * 这个方法是给次数commit用的
     * @param processResultSql
     */
    public void addBatch(ParsedSql processResultSql) {

    }
}
