package com.projectdata.transaction.repository.mongo;

import com.projectdata.transaction.model.mongo.AlertMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertMessageRepository extends MongoRepository<AlertMessage, String> {
    // Find alerts by userId with pagination
    Page<AlertMessage> findByUserId(String userId, Pageable pageable);
}
