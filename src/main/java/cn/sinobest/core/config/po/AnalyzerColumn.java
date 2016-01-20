package cn.sinobest.core.config.po;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by zhouyi1 on 2016/1/20 0020.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AnalyzerColumn {
    @XmlValue
    private String analyzerColumn;

    @XmlAttribute(name = "specialExpress")
    private String specialExpress;

    @Override
    public String toString() {
        return analyzerColumn.toString();
    }

    public void setAnalyzerColumn(String analyzerColumn) {
        this.analyzerColumn = analyzerColumn;
    }

    public String getSpecialExpress() {
        return specialExpress;
    }

    public void setSpecialExpress(String specialExpress) {
        this.specialExpress = specialExpress;
    }
}
