package sjes.dzfp.model.invoice.orderSerach;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "REQUEST_FPKJXX")
public class REQUEST_FPKJXX {
    private FPKJXX_FPTXX FPKJXX_FPTXX;
    private FPKJXX_XMXXS FPKJXX_XMXXS;
}
