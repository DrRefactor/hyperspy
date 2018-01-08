package hyperspy.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@EqualsAndHashCode(of = {"line", "sequenceNumber"})
@Table(name = "STOPS_ON_ROUTE", uniqueConstraints = @UniqueConstraint(columnNames={"LINE_ID", "SEQUENCE_NO"}))
@IdClass(StopsOnRoute.StopsOnRoutePK.class)
public class StopsOnRoute implements Serializable {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "LINE_ID", referencedColumnName = "ID", nullable = false)
    private Line line;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQUENCE_NO", nullable = false)
    private Integer sequenceNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STATION_ID", nullable = false)
    private Station station;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class StopsOnRoutePK implements Serializable{
        private Line line;
        private Integer sequenceNumber;
    }
}
