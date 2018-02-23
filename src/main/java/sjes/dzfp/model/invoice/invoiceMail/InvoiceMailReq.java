package sjes.dzfp.model.invoice.invoiceMail;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="DATA")
public class InvoiceMailReq {
    private String EMAIL;
    private String FPDM;
    private String FPHM;
    private String DDLSH;
}
