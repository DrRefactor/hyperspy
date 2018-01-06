package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@EqualsAndHashCode(of = "model")
@Table(name = "CAPSULE_TYPE")
public class CapsuleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODEL", nullable = false)
    private Integer model;

    @Column(name = "MODEL_DESC", unique = true, nullable = false)
    @Size(max = 45)
    private String modelDescription;

    @Column(name = "SEATS_NO", nullable = false)
    private Integer seatsNumber;
}
