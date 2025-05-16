package kz.spring.springboot.chat.Service.Impl;

import kz.spring.springboot.chat.Entity.Users;
import kz.spring.springboot.chat.Repository.UsersRepository;
import kz.spring.springboot.chat.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public Users registerIfNotExists(String name) {
        return usersRepository.findByName(name)
                .orElseGet(() -> {
                    Users user = Users.builder()
                            .name(name)
                            .build();
                    return usersRepository.save(user);
                });
    }


    @Override
    public Users createUser(Users user) {
        return usersRepository.save(user);
    }

    @Override
    public Optional<Users> getUserById(String id) {
        return usersRepository.findById(id);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users updateUser(String id, Users updatedUser) {
        return usersRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    return usersRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(String id) {
        usersRepository.deleteById(id);
    }
    public String getUsernameById(String userId) {
        return usersRepository.findById(userId)
                .map(Users::getName)
                .orElse(null);
    }

    public String getUserIdByUsername(String username) {
        return usersRepository.findByName(username)
                .map(Users::getId)
                .orElse(null);
    }

    @Override
    public void setUserOnlineStatus(String name, boolean status) {
        usersRepository.findByName(name).ifPresent(user -> {
            user.setOnline(status);
            usersRepository.save(user);
        });
    }

    public List<Users> searchUsersByName(String name) {
        return usersRepository.findByNameContainingIgnoreCase(name);
    }
}
