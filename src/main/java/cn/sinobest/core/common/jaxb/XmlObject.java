package cn.sinobest.core.common.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;

/**
 * Created by chenjianhua on 2015/9/8 0008.
 */
public abstract class XmlObject<T extends XmlObject> {
    /**
     * 业务代码
     *
     * @return
     */
//    @XmlAttribute(name = "ywdm")
//    public abstract String getYwdm();

    /**
     * 转为xml
     *
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public String toXml() throws JAXBException {
        return toXml(false);
    }

    /**
     * 转为xml
     *
     * @param isPretty 是否输出漂亮的格式
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public String toXml(boolean isPretty) throws JAXBException {
        //writer，用于保存XML内容
        StringWriter writer = new StringWriter();
        //获取真实的类类型
        Class<T> actualClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        //获取一个关于Customer类的 JAXB 对象
        JAXBContext context = JAXBContext.newInstance(actualClass);
        //由  Jaxbcontext 得到一个Marshaller（马歇尔）
        Marshaller marshaller = context.createMarshaller();
        //设置为格式化输出，就是XML自动格式化。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isPretty);
        //使用marshaller将对象输出到writer。
        marshaller.marshal(this, writer);
        //writer.toString()，将所有写入的内容转成String
        return writer.toString();
    }
}
