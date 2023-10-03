package io.github.talita.s.quarkussocial.rest;

import io.github.talita.s.quarkussocial.domain.model.Follower;
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
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //usuario
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);

        userRepository.persist(user);
        userId = user.getId();

        //seguidor
        var follower = new User();
        follower.setName("Cicrano");
        follower.setAge(30);

        userRepository.persist(follower);
        followerId = follower.getId();

        //novo seguidor
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User Id")
    public void sameUserAsFollowerTest() {
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
    @DisplayName("should return 404 on follow a user when User Id doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {
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

    @Test
    @DisplayName("should follow a user")
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON).body(JsonbBuilder.create().toJson(body))
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and User Id doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest() {
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                        .when()
                        .get()
                        .then()
                        .extract().response();
        var followersCount = response.jsonPath().get("followersCount");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
    }

}