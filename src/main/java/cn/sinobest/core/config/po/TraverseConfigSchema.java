package cn.sinobest.core.config.po;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.common.jaxb.XmlObject;
import cn.sinobest.core.common.param.Param;
import cn.sinobest.core.common.util.SqlUtil;
import cn.sinobest.traverse.adapter.CallBackAdapter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 * 针对schema的这些扩展字段，扩展adapter和扩展字段以及adapter的初始化如果有空需要用一个proxy进行封装
 */
@XmlAccessorType(value = XmlAccessType.NONE)
public class TraverseConfigSchema extends Data {
    private static final Log logger = LogFactory.getLog(TraverseConfigSchema.class);
    private Set<String> columns = new HashSet<String>();

    private ParsedSql insertSql;

    private ParsedSql detailSql;

    private ParsedSql fullSql;

    //在set的时候做封装columns这件事是因为getInsertSql要用到
    @Override
    public void setFullQuery(FullQuery fullQuery){
        this.columns.addAll(SqlUtil.getColumns(fullQuery.toString()));

        this.columns.add(Param.BSHID.toString().toLowerCase());
        this.columns.add(Param.BSHLX.toString().toLowerCase());
    }

    @Override
    public void setDetailQuery(DetailQuery detailQuery) {
        this.columns.addAll(SqlUtil.getColumns(detailQuery.toString()));

        this.columns.add(Param.BSHID.toString().toLowerCase());
        this.columns.add(Param.BSHLX.toString().toLowerCase());
    }

    public ParsedSql getInsertSql() {
        if (insertSql == null && !columns.isEmpty()){
            ImmutableMap<String, Object> paramMapTemplate = ImmutableMap.of();

            Set<String> resultStrColumns = this.columns;
            if(!analyzerColumns.isEmpty())
                resultStrColumns.removeAll(analyzerColumns);
            resultStrColumns.remove("SYSTEMID");

            try {
                paramMapTemplate = SqlUtil.getParamTemplate(columns);
            } catch (Exception e) {
                logger.error("结果字段集合为空！适配器初始化出错！",e);
            }

            String insertSqlSource = SqlUtil.getInsertSql(resultTable,resultStrColumns);
            String insertSqlNew = NamedParameterUtils.substituteNamedParameters(SqlUtil.getParsedSql(insertSqlSource), new MapSqlParameterSource(paramMapTemplate));
            this.insertSql = SqlUtil.getParsedSql(insertSqlNew);
        }
        return insertSql;
    }

    public ParsedSql getDetailSql() {
        if (detailSql == null){
            detailSql = SqlUtil.getParsedSql(detailQuery.toString());
        }
        return detailSql;
    }

    public ParsedSql getFullSql() {
        if (fullSql == null){
            fullSql = SqlUtil.getParsedSql(fullQuery.toString());
        }
        return fullSql;
    }

    public Set<String> getColumns() {
        return columns;
    }

}
