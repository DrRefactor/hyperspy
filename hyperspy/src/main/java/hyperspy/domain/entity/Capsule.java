package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "sideNumber")
@Table(name = "CAPSULE")
@Embeddable
public class Capsule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SIDE_NO", nullable = false, unique = true)
    private Integer sideNumber;

    @Column(name = "PRODUCTION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CAPSULE_TYPE_ID", nullable = false)
    private CapsuleType capsuleType;

    @Column(name = "SERIAL_NO", nullable = false, unique = true)
    @Size(max = 45)
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "CURRENT_LINE")
    private Line currentLine;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STATE_ID", nullable = false, unique = true)
    private State state;

}
