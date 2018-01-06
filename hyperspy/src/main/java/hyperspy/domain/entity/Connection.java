package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "CONNECTION")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "START_STATION", nullable = false)
    private Station startStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "END_STATION", nullable = false)
    private Station endStation;

    @Column(name = "DISTANCE", nullable = false)
    private Integer distance;

    @Column(name = "MAX_SPEED", nullable = false)
    private Integer maxSpeed;

}
