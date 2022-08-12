package org.experimental.services;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import org.experimental.entities.Person;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;


@Path("/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonService {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    public Response findAll(){
        authAdmin();
        return Response.ok(Person.listAll()).build() ;
    }

    @POST
    @Transactional
    public Response create(Person person){
        authUser();
        Person.persist(person);
        return Response.ok().status(201).build();
    }

    @PUT
    @Transactional
    public Response update(Person person) {
        authAdmin();
        if (person.getId() == null){
            return Response.ok("{\"message\":\"object must have an id\"}").status(400).build();
        }
        if(Person.findByIdOptional(person.getId()).isEmpty()){
            return Response.ok("{\"message\":\"object with such id does not exist\"}").status(400).build();
        }else {
            Person.getEntityManager().merge(person);
            return Response.ok().build();
        }
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        authAdmin();
        if (id == null){
            return Response.ok("{\"message\":\"object must have an id\"}").status(400).build();
        }
        if (Person.deleteById(id)){
            return Response.status(204).build();
        }else {
            return Response.ok("{\"message\":\"object with such id does not exist\"}").status(400).build();
        }
    }
    private void authAdmin(){
        if (!securityIdentity.getRoles().contains("admin")){
            throw new UnauthorizedException();
        }
    }

    private void authUser(){
        if (!securityIdentity.getRoles().contains("user")){
            throw new UnauthorizedException();
        }
    }
}
