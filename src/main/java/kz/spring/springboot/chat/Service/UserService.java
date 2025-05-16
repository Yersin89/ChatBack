package kz.spring.springboot.chat.Service;


import kz.spring.springboot.chat.Entity.Users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Users registerIfNotExists(String name);

    Users createUser(Users user);

    Optional<Users> getUserById(String id);

    List<Users> getAllUsers();

    Users updateUser(String id, Users updatedUser);

    void deleteUser(String id);

    public String getUsernameById(String userId);

    public String getUserIdByUsername(String username);

    void setUserOnlineStatus(String name, boolean status);

    public List<Users> searchUsersByName(String name);
}