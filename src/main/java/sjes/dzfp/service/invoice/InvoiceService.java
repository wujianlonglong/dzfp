package sjes.dzfp.service.invoice;


import sjes.dzfp.ResponseBody.ResponseMessage;

public interface InvoiceService {

    /**
     * 开票
     * @param searchNo 查询序列号
     * @param invoiceType 1单位 2个人
     * @param invoiceHead 发票抬头
     * @param nsrNo 税号
     * @param address 地址
     * @param mobile 手机
     * @param bankNo 银行卡号
     * @return
     */
    ResponseMessage invoiceOpen(String searchNo, Integer invoiceType, String invoiceHead, String nsrNo, String address, String mobile, String bankNo);

    /**
     * 发票查询
     *
     * @param invoiceStream 发票流水号
     * @param invoiceCode   发票代码
     * @param invoiceNo     怕票号码
     */
    ResponseMessage invoiceSearch(String invoiceStream, String invoiceCode, String invoiceNo);


    /**
     * 发票冲红
     *
     * @param invoiceStream 发票流水号
     * @param taxpayerCode  纳税人编号
     * @param orderStream   订单流水号
     */
    ResponseMessage invoiceFlushRed(String invoiceStream, String taxpayerCode, String orderStream);


    /**
     * 订单查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    ResponseMessage orderSerach(String searchNo);


    /**
     * 订单和发票查询
     *
     * @param searchNo 查询序列号
     * @return
     */
    ResponseMessage orderAndInvoiceSearch(String searchNo);


    /**
     * 发送发票邮箱
     *
     * @param mail        邮箱地址
     * @param searchNo 查询序列号
     * @return
     */
    ResponseMessage mailSend(String mail, String searchNo);


}
