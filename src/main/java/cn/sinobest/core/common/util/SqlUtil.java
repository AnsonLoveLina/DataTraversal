package cn.sinobest.core.common.util;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.traverse.analyzer.regular.DefaultRegularConvertor;
import cn.sinobest.traverse.analyzer.regular.IRegularConvertor;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Created by zhouyi1 on 2016/1/21 0021.
 */
public class SqlUtil {
    private static final Log logger = LogFactory.getLog(SqlUtil.class);

    public static Object[] getParam(ParsedSql parsedSql,Map<String,String> paramMap){
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, new MapSqlParameterSource(paramMap), (List)null);
        return params;
    }

    public static IRegularConvertor getConvertor(String className){
        IRegularConvertor convertor;
        if (className!=null){
            try {
                Class convertorClass = ClassLoaderUtil.loadClass(className);
                try{
                    convertor = (IRegularConvertor) SpringContextInit.getContext().getBean(convertorClass);
                }catch (NoSuchBeanDefinitionException e){
                    convertor = (IRegularConvertor) ReflectUtil.newInstance(convertorClass);
                }
            } catch (ClassNotFoundException e) {
                logger.error(className+"can't find in classLoader!");
                convertor = new DefaultRegularConvertor();
            } catch (InstantiationException e) {
                e.printStackTrace();
                convertor = new DefaultRegularConvertor();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                convertor = new DefaultRegularConvertor();
            }
        }else{
            convertor = new DefaultRegularConvertor();
        }
        return convertor;
    }

    public static Pattern getPattern(String regex,IRegularConvertor convertor) throws NullPointerException{
        Preconditions.checkNotNull(regex);
        Preconditions.checkNotNull(convertor);
        String cpgz = regex;
        int startIndex = cpgz.indexOf('/')+1;
        int endIndex = cpgz.lastIndexOf('/');
        cpgz = convertor.getRealRegex(cpgz.substring(startIndex, endIndex));
        return Pattern.compile(cpgz);
    }

    /**
     * Convert.toString(Object value, String defaultValue)
     * @param clob
     * @return
     */
    @Deprecated
    public static String ClobToString(Clob clob) {
        if(clob==null){
            return "";
        }
        String reString = "";
        Reader is = null;
        try {
            is = clob.getCharacterStream();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(is);
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        while (s != null) {
            sb.append(s);
            try {
                s = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reString = sb.toString();
        return reString;
    }

    //初始化一个64的缓存
    private final static ConcurrentHashMap<String, Future> parsedSqlCache = new ConcurrentHashMap<String, Future>(64);

    /**
     * 经过改造之后可以适应NamedParameterUtils.parseSqlStatement(sql)较慢的情况
     * 并且如果在加入过程中出现并发也能避免
     * 对于执行过程中的异常处理暂时没有很好的想法就先null
     * @param sql
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static ParsedSql getParsedSql(final String sql) {
        if (StringUtil.isBlank(sql)){
            return null;
        }
        Future<ParsedSql> future = parsedSqlCache.get(sql);
        if (future==null){
            Callable<ParsedSql> parsedSqlCallable = new Callable<ParsedSql>() {
                @Override
                public ParsedSql call() throws Exception {
                    return NamedParameterUtils.parseSqlStatement(sql);
                }
            };
            FutureTask<ParsedSql> futureTask = new FutureTask<ParsedSql>(parsedSqlCallable);
            future = parsedSqlCache.putIfAbsent(sql, futureTask);
            if (future==null){
                future = futureTask;
                futureTask.run();
            }
        }
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
//        并发不够严谨
//        ParsedSql parsedSql = (ParsedSql) parsedSqlCache.get(sql);
//        if (parsedSql == null) {
//            parsedSql = NamedParameterUtils.parseSqlStatement(sql);
//            parsedSqlCache.put(sql, parsedSql);
//        }

//        return parsedSql;
    }

    public static String getSubstituteNamedParameters(ParsedSql sql){
        return NamedParameterUtils.substituteNamedParameters(sql,null);
    }

    @Deprecated
    public static Map<String, Object> getParamTemplate(Set<String> columns) throws Exception {
        if (columns.isEmpty()){
            throw new Exception("结果字段集合为空！适配器初始化出错！");
        }
        Map<String, Object> paramMapTemplate = Maps.asMap(columns, new Function<String, Object>() {
            @Override
            public Object apply(String s) {
                return null;
            }
        });
        return paramMapTemplate;
    }

    public static String getInsertSql(String resultTable,Set<String> resultColumns){
        resultColumns.remove("systemid");
        StringBuilder insertSql = new StringBuilder("insert into ");
        insertSql.append(resultTable);
        insertSql.append("(SYSTEMID,CREATEDTIME,LASTUPDATEDTIME,");
        insertSql.append(StringUtil.join(resultColumns, ","));
        insertSql.append(") values(getid(NULL),sysdate,sysdate,:");
        insertSql.append(StringUtil.join(resultColumns, ",:"));
        insertSql.append(")");
        return insertSql.toString();
    }

    public static Set<String> getColumns(String fullSql){
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
                columns.add(column.toLowerCase());
            }
        }
        return columns;
    }
}
