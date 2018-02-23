package sjes.dzfp.controller;

import sjes.dzfp.ResponseBody.ResponseMessage;
import sjes.dzfp.model.invoice.orderInvoiceSearch.OrderInvoiceSearchRes;
import sjes.dzfp.service.invoice.InvoiceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * 开票
     *
     * @param searchNo    查询序列号
     * @param invoiceHead 发票抬头
     */

    /**
     * 开票
     *
     * @param searchNo    查询序列号
     * @param invoiceType 1单位 2个人
     * @param invoiceHead 发票抬头
     * @param nsrNo       税号
     * @param address     地址
     * @param mobile      手机
     * @param bankNo      银行卡号
     * @return
     */
    @RequestMapping(value = "/invoiceOpen")
    public ResponseMessage invoiceOpen(@RequestParam(name = "searchNo", required = false) String searchNo,
                                       @RequestParam(name = "invoiceType", required = false) Integer invoiceType,
                                       @RequestParam(name = "invoiceHead", required = false) String invoiceHead,
                                       @RequestParam(name = "nsrNo", required = false) String nsrNo,
                                       @RequestParam(name = "address", required = false) String address,
                                       @RequestParam(name = "mobile", required = false) String mobile,
                                       @RequestParam(name = "bankNo", required = false) String bankNo) {
        return invoiceService.invoiceOpen(searchNo, invoiceType, invoiceHead, nsrNo, address, mobile, bankNo);
    }


    /**
     * 发票查询
     *
     * @param invoiceStream
     * @param invoiceCode
     * @param invoiceNo
     */
    @RequestMapping(value = "/invoiceSearch", method = RequestMethod.GET)
    public ResponseMessage invoiceSearch(@RequestParam(name = "invoiceStream", required = false) String invoiceStream, @RequestParam(name = "invoiceCode", required = false) String invoiceCode, @RequestParam(name = "invoiceNo", required = false) String invoiceNo) {
        return invoiceService.invoiceSearch(invoiceStream, invoiceCode, invoiceNo);
    }


    /**
     * 发票冲红
     *
     * @param invoiceStream 发票流水号
     * @param taxpayerCode  纳税人编号
     * @param orderStream   订单流水号
     */
    @RequestMapping(value = "/invoiceFlushRed")
    public ResponseMessage invoiceFlushRed(@RequestParam(name = "invoiceStream", required = false) String invoiceStream, @RequestParam(name = "taxpayerCode", required = false) String taxpayerCode, @RequestParam(name = "orderStream", required = false) String orderStream) {
        return invoiceService.invoiceFlushRed(invoiceStream, taxpayerCode, orderStream);
    }


    /**
     * 订单查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    @RequestMapping(value = "/orderSerach")
    public ResponseMessage orderSerach(@RequestParam(name = "searchNo", required = false) String searchNo) {
        if (StringUtils.isEmpty(searchNo)) {
            return ResponseMessage.failure(-1, "订单查询失败：查询号码不能为空", null);
        }
        return invoiceService.orderSerach(searchNo);
    }

    /**
     * 订单和发票查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    @RequestMapping(value = "/orderAndInvoiceSearch")
    public ResponseMessage orderAndInvoiceSearch(@RequestParam(name = "searchNo", required = false) String searchNo) {
        if (StringUtils.isEmpty(searchNo)) {
            return ResponseMessage.failure(-1, "订单查询失败：查询号码不能为空", null);
        }
        return invoiceService.orderAndInvoiceSearch(searchNo);
    }


    /**
     * 发送发票邮箱
     *
     * @param mail     邮箱地址
     * @param searchNo 查询序列号
     * @return
     */
    @RequestMapping(value = "/mailSend")
    public ResponseMessage mailSend(@RequestParam(name = "mail", required = false) String mail, @RequestParam(name = "searchNo", required = false) String searchNo) {
        return invoiceService.mailSend(mail, searchNo);
    }


    /**
     * 查询发票图片
     *
     * @param searchNo 查询序列号
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/pic")
    public String viewSignatureImage(@RequestParam(name = "searchNo", required = false) String searchNo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseMessage res = invoiceService.orderAndInvoiceSearch(searchNo);
        OrderInvoiceSearchRes invoiceSearchRes = (OrderInvoiceSearchRes) res.getData();
        String jpg = "";
        if (invoiceSearchRes == null || invoiceSearchRes.getOUTSTR() == null)
            jpg = "";
        else jpg = invoiceSearchRes.getOUTSTR().getJPG_FILE();
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decoderBytes = decoder.decodeBuffer(jpg);
        response.addHeader("Access-Control-Expose-Headers","exist");
        if (StringUtils.isEmpty(jpg))
            response.addHeader("exist", "0");
        else
            response.addHeader("exist", "1");
        ServletOutputStream out = null;
        try {
            response.setContentType("image/jpg");
            // response.addHeader("Content-Disposition", "attachment;filename=test.jpg");
            out = response.getOutputStream();
            out.write(decoderBytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 下载发票pdf
     *
     * @param searchNo
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/downloadPdf")
    public String downloadPdf(@RequestParam(name = "searchNo", required = false) String searchNo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseMessage res = invoiceService.orderAndInvoiceSearch(searchNo);
        OrderInvoiceSearchRes invoiceSearchRes = (OrderInvoiceSearchRes) res.getData();
        String pdf = "";
        if (invoiceSearchRes == null || invoiceSearchRes.getOUTSTR() == null)
            pdf = "";
        else pdf = invoiceSearchRes.getOUTSTR().getPDF_FILE();
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] decoderBytes = base64Decoder.decodeBuffer(pdf);
        OutputStream out = response.getOutputStream();
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + searchNo + ".pdf");
            out.write(decoderBytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
