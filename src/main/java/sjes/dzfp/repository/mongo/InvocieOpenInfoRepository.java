package sjes.dzfp.repository.mongo;


import sjes.dzfp.domain.mongo.InvocieOpenInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvocieOpenInfoRepository extends MongoRepository<InvocieOpenInfo, String> {

    InvocieOpenInfo findBySearchNo(String searchNo);
}
