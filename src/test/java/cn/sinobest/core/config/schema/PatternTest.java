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
        Pattern pattern = SqlUtil.getPattern("/\\d{18}/", new DefaultRegularConvertor());
        String str = "周毅身份证： 430102198704020515 qq号码：326236882 还有一些其他信息？";
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            System.out.println(matcher.group());
        }
    }
}
