package cn.sinobest.core.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyi1 on 2016/1/25 0025.
 */
public class RegularAnalyzerUtil {

    /**
     * 将String解析成多个int
     * @param text
     * @return
     * @throws Exception
     */
    public static List<String> toIntList(String text) throws Exception{
        ArrayList<String> al = new ArrayList();
        text = text.trim();
        String tempText = "";
        boolean tempTextAddEnable = false;
        if(text != null && !"".equals(text)){
            for(int i=0;i<text.length();i++){
                if(text.charAt(i)>=48 && text.charAt(i)<=57){
                    tempText += text.charAt(i);
                    if(i==(text.length()-1)){
                        tempTextAddEnable = true;
                    }
                }else{
                    tempTextAddEnable = true;
                }
                if(tempTextAddEnable){
                    if(!"".equals(tempText)){
                        //System.out.println(tempText);
                        al.add(tempText);
                    }
                    tempText = "";
                    tempTextAddEnable = false;
                }
            }
        }
        return al;
    }
}
