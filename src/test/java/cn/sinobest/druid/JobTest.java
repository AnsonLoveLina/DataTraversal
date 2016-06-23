package cn.sinobest.druid;

import cn.sinobest.core.config.po.Data;
import cn.sinobest.core.config.po.DetailQuery;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jodd.bean.BeanTool;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:applicationContext-testDb.xml"})
public class JobTest {

    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testDb.xml");
        try {
            String sql = "SELECT DISTINCT A.zjhm from t_bz_ck_ryjbxx b,t_bz_ly_gnlkxx a\n" +
                    "where a.rzsj>=to_char('2016-04-19')\n" +
                    "and a.rzsj<=to_char('2016-06-20')\n" +
                    "and a.gxdwbm like rtrim('520000','0')||'%'\n" +
                    "and a.zjhm=b.PID";
            JdbcTemplate jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    System.out.println(resultSet.getObject("AJBH"));
                }
            });
//            DataSource ds = (DataSource) applicationContext.getBean("dataSource");
//            Connection connection = ds.getConnection();
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next()){
//                System.out.println(rs.getObject("AJBH"));
//            }
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
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-testDb.xml");
        DataSource ds = (DataSource) applicationContext.getBean("dataSource");
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert into B_YPGJ_YPKJ_AJBSHGX(systemid,ajmc) values (?,?)");
            for (int i = 0; i < 99; i++) {
                ps.setObject(1,i);
                ps.setObject(2, "ajmc" + i);
                try {
                    ps.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            conn.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @Test
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

//    @Test
    public void test5(){
        boolean needMoreMatches = true;
        boolean loop = true;
        for (int i=0;needMoreMatches?false:(false && loop);loop = needMoreMatches,i++){
            System.out.println(i);
        }
    }

    class TaskRun implements Runnable{

        @Override
        public void run() {

        }
    }

    @Test
    public void test6(){
        Object[] os = new Object[]{1,2,3};
        System.out.println(Arrays.toString(os));
    }
}
