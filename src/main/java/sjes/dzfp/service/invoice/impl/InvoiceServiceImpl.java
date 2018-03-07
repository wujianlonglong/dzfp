package sjes.dzfp.service.invoice.impl;


import sjes.dzfp.ResponseBody.ResponseMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sjes.dzfp.domain.mongo.InvocieOpenInfo;
import sjes.dzfp.domain.mongo.InvoiceErroInfo;
import sjes.dzfp.model.invoice.InvoiceUtils;
import sjes.dzfp.model.invoice.invoiceFlushRed.InvoiceFlushRedReq;
import sjes.dzfp.model.invoice.invoiceFlushRed.InvoiceFlushRedRes;
import sjes.dzfp.model.invoice.invoiceMail.InvoiceMailReq;
import sjes.dzfp.model.invoice.invoiceMail.InvoiceMailRes;
import sjes.dzfp.model.invoice.invoiceOpen.InvoiceOpenRes;
import sjes.dzfp.model.invoice.invoiceSearch.InvoiceSearchReq;
import sjes.dzfp.model.invoice.invoiceSearch.InvoiceSearchRes;
import sjes.dzfp.model.invoice.invoiceSearch.OUTSTR;
import sjes.dzfp.model.invoice.orderInvoiceSearch.OrderInvoiceSearchRes;
import sjes.dzfp.model.invoice.orderSerach.FPKJXX_XMXX;
import sjes.dzfp.model.invoice.orderSerach.OrderSearchRes;
import sjes.dzfp.model.invoice.orderSerach.REQUEST_FPKJXX;
import sjes.dzfp.repository.mongo.InvocieOpenInfoRepository;
import sjes.dzfp.repository.mongo.InvoiceErroInfoRepository;
import sjes.dzfp.service.invoice.InvoiceService;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("InvoiceService")
public class InvoiceServiceImpl implements InvoiceService {

    Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    @Autowired
    InvocieOpenInfoRepository invocieOpenInfoRepository;

    @Autowired
    InvoiceErroInfoRepository invoiceErroInfoRepository;

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
    @Override
    public ResponseMessage invoiceOpen(String searchNo, Integer invoiceType, String invoiceHead, String nsrNo, String address, String mobile, String bankNo) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (StringUtils.isEmpty(invoiceHead)) {
                responseMessage.setFailure(-1, "发票抬头不能为空", null);
                return responseMessage;
            }
            ResponseMessage orderAndInvoiceRes = orderAndInvoiceSearch(searchNo);
            if (orderAndInvoiceRes.getCode() == -1) {
                return orderAndInvoiceRes;
            }
            OrderInvoiceSearchRes orderInvoiceSearchRes = (OrderInvoiceSearchRes) orderAndInvoiceRes.getData();
            //判断是否为全部预付卡支付
            if (orderInvoiceSearchRes.getFPKJXX_FPTXX().getYFKZFBS().equals("1")) {
                responseMessage.setFailure(-1, "全部储值卡支付,不能开票", null);
                return responseMessage;
            }
            //之前开过票的先冲红再开票
            if (orderInvoiceSearchRes.getOUTSTR().getKPBZ() == true) {
                ResponseMessage flushRes = this.invoiceFlushRed(orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH(), orderInvoiceSearchRes.getFPKJXX_FPTXX().getNSRSBH(), null);
                if (flushRes.getCode() == -1) {
                    String errorMessage = "发票冲红失败，不能开票--订单号：" + orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH();
                    if (StringUtils.isNotBlank(orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH2())) {
                        errorMessage = "发票冲红失败，不能开票--订单号：" + orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH()
                                + ",发票号:" + orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH2();
                    }
                    responseMessage.setFailure(-1, errorMessage, null);
                    saveInvoiceErroMessage(searchNo, orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH(), "fpch", errorMessage);
                    return responseMessage;
                }
            }

