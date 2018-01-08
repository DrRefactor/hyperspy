package hyperspy.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Entity
@EqualsAndHashCode(of = {"startHour", "timetable"})
@Table(name = "TIMETABLE_TIME_FREQ", uniqueConstraints = @UniqueConstraint(columnNames={"TIMETABLE_ID", "START_HOUR"}))
@IdClass(TimetableTimeFreq.TimetableTimeFrequencyPK.class)
public class TimetableTimeFreq implements Serializable {

    @Id
    @Column(name = "START_HOUR", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date startHour;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = " TIMETABLE_ID", nullable = false)
    private Timetable timetable;

    @Column(name = "FREQUENCY", nullable = true)
    private Integer frequency;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TimetableTimeFrequencyPK implements Serializable {
        private Date startHour;
        private Timetable timetable;
    }

}
