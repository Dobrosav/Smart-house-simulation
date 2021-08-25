package entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-10T20:18:02")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Integer> occurence;
    public static volatile SingularAttribute<Alarm, Integer> period;
    public static volatile SingularAttribute<Alarm, Date> time;
    public static volatile SingularAttribute<Alarm, String> type;
    public static volatile SingularAttribute<Alarm, Integer> idAlarm;

}