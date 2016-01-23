package cn.sinobest.core.common.util;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SqlUtilTest {

    @Test
    public void testGetParsedSql() throws Exception {
        ParsedSql sql = SqlUtil.getParsedSql("select 1 from dual where 1=:name");
        String sqlStr = NamedParameterUtils.substituteNamedParameters(sql, null);
        HashMap hm = new HashMap();
        hm.put("id",5);
        hm.put("x",4);
        hm.put("name",1);
        hm.put("v",3);
        hm.put("b",2);
        Object[] obj = NamedParameterUtils.buildValueArray(sql, new MapSqlParameterSource(hm), (List) null);
        System.out.println(sql.toString());
        System.out.println(sqlStr);
        System.out.println("obj = " + obj.length);
    }
}