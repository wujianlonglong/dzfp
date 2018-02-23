package sjes.dzfp.model.invoice.invoiceSearch;


import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "ROOT")
public class InvoiceSearchReq {
    private String XPLSH;
    private String FPDM = "";
    private String FPHM = "";
}
