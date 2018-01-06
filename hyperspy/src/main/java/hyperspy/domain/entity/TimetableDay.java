package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@EqualsAndHashCode(of = "dayOfWeek")
@Table(name = "TIMETABLE_DAY")
public class TimetableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DAY_OF_WEEK", nullable = false, unique = true)
    private Integer dayOfWeek;

}
