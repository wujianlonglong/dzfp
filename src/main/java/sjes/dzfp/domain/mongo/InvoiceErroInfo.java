package sjes.dzfp.domain.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "invoiceErroInfo")
public class InvoiceErroInfo {
    @Id
    private String id;
    private String searchNo;
    private String xpls;
    private String type;
    private String erroMessage;
}
