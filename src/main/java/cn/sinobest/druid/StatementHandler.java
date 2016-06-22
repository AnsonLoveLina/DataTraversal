package cn.sinobest.druid;

import jodd.util.StringUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyi1 on 2016/6/22 0022.
 */
public class StatementHandler {
    public void execute(Statement statement,Map.Entry<List<String>,String> sqlConfigEntry){
        if (sqlConfigEntry.getKey().size()<3){
            return;
        }
        String express = sqlConfigEntry.getKey().get(2);
        if (StringUtil.isBlank(express)){
            return;
        }
        EvaluationContext context = new StandardEvaluationContext(statement);
        ExpressionParser parser = new SpelExpressionParser();

        parser.parseExpression(express).getValue(context,null);
    }
}
