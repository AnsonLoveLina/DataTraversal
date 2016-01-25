package cn.sinobest.traverse.analyzer.regular;

/**
 * Created by zy-xx on 16/1/25.
 */
public class SFZHRegularConvertor implements IRegularConvertor {
    @Override
    public String getRealRegex(String regex) {
        return "("+regex+")\\d*";
    }

    @Override
    public String getRealValue(String bshid, String bshlx) {
        if (bshid.length()==18 || bshid.length()==15){
            return bshid;
        }
        return null;
    }
}
