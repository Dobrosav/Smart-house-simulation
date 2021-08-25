package entities;

import entities.Event;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-10T20:18:02")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> idUser;
    public static volatile SingularAttribute<User, String> password;
    public static volatile ListAttribute<User, Event> eventList;
    public static volatile SingularAttribute<User, Integer> privilege;
    public static volatile SingularAttribute<User, String> username;
    public static volatile SingularAttribute<User, String> home;

}