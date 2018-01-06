package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "STATE_TYPE")
public class StateType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "TYPE", unique = true, nullable = false)
    @Size(max = 45)
    private String type;

}
