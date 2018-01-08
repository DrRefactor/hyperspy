package hyperspy.repository;

import hyperspy.domain.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICityRepository extends JpaRepository<City, Integer>{
}
