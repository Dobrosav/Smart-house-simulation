/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dinbo-PC
 */
@Entity
@Table(name = "event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByIdDogadjaj", query = "SELECT e FROM Event e WHERE e.idDogadjaj = :idDogadjaj"),
    @NamedQuery(name = "Event.findByStart", query = "SELECT e FROM Event e WHERE e.start = :start"),
    @NamedQuery(name = "Event.findByEnd", query = "SELECT e FROM Event e WHERE e.end = :end"),
    @NamedQuery(name = "Event.findByDestinacija", query = "SELECT e FROM Event e WHERE e.destinacija = :destinacija")})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idDogadjaj")
    private Integer idDogadjaj;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "destinacija")
    private String destinacija;
    @JoinColumn(name = "alarmId", referencedColumnName = "idAlarm")
    @ManyToOne
    private Alarm alarmId;
    @JoinColumn(name = "userId", referencedColumnName = "IdUser")
    @ManyToOne(optional = false)
    private User userId;

    public Event() {
    }

    public Event(Integer idDogadjaj) {
        this.idDogadjaj = idDogadjaj;
    }

    public Event(Integer idDogadjaj, Date start, Date end, String destinacija) {
        this.idDogadjaj = idDogadjaj;
        this.start = start;
        this.end = end;
        this.destinacija = destinacija;
    }

    public Integer getIdDogadjaj() {
        return idDogadjaj;
    }

    public void setIdDogadjaj(Integer idDogadjaj) {
        this.idDogadjaj = idDogadjaj;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public void setDestinacija(String destinacija) {
        this.destinacija = destinacija;
    }

    public Alarm getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Alarm alarmId) {
        this.alarmId = alarmId;
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
        hash += (idDogadjaj != null ? idDogadjaj.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.idDogadjaj == null && other.idDogadjaj != null) || (this.idDogadjaj != null && !this.idDogadjaj.equals(other.idDogadjaj))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Event[ idDogadjaj=" + idDogadjaj + " ]";
    }
    
}
