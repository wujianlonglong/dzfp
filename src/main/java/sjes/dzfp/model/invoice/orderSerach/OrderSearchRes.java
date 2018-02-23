package sjes.dzfp.model.invoice.orderSerach;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name="REQUEST_FPKJXXS")
public class OrderSearchRes {
    private REQUEST_FPKJXX REQUEST_FPKJXX;
    private List<REQUEST_FPKJXX> REQUEST_THXX;
}
