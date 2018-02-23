package sjes.dzfp.model.invoice.invoiceFlushRed;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="DATA")
public class InvoiceFlushRedReq {
    private String XPLSH;

    private String NSRSBH;

    private String DDLSH;
}
