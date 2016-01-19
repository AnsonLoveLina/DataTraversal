package cn.sinobest.core.config.po;

import java.util.Map;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class ResultColumn {

    private String columnName;
    private String resultText = "";

    public ResultColumn(String columnName, String resultText) {
        this.columnName = columnName;
        this.resultText = resultText;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}
