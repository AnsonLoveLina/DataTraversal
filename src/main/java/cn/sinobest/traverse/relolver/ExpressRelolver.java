package cn.sinobest.traverse.relolver;

import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.traverse.po.InsertParamObject;
import jodd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
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
    private static final Log logger = LogFactory.getLog(ExpressRelolver.class);

    /**
     * 将paramObject里面的analyzerColumns全部解释好，并且将结果放回去
     * @param paramObject 不能并发共享
     */
    @Override
    public void explain(final InsertParamObject paramObject) {
        Set<AnalyzerColumn> analyzerColumns = paramObject.getAnalyzerColumns();
        Map<String,String> param = paramObject.getParamMap();
        /**
         * 假如2个表达式本身就存在冲突将随便取其中成为最终表达式
         */
        for (AnalyzerColumn analyzerColumn:analyzerColumns){

            String specialExpress = analyzerColumn.getSpecialExpress();
            if (specialExpress==null){
                return;
            }
            String[] expressAndResult = StringUtil.split(specialExpress,"/");
            String express = expressAndResult[0];
            String result = expressAndResult[1];
            EvaluationContext context = new StandardEvaluationContext(param);
            ExpressionParser parser = new SpelExpressionParser();
            try{
                boolean passed = parser.parseExpression(express).getValue(context,boolean.class);
                if (passed){
                    parser.parseExpression(result).getValue(context);
                }
            }catch (SpelEvaluationException e){
                logger.error(express+" or "+result+" is error!",e);
            }
        }

    }

}
