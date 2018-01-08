package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name="INFRASTRUCTURE_ELEMENT_TYPE")
public class InfrastructureElementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "TYPE", unique = true, nullable = false)
    @Size(max = 45)
    private String type;

}