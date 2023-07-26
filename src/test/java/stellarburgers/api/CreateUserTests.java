package stellarburgers.api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.client.UserClient;
import stellarburgers.api.model.User;
import stellarburgers.api.util.UserGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTests {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getUser();
    }

    @After
    public void cleanUp() {
        try {
            userClient.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createNewUser() {
        ValidatableResponse createResponse = userClient.createUser(user);
        createResponse.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
        accessToken = createResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        ValidatableResponse createResponseFirst = userClient.createUser(user);
        ValidatableResponse createResponseSecond = userClient.createUser(user);
        createResponseSecond.assertThat()
                .statusCode(403)
                .and()
                .body("message", equalTo("User already exists"));
        accessToken = createResponseFirst.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем name")
    public void createUserWithoutName() {
        user.setName(null);
        ValidatableResponse createResponse = userClient.createUser(user);
        createResponse.assertThat()
                .statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с не заполненным полем email")
    public void createUserWithoutEmail() {
        user.setEmail(null);
        ValidatableResponse createResponse = userClient.createUser(user);
        createResponse.assertThat()
                .statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с не заполненным полем password")
    public void createUserWithoutPassword() {
        user.setPassword(null);
        ValidatableResponse createResponse = userClient.createUser(user);
        createResponse.assertThat()
                .statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}