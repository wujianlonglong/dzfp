package sjes.dzfp.model.invoice;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="globalInfo")
public class GlobalInfo {
    private String version;

    private String interfaceCode;

    public GlobalInfo(String version, String interfaceCode) {
        this.version = version;
        this.interfaceCode = interfaceCode;
    }

    public GlobalInfo(){}
}
