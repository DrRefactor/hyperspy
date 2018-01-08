package hyperspy.repository;

import hyperspy.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStationRepository extends JpaRepository<Station, Integer>{
    Optional<Station> findById(final Integer id);
}
