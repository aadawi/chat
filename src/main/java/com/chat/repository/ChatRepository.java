package com.chat.repository;

import com.chat.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, Long> {
    List<Chat> findByUsername(String lastName);
}
