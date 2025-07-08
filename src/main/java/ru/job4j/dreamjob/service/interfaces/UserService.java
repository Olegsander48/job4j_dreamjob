package ru.job4j.dreamjob.service.interfaces;

import ru.job4j.dreamjob.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> save(User user);

    Optional<User> findByEmailAndPassword(String email, String password);
}
