package kz.spring.springboot.chat.Repository;

import kz.spring.springboot.chat.Entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<Chat> findByName(String name);

    List<Chat> findByParticipantIdsContains(String userId);

    @Query("{ 'participantIds' : { $all: ?0 } }")
    Optional<Chat> findByParticipantIdsContainingAll(List<String> participantIds);

}