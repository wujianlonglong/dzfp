package sjes.dzfp.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class XmlUtils {

    public static String objectToXml(Object object, Class... clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);    // 获取上下文对象
        Marshaller marshaller = context.createMarshaller(); // 根据上下文获取marshaller对象
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");  // 设置编码字符集
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化XML输出，有分行和缩进
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(object, baos);
        String xmlObj = new String(baos.toByteArray());         // 生成XML字符串
        return xmlObj;
    }

    public static Object xmlToObject(String xml, Class... clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        Object object = unmarshaller.unmarshal(is);
        return object;
    }

}
