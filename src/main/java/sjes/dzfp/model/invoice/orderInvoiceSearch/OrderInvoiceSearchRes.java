package sjes.dzfp.model.invoice.orderInvoiceSearch;

import lombok.Data;
import sjes.dzfp.model.invoice.invoiceSearch.OUTSTR;
import sjes.dzfp.model.invoice.orderSerach.FPKJXX_FPTXX;
import sjes.dzfp.model.invoice.orderSerach.FPKJXX_XMXXS;

@Data
public class OrderInvoiceSearchRes {
    private FPKJXX_FPTXX FPKJXX_FPTXX;
    private FPKJXX_XMXXS FPKJXX_XMXXS;
    private OUTSTR OUTSTR;
}
