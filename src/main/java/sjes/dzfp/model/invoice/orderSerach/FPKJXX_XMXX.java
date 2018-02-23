package sjes.dzfp.model.invoice.orderSerach;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="FPKJXX_XMXX")
public class FPKJXX_XMXX {
    private String SPMC;
    private String SPDW;
    private String GGXH;
    private String SPSL;
    private String SPDJ;
    private String FPHXZ;
    private String SPBM;
    private String ZXBM;
    private String YHZCBS;
    private String ZZSTSGL;
    private String SPJE;
    private String SL;
    private String SE;
    private String SLANDDW;
}
