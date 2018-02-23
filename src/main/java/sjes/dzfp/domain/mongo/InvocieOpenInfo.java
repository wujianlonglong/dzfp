package sjes.dzfp.domain.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "InvocieOpenInfo")
@Data
public class InvocieOpenInfo {
    @Id
    private String id;
    private String searchNo;
    private Integer invoiceType;
    private String invoiceHead;
    private String nsrNo;
    private String address;
    private String mobile;
    private String bankNo;
    private String kpDate;
}
