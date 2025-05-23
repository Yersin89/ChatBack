package kz.spring.springboot.chat.Service;


import kz.spring.springboot.chat.Entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    User registerIfNotExists(String name);

    User createUser(User user);

    Optional<User> getUserById(String id);

    List<User> getAllUsers();

    User updateUser(String id, User updatedUser);

    void deleteUser(String id);

    String getUsernameById(String userId);

    String getUserIdByUsername(String username);

    void setUserOnlineStatus(String name, boolean status);

    List<User> searchUsersByName(String name);
}