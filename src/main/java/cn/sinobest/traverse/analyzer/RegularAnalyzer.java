package cn.sinobest.traverse.analyzer;

import cn.sinobest.core.common.util.RegularAnalyzerUtil;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.core.config.po.AnalyzerColumn;
import cn.sinobest.core.config.schema.SqlSchemaer;
import cn.sinobest.traverse.analyzer.regular.IRegularConvertor;
import cn.sinobest.traverse.po.InsertParamObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jodd.bean.BeanTool;
import jodd.bean.BeanUtil;
import jodd.typeconverter.Convert;
import jodd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zy-xx on 16/1/24.
 * 每个SqlSchemaer有自己独立的Analyzer
 */
@Component(value = "regularAnalyzer")
@Scope(value = "prototype")
@Lazy
public class RegularAnalyzer implements IAnalyzer {
    private static final Log logger = LogFactory.getLog(RegularAnalyzer.class);

//    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private SqlSchemaer sqlSchemaer;

    private final String bshidName = "bshid";
    private final String bshlxName = "bshlx";

    public class PatternInfo{
        private String code;
        private IRegularConvertor convertor;
        private Pattern pattern;

        public PatternInfo(String code, IRegularConvertor convertor, Pattern pattern) {
            this.code = code;
            this.convertor = convertor;
            this.pattern = pattern;
        }

        public String getCode() {
            return code;
        }

        public IRegularConvertor getConvertor() {
            return convertor;
        }

        public Pattern getPattern() {
            return pattern;
        }
    }

    private LinkedList<PatternInfo> patterns = new LinkedList<PatternInfo>();
    private LinkedList<PatternInfo> noNumPatterns = new LinkedList<PatternInfo>();

    public RegularAnalyzer(SqlSchemaer sqlSchemaer) {
        Preconditions.checkNotNull(sqlSchemaer);
        Set<String> resultColumns = sqlSchemaer.getAnalyzerResultColumns();
        if (!resultColumns.contains(bshidName) || !resultColumns.contains(bshlxName)){
            logger.error("can't find the analyzerResultColumn："+bshidName+","+bshidName+" in sqlSchemaer："+sqlSchemaer.getSchemaName()+"!");
        }
        this.sqlSchemaer = sqlSchemaer;
    }

    @PostConstruct
    public void init(){
//        List<Map<String,Object>> regex = jdbcTemplate.queryForList(sqlSchemaer.getRegexSql());
//        List<Map<String,Object>> noNumRegex = jdbcTemplate.queryForList(sqlSchemaer.getNoNumberRegexSql());
        Map<String,Object> map1 = Maps.newHashMap();
        map1.put("CODE","001");
        map1.put("GZ","/\\d{18}|\\d{17}[x|X]|\\d{15}/");
//        map1.put("ANALYZERCLASS","cn.sinobest.traverse.analyzer.regular.SFZHRegularConvertor");
        Map<String,Object> map2 = Maps.newHashMap();
        map2.put("CODE","002");
        map2.put("GZ","/\\d{9}/");
        List<Map<String,Object>> regex = Lists.newArrayList(map2);
        List<Map<String,Object>> noNumRegex = Lists.newArrayList(map1);
        setPatterns(patterns,regex);
        setPatterns(noNumPatterns,noNumRegex);

    }

    private void setPatterns(LinkedList<PatternInfo> patternsTarget,List<Map<String,Object>> regexs){
        for (Map map:regexs){
            Object convertorClass = map.get("ANALYZERCLASS");
            IRegularConvertor convertor = SqlUtil.getConvertor(convertorClass==null?null:convertorClass.toString());
            Object regex = map.get("GZ");
            try{
                Object code = map.get("CODE");
                Preconditions.checkNotNull(code);
                Pattern pattern = SqlUtil.getPattern(regex == null ? null : regex.toString(), convertor);
                Preconditions.checkNotNull(pattern);
                PatternInfo patternInfo = new PatternInfo(code.toString(),convertor,pattern);
                patternsTarget.add(patternInfo);
            }catch (NullPointerException e){
                logger.error("code,regex or convertor can't be null!",e);
                continue;
            }

        }
    }

    private void patternAnalyzer(final Set<InsertParamObject> paramObjectsTarget,String analyzerSource,PatternInfo patternInfo,AnalyzerColumn analyzerColumn,boolean needMoreMatches){
        Preconditions.checkNotNull(analyzerSource);
        Preconditions.checkNotNull(patternInfo);
        Preconditions.checkNotNull(analyzerColumn);
        Pattern pattern = patternInfo.getPattern();
        IRegularConvertor convertor = patternInfo.getConvertor();
        Matcher matcher = pattern.matcher(analyzerSource);
        //needMoreMatches为true：while(matcher.find())；needMoreMatches为false：if(matcher.matches())
        for (boolean loop = true;needMoreMatches?matcher.find():(matcher.matches() && loop);loop = needMoreMatches){
            String bshid = needMoreMatches?matcher.group():analyzerSource;
            String bshlx = patternInfo.getCode();
            bshid = convertor.getRealValue(bshid,bshlx);
            if (bshid != null){
                HashMap<String,String> paramMap = Maps.newHashMap();
                paramMap.put(bshidName,bshid);
                paramMap.put(bshlxName,bshlx);
                paramObjectsTarget.add(new InsertParamObject(paramMap,analyzerColumn.toString()));
            }
        }
    }

    /**
     * 对于2个LinkedList采用迭代器遍历，如果集合在JUC环境外，线程不安全
     * @param analyzerSource
     * @param analyzerColumn
     * @return
     */
    @Override
    public Set<InsertParamObject> analyzerStr(String analyzerSource, AnalyzerColumn analyzerColumn) {
        Set<InsertParamObject> paramObjects = Sets.<InsertParamObject>newHashSet();
        LinkedList<PatternInfo> noNumPatterns = null;
        LinkedList<PatternInfo> patterns = null;
        //空间换时间
        synchronized (this.noNumPatterns){
            noNumPatterns = Lists.newLinkedList(this.noNumPatterns);
        }
        synchronized (this.patterns){
            patterns = Lists.newLinkedList(this.patterns);
        }
        if (StringUtil.isNotBlank(analyzerSource)){
            for (PatternInfo patternInfo:noNumPatterns){
                patternAnalyzer(paramObjects,analyzerSource,patternInfo,analyzerColumn,true);
            }

            if (!patterns.isEmpty()){
                List<String> beforeFilter = null;
                try {
                    beforeFilter = RegularAnalyzerUtil.toIntList(analyzerSource);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                for (String numSource:beforeFilter){
                    for (PatternInfo patternInfo:patterns){
                        patternAnalyzer(paramObjects,numSource,patternInfo,analyzerColumn,false);
                    }
                }
            }
        }
        return paramObjects;
    }
}
