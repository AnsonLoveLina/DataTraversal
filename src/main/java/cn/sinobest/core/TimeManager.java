package cn.sinobest.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
@Component
public class TimeManager {
    private static final Log logger = LogFactory.getLog(TimeManager.class);

    private String TIMESTAMP_COMMENT_JYAQ = "主要案情标识号分词时间戳";
    private String TIMESTAMP_KEY_ZYAQ = "AJJQ.ZYAQ";

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void init(String TIMESTAMP_COMMENT_JYAQ, String TIMESTAMP_KEY_ZYAQ) {
        this.TIMESTAMP_COMMENT_JYAQ = TIMESTAMP_COMMENT_JYAQ;
        this.TIMESTAMP_KEY_ZYAQ = TIMESTAMP_KEY_ZYAQ;
    }

    /**
     * 获取比对时间戳
     *
     * @return
     */
    public String getTimestamp() {
        try {
            String sql = "select to_char(decode(BACKUPTIMESTAMP,null,TIMESTAMP,BACKUPTIMESTAMP), 'YYYY-MM-DD HH24:MI:SS') TIMESTAMP from S_TIMESTAMP where KEY=?";
//            List list = DBUtil.queryForList(sql, new Object[]{this.TIMESTAMP_KEY_ZYAQ});
//            if(list != null && list.size() > 0) {
//                return ((Map)list.get(0)).get("TIMESTAMP") == null ? "" : ((Map)list.get(0)).get("TIMESTAMP").toString();
//            }
            String timestamp = jdbcTemplate.queryForObject(sql, String.class, this.TIMESTAMP_KEY_ZYAQ);
            return timestamp;
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY_ZYAQ + "获取比对时间戳出错！", e);
        }
        return "";
    }

    /**
     * 获取当前时间
     * 基本不会出错，所以没加异常处理
     * @return
     */
    public String getCurrentTime() {
        String sql = "select to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') as CURRENTTIME from dual";
//        List list = DBUtil.queryForList(sql, null);
//        if(list != null && list.size() > 0) {
//            return ((Map)list.get(0)).get("CURRENTTIME") == null ? "" : ((Map)list.get(0)).get("CURRENTTIME").toString();
//        }
        String currentTime = jdbcTemplate.queryForObject(sql, String.class);
        return currentTime;
    }

    /**
     * 插入比对时间戳
     */
    public String insertTimestamp() throws Exception {
        String currentTime = getCurrentTime();
        try {
            String sql = "insert into S_TIMESTAMP (KEY,TIMESTAMP,COMMENTS) values (?,to_date(?,'YYYY-MM-DD HH24:MI:SS'),?)";
//            DBUtil.update(sql, null, true, new Object[]{this.TIMESTAMP_KEY_ZYAQ, currentTime, this.TIMESTAMP_COMMENT_JYAQ});
            jdbcTemplate.update(sql,this.TIMESTAMP_KEY_ZYAQ,currentTime,this.TIMESTAMP_COMMENT_JYAQ);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY_ZYAQ + TIMESTAMP_COMMENT_JYAQ + "插入比对时间戳出错！", e);
        }
        return currentTime;
    }

    /**
     * 更新backup比对时间戳
     */
    public void overTimestamp() {
        try {
            String sql = "update S_TIMESTAMP set BACKUPTIMESTAMP=null where KEY=?";
//            DBUtil.update(sql, null, true, new Object[]{this.TIMESTAMP_KEY_ZYAQ});
            jdbcTemplate.update(sql,this.TIMESTAMP_KEY_ZYAQ);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY_ZYAQ + "更新backup比对时间戳出错！", e);
        }
    }

    /**
     * 更新比对时间戳
     */
    public void updateTimestamp(String backupTimestamp) {
        try {
            String sql = "update S_TIMESTAMP set TIMESTAMP=sysdate,BACKUPTIMESTAMP=to_date(?,'YYYY-MM-DD HH24:MI:SS') where KEY=?";
//            DBUtil.update(sql, null, true, new Object[]{backupTimestamp, this.TIMESTAMP_KEY_ZYAQ});
            jdbcTemplate.update(sql,backupTimestamp,this.TIMESTAMP_KEY_ZYAQ);
        } catch (Exception e) {
            logger.error(TIMESTAMP_KEY_ZYAQ + "更新比对时间戳出错！", e);
        }
    }


    /**
     * 获得唯一ID
     *
     * @return
     */
    public String getSysid() {
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
}
