package sjes.dzfp.repository.mongo;


import sjes.dzfp.domain.mongo.InvoiceErroInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceErroInfoRepository extends MongoRepository<InvoiceErroInfo, String> {
    InvoiceErroInfo findBySearchNoAndType(String serachNo, String type);
}
