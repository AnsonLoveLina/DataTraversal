package cn.sinobest.traverse.relolver;

import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.po.InsertParamObject;
import jodd.util.StringUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/25 0025.
 */
@Component(value = "expressRelolver")
public class ExpressRelolver implements IExpressRelolver{
    @Override
    public void explain(final InsertParamObject paramObject) {
        Set<AnalyzerColumn> analyzerColumns = paramObject.getAnalyzerColumns();
        Map<String,String> param = paramObject.getParamMap();
        //先随便取一个吧，本身有一定的逻辑错误
        AnalyzerColumn analyzerColumn = analyzerColumns.iterator().next();

        String specialExpress = analyzerColumn.getSpecialExpress();
        if (specialExpress==null){
            return;
        }
        String[] expressAndResult = StringUtil.split(specialExpress,"/");
        String express = expressAndResult[0];
        String result = expressAndResult[1];
        EvaluationContext context = new StandardEvaluationContext(param);
        ExpressionParser parser = new SpelExpressionParser();
        boolean passed = parser.parseExpression(express).getValue(context,boolean.class);
        if (passed){
            parser.parseExpression(result).getValue(context);
        }
    }
}
