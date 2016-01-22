package cn.sinobest.core.config.po;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/20 0020.
 */
@XmlRootElement(name = "datas")
@XmlAccessorType(value = XmlAccessType.NONE)
public class Datas {
    @XmlElement(name = "data")
    private Set<Data> data;

    public Set<Data> getData() {
        return data;
    }

    public void setData(Set<Data> data) {
        this.data = data;
    }
}
