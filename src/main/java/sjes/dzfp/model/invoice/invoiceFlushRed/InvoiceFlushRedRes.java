package sjes.dzfp.model.invoice.invoiceFlushRed;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "ROOT")
public class InvoiceFlushRedRes {
    private String CODE;

    private String MESSAGE;
}
