package com.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Black-box tests for the Book API. Boots the application with MongoDB Dev
 * Services (requires a container runtime such as Docker/Podman).
 */
@QuarkusTest
class BookResourceTest {

    @Test
    void findAll_returnsPagedSeededBooks() {
        given()
            .queryParam("page", 0)
            .queryParam("size", 5)
            .when().get("/books")
            .then()
                .statusCode(200)
                .body("content.size()", org.hamcrest.Matchers.is(5))
                .body("page", org.hamcrest.Matchers.is(0))
                .body("size", org.hamcrest.Matchers.is(5))
                .body("totalElements", greaterThanOrEqualTo(5))
                .body("first", org.hamcrest.Matchers.is(true));
    }

    @Test
    void findAll_withInvalidSize_returns400() {
        given()
            .queryParam("size", 0)
            .when().get("/books")
            .then()
                .statusCode(400)
                .body("status", org.hamcrest.Matchers.is(400));
    }

    @Test
    void create_withValidPayload_returns201AndLocation() {
        given()
            .contentType("application/json")
            .body("""
                  {
                    "title": "Test-Driven Development by Example",
                    "pages": 240,
                    "year": 2002,
                    "authors": ["Kent Beck"]
                  }
                  """)
            .when().post("/books")
            .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("title", org.hamcrest.Matchers.is("Test-Driven Development by Example"));
    }

    @Test
    void create_withBlankTitle_returns400WithDetails() {
        given()
            .contentType("application/json")
            .body("""
                  {
                    "title": "",
                    "pages": -5,
                    "year": 2002,
                    "authors": []
                  }
                  """)
            .when().post("/books")
            .then()
                .statusCode(400)
                .body("status", org.hamcrest.Matchers.is(400))
                .body("details", org.hamcrest.Matchers.notNullValue())
                .body("details.size()", greaterThanOrEqualTo(1));
    }

    @Test
    void findById_unknownId_returns404WithApiError() {
        given()
            .when().get("/books/does-not-exist")
            .then()
                .statusCode(404)
                .body("status", org.hamcrest.Matchers.is(404))
                .body("error", org.hamcrest.Matchers.is("Not Found"));
    }
}
