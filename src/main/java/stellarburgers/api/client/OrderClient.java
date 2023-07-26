package stellarburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.api.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    private static final String ORDER_PATH = "api/orders";

    @Step("Создание заказа авторизованного пользователя")
    public ValidatableResponse createOrderWithAuth(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Создание заказа неавторизованного пользователя")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Получение заказа авторизованного пользователя")
    public ValidatableResponse getOrderWithAuth(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Получение заказа неавторизованного пользователя")
    public ValidatableResponse getOrderWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then()
                .log().all();
    }
}