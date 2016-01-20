package cn.sinobest.core.common.jaxb;

import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.config.po.TraverseConfigSchemas;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.*;

public class XmlObjectTest {

    @Test
    public void testToXml() throws Exception {

//        File file = new File("src\\main\\resources\\traverseConfig.xml");
//        JAXBContext jaxbContext = JAXBContext.newInstance(TraverseConfigSchemas.class);
//
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        TraverseConfigSchemas customer = (TraverseConfigSchemas) jaxbUnmarshaller.unmarshal(file);
//        System.out.println(customer);
    }
}