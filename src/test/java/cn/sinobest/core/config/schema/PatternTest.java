package cn.sinobest.core.config.schema;

import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.traverse.analyzer.regular.DefaultRegularConvertor;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zy-xx on 16/1/25.
 */
public class PatternTest {

    @Test
    public void test(){
        Pattern pattern = SqlUtil.getPattern("/^\\d{9}/", new DefaultRegularConvertor());
        String str = "326236882";
        Matcher matcher = pattern.matcher(str);
        while(matcher.matches()){
            System.out.println(str);
        }
    }
}
