package com.projectdata.transaction.repository.mongo;

import com.projectdata.transaction.model.mongo.RawTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawTransactionRepository extends MongoRepository<RawTransaction, String> {
    // Basic CRUD operations are provided by MongoRepository
    // You can add custom query methods here as needed
}
