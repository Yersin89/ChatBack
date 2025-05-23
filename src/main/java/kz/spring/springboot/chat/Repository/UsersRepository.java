package kz.spring.springboot.chat.Repository;

import kz.spring.springboot.chat.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends MongoRepository<User, String> {
    Optional<User> findByName(String name);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findAllById(List<String> ids);

}

