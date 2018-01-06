package hyperspy.repository;

import hyperspy.domain.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITimetableDayRepository extends JpaRepository<City, Integer>{
}
