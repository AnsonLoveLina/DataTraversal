package cn.sinobest.traverse.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

/**
 * Created by zy-xx on 16/1/21.
 * 好吧，叫这个名字肯定要比较吊
 */
@Component
public class PreparedStatementCommiter {
    private static final Log logger = LogFactory.getLog(PreparedStatementCommiter.class);

    public PreparedStatement getPrepareStatement(String processResultSql) {
        return null;
    }

    public void addBatch(String processResultSql) {

    }
}
