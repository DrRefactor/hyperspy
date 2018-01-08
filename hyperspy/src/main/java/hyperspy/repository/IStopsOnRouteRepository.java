package hyperspy.repository;

import hyperspy.domain.entity.StopsOnRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStopsOnRouteRepository extends JpaRepository<StopsOnRoute, StopsOnRoute.StopsOnRoutePK>{
}
