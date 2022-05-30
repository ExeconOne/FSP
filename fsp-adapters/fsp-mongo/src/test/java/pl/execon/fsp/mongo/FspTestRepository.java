package pl.execon.fsp.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FspTestRepository extends MongoRepository<FspTestObj, String>, MongoFsp<FspTestObj> {
}
