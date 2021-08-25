package entities;

import entities.Alarm;
import entities.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T18:15:08")
@StaticMetamodel(Event.class)
public class Event_ { 

    public static volatile SingularAttribute<Event, Integer> idDogadjaj;
    public static volatile SingularAttribute<Event, Date> start;
    public static volatile SingularAttribute<Event, Alarm> alarmId;
    public static volatile SingularAttribute<Event, String> destinacija;
    public static volatile SingularAttribute<Event, Date> end;
    public static volatile SingularAttribute<Event, User> userId;

}