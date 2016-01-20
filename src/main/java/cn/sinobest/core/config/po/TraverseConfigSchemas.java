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
public class TraverseConfigSchemas {
    @XmlElement(name = "data")
    private Set<TraverseConfigSchema> data;

    public Set<TraverseConfigSchema> getData() {
        return data;
    }

    public void setData(Set<TraverseConfigSchema> data) {
        this.data = data;
    }
}
