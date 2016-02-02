package cn.sinobest.traverse.io;

import cn.sinobest.core.common.init.SpringContextInit;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by zy-xx on 16/1/27.
 */
@Component(value = "futurePreparedStatementCommiter")
@Scope("prototype")
@Lazy
public class FuturePreparedStatementCommiter implements IBatchCommiter {
    PreparedStatementCommiter commiter;

    public FuturePreparedStatementCommiter(String processResultSql) {
        this.commiter = (PreparedStatementCommiter) SpringContextInit.getBeanByAware("preparedStatementCommiter",processResultSql);
    }

    @Override
    public void setObjects(Object... params) {

    }

    @Override
    public void executeAndCommit() {

    }
}
