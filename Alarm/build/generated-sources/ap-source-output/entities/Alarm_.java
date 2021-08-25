package entities;

import entities.Event;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T18:15:08")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Integer> occurence;
    public static volatile SingularAttribute<Alarm, Integer> period;
    public static volatile ListAttribute<Alarm, Event> eventList;
    public static volatile SingularAttribute<Alarm, Date> time;
    public static volatile SingularAttribute<Alarm, String> type;
    public static volatile SingularAttribute<Alarm, Integer> idAlarm;

}