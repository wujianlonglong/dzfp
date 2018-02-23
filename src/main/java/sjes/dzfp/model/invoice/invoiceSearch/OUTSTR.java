package sjes.dzfp.model.invoice.invoiceSearch;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name="OUTSTR")
public class OUTSTR {
    private String FP_CODE;
    private String FP_MESSAGE;
    private String DDLSH;
    private String XPLSH;
    private String JYM;
    private String EWM;
    private String FP_DM;
    private String FP_HM;
    private String KPRQ;
    private String KPLX;
    private String KPHJJE;
    private String GHFMC;
    private String PDF_FILE="";
    private String NSRSBH;
    private String JPG_FILE="";
    private Boolean KPBZ;
    private String GSMC="三江购物俱乐部股份有限公司";
}
