package entities;

import entities.Event;
import entities.MusicPlayed;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-12T17:27:01")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> idUser;
    public static volatile SingularAttribute<User, String> password;
    public static volatile ListAttribute<User, Event> eventList;
    public static volatile SingularAttribute<User, Integer> privilege;
    public static volatile ListAttribute<User, MusicPlayed> musicPlayedList;
    public static volatile SingularAttribute<User, String> home;
    public static volatile SingularAttribute<User, String> username;

}