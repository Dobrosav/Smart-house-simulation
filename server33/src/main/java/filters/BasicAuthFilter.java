/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import entities.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author vd180005d
 */
@Provider
public class BasicAuthFilter implements ContainerRequestFilter{
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        List<String> headerValues = crc.getHeaders().get("Authorization");
        
        if(headerValues != null && headerValues.size() > 0){
            String authHeaderValue = headerValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);

            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            if ( stringTokenizer.countTokens() != 2) {
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("No credentials").build();
                crc.abortWith(response);
                return;
            }
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            
            List<User> users = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getResultList();
            if(users.size() != 1){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Username is incorrect :(").build();
                crc.abortWith(response);
                return;
            }
            
            if (users.get(0).getPassword().equals(password) == false) {
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Wrong password").build();
                crc.abortWith(response);
                return;
            }
            
            UriInfo uriInfo = crc.getUriInfo();
            List<PathSegment> pathSegments = uriInfo.getPathSegments();
            String pathSegment1 = null;
            if(pathSegments.size() > 1) {
                pathSegment1 = pathSegments.get(1).getPath();
                if (pathSegment1.equals("user") && users.get(0).getPrivilege() == 0) {
                    Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Only admin").build();
                    crc.abortWith(response);
                    return;
                }
            }
        }
        else {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity("No credentials sent").build();
            crc.abortWith(response);
            return;
        }
    }
}
