package cn.sinobest.druid;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class StatementHandlerTest {

    @Test
    public void testExecute() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        EvaluationContext context = new StandardEvaluationContext("1");
        ExpressionParser parser = new SpelExpressionParser();

        Object res = parser.parseExpression("toString()").getValue(context, String.class);
        System.out.println("res = " + res);
    }
}