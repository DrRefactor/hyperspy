package hyperspy.repository;

import hyperspy.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILineRepository extends JpaRepository<Line, Integer>{
}
