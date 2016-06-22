package cn.sinobest.druid;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouyi1 on 2016/6/21 0021.
 */
public class SynonymSqlFilter extends FilterAdapter implements Filter {
    private static final Log logger = LogFactory.getLog(SynonymSqlFilter.class);

//    static{
//        System.out.println("SynonymSqlFilter");
//    }

    private SynonymSqlContext context;

    public String getSyn(String oldSql,StatementProxy statement){
        Preconditions.checkNotNull(oldSql);
        String newSql = null;
        Map<List<String>,String> synTable = context.getSynonymTable();
        if(synTable.isEmpty()){
            return newSql;
        }
        oldSql = SqlUtil.getNormSql(oldSql);
        for (Map.Entry<List<String>,String> entry:synTable.entrySet()){
            List<String> key = entry.getKey();
            String sqlTemp = key.get(0);
            String sqlRegex = key.get(1);

            Pattern pattern = Pattern.compile(sqlRegex);
            Matcher matcher = pattern.matcher(oldSql);
            if (matcher.matches()){
                Map<String,String> params = SqlUtil.getParams(sqlTemp, oldSql, context.getCharSet());
                String newSqlTemp = entry.getValue();
                newSql = SqlUtil.getNewSql(newSqlTemp, params, oldSql);

                StatementHandler handler = new StatementHandler();
                handler.execute(statement,entry);
                break;
            }
        }
        return newSql;
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
//        System.out.println("statement_executeQuery()");
        long start = System.currentTimeMillis();
        String oldSql = sql;
        String newSql = getSyn(oldSql,statement);
        if (newSql!=null){
            logger.trace("oldSql = " + oldSql);
            logger.trace("newSql = " + newSql);
            logger.trace("spendTime = "+((System.currentTimeMillis()-start)/1000)+" s");
            return super.statement_executeQuery(chain, statement, newSql);
        }
        return super.statement_executeQuery(chain, statement, sql);
    }

    public SynonymSqlContext getContext() {
        return context;
    }

    public void setContext(SynonymSqlContext context) {
        this.context = context;
    }
}
