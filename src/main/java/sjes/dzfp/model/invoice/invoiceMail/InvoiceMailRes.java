package sjes.dzfp.model.invoice.invoiceMail;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "ROOT")
public class InvoiceMailRes {
    private String CODE;
    private String MESSAGE;
}
