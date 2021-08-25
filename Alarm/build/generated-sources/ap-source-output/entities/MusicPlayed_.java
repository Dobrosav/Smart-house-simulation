package entities;

import entities.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-25T18:15:08")
@StaticMetamodel(MusicPlayed.class)
public class MusicPlayed_ { 

    public static volatile SingularAttribute<MusicPlayed, String> songName;
    public static volatile SingularAttribute<MusicPlayed, User> userId;
    public static volatile SingularAttribute<MusicPlayed, Integer> idMusicPlayed;

}