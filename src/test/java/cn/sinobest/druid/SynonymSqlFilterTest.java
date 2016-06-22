package cn.sinobest.druid;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SynonymSqlFilterTest {


    String oldSql ="SELECT DISTINCT a.idcardno,\n" +
            "                'f6405395-c5f5-48c8-9644-4fba4d10d302',\n" +
            "                'PCS3707201606200000000000045099'\n" +
            "  FROM t_bz_asj_ajxyr a\n" +
            " WHERE a.inputtime >= to_date(to_char('2016-04-19'), 'yyyy-mm-dd')\n" +
            "   and a.inputtime <= to_date(to_char('2016-06-20'), 'yyyy-mm-dd')\n" +
            "   and a.door not like rtrim('520000', '0') || '%'\n" +
            "   and not exists (select b.zjhm\n" +
            "          from t_bz_sqjw_syrk_ryxxb_ldrk b\n" +
            "         where b.zjhm = a.idcardno)";
    String tempSql = "SELECT DISTINCT a.idcardno,\n" +
            "                'f6405395-c5f5-48c8-9644-4fba4d10d302',\n" +
            "                'PCS3707201606200000000000045099'\n" +
            "  FROM t_bz_asj_ajxyr a\n" +
            " WHERE a.inputtime >= to_date(to_char(:inputtimeStart@), 'yyyy-mm-dd')\n" +
            "   and a.inputtime <= to_date(to_char(:inputtimeEnd@), 'yyyy-mm-dd')\n" +
            "   and a.door not like rtrim(:door@, '0') || '%'\n" +
            "   and not exists (select b.zjhm\n" +
            "          from t_bz_sqjw_syrk_ryxxb_ldrk b\n" +
            "         where b.zjhm = a.idcardno@)";
    String newSqlTemp = "   SELECT    idcardno,\n" +
            "                'f6405395-c5f5-48c8-9644-4fba4d10d302',\n" +
            "                'PCS3707201606200000000000045099' from \n" +
            "            (SELECT distinct  a.idcardno\n" +
            "   FROM t_bz_asj_ajxyr a\n" +
            "  WHERE a.inputtime >= to_date(:inputtimeStart@, 'yyyy-mm-dd')\n" +
            "    and a.inputtime <= to_date(:inputtimeStart@, 'yyyy-mm-dd')\n" +
            "    and a.door not like rtrim(:door@, '0') || '%'\n" +
            "    and not exists (select b.zjhm\n" +
            "          from t_bz_sqjw_syrk_ryxxb_ldrk b\n" +
            "         where b.zjhm::bpchar = a.idcardno::bpchar)\n" +
            "       );     \n";

    @Test
    public void regex(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        SynonymSqlFilter filter = (SynonymSqlFilter) applicationContext.getBean("synSqlFilter");
        String regex = filter.getContext().getSynonymTable().entrySet().iterator().next().getKey().get(1);
        String sql = "SELECT DISTINCT a.idcardno, 'f6405395-c5f5-48c8-9644-4fba4d10d302', 'PCS3707201606200000000000045099' FROM t_bz_asj_ajxyr a WHERE a.inputtime >= to_date(to_char('2016-04-19'), 'yyyy-mm-dd') and a.inputtime <= to_date(to_char('2016-06-20'), 'yyyy-mm-dd') and a.door not like rtrim('520000', '0') || '%' and not exists (select b.zjhm from t_bz_sqjw_syrk_ryxxb_ldrk b where b.zjhm = a.idcardno)";
        System.out.println("regex = " + regex);
        System.out.println("sql = " + sql);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);
        System.out.println(matcher.matches());
    }

    @Test
    public void testGetNewSql() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        SynonymSqlFilter filter = (SynonymSqlFilter) applicationContext.getBean("synSqlFilter");
        String oldSql = filter.getContext().getSynonymTable().values().iterator().next();
        oldSql = oldSql.replaceAll("\\s*\\n\\s*"," ");
        System.out.println("oldSql = " + oldSql);
    }

    @Test
    public void test(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        SynonymSqlFilter filter = (SynonymSqlFilter) applicationContext.getBean("synSqlFilter");
        Map<String,String> params = SqlUtil.getParams(tempSql, oldSql,filter.getContext().getCharSet());
        System.out.println("params = " + params.size());
        String newSql = SqlUtil.getNewSql(newSqlTemp,params,oldSql);
        System.out.println("newSql = " + newSql);
    }

    @Test
    public void testgetNormSql(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        SynonymSqlFilter filter = (SynonymSqlFilter) applicationContext.getBean("synSqlFilter");
        String sql = filter.getContext().getSynonymTable().values().iterator().next();
        System.out.println("sql = " + sql);
        String newSql = SqlUtil.getNormSql(sql);
        System.out.println("newSql = " + newSql);
    }

    @Test
    public void testGetParams() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testSynonym.xml");
        SynonymSqlFilter filter = (SynonymSqlFilter) applicationContext.getBean("synSqlFilter");
//        String newSql = filter.getSyn(oldSql);
//        System.out.println("newSql = " + newSql);
    }
}