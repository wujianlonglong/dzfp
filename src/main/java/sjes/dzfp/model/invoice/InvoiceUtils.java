package sjes.dzfp.model.invoice;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import sjes.dzfp.model.invoice.invoiceFlushRed.InvoiceFlushRedReq;
import sjes.dzfp.model.invoice.invoiceFlushRed.InvoiceFlushRedRes;
import sjes.dzfp.model.invoice.invoiceMail.InvoiceMailReq;
import sjes.dzfp.model.invoice.invoiceMail.InvoiceMailRes;
import sjes.dzfp.model.invoice.invoiceOpen.InvoiceOpenRes;
import sjes.dzfp.model.invoice.invoiceSearch.InvoiceSearchReq;
import sjes.dzfp.model.invoice.invoiceSearch.InvoiceSearchRes;
import sjes.dzfp.model.invoice.orderSerach.OrderSearchRes;
import sjes.dzfp.model.invoice.orderSerach.REQUEST_FPKJXX;
import sjes.dzfp.utils.Base64Utils;
import sjes.dzfp.utils.XmlUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class InvoiceUtils {

    private final static String url = "http://193.0.10.230:8121/DZFP";

        private final static String orderSearchUrl = "http://193.0.10.228:8088/webservice/dzfp?wsdl";
//    private final static String orderSearchUrl = "http://weixin-dev.sanjiang.com/webservice/dzfp?wsdl";

    private static String version = "1.0";

    private static String jskp = "DZFPKHDSC";

    private static String fpcx = "DZFPCX";

    private static String fpch = "DZFPCH";

    private static String dzyx = "DZFPEMAIL";

    /**
     * 发票查询
     *
     * @param invoiceSearch
     * @return
     * @throws JAXBException
     */
    public static InvoiceSearchRes invoiceSerach(InvoiceSearchReq invoiceSearch) throws JAXBException, IOException {
        BodyModel body = new BodyModel();
        GlobalInfo globalInfo = new GlobalInfo(version, fpcx);
        body.setGlobalInfo(globalInfo);
        ContentData data = new ContentData();
        String requetBodyXml = XmlUtils.objectToXml(invoiceSearch, InvoiceSearchReq.class);
        requetBodyXml = Base64Utils.getBase64(requetBodyXml);
        data.setContent(requetBodyXml);
        body.setData(data);

        String requetAllBodyXml = XmlUtils.objectToXml(body, BodyModel.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity entity = new HttpEntity(requetAllBodyXml, headers);
        RestTemplate restTemplate = new RestTemplate();
        InvoiceSearchRes responseBody = restTemplate.postForObject(url, entity, InvoiceSearchRes.class);
//        if (responseBody.getOUTSTRS() != null && responseBody.getOUTSTRS().getOUTSTR() != null) {
//            String pic = responseBody.getOUTSTRS().getOUTSTR().getJPG_FILE();
//            String pdf = responseBody.getOUTSTRS().getOUTSTR().getPDF_FILE();
//            if (StringUtils.isNotEmpty(pic))
//                Base64Utils.DecodeToFile(pic);
//        }

        return responseBody;
    }


    /**
     * 发票冲红
     *
     * @param invoiceFlushRedReq
     * @return
     * @throws JAXBException
     */
    public static InvoiceFlushRedRes invoiceFlushRed(InvoiceFlushRedReq invoiceFlushRedReq) throws JAXBException {
        BodyModel body = new BodyModel();
        GlobalInfo globalInfo = new GlobalInfo(version, fpch);
        body.setGlobalInfo(globalInfo);
        ContentData data = new ContentData();
        String requetBodyXml = XmlUtils.objectToXml(invoiceFlushRedReq, InvoiceFlushRedReq.class);
        requetBodyXml = Base64Utils.getBase64(requetBodyXml);
        data.setContent(requetBodyXml);
        body.setData(data);

        String requetAllBodyXml = XmlUtils.objectToXml(body, BodyModel.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity entity = new HttpEntity(requetAllBodyXml, headers);
        RestTemplate restTemplate = new RestTemplate();
        InvoiceFlushRedRes responseBody = restTemplate.postForObject(url, entity, InvoiceFlushRedRes.class);
        return responseBody;
    }


    public static OrderSearchRes txdOrderSerachDetail(String orderNo, String orderDate) throws Exception {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        org.apache.cxf.endpoint.Client client = dcf.createClient(orderSearchUrl);
        Object[] objects = client.invoke("getSaleInfo", new Object[]{"2", "", "", orderNo, orderDate});
        String data = objects[0].toString();
        BodyModel body = (BodyModel) XmlUtils.xmlToObject(data, BodyModel.class);
        String content = (String) body.getData().getContent();
        String deContent = Base64Utils.getFromBase64(content);
        OrderSearchRes res = (OrderSearchRes) XmlUtils.xmlToObject(deContent, OrderSearchRes.class);
        return res;
    }

    public static OrderSearchRes otherOrderSerachDetail(String shopNo, String posNo, String streamNo, String orderDate) throws Exception {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        org.apache.cxf.endpoint.Client client = dcf.createClient(orderSearchUrl);
        Object[] objects = client.invoke("getSaleInfo", new Object[]{"1", shopNo, posNo, streamNo, orderDate});
        String data = objects[0].toString();
        BodyModel body = (BodyModel) XmlUtils.xmlToObject(data, BodyModel.class);
        String content = (String) body.getData().getContent();
        String deContent = Base64Utils.getFromBase64(content);
        OrderSearchRes res = (OrderSearchRes) XmlUtils.xmlToObject(deContent, OrderSearchRes.class);
        return res;
    }

    public static InvoiceOpenRes invoiceOpen(REQUEST_FPKJXX REQUEST_FPKJXX) throws JAXBException {
        BodyModel body = new BodyModel();
        GlobalInfo globalInfo = new GlobalInfo(version, jskp);
        body.setGlobalInfo(globalInfo);
        ContentData data = new ContentData();
        String requetBodyXml = XmlUtils.objectToXml(REQUEST_FPKJXX, REQUEST_FPKJXX.class);
        requetBodyXml = Base64Utils.getBase64(requetBodyXml);
        data.setContent(requetBodyXml);
        body.setData(data);

        String requetAllBodyXml = XmlUtils.objectToXml(body, BodyModel.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity entity = new HttpEntity(requetAllBodyXml, headers);
        RestTemplate restTemplate = new RestTemplate();
        InvoiceOpenRes responseBody = restTemplate.postForObject(url, entity, InvoiceOpenRes.class);
        return responseBody;
    }


    public static InvoiceMailRes mailSend(InvoiceMailReq invoiceMailReq) throws JAXBException {
        BodyModel body = new BodyModel();
        GlobalInfo globalInfo = new GlobalInfo(version, dzyx);
        body.setGlobalInfo(globalInfo);
        ContentData data = new ContentData();
        String requetBodyXml = XmlUtils.objectToXml(invoiceMailReq, InvoiceMailReq.class);
        requetBodyXml = Base64Utils.getBase64(requetBodyXml);
        data.setContent(requetBodyXml);
        body.setData(data);

        String requetAllBodyXml = XmlUtils.objectToXml(body, BodyModel.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity entity = new HttpEntity(requetAllBodyXml, headers);
        RestTemplate restTemplate = new RestTemplate();
        InvoiceMailRes responseBody = restTemplate.postForObject(url, entity, InvoiceMailRes.class);
        return responseBody;
    }

}
