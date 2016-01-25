package cn.sinobest.traverse.analyzer.regular;

/**
 * Created by zhouyi1 on 2016/1/25 0025.
 */
public class DefaultRegularConvertor implements IRegularConvertor {
    @Override
    public String getRealRegex(String regex) {
        return regex;
    }

    @Override
    public String getRealValue(String bshid,String bshlx) {
        return bshid;
    }
}
