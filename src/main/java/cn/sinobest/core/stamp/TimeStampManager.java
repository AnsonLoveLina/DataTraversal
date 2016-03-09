package cn.sinobest.core.stamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
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

    private String TIMESTAMP_COMMENT = "默认时间戳";
    private String TIMESTAMP_KEY = "DefaultKey";

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取比对时间戳
     *
     * @return
     */
    private String getTimestamp() throws EmptyResultDataAccessException{
//        try {
            String sql = "select to_char(decode(BACKUPTIMESTAMP,null,TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY=?";
//            String sql = "select to_char(TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY=?";
            String timestamp = jdbcTemplate.queryForObject(sql, String.class, this.TIMESTAMP_KEY);
            return timestamp;
//        } catch (EmptyResultDataAccessException e){
//            logger.trace(TIMESTAMP_KEY + "时间戳不存在将采用全量！");
//            return "";
//        }
    }

    /**
     * 获取当前时间
     * 基本不会出错，所以没加异常处理
     * @return
     */
    private String getCurrentTime() throws Exception{
        String sql = "select to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') as CURRENTTIME from dual";
        String currentTime = jdbcTemplate.queryForObject(sql, String.class);
        return currentTime;
    }

    /**
     * 插入比对时间戳
     */
    public void insertTimestamp() throws Exception {
        String currentTime = getCurrentTime();
        try {
            String sql = "insert into S_TIMESTAMP (KEY,TIMESTAMP,COMMENTS) values (?,to_date(?,'YYYY-MM-DD HH24:MI:SS'),?)";
//            DBUtil.update(sql, null, true, new Object[]{this.TIMESTAMP_KEY, currentTime, this.TIMESTAMP_COMMENT});
            jdbcTemplate.update(sql,this.TIMESTAMP_KEY,currentTime,this.TIMESTAMP_COMMENT);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY + TIMESTAMP_COMMENT + "插入比对时间戳出错！", e);
            throw e;
        }
//        return currentTime;
    }

    /**
     * 更新backup比对时间戳
     */
    public void overTimestamp() {
        try {
            String sql = "update S_TIMESTAMP set BACKUPTIMESTAMP=null where KEY=?";
//            DBUtil.update(sql, null, true, new Object[]{this.TIMESTAMP_KEY});
            jdbcTemplate.update(sql,this.TIMESTAMP_KEY);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY + "更新backup比对时间戳出错！", e);
            throw e;
        }
    }

    /**
     * 更新比对时间戳
     */
    public void updateTimestamp(String backupTimestamp) {
        try {
//            String sql = "update S_TIMESTAMP set TIMESTAMP=sysdate where KEY=?";
//            jdbcTemplate.update(sql,this.TIMESTAMP_KEY);
            String sql = "update S_TIMESTAMP set TIMESTAMP=sysdate,BACKUPTIMESTAMP=to_date(?,'YYYY-MM-DD HH24:MI:SS') where KEY=?";
            jdbcTemplate.update(sql,backupTimestamp,this.TIMESTAMP_KEY);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY + "更新比对时间戳出错！", e);
            throw e;
        }
    }


    /**
     * 获得唯一ID
     *
     * @return
     */
    private String getSysid() {
        try {
            String sql = "select getid('') ID from dual";
//            List list = DBUtil.queryForList(sql, null);
//            if (list != null && list.size() > 0) {
//                return ((Map) list.get(0)).get("ID") == null ? "" : ((Map) list.get(0)).get("ID").toString();
//            }
            String sysId = jdbcTemplate.queryForObject(sql,String.class);
            return sysId;
        } catch (Exception e) {
            logger.error("获取唯一ID出错！", e);
        }
        return "";
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
            lastTime = getTimestamp();
        } catch (Exception e) {
            logger.info(TIMESTAMP_KEY + "时间戳不存在将采用全量！");
            return true;
        }
        return false;
    }

    @Override
    public void incrementBefore() {
        updateTimestamp(lastTime);
    }

    @Override
    public void incrementFinally() {
        overTimestamp();
    }

    @Override
    public void completeBefore() {
        try {
            insertTimestamp();
        } catch (Exception e) {
            logger.error("时间戳或运行语句出错！", e);
        }
    }
}
