package io.github.talita.s.quarkussocial.rest;

import io.github.talita.s.quarkussocial.domain.model.User;
import io.github.talita.s.quarkussocial.domain.repository.UserRepository;
import io.github.talita.s.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);

        userRepository.persist(user);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("sample text");

        var userId = 1;

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(postRequest)).pathParam("userId", userId)
        .when()
                .post()
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an nonexistent user")
    public void postForAnNonexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("sample text");

        var nonexistentUserId = 99;

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(postRequest)).pathParam("userId", nonexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

}