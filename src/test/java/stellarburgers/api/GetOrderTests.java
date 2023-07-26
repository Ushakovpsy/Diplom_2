package stellarburgers.api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.client.OrderClient;
import stellarburgers.api.client.UserClient;
import stellarburgers.api.model.Order;
import stellarburgers.api.model.User;
import stellarburgers.api.model.UserCredentials;
import stellarburgers.api.util.UserGenerator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderTests {

    private UserClient userClient;
    private OrderClient orderClient;
    private User user;
    private Order order;
    List<String> ingredients = Arrays.asList(
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa7a");
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getUser();
        userClient.createUser(user);
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        try {
            userClient.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrderWithAuth() {
        UserCredentials userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        loginResponse.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderClient.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());

        ValidatableResponse getOrderResponseWithAuth = orderClient.getOrderWithAuth(accessToken);
        getOrderResponseWithAuth.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказа неавторизованного пользователя")
    public void getOrderWithoutAuth() {
        order = new Order(ingredients);
        ValidatableResponse orderResponseWithoutAuth = orderClient.createOrderWithoutAuth(order);
        orderResponseWithoutAuth.assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());

        ValidatableResponse getOrderResponseWithoutAuth = orderClient.getOrderWithoutAuth();
        getOrderResponseWithoutAuth.assertThat()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}