package kz.spring.springboot.chat.Repository;

import kz.spring.springboot.chat.Entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends MongoRepository<Users, String> {
    boolean existsByName(String name);
    Optional<Users> findByName(String name);
    List<Users> findByNameContainingIgnoreCase(String name);
    List<Users> findAllById(List<String> ids);

}

