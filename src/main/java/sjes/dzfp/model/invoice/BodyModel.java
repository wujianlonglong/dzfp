package sjes.dzfp.model.invoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="interface")
public class BodyModel {

    private GlobalInfo globalInfo;

    private ContentData Data;

    public ContentData getData(){
        return Data;
    }
    @XmlElement(name="Data")
    public void setData(ContentData contentData){
        this.Data=contentData;
    }

    public GlobalInfo getGlobalInfo(){
        return globalInfo;
    }
    @XmlElement(name="globalInfo")
    public void setGlobalInfo(GlobalInfo globalInfo){
        this.globalInfo=globalInfo;
    }

}
