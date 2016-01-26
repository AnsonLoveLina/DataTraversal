package cn.sinobest.traverse.analyzer.regular;

/**
 * sqlSchemaer.getRegexSql():iRegularConvertor = 1:*
 * sqlSchemaer.getNoRegexSql():iRegularConvertor = 1:*
 * 每个bshlx的正则表达式对应自己独立的Convertor对象
 * Created by zy-xx on 16/1/25.
 */
public class SFZHRegularConvertor implements IRegularConvertor {
    @Override
    public String getRealRegex(String regex) {
        return "("+regex+")\\d*";
    }

    /**
     *
     * @param bshid
     * @param bshlx 通过这里的bshlx区分，避免多个bshlx的正则表达式配置了一个convertor类
     * @return
     */
    @Override
    public String getRealValue(String bshid, String bshlx) {
        if ((bshid.length()==18 || bshid.length()==15) && CheckSfzh.validate(bshid)) {
            return bshid;
        }
        return null;
    }
}
