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
import org.hamcrest.Matchers;
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
    Long userId;
    Long userNotFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){
        //usuario padrão
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);

        userRepository.persist(user);
        userId = user.getId();

        //usuario não seguidor
        var userNotFollower = new User();
        userNotFollower.setName("Fulano");
        userNotFollower.setAge(30);

        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("sample text");

        var userID = 1;

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(postRequest)).pathParam("userId", userID)
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

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var nonexistentUserId = 99;

        given()
                .pathParam("userId", nonexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var nonexistentFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", nonexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));

    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest(){

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostsTest(){

    }

}