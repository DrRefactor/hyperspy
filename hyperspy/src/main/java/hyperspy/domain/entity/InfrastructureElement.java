package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name="INFRASTRUCTURE_ELEMENT")
public class InfrastructureElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "INFRASTRUCTURE_ELEMENT_TYPE_ID", nullable = false)
    private InfrastructureElementType infrastructureElementType;

    @OneToOne(optional = false)
    @JoinColumn(name = "STATE_ID", nullable = false, unique = true)
    private State state;

}
