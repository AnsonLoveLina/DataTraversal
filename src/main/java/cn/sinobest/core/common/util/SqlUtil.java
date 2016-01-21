package cn.sinobest.core.common.util;

import cn.sinobest.core.common.param.Param;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/21 0021.
 */
public class SqlUtil {

    public static String getInsertSql(String resultTable,Set<String> resultColumns){
        StringBuilder insertSql = new StringBuilder("insert into ");
        insertSql.append(resultTable);
        insertSql.append("(SYSTEMID,CREATEDTIME,LASTUPDATEDTIME,");
        insertSql.append(Param.BSHID);
        insertSql.append(",");
        insertSql.append(Param.BSHLX);
        insertSql.append(",");
        insertSql.append(StringUtil.join(resultColumns, ","));
        insertSql.append(") values(getid(NULL),sysdate,sysdate,:");
        insertSql.append(Param.BSHID);
        insertSql.append(",:");
        insertSql.append(Param.BSHID);
        insertSql.append(",:");
        insertSql.append(StringUtil.join(resultColumns, ",:"));
        insertSql.append(")");
        return insertSql.toString();
    }

    public static Set<String> processColumns(String fullSql){
        String splitStr = "@";
        if(fullSql.indexOf("select")<0)
            return new HashSet<String>();
        String fullColumns = fullSql.substring(fullSql.indexOf("select")+6,fullSql.lastIndexOf(" from "));
        fullColumns = " "+fullColumns.trim();
        String newFullColumns = "";
        boolean state = true;
        for(int i=0;i<fullColumns.length();i++){
//            if(columns==null)
//            columns = new ArrayList<String>();
            switch(fullColumns.charAt(i)){
                case 40:
                    state = false;
                    break;
                case 41:
                    state = true;
                    break;
                case 44:
                    if(state){
                        newFullColumns += splitStr;
                    }
                    break;
                default:
                    newFullColumns += fullColumns.substring(i,i+1);
                    break;
            }

//            String column = fullColumns[i].trim().replaceAll("\\S*\\s","").toUpperCase();
        }

        return processColumnsStr(newFullColumns,splitStr);
    }

    private static Set<String> processColumnsStr(String fullSql,String splitStr){
//        System.out.println("fullSql:"+fullSql);
        Set<String> columns = new HashSet<String>();
        String[] fullColumns = fullSql.split(splitStr);
        for(int i=0;i<fullColumns.length;i++){
            String column = fullColumns[i].trim().replaceAll("\\S*\\s","").toUpperCase();
            if(StringUtil.isNotBlank(column)){
                columns.add(column);
            }
        }
        return columns;
    }
}
