package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "CITY")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    @Size(max = 45)
    private String name;

    @Column(name = "POSTAL_CODE", nullable = false)
    @Size(max = 20)
    private String postalCode;

    @Column(name = "COUNTRY", nullable = false)
    @Size(max = 45)
    private String country;

}