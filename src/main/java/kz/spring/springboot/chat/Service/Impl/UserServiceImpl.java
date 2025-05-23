package kz.spring.springboot.chat.Service.Impl;

import kz.spring.springboot.chat.Entity.User;
import kz.spring.springboot.chat.Exception.UserNotFoundException;
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
    public User registerIfNotExists(String name) {
        return usersRepository.findByName(name)
                .orElseGet(() -> createAndSaveUser(name));
    }
    private User createAndSaveUser(String name) {
        return usersRepository.save(User.builder().name(name).build());
    }

    @Override
    public User createUser(User user) {
        return usersRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return usersRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public User updateUser(String id, User updatedUser) {
        return usersRepository.save(
                getUserOrThrow(id).toBuilder()
                        .name(updatedUser.getName())
                        .build()
        );
    }

    public String getUsernameById(String id) {
        return getUserOrThrow(id).getName();
    }

    private User getUserOrThrow(String id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public String getUserIdByUsername(String username) {
        return usersRepository.findByName(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    public void setUserOnlineStatus(String name, boolean status) {
        usersRepository.findByName(name).ifPresent(user -> {
            user.setOnline(status);
            usersRepository.save(user);
        });
    }


    public List<User> searchUsersByName(String name) {

        return usersRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void deleteUser(String id) {
        if (!usersRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        usersRepository.deleteById(id);
    }
}
