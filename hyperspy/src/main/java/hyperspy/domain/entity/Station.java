package hyperspy.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "STATION")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CITY_ID", nullable = false)
    private City city;

    @Column(name = "PLATFORMS_NO", nullable = false)
    private Integer platformsNumber = 0;

    @Column(name = "COOR_X", nullable = false, precision = 6, scale = 3)
    private BigDecimal coorX;

    @Column(name = "COOR_Y", nullable = false, precision = 6, scale = 3)
    private BigDecimal coorY;

}
