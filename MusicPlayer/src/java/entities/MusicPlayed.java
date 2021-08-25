/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dinbo-PC
 */
@Entity
@Table(name = "music_played")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MusicPlayed.findAll", query = "SELECT m FROM MusicPlayed m"),
    @NamedQuery(name = "MusicPlayed.findByIdMusicPlayed", query = "SELECT m FROM MusicPlayed m WHERE m.idMusicPlayed = :idMusicPlayed"),
    @NamedQuery(name = "MusicPlayed.findBySongName", query = "SELECT m FROM MusicPlayed m WHERE m.songName = :songName")})
public class MusicPlayed implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "SongName")
    private String songName;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMusicPlayed")
    private Integer idMusicPlayed;
    @JoinColumn(name = "userId", referencedColumnName = "IdUser")
    @ManyToOne(optional = false)
    private User userId;

    public MusicPlayed() {
    }

    public MusicPlayed(Integer idMusicPlayed) {
        this.idMusicPlayed = idMusicPlayed;
    }

    public MusicPlayed(Integer idMusicPlayed, String songName) {
        this.idMusicPlayed = idMusicPlayed;
        this.songName = songName;
    }

    public Integer getIdMusicPlayed() {
        return idMusicPlayed;
    }

    public void setIdMusicPlayed(Integer idMusicPlayed) {
        this.idMusicPlayed = idMusicPlayed;
    }


    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMusicPlayed != null ? idMusicPlayed.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MusicPlayed)) {
            return false;
        }
        MusicPlayed other = (MusicPlayed) object;
        if ((this.idMusicPlayed == null && other.idMusicPlayed != null) || (this.idMusicPlayed != null && !this.idMusicPlayed.equals(other.idMusicPlayed))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.MusicPlayed[ idMusicPlayed=" + idMusicPlayed + " ]";
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
    
}
