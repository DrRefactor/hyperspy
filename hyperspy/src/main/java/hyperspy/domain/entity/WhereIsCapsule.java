package hyperspy.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Entity
@Setter
@EqualsAndHashCode(of = {"capsuleSideNumber"})
@Table(name = "WHERE_IS_CAPSULE")
@JsonIgnoreProperties({"capsule"})
public class WhereIsCapsule implements Serializable {

    @Id
    @Column(name = "CAPSULE_SIDE_NO", nullable = false, unique = true)
    private Integer capsuleSideNumber;

    @OneToOne(optional = false)
    @JoinColumn(name = "CAPSULE_SIDE_NO", referencedColumnName = "SIDE_NO", nullable = false, unique = true)
    private Capsule capsule;

    @ManyToOne(optional = false)
    @JoinColumn(name = "INFRASTRUCTURE_ELEMENT_ID", referencedColumnName = "ID", nullable = false)
    private InfrastructureElement infrastructureElement;

    @Column(name = "START_TIME", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

}