            REQUEST_FPKJXX REQUEST_FPKJXX = new REQUEST_FPKJXX();
            REQUEST_FPKJXX.setFPKJXX_FPTXX(orderInvoiceSearchRes.getFPKJXX_FPTXX());
            REQUEST_FPKJXX.setFPKJXX_XMXXS(orderInvoiceSearchRes.getFPKJXX_XMXXS());
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setGHF_MC(invoiceHead);
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setGHF_NSRSBH(nsrNo);
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setGHF_DZ(address);
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setGHF_GDDH(mobile);
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setGHF_YHZH(bankNo);
            REQUEST_FPKJXX.getFPKJXX_FPTXX().setKPY("自助开票");
//            InvoiceOpenRes res = null;
            InvoiceOpenRes res = InvoiceUtils.invoiceOpen(REQUEST_FPKJXX);
            if (res.getCODE().equals("9999")) {
                String errorMessage = res.getMESSAGE();
                if (StringUtils.isNotBlank(REQUEST_FPKJXX.getFPKJXX_FPTXX().getXPLSH2())) {
                    String[] split = res.getMESSAGE().split("--");
                    errorMessage = split[0] + "--订单号:" + REQUEST_FPKJXX.getFPKJXX_FPTXX().getXPLSH2() + res.getMESSAGE().substring(split[0].length());
                }
                responseMessage.setFailure(-1, errorMessage, null);
                saveInvoiceErroMessage(searchNo, orderInvoiceSearchRes.getFPKJXX_FPTXX().getXPLSH(), "fpkp", errorMessage);
                return responseMessage;
            }
            responseMessage.setData(res);
            saveInvoiceOpen(searchNo, invoiceType, invoiceHead, nsrNo, address, mobile, bankNo);
        } catch (Exception ex) {
            String errorMessage = "开票(查询序列号：" + searchNo + ")失败：" + ex.toString();
            log.error(errorMessage);
            responseMessage.setFailure(-1, "开票(查询序列号：" + searchNo + ")失败", null);
            saveInvoiceErroMessage(searchNo, "", "fpkp", errorMessage);

        }
        return responseMessage;
    }


    /**
     * 发票查询
     *
     * @param invoiceStream 发票流水号
     * @param invoiceCode   发票代码
     * @param invoiceNo     怕票号码
     */
    @Override
    public ResponseMessage invoiceSearch(String invoiceStream, String invoiceCode, String invoiceNo) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            InvoiceSearchReq invoiceSearch = new InvoiceSearchReq();
            invoiceSearch.setXPLSH(invoiceStream);
            invoiceSearch.setFPDM(invoiceCode);
            invoiceSearch.setFPHM(invoiceNo);
            InvoiceSearchRes res = InvoiceUtils.invoiceSerach(invoiceSearch);
            responseMessage.setData(res);
        } catch (Exception ex) {
            log.error("发票查询(流水：" + invoiceStream + ",代码：" + invoiceCode + ",号码：" + invoiceNo + ")失败：" + ex.toString());
            responseMessage.setFailure(-1, "发票查询异常", null);
        }
        return responseMessage;
    }


    /**
     * 发票冲红
     *
     * @param invoiceStream 发票流水号
     * @param taxpayerCode  纳税人编号
     * @param orderStream   订单流水号
     */
    @Override
    public ResponseMessage invoiceFlushRed(String invoiceStream, String taxpayerCode, String orderStream) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            InvoiceFlushRedReq invoiceFlushRedReq = new InvoiceFlushRedReq();
            invoiceFlushRedReq.setXPLSH(invoiceStream);
            invoiceFlushRedReq.setNSRSBH(taxpayerCode);
            invoiceFlushRedReq.setDDLSH(orderStream);
            InvoiceFlushRedRes res = InvoiceUtils.invoiceFlushRed(invoiceFlushRedReq);
            responseMessage.setData(res);
            return responseMessage;
        } catch (Exception ex) {
            String erroMessage = "发票冲红(发票流水号:" + invoiceStream + ",纳税人编号：" + taxpayerCode + ",订单流水号：" + orderStream + ")失败：" + ex.toString();
            log.error(erroMessage);
            responseMessage.setFailure(-1, "发票冲红异常", null);
        }
        return responseMessage;
    }


    /**
     * 订单查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    @Override
    public ResponseMessage orderSerach(String searchNo) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (StringUtils.isEmpty(searchNo)) {
                responseMessage.setFailure(-1, "订单查询失败：查询号不能为空", null);
                return responseMessage;
            }
            OrderSearchRes res = new OrderSearchRes();
            if (searchNo.startsWith("1")) {
                if (searchNo.length() != 22) {
                    responseMessage.setFailure(-1, "订单查询失败：查询号以1开头的长度必须为22位", null);
                    return responseMessage;
                }
                String shopNo = searchNo.substring(1, 4);
                String orderDate = searchNo.substring(4, 10);
                orderDate = "20" + orderDate.substring(0, 2) + "-" + orderDate.substring(2, 4) + "-" + orderDate.substring(4, 6);
                String posNo = searchNo.substring(10, 14);
                String streamNo = searchNo.substring(14, 20);
                res = InvoiceUtils.otherOrderSerachDetail(shopNo, posNo, streamNo, orderDate);
            } else if (searchNo.startsWith("2")) {
//                if (searchNo.length() != 27) {
//                    responseMessage.setFailure(-1, "订单查询失败：查询号以2开头的长度必须为27位", null);
//                    return responseMessage;
//                }
                String orderNo = searchNo.substring(7, searchNo.length() - 2);
                String orderDate = searchNo.substring(1, 7);
                orderDate = "20" + orderDate.substring(0, 2) + "-" + orderDate.substring(2, 4) + "-" + orderDate.substring(4, 6);
                res = InvoiceUtils.txdOrderSerachDetail(orderNo, orderDate);
            } else {
                responseMessage.setFailure(-1, "订单查询失败：查询号必须以1或2开头", null);
            }

            if (res.getREQUEST_FPKJXX() == null) {
                String erroMessage = "订单查询失败：未查询到发票信息";
                responseMessage.setFailure(-1, erroMessage, null);
                saveInvoiceErroMessage(searchNo, "", "ddcx", erroMessage);
            } else {
                responseMessage.setData(res);
            }

        } catch (Exception ex) {
            String erroMessage = "订单查询(查询序列号：" + searchNo + ")失败：" + ex.toString();
            log.error(erroMessage);
            responseMessage.setFailure(-1, "订单查询异常", null);
            saveInvoiceErroMessage(searchNo, "", "ddcx", erroMessage);
        }

        return responseMessage;
    }


    /**
     * 订单和发票查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    public ResponseMessage orderAndInvoiceSearch(String searchNo) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            OrderInvoiceSearchRes orderInvoiceSearchRes = new OrderInvoiceSearchRes();
            ResponseMessage<OrderSearchRes> orderDetailRes = this.orderSerach(searchNo);
            if (orderDetailRes.getCode() == -1) {
                return orderDetailRes;
            }
            orderInvoiceSearchRes.setFPKJXX_FPTXX(orderDetailRes.getData().getREQUEST_FPKJXX().getFPKJXX_FPTXX());
            orderInvoiceSearchRes.setFPKJXX_XMXXS(orderDetailRes.getData().getREQUEST_FPKJXX().getFPKJXX_XMXXS());
            if (orderInvoiceSearchRes.getFPKJXX_XMXXS() != null && orderInvoiceSearchRes.getFPKJXX_XMXXS().getFPKJXX_XMXX() != null) {
                for (FPKJXX_XMXX fpkjxx_xmxx : orderInvoiceSearchRes.getFPKJXX_XMXXS().getFPKJXX_XMXX()) {
                    fpkjxx_xmxx.setSLANDDW(subZeroAndDot(fpkjxx_xmxx.getSPSL()) + "/" + fpkjxx_xmxx.getSPDW());
                }
            }

            REQUEST_FPKJXX orderDetail = orderDetailRes.getData().getREQUEST_FPKJXX();
            String xplsh = orderDetail.getFPKJXX_FPTXX().getXPLSH();
            ResponseMessage<InvoiceSearchRes> invoiceDetailRes = this.invoiceSearch(xplsh, "", "");
            if (invoiceDetailRes.getCode() == -1 || invoiceDetailRes.getData().getCODE().equals("9999")||invoiceDetailRes.getData().getOUTSTRS().getOUTSTR().getFP_CODE().equals("9999")) {
                //设置开票标志为未开票
                OUTSTR outstr = new OUTSTR();
                outstr.setKPBZ(false);
                orderInvoiceSearchRes.setOUTSTR(outstr);
            } else {
                //设置开票标志为已开票
                OUTSTR outstr = invoiceDetailRes.getData().getOUTSTRS().getOUTSTR();
                outstr.setKPBZ(true);
                orderInvoiceSearchRes.setOUTSTR(outstr);
            }
            responseMessage.setData(orderInvoiceSearchRes);

        } catch (Exception ex) {
            log.error("订单和发票查询(查询序列号：" + searchNo + ")失败：" + ex.toString());
            responseMessage.setFailure(-1, "订单和发票查询异常", null);
        }
        return responseMessage;

    }


    /**
     * 发送发票邮箱
     *
     * @param mail     邮箱地址
     * @param searchNo 查询序列号
     * @return
     */
    @Override
    public ResponseMessage mailSend(String mail, String searchNo) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (StringUtils.isEmpty(mail) || !this.checkEmaile(mail)) {
                responseMessage.setFailure(-1, "邮箱不能为空或格式不对", null);
                return responseMessage;
            }
            ResponseMessage orderAndInvoiceSearchRes = this.orderAndInvoiceSearch(searchNo);
            if (orderAndInvoiceSearchRes.getCode() == -1) {
                return orderAndInvoiceSearchRes;
            }
            OrderInvoiceSearchRes orderInvoiceSearchRes = (OrderInvoiceSearchRes) orderAndInvoiceSearchRes.getData();
            if (orderInvoiceSearchRes.getOUTSTR() == null || orderInvoiceSearchRes.getOUTSTR().getKPBZ() == false) {
                responseMessage.setFailure(-1, "未开票", null);
                return responseMessage;
            }
            InvoiceMailReq invoiceMailReq = new InvoiceMailReq();
            invoiceMailReq.setEMAIL(mail);
            invoiceMailReq.setFPDM(orderInvoiceSearchRes.getOUTSTR().getFP_DM());
            invoiceMailReq.setFPHM(orderInvoiceSearchRes.getOUTSTR().getFP_HM());
            invoiceMailReq.setDDLSH(orderInvoiceSearchRes.getOUTSTR().getDDLSH());
            InvoiceMailRes res = InvoiceUtils.mailSend(invoiceMailReq);
        } catch (Exception ex) {
            log.error("发送发票邮箱(邮箱地址：" + mail + ",查询号：" + searchNo + ")失败：" + ex.toString());
            responseMessage.setFailure(-1, "发送发票邮箱失败", null);
        }
        return responseMessage;
    }


    /**
     * 保存开票信息
     *
     * @param searchNo    查询序列号
     * @param invoiceType 1单位 2个人
     * @param invoiceHead 发票抬头
     * @param nsrNo       税号
     * @param address     地址
     * @param mobile      手机
     * @param bankNo      银行卡号
     */
    @Async
    public void saveInvoiceOpen(String searchNo, Integer invoiceType, String invoiceHead, String nsrNo, String address, String mobile, String bankNo) {
        try {
            InvocieOpenInfo invocieOpenInfo = invocieOpenInfoRepository.findBySearchNo(searchNo);
            if (invocieOpenInfo == null) {
                invocieOpenInfo = new InvocieOpenInfo();
            }
            String now = LocalDateTime.now().toString();
            invocieOpenInfo.setSearchNo(searchNo);
            invocieOpenInfo.setInvoiceType(invoiceType);
            invocieOpenInfo.setInvoiceHead(invoiceHead);
            invocieOpenInfo.setNsrNo(nsrNo);
            invocieOpenInfo.setAddress(address);
            invocieOpenInfo.setMobile(mobile);
            invocieOpenInfo.setBankNo(bankNo);
            invocieOpenInfo.setKpDate(now);
            invocieOpenInfoRepository.save(invocieOpenInfo);
        } catch (Exception ex) {
            log.error("开票(查询号：" + searchNo + ",类型：" + invoiceType + ",抬头：" + invoiceHead + ")保存至mongo失败：" + ex.toString());
        }
    }

    /**
     * 正则表达式校验邮箱
     *
     * @param emaile 待匹配的邮箱
     * @return 匹配成功返回true 否则返回false;
     */
    private static boolean checkEmaile(String emaile) {
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(emaile);
        //进行正则匹配
        return m.matches();
    }


    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }


    /**
     * 保存错误信息
     *
     * @param searchNo
     * @param xpls
     * @param type
     * @param erroMessage
     */
    @Async
    public void saveInvoiceErroMessage(String searchNo, String xpls, String type, String erroMessage) {
        InvoiceErroInfo invoiceErroInfo = invoiceErroInfoRepository.findBySearchNoAndType(searchNo, type);
        if (invoiceErroInfo == null)
            invoiceErroInfo = new InvoiceErroInfo();
        invoiceErroInfo.setSearchNo(searchNo);
        invoiceErroInfo.setXpls(xpls);
        invoiceErroInfo.setType(type);
        invoiceErroInfo.setErroMessage(erroMessage);
        invoiceErroInfoRepository.save(invoiceErroInfo);
    }


}
