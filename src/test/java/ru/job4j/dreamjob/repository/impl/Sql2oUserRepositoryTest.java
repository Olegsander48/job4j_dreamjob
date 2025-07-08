package ru.job4j.dreamjob.repository.impl;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.User;
import java.util.List;
import java.util.Properties;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    void clearCandidates() {
        var candidates = sql2oUserRepository.findAll();
        for (var candidate : candidates) {
            sql2oUserRepository.deleteById(candidate.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "google@gmail.com", "Aleks", "1234")).get();
        var savedUser = sql2oUserRepository.findById(user.getId()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "google1@gmail.com", "Aleks1", "1234")).get();
        var user2 = sql2oUserRepository.save(new User(0, "google2@gmail.com", "Aleks2", "1234")).get();
        var user3 = sql2oUserRepository.save(new User(0, "google3@gmail.com", "Aleks3", "1234")).get();
        var result = sql2oUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(new User(0, "google@gmail.com", "Aleks", "1234"));
        var isDeleted = sql2oUserRepository.deleteById(user.get().getId());
        var savedUser = sql2oUserRepository.findById(user.get().getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(empty());
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteById(0)).isFalse();
    }

    @Test
    void whenPutExistingUserThenGetException() {
        sql2oUserRepository.save(new User(0, "google@gmail.com", "Aleks", "1234"));
        assertThatThrownBy(() -> sql2oUserRepository.save(new User(0, "google@gmail.com", "Aleks", "1234")))
                .isInstanceOf(Sql2oException.class);
    }

    @Test
    void whenPutExistingEmailThenGetException() {
        sql2oUserRepository.save(new User(0, "google@gmail.com", "Aleks", "1234"));
        assertThatThrownBy(() -> sql2oUserRepository.save(new User(0, "google@gmail.com", "Andrey", "password")))
                .isInstanceOf(Sql2oException.class);
    }
}