package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "STATE")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "longtext")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "STATE_TYPE_ID", nullable = false)
    private StateType stateType;
}
