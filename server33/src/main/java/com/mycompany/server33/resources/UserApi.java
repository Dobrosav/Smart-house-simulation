/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server33.resources;
import entities.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
/**
 *
 * @author vd180005d
 */
@Path("user")
@Stateless
public class UserApi {
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @POST
    public Response createUser(@QueryParam("name") String username, @QueryParam("pass") String password) {
        List<User> users = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getResultList();
        if (users.isEmpty() == false) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Username already exists :(").build();
        }
        User user = new User();
        user.setPrivilege(0);
        user.setUsername(username);
        user.setPassword(password);
        em.persist(user);
        return Response.status(Response.Status.OK).entity("OK").build();
    }
}
