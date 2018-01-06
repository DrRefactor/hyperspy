package hyperspy.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@EqualsAndHashCode(of = {"timetable", "timetableDay"})
@Table(name = "TIMETABLE_AT_TIMETABLE_DAY", uniqueConstraints = @UniqueConstraint(columnNames={"TIMETABLE_ID", "TIMETABLE_DAY_DAY_OF_WEEK"}))
@IdClass(TimetableAtTimetableDay.class)
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TimetableAtTimetableDay implements Serializable {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = " TIMETABLE_ID", nullable = false)
    private Timetable timetable;

    @Id
    @ManyToOne( optional= false)
    @JoinColumn(name = " TIMETABLE_DAY_DAY_OF_WEEK", nullable = false)
    private TimetableDay timetableDay;

}
