package io.github.talita.s.quarkussocial.rest;

import io.github.talita.s.quarkussocial.domain.model.User;
import io.github.talita.s.quarkussocial.domain.repository.FollowerRepository;
import io.github.talita.s.quarkussocial.domain.repository.UserRepository;
import io.github.talita.s.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;

    @BeforeEach
    @Transactional
    void setUp(){
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);

        userRepository.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User Id")
    public void sameUserAsFollowerTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 when User Id doesn't exist")
    public void userNotFoundTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", nonexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

}