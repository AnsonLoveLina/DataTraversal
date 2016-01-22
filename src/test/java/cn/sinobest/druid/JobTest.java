package cn.sinobest.druid;

import cn.sinobest.core.config.po.Data;
import cn.sinobest.core.config.po.DetailQuery;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.traverse.handler.RowAnalyzerCallBackHandlerImpl;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jodd.bean.BeanTool;
import jodd.bean.BeanUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ArraysUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
public class JobTest {

//    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        try {
            String sql = "select 'zaj' as zymc,(select RESERVATION07 from b_asj_jq j where j.ajbh = t.ajbh and rownum=1) jqms,(select body from b_asj_bl b where b.ajbh = t.ajbh and rownum=1) blxx,systemid as ajid,zyaq,ajbh,ajmc,'99' rylx from B_ASJ_AJ t";
            JdbcTemplate jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    System.out.println(resultSet.getObject("AJMC"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void test1() {
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        Set<String> s = Sets.newHashSet("1", "2", "3");
        Set<Integers> sInt = Sets.newHashSet(new Integers(1));
        s.removeAll(sInt);
        for (String str:s){
            System.out.println("str = " + str);
        }
    }

    public class Integers{
        int i;

        public Integers(int i) {
            this.i = i;
        }

        @Override
        public int hashCode() {
            return Integer.toString(i).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return Integer.toString(i).equals(obj);
        }
    }

//    @Test
    public void test2(){
        String sql = "select 1 from dual where 1=:id";
        Map m = Maps.newHashMap();
        m.put("id","1");
        m.put("ids","1");
        String sqlNew = NamedParameterUtils.substituteNamedParameters(NamedParameterUtils.parseSqlStatement(sql),new MapSqlParameterSource(m));
        System.out.println("sqlNew = " + sqlNew);
        Object[] params = NamedParameterUtils.buildValueArray(NamedParameterUtils.parseSqlStatement(sql), new MapSqlParameterSource(m), (List)null);
        for (Object o:params){
            System.out.println("o = " + o);
        }
    }

//    @Test
    public void test3(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        DataSource ds = (DataSource) applicationContext.getBean("dataSource");
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert into B_YPGJ_YPKJ_AJBSHGX(systemid,ajmc) values (getid(null),?)");
            for (int i = 0; i < 99; i++) {
                ps.setObject(1,"ajmc"+i);
                ps.addBatch();

                if (i==80){
                    ps.executeBatch();
                    conn.commit();
                }
            }
            ps.executeBatch();
            conn.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4(){
        Data data = new Data();
        DetailQuery dq = new DetailQuery();
        dq.setDetailQuery("detailQuery");
        data.setDetailQuery(dq);
        data.setSchemaName("schemaName");

        TraverseConfigSchema schema = new TraverseConfigSchema();
//        BeanUtil.setPropertyForced(schema, "detailQuery", dq);
        BeanTool.copyProperties(data, schema, (String[]) null, true);
        System.out.println(schema.getSchemaName());
        System.out.println(schema.getDetailQuery());

    }
}
