package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.traverse.io.IBatchCommiter;
import cn.sinobest.traverse.io.PreparedStatementCommiter;
import org.springframework.jdbc.core.namedparam.ParsedSql;

/**
 * Created by zhouyi1 on 2016/1/27 0027.
 */
public class ResultSql {
    private ParsedSql resultSql;
    private IBatchCommiter resultSqlCommiter;

    public ResultSql(String resultSqlStr) {
        this.resultSql = SqlUtil.getParsedSql(resultSqlStr);
        resultSqlCommiter = (IBatchCommiter) SpringContextInit.getBeanByAware("preparedStatementCommiter",this.resultSql);
    }

    public ParsedSql getResultSql() {
        return resultSql;
    }

    public IBatchCommiter getResultSqlCommiter() {
        return resultSqlCommiter;
    }
}
