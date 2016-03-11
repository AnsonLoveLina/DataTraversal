package cn.sinobest.core.stamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@Component
@Scope(value = "prototype")
public class TimeStampManager implements StampManager {
    private static final Log logger = LogFactory.getLog(TimeStampManager.class);
    private final String currentTimeSql = "select to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') as CURRENTTIME from dual";
    private final String getTimeStampSql = "select to_char(decode(JOBFINISHED,'1',TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY=?";
    private final String incrementSql = "update S_TIMESTAMP set TIMESTAMP=sysdate,BACKUPTIMESTAMP=to_date(?,'YYYY-MM-DD HH24:MI:SS') where KEY=?";
//    private final String incrementFinallySql = "update S_TIMESTAMP set JOBFINISHED='1' where KEY=?";
    //全量先不管异常情况
    private final String completeSql = "insert into S_TIMESTAMP (KEY,TIMESTAMP,COMMENTS,JOBFINISHED) values (?,to_date(?,'YYYY-MM-DD HH24:MI:SS'),?,'1')";

    private String TIMESTAMP_COMMENT = "默认时间戳";
    private String TIMESTAMP_KEY = "DefaultKey";

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取当前时间
     * 基本不会出错，所以没加异常处理
     * @return
     */
    private String getCurrentTime() throws DataAccessException {
        String currentTime = jdbcTemplate.queryForObject(currentTimeSql, String.class);
        return currentTime;
    }

    private String lastTime = "";

    @Override
    public void init(String TIMESTAMP_COMMENT, String TIMESTAMP_KEY) {
        this.TIMESTAMP_COMMENT = TIMESTAMP_COMMENT;
        this.TIMESTAMP_KEY = TIMESTAMP_KEY;
    }

    @Override
    public Object getIncrementIdenti(){
        return lastTime;
    }

    @Override
    public boolean isComplete() {
        try {
            lastTime = jdbcTemplate.queryForObject(getTimeStampSql, String.class, this.TIMESTAMP_KEY);
        } catch (EmptyResultDataAccessException e) {
            logger.info(TIMESTAMP_KEY + "时间戳不存在将采用全量！");
            return true;
        }
        if (lastTime==null){
            lastTime = "";
        }
        return false;
    }

    @Override
    public void increment() {
        try {
            jdbcTemplate.update(incrementSql,lastTime,this.TIMESTAMP_KEY);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY + "更新比对时间戳出错！", e);
            throw e;
        }
    }

//    @Override
//    public void incrementFinally() {
//        try {
//            jdbcTemplate.update(incrementFinallySql,this.TIMESTAMP_KEY);
//        } catch (Exception e) {
//            logger.error(TIMESTAMP_KEY + "更新backup比对时间戳出错！", e);
//            throw e;
//        }
//    }

    @Override
    public void complete() {
        try {
            String currentTime = getCurrentTime();
            jdbcTemplate.update(completeSql, this.TIMESTAMP_KEY, currentTime, this.TIMESTAMP_COMMENT);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY + TIMESTAMP_COMMENT + "插入比对时间戳出错！", e);
            throw e;
        }
    }
}
