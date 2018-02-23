package sjes.dzfp.model.invoice;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="Data")
public class ContentData {
    private String content;
}
