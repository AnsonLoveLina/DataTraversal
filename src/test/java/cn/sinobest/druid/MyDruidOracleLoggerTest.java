package cn.sinobest.druid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class MyDruidOracleLoggerTest {

    @Resource(name = "dataSource")
    DataSource ds;
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public class A{
        long a = 0l;
        long b = 1l;

        public long getA() {
            return a;
        }

        public void setA(long a) {
            this.a = a;
        }

        public long getB() {
            return b;
        }

        public void setB(long b) {
            this.b = b;
        }
    }

//    @Test
    public void testNamedParameterJdbcTemplate(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
        A model = new A();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(model);
        namedParameterJdbcTemplate.update("insert into B_DRUID_CONFIG(systemid,EXECUTECOUNTMAX) values (getid(null),:a)",paramSource );

        System.out.println(0%2);
    }

//    @Test
    public void testJdbcTemplate(){
        String x = jdbcTemplate.queryForObject("select to_char(decode(BACKUPTIMESTAMP,null,TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY='AJJQ.ZYAQXYR'",String.class);
        System.out.println("x = " + x);
    }

    @Test
    public void testJdbcTemplateConcurrent(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        Long l = System.currentTimeMillis();
        for (int i = 0; i < 40; i++) {
            executorService.execute(new RunnExecute());
        }
        System.out.println(System.currentTimeMillis()-l);
        executorService.shutdown();
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class RunnExecute implements Runnable{

        @Override
        public void run() {
            System.out.println("runnStart!");
            execute();
            System.out.println("runnOver!");
        }
        public void execute(){
            String x = jdbcTemplate.queryForObject("select to_char(decode(BACKUPTIMESTAMP,null,TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY='AJJQ.ZYAQXYR'",String.class);
            String x1 = jdbcTemplate.queryForObject("select to_char(decode(BACKUPTIMESTAMP,null,TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY='AJJQ.ZYAQXYR'",String.class);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jdbcTemplate.update("insert into B_YPGJ_YPKJ_AJBSHGX(systemid,ajid) values (getid(null),'1')");
        }
    }
}