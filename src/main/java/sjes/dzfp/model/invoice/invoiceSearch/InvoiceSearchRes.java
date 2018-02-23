package sjes.dzfp.model.invoice.invoiceSearch;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="ROOT")
public class InvoiceSearchRes {
    private OUTSTRS OUTSTRS;
    private String CODE;
    private String MESSAGE;
}
