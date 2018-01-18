package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "TIMETABLE")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LINE_ID", nullable = false)
    private Line line;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "FROM_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "UNTIL", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date until;

}
