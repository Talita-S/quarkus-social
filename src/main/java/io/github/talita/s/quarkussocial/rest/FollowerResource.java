package io.github.talita.s.quarkussocial.rest;

import io.github.talita.s.quarkussocial.domain.model.Follower;
import io.github.talita.s.quarkussocial.domain.repository.FollowerRepository;
import io.github.talita.s.quarkussocial.domain.repository.UserRepository;
import io.github.talita.s.quarkussocial.rest.dto.FollowerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository repository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(
            @PathParam("userId") Long userId, FollowerRequest request){

        var user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());

        boolean follows = repository.follows(follower, user);

        if(!follows){
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            repository.persist(entity);
        }

        return  Response.status(Response.Status.NO_CONTENT).build();

    }

}