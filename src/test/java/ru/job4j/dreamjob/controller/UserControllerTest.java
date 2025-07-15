package ru.job4j.dreamjob.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.interfaces.UserService;
import java.util.Optional;

class UserControllerTest {
    private UserService userService;
    private UserController userController;
    private MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    void whenRequestUserRegistrationPageThenGetPage() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenPostUserWithAllFieldsThenSameDataAndRedirectToVacanciesPage() {
        var user = new User(1, "example@test.com", "Nikolay", "qwerty123");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        var optionalUser = Optional.of(user);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(optionalUser);

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    void whenPostExistingUserWithAllFieldsThenGetErrorPage() throws Exception {
        var expectedException = new RuntimeException("Пользователь с такой почтой уже существует");
        when(userService.save(any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = userController.register(model, new User());
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("fragments/errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    void whenRequestUserLoginPageThenGetPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenLoginExistingUserThenGetRedirectionAndSameUser() {
        var user = new User(1, "example@test.com", "Nikolay", "qwerty123");
        var expectedUser = Optional.of(user);
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(expectedUser);

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, mockHttpServletRequest);
        var actualUser = mockHttpServletRequest.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    void whenLoginNonExistingUserThenGetRedirectionAndErrorMessage() {
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(new User(), model, mockHttpServletRequest);
        var actualExceptionMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualExceptionMessage).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    void whenLogoutFromAccountThenGetRedirectionToLoginPage() {
        var view = userController.logout(mockHttpServletRequest.getSession());

        assertThat(view).isEqualTo("redirect:/users/login");
        assertThat(mockHttpServletRequest.getSession(false)).isNull();
    }
}