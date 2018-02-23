package sjes.dzfp.model.invoice.orderSerach;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "FPKJXX_XMXXS")
public class FPKJXX_XMXXS {
    private List<FPKJXX_XMXX> FPKJXX_XMXX;
}
