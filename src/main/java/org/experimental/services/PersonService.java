package org.experimental.services;

import org.experimental.entities.Person;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonService {

    @GET
    public List<Person> findAll(){
        return Person.listAll();
    }

    @POST
    @Transactional
    public Response create(Person person){
        Person.persist(person);
        return Response.ok().status(201).build();
    }

    @PUT
    @Transactional
    public Response update(Person person) {
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
        if (id == null){
            return Response.ok("{\"message\":\"object must have an id\"}").status(400).build();
        }
        if (Person.deleteById(id)){
            return Response.status(204).build();
        }else {
            return Response.ok("{\"message\":\"object with such id does not exist\"}").status(400).build();
        }
    }
}
