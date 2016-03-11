package cn.sinobest.core.config.schema;

import cn.sinobest.core.config.po.Data;
import jodd.bean.BeanTool;

/**
 * Created by zy-xx on 16/1/24.
 * 每个schema一个schemaer
 */
public class Schemaer {

    private SqlSchemaer fullSchemaer = new SqlSchemaer();

    private SqlSchemaer detailSchemaer = new SqlSchemaer();

    public Schemaer(Data data) {
        BeanTool.copy(data,fullSchemaer);
        fullSchemaer.setTraverseQuery(data.getFullQuery());
        fullSchemaer.parseAndSetOtherVar();
        BeanTool.copy(data, detailSchemaer);
        detailSchemaer.setTraverseQuery(data.getDetailQuery());
        fullSchemaer.parseAndSetOtherVar();
    }

    public SqlSchemaer getFullSchemaer() {
        return fullSchemaer;
    }

    public SqlSchemaer getDetailSchemaer() {
        return detailSchemaer;
    }

}
