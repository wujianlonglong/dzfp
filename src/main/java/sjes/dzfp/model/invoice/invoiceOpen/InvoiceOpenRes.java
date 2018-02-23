package sjes.dzfp.model.invoice.invoiceOpen;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "ROOT")
public class InvoiceOpenRes {
    private String CODE;
    private String MESSAGE;
    private String DDLSH;
}
